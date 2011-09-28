/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

		final List<VariableContainer> vars = new ArrayList<VariableContainer>();
		vars.add(new VariableContainer("e", 2.71, true));
		vars.add(new VariableContainer("π", 3.14, true));
		setListAdapter(new ArrayAdapter<VariableContainer>(this, R.layout.var, R.id.var_text, vars));

		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				final Intent intent = new Intent(CalculatorActivity.INSERT_TEXT_INTENT);
				intent.putExtra(CalculatorActivity.INSERT_TEXT_INTENT_EXTRA_STRING, vars.get(position).getId());
				sendOrderedBroadcast(intent, null);

				CalculatorVarsActivity.this.finish();
			}
		});

	}
}
