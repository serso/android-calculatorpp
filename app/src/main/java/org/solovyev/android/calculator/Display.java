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

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.calculations.CalculationCancelledEvent;
import org.solovyev.android.calculator.calculations.CalculationFailedEvent;
import org.solovyev.android.calculator.calculations.CalculationFinishedEvent;
import org.solovyev.android.calculator.calculations.ConversionFailedEvent;
import org.solovyev.android.calculator.calculations.ConversionFinishedEvent;
import org.solovyev.android.calculator.errors.FixableErrorsActivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;

@Singleton
public class Display {

    @Nonnull
    private final Bus bus;
    @Inject
    Application application;
    @Inject
    Engine engine;
    @Inject
    Lazy<Clipboard> clipboard;
    @Inject
    Lazy<Notifier> notifier;
    @Inject
    Lazy<UiPreferences> uiPreferences;
    @Nullable
    private DisplayView view;
    @Nonnull
    private DisplayState state = DisplayState.empty();

    @Inject
    public Display(@Nonnull Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    @Subscribe
    public void onCopy(@Nonnull CopyOperation o) {
        copy();
    }

    void copy() {
        if (!state.valid) {
            return;
        }
        clipboard.get().setText(state.text);
        notifier.get().showMessage(R.string.cpp_text_copied);
    }

    @Subscribe
    public void onCalculationFinished(@Nonnull CalculationFinishedEvent e) {
        if (e.sequence < state.sequence) return;
        setState(DisplayState.createValid(e.operation, e.result, e.stringResult, e.sequence));
        if (!e.messages.isEmpty() && uiPreferences.get().isShowFixableErrorDialog()) {
            final Context context = view != null ? view.getContext() : application;
            FixableErrorsActivity.show(context, e.messages);
        }
    }

    @Subscribe
    public void onCalculationCancelled(@Nonnull CalculationCancelledEvent e) {
        if (e.sequence < state.sequence) return;
        final String error = CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error);
        setState(DisplayState.createError(e.operation, error, e.sequence));
    }

    @Subscribe
    public void onCalculationFailed(@Nonnull CalculationFailedEvent e) {
        if (e.sequence < state.sequence) return;
        final String error;
        if (e.exception instanceof ParseException) {
            error = e.exception.getLocalizedMessage();
        } else {
            error = CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error);
        }
        setState(DisplayState.createError(e.operation, error, e.sequence));
    }

    @Subscribe
    public void onConversionFinished(@NonNull ConversionFinishedEvent e) {
        if (e.state.sequence != state.sequence) return;
        final String result = e.numeralBase.getJsclPrefix() + e.result;
        setState(DisplayState.createValid(e.state.getOperation(), e.state.getResult(), result,
                e.state.sequence));
    }

    @Subscribe
    public void onConversionFailed(@NonNull ConversionFailedEvent e) {
        if (e.state.sequence != state.sequence) return;
        setState(DisplayState.createError(e.state.getOperation(),
                CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error),
                e.state.sequence));
    }

    public void clearView(@Nonnull DisplayView view) {
        Check.isMainThread();
        if (this.view != view) {
            return;
        }
        this.view.onDestroy();
        this.view = null;
    }

    public void setView(@Nonnull DisplayView view) {
        Check.isMainThread();
        this.view = view;
        this.view.setState(state);
        this.view.setEngine(engine);
    }

    @Nonnull
    public DisplayState getState() {
        Check.isMainThread();
        return state;
    }

    public void setState(@Nonnull DisplayState newState) {
        Check.isMainThread();

        final DisplayState oldState = state;
        state = newState;
        if (view != null) {
            view.setState(newState);
        }
        bus.post(new ChangedEvent(oldState, newState));
    }

    public static class CopyOperation {
    }

    public static class ChangedEvent {

        @Nonnull
        public final DisplayState oldState;

        @Nonnull
        public final DisplayState newState;

        public ChangedEvent(@Nonnull DisplayState oldState, @Nonnull DisplayState newState) {
            this.oldState = oldState;
            this.newState = newState;
        }
    }
}
