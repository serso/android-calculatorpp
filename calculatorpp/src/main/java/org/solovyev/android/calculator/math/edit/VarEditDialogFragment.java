package org.solovyev.android.calculator.math.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.Var;
import org.solovyev.common.text.StringUtils;

/**
 * User: Solovyev_S
 * Date: 01.10.12
 * Time: 17:41
 */
public class VarEditDialogFragment extends DialogFragment {

    @NotNull
    private final Input input;

    public VarEditDialogFragment() {
        this(Input.newInstance());
    }

    public VarEditDialogFragment(@NotNull Input input) {
        this.input = input;
    }

    public static void createEditVariableDialog(@NotNull final AbstractMathEntityListFragment<IConstant> fragment,
                                                @NotNull Input input) {

        final FragmentManager fm = fragment.getActivity().getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag("constant-editor");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        final DialogFragment newFragment = new VarEditDialogFragment(input);
        newFragment.show(ft, "constant-editor");

    }

    public static void createEditVariableDialog0(@NotNull final AbstractMathEntityListFragment<IConstant> fragment,
                                                 @Nullable final IConstant var,
                                                 @Nullable final String name,
                                                 @Nullable final String value,
                                                 @Nullable final String description) {
        final FragmentActivity activity = fragment.getActivity();

        if (var == null || !var.isSystem()) {

            final LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            final View result = layoutInflater.inflate(R.layout.var_edit, null);

            final String errorMsg = fragment.getString(R.string.c_char_is_not_accepted);

            final EditText editName = (EditText) result.findViewById(R.id.var_edit_name);
            editName.setText(name);
            editName.addTextChangedListener(new TextWatcher() {

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
                        if (!AbstractMathEntityListFragment.acceptableChars.contains(c)) {
                            s.delete(i, i + 1);
                            Toast.makeText(activity, String.format(errorMsg, c), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            final EditText editValue = (EditText) result.findViewById(R.id.var_edit_value);
            if (!StringUtils.isEmpty(value)) {
                editValue.setText(value);
            }

            final EditText editDescription = (EditText) result.findViewById(R.id.var_edit_description);
            editDescription.setText(description);

            final Var.Builder varBuilder;
            if (var != null) {
                varBuilder = new Var.Builder(var);
            } else {
                varBuilder = new Var.Builder();
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setNegativeButton(R.string.c_cancel, null)
                    .setPositiveButton(R.string.c_save, new VarEditorSaver<IConstant>(varBuilder, var, result, fragment, CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry(), new VarEditorSaver.EditorCreator<IConstant>() {
                        @Override
                        public void showEditor(@NotNull AbstractMathEntityListFragment<IConstant> activity, @Nullable IConstant editedInstance, @Nullable String name, @Nullable String value, @Nullable String description) {
                            createEditVariableDialog(activity, Input.newInstance(editedInstance, name, value, description));
                        }
                    }))
                    .setView(result);

            if (var != null) {
                // EDIT mode

                builder.setTitle(R.string.c_var_edit_var);
                builder.setNeutralButton(R.string.c_remove, new MathEntityRemover<IConstant>(var, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createEditVariableDialog(fragment, Input.newInstance(var, name, value, description));
                    }
                }, CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry(), fragment));
            } else {
                // CREATE mode

                builder.setTitle(R.string.c_var_create_var);
            }

            builder.create().show();
        } else {
            Toast.makeText(activity, fragment.getString(R.string.c_sys_var_cannot_be_changed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View result = inflater.inflate(R.layout.var_edit, container, false);


        return result;
    }

    @Override
    public void onViewCreated(@NotNull View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final String errorMsg = this.getString(R.string.c_char_is_not_accepted);

        final EditText editName = (EditText) root.findViewById(R.id.var_edit_name);
        editName.setText(input.getName());
        editName.addTextChangedListener(new TextWatcher() {

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
                    if (!AbstractMathEntityListFragment.acceptableChars.contains(c)) {
                        s.delete(i, i + 1);
                        Toast.makeText(getActivity(), String.format(errorMsg, c), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        final EditText editValue = (EditText) root.findViewById(R.id.var_edit_value);
        editValue.setText(input.getValue());

        final EditText editDescription = (EditText) root.findViewById(R.id.var_edit_description);
        editDescription.setText(input.getDescription());

        final Var.Builder varBuilder;
        final IConstant constant = input.getConstant();
        if (constant != null) {
            varBuilder = new Var.Builder(constant);
        } else {
            varBuilder = new Var.Builder();
        }

        if ( constant == null ) {
            // CREATE MODE
            getDialog().setTitle(R.string.c_var_create_var);
        } else {
            // EDIT MODE
            getDialog().setTitle(R.string.c_var_edit_var);
        }
    }

    public static class Input {

        @Nullable
        private IConstant constant;

        @Nullable
        private String name;

        @Nullable
        private String value;

        @Nullable
        private String description;

        private Input() {
        }

        @NotNull
        public static Input newInstance() {
            return new Input();
        }

        @NotNull
        public static Input newFromConstant(@NotNull IConstant constant) {
            final Input result = new Input();
            result.constant = constant;
            return result;
        }

        @NotNull
        public static Input newFromValue(@Nullable String value) {
            final Input result = new Input();
            result.value = value;
            return result;
        }

        @NotNull
        public static Input newInstance(@Nullable IConstant constant, @Nullable String name, @Nullable String value, @Nullable String description) {
            final Input result = new Input();
            result.constant = constant;
            result.name = name;
            result.value = value;
            result.description = description;
            return result;
        }

        @Nullable
        public IConstant getConstant() {
            return constant;
        }

        @Nullable
        public String getName() {
            return name == null ? (constant == null ? null : constant.getName()) : name;
        }

        @Nullable
        public String getValue() {
            return value == null ? (constant == null ? null : constant.getValue()) : value;
        }

        @Nullable
        public String getDescription() {
            return description == null ? (constant == null ? null : constant.getDescription()) : description;
        }
    }
}
