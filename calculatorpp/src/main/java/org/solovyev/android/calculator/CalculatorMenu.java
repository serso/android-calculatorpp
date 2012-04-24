package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.LayoutActivityMenu;
import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;

/**
 * User: serso
 * Date: 4/23/12
 * Time: 2:25 PM
 */
enum CalculatorMenu implements LayoutActivityMenu.LayoutMenuItem {

    settings(R.id.main_menu_item_settings){
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            CalculatorActivityLauncher.showSettings(context);
        }
    },

    history(R.id.main_menu_item_history) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            CalculatorActivityLauncher.showHistory(context);
        }
    },

    about(R.id.main_menu_item_about) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            CalculatorActivityLauncher.showAbout(context);
        }
    },

    help(R.id.main_menu_item_help) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            CalculatorActivityLauncher.showHelp(context);
        }
    },

    conversion_tool( R.id.main_menu_conversion_tool) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            new NumeralBaseConverterDialog(null).show(context);
        }
    },

    exit(R.id.main_menu_item_exit) {
        @Override
        public void onClick(@NotNull MenuItem data, @NotNull Context context) {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            } else {
                Log.e(CalculatorActivity.TAG, "Activity menu used with context");
            }
        }
    };

    private final int menuItemId;

    private CalculatorMenu (int menuItemId) {
        this.menuItemId = menuItemId;
    }

    @NotNull
    @Override
    public Integer getItemId() {
        return menuItemId;
    }
}
