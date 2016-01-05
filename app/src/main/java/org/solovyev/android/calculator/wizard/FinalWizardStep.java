package org.solovyev.android.calculator.wizard;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.R;

public class FinalWizardStep extends WizardFragment {

    @Override
    protected int getViewResId() {
        return R.layout.cpp_wizard_step_final;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (App.getTheme().isLight()) {
            final TextView message = (TextView) view.findViewById(R.id.wizard_final_message);
            message.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_done_light, 0, 0);
        }
    }
}
