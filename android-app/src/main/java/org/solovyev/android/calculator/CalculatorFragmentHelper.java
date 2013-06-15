package org.solovyev.android.calculator;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 9/26/12
 * Time: 10:14 PM
 */
public interface CalculatorFragmentHelper {

	boolean isPane(@Nonnull Fragment fragment);

	void setPaneTitle(@Nonnull Fragment fragment, int titleResId);

	void onCreate(@Nonnull Fragment fragment);

	@Nonnull
	View onCreateView(@Nonnull Fragment fragment, @Nonnull LayoutInflater inflater, @Nullable ViewGroup container);

	void onViewCreated(@Nonnull Fragment fragment, @Nonnull View root);

	void onResume(@Nonnull Fragment fragment);

	void onPause(@Nonnull Fragment fragment);

	void onDestroy(@Nonnull Fragment fragment);
}
