package org.solovyev.android.calculator.release;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import javax.annotation.Nonnull;

public class ChooseThemeReleaseNoteStep extends ReleaseNoteStep {
	public ChooseThemeReleaseNoteStep(Integer version) {
		super(version);
	}

	public ChooseThemeReleaseNoteStep(@Nonnull Bundle arguments) {
		super(arguments);
	}

	@Nonnull
	@Override
	public Class<? extends Fragment> getFragmentClass() {
		return ChooseThemeReleaseNoteFragment.class;
	}
}
