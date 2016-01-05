package org.solovyev.android.wizard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WizardUi<A extends FragmentActivity> {

    private static final String FLOW = "flow";
    private static final String ARGUMENTS = "arguments";
    private static final String STEP = "step";
    @Nonnull
    protected final A activity;
    @Nonnull
    protected final WizardsAware wizardsAware;
    protected final int layoutResId;
    protected WizardStep step;
    protected Wizard wizard;

    public WizardUi(@Nonnull A activity, @Nonnull WizardsAware wizardsAware, int layoutResId) {
        this.activity = activity;
        this.wizardsAware = wizardsAware;
        this.layoutResId = layoutResId;
    }

    public static void startWizard(@Nonnull Wizards wizards, @Nonnull Context context) {
        context.startActivity(createLaunchIntent(wizards, null, context));
    }

    public static void startWizard(@Nonnull Wizards wizards, @Nullable String name, @Nonnull Context context) {
        context.startActivity(createLaunchIntent(wizards, name, context));
    }

    public static void continueWizard(@Nonnull Wizards wizards, @Nonnull String name, @Nonnull Context context) {
        final Intent intent = createLaunchIntent(wizards, name, context);

        final Wizard wizard = wizards.getWizard(name);
        final String step = wizard.getLastSavedStepName();
        tryPutStep(intent, step);

        context.startActivity(intent);
    }

    public static void tryPutStep(@Nonnull Intent intent, @Nullable WizardStep step) {
        tryPutStep(intent, step != null ? step.getName() : null);
    }

    private static void tryPutStep(@Nonnull Intent intent, @Nullable String step) {
        if (step != null) {
            intent.putExtra(STEP, step);
        }
    }

    @Nonnull
    public static Intent createLaunchIntent(@Nonnull Wizards wizards, @Nullable String name, @Nonnull Context context) {
        return createLaunchIntent(wizards, name, context, null);
    }

    @Nonnull
    public static Intent createLaunchIntent(@Nonnull Wizards wizards, @Nullable String name, @Nonnull Context context, @Nullable Bundle arguments) {
        final Intent intent = new Intent(context, wizards.getActivityClassName());
        intent.putExtra(FLOW, name);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ARGUMENTS, arguments);
        return intent;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (layoutResId != 0) {
            activity.setContentView(layoutResId);
        }

        final Intent intent = activity.getIntent();
        String wizardName = intent.getStringExtra(FLOW);
        String stepName = intent.getStringExtra(STEP);
        if (savedInstanceState != null) {
            wizardName = savedInstanceState.getString(FLOW);
            stepName = savedInstanceState.getString(STEP);
        }

        final Bundle arguments = intent.getBundleExtra(ARGUMENTS);
        wizard = wizardsAware.getWizards().getWizard(wizardName, arguments);

        if (stepName != null) {
            step = wizard.getFlow().getStepByName(stepName);
        }

        if (step == null) {
            step = wizard.getFlow().getFirstStep();
        }
    }

    public void finishWizardAbruptly() {
        finishWizard(true);
    }

    public void finishWizard() {
        finishWizard(false);
    }

    protected final void finishWizard(boolean forceFinish) {
        if (wizard != null && step != null) {
            wizard.saveFinished(step, forceFinish);
        }
        activity.finish();
    }

    @Nonnull
    protected final FragmentManager getFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    public void onSaveInstanceState(@Nonnull Bundle out) {
        out.putString(FLOW, wizard.getName());
        out.putString(STEP, step.getName());
    }

    public void onPause() {
        if (wizard != null && step != null) {
            wizard.saveLastStep(step);
        }
    }

    public WizardStep getStep() {
        return step;
    }

    public void setStep(WizardStep step) {
        this.step = step;
    }

    public WizardFlow getFlow() {
        return wizard.getFlow();
    }

    public Wizard getWizard() {
        return wizard;
    }
}
