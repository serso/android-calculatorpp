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
public interface CalculatorLocator {

    void init(@Nonnull Calculator calculator,
              @Nonnull CalculatorEngine engine,
              @Nonnull CalculatorClipboard clipboard,
              @Nonnull CalculatorNotifier notifier,
              @Nonnull History history,
              @Nonnull CalculatorLogger logger,
              @Nonnull CalculatorPreferenceService preferenceService,
              @Nonnull CalculatorKeyboard keyboard,
              @Nonnull CalculatorPlotter plotter,
              @Nullable TextProcessor<TextProcessorEditorResult, String> editorTextProcessor);

    @Nonnull
    Calculator getCalculator();

    @Nonnull
    CalculatorEngine getEngine();

    @Nonnull
    Display getDisplay();

    @Nonnull
    Editor getEditor();

    @Nonnull
    CalculatorKeyboard getKeyboard();

    @Nonnull
    CalculatorClipboard getClipboard();

    @Nonnull
    CalculatorNotifier getNotifier();

    @Nonnull
    History getHistory();

    @Nonnull
    CalculatorLogger getLogger();

    @Nonnull
    CalculatorPlotter getPlotter();

    @Nonnull
    CalculatorPreferenceService getPreferenceService();
}
