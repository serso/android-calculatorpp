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

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import jscl.math.operator.Operator;

import javax.annotation.Nonnull;

import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.common.text.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 1:53 PM
 */

public class CalculatorOperatorsFragment extends AbstractMathEntityListFragment<Operator> {

	public CalculatorOperatorsFragment() {
		super(CalculatorFragmentType.operators);
	}

	@Override
	protected AMenuItem<Operator> getOnClickAction() {
		return LongClickMenuItem.use;
	}

	@Nonnull
	@Override
	protected List<LabeledMenuItem<Operator>> getMenuItemsOnLongClick(@Nonnull Operator item) {
		final List<LabeledMenuItem<Operator>> result = new ArrayList<LabeledMenuItem<Operator>>(Arrays.asList(LongClickMenuItem.values()));

		if (Strings.isEmpty(OperatorDescriptionGetter.instance.getDescription(this.getActivity(), item.getName()))) {
			result.remove(LongClickMenuItem.copy_description);
		}

		return result;
	}

	@Nonnull
	@Override
	protected MathEntityDescriptionGetter getDescriptionGetter() {
		return OperatorDescriptionGetter.instance;
	}


	@Nonnull
	@Override
	protected List<Operator> getMathEntities() {
		final List<Operator> result = new ArrayList<Operator>();

		result.addAll(Locator.getInstance().getEngine().getOperatorsRegistry().getEntities());
		result.addAll(Locator.getInstance().getEngine().getPostfixFunctionsRegistry().getEntities());

		return result;
	}

	@Override
	protected String getMathEntityCategory(@Nonnull Operator operator) {
		String result = Locator.getInstance().getEngine().getOperatorsRegistry().getCategory(operator);
		if (result == null) {
			result = Locator.getInstance().getEngine().getPostfixFunctionsRegistry().getCategory(operator);
		}

		return result;
	}

	private static enum OperatorDescriptionGetter implements MathEntityDescriptionGetter {

		instance;

		@Override
		public String getDescription(@Nonnull Context context, @Nonnull String mathEntityName) {
			String result = Locator.getInstance().getEngine().getOperatorsRegistry().getDescription(mathEntityName);
			if (Strings.isEmpty(result)) {
				result = Locator.getInstance().getEngine().getPostfixFunctionsRegistry().getDescription(mathEntityName);
			}

			return result;
		}
	}

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

	private static enum LongClickMenuItem implements LabeledMenuItem<Operator> {

		use(R.string.c_use) {
			@Override
			public void onClick(@Nonnull Operator data, @Nonnull Context context) {
				Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_operator, data);
			}
		},

		copy_description(R.string.c_copy_description) {
			@Override
			public void onClick(@Nonnull Operator data, @Nonnull Context context) {
				final String text = OperatorDescriptionGetter.instance.getDescription(context, data.getName());
				if (!Strings.isEmpty(text)) {
					final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
					clipboard.setText(text);
				}
			}
		};
		private final int captionId;

		LongClickMenuItem(int captionId) {
			this.captionId = captionId;
		}

		@Nonnull
		@Override
		public String getCaption(@Nonnull Context context) {
			return context.getString(captionId);
		}
	}

}

