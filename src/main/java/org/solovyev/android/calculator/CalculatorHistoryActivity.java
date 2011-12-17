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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.history.EditorHistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.view.prefs.ResourceCache;
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

	@NotNull
	private HistoryArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.history_activity);

		final List<CalculatorHistoryState> historyList = getHistoryList();
		if ( historyList.isEmpty() ) {
			Toast.makeText(this, R.string.c_history_is_empty, Toast.LENGTH_SHORT).show();
			this.finish();
		}

		adapter = new HistoryArrayAdapter(this, R.layout.history, R.id.history_item, historyList);
		setListAdapter(adapter);

		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent,
									final View view,
									final int position,
									final long id) {

				final CalculatorHistoryState historyState = (CalculatorHistoryState) parent.getItemAtPosition(position);

				CalculatorModel.instance.doTextOperation(new CalculatorModel.TextOperation() {
					@Override
					public void doOperation(@NotNull EditText editor) {
						final EditorHistoryState editorState = historyState.getEditorState();
						editor.setText(editorState.getText());
						editor.setSelection(editorState.getCursorPosition());
					}
				}, false, historyState.getDisplayState().getJsclOperation(), true);

				CalculatorModel.instance.setCursorOnEnd();

				CalculatorHistoryActivity.this.finish();
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final CalculatorHistoryState historyState = (CalculatorHistoryState) parent.getItemAtPosition(position);

				final Context context = CalculatorHistoryActivity.this;

				final CharSequence[] items = {context.getText(R.string.c_save), context.getText(R.string.c_remove)};

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							if (!historyState.isSaved()) {
								historyState.setSaved(true);
								CalculatorHistory.instance.getSavedHistory().addState(historyState);
								CalculatorHistory.instance.save(context);
								CalculatorHistoryActivity.this.adapter.notifyDataSetChanged();
								Toast.makeText(context, "History item was successfully saved!", Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(context, "History item was already saved!", Toast.LENGTH_LONG).show();
							}
						} else if (item == 1) {
							if (historyState.isSaved()) {
								historyState.setSaved(false);
								CalculatorHistory.instance.save(context);
								CalculatorHistory.instance.getSavedHistory().clear();
								CalculatorHistory.instance.load(context, PreferenceManager.getDefaultSharedPreferences(context));
								CalculatorHistoryActivity.this.adapter.notifyDataSetChanged();
								Toast.makeText(context, "History item was removed!", Toast.LENGTH_LONG).show();
							}
						}
					}
				});
				builder.create().show();
				return true;
			}
		});
	}

	private static List<CalculatorHistoryState> getHistoryList() {
		final List<CalculatorHistoryState> calculatorHistoryStates = new ArrayList<CalculatorHistoryState>(CalculatorHistory.instance.getStates());
		calculatorHistoryStates.addAll(CalculatorHistory.instance.getSavedHistory().getStates());

		Collections.sort(calculatorHistoryStates, new Comparator<CalculatorHistoryState>() {
			@Override
			public int compare(CalculatorHistoryState state1, CalculatorHistoryState state2) {
				if ( state1.isSaved() == state2.isSaved() ) {
					return state2.getTime().compareTo(state1.getTime());
				} else if ( state1.isSaved() ) {
					return -1;
				} else if ( state2.isSaved() ) {
					return 1;
				}
				return 0;
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
			final StringBuilder historyText = new StringBuilder();
			historyText.append(state.getEditorState().getText());
			historyText.append(getIdentitySign(state.getDisplayState().getJsclOperation()));
			historyText.append(state.getDisplayState().getEditorState().getText());
			final String comment = state.getComment();
			if (!StringUtils.isEmpty(comment)) {
				historyText.append("\n");
				historyText.append(ResourceCache.instance.getCaption("c_comment")).append(": ");
				historyText.append(comment);
			}
			if ( state.isSaved() ) {
				historyText.append("\n");
				historyText.append(ResourceCache.instance.getCaption("c_history_item_saved"));
			}
			editor.setText(historyText);

			return result;
		}

		@NotNull
		private String getIdentitySign(@NotNull JsclOperation jsclOperation) {
			return jsclOperation == JsclOperation.simplify ? "≡" : "=";
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
