package org.solovyev.android.calculator;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 03.10.12
 * Time: 14:07
 */
public abstract class CalculatorFragmentActivity extends SherlockFragmentActivity {

	@NotNull
	private final CalculatorActivityHelper activityHelper;

	protected CalculatorFragmentActivity() {
		this(R.layout.main_empty);
	}

	protected CalculatorFragmentActivity(int layoutResId) {
		this.activityHelper = CalculatorApplication.getInstance().createActivityHelper(layoutResId, getClass().getSimpleName());
	}

	@NotNull
	protected CalculatorActivityHelper getActivityHelper() {
		return activityHelper;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activityHelper.onCreate(this, savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		activityHelper.onSaveInstanceState(this, outState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		activityHelper.onResume(this);
	}

	@Override
	protected void onPause() {
		this.activityHelper.onPause(this);

		super.onPause();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		activityHelper.onDestroy(this);
	}
}
