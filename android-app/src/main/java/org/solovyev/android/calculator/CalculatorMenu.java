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

package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.actionbarsherlock.view.MenuItem;

import javax.annotation.Nonnull;

import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;
import org.solovyev.android.menu.LabeledMenuItem;

/**
 * User: serso
 * Date: 4/23/12
 * Time: 2:25 PM
 */
enum CalculatorMenu implements LabeledMenuItem<MenuItem> {

	settings(R.string.c_settings) {
		@Override
		public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
			CalculatorActivityLauncher.showSettings(context);
		}
	},

	history(R.string.c_history) {
		@Override
		public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
			CalculatorActivityLauncher.showHistory(context);
		}
	},

	plotter(R.string.cpp_plotter) {
		@Override
		public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
			Locator.getInstance().getPlotter().plot();
		}
	},

	conversion_tool(R.string.c_conversion_tool) {
		@Override
		public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
			new NumeralBaseConverterDialog(null).show(context);
		}
	},

	exit(R.string.c_exit) {
		@Override
		public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
			if (context instanceof Activity) {
				((Activity) context).finish();
			} else {
				Log.e(CalculatorActivity.TAG, "Activity menu used with context");
			}
		}
	},

	about(R.string.c_about) {
		@Override
		public void onClick(@Nonnull MenuItem data, @Nonnull Context context) {
			CalculatorActivityLauncher.showAbout(context);
		}
	};

	private final int captionResId;

	private CalculatorMenu(int captionResId) {
		this.captionResId = captionResId;
	}

	@Nonnull
	@Override
	public String getCaption(@Nonnull Context context) {
		return context.getString(captionResId);
	}
}
