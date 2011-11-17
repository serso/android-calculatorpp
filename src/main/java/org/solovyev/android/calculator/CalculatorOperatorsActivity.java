package org.solovyev.android.calculator;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import jscl.math.function.Function;
import jscl.math.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 1:53 PM
 */

public class CalculatorOperatorsActivity extends ListActivity {

	@NotNull
	private OperatorsArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.operators);

		adapter = new OperatorsArrayAdapter(this, R.layout.var, R.id.var_text, new ArrayList<Operator>(CalculatorEngine.instance.getOperatorsRegistry().getEntities()));
		setListAdapter(adapter);

		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent,
									final View view,
									final int position,
									final long id) {

				CalculatorModel.instance.processDigitButtonAction(((Operator) parent.getItemAtPosition(position)).getName(), false);

				CalculatorOperatorsActivity.this.finish();
			}
		});

		sort();

	}

	private void sort() {
		CalculatorOperatorsActivity.this.adapter.sort(new Comparator<Operator>() {
			@Override
			public int compare(Operator operator1, Operator operator2) {
				return operator1.getName().compareTo(operator2.getName());
			}
		});

		CalculatorOperatorsActivity.this.adapter.notifyDataSetChanged();
	}

	private class OperatorsArrayAdapter extends ArrayAdapter<Operator> {

		private OperatorsArrayAdapter(Context context, int resource, int textViewResourceId, List<Operator> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewGroup result = (ViewGroup) super.getView(position, convertView, parent);

			final Operator operator = getItem(position);

			final String operatorDescription = CalculatorEngine.instance.getOperatorsRegistry().getDescription(getContext(), operator.getName());
			if (!StringUtils.isEmpty(operatorDescription)) {
				TextView description = (TextView) result.findViewById(R.id.var_description);
				if (description == null) {
					final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					final ViewGroup itemView = (ViewGroup) layoutInflater.inflate(R.layout.var, null);
					description = (TextView) itemView.findViewById(R.id.var_description);
					itemView.removeView(description);
					result.addView(description);
				}
				description.setText(operatorDescription);
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

