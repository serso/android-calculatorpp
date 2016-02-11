package org.solovyev.android.calculator.converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.measure.unit.Dimension;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.util.*;

public class ConverterFragment extends BaseDialogFragment
        implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener, TextView.OnEditorActionListener, View.OnClickListener, TextWatcher {

    @NonNull
    private static final Set<String> excludedUnits = new HashSet<>(Arrays.asList("year_sidereal", "year_calendar", "day_sidereal", "foot_survey_us"));
    @NonNull
    private static final Map<MyDimension, List<Unit<?>>> units = new HashMap<>();
    private static final String STATE_SELECTION_FROM = "selection.from";
    private static final String STATE_SELECTION_TO = "selection.to";
    private static final String EXTRA_VALUE = "value";

    static {
        for (Unit<?> unit : SI.getInstance().getUnits()) {
            addUnit(unit);
        }
        for (Unit<?> unit : NonSI.getInstance().getUnits()) {
            addUnit(unit);
        }
    }

    @Bind(R.id.converter_dimensions_spinner)
    Spinner dimensionsSpinner;
    @Bind(R.id.converter_spinner_from)
    Spinner spinnerFrom;
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
    @Bind(R.id.converter_swap_button)
    ImageButton swapButton;
    private ArrayAdapter<MyDimensionUi> dimensionsAdapter;
    private ArrayAdapter<Unit<?>> adapterFrom;
    private ArrayAdapter<Unit<?>> adapterTo;

    private int pendingFromSelection = View.NO_ID;
    private int pendingToSelection = View.NO_ID;

    private static void addUnit(@NonNull Unit<?> unit) {
        if (excludedUnits.contains(unit.toString())) {
            return;
        }

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
        show(activity, 1d);
    }

    public static void show(@Nonnull FragmentActivity activity, double value) {
        final ConverterFragment fragment = new ConverterFragment();
        final Bundle args = new Bundle(1);
        args.putDouble(EXTRA_VALUE, value);
        fragment.setArguments(args);
        App.showDialog(fragment, "converter",
                activity.getSupportFragmentManager());
    }

    @Nonnull
    private static <T> ArrayAdapter<T> makeAdapter(@NonNull Context context) {
        final ArrayAdapter<T> adapter =
                new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
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

        dimensionsAdapter = makeAdapter(context);
        for (MyDimension dimension : MyDimension.values()) {
            dimensionsAdapter.add(new MyDimensionUi(dimension));
        }
        adapterFrom = makeAdapter(context);
        adapterTo = makeAdapter(context);

        dimensionsSpinner.setAdapter(dimensionsAdapter);
        spinnerFrom.setAdapter(adapterFrom);
        spinnerTo.setAdapter(adapterTo);

        dimensionsSpinner.setOnItemSelectedListener(this);
        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo.setOnItemSelectedListener(this);

        editTextFrom.setOnFocusChangeListener(this);
        editTextFrom.setOnEditorActionListener(this);
        editTextFrom.addTextChangedListener(this);

        swapButton.setOnClickListener(this);
        swapButton.setImageResource(App.getTheme().light ? R.drawable.ic_swap_vert_black_24dp : R.drawable.ic_swap_vert_white_24dp);

        if (savedInstanceState == null) {
            editTextFrom.setText(String.valueOf(getArguments().getDouble(EXTRA_VALUE, 1f)));
            dimensionsSpinner.setSelection(0);
        } else {
            pendingFromSelection = savedInstanceState.getInt(STATE_SELECTION_FROM, View.NO_ID);
            pendingToSelection = savedInstanceState.getInt(STATE_SELECTION_TO, View.NO_ID);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTION_FROM, spinnerFrom.getSelectedItemPosition());
        outState.putInt(STATE_SELECTION_TO, spinnerTo.getSelectedItemPosition());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.converter_dimensions_spinner:
                onDimensionChanged(dimensionsAdapter.getItem(position).dimension);
                break;
            case R.id.converter_spinner_from:
                onUnitFromChanged(adapterFrom.getItem(position));
                break;
            case R.id.converter_spinner_to:
                convert();
                break;
        }
    }

    private void onUnitFromChanged(@NonNull Unit<?> unit) {
        final int dimensionPosition = dimensionsSpinner.getSelectedItemPosition();
        updateUnitsTo(dimensionsAdapter.getItem(dimensionPosition).dimension, unit);
        convert();
    }

    private void onDimensionChanged(@NonNull MyDimension dimension) {
        updateUnitsFrom(dimension);
        updateUnitsTo(dimension, adapterFrom.getItem(spinnerFrom.getSelectedItemPosition()));
        convert();
    }

    private void updateUnitsFrom(@NonNull MyDimension dimension) {
        adapterFrom.setNotifyOnChange(false);
        adapterFrom.clear();
        for (Unit<?> unit : units.get(dimension)) {
            adapterFrom.add(unit);
        }
        adapterFrom.setNotifyOnChange(true);
        adapterFrom.notifyDataSetChanged();
        spinnerFrom.setSelection(Math.max(0, Math.min(pendingFromSelection, adapterFrom.getCount() - 1)));
        pendingFromSelection = View.NO_ID;
    }

    private void updateUnitsTo(@NonNull MyDimension dimension, @NonNull Unit<?> except) {
        final Unit<?> selectedUnit;
        if (pendingToSelection > View.NO_ID) {
            selectedUnit = null;
        } else {
            final int selectedPosition = spinnerTo.getSelectedItemPosition();
            selectedUnit = selectedPosition >= 0 && selectedPosition < adapterTo.getCount() ? adapterTo.getItem(selectedPosition) : null;
        }
        adapterTo.setNotifyOnChange(false);
        adapterTo.clear();
        for (Unit<?> unit : units.get(dimension)) {
            if (!except.equals(unit)) {
                adapterTo.add(unit);
            }
        }
        adapterTo.setNotifyOnChange(true);
        adapterTo.notifyDataSetChanged();
        if (selectedUnit != null && !except.equals(selectedUnit)) {
            for (int i = 0; i < adapterTo.getCount(); i++) {
                final Unit<?> unit = adapterTo.getItem(i);
                if (unit.equals(selectedUnit)) {
                    spinnerTo.setSelection(i);
                    return;
                }
            }
        }
        spinnerTo.setSelection(Math.max(0, Math.min(pendingToSelection, adapterTo.getCount() - 1)));
        pendingToSelection = View.NO_ID;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.converter_edittext_from:
                if (!hasFocus) {
                    convert();
                } else {
                    clearError(labelFrom);
                }
                break;
        }
    }

    private void convert() {
        convert(true);
    }

    private void convert(boolean validate) {
        final String value = editTextFrom.getText().toString();
        if (TextUtils.isEmpty(value)) {
            if (validate) {
                setError(labelFrom, "Empty");
            }
            return;
        }

        try {
            final Double fromValue = Double.valueOf(value);
            final Unit<?> from = adapterFrom.getItem(spinnerFrom.getSelectedItemPosition());
            final Unit<?> to = adapterTo.getItem(spinnerTo.getSelectedItemPosition());
            final double toValue = from.getConverterTo(to).convert(fromValue);
            editTextTo.setText(String.valueOf(toValue));
            clearError(labelFrom);
        } catch (RuntimeException e) {
            if (validate) {
                setError(labelFrom, e.getLocalizedMessage());
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (v.getId()) {
            case R.id.converter_edittext_from:
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    App.hideIme(editTextFrom);
                    convert();
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.converter_swap_button:
                swap();
                return;
        }
    }

    private void swap() {
        editTextFrom.setText(editTextTo.getText());
        final Unit<?> oldFromUnit = adapterFrom.getItem(spinnerFrom.getSelectedItemPosition());
        final Unit<?> oldToUnit = adapterTo.getItem(spinnerTo.getSelectedItemPosition());

        pendingToSelection = -1;
        for (int i = 0; i < adapterFrom.getCount(); i++) {
            pendingToSelection++;
            final Unit<?> unit = adapterFrom.getItem(i);
            if (unit.equals(oldToUnit)) {
                pendingToSelection--;
            } else if (unit.equals(oldFromUnit)) {
                break;
            }
        }

        for (int i = 0; i < adapterFrom.getCount(); i++) {
            final Unit<?> unit = adapterFrom.getItem(i);
            if (unit.equals(oldToUnit)) {
                spinnerFrom.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        convert(false);
    }

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
    }

    private class MyDimensionUi {
        @NonNull
        public final MyDimension dimension;

        private MyDimensionUi(@NonNull MyDimension dimension) {
            this.dimension = dimension;
        }

        @Override
        public String toString() {
            return dimension.name;
        }
    }
}
