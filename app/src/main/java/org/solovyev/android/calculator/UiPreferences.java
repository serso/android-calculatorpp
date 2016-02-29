package org.solovyev.android.calculator;

import android.support.annotation.NonNull;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.IntegerPreference;
import org.solovyev.android.prefs.Preference;

final class UiPreferences {
    @NonNull
    public static final Preference<Integer> opened = IntegerPreference.of("opened", 0);
    @NonNull
    public static final Preference<Integer> version = IntegerPreference.of("version", IntegerPreference.DEF_VALUE);
    @NonNull
    public static final Preference<Boolean> rateUsShown = BooleanPreference.of("rateUsShown", false);
}
