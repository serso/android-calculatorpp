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

package org.solovyev.android.calculator.widget;

import android.content.Context;
import android.content.Intent;

import javax.annotation.Nonnull;

import org.solovyev.android.calculator.CalculatorButton;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.external.DefaultExternalCalculatorIntentHandler;
import org.solovyev.android.calculator.external.ExternalCalculatorStateUpdater;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 10:39 PM
 */
public class CalculatorWidgetIntentHandler extends DefaultExternalCalculatorIntentHandler {

	public CalculatorWidgetIntentHandler(@Nonnull ExternalCalculatorStateUpdater stateUpdater) {
		super(stateUpdater);
	}

	@Override
	public void onIntent(@Nonnull Context context, @Nonnull Intent intent) {
		super.onIntent(context, intent);

		if (AbstractCalculatorWidgetProvider.BUTTON_PRESSED_ACTION.equals(intent.getAction())) {
			final int buttonId = intent.getIntExtra(AbstractCalculatorWidgetProvider.BUTTON_ID_EXTRA, 0);

			final CalculatorButton button = CalculatorButton.getById(buttonId);
			if (button != null) {
				button.onClick(context);
			}
		} else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(intent.getAction())) {
			updateState(context, Locator.getInstance().getEditor().getViewState(), Locator.getInstance().getDisplay().getViewState());
		}
	}

}
