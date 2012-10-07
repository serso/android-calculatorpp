package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 7:17 PM
 */
public enum AndroidFunctionCategory {

    trigonometric(R.string.c_fun_category_trig),
    hyperbolic_trigonometric(R.string.c_fun_category_hyper_trig),
    comparison(R.string.c_fun_category_comparison),
    my(R.string.c_fun_category_my),
    common(R.string.c_fun_category_common);

    private final int captionId;

    AndroidFunctionCategory(int captionId) {
        this.captionId = captionId;
    }

    public int getCaptionId() {
        return captionId;
    }

    @Nullable
    public AndroidFunctionCategory valueOf( @NotNull FunctionCategory functionCategory ) {
        for (AndroidFunctionCategory androidFunctionCategory : values()) {
            if ( androidFunctionCategory.name().equals(functionCategory.name()) ) {
                return androidFunctionCategory;
            }
        }

        return null;
    }
}
