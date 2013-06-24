package org.solovyev.android.calculator.wizard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:22 PM
 */
public interface WizardFlow {

	@Nonnull
	String getName();

	@Nullable
	WizardStep getStep(@Nonnull String name);

	@Nullable
	WizardStep getNextStep(@Nonnull WizardStep step);

	@Nullable
	WizardStep getPrevStep(@Nonnull WizardStep step);

	@Nonnull
	WizardStep getFirstStep();
}
