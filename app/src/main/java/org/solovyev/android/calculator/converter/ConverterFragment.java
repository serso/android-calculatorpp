package org.solovyev.android.calculator.converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.AppModule;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.Clipboard;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.Keyboard;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.keyboard.FloatingKeyboard;
import org.solovyev.android.calculator.keyboard.FloatingKeyboardWindow;
import org.solovyev.android.calculator.keyboard.FloatingNumberKeyboard;
import org.solovyev.android.calculator.math.MathUtils;
import org.solovyev.android.calculator.view.EditTextCompat;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.solovyev.android.calculator.UiPreferences.Converter.lastDimension;
import static org.solovyev.android.calculator.UiPreferences.Converter.lastUnitsFrom;
import static org.solovyev.android.calculator.UiPreferences.Converter.lastUnitsTo;

public class ConverterFragment extends BaseDialogFragment
        implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener, TextView.OnEditorActionListener, View.OnClickListener, TextWatcher {

    private static final int NUMBER_INPUT_TYPE = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED;
    private static final String STATE_SELECTION_FROM = "selection.from";
    private static final String STATE_SELECTION_TO = "selection.to";
    private static final String EXTRA_VALUE = "value";
    private static final NamedItemComparator COMPARATOR = new NamedItemComparator();
    public static final int NONE = -1;

    @NonNull
    private final FloatingKeyboardWindow keyboardWindow = new FloatingKeyboardWindow(null);
    @Inject
    Typeface typeface;
    @Inject
    Clipboard clipboard;
    @Inject
    Keyboard keyboard;
    @javax.inject.Named(AppModule.PREFS_UI)
    @Inject
    SharedPreferences uiPreferences;
    @Inject
    Editor editor;
    @Bind(R.id.converter_dimensions_spinner)
    Spinner dimensionsSpinner;
    @Bind(R.id.converter_spinner_from)
    Spinner spinnerFrom;
    @Bind(R.id.converter_label_from)
    TextInputLayout labelFrom;
    @Bind(R.id.converter_edittext_from)
    EditTextCompat editTextFrom;
    @Bind(R.id.converter_spinner_to)
    Spinner spinnerTo;
    @Bind(R.id.converter_label_to)
    TextInputLayout labelTo;
    @Bind(R.id.converter_edittext_to)
    EditText editTextTo;
    @Bind(R.id.converter_swap_button)
    ImageButton swapButton;
    private ArrayAdapter<Named<ConvertibleDimension>> dimensionsAdapter;
    private ArrayAdapter<Named<Convertible>> adapterFrom;
    private ArrayAdapter<Named<Convertible>> adapterTo;

    private int pendingFromSelection = NONE;
    private int pendingToSelection = NONE;
    private boolean useSystemKeyboard = true;

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

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
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
        for (ConvertibleDimension dimension : UnitDimension.values()) {
            dimensionsAdapter.add(dimension.named(context));
        }
        dimensionsAdapter.add(NumeralBaseDimension.get().named(context));
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
        editTextFrom.setOnClickListener(this);
        onKeyboardTypeChanged();

        swapButton.setOnClickListener(this);
        swapButton.setImageResource(App.getTheme().light ? R.drawable.ic_swap_vert_black_24dp : R.drawable.ic_swap_vert_white_24dp);

        if (savedInstanceState == null) {
            editTextFrom.setText(String.valueOf(getArguments().getDouble(EXTRA_VALUE, 1f)));
            pendingFromSelection = lastUnitsFrom.getPreference(uiPreferences);
            pendingToSelection = lastUnitsTo.getPreference(uiPreferences);
            dimensionsSpinner.setSelection(MathUtils.clamp(lastDimension.getPreference(uiPreferences), 0, dimensionsAdapter.getCount() - 1));
        } else {
            pendingFromSelection = savedInstanceState.getInt(STATE_SELECTION_FROM, NONE);
            pendingToSelection = savedInstanceState.getInt(STATE_SELECTION_TO, NONE);
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
    public void onPause() {
        saveLastUsedValues();
        super.onPause();
    }

    private void saveLastUsedValues() {
        final SharedPreferences.Editor editor = uiPreferences.edit();
        lastDimension.putPreference(editor, dimensionsSpinner.getSelectedItemPosition());
        lastUnitsFrom.putPreference(editor, spinnerFrom.getSelectedItemPosition());
        lastUnitsTo.putPreference(editor, spinnerTo.getSelectedItemPosition());
        editor.apply();
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

    private void onUnitFromChanged(@NonNull Convertible unit) {
        final int dimensionPosition = dimensionsSpinner.getSelectedItemPosition();
        updateUnitsTo(dimensionsAdapter.getItem(dimensionPosition).item, unit);
        convert();
    }

    private void onDimensionChanged(@NonNull ConvertibleDimension dimension) {
        updateUnitsFrom(dimension);
        updateUnitsTo(dimension, adapterFrom.getItem(spinnerFrom.getSelectedItemPosition()).item);
        convert();

        checkKeyboardType(dimension);
    }

    private void checkKeyboardType(@NonNull ConvertibleDimension dimension) {
        keyboardWindow.hide();
        useSystemKeyboard = !(dimension instanceof NumeralBaseDimension);
        onKeyboardTypeChanged();
    }

    private void onKeyboardTypeChanged() {
        editTextFrom.setInputType(useSystemKeyboard ? NUMBER_INPUT_TYPE : InputType.TYPE_CLASS_TEXT);
        editTextFrom.setShowSoftInputOnFocusCompat(useSystemKeyboard);
    }

    private void updateUnitsFrom(@NonNull ConvertibleDimension dimension) {
        adapterFrom.setNotifyOnChange(false);
        adapterFrom.clear();
        for (Convertible unit : dimension.getUnits()) {
            adapterFrom.add(unit.named(getActivity()));
        }
        adapterFrom.sort(COMPARATOR);
        adapterFrom.setNotifyOnChange(true);
        adapterFrom.notifyDataSetChanged();
        if (pendingFromSelection != NONE) {
            spinnerFrom.setSelection(MathUtils.clamp(pendingFromSelection, 0, adapterFrom.getCount() - 1));
            pendingFromSelection = NONE;
        } else {
            spinnerFrom.setSelection(MathUtils.clamp(spinnerFrom.getSelectedItemPosition(), 0, adapterFrom.getCount() - 1));
        }
    }

    private void updateUnitsTo(@NonNull ConvertibleDimension dimension, @NonNull Convertible except) {
        final Convertible selectedUnit;
        if (pendingToSelection > NONE) {
            selectedUnit = null;
        } else {
            final int selectedPosition = spinnerTo.getSelectedItemPosition();
            selectedUnit = selectedPosition >= 0 && selectedPosition < adapterTo.getCount() ? adapterTo.getItem(selectedPosition).item : null;
        }
        adapterTo.setNotifyOnChange(false);
        adapterTo.clear();
        for (Convertible unit : dimension.getUnits()) {
            if (!except.equals(unit)) {
                adapterTo.add(unit.named(getActivity()));
            }
        }
        adapterTo.sort(COMPARATOR);
        adapterTo.setNotifyOnChange(true);
        adapterTo.notifyDataSetChanged();
        if (selectedUnit != null && !except.equals(selectedUnit)) {
            for (int i = 0; i < adapterTo.getCount(); i++) {
                final Convertible unit = adapterTo.getItem(i).item;
                if (unit.equals(selectedUnit)) {
                    spinnerTo.setSelection(i);
                    return;
                }
            }
        }
        if (pendingToSelection != NONE) {
            spinnerTo.setSelection(MathUtils.clamp(pendingToSelection, 0, adapterTo.getCount() - 1));
            pendingToSelection = NONE;
        } else {
            spinnerTo.setSelection(MathUtils.clamp(spinnerTo.getSelectedItemPosition(), 0, adapterTo.getCount() - 1));
        }
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
                    showKeyboard();
                }
                break;
        }
    }

    private void showKeyboard() {
        if (useSystemKeyboard) {
            return;
        }
        keyboardWindow.show(new FloatingNumberKeyboard(new KeyboardUser()), null);
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
            final Convertible from = adapterFrom.getItem(spinnerFrom.getSelectedItemPosition()).item;
            final Convertible to = adapterTo.getItem(spinnerTo.getSelectedItemPosition()).item;
            editTextTo.setText(from.convert(to, value));
            clearError(labelFrom);
        } catch (RuntimeException e) {
            editTextTo.setText("");
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
                keyboardWindow.hide();
                swap();
                break;
            case R.id.converter_edittext_from:
                showKeyboard();
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
        final Convertible oldFromUnit = adapterFrom.getItem(spinnerFrom.getSelectedItemPosition()).item;
        final Convertible oldToUnit = adapterTo.getItem(spinnerTo.getSelectedItemPosition()).item;

        pendingToSelection = NONE;
        for (int i = 0; i < adapterFrom.getCount(); i++) {
            pendingToSelection++;
            final Convertible unit = adapterFrom.getItem(i).item;
            if (unit.equals(oldToUnit)) {
                pendingToSelection--;
            } else if (unit.equals(oldFromUnit)) {
                break;
            }
        }

        for (int i = 0; i < adapterFrom.getCount(); i++) {
            final Convertible unit = adapterFrom.getItem(i).item;
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

    private static class NamedItemComparator implements Comparator<Named<Convertible>> {
        @Override
        public int compare(Named<Convertible> lhs, Named<Convertible> rhs) {
            return lhs.toString().compareTo(rhs.toString());
        }
    }

    private class KeyboardUser implements FloatingKeyboard.User {
        @NonNull
        @Override
        public Context getContext() {
            return getActivity();
        }

        @NonNull
        @Override
        public EditText getEditor() {
            return editTextFrom;
        }

        @NonNull
        @Override
        public ViewGroup getKeyboard() {
            return keyboardWindow.getContentView();
        }

        @Override
        public void done() {
            keyboardWindow.hide();
            convert();
        }

        @Override
        public void showIme() {
            final InputMethodManager keyboard = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(getEditor(), InputMethodManager.SHOW_FORCED);
            keyboardWindow.hide();
        }

        @Override
        public boolean isVibrateOnKeypress() {
            return keyboard.isVibrateOnKeypress();
        }

        @NonNull
        @Override
        public Typeface getTypeface() {
            return typeface;
        }
    }
}
