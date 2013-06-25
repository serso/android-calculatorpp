package org.solovyev.android.calculator.wizard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import javax.annotation.Nonnull;

import org.solovyev.android.sherlock.AndroidSherlockUtils;

public class FinishWizardConfirmationDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setMessage("Do you really want to finish wizard?");
		b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				((CalculatorWizardActivity) getActivity()).finishFlow(true);
			}
		});
		b.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dismiss();
			}
		});
		return b.create();
	}

	public static void show(@Nonnull CalculatorWizardActivity activity){
		AndroidSherlockUtils.showDialog(new FinishWizardConfirmationDialog(), FinishWizardConfirmationDialog.class.getSimpleName(), activity.getSupportFragmentManager());
	}
}
