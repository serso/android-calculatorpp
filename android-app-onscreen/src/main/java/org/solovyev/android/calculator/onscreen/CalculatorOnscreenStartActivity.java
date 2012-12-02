package org.solovyev.android.calculator.onscreen;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import org.solovyev.android.AndroidUtils2;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.Preference;

public class CalculatorOnscreenStartActivity extends Activity {

    private static final Preference<Boolean> removeIconDialogShown = new BooleanPreference("onscreen_remove_icon_dialog_shown", false);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (AndroidUtils2.isComponentEnabled(this, CalculatorOnscreenStartActivity.class)) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (!removeIconDialogShown.getPreference(prefs)) {

                removeIconDialogShown.putPreference(prefs, true);
            }
        }

        CalculatorOnscreenService.showOnscreenView(this);

		this.finish();
	}
}
