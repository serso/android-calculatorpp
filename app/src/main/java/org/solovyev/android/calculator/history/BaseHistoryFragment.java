/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.*;
import android.widget.*;
import com.melnykov.fab.FloatingActionButton;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.menu.*;
import org.solovyev.android.plotter.Check;
import org.solovyev.common.JPredicate;
import org.solovyev.common.collections.Collections;
import org.solovyev.common.equals.Equalizer;
import org.solovyev.common.filter.Filter;
import org.solovyev.common.filter.FilterRulesChain;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static android.view.Menu.NONE;
import static org.solovyev.android.calculator.CalculatorEventType.clear_history_requested;

public abstract class BaseHistoryFragment extends ListFragment implements CalculatorEventListener {

    public static final Comparator<HistoryState> COMPARATOR = new Comparator<HistoryState>() {
        @Override
        public int compare(HistoryState state1, HistoryState state2) {
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
    @Nonnull
    private static final String TAG = "CalculatorHistoryFragment";

    private final ActivityMenu<Menu, MenuItem> menu = ListActivityMenu.fromResource(R.menu.history_menu, HistoryMenu.class, AndroidMenuHelper.getInstance(), new HistoryMenuFilter());
    @Nonnull
    private final SharedPreferences.OnSharedPreferenceChangeListener preferencesListener = new HistoryOnPreferenceChangeListener();
    @Nonnull
    private final DialogInterface.OnClickListener clearDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    clearHistory();
                    break;
            }
        }
    };
    @Nonnull
    private HistoryArrayAdapter adapter;
    @Nonnull
    private FragmentUi ui;
    @Nullable
    private AlertDialog clearDialog;

    protected BaseHistoryFragment(@Nonnull CalculatorFragmentType fragmentType) {
        ui = new FragmentUi(fragmentType.getDefaultLayoutId(), fragmentType.getDefaultTitleResId(), false);
    }

    public static boolean isAlreadySaved(@Nonnull HistoryState historyState) {
        Check.isTrue(!historyState.isSaved());

        boolean result = false;
        try {
            historyState.setSaved(true);
            if (Collections.contains(historyState, Locator.getInstance().getHistory().getSavedHistory(), new Equalizer<HistoryState>() {
                @Override
                public boolean areEqual(@Nullable HistoryState first, @Nullable HistoryState second) {
                    return first != null && second != null &&
                            first.getTime() == second.getTime() &&
                            first.getDisplayState().equals(second.getDisplayState()) &&
                            first.getEditorState().equals(second.getEditorState());
                }
            })) {
                result = true;
            }
        } finally {
            historyState.setSaved(false);
        }
        return result;
    }

    @Nonnull
    public static String getHistoryText(@Nonnull HistoryState state) {
        final StringBuilder result = new StringBuilder();
        result.append(state.getEditorState().getText());
        result.append(getIdentitySign(state.getDisplayState().getJsclOperation()));
        final String expressionResult = state.getDisplayState().getEditorState().getText();
        if (expressionResult != null) {
            result.append(expressionResult);
        }
        return result.toString();
    }

    @Nonnull
    private static String getIdentitySign(@Nonnull JsclOperation jsclOperation) {
        return jsclOperation == JsclOperation.simplify ? "≡" : "=";
    }

    public void useState(@Nonnull final HistoryState state) {
        App.getEditor().setState(state.getEditorState());
        final FragmentActivity activity = getActivity();
        if (!(activity instanceof CalculatorActivity)) {
            activity.finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ui.onCreate(this);

        setHasOptionsMenu(true);

        logDebug("onCreate");
    }

    private int logDebug(@Nonnull String msg) {
        return Log.d(TAG + ": " + getTag(), msg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return ui.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final Boolean showDatetime = Preferences.History.showDatetime.getPreference(preferences);

        ui.onViewCreated(this, root);

        adapter = new HistoryArrayAdapter(this.getActivity(), getItemLayoutId(), org.solovyev.android.calculator.R.id.history_item, new ArrayList<HistoryState>(), showDatetime);
        setListAdapter(adapter);

        final ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.attachToListView(lv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locator.getInstance().getCalculator().fireCalculatorEvent(clear_history_requested, null);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent,
                                    final View view,
                                    final int position,
                                    final long id) {
                useState((HistoryState) parent.getItemAtPosition(position));
            }
        });

        registerForContextMenu(lv);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.ui.onResume(this);

        updateAdapter();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(preferencesListener);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final HistoryState state = (HistoryState) getListView().getItemAtPosition(info.position);

        if (state.isSaved()) {
            menu.add(NONE, R.string.c_use, NONE, R.string.c_use);
            menu.add(NONE, R.string.c_copy_expression, NONE, R.string.c_copy_expression);
            if (shouldHaveCopyResult(state)) {
                menu.add(NONE, R.string.c_copy_result, NONE, R.string.c_copy_result);
            }
            menu.add(NONE, R.string.c_edit, NONE, R.string.c_edit);
            menu.add(NONE, R.string.c_remove, NONE, R.string.c_remove);
        } else {
            menu.add(NONE, R.string.c_use, NONE, R.string.c_use);
            menu.add(NONE, R.string.c_copy_expression, NONE, R.string.c_copy_expression);
            if (shouldHaveCopyResult(state)) {
                menu.add(NONE, R.string.c_copy_result, NONE, R.string.c_copy_result);
            }
            if (!isAlreadySaved(state)) {
                menu.add(NONE, R.string.c_save, NONE, R.string.c_save);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final Context context = getActivity();
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final HistoryState state = (HistoryState) getListView().getItemAtPosition(info.position);

        switch (item.getItemId()) {
            case R.string.c_use:
                useState(state);
                return true;
            case R.string.c_copy_expression:
                final String editorText = state.getEditorState().getText();
                if (!Strings.isEmpty(editorText)) {
                    final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
                    clipboard.setText(editorText);
                    Toast.makeText(context, context.getText(R.string.c_expression_copied), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.string.c_copy_result:
                final String displayText = state.getDisplayState().getEditorState().getText();
                if (!Strings.isEmpty(displayText)) {
                    final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
                    clipboard.setText(displayText);
                    Toast.makeText(context, context.getText(R.string.c_result_copied), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.string.c_save:
                if (!state.isSaved()) {
                    createEditHistoryDialog(state, context, true);
                } else {
                    Toast.makeText(context, context.getText(R.string.c_history_already_saved), Toast.LENGTH_LONG).show();
                }
                return true;
            case R.string.c_edit:
                if (state.isSaved()) {
                    createEditHistoryDialog(state, context, false);
                } else {
                    Toast.makeText(context, context.getText(R.string.c_history_must_be_saved), Toast.LENGTH_LONG).show();
                }
                return true;
            case R.string.c_remove:
                if (state.isSaved()) {
                    getAdapter().remove(state);
                    Locator.getInstance().getHistory().removeSavedHistory(state);
                    Toast.makeText(context, context.getText(R.string.c_history_was_removed), Toast.LENGTH_LONG).show();
                    getAdapter().notifyDataSetChanged();
                }
                return true;

        }
        return super.onContextItemSelected(item);
    }

    private void createEditHistoryDialog(@Nonnull final HistoryState state, @Nonnull final Context context, final boolean save) {
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View editView = layoutInflater.inflate(R.layout.history_edit, null);
        final TextView historyExpression = (TextView) editView.findViewById(R.id.history_edit_expression);
        historyExpression.setText(BaseHistoryFragment.getHistoryText(state));

        final EditText comment = (EditText) editView.findViewById(R.id.history_edit_comment);
        comment.setText(state.getComment());

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(save ? R.string.c_save_history : R.string.c_edit_history)
                .setCancelable(true)
                .setNegativeButton(R.string.c_cancel, null)
                .setPositiveButton(R.string.c_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (save) {
                            final HistoryState savedHistoryItem = Locator.getInstance().getHistory().addSavedState(state);
                            savedHistoryItem.setComment(comment.getText().toString());
                            Locator.getInstance().getHistory().save();
                            // we don't need to add element to the adapter as adapter of another activity must be updated and not this
                            //data.getAdapter().add(savedHistoryItem);
                        } else {
                            state.setComment(comment.getText().toString());
                            Locator.getInstance().getHistory().save();
                        }
                        getAdapter().notifyDataSetChanged();
                        Toast.makeText(context, context.getText(R.string.c_history_saved), Toast.LENGTH_LONG).show();
                    }
                })
                .setView(editView);

        builder.create().show();
    }

    private boolean shouldHaveCopyResult(@Nonnull HistoryState state) {
        return !state.getDisplayState().isValid() || !Strings.isEmpty(state.getDisplayState().getEditorState().getText());
    }

    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(preferencesListener);

        this.ui.onPause(this);

        super.onPause();
    }

    @Override
    public void onDestroyView() {
        ui.onDestroyView(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        logDebug("onDestroy");
        if (clearDialog != null) {
            clearDialog.dismiss();
            clearDialog = null;
        }
        ui.onDestroy(this);

        super.onDestroy();
    }

    protected abstract int getItemLayoutId();

    private void updateAdapter() {
        final List<HistoryState> historyList = getHistoryList();

        final ArrayAdapter<HistoryState> adapter = getAdapter();
        try {
            adapter.setNotifyOnChange(false);
            adapter.clear();
            for (HistoryState historyState : historyList) {
                adapter.add(historyState);
            }
        } finally {
            adapter.setNotifyOnChange(true);
        }

        adapter.notifyDataSetChanged();
    }

    @Nonnull
    private List<HistoryState> getHistoryList() {
        final List<HistoryState> historyStates = getHistoryItems();

        java.util.Collections.sort(historyStates, COMPARATOR);

        final FilterRulesChain<HistoryState> filterRulesChain = new FilterRulesChain<>();
        filterRulesChain.addFilterRule(new JPredicate<HistoryState>() {
            @Override
            public boolean apply(HistoryState object) {
                return object == null || Strings.isEmpty(object.getEditorState().getText());
            }
        });

        new Filter<>(filterRulesChain).filter(historyStates.iterator());

        return historyStates;
    }

    @Nonnull
    protected abstract List<HistoryState> getHistoryItems();

    protected abstract void clearHistory();

    @Nonnull
    protected HistoryArrayAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
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
                        clearDialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.cpp_clear_history_title)
                                .setMessage(R.string.cpp_clear_history_message)
                                .setPositiveButton(R.string.cpp_clear_history, clearDialogListener)
                                .setNegativeButton(R.string.c_cancel, clearDialogListener)
                                .create();
                        clearDialog.show();
                    }
                });
                break;
        }

    }

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

        toggle_datetime(R.id.menu_history_toggle_datetime) {
            @Override
            public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
                final SharedPreferences preferences = App.getPreferences();
                final Boolean showDatetime = Preferences.History.showDatetime.getPreference(preferences);
                Preferences.History.showDatetime.putPreference(preferences, !showDatetime);
            }
        },

        fullscreen(R.id.menu_history_fullscreen) {
            @Override
            public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
                context.startActivity(new Intent(context, CalculatorHistoryActivity.class));
            }
        };

        private final int itemId;

        HistoryMenu(int itemId) {
            this.itemId = itemId;
        }

        @Nonnull
        @Override
        public Integer getItemId() {
            return this.itemId;
        }
    }

    private class HistoryMenuFilter implements JPredicate<AMenuItem<MenuItem>> {

        @Override
        public boolean apply(@Nullable AMenuItem<MenuItem> menuItem) {
            boolean result = false;

            if (menuItem instanceof IdentifiableMenuItem<?>) {
                switch (((IdentifiableMenuItem) menuItem).getItemId()) {
                    case R.id.menu_history_fullscreen:
                        result = !ui.isPane(BaseHistoryFragment.this);
                        break;
                }
            }

            return result;
        }
    }

    private final class HistoryOnPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
            if (Preferences.History.showDatetime.isSameKey(key)) {
                getAdapter().setShowDatetime(Preferences.History.showDatetime.getPreference(preferences));
            }
        }
    }
}
