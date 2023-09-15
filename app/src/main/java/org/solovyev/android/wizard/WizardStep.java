package org.solovyev.android.wizard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WizardStep {

    @Nonnull
    String getFragmentTag();

    @Nonnull
    Class<? extends Fragment> getFragmentClass();

    @Nullable
    Bundle getFragmentArgs();

    int getTitleResId();

    int getNextButtonTitleResId();

    boolean onNext(@Nonnull Fragment fragment);

    boolean onPrev(@Nonnull Fragment fragment);

    boolean isVisible();

    @Nonnull
    String getName();
}
