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
import android.util.Log;
import org.solovyev.android.calculator.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CalculatorFunctionsActivity extends BaseActivity implements CalculatorEventListener {

    public CalculatorFunctionsActivity() {
        super(R.layout.main_empty, CalculatorFunctionsActivity.class.getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = getIntent().getExtras();

        final CalculatorFragmentType fragmentType = CalculatorFragmentType.functions;

        for (FunctionCategory category : FunctionCategory.getCategoriesByTabOrder()) {
            final AndroidFunctionCategory androidCategory = AndroidFunctionCategory.valueOf(category);
            if (androidCategory != null) {

                final Bundle fragmentParameters;

                if (category == FunctionCategory.my && bundle != null) {
                    BaseEntitiesFragment.putCategory(bundle, category.name());
                    fragmentParameters = bundle;
                } else {
                    fragmentParameters = BaseEntitiesFragment.createBundleFor(category.name());
                }

                ui.addTab(this, fragmentType.createSubFragmentTag(category.name()), fragmentType.getFragmentClass(), fragmentParameters, androidCategory.getCaptionId(), R.id.main_layout);
            } else {
                Log.e(CalculatorFunctionsActivity.class.getSimpleName(), "Unable to find android function category for " + category);
            }
        }
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType) {
            case use_function:
                this.finish();
                break;
        }
    }
}
