package org.solovyev.android.calculator.wizard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
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
	private ViewGroup wizardContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cpp_wizard);
		prevButton = findViewById(R.id.wizard_prev_button);
		nextButton = findViewById(R.id.wizard_next_button);
		finishButton = findViewById(R.id.wizard_finish_button);
		wizardContent = (ViewGroup) findViewById(R.id.wizard_content);

		String wizardName = null;
		WizardStep step = null;
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

	private void setStep(@Nonnull WizardStep step) {
		if (this.step == null || !this.step.equals(step)) {
			final FragmentManager fm = getSupportFragmentManager();
			final FragmentTransaction ft = fm.beginTransaction();

			if (this.step != null) {
				final Fragment oldFragment = fm.findFragmentByTag(this.step.getFragmentTag());
				if (oldFragment != null) {
					ft.hide(oldFragment);
				}
			}

			this.step = step;

			Fragment newFragment = fm.findFragmentByTag(step.getFragmentTag());
			if(newFragment == null) {
				newFragment = Fragment.instantiate(this, step.getFragmentClass().getName());
				ft.add(R.id.wizard_content, newFragment, step.getFragmentTag());
			} else {
				ft.show(newFragment);
			}

			ft.commit();

			final WizardStep nextStep = flow.getNextStep(step);
			if (nextStep == null) {
				finishButton.setVisibility(VISIBLE);
				finishButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
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
						setStep(nextStep);
					}
				});
			}

			final WizardStep prevStep = flow.getPrevStep(step);
			if (prevStep == null) {
				prevButton.setVisibility(GONE);
				prevButton.setOnClickListener(null);
			} else {
				prevButton.setVisibility(VISIBLE);
				prevButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setStep(prevStep);
					}
				});
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle out) {
		super.onSaveInstanceState(out);

		out.putSerializable(STEP, step);
	}

}
