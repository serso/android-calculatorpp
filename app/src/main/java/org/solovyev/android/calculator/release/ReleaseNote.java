package org.solovyev.android.calculator.release;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class ReleaseNote {
    @NonNull
    public final String versionName;
    @StringRes
    public final int description;

    private ReleaseNote(@NonNull String versionName, int description) {
        this.versionName = versionName;
        this.description = description;
    }

    @NonNull
    public static ReleaseNote make(@NonNull String versionName, int description) {
        return new ReleaseNote(versionName, description);
    }
}
