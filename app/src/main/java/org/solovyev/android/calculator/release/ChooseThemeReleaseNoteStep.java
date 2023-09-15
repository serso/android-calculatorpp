package org.solovyev.android.calculator.release;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import javax.annotation.Nonnull;

public class ChooseThemeReleaseNoteStep extends ReleaseNoteStep {
    public static final int VERSION_CODE = 137;

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
