package org.solovyev.android.calculator.function;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jscl.math.function.CustomFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils2;
import org.solovyev.android.calculator.*;

/**
 * User: serso
 * Date: 11/13/12
 * Time: 11:34 PM
 */
public class FunctionEditDialogFragment extends DialogFragment implements CalculatorEventListener {

    @NotNull
    private final Input input;

    public FunctionEditDialogFragment() {
        this(Input.newInstance());
    }

    public FunctionEditDialogFragment(@NotNull Input input) {
        this.input = input;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.function_edit, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final View addParamButton = root.findViewById(R.id.function_add_param_button);

        final ViewGroup functionParamsLayout = (ViewGroup) root.findViewById(R.id.function_params_layout);
        addParamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View functionEditParamLayout = inflater.inflate(R.layout.function_edit_param, null);

                final View addParamButton = functionEditParamLayout.findViewById(R.id.function_add_param_button);
                addParamButton.setVisibility(View.GONE);

                final View removeParamButton = functionEditParamLayout.findViewById(R.id.function_remove_param_button);
                removeParamButton.setVisibility(View.VISIBLE);

                functionParamsLayout.addView(functionEditParamLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        CalculatorLocatorImpl.getInstance().getCalculator().addCalculatorEventListener(this);
    }

    @Override
    public void onPause() {
        CalculatorLocatorImpl.getInstance().getCalculator().removeCalculatorEventListener(this);

        super.onPause();
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
    }

        /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    public static void showDialog(@NotNull Input input, @NotNull FragmentManager fm) {
        AndroidUtils2.showDialog(new FunctionEditDialogFragment(input), "function-editor", fm);
    }

    public static class Input {

        @Nullable
        private CustomFunction function;

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
        public static Input newFromFunction(@NotNull CustomFunction function) {
            final Input result = new Input();
            result.function = function;
            return result;
        }

        @NotNull
        public static Input newFromValue(@Nullable String value) {
            final Input result = new Input();
            result.value = value;
            return result;
        }

        @NotNull
        public static Input newInstance(@Nullable CustomFunction function, @Nullable String name, @Nullable String value, @Nullable String description) {
            final Input result = new Input();
            result.function = function;
            result.name = name;
            result.value = value;
            result.description = description;
            return result;
        }

        @Nullable
        public CustomFunction getFunction() {
            return function;
        }

        @Nullable
        public String getName() {
            return name == null ? (function == null ? null : function.getName()) : name;
        }

        @Nullable
        public String getValue() {
            return value == null ? (function == null ? null : function.getContent()) : value;
        }

        @Nullable
        public String getDescription() {
            return description == null ? (function == null ? null : function.getContent()) : description;
        }
    }
}
