/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 9/28/11
 * Time: 10:55 PM
 */
public class CalculatorVarsActivity extends ListActivity {

	@NotNull
	private VarsArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new VarsArrayAdapter(this, R.layout.var, R.id.var_text, new ArrayList<Var>(CalculatorModel.getInstance().getVarsRegister().getVars()));
		setListAdapter(adapter);

		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);


		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final Var var = (Var) parent.getItemAtPosition(position);
				createEditVariableDialog(var, var.getName(), var.getValue(), var.getDescription());
				return true;
			}
		});

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				final Intent intent = new Intent(CalculatorActivity.INSERT_TEXT_INTENT);
				intent.putExtra(CalculatorActivity.INSERT_TEXT_INTENT_EXTRA_STRING, ((Var) parent.getItemAtPosition(position)).getName());
				sendOrderedBroadcast(intent, null);

				CalculatorVarsActivity.this.finish();
			}
		});

	}

	private void createEditVariableDialog(@Nullable final Var var, @Nullable final String name, @Nullable final String value, @Nullable final String description) {
		if (var == null || !var.isSystem()) {

			final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			final View editView = layoutInflater.inflate(R.layout.var_edit, null);

			final EditText editName = (EditText) editView.findViewById(R.id.var_edit_name);
			editName.setText(name);

			final EditText editValue = (EditText) editView.findViewById(R.id.var_edit_value);
			editValue.setText(value);

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
			Toast.makeText(this, "System variable cannot be changed!", Toast.LENGTH_LONG).show();
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
				final String error;

				final EditText editName = (EditText) editView.findViewById(R.id.var_edit_name);
				String name = editName.getText().toString();

				final EditText editValue = (EditText) editView.findViewById(R.id.var_edit_value);
				String value = editValue.getText().toString();

				final EditText editDescription = (EditText) editView.findViewById(R.id.var_edit_description);
				String description = editDescription.getText().toString();


				final VarsRegister varsRegister = CalculatorModel.getInstance().getVarsRegister();
				if (!StringUtils.isEmpty(name)) {
					final Var varFromRegister = varsRegister.getVar(name);
					if (varFromRegister == null || varFromRegister == editedInstance) {

						boolean correctDouble = true;
						try {
							Double.valueOf(value);
						} catch (NumberFormatException e) {
							correctDouble = false;
						}

						if (correctDouble) {
							varBuilder.setName(name);
							varBuilder.setValue(value);
							varBuilder.setDescription(description);
							error = null;
						} else {
							error = "Value is not a number!";
						}
					} else {
						error = "Variable with same name already exist!";
					}
				} else {
					error = "Name is empty!";
				}

				if (error != null) {
					Toast.makeText(CalculatorVarsActivity.this, error, Toast.LENGTH_LONG).show();
					createEditVariableDialog(editedInstance, name, value, description);
				} else {
					if ( editedInstance == null ) {
						CalculatorVarsActivity.this.adapter.add(varsRegister.addVar(null, varBuilder));
					} else {
						varsRegister.addVar(editedInstance.getName(), varBuilder);
					}

					varsRegister.save(CalculatorVarsActivity.this);

					CalculatorVarsActivity.this.adapter.notifyDataSetChanged();
				}
			}
		}
	}

	private class VarsArrayAdapter extends ArrayAdapter<Var> {

		private VarsArrayAdapter(Context context, int resource, int textViewResourceId, List<Var> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewGroup result = (ViewGroup) super.getView(position, convertView, parent);

			final Var var = getItem(position);

			if (!StringUtils.isEmpty(var.getDescription())) {
				TextView description = (TextView) result.findViewById(R.id.var_description);
				if (description == null) {
					final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					final ViewGroup itemView = (ViewGroup) layoutInflater.inflate(R.layout.var, null);
					description = (TextView) itemView.findViewById(R.id.var_description);
					itemView.removeView(description);
					result.addView(description);
				}
				description.setText(var.getDescription());
			} else {
				TextView description = (TextView) result.findViewById(R.id.var_description);
				if (description != null) {
					result.removeView(description);
				}
			}


			return result;
		}
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
				final TextView question = new TextView(CalculatorVarsActivity.this);
				question.setText(String.format(getString(R.string.c_var_removal_confirmation_question), var.getName()));
				question.setPadding(6, 6, 6, 6);
				final AlertDialog.Builder builder = new AlertDialog.Builder(CalculatorVarsActivity.this)
						.setCancelable(true)
						.setView(question)
						.setTitle(R.string.c_var_removal_confirmation)
						.setNegativeButton(R.string.c_no, callbackOnCancel)
						.setPositiveButton(R.string.c_yes, new VarEditorRemover(var, callbackOnCancel, true));

				builder.create().show();
			} else {
				adapter.remove(var);
				final VarsRegister varsRegister = CalculatorModel.getInstance().getVarsRegister();
				varsRegister.remove(var);
				varsRegister.save(CalculatorVarsActivity.this);
				CalculatorVarsActivity.this.adapter.notifyDataSetChanged();
			}
		}
	}
}
