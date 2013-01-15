package org.solovyev.android.calculator.onscreen;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.AndroidUtils2;
import org.solovyev.android.calculator.AbstractFixableError;
import org.solovyev.android.App;
import org.solovyev.android.calculator.FixableMessage;
import org.solovyev.android.calculator.FixableMessagesDialog;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.common.msg.MessageType;

import java.util.Arrays;

public class CalculatorOnscreenStartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (AndroidUtils2.isComponentEnabled(this, CalculatorOnscreenStartActivity.class)) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

			if (!CalculatorPreferences.OnscreenCalculator.removeIconDialogShown.getPreference(prefs)) {
				FixableMessagesDialog.showDialog(Arrays.asList(new FixableMessage(getString(R.string.cpp_onscreen_remove_icon_message), MessageType.warning, new RemoveIconFixableError(this))), this, false);
				CalculatorPreferences.OnscreenCalculator.removeIconDialogShown.putPreference(prefs, true);
            }
        }

        CalculatorOnscreenService.showOnscreenView(this);

		this.finish();
	}

	public static class RemoveIconFixableError extends AbstractFixableError {

		public RemoveIconFixableError(@NotNull Context context) {
			super(context.getString(R.string.cpp_onscreen_remove_icon_button_text));
		}

		@Override
		public void fix() {
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance().getApplication());
			CalculatorPreferences.OnscreenCalculator.showAppIcon.putPreference(prefs, false);
		}
	}

}
