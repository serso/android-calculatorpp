/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import jscl.math.function.Function;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorModel;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.view.AMenuItem;
import org.solovyev.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 10/29/11
 * Time: 4:55 PM
 */
public class CalculatorFunctionsActivity extends AbstractMathEntityListActivity<Function> {

	private static enum LongClickMenuItem implements AMenuItem<Function>{
		use(R.string.c_use) {
			@Override
			public void doAction(@NotNull Function data, @NotNull Context context) {
				CalculatorModel.instance.processDigitButtonAction(data.getName(), false);
				if (context instanceof Activity) {
					((Activity) context).finish();
				}
			}
		},

		/*edit(R.string.c_edit) {
			@Override
			public void doAction(@NotNull Function data, @NotNull Context context) {
				if (context instanceof AbstractMathEntityListActivity) {
				}
			}
		},*/

		copy_description(R.string.c_copy_description) {
			@Override
			public void doAction(@NotNull Function data, @NotNull Context context) {
				final String text = CalculatorEngine.instance.getFunctionsRegistry().getDescription(context, data.getName());
				if (!StringUtils.isEmpty(text)) {
					final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
					clipboard.setText(text);
				}
			}
		};
		private final int captionId;

		LongClickMenuItem(int captionId) {
			this.captionId = captionId;
		}

		@NotNull
		@Override
		public String getCaption(@NotNull Context context) {
			return context.getString(captionId);
		}
	}

	public static final String CREATE_FUN_EXTRA_STRING = "org.solovyev.android.calculator.math.edit.CalculatorFunctionsTabActivity_create_fun";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final Function function = (Function) parent.getItemAtPosition(position);
				if (function instanceof CustomFunction) {
					createEditVariableDialog(CalculatorFunctionsTabActivity.this,
							((CustomFunction) function),
							function.getName(),
							((CustomFunction) function).getContent(),
							((CustomFunction) function).getParameterNames(),
							null);
				}
				return true;
			}
		});*/

		/*final Intent intent = getIntent();
		if (intent != null) {
			final String varValue = intent.getStringExtra(CREATE_FUN_EXTRA_STRING);
			if (!StringUtils.isEmpty(varValue)) {
				createEditVariableDialog(this, null, null, varValue, null, null);

				// in order to stop intent for other tabs
				intent.removeExtra(CREATE_FUN_EXTRA_STRING);
			}
		}*/
	}

	@NotNull
	@Override
	protected List<AMenuItem<Function>> getMenuItemsOnLongClick(@NotNull Function item) {
		List<AMenuItem<Function>> result = new ArrayList<AMenuItem<Function>>(Arrays.asList(LongClickMenuItem.values()));
		
		if ( StringUtils.isEmpty(CalculatorEngine.instance.getFunctionsRegistry().getDescription(this, item.getName())) ) {
			result.remove(LongClickMenuItem.copy_description);
		}
		
		return result;
	}

/*	private static void createEditVariableDialog(@NotNull final AbstractMathEntityListActivity<Function> activity,
												 @Nullable final CustomFunction function,
												 @Nullable final String name,
												 @Nullable final String expression,
												 @Nullable final String[] parameterNames,
												 @Nullable final String description) {
		if (function == null || !function.isSystem()) {

			final LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View editView = layoutInflater.inflate(R.layout.var_edit, null);

			final String errorMsg = activity.getString(R.string.c_char_is_not_accepted);

			final EditText editName = (EditText) editView.findViewById(R.id.var_edit_name);
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
						if (!acceptableChars.contains(c)) {
							s.delete(i, i + 1);
							Toast.makeText(activity, String.format(errorMsg, c), Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			final EditText editValue = (EditText) editView.findViewById(R.id.var_edit_value);
			if (!StringUtils.isEmpty(expression)) {
				editValue.setText(expression);
			}

			final EditText editDescription = (EditText) editView.findViewById(R.id.var_edit_description);
			editDescription.setText(description);

			final CustomFunction.Builder functionBuilder;
			if (function != null) {
				functionBuilder = new CustomFunction.Builder(function);
			} else {
				functionBuilder = new CustomFunction.Builder();
			}

			final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
					.setCancelable(true)
					.setNegativeButton(R.string.c_cancel, null)
					.setPositiveButton(R.string.c_save, new FunctionEditorSaver(functionBuilder, function, editView, activity, CalculatorEngine.instance.getFunctionsRegistry(), new FunctionEditorSaver.EditorCreator<Function>() {

						@Override
						public void showEditor(@NotNull AbstractMathEntityListActivity<Function> activity, @Nullable CustomFunction editedInstance, @Nullable String name, @Nullable String value, @Nullable String[] parameterNames, @Nullable String description) {
							createEditVariableDialog(activity, editedInstance, name, value, parameterNames, description);
						}
					}))
					.setView(editView);

			if (function != null) {
				// EDIT mode

				builder.setTitle(R.string.c_var_edit_var);
				builder.setNeutralButton(R.string.c_remove, new MathEntityRemover<Function>(function, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						createEditVariableDialog(activity, function, name, expression, parameterNames, description);
					}
				}, CalculatorEngine.instance.getFunctionsRegistry(), activity));
			} else {
				// CREATE mode

				builder.setTitle(R.string.c_var_create_var);
			}

			builder.create().show();
		} else {
			Toast.makeText(activity, activity.getString(R.string.c_sys_var_cannot_be_changed), Toast.LENGTH_LONG).show();
		}
	}*/

	@NotNull
	@Override
	protected MathEntityDescriptionGetter getDescriptionGetter() {
		return new MathEntityDescriptionGetterImpl(CalculatorEngine.instance.getFunctionsRegistry());
	}

	@NotNull
	@Override
	protected List<Function> getMathEntities() {
		return new ArrayList<Function>(CalculatorEngine.instance.getFunctionsRegistry().getEntities());
	}

	@Override
	protected String getMathEntityCategory(@NotNull Function function) {
		return CalculatorEngine.instance.getFunctionsRegistry().getCategory(function);
	}
}
