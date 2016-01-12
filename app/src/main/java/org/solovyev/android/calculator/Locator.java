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

import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.plot.CalculatorPlotter;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:45
 */
public class Locator implements CalculatorLocator {

    @Nonnull
    private static final Locator instance = new Locator();
    @Nonnull
    private CalculatorEngine calculatorEngine;
    @Nonnull
    private Calculator calculator;
    @Nonnull
    private Editor editor;
    @Nonnull
    private Display display;
    @Nonnull
    private CalculatorKeyboard calculatorKeyboard;
    @Nonnull
    private History history;
    @Nonnull
    private CalculatorNotifier calculatorNotifier = new DummyCalculatorNotifier();
    @Nonnull
    private CalculatorLogger calculatorLogger = new SystemOutCalculatorLogger();
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
                     @Nonnull History history,
                     @Nonnull CalculatorLogger logger,
                     @Nonnull CalculatorPreferenceService preferenceService,
                     @Nonnull CalculatorKeyboard keyboard,
                     @Nonnull CalculatorPlotter plotter,
                     @Nullable TextProcessor<TextProcessorEditorResult, String> editorTextProcessor) {

        this.calculator = calculator;
        this.calculatorEngine = engine;
        this.calculatorClipboard = clipboard;
        this.calculatorNotifier = notifier;
        this.history = history;
        this.calculatorLogger = logger;
        this.calculatorPreferenceService = preferenceService;
        this.calculatorPlotter = plotter;

        editor = new Editor(editorTextProcessor);
        display = new Display(this.calculator);
        calculatorKeyboard = keyboard;
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
    public Display getDisplay() {
        return display;
    }

    @Nonnull
    @Override
    public Editor getEditor() {
        return editor;
    }

    @Override
    @Nonnull
    public CalculatorKeyboard getKeyboard() {
        return calculatorKeyboard;
    }

    public static void setKeyboard(@Nonnull CalculatorKeyboard keyboard) {
        instance.calculatorKeyboard = keyboard;
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
    public History getHistory() {
        return history;
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
}
