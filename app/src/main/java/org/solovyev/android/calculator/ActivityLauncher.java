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
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.solovyev.android.Activities;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.about.AboutActivity;
import org.solovyev.android.calculator.functions.CppFunction;
import org.solovyev.android.calculator.functions.EditFunctionFragment;
import org.solovyev.android.calculator.functions.FunctionsActivity;
import org.solovyev.android.calculator.history.HistoryActivity;
import org.solovyev.android.calculator.matrix.CalculatorMatrixActivity;
import org.solovyev.android.calculator.operators.OperatorsActivity;
import org.solovyev.android.calculator.plot.CalculatorPlotter;
import org.solovyev.android.calculator.plot.PlotActivity;
import org.solovyev.android.calculator.preferences.PreferencesActivity;
import org.solovyev.android.calculator.variables.CppVariable;
import org.solovyev.android.calculator.variables.EditVariableFragment;
import org.solovyev.android.calculator.variables.VariablesActivity;
import org.solovyev.android.calculator.variables.VariablesFragment;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.Strings;

import jscl.math.Generic;
import jscl.math.function.Constant;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ActivityLauncher implements CalculatorEventListener {

    @Inject
    Application application;
    @Nullable
    private CalculatorActivity activity;

    @Inject
    public ActivityLauncher() {
    }

    private static void show(@Nonnull final Context context, @NonNull Class<? extends Activity> activity) {
        show(context, new Intent(context, activity));
    }

    private static void show(@Nonnull Context context, @NonNull Intent intent) {
        final boolean detached = !(context instanceof Activity);
        Activities.addIntentFlags(intent, detached, context);
        context.startActivity(intent);
    }

    public static void tryCreateVar(@Nonnull final Context context) {
        final Display display = App.getDisplay();
        final DisplayState state = display.getState();
        if (state.valid) {
            final String value = state.text;
            if (!Strings.isEmpty(value)) {
                if (VariablesFragment.isValidValue(value)) {
                    EditVariableFragment.showDialog(CppVariable.builder("").withValue(value).build(), context);
                } else {
                    getNotifier().showMessage(R.string.c_value_is_not_a_number, MessageType.error);
                }
            } else {
                getNotifier().showMessage(R.string.empty_var_error, MessageType.error);
            }
        } else {
            getNotifier().showMessage(R.string.not_valid_result, MessageType.error);
        }
    }

    public static void tryCreateFunction(@Nonnull final Context context) {
        final Display display = App.getDisplay();
        final DisplayState viewState = display.getState();

        if (viewState.valid) {
            final String functionBody = viewState.text;
            if (!Strings.isEmpty(functionBody)) {
                final CppFunction.Builder builder = CppFunction.builder("", functionBody);
                final Generic generic = viewState.getResult();
                if (generic != null) {
                    final Set<Constant> constants = CalculatorUtils.getNotSystemConstants(generic);
                    for (Constant constant : constants) {
                        builder.withParameter(constant.getName());
                    }
                }
                EditFunctionFragment.show(builder.build(), context);
            } else {
                getNotifier().showMessage(R.string.empty_function_error, MessageType.error);
            }
        } else {
            getNotifier().showMessage(R.string.not_valid_result, MessageType.error);
        }
    }

    @Nonnull
    private static Notifier getNotifier() {
        return ((CalculatorApplication) App.getApplication()).notifier;
    }

    public static void tryPlot() {
        final CalculatorPlotter plotter = Locator.getInstance().getPlotter();
        final Display display = App.getDisplay();
        final DisplayState viewState = display.getState();

        if (viewState.valid) {
            final String functionValue = viewState.text;
            final Generic expression = viewState.getResult();
            if (!Strings.isEmpty(functionValue) && expression != null) {
                if (plotter.isPlotPossibleFor(expression)) {
                    plotter.plot(expression);
                } else {
                    getNotifier().showMessage(R.string.cpp_plot_too_many_variables, MessageType.error);
                }
            } else {
                getNotifier().showMessage(R.string.cpp_plot_empty_function_error, MessageType.error);
            }
        } else {
            getNotifier().showMessage(R.string.not_valid_result, MessageType.error);
        }
    }

    public void showHistory() {
        show(getContext(), HistoryActivity.getClass(getContext()));
    }

    public void showSettings() {
        show(getContext(), PreferencesActivity.getClass(getContext()));
    }

    public void showWidgetSettings() {
        final Context context = getContext();
        show(context, PreferencesActivity.makeIntent(context, R.xml.preferences_widget,
            R.string.prefs_widget_title));
    }

    public void showOperators() {
        show(getContext(), OperatorsActivity.getClass(getContext()));
    }

    public void showAbout() {
        show(getContext(), AboutActivity.getClass(getContext()));
    }

    public void showPlotter() {
        show(getContext(), PlotActivity.class);
    }

    public void openFacebook() {
        final Uri uri = Uri.parse(application.getString(R.string.cpp_share_link));
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }

    public void setActivity(@Nullable CalculatorActivity activity) {
        Check.isNull(this.activity);
        this.activity = activity;
    }

    public void clearActivity(@Nullable CalculatorActivity activity) {
        Check.isNotNull(this.activity);
        Check.equals(this.activity, activity);
        this.activity = null;
    }

    public void show(@NonNull Class<HistoryActivity> activity) {
        show(getContext(), activity);
    }

    @NonNull
    private Context getContext() {
        return activity == null ? application : activity;
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        final Context context;

        final Object source = calculatorEventData.getSource();
        if (source instanceof Context) {
            context = ((Context) source);
        } else {
            context = App.getApplication();
        }

        switch (calculatorEventType) {
            case show_create_matrix_dialog:
                App.getUiThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        final Intent intent = new Intent(context, CalculatorMatrixActivity.class);
                        Activities.addIntentFlags(intent, false, context);
                        context.startActivity(intent);
                    }
                });
                break;
            case show_create_var_dialog:
                App.getUiThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ActivityLauncher.tryCreateVar(context);
                    }
                });
                break;
            case show_create_function_dialog:
                App.getUiThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ActivityLauncher.tryCreateFunction(context);
                    }
                });
                break;
        }
    }

    public void showFunctions() {
        show(getContext(), FunctionsActivity.getClass(getContext()));
    }

    public void showVariables() {
        show(getContext(), VariablesActivity.getClass(getContext()));
    }

    public void openApp() {
        final Context context = getContext();
        final Intent intent = new Intent(context, CalculatorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
