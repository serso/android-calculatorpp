package org.solovyev.android.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/25/12
 * Time: 10:31 PM
 */
public interface CalculatorActivityHelper {

    void onCreate(@NotNull SherlockFragmentActivity activity, @Nullable Bundle savedInstanceState);
    void onCreate(@NotNull Activity activity, @Nullable Bundle savedInstanceState);

    void onSaveInstanceState(@NotNull SherlockFragmentActivity activity, @NotNull Bundle outState);
    void onSaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState);

    int getLayoutId();

    @NotNull
    CalculatorPreferences.Gui.Theme getTheme();

    void onResume(@NotNull SherlockFragmentActivity activity);
    void onResume(@NotNull Activity activity);

    void onDestroy(@NotNull SherlockFragmentActivity activity);

    void addTab(@NotNull SherlockFragmentActivity activity,
                @NotNull String tag,
                @NotNull Class<? extends Fragment> fragmentClass,
                @Nullable Bundle fragmentArgs,
                int captionResId, int parentViewId);

    void restoreSavedTab(@NotNull SherlockFragmentActivity activity);

    void logDebug(@NotNull String message);

    void onPause(@NotNull SherlockFragmentActivity activity);
}
