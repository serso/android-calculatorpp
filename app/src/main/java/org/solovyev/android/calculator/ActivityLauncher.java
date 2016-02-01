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
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import jscl.math.Generic;
import jscl.math.function.Constant;
import org.solovyev.android.Activities;
import org.solovyev.android.calculator.about.CalculatorAboutActivity;
import org.solovyev.android.calculator.functions.CppFunction;
import org.solovyev.android.calculator.functions.EditFunctionFragment;
import org.solovyev.android.calculator.functions.FunctionsActivity;
import org.solovyev.android.calculator.history.CalculatorHistoryActivity;
import org.solovyev.android.calculator.matrix.CalculatorMatrixActivity;
import org.solovyev.android.calculator.operators.OperatorsActivity;
import org.solovyev.android.calculator.plot.CalculatorPlotter;
import org.solovyev.android.calculator.preferences.PreferencesActivity;
import org.solovyev.android.calculator.variables.CppVariable;
import org.solovyev.android.calculator.variables.EditVariableFragment;
import org.solovyev.android.calculator.variables.VariablesActivity;
import org.solovyev.android.calculator.variables.VariablesFragment;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public final class ActivityLauncher implements CalculatorEventListener {

    public ActivityLauncher() {
    }

    public static void showHistory(@Nonnull final Context context) {
        showHistory(context, false);
    }

    public static void showHistory(@Nonnull final Context context, boolean detached) {
        final Intent intent = new Intent(context, CalculatorHistoryActivity.class);
        Activities.addIntentFlags(intent, detached, context);
        context.startActivity(intent);
    }

    public static void showSettings(@Nonnull final Context context) {
        showSettings(context, false);
    }

    public static void showWidgetSettings(@Nonnull final Context context, boolean detached) {
        final Intent intent = PreferencesActivity.makeIntent(context, R.xml.preferences_widget, R.string.prefs_widget_title);
        Activities.addIntentFlags(intent, detached, context);
        context.startActivity(intent);
    }

    public static void showSettings(@Nonnull final Context context, boolean detached) {
        final Intent intent = new Intent(context, PreferencesActivity.class);
        Activities.addIntentFlags(intent, detached, context);
        context.startActivity(intent);
    }

    public static void showAbout(@Nonnull final Context context) {
        context.startActivity(new Intent(context, CalculatorAboutActivity.class));
    }

    public static void showFunctions(@Nonnull final Context context) {
        showFunctions(context, false);
    }

    public static void showFunctions(@Nonnull final Context context, boolean detached) {
        final Intent intent = new Intent(context, FunctionsActivity.class);
        Activities.addIntentFlags(intent, detached, context);
        context.startActivity(intent);
    }

    public static void showOperators(@Nonnull final Context context) {
        showOperators(context, false);
    }

    public static void showOperators(@Nonnull final Context context, boolean detached) {
        final Intent intent = new Intent(context, OperatorsActivity.class);
        Activities.addIntentFlags(intent, detached, context);
        context.startActivity(intent);
    }

    public static void showVars(@Nonnull final Context context) {
        showVars(context, false);
    }

    public static void showVars(@Nonnull final Context context, boolean detached) {
        final Intent intent = new Intent(context, VariablesActivity.class);
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

    public static void openApp(@Nonnull Context context) {
        final Intent intent = new Intent(context, CalculatorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void likeButtonPressed(@Nonnull final Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.cpp_share_link)));
        Activities.addIntentFlags(intent, false, context);
        context.startActivity(intent);
    }

    public static void showEvaluationError(@Nonnull Context context, @Nonnull final String errorMessage) {
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        final View errorMessageView = layoutInflater.inflate(R.layout.display_error_message, null);
        ((TextView) errorMessageView.findViewById(R.id.error_message_text_view)).setText(errorMessage);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton(R.string.c_cancel, null)
                .setView(errorMessageView);

        builder.create().show();
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
            case show_evaluation_error:
                final String errorMessage = (String) data;
                if (errorMessage != null) {
                    App.getUiThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            showEvaluationError(context, errorMessage);
                        }
                    });
                }
                break;
        }
    }
}