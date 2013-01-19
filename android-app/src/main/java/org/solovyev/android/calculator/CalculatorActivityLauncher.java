package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils2;
import org.solovyev.android.App;
import org.solovyev.android.calculator.about.CalculatorAboutActivity;
import org.solovyev.android.calculator.function.FunctionEditDialogFragment;
import org.solovyev.android.calculator.help.CalculatorHelpActivity;
import org.solovyev.android.calculator.history.CalculatorHistoryActivity;
import org.solovyev.android.calculator.math.edit.*;
import org.solovyev.android.calculator.matrix.CalculatorMatrixActivity;
import org.solovyev.android.calculator.plot.CalculatorPlotActivity;
import org.solovyev.android.calculator.plot.CalculatorPlotter;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.StringUtils;

import java.util.List;

/**
 * User: serso
 * Date: 11/2/11
 * Time: 2:18 PM
 */
public final class CalculatorActivityLauncher implements CalculatorEventListener {

    public CalculatorActivityLauncher() {
    }

    public static void showHistory(@NotNull final Context context) {
        showHistory(context, false);
    }

	public static void showHistory(@NotNull final Context context, boolean detached) {
        final Intent intent = new Intent(context, CalculatorHistoryActivity.class);
        AndroidUtils2.addFlags(intent, detached, context);
        context.startActivity(intent);
	}

	public static void showHelp(@NotNull final Context context) {
		context.startActivity(new Intent(context, CalculatorHelpActivity.class));
	}

	public static void showSettings(@NotNull final Context context) {
        showSettings(context, false);
    }
	public static void showSettings(@NotNull final Context context, boolean detached) {
        final Intent intent = new Intent(context, CalculatorPreferencesActivity.class);
        AndroidUtils2.addFlags(intent, detached, context);
        context.startActivity(intent);
	}

    public static void showAbout(@NotNull final Context context) {
		context.startActivity(new Intent(context, CalculatorAboutActivity.class));
	}

	public static void showFunctions(@NotNull final Context context) {
        showFunctions(context, false);
    }

	public static void showFunctions(@NotNull final Context context, boolean detached) {
        final Intent intent = new Intent(context, CalculatorFunctionsActivity.class);
        AndroidUtils2.addFlags(intent, detached, context);
        context.startActivity(intent);
	}

	public static void showOperators(@NotNull final Context context) {
        showOperators(context, false);
    }

	public static void showOperators(@NotNull final Context context, boolean detached) {
        final Intent intent = new Intent(context, CalculatorOperatorsActivity.class);
        AndroidUtils2.addFlags(intent, detached, context);
        context.startActivity(intent);
	}

	public static void showVars(@NotNull final Context context) {
        showVars(context, false);
    }

    public static void showVars(@NotNull final Context context, boolean detached) {
        final Intent intent = new Intent(context, CalculatorVarsActivity.class);
        AndroidUtils2.addFlags(intent, detached, context);
        context.startActivity(intent);
	}

	public static void plotGraph(@NotNull final Context context){
		final Intent intent = new Intent();
		intent.setClass(context, CalculatorPlotActivity.class);
        AndroidUtils2.addFlags(intent, false, context);
		context.startActivity(intent);
	}

	public static void tryCreateVar(@NotNull final Context context) {
        final CalculatorDisplay display = Locator.getInstance().getDisplay();
        final CalculatorDisplayViewState viewState = display.getViewState();
        if (viewState.isValid() ) {
			final String varValue = viewState.getText();
			if (!StringUtils.isEmpty(varValue)) {
				if (CalculatorVarsFragment.isValidValue(varValue)) {
                    if (context instanceof SherlockFragmentActivity) {
                        VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newFromValue(varValue), ((SherlockFragmentActivity) context).getSupportFragmentManager());
                    } else {
                        final Intent intent = new Intent(context, CalculatorVarsActivity.class);
                        intent.putExtra(CalculatorVarsFragment.CREATE_VAR_EXTRA_STRING, varValue);
                        AndroidUtils2.addFlags(intent, false, context);
                        context.startActivity(intent);
                    }
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

    public static void tryCreateFunction(@NotNull final Context context) {
        final CalculatorDisplay display = Locator.getInstance().getDisplay();
        final CalculatorDisplayViewState viewState = display.getViewState();

        if (viewState.isValid() ) {
            final String functionValue = viewState.getText();
            if (!StringUtils.isEmpty(functionValue)) {

                FunctionEditDialogFragment.showDialog(FunctionEditDialogFragment.Input.newFromDisplay(viewState), context);

            } else {
                getNotifier().showMessage(R.string.empty_function_error, MessageType.error);
            }
        } else {
            getNotifier().showMessage(R.string.not_valid_result, MessageType.error);
        }
    }

    @NotNull
    private static CalculatorNotifier getNotifier() {
        return Locator.getInstance().getNotifier();
    }

    public static void tryPlot() {
        final CalculatorPlotter plotter = Locator.getInstance().getPlotter();
        final CalculatorDisplay display = Locator.getInstance().getDisplay();
        final CalculatorDisplayViewState viewState = display.getViewState();

        if (viewState.isValid() ) {
            final String functionValue = viewState.getText();
            final Generic expression = viewState.getResult();
            if (!StringUtils.isEmpty(functionValue) && expression != null) {
                if ( plotter.isPlotPossibleFor(expression) ) {
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

    public static void openApp(@NotNull Context context) {
        final Intent intent = new Intent(context, CalculatorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void likeButtonPressed(@NotNull final Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(CalculatorApplication.FACEBOOK_APP_URL));
        AndroidUtils2.addFlags(intent, false, context);
        context.startActivity(intent);
    }

    public static void showCalculationMessagesDialog(@NotNull Context context, @NotNull List<Message> messages) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if ( CalculatorPreferences.Calculations.showCalculationMessagesDialog.getPreference(prefs) ) {
            FixableMessagesDialog.showDialogForMessages(messages, context, true);
        }
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        final Context context;

        final Object source = calculatorEventData.getSource();
        if ( source instanceof Context ) {
            context = ((Context) source);
        } else {
            context = App.getInstance().getApplication();
        }

        switch (calculatorEventType){
            case show_create_matrix_dialog:
                App.getInstance().getUiThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        final Intent intent = new Intent(context, CalculatorMatrixActivity.class);
                        AndroidUtils2.addFlags(intent, false, context);
                        context.startActivity(intent);
                    }
                });
                break;
            case show_create_var_dialog:
                App.getInstance().getUiThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        CalculatorActivityLauncher.tryCreateVar(context);
                    }
                });
                break;
            case show_create_function_dialog:
                App.getInstance().getUiThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        CalculatorActivityLauncher.tryCreateFunction(context);
                    }
                });
                break;
            case show_evaluation_error:
                final String errorMessage = (String) data;
                if (errorMessage != null) {
                    App.getInstance().getUiThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            showEvaluationError(context, errorMessage);
                        }
                    });
                }
                break;
        }
    }

    public static void showEvaluationError(@NotNull Context context, @NotNull final String errorMessage) {
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        final View errorMessageView = layoutInflater.inflate(R.layout.display_error_message, null);
        ((TextView) errorMessageView.findViewById(R.id.error_message_text_view)).setText(errorMessage);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton(R.string.c_cancel, null)
                .setView(errorMessageView);

        builder.create().show();
    }
}
