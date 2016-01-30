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

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.Activities;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.keyboard.FloatingKeyboard;
import org.solovyev.android.calculator.keyboard.FloatingKeyboardWindow;
import org.solovyev.android.calculator.view.EditTextCompat;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static org.solovyev.android.calculator.functions.CppFunction.NO_ID;

public class EditVariableFragment extends BaseDialogFragment implements CalculatorEventListener, View.OnFocusChangeListener, View.OnKeyListener, View.OnClickListener {

    private static final String ARG_VARIABLE = "variable";
    private final static String greekAlphabet = "αβγδεζηθικλμνξοπρστυφχψω";
    private final static List<Character> acceptableChars = Arrays.asList(Strings.toObjects(("1234567890abcdefghijklmnopqrstuvwxyzйцукенгшщзхъфывапролджэячсмитьбюё_" + greekAlphabet).toCharArray()));
    @NonNull
    private final FloatingKeyboardWindow keyboardWindow = new FloatingKeyboardWindow();
    @NonNull
    private final KeyboardUser keyboardUser = new KeyboardUser();
    @Bind(R.id.variable_name_label)
    TextInputLayout nameLabel;
    @Bind(R.id.variable_name)
    EditTextCompat nameView;
    @Bind(R.id.variable_keyboard_button)
    Button keyboardButton;
    @Bind(R.id.variable_value_label)
    TextInputLayout valueLabel;
    @Bind(R.id.variable_value)
    EditText valueView;
    @Bind(R.id.variable_description)
    EditText descriptionView;
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
            final Intent intent = new Intent(context, VariablesActivity.class);
            Activities.addIntentFlags(intent, false, context);
            intent.putExtra(VariablesActivity.EXTRA_VARIABLE, variable);
            context.startActivity(intent);
        } else {
            EditVariableFragment.showDialog(variable, ((VariablesActivity) context).getSupportFragmentManager());
        }
    }

    public static void showDialog(@Nullable CppVariable variable, @Nonnull FragmentManager fm) {
        App.showDialog(create(variable), "variable-editor", fm);
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
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.c_cancel, null);
        builder.setPositiveButton(R.string.ok, null);
        builder.setTitle(isNewVariable() ? R.string.c_var_create_var : R.string.c_var_edit_var);
        if (!isNewVariable()) {
            builder.setNeutralButton(R.string.c_remove, null);
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
    protected void onShowDialog(@NonNull AlertDialog dialog) {
        super.onShowDialog(dialog);

        nameView.selectAll();
        showIme(nameView);

        final Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryClose();
            }
        });
        if (!isNewVariable()) {
            Check.isNotNull(variable);
            final Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // FIXME: 2016-01-30 removal dialog
                    // showRemovalDialog(function);
                }
            });
        }
    }

    private void tryClose() {
        if (validate() && applyData()) {
            dismiss();
        }
    }

    private boolean applyData() {
        return false;
    }

    private boolean validate() {
        return validateName() & validateValue();
    }

    private boolean validateValue() {
        return false;
    }

    private boolean validateName() {
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();

        Locator.getInstance().getCalculator().addCalculatorEventListener(this);
    }

    @Override
    public void onPause() {
        Locator.getInstance().getCalculator().removeCalculatorEventListener(this);

        super.onPause();
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType) {
            case constant_removed:
            case constant_added:
            case constant_changed:
                if (calculatorEventData.getSource() == this) {
                    dismiss();
                }
                break;

        }
    }

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
        }
    }

    private void showKeyboard() {
        nameView.dontShowSoftInputOnFocusCompat();
        keyboardWindow.show(new GreekFloatingKeyboard(keyboardUser), getDialog());
    }

    private class NameWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!acceptableChars.contains(Character.toLowerCase(c))) {
                    s.delete(i, i + 1);
                    Toast.makeText(getActivity(), String.format(getString(R.string.c_char_is_not_accepted), c), Toast.LENGTH_SHORT).show();
                }
            }
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
        public Resources getResources() {
            return EditVariableFragment.this.getResources();
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
    }
}
