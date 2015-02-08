package org.solovyev.android.calculator.wizard;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.release.ChooseThemeReleaseNoteFragment;
import org.solovyev.android.calculator.release.ChooseThemeReleaseNoteStep;
import org.solovyev.android.calculator.release.ReleaseNoteFragment;
import org.solovyev.android.calculator.release.ReleaseNoteStep;
import org.solovyev.android.wizard.Wizard;
import org.solovyev.android.wizard.WizardFlow;
import org.solovyev.android.wizard.WizardStep;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class WizardFragment extends Fragment implements View.OnClickListener {

	@Nullable
	protected TextView nextButton;

	@Nullable
	protected TextView prevButton;

	private WizardStep step;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		step = findStepByClassName();
	}

	@Nonnull
	private WizardStep findStepByClassName() {
		if (this instanceof ReleaseNoteFragment) {
			return new ReleaseNoteStep(getArguments());
		}

		if(this instanceof ChooseThemeReleaseNoteFragment) {
			return new ChooseThemeReleaseNoteStep(getArguments());
		}

		for (CalculatorWizardStep step : CalculatorWizardStep.values()) {
			if (step.getFragmentClass().equals(getClass())) {
				return step;
			}
		}

		throw new AssertionError("Wizard step for class " + getClass() + " was not found");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_wizard, container, false);

		final ViewGroup content = (ViewGroup) view.findViewById(R.id.wizard_content);
		inflater.inflate(getViewResId(), content, true);

		nextButton = (TextView) view.findViewById(R.id.wizard_next);
		if (nextButton != null) {
			nextButton.setOnClickListener(this);
		}
		prevButton = (TextView) view.findViewById(R.id.wizard_prev);
		if (prevButton != null) {
			prevButton.setOnClickListener(this);
		}

		final Wizard wizard = getWizardActivity().getWizard();
		final WizardFlow flow = wizard.getFlow();
		final boolean canGoNext = flow.getNextStep(step) != null;
		final boolean canGoPrev = flow.getPrevStep(step) != null;
		final boolean firstTimeWizard = TextUtils.equals(wizard.getName(), CalculatorWizards.FIRST_TIME_WIZARD);
		if (canGoNext) {
			if (canGoPrev || !firstTimeWizard) {
				setupNextButton(R.string.acl_wizard_next);
			} else {
				setupNextButton(R.string.acl_wizard_start);
			}
		} else {
			setupNextButton(R.string.acl_wizard_finish);
		}

		if (canGoPrev) {
			setupPrevButton(R.string.acl_wizard_back);
		} else {
			if (firstTimeWizard) {
				setupPrevButton(R.string.wizard_skip);
			}
		}

		return view;
	}

	protected final void setupNextButton(int textResId) {
		assert nextButton != null;
		nextButton.setText(textResId);
		nextButton.setVisibility(View.VISIBLE);
	}

	protected final void setupPrevButton(int textResId) {
		assert prevButton != null;
		prevButton.setText(textResId);
		prevButton.setVisibility(View.VISIBLE);
	}

	@LayoutRes
	protected abstract int getViewResId();

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		final WizardActivity activity = getWizardActivity();
		if (id == R.id.wizard_next) {
			if (activity.canGoNext()) {
				activity.goNext();
			} else {
				activity.finishWizard();
			}
		} else if (id == R.id.wizard_prev) {
			if (activity.canGoPrev()) {
				activity.goPrev();
			} else {
				activity.finishWizardAbruptly();
			}
		}
	}

	private WizardActivity getWizardActivity() {
		return (WizardActivity) getActivity();
	}

	public WizardStep getStep() {
		if (step == null) {
			step = findStepByClassName();
		}
		return step;
	}

	public void onNext() {
		getStep().onNext(this);
	}

	public void onPrev() {
		getStep().onPrev(this);
	}
}
