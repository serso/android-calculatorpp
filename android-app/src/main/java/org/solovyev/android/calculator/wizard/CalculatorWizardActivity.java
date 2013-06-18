package org.solovyev.android.calculator.wizard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:07 PM
 */
public final class CalculatorWizardActivity extends SherlockFragmentActivity {

	static final String FLOW = "flow";
	static final String STEP = "step";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/
	private WizardStep step;

	private WizardFlow flow;

	/*
	**********************************************************************
	*
	*                           VIEWS
	*
	**********************************************************************
	*/

	private View prevButton;
	private View nextButton;
	private View finishButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cpp_wizard);
		prevButton = findViewById(R.id.wizard_prev_button);
		nextButton = findViewById(R.id.wizard_next_button);
		finishButton = findViewById(R.id.wizard_finish_button);

		String wizardName = getIntent().getStringExtra(FLOW);
		WizardStep step = (WizardStep) getIntent().getSerializableExtra(STEP);
		if (savedInstanceState != null) {
			wizardName = savedInstanceState.getString(FLOW);
			step = (WizardStep) savedInstanceState.getSerializable(STEP);
		}

		flow = Wizard.getWizardFlow(wizardName != null ? wizardName : FirstTimeWizardFlow.NAME);

		if (step == null) {
			step = flow.getFirstStep();
		}

		setStep(step);
	}

	void setStep(@Nonnull WizardStep step) {
		if (this.step == null || !this.step.equals(step)) {
			final FragmentManager fm = getSupportFragmentManager();
			final FragmentTransaction ft = fm.beginTransaction();

			hideFragment(fm, ft);

			this.step = step;

			showFragment(fm, ft);

			ft.commit();

			initNextButton();
			initPrevButton();
		}
	}

	private void initPrevButton() {
		final WizardStep prevStep = flow.getPrevStep(step);
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

	private void initNextButton() {
		final WizardStep nextStep = flow.getNextStep(step);
		if (nextStep == null) {
			finishButton.setVisibility(VISIBLE);
			finishButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tryGoNext()) {
						finish();
					}
				}
			});

			nextButton.setVisibility(GONE);
			nextButton.setOnClickListener(null);
		} else {
			finishButton.setVisibility(GONE);
			finishButton.setOnClickListener(null);

			nextButton.setVisibility(VISIBLE);
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

	private boolean tryGoPrev() {
		if (this.step == null) {
			return true;
		} else {
			final Fragment fragment = getSupportFragmentManager().findFragmentByTag(this.step.getFragmentTag());
			if (fragment != null) {
				return this.step.onPrev(fragment);
			} else {
				return true;
			}
		}
	}

	private boolean tryGoNext() {
		if (this.step == null) {
			return true;
		} else {
			final Fragment fragment = getSupportFragmentManager().findFragmentByTag(this.step.getFragmentTag());
			if (fragment != null) {
				return this.step.onNext(fragment);
			} else {
				return true;
			}
		}
	}

	@Nonnull
	private Fragment showFragment(@Nonnull FragmentManager fm, @Nonnull FragmentTransaction ft) {
		Fragment newFragment = fm.findFragmentByTag(this.step.getFragmentTag());

		if (newFragment == null) {
			newFragment = Fragment.instantiate(this, this.step.getFragmentClass().getName(), this.step.getFragmentArgs());
			ft.add(R.id.wizard_content, newFragment, this.step.getFragmentTag());
		} else {
			ft.show(newFragment);
		}

		return newFragment;
	}

	private void hideFragment(@Nonnull FragmentManager fm, @Nonnull FragmentTransaction ft) {
		if (this.step != null) {
			final Fragment oldFragment = fm.findFragmentByTag(this.step.getFragmentTag());
			if (oldFragment != null) {
				ft.hide(oldFragment);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle out) {
		super.onSaveInstanceState(out);

		out.putString(FLOW, flow.getName());
		out.putSerializable(STEP, step);
	}

	WizardStep getStep() {
		return step;
	}

	WizardFlow getFlow() {
		return flow;
	}

	/*
	**********************************************************************
	*
	*                           STATIC/INNER
	*
	**********************************************************************
	*/

	public static void startWizard(@Nonnull String name, @Nonnull Context context) {
		final Intent intent = new Intent(context, CalculatorWizardActivity.class);
		intent.putExtra(FLOW, name);
		context.startActivity(intent);
	}
}
