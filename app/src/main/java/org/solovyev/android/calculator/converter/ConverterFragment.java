package org.solovyev.android.calculator.converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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
import jscl.JsclMathEngine;
import jscl.NumeralBase;
import midpcalc.Real;
import org.solovyev.android.calculator.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.util.*;

public class ConverterFragment extends BaseDialogFragment
        implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener, TextView.OnEditorActionListener, View.OnClickListener, TextWatcher {

    // todo serso: better to provide a dimension-id pair as units might not be unique in different dimensions
    @NonNull
    private static final Set<String> excludedUnits = new HashSet<>(Arrays.asList("year_sidereal", "year_calendar", "day_sidereal", "foot_survey_us", "me", "u"));
    @NonNull
    private static final Map<NamedDimension, List<MyUnit>> units = new HashMap<>();
    private static final String STATE_SELECTION_FROM = "selection.from";
    private static final String STATE_SELECTION_TO = "selection.to";
    private static final String EXTRA_VALUE = "value";
    private static final NamedItemComparator COMPARATOR = new NamedItemComparator();

    static {
        for (Unit<?> unit : SI.getInstance().getUnits()) {
            addUnit(unit);
        }
        for (Unit<?> unit : NonSI.getInstance().getUnits()) {
            addUnit(unit);
        }
    }

    @Inject
    Clipboard clipboard;
    @Inject
    Editor editor;
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
    private ArrayAdapter<NamedItem<NamedDimension>> dimensionsAdapter;
    private ArrayAdapter<NamedItem<MyUnit>> adapterFrom;
    private ArrayAdapter<NamedItem<MyUnit>> adapterTo;

    private int pendingFromSelection = View.NO_ID;
    private int pendingToSelection = View.NO_ID;

    private static void addUnit(@NonNull Unit<?> unit) {
        if (excludedUnits.contains(unit.toString())) {
            return;
        }

        final NamedDimension dimension = NamedDimension.of(unit);
        if (dimension == null) {
            return;
        }

        List<MyUnit> unitsInDimension = units.get(dimension);
        if (unitsInDimension == null) {
            unitsInDimension = new ArrayList<>();
            units.put(dimension, unitsInDimension);
        }
        unitsInDimension.add(MeasureUnit.create(unit));
    }

    public static void show(@Nonnull FragmentActivity activity) {
        show(activity, 1d);
    }

    public static void show(@Nonnull FragmentActivity activity, double value) {
        final ConverterFragment fragment = new ConverterFragment();
        final Bundle args = new Bundle(1);
        args.putDouble(EXTRA_VALUE, value);
        fragment.setArguments(args);
        App.showDialog(fragment, "converter", activity.getSupportFragmentManager());
    }

    @Nonnull
    private static <T> ArrayAdapter<T> makeAdapter(@NonNull Context context) {
        return new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item);
    }

    @Nonnull
    private static <T> NamedItem<T> createNamedItem(@NonNull T item, @StringRes int name, @NonNull Context context) {
        return new NamedItem<>(item, name == 0 ? item.toString() : context.getString(name));
    }

    @Override
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.c_use, null);
        builder.setNegativeButton(R.string.cpp_cancel, null);
        builder.setNeutralButton(R.string.cpp_copy, null);
    }

    @Override
    protected void inject(@NonNull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    protected View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater,
                                      @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.cpp_unit_converter, null);
        ButterKnife.bind(this, view);

        dimensionsAdapter = makeAdapter(context);
        for (NamedDimension dimension : NamedDimension.values()) {
            dimensionsAdapter.add(named(dimension));
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

    @Nonnull
    private NamedItem<NamedDimension> named(@Nonnull NamedDimension dimension) {
        return createNamedItem(dimension, dimension.name);
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
                onDimensionChanged(dimensionsAdapter.getItem(position).item);
                break;
            case R.id.converter_spinner_from:
                onUnitFromChanged(adapterFrom.getItem(position).item);
                break;
            case R.id.converter_spinner_to:
                convert();
                break;
        }
    }

    private void onUnitFromChanged(@NonNull MyUnit unit) {
        final int dimensionPosition = dimensionsSpinner.getSelectedItemPosition();
        updateUnitsTo(dimensionsAdapter.getItem(dimensionPosition).item, unit);
        convert();
    }

    private void onDimensionChanged(@NonNull NamedDimension dimension) {
        updateUnitsFrom(dimension);
        updateUnitsTo(dimension, adapterFrom.getItem(spinnerFrom.getSelectedItemPosition()).item);
        convert();
    }

    private void updateUnitsFrom(@NonNull NamedDimension dimension) {
        adapterFrom.setNotifyOnChange(false);
        adapterFrom.clear();
        for (MyUnit unit : units.get(dimension)) {
            adapterFrom.add(unit.named(getActivity()));
        }
        adapterFrom.sort(COMPARATOR);
        adapterFrom.setNotifyOnChange(true);
        adapterFrom.notifyDataSetChanged();
        spinnerFrom.setSelection(Math.max(0, Math.min(pendingFromSelection, adapterFrom.getCount() - 1)));
        pendingFromSelection = View.NO_ID;
    }

    private void updateUnitsTo(@NonNull NamedDimension dimension, @NonNull MyUnit except) {
        final MyUnit selectedUnit;
        if (pendingToSelection > View.NO_ID) {
            selectedUnit = null;
        } else {
            final int selectedPosition = spinnerTo.getSelectedItemPosition();
            selectedUnit = selectedPosition >= 0 && selectedPosition < adapterTo.getCount() ? adapterTo.getItem(selectedPosition).item : null;
        }
        adapterTo.setNotifyOnChange(false);
        adapterTo.clear();
        for (MyUnit unit : units.get(dimension)) {
            if (!except.equals(unit)) {
                adapterTo.add(unit.named(getActivity()));
            }
        }
        adapterTo.sort(COMPARATOR);
        adapterTo.setNotifyOnChange(true);
        adapterTo.notifyDataSetChanged();
        if (selectedUnit != null && !except.equals(selectedUnit)) {
            for (int i = 0; i < adapterTo.getCount(); i++) {
                final MyUnit unit = adapterTo.getItem(i).item;
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
            final MyUnit from = adapterFrom.getItem(spinnerFrom.getSelectedItemPosition()).item;
            final Object fromValue = from.parse(value);
            final MyUnit to = adapterTo.getItem(spinnerTo.getSelectedItemPosition()).item;
            final Object toValue = from.getConverterTo(to).convert(fromValue);
            editTextTo.setText(to.format(toValue));
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
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            dismiss();
            return;
        }
        final String text = editTextTo.getText().toString();
        try {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    editor.insert(text);
                    dismiss();
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    clipboard.setText(text);
                    Toast.makeText(getActivity(), getString(R.string.c_result_copied),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (RuntimeException ignored) {
        }
    }

    private void swap() {
        editTextFrom.setText(editTextTo.getText());
        final MyUnit oldFromUnit = adapterFrom.getItem(spinnerFrom.getSelectedItemPosition()).item;
        final MyUnit oldToUnit = adapterTo.getItem(spinnerTo.getSelectedItemPosition()).item;

        pendingToSelection = -1;
        for (int i = 0; i < adapterFrom.getCount(); i++) {
            pendingToSelection++;
            final MyUnit unit = adapterFrom.getItem(i).item;
            if (unit.equals(oldToUnit)) {
                pendingToSelection--;
            } else if (unit.equals(oldFromUnit)) {
                break;
            }
        }

        for (int i = 0; i < adapterFrom.getCount(); i++) {
            final MyUnit unit = adapterFrom.getItem(i).item;
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

    @Override
    public void dismiss() {
        App.hideIme(this);
        super.dismiss();
    }

    @Nonnull
    private <T> NamedItem<T> createNamedItem(@NonNull T item, @StringRes int name) {
        return createNamedItem(item, name, getActivity());
    }

    private interface MyUnit {
        @NonNull
        Object parse(@NonNull String value);

        @NonNull
        String format(@NonNull Object value);

        @NonNull
        MyConverter getConverterTo(@NonNull MyUnit to);

        @NonNull
        NamedItem<MyUnit> named(@NonNull Context context);
    }

    private interface MyConverter {
        Object convert(Object value);
    }

    private static class NamedItem<T> {
        @NonNull
        public final T item;
        @NonNull
        public final CharSequence name;

        private NamedItem(@NonNull T item, @Nonnull String name) {
            this.item = item;
            this.name = name;
        }

        @Override
        public String toString() {
            return name.toString();
        }
    }

    private static class NamedItemComparator implements Comparator<NamedItem<MyUnit>> {
        @Override
        public int compare(NamedItem<MyUnit> lhs, NamedItem<MyUnit> rhs) {
            return lhs.toString().compareTo(rhs.toString());
        }
    }

    private static final class MeasureUnit implements MyUnit {
        @NonNull
        final Unit unit;

        private MeasureUnit(@NonNull Unit unit) {
            this.unit = unit;
        }

        private static MeasureUnit create(@NonNull Unit unit) {
            return new MeasureUnit(unit);
        }

        @Override
        public String toString() {
            return unit.toString();
        }

        @NonNull
        @Override
        public Object parse(@NonNull String value) {
            final String groupingSeparator = String.valueOf(JsclMathEngine.getInstance().getGroupingSeparator());
            if (!TextUtils.isEmpty(groupingSeparator)) {
                value = value.replace(groupingSeparator, "");
            }
            final long bits = new Real(value).toDoubleBits();
            return Double.longBitsToDouble(bits);
        }

        @NonNull
        @Override
        public String format(@NonNull Object value) {
            return JsclMathEngine.getInstance().format((Double) value, NumeralBase.dec);
        }

        @NonNull
        @Override
        public MyConverter getConverterTo(@NonNull final MyUnit to) {
            return new MyConverter() {
                @Override
                public Object convert(Object value) {
                    final UnitConverter converter = unit.getConverterTo(((MeasureUnit) to).unit);
                    return converter.convert((Double) value);
                }
            };
        }

        @NonNull
        @Override
        public NamedItem<MyUnit> named(@Nonnull Context context) {
            final NamedDimension dimension = NamedDimension.of(unit);
            if (dimension == null) {
                return createNamedItem((MyUnit) MeasureUnit.create(unit), 0, context);
            }
            return createNamedItem((MyUnit) MeasureUnit.create(unit), Converter.unitName(unit, dimension), context);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final MeasureUnit that = (MeasureUnit) o;
            return unit.equals(that.unit);

        }

        @Override
        public int hashCode() {
            return unit.hashCode();
        }
    }
}
