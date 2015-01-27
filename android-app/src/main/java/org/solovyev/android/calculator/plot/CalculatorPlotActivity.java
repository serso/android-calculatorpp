/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import android.content.Intent;
import android.os.Bundle;

import org.solovyev.android.calculator.EmptyActivity;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD;

/**
 * User: serso
 * Date: 9/30/12
 * Time: 4:56 PM
 */
public class CalculatorPlotActivity extends EmptyActivity {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();

		final Bundle arguments;
		if (intent != null) {
			arguments = intent.getExtras();
		} else {
			arguments = null;
		}

		getSupportActionBar().setNavigationMode(NAVIGATION_MODE_STANDARD);
		getUi().setFragment(this, getPlotterFragmentType(), arguments, R.id.main_layout);
	}

	@Nonnull
	public static CalculatorFragmentType getPlotterFragmentType() {
		return CalculatorFragmentType.plotter;
	}
}
