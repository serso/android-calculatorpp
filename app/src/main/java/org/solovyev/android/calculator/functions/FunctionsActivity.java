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
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.math.edit.FunctionsFragment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FunctionsActivity extends BaseActivity {

    public static final String EXTRA_FUNCTION = "function";
    private static final CalculatorFragmentType FRAGMENT_TYPE = CalculatorFragmentType.functions;

    public FunctionsActivity() {
        super(R.layout.main_empty, FunctionsActivity.class.getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (FunctionCategory category : FunctionCategory.values()) {
            addTab(category);
        }

        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            final Parcelable function = extras != null ? extras.getParcelable(EXTRA_FUNCTION) : null;
            if (function instanceof CppFunction) {
                EditFunctionFragment.showDialog((CppFunction) function, getSupportFragmentManager());
            }
        }
    }

    private void addTab(@Nonnull FunctionCategory category) {
        final Bundle arguments = new Bundle(1);
        arguments.putString(FunctionsFragment.ARG_CATEGORY, category.name());
        final String fragmentTag = FRAGMENT_TYPE.createSubFragmentTag(category.name());
        final Class<? extends Fragment> fragmentClass = FRAGMENT_TYPE.getFragmentClass();
        ui.addTab(this, fragmentTag, fragmentClass, arguments, category.title, R.id.main_layout);
    }
}