package org.solovyev.android.wizard;

import android.content.Context;
import android.content.SharedPreferences;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class BaseWizard implements Wizard {

    static final String FLOW = "flow";
    static final String FLOW_FINISHED = "flow_finished";

    @Nonnull
    private final String name;

    @Nonnull
    private final Context context;

    @Nonnull
    private final WizardFlow flow;

    public BaseWizard(@Nonnull String name,
                      @Nonnull Context context,
                      @Nonnull WizardFlow flow) {
        this.name = name;
        this.context = context;
        this.flow = flow;
    }

    @Nonnull
    static String makeFinishedPreferenceKey(@Nonnull String flowName) {
        return FLOW_FINISHED + ":" + flowName;
    }

    @Nonnull
    static String makeLastStepPreferenceKey(@Nonnull String flowName) {
        return FLOW + ":" + flowName;
    }

    @Override
    public void saveLastStep(@Nonnull WizardStep step) {
        final SharedPreferences preferences = getPreferences();
        final SharedPreferences.Editor editor = preferences.edit();

        editor.putString(makeLastStepPreferenceKey(name), step.getName());

        editor.apply();
    }

    @Override
    public void saveFinished(@Nonnull WizardStep step, boolean forceFinish) {
        final SharedPreferences preferences = getPreferences();
        final SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(makeFinishedPreferenceKey(name), forceFinish || flow.getNextStep(step) == null);

        editor.apply();
    }

    @Nonnull
    private SharedPreferences getPreferences() {
        return getDefaultSharedPreferences(context);
    }

    @Override
    @Nullable
    public String getLastSavedStepName() {
        return getPreferences().getString(makeLastStepPreferenceKey(name), null);
    }

    @Override
    public boolean isFinished() {
        return getPreferences().getBoolean(makeFinishedPreferenceKey(name), false);
    }

    @Override
    public boolean isStarted() {
        return getLastSavedStepName() != null;
    }

    @Override
    @Nonnull
    public WizardFlow getFlow() {
        return flow;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }
}
