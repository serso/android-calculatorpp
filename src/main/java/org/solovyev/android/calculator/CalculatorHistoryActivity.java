/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.utils.Filter;
import org.solovyev.common.utils.FilterRule;
import org.solovyev.common.utils.FilterRulesChain;
import org.solovyev.common.utils.StringUtils;

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
			public void onItemClick(final AdapterView<?> parent,
									final View view,
									final int position,
									final long id) {

				CalculatorView.instance.doTextOperation(new CalculatorView.TextOperation() {
					@Override
					public void doOperation(@NotNull EditText editor) {
						final EditorHistoryState editorState = ((CalculatorHistoryState) parent.getItemAtPosition(position)).getEditorState();
						editor.setText(editorState.getText());
						editor.setSelection(editorState.getCursorPosition());
					}
				}, false);

				CalculatorView.instance.setCursorOnEnd();

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.history_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;

		switch (item.getItemId()) {
			case R.id.history_menu_clear_history:
				clearHistory();
				result = true;
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	private void clearHistory() {
		CalculatorHistory.instance.clear();
		Toast.makeText(this, R.string.c_history_is_empty, Toast.LENGTH_SHORT).show();
		this.finish();
	}
}
