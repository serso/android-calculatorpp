package org.solovyev.android.calculator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import jscl.math.Generic;
import jscl.math.function.Constant;
import org.achartengine.ChartFactory;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.about.CalculatorAboutActivity;
import org.solovyev.android.calculator.help.CalculatorHelpActivity;
import org.solovyev.android.calculator.history.CalculatorHistoryActivity;
import org.solovyev.android.calculator.math.edit.*;
import org.solovyev.android.calculator.plot.CalculatorPlotActivity;
import org.solovyev.android.calculator.plot.CalculatorPlotFragment;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.StringUtils;

/**
 * User: serso
 * Date: 11/2/11
 * Time: 2:18 PM
 */
public class CalculatorActivityLauncher {

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
                    CalculatorLocatorImpl.getInstance().getNotifier().showMessage(R.string.c_not_valid_result, MessageType.error);
				}
			} else {
                CalculatorLocatorImpl.getInstance().getNotifier().showMessage(R.string.c_empty_var_error, MessageType.error);
			}
		} else {
            CalculatorLocatorImpl.getInstance().getNotifier().showMessage(R.string.c_not_valid_result, MessageType.error);
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
}
