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

import org.solovyev.android.calculator.AndroidOperatorCategory;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.CalculatorEventData;
import org.solovyev.android.calculator.CalculatorEventListener;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.OperatorCategory;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CalculatorOperatorsActivity extends BaseActivity implements CalculatorEventListener {

    public CalculatorOperatorsActivity() {
        super(R.layout.main_empty, CalculatorOperatorsActivity.class.getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CalculatorFragmentType fragmentType = CalculatorFragmentType.operators;

        for (OperatorCategory category : OperatorCategory.getCategoriesByTabOrder()) {
            final AndroidOperatorCategory androidCategory = AndroidOperatorCategory.valueOf(category);
            if (androidCategory != null) {
                ui.addTab(this, fragmentType.createSubFragmentTag(category.name()), fragmentType.getFragmentClass(), BaseEntitiesFragment.createBundleFor(category.name()), androidCategory.getCaptionId(), R.id.main_layout);
            } else {
                ui.logError("Unable to find android operator category for " + category);
            }
        }
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType) {
            case use_operator:
                this.finish();
                break;
        }
    }
}
