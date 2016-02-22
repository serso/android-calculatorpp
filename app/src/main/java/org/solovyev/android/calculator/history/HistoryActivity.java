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

import static org.solovyev.android.calculator.FragmentTab.saved_history;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.FragmentTab;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.view.Tabs;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class HistoryActivity extends BaseActivity {

    public static class Dialog extends HistoryActivity {
    }

    @Nonnull
    public static Class<? extends HistoryActivity> getClass(@NonNull Context context) {
        boolean tablet = context.getResources().getBoolean(R.bool.cpp_tablet);
        return tablet ? Dialog.class : HistoryActivity.class;
    }


    @Inject
    History history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ui.withFab(R.drawable.ic_delete_white_36dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Fragment fragment = ui.getTabs().getCurrentFragment();
                showClearHistoryDialog(fragment instanceof RecentHistoryFragment);
            }
        });
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    protected void populateTabs(@Nonnull Tabs tabs) {
        super.populateTabs(tabs);
        tabs.addTab(FragmentTab.history);
        tabs.addTab(saved_history);
    }

    private void showClearHistoryDialog(final boolean recentHistory) {
        new AlertDialog.Builder(this, App.getTheme().alertDialogTheme)
                .setTitle(R.string.cpp_clear_history_title)
                .setMessage(R.string.cpp_clear_history_message)
                .setPositiveButton(R.string.cpp_clear_history, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (recentHistory) {
                            history.clearRecent();
                        } else {
                            history.clearSaved();
                        }
                    }
                })
                .setNegativeButton(R.string.c_cancel, null)
                .create()
                .show();
    }

}
