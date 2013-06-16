package org.solovyev.android.calculator.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:44 PM
 */
public final class WelcomeWizardStep extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_welcome, null);
	}
}
