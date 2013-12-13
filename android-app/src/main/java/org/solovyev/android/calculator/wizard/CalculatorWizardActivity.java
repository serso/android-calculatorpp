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

package org.solovyev.android.calculator.wizard;

import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.R;
import org.solovyev.android.wizard.BaseWizardActivity;
import org.solovyev.android.wizard.Wizards;

import javax.annotation.Nonnull;

public final class CalculatorWizardActivity extends BaseWizardActivity {

	@Nonnull
	private Wizards wizards;

	public CalculatorWizardActivity() {
		super(R.layout.cpp_wizard);
		this.wizards = CalculatorApplication.getInstance().getWizards();
	}

	@Nonnull
	public Wizards getWizards() {
		return wizards;
	}

	public void setWizards(@Nonnull Wizards wizards) {
		this.wizards = wizards;
	}
}
