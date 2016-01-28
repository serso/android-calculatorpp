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

package org.solovyev.android.calculator.function;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.Calculator;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.FunctionsRegistry;
import org.solovyev.android.calculator.KeyboardUi;
import org.solovyev.android.calculator.KeyboardWindow;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.ParseException;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.VariablesRegistry;
import org.solovyev.android.calculator.math.edit.FunctionsActivity;
import org.solovyev.android.calculator.math.edit.VarEditorSaver;
import org.solovyev.android.calculator.view.EditTextCompat;
import org.solovyev.common.math.MathRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import jscl.math.function.Function;

import static org.solovyev.android.calculator.function.CppFunction.NO_ID;

public class EditFunctionFragment extends BaseDialogFragment implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    private static final String ARG_FUNCTION = "function";
    private static final int MENU_FUNCTION = Menu.FIRST;
    private static final int MENU_CONSTANT = Menu.FIRST + 1;
    private static final int MENU_CATEGORY = Menu.FIRST + 2;

    @NonNull
    private final VariablesRegistry constantsRegistry = Locator.getInstance().getEngine().getVariablesRegistry();
    @NonNull
    private final KeyboardWindow keyboardWindow = new KeyboardWindow();
    @NonNull
    private final KeyboardUser keyboardUser = new KeyboardUser();
    @Bind(R.id.function_params)
    FunctionParamsView paramsView;
    @Bind(R.id.function_name_label)
    TextInputLayout nameLabel;
    @Bind(R.id.function_name)
    EditText nameView;
    @Bind(R.id.function_body_label)
    TextInputLayout bodyLabel;
    @Bind(R.id.function_body)
    EditTextCompat bodyView;
    @Bind(R.id.function_description)
    EditText descriptionView;
    @Inject
    Calculator calculator;
    @Inject
    FunctionsRegistry functionsRegistry;
    @Nullable
    private CppFunction function;

    @Nonnull
    private static EditFunctionFragment create(@Nullable CppFunction function) {
        final EditFunctionFragment fragment = new EditFunctionFragment();
        if (function != null) {
            final Bundle args = new Bundle();
            args.putParcelable(ARG_FUNCTION, function);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public static void showDialog(@Nonnull FragmentActivity activity) {
        EditFunctionFragment.showDialog(null, activity.getSupportFragmentManager());
    }

    public static void showDialog(@Nonnull CppFunction function, @Nonnull Context context) {
        if (!(context instanceof FunctionsActivity)) {
            final Intent intent = new Intent(context, FunctionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(FunctionsActivity.EXTRA_FUNCTION, function);
            context.startActivity(intent);
        } else {
            EditFunctionFragment.showDialog(function, ((AppCompatActivity) context).getSupportFragmentManager());
        }
    }

    public static void showDialog(@Nullable CppFunction input, @Nonnull FragmentManager fm) {
        App.showDialog(create(input), "function-editor", fm);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            function = arguments.getParcelable(ARG_FUNCTION);
        }
    }

    @Override
    protected void inject(@NonNull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.c_cancel, null);
        builder.setPositiveButton(R.string.ok, null);
        builder.setTitle(isNewFunction() ? R.string.function_create_function : R.string.function_edit_function);
        if (!isNewFunction()) {
            builder.setNeutralButton(R.string.c_remove, null);
        }
    }

    private boolean isNewFunction() {
        return function == null || function.id == NO_ID;
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
        if (!isNewFunction()) {
            Check.isNotNull(function);
            final Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tryRemoveFunction(function, false);
                }
            });
        }
    }

    private void tryRemoveFunction(@NonNull CppFunction function, boolean confirmed) {
        if (!confirmed) {
            showRemovalDialog(function);
            return;
        }
        final Function entity = function.toJsclBuilder().create();
        functionsRegistry.remove(entity);
        calculator.fireCalculatorEvent(CalculatorEventType.function_removed, entity, this);
        dismiss();
    }

    private void showRemovalDialog(@NonNull final CppFunction function) {
        new AlertDialog.Builder(getActivity(), App.getTheme().alertDialogTheme)
                .setCancelable(true)
                .setTitle(R.string.removal_confirmation)
                .setMessage(getString(R.string.function_removal_confirmation_question, function.getName()))
                .setNegativeButton(R.string.c_no, null)
                .setPositiveButton(R.string.c_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tryRemoveFunction(function, true);
                    }
                })
                .create().show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v instanceof EditText && FunctionParamsView.PARAM_VIEW_TAG.equals(v.getTag())) {
            final ViewParent parentView = v.getParent();
            if (parentView instanceof TextInputLayout) {
                if (hasFocus) {
                    clearError((TextInputLayout) parentView);
                } else {
                    validateParameters();
                }
            }
            return;
        }

        final int id = v.getId();
        switch (id) {
            case R.id.function_name:
                if (hasFocus) {
                    clearError(nameLabel);
                } else {
                    validateName();
                }
                break;
            case R.id.function_body:
                if (hasFocus) {
                    clearError(bodyLabel);
                    showKeyboard();
                } else {
                    keyboardWindow.hide();
                    validateBody();
                }
                break;
        }
    }

    private void showKeyboard() {
        keyboardWindow.show(keyboardUser, getDialog(), collectParameters());
    }

    @Nonnull
    private List<String> collectParameters() {
        final List<String> parameters = new ArrayList<>();
        for (String parameter : paramsView.getParams()) {
            if (!TextUtils.isEmpty(parameter)) {
                parameters.add(parameter);
            }
        }
        return parameters;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.function_body) {
            showKeyboard();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v.getId() == R.id.function_body) {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK && keyboardWindow.isShown()) {
                keyboardWindow.hide();
                return true;
            }
        }
        return false;
    }

    private void tryClose() {
        if (validate() && applyData()) {
            dismiss();
        }
    }

    private boolean applyData() {
        try {
            final String body = calculator.prepareExpression(bodyView.getText().toString()).getExpression();

            final CppFunction newFunction = CppFunction.builder(nameView.getText().toString(), body)
                    .withId(isNewFunction() ? NO_ID : function.id)
                    .withParameters(collectParameters())
                    .withDescription(descriptionView.getText().toString()).build();
            final Function oldFunction = isNewFunction() ? null : functionsRegistry.getById(function.id);
            functionsRegistry.add(newFunction.toJsclBuilder(), oldFunction);
            return true;
        } catch (RuntimeException e) {
            setError(bodyLabel, e.getLocalizedMessage());
        }
        return false;
    }

    private boolean validate() {
        return validateName() & validateParameters() & validateBody();
    }

    private boolean validateName() {
        final String name = nameView.getText().toString();
        if (!VarEditorSaver.isValidName(name)) {
            setError(nameLabel, getString(R.string.function_name_is_not_valid));
            return false;
        }
        final Function existingFunction = functionsRegistry.get(name);
        if (existingFunction != null) {
            if (!existingFunction.isIdDefined()) {
                Check.shouldNotHappen();
                setError(nameLabel, getString(R.string.function_already_exists));
                return false;
            }
            if (isNewFunction()) {
                // trying to create a new function with existing name
                setError(nameLabel, getString(R.string.function_already_exists));
                return false;
            }
            Check.isNotNull(function);
            if (!existingFunction.getId().equals(function.getId())) {
                // trying ti change the name of existing function to some other function's name
                setError(nameLabel, getString(R.string.function_already_exists));
                return false;
            }
        }
        clearError(nameLabel);
        return true;
    }

    private boolean validateBody() {
        final String body = bodyView.getText().toString();
        if (TextUtils.isEmpty(body)) {
            setError(bodyLabel, getString(R.string.function_is_empty));
            return false;
        }
        try {
            calculator.prepareExpression(body);
            clearError(bodyLabel);
            return true;
        } catch (ParseException e) {
            setError(bodyLabel, e.getLocalizedMessage());
            return false;
        }
    }

    private boolean validateParameters() {
        boolean valid = true;
        final List<String> parameters = paramsView.getParams();
        final Set<String> usedParameters = new HashSet<>();
        for (int i = 0; i < parameters.size(); i++) {
            final String parameter = parameters.get(i);
            final TextInputLayout paramLabel = paramsView.getParamLabel(i);
            if (TextUtils.isEmpty(parameter)) {
                clearError(paramLabel);
            } else if (!VarEditorSaver.isValidName(parameter)) {
                valid = false;
                setError(paramLabel, getString(R.string.invalid_name));
            } else if (usedParameters.contains(parameter)) {
                valid = false;
                setError(paramLabel, getString(R.string.function_duplicate_parameter));
            } else {
                usedParameters.add(parameter);
                clearError(paramLabel);
            }
        }
        return valid;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    protected View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_function_edit, null);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null && function != null) {
            paramsView.addParams(function.getParameters());
            nameView.setText(function.getName());
            descriptionView.setText(function.getDescription());
            bodyView.setText(function.getBody());
        }
        nameView.setOnFocusChangeListener(this);
        paramsView.setOnFocusChangeListener(this);
        bodyView.setOnClickListener(this);
        bodyView.setOnFocusChangeListener(this);
        bodyView.setOnKeyListener(this);
        bodyView.dontShowSoftInputOnFocusCompat();
        descriptionView.setOnFocusChangeListener(this);

        return view;
    }

    private class KeyboardUser implements KeyboardUi.User, MenuItem.OnMenuItemClickListener {
        @NonNull
        @Override
        public Context getContext() {
            return getActivity();
        }

        @NonNull
        @Override
        public Resources getResources() {
            return EditFunctionFragment.this.getResources();
        }

        @NonNull
        @Override
        public EditText getEditor() {
            return bodyView;
        }

        @NonNull
        @Override
        public ViewGroup getKeyboard() {
            return keyboardWindow.getContentView();
        }

        @Override
        public void insertOperator(char operator) {
            insertOperator(String.valueOf(operator));
        }

        public int clampSelection(int selection) {
            return selection < 0 ? 0 : selection;
        }

        @Override
        public void insertOperator(@NonNull String operator) {
            final int start = clampSelection(bodyView.getSelectionStart());
            final int end = clampSelection(bodyView.getSelectionEnd());
            final Editable e = bodyView.getText();
            e.replace(start, end, getOperator(start, end, e, operator));
        }

        @NonNull
        private String getOperator(int start, int end, @NonNull Editable e, @NonNull CharSequence operator) {
            boolean spaceBefore = true;
            boolean spaceAfter = true;
            if (start > 0 && Character.isSpaceChar(e.charAt(start - 1))) {
                spaceBefore = false;
            }
            if (end < e.length() && Character.isSpaceChar(e.charAt(end))) {
                spaceAfter = false;
            }

            if (spaceBefore && spaceAfter) {
                return " " + operator + " ";
            }
            if (spaceBefore) {
                return " " + operator;
            }
            if (spaceAfter) {
                return operator + " ";
            }
            return String.valueOf(operator);
        }

        @Override
        public void showConstants(@NonNull View v) {
            bodyView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    final int id = v.getId();
                    if (id == R.id.function_body) {
                        menu.clear();
                        addEntities(menu, getNamesSorted(constantsRegistry), MENU_CONSTANT);
                        unregisterForContextMenu(bodyView);
                    }
                }
            });
            bodyView.showContextMenu();
        }

        @Nonnull
        private List<String> getNamesSorted(@NonNull MathRegistry<?> registry) {
            final List<String> names = new ArrayList<>(registry.getNames());
            Collections.sort(names);
            return names;
        }

        @Override
        public void showFunctions(@NonNull View v) {
            bodyView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    final int id = v.getId();
                    if (id == R.id.function_body) {
                        menu.clear();
                        addEntities(menu, getNamesSorted(functionsRegistry), MENU_FUNCTION);
                        unregisterForContextMenu(bodyView);
                    }
                }
            });
            bodyView.showContextMenu();
        }

        private void addEntities(@NonNull Menu menu, @NonNull List<String> entities, int groupId) {
            for (String entity : entities) {
                menu.add(groupId, Menu.NONE, Menu.NONE, entity).setOnMenuItemClickListener(KeyboardUser.this);
            }
        }

        @Override
        public void showFunctionsConstants(@NonNull View v) {
            bodyView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    final int id = v.getId();
                    if (id == R.id.function_body) {
                        menu.clear();
                        // can't use sub-menus as AlertDialog doesn't support them
                        menu.add(MENU_CATEGORY, MENU_CONSTANT, Menu.NONE, R.string.c_vars_and_constants).setOnMenuItemClickListener(KeyboardUser.this);
                        menu.add(MENU_CATEGORY, MENU_FUNCTION, Menu.NONE, R.string.c_functions).setOnMenuItemClickListener(KeyboardUser.this);
                        unregisterForContextMenu(bodyView);
                    }
                }
            });
            bodyView.showContextMenu();
        }

        @Override
        public void insertText(@NonNull CharSequence text, int selectionOffset) {
            final int start = clampSelection(bodyView.getSelectionStart());
            final int end = clampSelection(bodyView.getSelectionEnd());
            final Editable e = bodyView.getText();
            e.replace(start, end, text);
            if (selectionOffset != 0) {
                final int selection = clampSelection(bodyView.getSelectionEnd());
                final int newSelection = selection + selectionOffset;
                if (newSelection >= 0 && newSelection < e.length()) {
                    bodyView.setSelection(newSelection);
                }
            }
        }

        @Override
        public void done() {
            keyboardWindow.hide();
            validateBody();
        }

        @Override
        public void showIme() {
            final InputMethodManager keyboard = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(getEditor(), InputMethodManager.SHOW_FORCED);
            keyboardWindow.hide();
        }

        @Override
        public boolean onMenuItemClick(final MenuItem item) {
            final int groupId = item.getGroupId();
            final CharSequence title = item.getTitle();
            switch (groupId) {
                case MENU_FUNCTION:
                    final int argsListIndex = title.toString().indexOf("(");
                    if (argsListIndex < 0) {
                        keyboardUser.insertText(title + "()", -1);
                    } else {
                        keyboardUser.insertText(title.subSequence(0, argsListIndex) + "()", -1);
                    }
                    return true;
                case MENU_CONSTANT:
                    keyboardUser.insertText(title.toString(), 0);
                    return true;
                case MENU_CATEGORY:
                    bodyView.post(new Runnable() {
                        @Override
                        public void run() {
                            final int itemId = item.getItemId();
                            if (itemId == MENU_FUNCTION) {
                                showFunctions(bodyView);
                            } else if (itemId == MENU_CONSTANT) {
                                showConstants(bodyView);
                            }
                        }
                    });
                    return true;
            }
            return false;
        }
    }
}
