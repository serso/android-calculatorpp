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

import android.view.View;
import android.widget.EditText;
import jscl.CustomFunctionCalculationException;
import jscl.math.function.Function;
import jscl.math.function.IFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.calculator.CalculatorFunctionsMathRegistry;
import org.solovyev.android.calculator.CalculatorMathRegistry;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.math.edit.VarEditorSaver;
import org.solovyev.android.calculator.model.AFunction;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.Strings;

import java.util.Collections;
import java.util.List;

public class FunctionEditorSaver implements View.OnClickListener {

	@Nonnull
	private final Object source;

	@Nonnull
	private final AFunction.Builder builder;

	@Nullable
	private final IFunction editedInstance;

	@Nonnull
	private final View view;

	@Nonnull
	private final CalculatorMathRegistry<Function> mathRegistry;


	public FunctionEditorSaver(@Nonnull AFunction.Builder builder,
							   @Nullable IFunction editedInstance,
							   @Nonnull View view,
							   @Nonnull CalculatorMathRegistry<Function> registry,
							   @Nonnull Object source) {

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
		if (parameterNames == null) {
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

					if (!Strings.isEmpty(content)) {
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
			Locator.getInstance().getNotifier().showMessage(error, MessageType.error);
		} else {
			try {
				CalculatorFunctionsMathRegistry.saveFunction(mathRegistry, new FunctionBuilderAdapter(builder), editedInstance, source, true);
			} catch (CustomFunctionCalculationException e) {
				Locator.getInstance().getNotifier().showMessage(e);
			} catch (AFunction.Builder.CreationException e) {
				Locator.getInstance().getNotifier().showMessage(e);
			}
		}
	}

	@Nonnull
	public static FunctionEditDialogFragment.Input readInput(@Nullable IFunction function, @Nonnull View root) {
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

	private boolean validateParameters(@Nonnull List<String> parameterNames) {
		for (String parameterName : parameterNames) {
			if (!VarEditorSaver.isValidName(parameterName)) {
				return false;
			}
		}

		return true;
	}

}
