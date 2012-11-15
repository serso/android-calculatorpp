package org.solovyev.android.calculator.function;

import android.view.View;
import android.widget.EditText;
import jscl.CustomFunctionCalculationException;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorFunctionsMathRegistry;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.CalculatorMathRegistry;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.math.edit.VarEditorSaver;
import org.solovyev.android.calculator.model.AFunction;
import org.solovyev.android.calculator.model.MathEntityBuilder;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.StringUtils;

import java.util.Collections;
import java.util.List;

public class FunctionEditorSaver implements View.OnClickListener {

	@NotNull
	private final Object source;

	@NotNull
	private final AFunction.Builder builder;

	@Nullable
	private final IFunction editedInstance;

	@NotNull
	private final View view;

	@NotNull
	private final CalculatorMathRegistry<Function> mathRegistry;


	public FunctionEditorSaver(@NotNull AFunction.Builder builder,
							   @Nullable IFunction editedInstance,
							   @NotNull View view,
							   @NotNull CalculatorMathRegistry<Function> registry,
							   @NotNull Object source) {

		this.builder = builder;
		this.editedInstance = editedInstance;
		this.view = view;
		this.mathRegistry = registry;
		this.source = source;
	}

	@Override
	public void onClick(View v) {
		final Integer error;

		final FunctionEditDialogFragment.Input input = readInput(null, view);

		final String name = input.getName();
		final String content = input.getContent();
		final String description = input.getDescription();

		List<String> parameterNames = input.getParameterNames();
		if ( parameterNames == null ) {
			parameterNames = Collections.emptyList();
		}

		if (VarEditorSaver.isValidName(name)) {

			boolean canBeSaved = false;

			final Function entityFromRegistry = mathRegistry.get(name);
			if (entityFromRegistry == null) {
				canBeSaved = true;
			} else if (editedInstance != null && entityFromRegistry.getId().equals(editedInstance.getId())) {
				canBeSaved = true;
			}

			if (canBeSaved) {
				if (validateParameters(parameterNames)) {

                    if (!StringUtils.isEmpty(content)) {
                        builder.setParameterNames(parameterNames);
                        builder.setName(name);
                        builder.setDescription(description);
                        builder.setValue(content);
                        error = null;
                    } else {
                        error = R.string.function_is_empty;
                    }
                } else {
					error = R.string.function_param_not_empty;
				}
			} else {
				error = R.string.function_already_exists;
			}
		} else {
			error = R.string.function_name_is_not_valid;
		}

		if (error != null) {
			CalculatorLocatorImpl.getInstance().getNotifier().showMessage(error, MessageType.error);
		} else {
            try {
                CalculatorFunctionsMathRegistry.saveFunction(mathRegistry, new BuilderAdapter(builder), editedInstance, source, true);
            } catch (CustomFunctionCalculationException e) {
                CalculatorLocatorImpl.getInstance().getNotifier().showMessage(e);
            } catch (AFunction.Builder.CreationException e) {
				CalculatorLocatorImpl.getInstance().getNotifier().showMessage(e);
			}
        }
	}

	@NotNull
	public static FunctionEditDialogFragment.Input readInput(@Nullable IFunction function, @NotNull View root) {
		final EditText editName = (EditText) root.findViewById(R.id.function_edit_name);
		String name = editName.getText().toString();

		final EditText editValue = (EditText) root.findViewById(R.id.function_edit_value);
		String content = editValue.getText().toString();

		final EditText editDescription = (EditText) root.findViewById(R.id.function_edit_description);
		String description = editDescription.getText().toString();

		final FunctionParamsView editParams = (FunctionParamsView) root.findViewById(R.id.function_params_layout);
		List<String> parameterNames = editParams.getParameterNames();

		return FunctionEditDialogFragment.Input.newInstance(function, name, content, description, parameterNames);
	}

	private boolean validateParameters(@NotNull List<String> parameterNames) {
		for (String parameterName : parameterNames) {
			if ( !VarEditorSaver.isValidName(parameterName) ) {
				return false;
			}
		}

		return true;
	}

	private static final class BuilderAdapter implements MathEntityBuilder<Function> {

		@NotNull
		private final AFunction.Builder nestedBuilder;

		public BuilderAdapter(@NotNull AFunction.Builder nestedBuilder) {
			this.nestedBuilder = nestedBuilder;
		}

		@NotNull
		@Override
		public MathEntityBuilder<Function> setName(@NotNull String name) {
			nestedBuilder.setName(name);
			return this;
		}

		@NotNull
		@Override
		public MathEntityBuilder<Function> setDescription(@Nullable String description) {
			nestedBuilder.setDescription(description);
			return this;
		}

		@NotNull
		@Override
		public MathEntityBuilder<Function> setValue(@Nullable String value) {
			nestedBuilder.setValue(value);
			return this;
		}

		@NotNull
		@Override
		public Function create() {
			final AFunction function = nestedBuilder.create();
			return new CustomFunction.Builder(function).create();
		}
	}
}
