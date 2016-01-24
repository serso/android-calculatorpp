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

package org.solovyev.android.calculator.math.edit;

import android.os.Bundle;
import android.os.Parcelable;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.FunctionCategory;
import org.solovyev.android.calculator.R;

import javax.annotation.Nullable;

public class FunctionsActivity extends BaseActivity {

    public static final String EXTRA_FUNCTION = "function";

    public FunctionsActivity() {
        super(R.layout.main_empty, FunctionsActivity.class.getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();
        final Parcelable function = extras != null ? extras.getParcelable(EXTRA_FUNCTION) : null;

        final CalculatorFragmentType fragmentType = CalculatorFragmentType.functions;

        for (FunctionCategory category : FunctionCategory.values()) {
            final Bundle arguments = new Bundle(2);
            if (category == FunctionCategory.my && function != null) {
                arguments.putParcelable(FunctionsFragment.ARG_FUNCTION, function);
            }
            arguments.putString(FunctionsFragment.ARG_CATEGORY, category.name());
            ui.addTab(this, fragmentType.createSubFragmentTag(category.name()), fragmentType.getFragmentClass(), arguments, category.title, R.id.main_layout);
        }
    }
}
