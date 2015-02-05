package org.solovyev.android.calculator.release;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import org.solovyev.android.wizard.WizardStep;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReleaseNoteStep implements WizardStep {
	private final int version;

	public ReleaseNoteStep(int version) {
		this.version = version;
	}

	public ReleaseNoteStep(@Nonnull Bundle arguments) {
		this(arguments.getInt(ReleaseNoteFragment.ARG_VERSION, 0));
	}

	@Nonnull
	@Override
	public String getFragmentTag() {
		return getName();
	}

	@Nonnull
	@Override
	public Class<? extends Fragment> getFragmentClass() {
		return ReleaseNoteFragment.class;
	}

	@Nullable
	@Override
	public Bundle getFragmentArgs() {
		final Bundle bundle = new Bundle();
		bundle.putInt(ReleaseNoteFragment.ARG_VERSION, version);
		return bundle;
	}

	@Override
	public int getTitleResId() {
		return 0;
	}

	@Override
	public int getNextButtonTitleResId() {
		return 0;
	}

	@Override
	public boolean onNext(@Nonnull Fragment fragment) {
		return false;
	}

	@Override
	public boolean onPrev(@Nonnull Fragment fragment) {
		return false;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Nonnull
	@Override
	public String getName() {
		return "release-note-" + version;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ReleaseNoteStep that = (ReleaseNoteStep) o;
		return version == that.version;

	}

	@Override
	public int hashCode() {
		return version;
	}
}
