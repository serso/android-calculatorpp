package org.solovyev.android.calculator;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/26/12
 * Time: 10:14 PM
 */
public interface CalculatorFragmentHelper {

    boolean isPane(@NotNull Fragment fragment);

    void setPaneTitle(@NotNull Fragment fragment, int titleResId);

    void onCreate(@NotNull Fragment fragment);

    @NotNull
    View onCreateView(@NotNull Fragment fragment, @NotNull LayoutInflater inflater, @Nullable ViewGroup container);

    void onViewCreated(@NotNull Fragment fragment, @NotNull View root);

    void onResume(@NotNull Fragment fragment);

    void onPause(@NotNull Fragment fragment);

    void onDestroy(@NotNull Fragment fragment);
}
