package org.solovyev.android.wizard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WizardUi<A extends FragmentActivity & FinishWizardConfirmationDialog.Listener> {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	private static final String FLOW = "flow";
	private static final String STEP = "step";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	private WizardStep step;

	private Wizard wizard;

	/*
	**********************************************************************
	*
	*                           VIEWS
	*
	**********************************************************************
	*/

	@Nullable
	private View prevButton;

	@Nonnull
	private Button nextButton;

	@Nonnull
	private final A activity;

	@Nonnull
	private final WizardsAware wizardsAware;

	private final int layoutResId;

	public WizardUi(@Nonnull A activity, @Nonnull WizardsAware wizardsAware, int layoutResId) {
		this.activity = activity;
		this.wizardsAware = wizardsAware;
		this.layoutResId = layoutResId;
	}

	/*
	**********************************************************************
	*
	*                           LIFECYCLE
	*
	**********************************************************************
	*/

	public void onCreate(@Nullable Bundle savedInstanceState) {
		activity.setContentView(layoutResId);

		prevButton = activity.findViewById(R.id.acl_wizard_prev_button);
		nextButton = (Button) activity.findViewById(R.id.acl_wizard_next_button);

		String wizardName = activity.getIntent().getStringExtra(FLOW);
		String stepName = activity.getIntent().getStringExtra(STEP);
		if (savedInstanceState != null) {
			wizardName = savedInstanceState.getString(FLOW);
			stepName = savedInstanceState.getString(STEP);
		}

		wizard = wizardsAware.getWizards().getWizard(wizardName);

		WizardStep step = null;
		if (stepName != null) {
			step = wizard.getFlow().getStepByName(stepName);
		}

		if (step == null) {
			step = wizard.getFlow().getFirstStep();
		}

		setStep(step);
	}


	public void setStep(@Nonnull WizardStep step) {
		if (this.step == null || !this.step.equals(step)) {
			hideFragment();
			this.step = step;
			showFragment();

			initTitle();
			initNextButton();
			initPrevButton();
		}
	}

	private void initTitle() {
		activity.setTitle(step.getTitleResId());
	}

	private void initPrevButton() {
		if (prevButton != null) {
			final WizardStep prevStep = wizard.getFlow().getPrevStep(step);
			if (prevStep == null) {
				prevButton.setVisibility(GONE);
				prevButton.setOnClickListener(null);
			} else {
				prevButton.setVisibility(VISIBLE);
				prevButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (tryGoPrev()) {
							setStep(prevStep);
						}
					}
				});
			}
		}
	}

	private void initNextButton() {
		final WizardStep nextStep = wizard.getFlow().getNextStep(step);
		if (nextStep == null) {
			nextButton.setText(R.string.acl_wizard_finish);
			nextButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tryGoNext()) {
						finishWizard();
					}
				}
			});
		} else {
			nextButton.setText(step.getNextButtonTitleResId());
			nextButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tryGoNext()) {
						setStep(nextStep);
					}
				}
			});
		}
	}

	public void finishWizardAbruptly() {
		finishWizard(true);
	}

	public void finishWizard() {
		finishWizard(false);
	}

	void finishWizard(boolean forceFinish) {
		if (wizard != null && step != null) {
			wizard.saveFinished(step, forceFinish);
		}
		activity.finish();
	}

	private boolean tryGoPrev() {
		if (step == null) {
			return true;
		} else {
			final Fragment fragment = getFragmentManager().findFragmentByTag(step.getFragmentTag());
			return fragment == null || step.onPrev(fragment);
		}
	}

	@Nonnull
	private FragmentManager getFragmentManager() {
		return activity.getSupportFragmentManager();
	}

	private boolean tryGoNext() {
		if (step == null) {
			return true;
		} else {
			final Fragment fragment = getFragmentManager().findFragmentByTag(step.getFragmentTag());
			return fragment == null || step.onNext(fragment);
		}
	}

	@Nonnull
	private Fragment showFragment() {
		final FragmentManager fm = getFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();

		Fragment newFragment = fm.findFragmentByTag(step.getFragmentTag());

		if (newFragment == null) {
			newFragment = Fragment.instantiate(activity, step.getFragmentClass().getName(), step.getFragmentArgs());
			ft.add(R.id.acl_wizard_content, newFragment, step.getFragmentTag());
		}

		ft.commit();
		fm.executePendingTransactions();

		return newFragment;
	}

	private void hideFragment() {
		final FragmentManager fm = getFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();

		if (step != null) {
			hideFragmentByTag(fm, ft, step.getFragmentTag());
		}

		ft.commit();
		fm.executePendingTransactions();
	}

	private void hideFragmentByTag(@Nonnull FragmentManager fm, @Nonnull FragmentTransaction ft, @Nonnull String fragmentTag) {
		final Fragment oldFragment = fm.findFragmentByTag(fragmentTag);
		if (oldFragment != null) {
			ft.remove(oldFragment);
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

	public void onBackPressed() {
		FinishWizardConfirmationDialog.show(activity);
	}

	/*
	**********************************************************************
	*
	*                           GETTERS
	*
	**********************************************************************
	*/

	public WizardStep getStep() {
		return step;
	}

	public WizardFlow getFlow() {
		return wizard.getFlow();
	}

	public Wizard getWizard() {
		return wizard;
	}

	@Nullable
	public View getPrevButton() {
		return prevButton;
	}

	public View getNextButton() {
		return nextButton;
	}

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

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
