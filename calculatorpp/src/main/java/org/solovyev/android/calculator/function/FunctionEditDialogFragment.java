package org.solovyev.android.calculator.function;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils2;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.math.edit.MathEntityRemover;
import org.solovyev.android.calculator.model.AFunction;

import java.util.List;

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

        final FunctionParamsView paramsView = (FunctionParamsView) root.findViewById(R.id.function_params_layout);

		final AFunction.Builder builder;
		final IFunction function = input.getFunction();
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

		root.findViewById(R.id.save_button).setOnClickListener(new FunctionEditorSaver(builder, function, root, CalculatorLocatorImpl.getInstance().getEngine().getFunctionsRegistry(), this));

		if ( function == null ) {
			// CREATE MODE
			getDialog().setTitle(R.string.function_create_function);

			root.findViewById(R.id.remove_button).setVisibility(View.GONE);
		} else {
			// EDIT MODE
			getDialog().setTitle(R.string.function_edit_function);

			Function customFunction = new CustomFunction.Builder(function).create();
			root.findViewById(R.id.remove_button).setOnClickListener(MathEntityRemover.newFunctionRemover(customFunction, null, this.getActivity(), FunctionEditDialogFragment.this));
		}
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
		switch (calculatorEventType) {
			case function_removed:
			case function_added:
			case function_changed:
				if ( calculatorEventData.getSource() == FunctionEditDialogFragment.this ) {
					dismiss();
				}
				break;

		}
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
        private IFunction function;

        @Nullable
        private String name;

        @Nullable
        private String content;

        @Nullable
        private String description;

        @Nullable
        private List<String> parameterNames;

        private Input() {
        }

        @NotNull
        public static Input newInstance() {
            return new Input();
        }

        @NotNull
        public static Input newFromFunction(@NotNull IFunction function) {
            final Input result = new Input();
            result.function = function;
            return result;
        }

        @NotNull
        public static Input newFromValue(@Nullable String value) {
            final Input result = new Input();
            result.content = value;
            return result;
        }

        @NotNull
        public static Input newInstance(@Nullable IFunction function,
                                        @Nullable String name,
                                        @Nullable String value,
                                        @Nullable String description) {

            final Input result = new Input();
            result.function = function;
            result.name = name;
            result.content = value;
            result.description = description;
            return result;
        }

        @Nullable
        public IFunction getFunction() {
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
    }
}
