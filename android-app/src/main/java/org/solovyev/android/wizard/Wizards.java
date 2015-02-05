package org.solovyev.android.wizard;

import android.app.Activity;
import android.os.Bundle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Wizards {

	@Nonnull
	Class<? extends Activity> getActivityClassName();

	@Nonnull
	public Wizard getWizard(@Nullable String name, @Nullable Bundle arguments) throws IllegalArgumentException;

	@Nonnull
	public Wizard getWizard(@Nullable String name) throws IllegalArgumentException;
}
