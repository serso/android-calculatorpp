package org.solovyev.android.calculator.entities;

import androidx.annotation.NonNull;

import org.solovyev.common.math.MathEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Entities {

    private Entities() {
    }

    @Nullable
    public static <E extends MathEntity, C extends Category<E>> Category<E> getCategory(@Nonnull E entity, @NonNull C[] categories) {
        for (C category : categories) {
            if (category.isInCategory(entity)) {
                return category;
            }
        }

        return null;
    }
}
