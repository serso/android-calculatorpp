package org.solovyev.android.calculator;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import javax.annotation.Nonnull;

public class BaseActivity extends SherlockFragmentActivity {

	@Nonnull
	protected final ActivityUi ui;

	public BaseActivity(@Nonnull ActivityUi ui) {
		this.ui = ui;
	}

	public BaseActivity(@LayoutRes int layout) {
		this(layout, "Activity");
	}

	public BaseActivity(@LayoutRes int layout, @Nonnull String logTag) {
		this.ui = CalculatorApplication.getInstance().createActivityHelper(layout, logTag);
	}

	@Nonnull
	public ActivityUi getUi() {
		return ui;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ui.onCreate(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ui.onSaveInstanceState(this, outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		ui.onStart(this);
	}

	@Override
	protected void onStop() {
		ui.onStop(this);
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ui.onResume(this);
	}

	@Override
	protected void onPause() {
		this.ui.onPause(this);
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		ui.onDestroy(this);
	}
}
