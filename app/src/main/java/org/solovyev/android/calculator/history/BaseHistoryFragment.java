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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.text.ClipboardManager;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseHistoryFragment extends ListFragment {
    @Inject
    History history;
    @Inject
    Bus bus;
    private HistoryArrayAdapter adapter;
    @Nonnull
    private FragmentUi ui;

    protected BaseHistoryFragment(@Nonnull CalculatorFragmentType type) {
        ui = new FragmentUi(type.getDefaultLayoutId(), type.getDefaultTitleResId(), false);
    }

    @Nonnull
    public static String getHistoryText(@Nonnull HistoryState state) {
        return state.editor.getTextString() + getIdentitySign(state.display.getOperation()) + state.display.text;
    }

    @Nonnull
    private static String getIdentitySign(@Nonnull JsclOperation operation) {
        return operation == JsclOperation.simplify ? "≡" : "=";
    }

    public void useState(@Nonnull final HistoryState state) {
        App.getEditor().setState(state.editor);
        final FragmentActivity activity = getActivity();
        if (!(activity instanceof CalculatorActivity)) {
            activity.finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CalculatorApplication) getActivity().getApplication()).getComponent().inject(this);
        bus.register(this);
        ui.onCreate(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return ui.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        ui.onViewCreated(this, root);

        adapter = new HistoryArrayAdapter(this.getActivity(), getItemLayoutId(), R.id.history_item, new ArrayList<HistoryState>());
        setListAdapter(adapter);

        final ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.attachToListView(lv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearHistoryDialog();
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

    private void showClearHistoryDialog() {
        new AlertDialog.Builder(getActivity(), ui.getTheme().alertDialogTheme)
                .setTitle(R.string.cpp_clear_history_title)
                .setMessage(R.string.cpp_clear_history_message)
                .setPositiveButton(R.string.cpp_clear_history, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearHistory();
                    }
                })
                .setNegativeButton(R.string.c_cancel, null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ui.onResume(this);
        updateAdapter();
    }

    @Override
    public final void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final HistoryState state = (HistoryState) getListView().getItemAtPosition(info.position);

        onCreateContextMenu(menu, state);
    }

    protected abstract void onCreateContextMenu(@Nonnull ContextMenu menu, @Nonnull HistoryState state);

    @Override
    public final boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final HistoryState state = (HistoryState) getListView().getItemAtPosition(info.position);

        if (onContextItemSelected(item, state)) {
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected abstract boolean onContextItemSelected(@Nonnull MenuItem item, @Nonnull HistoryState state);

    @SuppressWarnings("deprecation")
    protected final void copyResult(@Nonnull HistoryState state) {
        final Context context = getActivity();
        final String displayText = state.display.text;
        if (Strings.isEmpty(displayText)) {
            return;
        }
        final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        clipboard.setText(displayText);
    }

    @SuppressWarnings("deprecation")
    protected final void copyExpression(@Nonnull HistoryState state) {
        final Context context = getActivity();
        final String editorText = state.editor.getTextString();
        if (Strings.isEmpty(editorText)) {
            return;
        }
        final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        clipboard.setText(editorText);
    }

    protected final boolean shouldHaveCopyResult(@Nonnull HistoryState state) {
        return !state.display.valid || !Strings.isEmpty(state.display.text);
    }

    @Override
    public void onPause() {
        ui.onPause(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        ui.onDestroyView(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        ui.onDestroy(this);
        super.onDestroy();
    }

    protected abstract int getItemLayoutId();

    private void updateAdapter() {
        final List<HistoryState> historyList = getHistoryItems();

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
    protected abstract List<HistoryState> getHistoryItems();

    protected abstract void clearHistory();

    @Nonnull
    protected HistoryArrayAdapter getAdapter() {
        return adapter;
    }

    @Subscribe
    void onHistoryChanged(@Nonnull History.ChangedEvent e) {
        if (e.recent != isRecentHistory()) {
            return;
        }
        updateAdapter();
    }

    protected abstract boolean isRecentHistory();
}
