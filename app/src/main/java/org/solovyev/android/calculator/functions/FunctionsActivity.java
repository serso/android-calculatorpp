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

import android.os.Bundle;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.FragmentTab;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.operators.OperatorCategory;

import javax.annotation.Nullable;

public class FunctionsActivity extends BaseActivity {

    public static final String EXTRA_FUNCTION = "function";

    public FunctionsActivity() {
        super(R.layout.main_empty, FunctionsActivity.class.getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (FunctionCategory category : FunctionCategory.values()) {
            addTab(category, FragmentTab.functions);
        }

        for (OperatorCategory category : OperatorCategory.values()) {
            final String title;
            if (category == OperatorCategory.common || category == OperatorCategory.other) {
                title = getString(R.string.c_operators) + ": " + getString(category.title());
            } else {
                title = getString(category.title());
            }
            addTab(category, FragmentTab.operators, title);
        }

        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            final CppFunction function = extras != null ? (CppFunction) extras.getParcelable(EXTRA_FUNCTION) : null;
            if (function != null) {
                EditFunctionFragment.show(function, this);
            }
        }
    }
}