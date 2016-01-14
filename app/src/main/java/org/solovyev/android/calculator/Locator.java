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

import org.solovyev.android.calculator.plot.CalculatorPlotter;

import javax.annotation.Nonnull;

public class Locator implements CalculatorLocator {

    @Nonnull
    private static final Locator instance = new Locator();
    @Nonnull
    private CalculatorEngine calculatorEngine;
    @Nonnull
    private Calculator calculator;
    @Nonnull
    private Keyboard keyboard;
    @Nonnull
    private CalculatorNotifier calculatorNotifier = new DummyCalculatorNotifier();
    @Nonnull
    private ErrorReporter errorReporter = new SystemErrorReporter();
    @Nonnull
    private CalculatorClipboard calculatorClipboard = new DummyCalculatorClipboard();
    @Nonnull
    private CalculatorPreferenceService calculatorPreferenceService;

    @Nonnull
    private CalculatorPlotter calculatorPlotter;

    public Locator() {
    }

    @Nonnull
    public static CalculatorLocator getInstance() {
        return instance;
    }

    @Override
    public void init(@Nonnull Calculator calculator,
                     @Nonnull CalculatorEngine engine,
                     @Nonnull CalculatorClipboard clipboard,
                     @Nonnull CalculatorNotifier notifier,
                     @Nonnull ErrorReporter errorReporter,
                     @Nonnull CalculatorPreferenceService preferenceService,
                     @Nonnull Keyboard keyboard,
                     @Nonnull CalculatorPlotter plotter) {

        this.calculator = calculator;
        this.calculatorEngine = engine;
        this.calculatorClipboard = clipboard;
        this.calculatorNotifier = notifier;
        this.errorReporter = errorReporter;
        this.calculatorPreferenceService = preferenceService;
        this.calculatorPlotter = plotter;

        this.keyboard = keyboard;
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
    public Keyboard getKeyboard() {
        return keyboard;
    }

    public static void setKeyboard(@Nonnull Keyboard keyboard) {
        instance.keyboard = keyboard;
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
    public ErrorReporter getErrorReporter() {
        return errorReporter;
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
}
