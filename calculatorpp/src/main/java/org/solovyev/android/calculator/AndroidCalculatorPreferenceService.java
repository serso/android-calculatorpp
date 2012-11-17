package org.solovyev.android.calculator;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import jscl.AngleUnit;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.msg.AndroidMessage;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 7:46 PM
 */
public class AndroidCalculatorPreferenceService implements CalculatorPreferenceService {

    @NotNull
    private final Application application;

    public AndroidCalculatorPreferenceService(@NotNull Application application) {
        this.application = application;
    }

    @Override
    public void setAngleUnits(@NotNull AngleUnit angleUnit) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
        AndroidCalculatorEngine.Preferences.angleUnit.putPreference(preferences, angleUnit);

        CalculatorLocatorImpl.getInstance().getNotifier().showMessage(new AndroidMessage(R.string.c_angle_units_changed_to, MessageType.info, application, angleUnit.name()));
    }
}
