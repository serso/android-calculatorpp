package org.solovyev.android.wizard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Wizard {

    @Nullable
    String getLastSavedStepName();

    boolean isFinished();

    boolean isStarted();

    void saveLastStep(@Nonnull WizardStep step);

    void saveFinished(@Nonnull WizardStep step, boolean forceFinish);

    @Nonnull
    WizardFlow getFlow();

    @Nonnull
    String getName();
}
