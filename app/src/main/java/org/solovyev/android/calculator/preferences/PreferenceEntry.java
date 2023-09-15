package org.solovyev.android.calculator.preferences;

import android.content.Context;
import androidx.annotation.NonNull;

public interface PreferenceEntry {
    @NonNull
    CharSequence getName(@NonNull Context context);

    @NonNull
    CharSequence getId();
}
