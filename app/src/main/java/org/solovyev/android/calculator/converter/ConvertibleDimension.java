package org.solovyev.android.calculator.converter;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

public interface ConvertibleDimension {
    @NonNull
    Named<ConvertibleDimension> named(@NonNull Context context);

    @NonNull
    List<Convertible> getUnits();
}
