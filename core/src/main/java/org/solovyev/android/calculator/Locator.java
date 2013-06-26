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

import org.solovyev.android.calculator.external.CalculatorExternalListenersContainer;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.plot.CalculatorPlotter;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:45
 */
public class Locator implements CalculatorLocator {

	@Nonnull
	private CalculatorEngine calculatorEngine;

	@Nonnull
	private Calculator calculator;

	@Nonnull
	private CalculatorEditor calculatorEditor;

	@Nonnull
	private CalculatorDisplay calculatorDisplay;

	@Nonnull
	private CalculatorKeyboard calculatorKeyboard;

	@Nonnull
	private CalculatorHistory calculatorHistory;

	@Nonnull
	private CalculatorNotifier calculatorNotifier = new DummyCalculatorNotifier();

	@Nonnull
	private CalculatorLogger calculatorLogger = new SystemOutCalculatorLogger();

	@Nonnull
	private CalculatorClipboard calculatorClipboard = new DummyCalculatorClipboard();

	@Nonnull
	private static final CalculatorLocator instance = new Locator();

	@Nonnull
	private CalculatorPreferenceService calculatorPreferenceService;

	/*@Nonnull*/
	private CalculatorExternalListenersContainer calculatorExternalListenersContainer;

	@Nonnull
	private CalculatorPlotter calculatorPlotter;

	public Locator() {
	}

	@Override
	public void init(@Nonnull Calculator calculator,
					 @Nonnull CalculatorEngine engine,
					 @Nonnull CalculatorClipboard clipboard,
					 @Nonnull CalculatorNotifier notifier,
					 @Nonnull CalculatorHistory history,
					 @Nonnull CalculatorLogger logger,
					 @Nonnull CalculatorPreferenceService preferenceService,
					 @Nonnull CalculatorKeyboard keyboard,
					 @Nonnull CalculatorExternalListenersContainer externalListenersContainer,
					 @Nonnull CalculatorPlotter plotter) {

		this.calculator = calculator;
		this.calculatorEngine = engine;
		this.calculatorClipboard = clipboard;
		this.calculatorNotifier = notifier;
		this.calculatorHistory = history;
		this.calculatorLogger = logger;
		this.calculatorPreferenceService = preferenceService;
		this.calculatorExternalListenersContainer = externalListenersContainer;
		this.calculatorPlotter = plotter;

		calculatorEditor = new CalculatorEditorImpl(this.calculator);
		calculatorDisplay = new CalculatorDisplayImpl(this.calculator);
		calculatorKeyboard = keyboard;
	}

	@Nonnull
	public static CalculatorLocator getInstance() {
		return instance;
	}

	@Nonnull
	@Override
	public CalculatorEngine getEngine() {
		return calculatorEngine;
	}

	@Nonnull
	@Override
	public Calculator getCalculator() {
		return this.calculator;
	}

	@Override
	@Nonnull
	public CalculatorDisplay getDisplay() {
		return calculatorDisplay;
	}

	@Nonnull
	@Override
	public CalculatorEditor getEditor() {
		return calculatorEditor;
	}

	@Override
	@Nonnull
	public CalculatorKeyboard getKeyboard() {
		return calculatorKeyboard;
	}

	@Override
	@Nonnull
	public CalculatorClipboard getClipboard() {
		return calculatorClipboard;
	}

	@Override
	@Nonnull
	public CalculatorNotifier getNotifier() {
		return calculatorNotifier;
	}

	@Override
	@Nonnull
	public CalculatorHistory getHistory() {
		return calculatorHistory;
	}

	@Override
	@Nonnull
	public CalculatorLogger getLogger() {
		return calculatorLogger;
	}

	@Nonnull
	@Override
	public CalculatorPlotter getPlotter() {
		return calculatorPlotter;
	}

	@Nonnull
	@Override
	public CalculatorPreferenceService getPreferenceService() {
		return this.calculatorPreferenceService;
	}

	@Override
	/*@Nonnull*/
	public CalculatorExternalListenersContainer getExternalListenersContainer() {
		return calculatorExternalListenersContainer;
	}
}
