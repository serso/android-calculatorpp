/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.google.ads.AdView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.CalculatorModel;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.view.AMenuBuilder;
import org.solovyev.android.view.MenuImpl;
import org.solovyev.common.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 10/15/11
 * Time: 1:13 PM
 */
public abstract class AbstractHistoryActivity extends ListActivity {

	public static final Comparator<CalculatorHistoryState> COMPARATOR = new Comparator<CalculatorHistoryState>() {
		@Override
		public int compare(CalculatorHistoryState state1, CalculatorHistoryState state2) {
			if (state1.isSaved() == state2.isSaved()) {
				long l = state2.getTime() - state1.getTime();
				return l > 0l ? 1 : (l < 0l ? -1 : 0);
			} else if (state1.isSaved()) {
				return -1;
			} else if (state2.isSaved()) {
				return 1;
			}
			return 0;
		}
	};


	@NotNull
	private ArrayAdapter<CalculatorHistoryState> adapter;

	@Nullable
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.history_activity);

		adView = CalculatorApplication.inflateAd(this);

		adapter = new HistoryArrayAdapter(this, getLayoutId(), R.id.history_item, new ArrayList<CalculatorHistoryState>());
		setListAdapter(adapter);

		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent,
									final View view,
									final int position,
									final long id) {

				useHistoryItem((CalculatorHistoryState) parent.getItemAtPosition(position), AbstractHistoryActivity.this);
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				final CalculatorHistoryState historyState = (CalculatorHistoryState) parent.getItemAtPosition(position);

				final Context context = AbstractHistoryActivity.this;

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

				final AMenuBuilder<HistoryItemMenuItem, HistoryItemMenuData> menuBuilder = AMenuBuilder.newInstance(context, MenuImpl.newInstance(menuItems));
				menuBuilder.create(data).show();

				return true;
			}
		});
	}

	@Override
	protected void onDestroy() {
		if ( this.adView != null ) {
			this.adView.destroy();
		}
		super.onDestroy();
	}

	protected abstract int getLayoutId();

	@Override
	protected void onResume() {
		super.onResume();

		final List<CalculatorHistoryState> historyList = getHistoryList();
		try {
			this.adapter.setNotifyOnChange(false);
			this.adapter.clear();
			for (CalculatorHistoryState historyState : historyList) {
				this.adapter.add(historyState);
			}
		} finally {
			this.adapter.setNotifyOnChange(true);
		}

		this.adapter.notifyDataSetChanged();
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
							first.getTime() == second.getTime() &&
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

	public static void useHistoryItem(@NotNull final CalculatorHistoryState historyState, @NotNull AbstractHistoryActivity activity) {

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

	@NotNull
	private List<CalculatorHistoryState> getHistoryList() {
		final List<CalculatorHistoryState> calculatorHistoryStates = getHistoryItems();

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

	@NotNull
	protected abstract List<CalculatorHistoryState> getHistoryItems();

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

	protected abstract void clearHistory();

	@NotNull
	protected ArrayAdapter<CalculatorHistoryState> getAdapter() {
		return adapter;
	}
}
