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
import android.text.TextUtils;
import dagger.Lazy;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.CustomFunction;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.about.AboutActivity;
import org.solovyev.android.calculator.functions.CppFunction;
import org.solovyev.android.calculator.functions.EditFunctionFragment;
import org.solovyev.android.calculator.functions.FunctionsActivity;
import org.solovyev.android.calculator.history.HistoryActivity;
import org.solovyev.android.calculator.operators.OperatorsActivity;
import org.solovyev.android.calculator.plot.ExpressionFunction;
import org.solovyev.android.calculator.plot.PlotActivity;
import org.solovyev.android.calculator.preferences.PreferencesActivity;
import org.solovyev.android.calculator.variables.CppVariable;
import org.solovyev.android.calculator.variables.EditVariableFragment;
import org.solovyev.android.calculator.variables.VariablesActivity;
import org.solovyev.android.plotter.Plotter;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public final class ActivityLauncher {

    @Inject
    Application application;
    @Inject
    Lazy<Plotter> plotter;
    @Inject
    Lazy<ErrorReporter> errorReporter;
    @Inject
    Lazy<Display> display;
    @Inject
    Lazy<VariablesRegistry> variablesRegistry;
    @Inject
    Notifier notifier;
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
        App.addIntentFlags(intent, detached, context);
        context.startActivity(intent);
    }

    @Nonnull
    private static Notifier getNotifier() {
        return ((CalculatorApplication) App.getApplication()).notifier;
    }

    public void plotDisplayedExpression() {
        final DisplayState state = display.get().getState();
        if (!state.valid) {
            getNotifier().showMessage(R.string.not_valid_result, MessageType.error);
            return;
        }
        plot(state.getResult());
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

    void plot(@Nullable Generic expression) {
        if (expression == null) {
            notifier.showMessage(R.string.cpp_plot_empty_function_error);
            return;
        }
        final String content = expression.toString();
        if (TextUtils.isEmpty(content)) {
            notifier.showMessage(R.string.cpp_plot_empty_function_error);
            return;
        }
        final List<String> parameters = new ArrayList<>();
        for (Constant parameter : expression.getUndefinedConstants(variablesRegistry.get())) {
            parameters.add(parameter.getName());
        }
        if (parameters.size() > 2) {
            notifier.showMessage(R.string.cpp_plot_too_many_variables);
            return;
        }

        try {
            final CustomFunction f = new CustomFunction.Builder().setName("").setParameterNames(parameters).setContent(content).create();
            final ExpressionFunction ef = new ExpressionFunction(f, false);
            plotter.get().add(ef);
            showPlotter();
        } catch (RuntimeException e) {
            errorReporter.get().onException(e);
            notifier.showMessage(e.getLocalizedMessage());
        }
    }

    public boolean canPlot(@Nullable Generic expression) {
        if (expression == null || TextUtils.isEmpty(expression.toString())) {
            return false;
        }
        if (expression.getUndefinedConstants(variablesRegistry.get()).size() > 2) {
            return false;
        }
        return true;
    }

    public void showConstantEditor() {
        final DisplayState state = display.get().getState();
        if (!state.valid) {
            notifier.showMessage(R.string.not_valid_result);
            return;
        }
        EditVariableFragment.showDialog(CppVariable.builder("").withValue(state.text).build(), getContext());
    }

    public void showFunctionEditor() {
        final DisplayState state = display.get().getState();
        if (!state.valid) {
            notifier.showMessage(R.string.not_valid_result);
            return;
        }
        final CppFunction.Builder builder = CppFunction.builder("", state.text);
        final Generic expression = state.getResult();
        if (expression != null) {
            for (Constant constant : expression.getUndefinedConstants(variablesRegistry.get())) {
                builder.withParameter(constant.getName());
            }
        }
        EditFunctionFragment.show(builder.build(), getContext());
    }
}
