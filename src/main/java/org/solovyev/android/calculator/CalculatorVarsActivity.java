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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.solovyev.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 9/28/11
 * Time: 10:55 PM
 */
public class CalculatorVarsActivity extends ListActivity implements DialogInterface.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new VarsArrayAdapter(this, R.layout.var, R.id.var_text, new ArrayList<Var>(CalculatorModel.getInstance().getVarsRegister().getVars())));

		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);


		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				final View editView = layoutInflater.inflate(R.layout.var_edit, null);

				final Var var = (Var)parent.getItemAtPosition(position);

				final EditText editName = (EditText)editView.findViewById(R.id.var_name);
				editName.setText(var.getName());
				final EditText editValue = (EditText)editView.findViewById(R.id.var_value);
				editValue.setText(var.getValue());
				final EditText editDescription = (EditText)editView.findViewById(R.id.var_description);
				editDescription.setText(var.getDescription());


				new AlertDialog.Builder(CalculatorVarsActivity.this)
						.setCancelable(true)
						.setPositiveButton("Save", CalculatorVarsActivity.this)
						.setView(editView).create().show();
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

	@Override
	public void onClick(DialogInterface dialog, int which) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	private class VarsArrayAdapter extends ArrayAdapter<Var> {

		private VarsArrayAdapter(Context context, int resource, int textViewResourceId, List<Var> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		public VarsArrayAdapter(Context context, int resource, List<Var> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewGroup result = (ViewGroup)super.getView(position, convertView, parent);

			if (convertView == null) {
				final Var var = getItem(position);

				if (!StringUtils.isEmpty(var.getDescription())) {
					final TextView description = new TextView(getContext());
					description.setText(var.getDescription());
					description.setPadding(6, 0, 6, 6);
					result.addView(description, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				}
			}

			return result;
		}
	}
}
