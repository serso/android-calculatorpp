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
import jscl.math.function.Constant;
import org.achartengine.ChartFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.about.CalculatorAboutActivity;
import org.solovyev.android.calculator.function.FunctionEditDialogFragment;
import org.solovyev.android.calculator.help.CalculatorHelpActivity;
import org.solovyev.android.calculator.history.CalculatorHistoryActivity;
import org.solovyev.android.calculator.math.edit.*;
import org.solovyev.android.calculator.plot.CalculatorPlotActivity;
import org.solovyev.android.calculator.plot.CalculatorPlotFragment;
import org.solovyev.android.calculator.plot.PlotInput;
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
        addFlags(intent, detached);
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
        addFlags(intent, detached);
        context.startActivity(intent);
	}

    private static void addFlags(@NotNull Intent intent, boolean detached) {
        int flags = Intent.FLAG_ACTIVITY_NEW_TASK;

        if (detached) {
            flags = flags | Intent.FLAG_ACTIVITY_NO_HISTORY;
        }

        intent.setFlags(flags);

    }

    public static void showAbout(@NotNull final Context context) {
		context.startActivity(new Intent(context, CalculatorAboutActivity.class));
	}

	public static void showFunctions(@NotNull final Context context) {
        showFunctions(context, false);
    }

	public static void showFunctions(@NotNull final Context context, boolean detached) {
        final Intent intent = new Intent(context, CalculatorFunctionsActivity.class);
        addFlags(intent, detached);
        context.startActivity(intent);
	}

	public static void showOperators(@NotNull final Context context) {
        showOperators(context, false);
    }

	public static void showOperators(@NotNull final Context context, boolean detached) {
        final Intent intent = new Intent(context, CalculatorOperatorsActivity.class);
        addFlags(intent, detached);
        context.startActivity(intent);
	}

	public static void showVars(@NotNull final Context context) {
        showVars(context, false);
    }

    public static void showVars(@NotNull final Context context, boolean detached) {
        final Intent intent = new Intent(context, CalculatorVarsActivity.class);
        addFlags(intent, detached);
        context.startActivity(intent);
	}

	public static void plotGraph(@NotNull final Context context, @NotNull Generic generic, @NotNull Constant constant){
		final Intent intent = new Intent();
		intent.putExtra(ChartFactory.TITLE, context.getString(R.string.c_graph));
		intent.putExtra(CalculatorPlotFragment.INPUT, new CalculatorPlotFragment.Input(generic.toString(), constant.getName()));
		intent.setClass(context, CalculatorPlotActivity.class);
		context.startActivity(intent);
	}

	public static void createVar(@NotNull final Context context, @NotNull CalculatorDisplay calculatorDisplay) {
        final CalculatorDisplayViewState viewState = calculatorDisplay.getViewState();
        if (viewState.isValid() ) {
			final String varValue = viewState.getText();
			if (!StringUtils.isEmpty(varValue)) {
				if (CalculatorVarsFragment.isValidValue(varValue)) {
                    if (context instanceof SherlockFragmentActivity) {
                        VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newFromValue(varValue), ((SherlockFragmentActivity) context).getSupportFragmentManager());
                    } else {
                        final Intent intent = new Intent(context, CalculatorVarsActivity.class);
                        intent.putExtra(CalculatorVarsFragment.CREATE_VAR_EXTRA_STRING, varValue);
                        context.startActivity(intent);
                    }
                } else {
                    Locator.getInstance().getNotifier().showMessage(R.string.c_value_is_not_a_number, MessageType.error);
				}
			} else {
                Locator.getInstance().getNotifier().showMessage(R.string.empty_var_error, MessageType.error);
			}
		} else {
            Locator.getInstance().getNotifier().showMessage(R.string.not_valid_result, MessageType.error);
		}
	}

    public static void createFunction(@NotNull final Context context, @NotNull CalculatorDisplay calculatorDisplay) {
        final CalculatorDisplayViewState viewState = calculatorDisplay.getViewState();

        if (viewState.isValid() ) {
            final String functionValue = viewState.getText();
            if (!StringUtils.isEmpty(functionValue)) {

                FunctionEditDialogFragment.showDialog(FunctionEditDialogFragment.Input.newFromDisplay(viewState), context);

            } else {
                Locator.getInstance().getNotifier().showMessage(R.string.empty_function_error, MessageType.error);
            }
        } else {
            Locator.getInstance().getNotifier().showMessage(R.string.not_valid_result, MessageType.error);
        }
    }

    public static void openApp(@NotNull Context context) {
        final Intent intent = new Intent(context, CalculatorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void likeButtonPressed(@NotNull final Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(CalculatorApplication.FACEBOOK_APP_URL));
        addFlags(intent, false);
        context.startActivity(intent);
    }

    public static void showCalculationMessagesDialog(@NotNull Context context, @NotNull List<Message> messages) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if ( CalculatorPreferences.Calculations.showCalculationMessagesDialog.getPreference(prefs) ) {
            CalculatorMessagesDialog.showDialogForMessages(messages, context);
        }
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType){
            case show_create_var_dialog:
                App.getInstance().getUiThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        CalculatorActivityLauncher.createVar(App.getInstance().getApplication(), Locator.getInstance().getDisplay());
                    }
                });
                break;
            case show_create_function_dialog:
                App.getInstance().getUiThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        CalculatorActivityLauncher.createFunction(App.getInstance().getApplication(), Locator.getInstance().getDisplay());
                    }
                });
                break;
            case plot_graph:
                final PlotInput plotInput = (PlotInput) data;
                assert plotInput != null;
                App.getInstance().getUiThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        plotGraph(App.getInstance().getApplication(), plotInput.getFunction(), plotInput.getConstant());
                    }
                });
                break;
            case show_evaluation_error:
                final String errorMessage = (String) data;
                if (errorMessage != null) {
                    App.getInstance().getUiThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            showEvaluationError(App.getInstance().getApplication(), errorMessage);
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
