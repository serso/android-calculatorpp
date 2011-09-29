/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
public class CalculatorVarsActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTheme(android.R.style.Theme_Dialog);

		final List<Var> vars = new ArrayList<Var>(CalculatorModel.getInstance().getVarsRegister().getVars());
		setListAdapter(new VarsArrayAdapter(this, R.layout.var, R.id.var_text, vars));

		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				final Intent intent = new Intent(CalculatorActivity.INSERT_TEXT_INTENT);
				intent.putExtra(CalculatorActivity.INSERT_TEXT_INTENT_EXTRA_STRING, vars.get(position).getName());
				sendOrderedBroadcast(intent, null);

				CalculatorVarsActivity.this.finish();
			}
		});

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

			final Var var = getItem(position);

			if (!StringUtils.isEmpty(var.getDescription())) {
				final TextView description = new TextView(getContext());
				description.setText(var.getDescription());
				result.addView(description, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			}

			return result;
		}
	}
}
