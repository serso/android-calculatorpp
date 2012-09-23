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
 * Time: 9:52 PM
 */
class VarEditorSaver<T extends MathEntity> implements DialogInterface.OnClickListener {

	public static interface EditorCreator<T extends MathEntity> {
		void showEditor(@NotNull AbstractMathEntityListActivity<T> activity,
						@Nullable T editedInstance,
						@Nullable String name,
						@Nullable String value,
						@Nullable String description);
	}

	@NotNull
	private final EditorCreator<T> editorCreator;

	@NotNull
	private final MathEntityBuilder<? extends T> varBuilder;

	@Nullable
	private final T editedInstance;

	@NotNull
	private final CalculatorMathRegistry<T> mathRegistry;

	@NotNull
	private final AbstractMathEntityListActivity<T> activity;

	@NotNull
	private View editView;

	public VarEditorSaver(@NotNull MathEntityBuilder<? extends T> varBuilder,
						  @Nullable T editedInstance,
						  @NotNull View editView,
						  @NotNull AbstractMathEntityListActivity<T> activity,
						  @NotNull CalculatorMathRegistry<T> mathRegistry,
						  @NotNull EditorCreator<T> editorCreator) {
		this.varBuilder = varBuilder;
		this.editedInstance = editedInstance;
		this.editView = editView;
		this.activity = activity;
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

				final T entityFromRegistry = mathRegistry.get(name);
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
							boolean valid = CalculatorVarsActivity.isValidValue(value);

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
				Toast.makeText(activity, activity.getString(error), Toast.LENGTH_LONG).show();
				editorCreator.showEditor(activity, editedInstance, name, value, description);
			} else {
				final T addedVar = mathRegistry.add(varBuilder);
				if (activity.isInCategory(addedVar)) {
					if (editedInstance != null) {
						activity.removeFromAdapter(editedInstance);
					}
					activity.addToAdapter(addedVar);
				}

				mathRegistry.save();

				if (activity.isInCategory(addedVar)) {
					activity.sort();
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
