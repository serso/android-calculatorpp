package org.solovyev.android.calculator.converter;

import android.content.Context;
import android.support.annotation.NonNull;

interface Convertible {
    @NonNull
    String convert(@NonNull Convertible to, @NonNull String value);

    @NonNull
    Named<Convertible> named(@NonNull Context context);
}
