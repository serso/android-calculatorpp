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

package org.solovyev.android.wizard;

import org.solovyev.common.JPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static org.solovyev.common.collections.Collections.find;

public final class ListWizardFlow implements WizardFlow {

	@Nonnull
	private final List<WizardStep> steps;

	public ListWizardFlow(@Nonnull List<WizardStep> steps) {
		this.steps = steps;
	}

	@Nullable
	@Override
	public WizardStep getStepByName(@Nonnull final String name) {
		return find(steps, new JPredicate<WizardStep>() {
			@Override
			public boolean apply(@Nullable WizardStep step) {
				assert step != null;
				return step.getName().equals(name);
			}
		});
	}

	@Nullable
	@Override
	public WizardStep getNextStep(@Nonnull WizardStep step) {
		final int i = steps.indexOf(step);
		if (i >= 0 && i + 1 < steps.size()) {
			return steps.get(i + 1);
		} else {
			return null;
		}
	}

	@Nullable
	@Override
	public WizardStep getPrevStep(@Nonnull WizardStep step) {
		final int i = steps.indexOf(step);
		if (i >= 1) {
			return steps.get(i - 1);
		} else {
			return null;
		}
	}

	@Nonnull
	@Override
	public WizardStep getFirstStep() {
		return steps.get(0);
	}
}
