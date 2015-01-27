/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.wizard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import org.solovyev.android.calculator.R;
import org.solovyev.android.sherlock.AndroidSherlockUtils;

import javax.annotation.Nonnull;

public class FinishWizardConfirmationDialog extends DialogFragment {

	@Nonnull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setMessage(R.string.acl_wizard_finish_confirmation);
		b.setPositiveButton(R.string.acl_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				((Listener) getActivity()).finishWizardAbruptly();
			}
		});
		b.setNegativeButton(R.string.acl_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dismiss();
			}
		});
		return b.create();
	}

	public static <A extends FragmentActivity & Listener> void show(@Nonnull A activity) {
		if (!(activity instanceof Listener)) {
			throw new IllegalArgumentException("Activity should implement " + Listener.class);
		}
		AndroidSherlockUtils.showDialog(new FinishWizardConfirmationDialog(), FinishWizardConfirmationDialog.class.getSimpleName(), activity.getSupportFragmentManager());
	}

	public static interface Listener {
		void finishWizardAbruptly();
	}
}
