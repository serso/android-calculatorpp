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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:30 PM
 */
public final class ListWizardFlow implements WizardFlow {

	@Nonnull
	private final String name;

	@Nonnull
	private final List<WizardStep> wizardSteps;

	public ListWizardFlow(@Nonnull String name, @Nonnull List<WizardStep> wizardSteps) {
		this.name = name;
		this.wizardSteps = wizardSteps;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Nullable
	@Override
	public WizardStep getStep(@Nonnull final String name) {
		return Iterables.find(wizardSteps, new Predicate<WizardStep>() {
			@Override
			public boolean apply(@Nullable WizardStep step) {
				assert step != null;
				return step.getName().equals(name);
			}
		}, null);
	}

	@Nullable
	@Override
	public WizardStep getNextStep(@Nonnull WizardStep step) {
		final int i = wizardSteps.indexOf(step);
		if (i >= 0 && i + 1 < wizardSteps.size()) {
			return wizardSteps.get(i + 1);
		} else {
			return null;
		}
	}

	@Nullable
	@Override
	public WizardStep getPrevStep(@Nonnull WizardStep step) {
		final int i = wizardSteps.indexOf(step);
		if (i >= 1) {
			return wizardSteps.get(i - 1);
		} else {
			return null;
		}
	}

	@Nonnull
	@Override
	public WizardStep getFirstStep() {
		return wizardSteps.get(0);
	}
}
