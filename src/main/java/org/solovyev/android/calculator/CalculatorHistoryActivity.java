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
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.utils.*;
import org.solovyev.common.utils.Filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 10/15/11
 * Time: 1:13 PM
 */
public class CalculatorHistoryActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.history_activity);

		final List<CalculatorHistoryState> historyList = getHistoryList();
		if ( historyList.isEmpty() ) {
			Toast.makeText(this, R.string.c_history_is_empty, Toast.LENGTH_SHORT).show();
			this.finish();
		}

		setListAdapter(new HistoryArrayAdapter(this, R.layout.history, R.id.history_item, historyList));

		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				final Intent intent = new Intent(CalculatorActivity.SET_TEXT_INTENT);
				intent.putExtra(CalculatorActivity.SET_TEXT_INTENT_EXTRA_STRING, ((CalculatorHistoryState) parent.getItemAtPosition(position)).getEditorState().getText());
				sendOrderedBroadcast(intent, null);

				CalculatorHistoryActivity.this.finish();
			}
		});
	}

	private static List<CalculatorHistoryState> getHistoryList() {
		final List<CalculatorHistoryState> calculatorHistoryStates = new ArrayList<CalculatorHistoryState>(CalculatorHistory.instance.getStates());

		Collections.sort(calculatorHistoryStates, new Comparator<CalculatorHistoryState>() {
			@Override
			public int compare(CalculatorHistoryState state1, CalculatorHistoryState state2) {
				return state2.getTime().compareTo(state1.getTime());
			}
		});

		final FilterRulesChain<CalculatorHistoryState> filterRulesChain = new FilterRulesChain<CalculatorHistoryState>();
		filterRulesChain.addFilterRule(new FilterRule<CalculatorHistoryState>() {
			@Override
			public boolean isFiltered(CalculatorHistoryState object) {
				return object == null || StringUtils.isEmpty(object.getEditorState().getText());
			}
		});

		new Filter<CalculatorHistoryState>(filterRulesChain).filter(calculatorHistoryStates.iterator());

		return calculatorHistoryStates;
	}

	private static class HistoryArrayAdapter extends ArrayAdapter<CalculatorHistoryState> {

		private HistoryArrayAdapter(Context context, int resource, int textViewResourceId, @NotNull List<CalculatorHistoryState> historyList) {
			super(context, resource, textViewResourceId, historyList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewGroup result = (ViewGroup) super.getView(position, convertView, parent);

			final CalculatorHistoryState state = getItem(position);

			final TextView time = (TextView) result.findViewById(R.id.history_time);
			time.setText(new SimpleDateFormat().format(state.getTime()));

			final TextView editor = (TextView) result.findViewById(R.id.history_item);
			editor.setText(state.getEditorState().getText() + "=" + state.getDisplayState().getEditorHistoryState().getText());

			return result;
		}
	}
}
