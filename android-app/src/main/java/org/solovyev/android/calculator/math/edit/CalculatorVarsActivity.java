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

import android.content.Intent;
import android.os.Bundle;
import org.solovyev.android.calculator.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CalculatorVarsActivity extends BaseActivity implements CalculatorEventListener {

	public CalculatorVarsActivity() {
		super(R.layout.main_empty, CalculatorVarsActivity.class.getSimpleName());
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle bundle;

		final Intent intent = getIntent();
		if (intent != null) {
			bundle = intent.getExtras();
		} else {
			bundle = null;
		}

		final CalculatorFragmentType fragmentType = CalculatorFragmentType.variables;

		for (VarCategory category : VarCategory.getCategoriesByTabOrder()) {

			final Bundle fragmentParameters;

			if (category == VarCategory.my && bundle != null) {
				AbstractMathEntityListFragment.putCategory(bundle, category.name());
				fragmentParameters = bundle;
			} else {
				fragmentParameters = AbstractMathEntityListFragment.createBundleFor(category.name());
			}


			final AndroidVarCategory androidVarCategory = AndroidVarCategory.valueOf(category);

			if (androidVarCategory != null) {
				ui.addTab(this, fragmentType.createSubFragmentTag(category.name()), fragmentType.getFragmentClass(), fragmentParameters, androidVarCategory.getCaptionId(), R.id.main_layout);
			} else {
				ui.logError("Unable to find android var category for " + category);
			}
		}
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
		switch (calculatorEventType) {
			case use_constant:
				this.finish();
				break;
		}
	}
}
