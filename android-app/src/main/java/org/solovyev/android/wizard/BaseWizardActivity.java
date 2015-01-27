package org.solovyev.android.wizard;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import javax.annotation.Nonnull;

public abstract class BaseWizardActivity extends ActionBarActivity implements WizardsAware, FinishWizardConfirmationDialog.Listener {

	@Nonnull
	private WizardUi ui;

	protected BaseWizardActivity(int layoutResId) {
		ui = new WizardUi<BaseWizardActivity>(this, this, layoutResId);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ui.onCreate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle out) {
		super.onSaveInstanceState(out);
		ui.onSaveInstanceState(out);
	}

	@Override
	public void onPause() {
		super.onPause();
		ui.onPause();
	}

	@Override
	public void onBackPressed() {
		ui.onBackPressed();
	}

	@Override
	public void finishWizardAbruptly() {
		ui.finishWizardAbruptly();
	}
}
