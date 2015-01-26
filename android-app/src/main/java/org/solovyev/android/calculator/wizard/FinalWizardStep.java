package org.solovyev.android.calculator.wizard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.preferences.PurchaseDialogActivity;

import static android.content.Intent.ACTION_VIEW;

public class FinalWizardStep extends Fragment {

	private static final String GITHUB_URL = "https://github.com/serso/android-calculatorpp";
	private static final String CROWDIN_URL = "http://crowdin.net/project/calculatorpp";

	private Button donateButton;
	private Button translateButton;
	private Button contributeButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_final, null);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		donateButton = (Button) root.findViewById(R.id.cpp_wizard_final_donate_button);
		donateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), PurchaseDialogActivity.class));
			}
		});

		translateButton = (Button) root.findViewById(R.id.cpp_wizard_final_translate_button);
		translateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showUrl(CROWDIN_URL);
			}
		});

		contributeButton = (Button) root.findViewById(R.id.cpp_wizard_final_contribute_button);
		contributeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showUrl(GITHUB_URL);
			}
		});
	}

	private void showUrl(String url) {
		startActivity(new Intent(ACTION_VIEW, Uri.parse(url)));
	}
}
