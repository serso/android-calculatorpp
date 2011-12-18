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
import android.view.*;
import android.widget.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.history.*;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.view.AMenu;
import org.solovyev.android.view.AMenuItem;
import org.solovyev.android.view.MenuImpl;
import org.solovyev.android.view.prefs.ResourceCache;
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

	private static final Comparator<CalculatorHistoryState> COMPARATOR = new Comparator<CalculatorHistoryState>() {
		@Override
		public int compare(CalculatorHistoryState state1, CalculatorHistoryState state2) {
			if (state1.isSaved() == state2.isSaved()) {
				return state2.getTime().compareTo(state1.getTime());
			} else if (state1.isSaved()) {
				return -1;
			} else if (state2.isSaved()) {
				return 1;
			}
			return 0;
		}
	};


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

				useHistoryItem((CalculatorHistoryState) parent.getItemAtPosition(position), CalculatorHistoryActivity.this);
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				final CalculatorHistoryState historyState = (CalculatorHistoryState) parent.getItemAtPosition(position);

				final Context context = CalculatorHistoryActivity.this;

				final HistoryItemMenuData data = new HistoryItemMenuData(historyState, adapter);

				final List<HistoryItemMenuItem> menuItems = CollectionsUtils.asList(HistoryItemMenuItem.values());

				if (historyState.isSaved()) {
					menuItems.remove(HistoryItemMenuItem.save);
				} else {
					if (isAlreadySaved(historyState)) {
						menuItems.remove(HistoryItemMenuItem.save);
					}
					menuItems.remove(HistoryItemMenuItem.remove);
					menuItems.remove(HistoryItemMenuItem.edit);
				}

				if (historyState.getDisplayState().isValid() && StringUtils.isEmpty(historyState.getDisplayState().getEditorState().getText())) {
					menuItems.remove(HistoryItemMenuItem.copy_result);
				}
				final AMenu<HistoryItemMenuItem> historyItemMenu = new MenuImpl<HistoryItemMenuItem>(menuItems);

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setItems(historyItemMenu.getMenuCaptions(), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						final AMenuItem<HistoryItemMenuData> historyItemMenuItem = historyItemMenu.itemAt(item);
						if (historyItemMenuItem != null) {
							historyItemMenuItem.doAction(data, context);
						}
					}
				});
				builder.create().show();
				return true;
			}
		});
	}

	public static boolean isAlreadySaved(@NotNull CalculatorHistoryState historyState) {
		assert !historyState.isSaved();

		boolean result = false;
		try {
			historyState.setSaved(true);
			if ( CollectionsUtils.contains(historyState, CalculatorHistory.instance.getSavedHistory(), new Equalizer<CalculatorHistoryState>() {
				@Override
				public boolean equals(@Nullable CalculatorHistoryState first, @Nullable CalculatorHistoryState second) {
					return first != null && second != null &&
							first.getTime().getTime() == second.getTime().getTime() &&
								first.getDisplayState().equals(second.getDisplayState()) &&
									first.getEditorState().equals(second.getEditorState());
				}
			}) ) {
				result = true;
			}
		} finally {
			historyState.setSaved(false);
		}
		return result;
	}

	public static void useHistoryItem(@NotNull final CalculatorHistoryState historyState, @NotNull CalculatorHistoryActivity activity) {

		CalculatorModel.instance.doTextOperation(new CalculatorModel.TextOperation() {
			@Override
			public void doOperation(@NotNull EditText editor) {
				final EditorHistoryState editorState = historyState.getEditorState();
				editor.setText(editorState.getText());
				editor.setSelection(editorState.getCursorPosition());
			}
		}, false, historyState.getDisplayState().getJsclOperation(), true);

		CalculatorModel.instance.setCursorOnEnd();

		activity.finish();
	}

	private static List<CalculatorHistoryState> getHistoryList() {
		final List<CalculatorHistoryState> calculatorHistoryStates = new ArrayList<CalculatorHistoryState>(CalculatorHistory.instance.getStates());
		calculatorHistoryStates.addAll(CalculatorHistory.instance.getSavedHistory());

		Collections.sort(calculatorHistoryStates, COMPARATOR);

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

	public class HistoryArrayAdapter extends ArrayAdapter<CalculatorHistoryState> {

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
			editor.setText(getHistoryText(state));

			final TextView commentView = (TextView) result.findViewById(R.id.history_item_comment);
			final String comment = state.getComment();
			if (!StringUtils.isEmpty(comment)) {
				commentView.setText(comment);
			} else {
				commentView.setText("");
			}

			final TextView status = (TextView) result.findViewById(R.id.history_item_status);
			if (state.isSaved()) {
				status.setText(ResourceCache.instance.getCaption("c_history_item_saved"));
			} else {
				if ( isAlreadySaved(state) ) {
					status.setText(ResourceCache.instance.getCaption("c_history_item_already_saved"));
				} else {
					status.setText(ResourceCache.instance.getCaption("c_history_item_not_saved"));
				}
			}

			return result;
		}

		@Override
		public void notifyDataSetChanged() {
			this.setNotifyOnChange(false);
			this.sort(COMPARATOR);
			this.setNotifyOnChange(true);
			super.notifyDataSetChanged();
		}
	}

	@NotNull
	public static String getHistoryText(@NotNull CalculatorHistoryState state) {
		final StringBuilder result = new StringBuilder();
		result.append(state.getEditorState().getText());
		result.append(getIdentitySign(state.getDisplayState().getJsclOperation()));
		final String expressionResult = state.getDisplayState().getEditorState().getText();
		if (expressionResult != null) {
			result.append(expressionResult);
		}
		return result.toString();
	}

	@NotNull
	private static String getIdentitySign(@NotNull JsclOperation jsclOperation) {
		return jsclOperation == JsclOperation.simplify ? "≡" : "=";
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
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
		final List<CalculatorHistoryState> historyStates = new ArrayList<CalculatorHistoryState>(CalculatorHistory.instance.getStates());
		CalculatorHistory.instance.clear();
		for (CalculatorHistoryState historyState : historyStates) {
			adapter.remove(historyState);
		}

		if (adapter.getCount() > 0) {
			adapter.notifyDataSetChanged();
		} else {
			Toast.makeText(this, R.string.c_history_is_empty, Toast.LENGTH_SHORT).show();
			this.finish();
		}
	}
}
