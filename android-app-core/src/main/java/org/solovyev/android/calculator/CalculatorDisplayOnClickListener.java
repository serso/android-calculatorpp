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

package org.solovyev.android.calculator;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import javax.annotation.Nonnull;

import org.solovyev.android.menu.ContextMenuBuilder;
import org.solovyev.android.menu.ListContextMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 10:58
 */
public class CalculatorDisplayOnClickListener implements View.OnClickListener {

	@Nonnull
	private final FragmentActivity activity;

	public CalculatorDisplayOnClickListener(@Nonnull FragmentActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		if (v instanceof CalculatorDisplayView) {
			final CalculatorDisplay cd = Locator.getInstance().getDisplay();

			final CalculatorDisplayViewState displayViewState = cd.getViewState();

			if (displayViewState.isValid()) {
				final List<CalculatorDisplayMenuItem> filteredMenuItems = new ArrayList<CalculatorDisplayMenuItem>(CalculatorDisplayMenuItem.values().length);
				for (CalculatorDisplayMenuItem menuItem : CalculatorDisplayMenuItem.values()) {
					if (menuItem.isItemVisible(displayViewState)) {
						filteredMenuItems.add(menuItem);
					}
				}

				if (!filteredMenuItems.isEmpty()) {
					ContextMenuBuilder.newInstance(activity, "display-menu", ListContextMenu.newInstance(filteredMenuItems)).build(displayViewState).show();
				}

			} else {
				final String errorMessage = displayViewState.getErrorMessage();
				if (errorMessage != null) {
					Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_evaluation_error, errorMessage, activity);
				}
			}
		}
	}
}
