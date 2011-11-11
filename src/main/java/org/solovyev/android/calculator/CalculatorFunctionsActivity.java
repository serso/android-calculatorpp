/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import jscl.math.function.Function;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.Var;
import org.solovyev.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 10/29/11
 * Time: 4:55 PM
 */
public class CalculatorFunctionsActivity extends ListActivity {

	@NotNull
	private FunctionsArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.functions);

		adapter = new FunctionsArrayAdapter(this, R.layout.var, R.id.var_text, new ArrayList<Function>(CalculatorEngine.instance.getFunctionsRegistry().getEntities()));
		setListAdapter(adapter);

		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent,
									final View view,
									final int position,
									final long id) {

				CalculatorModel.instance.processDigitButtonAction(((Function) parent.getItemAtPosition(position)).getName(), false);

				CalculatorFunctionsActivity.this.finish();
			}
		});

		sort();

	}

	private void sort() {
		CalculatorFunctionsActivity.this.adapter.sort(new Comparator<Function>() {
			@Override
			public int compare(Function function1, Function function2) {
				return function1.getName().compareTo(function2.getName());
			}
		});

		CalculatorFunctionsActivity.this.adapter.notifyDataSetChanged();
	}

	private class FunctionsArrayAdapter extends ArrayAdapter<Function> {

		private FunctionsArrayAdapter(Context context, int resource, int textViewResourceId, List<Function> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewGroup result = (ViewGroup) super.getView(position, convertView, parent);

			final Function function = getItem(position);

			final String functionDescription = CalculatorEngine.instance.getFunctionsRegistry().getDescription(getContext(), function.getName());
			if (!StringUtils.isEmpty(functionDescription)) {
				TextView description = (TextView) result.findViewById(R.id.var_description);
				if (description == null) {
					final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					final ViewGroup itemView = (ViewGroup) layoutInflater.inflate(R.layout.var, null);
					description = (TextView) itemView.findViewById(R.id.var_description);
					itemView.removeView(description);
					result.addView(description);
				}
				description.setText(functionDescription);
			} else {
				TextView description = (TextView) result.findViewById(R.id.var_description);
				if (description != null) {
					result.removeView(description);
				}
			}


			return result;
		}
	}


}
