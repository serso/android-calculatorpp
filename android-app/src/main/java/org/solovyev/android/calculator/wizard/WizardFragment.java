package org.solovyev.android.calculator.wizard;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.solovyev.android.calculator.R;

import javax.annotation.Nullable;

public abstract class WizardFragment extends Fragment implements View.OnClickListener {

	@Nullable
	private TextView nextButton;

	@Nullable
	private TextView prevButton;

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

		return view;
	}

	protected final void setupNextButton(int textResId) {
		assert nextButton != null;
		nextButton.setText(textResId);
	}

	protected final void setupPrevButton(int textResId) {
		assert prevButton != null;
		prevButton.setText(textResId);
	}

	@LayoutRes
	protected abstract int getViewResId();

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		final WizardActivity activity = (WizardActivity) getActivity();
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
}
