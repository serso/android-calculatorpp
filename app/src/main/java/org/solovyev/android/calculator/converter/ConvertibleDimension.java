package org.solovyev.android.calculator.converter;

import android.content.Context;
import androidx.annotation.NonNull;

import org.solovyev.android.calculator.Named;

import java.util.List;

public interface ConvertibleDimension {
    @NonNull
    Named<ConvertibleDimension> named(@NonNull Context context);

    @NonNull
    List<Convertible> getUnits();
}
