package org.solovyev.android.wizard;

import android.app.Activity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Wizards {

	@Nonnull
	Class<? extends Activity> getActivityClassName();

	@Nonnull
	public Wizard getWizard(@Nullable String name) throws IllegalArgumentException;
}
