package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.actionbarsherlock.view.MenuItem;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;
import org.solovyev.android.menu.LabeledMenuItem;

/**
 * User: serso
 * Date: 4/23/12
 * Time: 2:25 PM
 */
enum CalculatorMenu implements LabeledMenuItem<MenuItem> {

    settings(R.string.c_settings) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            CalculatorActivityLauncher.showSettings(context);
        }
    },

    history(R.string.c_history) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            CalculatorActivityLauncher.showHistory(context);
        }
    },

    plotter(R.string.cpp_plotter) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            Locator.getInstance().getPlotter().plot();
        }
    },

    conversion_tool(R.string.c_conversion_tool) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            new NumeralBaseConverterDialog(null).show(context);
        }
    },

    help(R.string.c_help) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            CalculatorActivityLauncher.showHelp(context);
        }
    },

    about(R.string.c_about) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            CalculatorActivityLauncher.showAbout(context);
        }
    },

    exit(R.string.c_exit) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            } else {
                Log.e(CalculatorActivity.TAG, "Activity menu used with context");
            }
        }
    };

    private final int captionResId;

    private CalculatorMenu(int captionResId) {
        this.captionResId = captionResId;
    }

    @NotNull
    @Override
    public String getCaption(@NotNull Context context) {
        return context.getString(captionResId);
    }
}
