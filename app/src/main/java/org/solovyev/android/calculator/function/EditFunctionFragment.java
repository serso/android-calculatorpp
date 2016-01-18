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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.CalculatorEventData;
import org.solovyev.android.calculator.CalculatorEventListener;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorUtils;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.math.edit.CalculatorFunctionsActivity;
import org.solovyev.android.calculator.math.edit.FunctionsFragment;
import org.solovyev.android.calculator.math.edit.MathEntityRemover;
import org.solovyev.android.calculator.math.edit.VarEditorSaver;
import org.solovyev.android.calculator.model.AFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import butterknife.Bind;
import butterknife.ButterKnife;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;

public class EditFunctionFragment extends BaseDialogFragment implements CalculatorEventListener {

    private static final String ARG_INPUT = "input";
    @Bind(R.id.function_params)
    FunctionParamsView paramsView;
    @Bind(R.id.function_name_label)
    TextInputLayout nameLabel;
    @Bind(R.id.function_name)
    EditText nameView;
    @Bind(R.id.function_body)
    EditText bodyView;
    @Bind(R.id.function_description)
    EditText descriptionView;
    private Input input;

    @Nonnull
    public static EditFunctionFragment create(@Nonnull Input input) {
        final EditFunctionFragment fragment = new EditFunctionFragment();
        fragment.input = input;
        final Bundle args = new Bundle();
        args.putParcelable("input", input);
        fragment.setArguments(args);
        return fragment;
    }

    public static void showDialog(@Nonnull Input input, @Nonnull Context context) {
        if (context instanceof AppCompatActivity) {
            EditFunctionFragment.showDialog(input, ((AppCompatActivity) context).getSupportFragmentManager());
        } else {
            final Intent intent = new Intent(context, CalculatorFunctionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(FunctionsFragment.EXTRA_FUNCTION, input);
            context.startActivity(intent);
        }
    }

    public static void showDialog(@Nonnull Input input, @Nonnull FragmentManager fm) {
        App.showDialog(create(input), "function-editor", fm);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        input = getArguments().getParcelable(ARG_INPUT);
    }

    @Override
    protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.c_cancel, null);
        builder.setPositiveButton(R.string.ok, null);
        final AFunction function = input.getFunction();
        builder.setTitle(function == null ? R.string.function_create_function : R.string.function_edit_function);
        if(function != null) {
            builder.setNeutralButton(R.string.c_remove, null);
        }
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                nameView.selectAll();
                showIme(nameView);

                final Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tryClose();
                    }
                });
                final AFunction function = input.getFunction();
                if (function != null) {
                    final Function customFunction = new CustomFunction.Builder(function).create();
                    final Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                    neutral.setOnClickListener(MathEntityRemover.newFunctionRemover(customFunction, null, getActivity(), EditFunctionFragment.this));
                }
            }
        });
        return dialog;
    }

    private void tryClose() {
        if (validate()) {
            applyData();
            dismiss();
        }
    }

    private void applyData() {

    }

    private boolean validate() {
        if (!validateName()) {
            return false;
        }
        return true;
    }

    private boolean validateName() {
        final String name = nameView.getText().toString();
        if (!VarEditorSaver.isValidName(name)) {
            setError(nameLabel, getString(R.string.function_name_is_not_valid));
            return false;
        }
        clearError(nameLabel);
        return true;
    }

    @NonNull
    @Override
    protected View onCreateDialogView(@NonNull Context context, @NonNull LayoutInflater inflater, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_function_edit, null);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            final List<String> parameterNames = input.getParameterNames();
            if (parameterNames != null) {
                paramsView.addParams(parameterNames);
            }

            nameView.setText(input.getName());
            descriptionView.setText(input.getDescription());
            bodyView.setText(input.getContent());
        }

        return view;
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
            case function_removed:
            case function_added:
            case function_changed:
                if (calculatorEventData.getSource() == EditFunctionFragment.this) {
                    dismiss();
                }
                break;

        }
    }

    public static class Input implements Parcelable {

        private static final Parcelable.Creator<String> STRING_CREATOR = new Creator<String>() {
            @Override
            public String createFromParcel(@Nonnull Parcel in) {
                return in.readString();
            }

            @Override
            public String[] newArray(int size) {
                return new String[size];
            }
        };
        @Nullable
        private AFunction function;
        @Nullable
        private String name;
        @Nullable
        private String content;
        @Nullable
        private String description;
        @Nullable
        private List<String> parameterNames;
        public static final Parcelable.Creator<Input> CREATOR = new Creator<Input>() {
            @Override
            public Input createFromParcel(@Nonnull Parcel in) {
                return Input.fromParcel(in);
            }

            @Override
            public Input[] newArray(int size) {
                return new Input[size];
            }
        };

        private Input() {
        }

        @Nonnull
        private static Input fromParcel(@Nonnull Parcel in) {
            final Input result = new Input();
            result.name = in.readString();
            result.content = in.readString();
            result.description = in.readString();

            final List<String> parameterNames = new ArrayList<String>();
            in.readTypedList(parameterNames, STRING_CREATOR);
            result.parameterNames = parameterNames;

            result.function = (AFunction) in.readSerializable();

            return result;
        }

        @Nonnull
        public static Input newInstance() {
            return new Input();
        }

        @Nonnull
        public static Input newFromFunction(@Nonnull IFunction function) {
            final Input result = new Input();
            result.function = AFunction.fromIFunction(function);
            return result;
        }

        @Nonnull
        public static Input newInstance(@Nullable IFunction function,
                                        @Nullable String name,
                                        @Nullable String value,
                                        @Nullable String description,
                                        @Nonnull List<String> parameterNames) {

            final Input result = new Input();
            if (function != null) {
                result.function = AFunction.fromIFunction(function);
            }
            result.name = name;
            result.content = value;
            result.description = description;
            result.parameterNames = new ArrayList<String>(parameterNames);

            return result;
        }

        @Nonnull
        public static Input newFromDisplay(@Nonnull DisplayState viewState) {
            final Input result = new Input();

            result.content = viewState.text;
            final Generic generic = viewState.getResult();
            if (generic != null) {
                final Set<Constant> constants = CalculatorUtils.getNotSystemConstants(generic);
                final List<String> parameterNames = new ArrayList<String>(constants.size());
                for (Constant constant : constants) {
                    parameterNames.add(constant.getName());
                }
                result.parameterNames = parameterNames;
            }

            return result;
        }

        @Nullable
        public AFunction getFunction() {
            return function;
        }

        @Nullable
        public String getName() {
            return name == null ? (function == null ? null : function.getName()) : name;
        }

        @Nullable
        public String getContent() {
            return content == null ? (function == null ? null : function.getContent()) : content;
        }

        @Nullable
        public String getDescription() {
            return description == null ? (function == null ? null : function.getDescription()) : description;
        }

        @Nullable
        public List<String> getParameterNames() {
            return parameterNames == null ? (function == null ? null : function.getParameterNames()) : parameterNames;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@Nonnull Parcel out, int flags) {
            out.writeString(name);
            out.writeString(content);
            out.writeString(description);
            out.writeList(parameterNames);
            out.writeSerializable(function);
        }
    }
}
