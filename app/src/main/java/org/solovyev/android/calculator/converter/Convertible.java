package org.solovyev.android.calculator.converter;

import android.content.Context;
import android.support.annotation.NonNull;

import org.solovyev.android.calculator.Named;

interface Convertible {
    @NonNull
    String convert(@NonNull Convertible to, @NonNull String value) throws NumberFormatException;

    @NonNull
    Named<Convertible> named(@NonNull Context context);
}
