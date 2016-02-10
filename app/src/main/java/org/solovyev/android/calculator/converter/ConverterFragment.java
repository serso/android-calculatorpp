package org.solovyev.android.calculator.converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.measure.unit.Dimension;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

public class ConverterFragment extends BaseDialogFragment
        implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        MenuItem.OnMenuItemClickListener {

    private static final Map<MyDimension, List<Unit<?>>> units = new HashMap<>();

    private enum MyDimension {
        TIME(Dimension.TIME, "Time"),
        AMOUNT_OF_SUBSTANCE(Dimension.AMOUNT_OF_SUBSTANCE, "Amount of substance"),
        ELECTRIC_CURRENT(Dimension.ELECTRIC_CURRENT, "Electric current"),
        LENGTH(Dimension.LENGTH, "Length"),
        MASS(Dimension.MASS, "Mass"),
        TEMPERATURE(Dimension.TEMPERATURE, "Temperature");

        @NonNull
        public final Dimension dimension;
        @NonNull
        public final String name;

        MyDimension(@NonNull Dimension dimension, @NonNull String name) {
            this.dimension = dimension;
            this.name = name;
        }

        @Nullable
        public static MyDimension getByDimension(@NonNull Unit<?> unit) {
            for (MyDimension myDimension : values()) {
                if (myDimension.dimension.equals(unit.getDimension())) {
                    return myDimension;
                }
            }
            return null;
        }

        @Nullable
        public static MyDimension getByGroup(int group) {
            final int ordinal = group - Menu.FIRST;
            final MyDimension[] values = values();
            if (ordinal >= 0 && ordinal < values.length) {
                return values[ordinal];
            }
            return null;
        }

        public int group() {
            return Menu.FIRST + ordinal();
        }
    }

    @Bind(R.id.converter_spinner_from)
    Button spinnerFrom;
    @Bind(R.id.converter_label_from)
    TextInputLayout labelFrom;
    @Bind(R.id.converter_edittext_from)
    EditText editTextFrom;
    @Bind(R.id.converter_spinner_to)
    Spinner spinnerTo;
    @Bind(R.id.converter_label_to)
    TextInputLayout labelTo;
    @Bind(R.id.converter_edittext_to)
    EditText editTextTo;
    private ArrayAdapter<Unit<?>> adapterTo;

    private static void addUnit(@NonNull Unit<?> unit) {
        final MyDimension dimension = MyDimension.getByDimension(unit);
        if (dimension == null) {
            return;
        }

        List<Unit<?>> unitsInDimension = units.get(dimension);
        if (unitsInDimension == null) {
            unitsInDimension = new ArrayList<>();
            units.put(dimension, unitsInDimension);
        }
        unitsInDimension.add(unit);
    }

    public static void show(@Nonnull FragmentActivity activity) {
        App.showDialog(new ConverterFragment(), "converter",
                activity.getSupportFragmentManager());
    }

    @Override
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {

    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    protected View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.cpp_unit_converter, null);
        ButterKnife.bind(this, view);

        adapterTo = makeAdapter(context);
        spinnerTo.setAdapter(adapterTo);

        spinnerFrom.setOnClickListener(this);

        return view;
    }

    @Nonnull
    private static ArrayAdapter<Unit<?>> makeAdapter(@NonNull Context context) {
        final ArrayAdapter<Unit<?>> adapter =
                new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        adapterTo.clear();
        /*final Unit<?> unitFrom = adapterFrom.getItem(position);
        final List<Unit<?>> units = ConverterFragment.units.get(unitFrom.getDimension());
        for (Unit<?> unitTo : units) {
            if (!unitTo.equals(unitFrom)) {
                adapterTo.add(unitTo);
            }
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.converter_spinner_from:
                showDimensions();
                break;
        }
    }

    private void showDimensions() {
        spinnerFrom.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                    ContextMenu.ContextMenuInfo menuInfo) {
                Check.isTrue(v.getId() == R.id.converter_spinner_from);
                menu.clear();
                // can't use sub-menus as AlertDialog doesn't support them
                for (MyDimension dimension : units.keySet()) {
                    menu.add(Menu.NONE, dimension.group(), Menu.NONE, dimension.name)
                            .setOnMenuItemClickListener(ConverterFragment.this);
                }
                unregisterForContextMenu(spinnerFrom);
            }
        });
        spinnerFrom.showContextMenu();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getGroupId() == Menu.NONE) {
            final MyDimension dimension = MyDimension.getByGroup(item.getItemId());
            if (dimension == null) {
                return false;
            }
            spinnerFrom.post(new Runnable() {
                @Override
                public void run() {
                    showUnits(dimension);
                }
            });
            return true;
        }
        final MyDimension dimension = MyDimension.getByGroup(item.getGroupId());
        final List<Unit<?>> unitsInDimension = units.get(dimension);
        final Unit<?> unit = unitsInDimension.get(item.getItemId());
        spinnerFrom.setText(unit.toString());
        adapterTo.clear();
        for (Unit<?> unitInDimension : unitsInDimension) {
            if (!unitInDimension.equals(unit)) {
                adapterTo.add(unitInDimension);
            }
        }
        return true;
    }

    private void showUnits(@NonNull final MyDimension dimension) {
        spinnerFrom.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                    ContextMenu.ContextMenuInfo menuInfo) {
                Check.isTrue(v.getId() == R.id.converter_spinner_from);
                menu.clear();
                final int group = dimension.group();
                final List<Unit<?>> get = units.get(dimension);
                for (int i = 0; i < get.size(); i++) {
                    final Unit<?> unit = get.get(i);
                    menu.add(group, i, Menu.NONE, unit.toString())
                            .setOnMenuItemClickListener(ConverterFragment.this);
                }
                unregisterForContextMenu(spinnerFrom);
            }
        });
        spinnerFrom.showContextMenu();
    }

    static {
        for (Unit<?> unit : SI.getInstance().getUnits()) {
            addUnit(unit);
        }
        for (Unit<?> unit : NonSI.getInstance().getUnits()) {
            addUnit(unit);
        }
    }
}
