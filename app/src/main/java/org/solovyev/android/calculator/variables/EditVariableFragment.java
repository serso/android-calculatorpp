/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.variables;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.Calculator;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Keyboard;
import org.solovyev.android.calculator.PreparedExpression;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.RemovalConfirmationDialog;
import org.solovyev.android.calculator.ToJsclTextProcessor;
import org.solovyev.android.calculator.VariablesRegistry;
import org.solovyev.android.calculator.functions.FunctionsRegistry;
import org.solovyev.android.calculator.keyboard.FloatingKeyboard;
import org.solovyev.android.calculator.keyboard.FloatingKeyboardWindow;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.view.EditTextCompat;
import org.solovyev.android.text.method.NumberInputFilter;
import org.solovyev.common.text.Strings;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Lazy;
import jscl.math.function.IConstant;

import static org.solovyev.android.calculator.variables.CppVariable.NO_ID;

public class EditVariableFragment extends BaseDialogFragment implements View.OnFocusChangeListener, View.OnKeyListener, View.OnClickListener {

    private static final String ARG_VARIABLE = "variable";
    private final static List<Character> ACCEPTABLE_CHARACTERS = Arrays.asList(Strings.toObjects(("1234567890abcdefghijklmnopqrstuvwxyzйцукенгшщзхъфывапролджэячсмитьбюё_" + GreekFloatingKeyboard.ALPHABET).toCharArray()));
    @NonNull
    private final KeyboardUser keyboardUser = new KeyboardUser();
    @Bind(R.id.variable_name_label)
    TextInputLayout nameLabel;
    @Bind(R.id.variable_name)
    EditTextCompat nameView;
    @NonNull
    private final FloatingKeyboardWindow keyboardWindow = new FloatingKeyboardWindow(new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            nameView.setShowSoftInputOnFocusCompat(true);
        }
    });
    @Bind(R.id.variable_keyboard_button)
    Button keyboardButton;
    @Bind(R.id.variable_value_label)
    TextInputLayout valueLabel;
    @Bind(R.id.variable_value)
    EditText valueView;
    @Bind(R.id.variable_exponent_button)
    Button exponentButton;
    @Bind(R.id.variable_description)
    EditText descriptionView;
    @Inject
    Calculator calculator;
    @Inject
    Keyboard keyboard;
    @Inject
    Typeface typeface;
    @Inject
    FunctionsRegistry functionsRegistry;
    @Inject
    VariablesRegistry variablesRegistry;
    @Inject
    Lazy<ToJsclTextProcessor> toJsclTextProcessor;
    @Inject
    Engine engine;
    @Nullable
    private CppVariable variable;

    public EditVariableFragment() {
    }

    @Nonnull
    public static EditVariableFragment create(@Nullable CppVariable variable) {
        final EditVariableFragment fragment = new EditVariableFragment();
        if (variable != null) {
            final Bundle args = new Bundle();
            args.putParcelable(ARG_VARIABLE, variable);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public static void showDialog(@Nonnull FragmentActivity activity) {
        EditVariableFragment.showDialog(null, activity.getSupportFragmentManager());
    }

    public static void showDialog(@Nullable CppVariable variable, @Nonnull Context context) {
        if (!(context instanceof VariablesActivity)) {
            final Intent intent = new Intent(context, VariablesActivity.getClass(context));
            App.addIntentFlags(intent, false, context);
            intent.putExtra(VariablesActivity.EXTRA_VARIABLE, variable);
            context.startActivity(intent);
        } else {
            EditVariableFragment.showDialog(variable,
                    ((VariablesActivity) context).getSupportFragmentManager());
        }
    }

    public static void showDialog(@Nullable CppVariable variable, @Nonnull FragmentManager fm) {
        App.showDialog(create(variable), "variable-editor", fm);
    }

    public boolean isValidValue(@Nonnull String value) {
        try {
            final PreparedExpression pe = toJsclTextProcessor.get().process(value);
            return !pe.hasUndefinedVariables();
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            variable = arguments.getParcelable(ARG_VARIABLE);
        }
    }

    @Override
    protected void inject(@NonNull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.cpp_cancel, null);
        builder.setPositiveButton(R.string.cpp_done, null);
        builder.setTitle(isNewVariable() ? R.string.c_var_create_var : R.string.c_var_edit_var);
        if (!isNewVariable()) {
            builder.setNeutralButton(R.string.cpp_delete, null);
        }
    }

    private boolean isNewVariable() {
        return variable == null || variable.id == NO_ID;
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    @Override
    protected void onShowDialog(@NonNull AlertDialog dialog, boolean firstTime) {
        if (firstTime) {
            nameView.selectAll();
            showIme(nameView);
        }
    }

    private void showRemovalDialog(@NonNull final CppVariable variable) {
        RemovalConfirmationDialog.showForVariable(getActivity(), variable.name,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Check.isTrue(which == DialogInterface.BUTTON_POSITIVE);
                        variablesRegistry.remove(variable.toJsclConstant());
                        dismiss();
                    }
                });
    }

    private void tryClose() {
        if (validate() && applyData()) {
            dismiss();
        }
    }

    private boolean applyData() {
        try {
            final CppVariable newVariable = CppVariable.builder(nameView.getText().toString())
                    .withId(isNewVariable() ? NO_ID : variable.id)
                    .withValue(valueView.getText().toString())
                    .withDescription(descriptionView.getText().toString()).build();
            final IConstant oldVariable = isNewVariable() ? null : variablesRegistry.getById(variable.id);
            variablesRegistry.addOrUpdate(newVariable.toJsclConstant(), oldVariable);
            return true;
        } catch (RuntimeException e) {
            setError(valueLabel, e.getLocalizedMessage());
        }
        return false;
    }

    private boolean validate() {
        return validateName() & validateValue();
    }

    private boolean validateValue() {
        final String value = valueView.getText().toString();
        if (!Strings.isEmpty(value)) {
            // value is not empty => must be a number
            if (!isValidValue(value)) {
                setError(valueLabel, R.string.c_value_is_not_a_number);
                return false;
            }
        }

        clearError(valueLabel);
        return true;
    }

    private boolean validateName() {
        final String name = nameView.getText().toString();
        if (!Engine.isValidName(name)) {
            setError(nameLabel, getString(R.string.cpp_name_contains_invalid_characters));
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (!ACCEPTABLE_CHARACTERS.contains(Character.toLowerCase(c))) {
                setError(nameLabel, getString(R.string.c_char_is_not_accepted, c));
                return false;
            }
        }
        final IConstant existingVariable = variablesRegistry.get(name);
        if (existingVariable != null) {
            if (!existingVariable.isIdDefined()) {
                Check.shouldNotHappen();
                setError(nameLabel, getString(R.string.c_var_already_exists));
                return false;
            }
            if (isNewVariable()) {
                // trying to create a new variable with existing name
                setError(nameLabel, getString(R.string.c_var_already_exists));
                return false;
            }
            Check.isNotNull(variable);
            if (!existingVariable.getId().equals(variable.id)) {
                // trying to change the name of existing variable to some other variable's name
                setError(nameLabel, getString(R.string.c_var_already_exists));
                return false;
            }
        }

        final MathType.Result type = MathType.getType(name, 0, false, engine);
        if (type.type != MathType.text && type.type != MathType.constant) {
            setError(nameLabel, getString(R.string.c_var_name_clashes));
            return false;
        }

        clearError(nameLabel);
        return true;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    protected View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater, @android.support.annotation.Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_variable_edit, null);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null && variable != null) {
            nameView.setText(variable.name);
            valueView.setText(variable.value);
            descriptionView.setText(variable.description);
        }
        nameView.setOnFocusChangeListener(this);
        nameView.setOnKeyListener(this);
        valueView.setOnFocusChangeListener(this);
        valueView.setEditableFactory(new Editable.Factory() {
            @Override
            public Editable newEditable(CharSequence source) {
                return new NumberEditable(source);
            }
        });
        exponentButton.setOnClickListener(this);
        descriptionView.setOnFocusChangeListener(this);
        keyboardButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.variable_name:
                if (hasFocus) {
                    clearError(nameLabel);
                } else {
                    keyboardUser.done();
                }
                break;
            case R.id.variable_value:
                if (hasFocus) {
                    clearError(valueLabel);
                } else {
                    validateValue();
                }
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v.getId() == R.id.variable_name) {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK && keyboardWindow.isShown()) {
                keyboardUser.done();
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.variable_keyboard_button:
                if (keyboardWindow.isShown()) {
                    keyboardUser.showIme();
                } else {
                    showKeyboard();
                }
                break;
            case R.id.variable_exponent_button:
                final int start = Math.max(valueView.getSelectionStart(), 0);
                final int end = Math.max(valueView.getSelectionEnd(), 0);
                valueView.getText().replace(Math.min(start, end), Math.max(start, end), "E", 0, 1);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                tryClose();
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                Check.isNotNull(variable);
                showRemovalDialog(variable);
                break;
            default:
                super.onClick(dialog, which);
                break;
        }
    }

    private void showKeyboard() {
        nameView.dontShowSoftInputOnFocusCompat();
        keyboardWindow.show(new GreekFloatingKeyboard(keyboardUser), getDialog());
    }

    private static class NumberEditable extends SpannableStringBuilder {
        public NumberEditable(CharSequence source) {
            super(source);
            super.setFilters(new InputFilter[]{NumberInputFilter.getInstance()});
        }

        @Override
        public void setFilters(InputFilter[] filters) {
            // we don't want filters as we want to support numbers in scientific notation
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
            return nameView;
        }

        @NonNull
        @Override
        public ViewGroup getKeyboard() {
            return keyboardWindow.getContentView();
        }

        @Override
        public void done() {
            if (keyboardWindow.isShown()) {
                keyboardWindow.hide();
            }
            validateName();
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
