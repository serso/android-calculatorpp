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
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.math.edit.CalculatorFunctionsActivity;
import org.solovyev.android.calculator.math.edit.FunctionsFragment;
import org.solovyev.android.calculator.math.edit.MathEntityRemover;
import org.solovyev.android.calculator.model.AFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: serso
 * Date: 11/13/12
 * Time: 11:34 PM
 */
public class FunctionEditDialogFragment extends DialogFragment implements CalculatorEventListener {

    private static final String INPUT = "input";

    private Input input;

    public FunctionEditDialogFragment() {
    }

    @Nonnull
    public static FunctionEditDialogFragment create(@Nonnull Input input) {
        final FunctionEditDialogFragment fragment = new FunctionEditDialogFragment();
        fragment.input = input;
        final Bundle args = new Bundle();
        args.putParcelable("input", input);
        fragment.setArguments(args);
        return fragment;
    }

    public static void showDialog(@Nonnull Input input, @Nonnull Context context) {
        if (context instanceof ActionBarActivity) {
            FunctionEditDialogFragment.showDialog(input, ((ActionBarActivity) context).getSupportFragmentManager());
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

        if (input == null) {
            input = getArguments().getParcelable("input");
            if (input == null) throw new AssertionError();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View result = inflater.inflate(R.layout.function_edit, container, false);

        if (savedInstanceState != null) {
            final Parcelable input = savedInstanceState.getParcelable(INPUT);
            if (input instanceof Input) {
                this.input = (Input) input;
            }
        }

        return result;
    }

    @Override
    public void onViewCreated(@Nonnull View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final FunctionParamsView paramsView = (FunctionParamsView) root.findViewById(R.id.function_params_layout);

        final AFunction.Builder builder;
        final AFunction function = input.getFunction();
        if (function != null) {
            builder = new AFunction.Builder(function);
        } else {
            builder = new AFunction.Builder();
        }

        final List<String> parameterNames = input.getParameterNames();
        if (parameterNames != null) {
            paramsView.init(parameterNames);
        } else {
            paramsView.init();
        }

        final EditText editName = (EditText) root.findViewById(R.id.function_edit_name);
        // show soft keyboard automatically
        editName.requestFocus();
        editName.setText(input.getName());

        final EditText editDescription = (EditText) root.findViewById(R.id.function_edit_description);
        editDescription.setText(input.getDescription());

        final EditText editContent = (EditText) root.findViewById(R.id.function_edit_value);
        editContent.setText(input.getContent());

        root.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        root.findViewById(R.id.save_button).setOnClickListener(new FunctionEditorSaver(builder, function, root, Locator.getInstance().getEngine().getFunctionsRegistry(), this));

        if (function == null) {
            // CREATE MODE
            getDialog().setTitle(R.string.function_create_function);

            root.findViewById(R.id.remove_button).setVisibility(View.GONE);
        } else {
            // EDIT MODE
            getDialog().setTitle(R.string.function_edit_function);

            final Function customFunction = new CustomFunction.Builder(function).create();
            root.findViewById(R.id.remove_button).setOnClickListener(MathEntityRemover.newFunctionRemover(customFunction, null, this.getActivity(), FunctionEditDialogFragment.this));
        }
    }

    @Override
    public void onSaveInstanceState(@Nonnull Bundle out) {
        super.onSaveInstanceState(out);

        out.putParcelable(INPUT, FunctionEditorSaver.readInput(input.getFunction(), getView()));
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
                if (calculatorEventData.getSource() == FunctionEditDialogFragment.this) {
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
