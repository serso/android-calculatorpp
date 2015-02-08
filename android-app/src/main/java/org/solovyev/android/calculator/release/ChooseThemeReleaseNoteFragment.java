package org.solovyev.android.calculator.release;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.wizard.ChooseThemeWizardStep;

public class ChooseThemeReleaseNoteFragment extends ChooseThemeWizardStep {

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		final TextView title = (TextView) root.findViewById(R.id.wizard_theme_title);
		title.setText(R.string.release_notes_choose_theme);
	}
}
