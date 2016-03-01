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

package org.solovyev.android.calculator.functions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.FragmentTab;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.operators.OperatorCategory;
import org.solovyev.android.calculator.view.Tabs;

import javax.annotation.Nonnull;

public class FunctionsActivity extends BaseActivity {

    public static final String EXTRA_FUNCTION = "function";

    public FunctionsActivity() {
        super(R.string.c_functions);
    }

    @Nonnull
    public static Class<? extends FunctionsActivity> getClass(@NonNull Context context) {
        return App.isTablet(context) ? Dialog.class : FunctionsActivity.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            final CppFunction function = extras != null ? (CppFunction) extras.getParcelable(EXTRA_FUNCTION) : null;
            if (function != null) {
                EditFunctionFragment.show(function, this);
            }
        }

        withFab(R.drawable.ic_add_white_36dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditFunctionFragment.show(FunctionsActivity.this);
            }
        });
    }

    @Override
    protected void populateTabs(@Nonnull Tabs tabs) {
        super.populateTabs(tabs);

        for (FunctionCategory category : FunctionCategory.values()) {
            tabs.addTab(category, FragmentTab.functions);
        }

        for (OperatorCategory category : OperatorCategory.values()) {
            final String title;
            if (category == OperatorCategory.common || category == OperatorCategory.other) {
                title = getString(R.string.c_operators) + ": " + getString(category.title());
            } else {
                title = getString(category.title());
            }
            tabs.addTab(category, FragmentTab.operators, title);
        }
    }

    public static final class Dialog extends FunctionsActivity {
    }
}