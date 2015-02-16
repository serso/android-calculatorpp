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
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import org.solovyev.android.view.VibratorContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 11/18/12
 * Time: 6:05 PM
 */
public class AndroidCalculatorKeyboard implements CalculatorKeyboard {

	@Nonnull
	private final CalculatorKeyboard calculatorKeyboard;

	@Nonnull
	private final Context context;

	@android.support.annotation.Nullable
	private org.solovyev.android.calculator.Vibrator vibrator;

	public AndroidCalculatorKeyboard(@Nonnull Application application,
									 @Nonnull CalculatorKeyboard calculatorKeyboard) {
		this.context = application;
		this.calculatorKeyboard = calculatorKeyboard;
	}

	@Override
	public boolean buttonPressed(@Nullable String text) {
		App.getGa().onButtonPressed(text);
		final boolean processed = calculatorKeyboard.buttonPressed(text);
		if (processed) {
			vibrate();
		}
		return processed;
	}

	private void vibrate() {
		if (vibrator == null) {
			vibrator = App.getVibrator();
		}
		vibrator.vibrate();
	}

	@Override
	public void roundBracketsButtonPressed() {
		vibrate();
		calculatorKeyboard.roundBracketsButtonPressed();
	}

	@Override
	public void pasteButtonPressed() {
		vibrate();
		calculatorKeyboard.pasteButtonPressed();
	}

	@Override
	public void clearButtonPressed() {
		vibrate();
		calculatorKeyboard.clearButtonPressed();
	}

	@Override
	public void copyButtonPressed() {
		vibrate();
		calculatorKeyboard.copyButtonPressed();
	}

	@Override
	public void moveCursorLeft() {
		vibrate();
		calculatorKeyboard.moveCursorLeft();
	}

	@Override
	public void moveCursorRight() {
		vibrate();
		calculatorKeyboard.moveCursorRight();
	}
}
