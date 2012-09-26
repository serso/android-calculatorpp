/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.text.Identifier;
import jscl.text.MutableInt;
import jscl.text.ParseException;
import jscl.text.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.CalculatorMathRegistry;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.MathEntityBuilder;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.text.StringUtils;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 11:26 PM
 */
public class FunctionEditorSaver implements DialogInterface.OnClickListener{

	public static interface EditorCreator<T extends MathEntity> {
		void showEditor(@NotNull AbstractMathEntityListFragment<T> activity,
						@Nullable CustomFunction editedInstance,
						@Nullable String name,
						@Nullable String value,
						@Nullable String[] parameterNames,
						@Nullable String description);
	}

	@NotNull
	private final EditorCreator<Function> editorCreator;

	@NotNull
	private final MathEntityBuilder<CustomFunction> varBuilder;

	@Nullable
	private final CustomFunction editedInstance;

	@NotNull
	private final CalculatorMathRegistry<Function> mathRegistry;

	@NotNull
	private final AbstractMathEntityListFragment<Function> fragment;

	@NotNull
	private View editView;

	public FunctionEditorSaver(@NotNull MathEntityBuilder<CustomFunction> varBuilder,
							   @Nullable CustomFunction editedInstance,
							   @NotNull View editView,
							   @NotNull AbstractMathEntityListFragment<Function> fragment,
							   @NotNull CalculatorMathRegistry<Function> mathRegistry,
							   @NotNull EditorCreator<Function> editorCreator) {
		this.varBuilder = varBuilder;
		this.editedInstance = editedInstance;
		this.editView = editView;
		this.fragment = fragment;
		this.mathRegistry = mathRegistry;
		this.editorCreator = editorCreator;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			final Integer error;

			final EditText editName = (EditText) editView.findViewById(R.id.var_edit_name);
			String name = editName.getText().toString();

			final EditText editValue = (EditText) editView.findViewById(R.id.var_edit_value);
			String value = editValue.getText().toString();

			final EditText editDescription = (EditText) editView.findViewById(R.id.var_edit_description);
			String description = editDescription.getText().toString();

			if (isValidName(name)) {

				boolean canBeSaved = false;

				final Function entityFromRegistry = mathRegistry.get(name);
				if (entityFromRegistry == null) {
					canBeSaved = true;
				} else if (editedInstance != null && entityFromRegistry.getId().equals(editedInstance.getId())) {
					canBeSaved = true;
				}

				if (canBeSaved) {
					final MathType.Result mathType = MathType.getType(name, 0, false);

					if (mathType.getMathType() == MathType.text || mathType.getMathType() == MathType.constant) {

						if (StringUtils.isEmpty(value)) {
							// value is empty => undefined variable
							varBuilder.setName(name);
							varBuilder.setDescription(description);
							varBuilder.setValue(null);
							error = null;
						} else {
							// value is not empty => must be a number
							boolean valid = CalculatorVarsFragment.isValidValue(value);

							if (valid) {
								varBuilder.setName(name);
								varBuilder.setDescription(description);
								varBuilder.setValue(value);
								error = null;
							} else {
								error = R.string.c_value_is_not_a_number;
							}
						}
					} else {
						error = R.string.c_var_name_clashes;
					}
				} else {
					error = R.string.c_var_already_exists;
				}
			} else {
				error = R.string.c_name_is_not_valid;
			}

			if (error != null) {
				Toast.makeText(fragment.getActivity(), fragment.getString(error), Toast.LENGTH_LONG).show();
				editorCreator.showEditor(fragment, editedInstance, name, value, null, description);
			} else {
				final Function addedVar = mathRegistry.add(varBuilder);
				if (fragment.isInCategory(addedVar)) {
					if (editedInstance != null) {
						fragment.removeFromAdapter(editedInstance);
					}
					fragment.addToAdapter(addedVar);
				}

				mathRegistry.save();

				if (fragment.isInCategory(addedVar)) {
					fragment.sort();
				}
			}
		}
	}

	boolean isValidName(@Nullable String name) {
		boolean result = false;

		if (!StringUtils.isEmpty(name)) {
			try {
				assert name != null;
				Identifier.parser.parse(Parser.Parameters.newInstance(name, new MutableInt(0), CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0()), null);
				result = true;
			} catch (ParseException e) {
				// not valid name;
			}
		}

		return result;
	}
}

