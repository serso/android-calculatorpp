package org.solovyev.android.wizard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WizardUi<A extends FragmentActivity> {

	private static final String FLOW = "flow";
	private static final String STEP = "step";

	protected WizardStep step;
	protected Wizard wizard;

	@Nonnull
	protected final A activity;

	@Nonnull
	protected final WizardsAware wizardsAware;

	protected final int layoutResId;

	public WizardUi(@Nonnull A activity, @Nonnull WizardsAware wizardsAware, int layoutResId) {
		this.activity = activity;
		this.wizardsAware = wizardsAware;
		this.layoutResId = layoutResId;
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

		wizard = wizardsAware.getWizards().getWizard(wizardName);

		if (stepName != null) {
			step = wizard.getFlow().getStepByName(stepName);
		}

		if (step == null) {
			step = wizard.getFlow().getFirstStep();
		}
	}

	public void setStep(WizardStep step) {
		this.step = step;
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

	protected final boolean tryGoPrev() {
		if (step == null) {
			return true;
		} else {
			final Fragment fragment = getFragmentManager().findFragmentByTag(step.getFragmentTag());
			return fragment == null || step.onPrev(fragment);
		}
	}

	@Nonnull
	protected final FragmentManager getFragmentManager() {
		return activity.getSupportFragmentManager();
	}

	protected final boolean tryGoNext() {
		if (step == null) {
			return true;
		} else {
			final Fragment fragment = getFragmentManager().findFragmentByTag(step.getFragmentTag());
			return fragment == null || step.onNext(fragment);
		}
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

	public WizardFlow getFlow() {
		return wizard.getFlow();
	}

	public Wizard getWizard() {
		return wizard;
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
	private static Intent createLaunchIntent(@Nonnull Wizards wizards, @Nullable String name, @Nonnull Context context) {
		final Intent intent = new Intent(context, wizards.getActivityClassName());
		intent.putExtra(FLOW, name);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return intent;
	}
}
