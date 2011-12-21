/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import jscl.text.Identifier;
import jscl.text.MutableInt;
import jscl.text.ParseException;
import jscl.text.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.AndroidVarsRegistry;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.Var;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;
import org.solovyev.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 9/28/11
 * Time: 10:55 PM
 */
public class CalculatorVarsTabActivity extends AbstractMathEntityListActivity<Var> {

	public static final String CREATE_VAR_EXTRA_STRING = "org.solovyev.android.calculator.CalculatorVarsActivity_create_var";

	private final static List<Character> acceptableChars = Arrays.asList(StringUtils.toObject("1234567890abcdefghijklmnopqrstuvwxyzйцукенгшщзхъфывапролджэячсмитьбюё_".toCharArray()));

    @Override
    protected int getLayoutId() {
        return R.layout.vars;
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Var var = (Var) parent.getItemAtPosition(position);
                createEditVariableDialog(var, var.getName(), StringUtils.getNotEmpty(var.getValue(), ""), var.getDescription());
                return true;
            }
        });

		final Intent intent = getIntent();
		if ( intent != null ) {
			final String varValue = intent.getStringExtra(CREATE_VAR_EXTRA_STRING);
			if (!StringUtils.isEmpty(varValue)) {
				createEditVariableDialog(null, null, varValue, null);

                // in order to stop intent for other tabs
                intent.removeExtra(CREATE_VAR_EXTRA_STRING);
			}
		}
	}

    @NotNull
    @Override
    protected MathEntityDescriptionGetter getDescriptionGetter() {
        return new MathEntityDescriptionGetterImpl(CalculatorEngine.instance.getVarsRegister());
    }

    @SuppressWarnings({"UnusedDeclaration"})
	public void addVarButtonClickHandler(@NotNull View v) {
		createEditVariableDialog(null, null, null, null);
	}

    @NotNull
    @Override
    protected List<Var> getMathEntities() {
        final List<Var> result = new ArrayList<Var>(CalculatorEngine.instance.getVarsRegister().getEntities());

        CollectionsUtils.removeAll(result, new Finder<Var>() {
            @Override
            public boolean isFound(@Nullable Var var) {
                return var != null && CollectionsUtils.contains(var.getName(), MathType.INFINITY_JSCL, MathType.NAN);
            }
        });

        return result;
    }

    @Override
    protected String getMathEntityCategory(@NotNull Var var) {
        return CalculatorEngine.instance.getVarsRegister().getCategory(var);
    }

    private void createEditVariableDialog(@Nullable final Var var, @Nullable final String name, @Nullable final String value, @Nullable final String description) {
		if (var == null || !var.isSystem()) {

			final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			final View editView = layoutInflater.inflate(R.layout.var_edit, null);

			final String errorMsg = CalculatorVarsTabActivity.this.getString(R.string.c_char_is_not_accepted);

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
					for ( int i = 0; i < s.length(); i++ ) {
						char c = s.charAt(i);
						if (!acceptableChars.contains(c)) {
							s.delete(i, i+1);
							Toast.makeText(CalculatorVarsTabActivity.this, String.format(errorMsg, c), Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			final EditText editValue = (EditText) editView.findViewById(R.id.var_edit_value);
			if (!StringUtils.isEmpty(value)) {
				editValue.setText(value);
			}

			final EditText editDescription = (EditText) editView.findViewById(R.id.var_edit_description);
			editDescription.setText(description);

			final Var.Builder varBuilder;
			if (var != null) {
				varBuilder = new Var.Builder(var);
			} else {
				varBuilder = new Var.Builder();
			}

			final AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setCancelable(true)
					.setNegativeButton(R.string.c_cancel, null)
					.setPositiveButton(R.string.c_save, new VarEditorSaver(varBuilder, var, editView))
					.setView(editView);

			if ( var != null ) {
				// EDIT mode

				builder.setTitle(R.string.c_var_edit_var);
				builder.setNeutralButton(R.string.c_remove, new VarEditorRemover(var, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						createEditVariableDialog(var, name, value, description);
					}
				}));
			} else {
				// CREATE mode

				builder.setTitle(R.string.c_var_create_var);
			}

			builder.create().show();
		} else {
			Toast.makeText(this, getString(R.string.c_sys_var_cannot_be_changed), Toast.LENGTH_LONG).show();
		}
	}

	private class VarEditorSaver implements DialogInterface.OnClickListener {

		@NotNull
		private Var.Builder varBuilder;

		@Nullable
		private final Var editedInstance;

		@NotNull
		private View editView;

		public VarEditorSaver(@NotNull Var.Builder varBuilder, @Nullable Var editedInstance, @NotNull View editView) {
			this.varBuilder = varBuilder;
			this.editedInstance = editedInstance;
			this.editView = editView;
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


				final AndroidVarsRegistry varsRegistry = CalculatorEngine.instance.getVarsRegister();
				if (isValidName(name)) {

					boolean canBeSaved = false;

					final Var varFromRegister = varsRegistry.get(name);
					if ( varFromRegister == null ) {
						canBeSaved = true;
					} else if ( editedInstance != null && varFromRegister.getId().equals(editedInstance.getId()) ) {
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
								boolean valid = isValidValue(value);

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
					Toast.makeText(CalculatorVarsTabActivity.this, getString(error), Toast.LENGTH_LONG).show();
					createEditVariableDialog(editedInstance, name, value, description);
				} else {
                    final Var addedVar = varsRegistry.add(varBuilder);
	                if (isInCategory(addedVar)) {
                        if ( editedInstance != null ) {
                            CalculatorVarsTabActivity.this.getAdapter().remove(editedInstance);
                        }
                        CalculatorVarsTabActivity.this.getAdapter().add(addedVar);
                    }

					varsRegistry.save(CalculatorVarsTabActivity.this);

                    if (isInCategory(addedVar)) {
                        sort();
                    }
                }
			}
		}
	}

	private static boolean isValidName(@Nullable String name) {
		boolean result = false;

		if (!StringUtils.isEmpty(name)) {
			try {
				Identifier.parser.parse(Parser.Parameters.newInstance(name, new MutableInt(0), CalculatorEngine.instance.getEngine()), null);
				result = true;
			} catch (ParseException e) {
				// not valid name;
			}
		}

		return result;
	}

	public static boolean isValidValue(@NotNull String value) {
		// now every string might be constant
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.var_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;

		switch (item.getItemId()) {
			case R.id.var_menu_add_var:
				createEditVariableDialog(null, null, null, null);
				result = true;
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	private class VarEditorRemover implements DialogInterface.OnClickListener {

		@NotNull
		private final Var var;

		@Nullable
		private final DialogInterface.OnClickListener callbackOnCancel;

		private final boolean confirmed;

		public VarEditorRemover(@NotNull Var var, @Nullable DialogInterface.OnClickListener callbackOnCancel) {
			this(var, callbackOnCancel, false);
		}

		public VarEditorRemover(@NotNull Var var, @Nullable DialogInterface.OnClickListener callbackOnCancel, boolean confirmed) {
			this.var = var;
			this.callbackOnCancel = callbackOnCancel;
			this.confirmed = confirmed;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (!confirmed) {
				final TextView question = new TextView(CalculatorVarsTabActivity.this);
				question.setText(String.format(getString(R.string.c_var_removal_confirmation_question), var.getName()));
				question.setPadding(6, 6, 6, 6);
				final AlertDialog.Builder builder = new AlertDialog.Builder(CalculatorVarsTabActivity.this)
						.setCancelable(true)
						.setView(question)
						.setTitle(R.string.c_var_removal_confirmation)
						.setNegativeButton(R.string.c_no, callbackOnCancel)
						.setPositiveButton(R.string.c_yes, new VarEditorRemover(var, callbackOnCancel, true));

				builder.create().show();
			} else {
                if (isInCategory(var)) {
                    getAdapter().remove(var);
                }
                final AndroidVarsRegistry varsRegistry = CalculatorEngine.instance.getVarsRegister();
				varsRegistry.remove(var);
				varsRegistry.save(CalculatorVarsTabActivity.this);
                if (isInCategory(var)) {
                    CalculatorVarsTabActivity.this.getAdapter().notifyDataSetChanged();
                }
            }
		}
	}
}
