/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.about.CalculatorFragmentType;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.menu.*;
import org.solovyev.android.sherlock.menu.SherlockMenuHelper;
import org.solovyev.common.collections.CollectionsUtils;
import org.solovyev.common.equals.Equalizer;
import org.solovyev.common.filter.Filter;
import org.solovyev.common.filter.FilterRule;
import org.solovyev.common.filter.FilterRulesChain;
import org.solovyev.common.text.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 10/15/11
 * Time: 1:13 PM
 */
public abstract class AbstractCalculatorHistoryFragment extends SherlockListFragment implements CalculatorEventListener {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    @NotNull
    private static final String TAG = "CalculatorHistoryFragment";

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

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */


	@NotNull
	private ArrayAdapter<CalculatorHistoryState> adapter;

    @NotNull
    private CalculatorFragmentHelper fragmentHelper;

    private ActivityMenu<Menu, MenuItem> menu = ListActivityMenu.fromResource(org.solovyev.android.calculator.R.menu.history_menu, HistoryMenu.class, SherlockMenuHelper.getInstance());

    protected AbstractCalculatorHistoryFragment(@NotNull CalculatorFragmentType fragmentType) {
        fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(fragmentType.getDefaultLayoutId(), fragmentType.getDefaultTitleResId(), false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentHelper.onCreate(this);

        setHasOptionsMenu(true);

        logDebug("onCreate");
    }

    private int logDebug(@NotNull String msg) {
        return Log.d(TAG + ": " + getTag(), msg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return fragmentHelper.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        logDebug("onViewCreated");

        fragmentHelper.onViewCreated(this, root);

        adapter = new HistoryArrayAdapter(this.getActivity(), getItemLayoutId(), org.solovyev.android.calculator.R.id.history_item, new ArrayList<CalculatorHistoryState>());
        setListAdapter(adapter);

        final ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent,
                                    final View view,
                                    final int position,
                                    final long id) {

                useHistoryItem((CalculatorHistoryState) parent.getItemAtPosition(position));
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CalculatorHistoryState historyState = (CalculatorHistoryState) parent.getItemAtPosition(position);

                final Context context = getActivity();

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
    public void onResume() {
        super.onResume();

        this.fragmentHelper.onResume(this);

        updateAdapter();
    }

    @Override
    public void onPause() {
        this.fragmentHelper.onPause(this);

        super.onPause();
    }

    @Override
	public void onDestroy() {
        logDebug("onDestroy");

        fragmentHelper.onDestroy(this);

        super.onDestroy();
	}

	protected abstract int getItemLayoutId();

    private void updateAdapter() {
        final List<CalculatorHistoryState> historyList = getHistoryList();

        final ArrayAdapter<CalculatorHistoryState> adapter = getAdapter();
        try {
            adapter.setNotifyOnChange(false);
            adapter.clear();
            for (CalculatorHistoryState historyState : historyList) {
                adapter.add(historyState);
            }
        } finally {
            adapter.setNotifyOnChange(true);
        }

        adapter.notifyDataSetChanged();
    }

    public static boolean isAlreadySaved(@NotNull CalculatorHistoryState historyState) {
		assert !historyState.isSaved();

		boolean result = false;
		try {
			historyState.setSaved(true);
			if ( CollectionsUtils.contains(historyState, CalculatorLocatorImpl.getInstance().getHistory().getSavedHistory(), new Equalizer<CalculatorHistoryState>() {
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

	public static void useHistoryItem(@NotNull final CalculatorHistoryState historyState) {
        CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_history_state, historyState);
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

	protected abstract void clearHistory();

	@NotNull
	protected ArrayAdapter<CalculatorHistoryState> getAdapter() {
		return adapter;
	}

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType) {
            case history_state_added:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logDebug("onCalculatorEvent");
                        updateAdapter();
                    }
                });
                break;

            case clear_history_requested:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearHistory();
                    }
                });
                break;
        }

    }

    /*
    **********************************************************************
    *
    *                           MENU
    *
    **********************************************************************
    */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu.onCreateOptionsMenu(this.getActivity(), menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        this.menu.onPrepareOptionsMenu(this.getActivity(), menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return this.menu.onOptionsItemSelected(this.getActivity(), item);
    }

    private static enum HistoryMenu implements IdentifiableMenuItem<MenuItem> {

        clear_history(org.solovyev.android.calculator.R.id.history_menu_clear_history) {
            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.clear_history_requested, null);
            }
        };

        private final int itemId;

        HistoryMenu(int itemId) {
            this.itemId = itemId;
        }

        @NotNull
        @Override
        public Integer getItemId() {
            return this.itemId;
        }
    }
}
