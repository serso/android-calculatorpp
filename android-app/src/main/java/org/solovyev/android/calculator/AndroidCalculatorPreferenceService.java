package org.solovyev.android.calculator;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import jscl.AngleUnit;
import jscl.NumeralBase;
import javax.annotation.Nonnull;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.msg.AndroidMessage;
import org.solovyev.common.msg.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 7:46 PM
 */
public class AndroidCalculatorPreferenceService implements CalculatorPreferenceService {

	// one hour
	private static final Long PREFERRED_PREFS_INTERVAL_TIME = 1000L * 60L * 60L;

	@Nonnull
	private final Application application;

	public AndroidCalculatorPreferenceService(@Nonnull Application application) {
		this.application = application;
	}

	@Override
	public void checkPreferredPreferences(boolean force) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);

		final Long currentTime = System.currentTimeMillis();

		if (force || (CalculatorPreferences.Calculations.showCalculationMessagesDialog.getPreference(prefs) && isTimeForCheck(currentTime, prefs))) {
			final NumeralBase preferredNumeralBase = CalculatorPreferences.Calculations.preferredNumeralBase.getPreference(prefs);
			final NumeralBase numeralBase = AndroidCalculatorEngine.Preferences.numeralBase.getPreference(prefs);

			final AngleUnit preferredAngleUnits = CalculatorPreferences.Calculations.preferredAngleUnits.getPreference(prefs);
			final AngleUnit angleUnits = AndroidCalculatorEngine.Preferences.angleUnit.getPreference(prefs);

			final List<FixableMessage> messages = new ArrayList<FixableMessage>(2);
			if (numeralBase != preferredNumeralBase) {
				messages.add(new FixableMessage(application.getString(R.string.preferred_numeral_base_message, preferredNumeralBase.name(), numeralBase.name()), MessageType.warning, CalculatorFixableError.preferred_numeral_base));
			}

			if (angleUnits != preferredAngleUnits) {
				messages.add(new FixableMessage(application.getString(R.string.preferred_angle_units_message, preferredAngleUnits.name(), angleUnits.name()), MessageType.warning, CalculatorFixableError.preferred_angle_units));
			}

			FixableMessagesDialog.showDialog(messages, application, true);

			CalculatorPreferences.Calculations.lastPreferredPreferencesCheck.putPreference(prefs, currentTime);
		}
	}

	private boolean isTimeForCheck(@Nonnull Long currentTime, @Nonnull SharedPreferences preferences) {
		final Long lastPreferredPreferencesCheckTime = CalculatorPreferences.Calculations.lastPreferredPreferencesCheck.getPreference(preferences);

		return currentTime - lastPreferredPreferencesCheckTime > PREFERRED_PREFS_INTERVAL_TIME;
	}

	@Override
	public void setPreferredAngleUnits() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
		setAngleUnits(CalculatorPreferences.Calculations.preferredAngleUnits.getPreference(preferences));
	}

	@Override
	public void setAngleUnits(@Nonnull AngleUnit angleUnit) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
		AndroidCalculatorEngine.Preferences.angleUnit.putPreference(preferences, angleUnit);

		Locator.getInstance().getNotifier().showMessage(new AndroidMessage(R.string.c_angle_units_changed_to, MessageType.info, application, angleUnit.name()));
	}

	@Override
	public void setPreferredNumeralBase() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
		setNumeralBase(CalculatorPreferences.Calculations.preferredNumeralBase.getPreference(preferences));
	}

	@Override
	public void setNumeralBase(@Nonnull NumeralBase numeralBase) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
		AndroidCalculatorEngine.Preferences.numeralBase.putPreference(preferences, numeralBase);

		Locator.getInstance().getNotifier().showMessage(new AndroidMessage(R.string.c_numeral_base_changed_to, MessageType.info, application, numeralBase.name()));
	}
}
