/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.history.CalculatorHistoryActivity;

/**
 * User: serso
 * Date: 12/21/11
 * Time: 10:33 PM
 */
public class CalculatorFunctionsActivity extends SherlockFragmentActivity implements CalculatorEventListener {

	@Nonnull
	private final CalculatorActivityHelper activityHelper = CalculatorApplication.getInstance().createActivityHelper(R.layout.main_empty, CalculatorHistoryActivity.class.getSimpleName());

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activityHelper.onCreate(this, savedInstanceState);

		final Bundle bundle;

		final Intent intent = getIntent();
		if (intent != null) {
			bundle = intent.getExtras();
		} else {
			bundle = null;
		}

		final CalculatorFragmentType fragmentType = CalculatorFragmentType.functions;

		for (FunctionCategory category : FunctionCategory.getCategoriesByTabOrder()) {
			final AndroidFunctionCategory androidCategory = AndroidFunctionCategory.valueOf(category);
			if (androidCategory != null) {

				final Bundle fragmentParameters;

				if (category == FunctionCategory.my && bundle != null) {
					AbstractMathEntityListFragment.putCategory(bundle, category.name());
					fragmentParameters = bundle;
				} else {
					fragmentParameters = AbstractMathEntityListFragment.createBundleFor(category.name());
				}

				activityHelper.addTab(this, fragmentType.createSubFragmentTag(category.name()), fragmentType.getFragmentClass(), fragmentParameters, androidCategory.getCaptionId(), R.id.main_layout);
			} else {
				Log.e(CalculatorFunctionsActivity.class.getSimpleName(), "Unable to find android function category for " + category);
			}
		}

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

		this.activityHelper.onDestroy(this);
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
		switch (calculatorEventType) {
			case use_function:
				this.finish();
				break;
		}
	}
}
