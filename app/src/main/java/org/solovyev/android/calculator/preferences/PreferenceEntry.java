package org.solovyev.android.calculator.preferences;

import android.content.Context;
import android.support.annotation.NonNull;

public interface PreferenceEntry {
    @NonNull
    CharSequence getName(@NonNull Context context);

    @NonNull
    CharSequence getId();
}
