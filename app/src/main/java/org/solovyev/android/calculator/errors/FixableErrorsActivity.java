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

package org.solovyev.android.calculator.errors;

import static org.solovyev.android.calculator.App.cast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.solovyev.android.Activities;
import org.solovyev.android.calculator.PreferredPreferences;
import org.solovyev.common.msg.Message;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class FixableErrorsActivity extends AppCompatActivity {

    public static final String EXTRA_ERRORS = "errors";
    public static final String STATE_ERRORS = "errors";
    @Inject
    SharedPreferences preferences;
    @Inject
    PreferredPreferences preferredPreferences;
    private ArrayList<FixableError> errors;

    public static void show(@Nonnull Context context, @Nonnull List<Message> messages) {
        final ArrayList<FixableError> errors = new ArrayList<>();
        for (Message message : messages) {
            errors.add(new FixableError(message));
        }
        show(context, errors);
    }

    public static void show(@Nonnull Context context, @Nonnull ArrayList<FixableError> errors) {
        final Intent intent = new Intent(context, FixableErrorsActivity.class);
        intent.putExtra(EXTRA_ERRORS, errors);
        Activities.addIntentFlags(intent, false, context);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        if (state != null) {
            errors = state.getParcelableArrayList(STATE_ERRORS);
        } else {
            final Intent intent = getIntent();
            errors = intent.getParcelableArrayListExtra(EXTRA_ERRORS);
        }

        if (errors == null) {
            finish();
            return;
        }
        cast(getApplication()).getComponent().inject(this);
        if (state == null) {
            showNextError();
        }
    }

    @Override
    protected void onSaveInstanceState(@Nonnull Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelableArrayList(STATE_ERRORS, errors);
    }

    public void showNextError() {
        if (errors.isEmpty()) {
            finish();
            return;
        }
        if (!preferredPreferences.isShowWarningDialog()) {
            finish();
            return;
        }
        final FixableError fixableError = errors.remove(0);
        FixableErrorFragment.show(fixableError, getSupportFragmentManager());
    }

    public void onDialogClosed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FixableErrorFragment.FRAGMENT_TAG);
        if (fragment == null) {
            // activity is closing
            return;
        }
        showNextError();
    }
}

