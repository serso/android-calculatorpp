package org.solovyev.android.calculator;

import android.app.Activity;
import android.view.View;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.menu.AMenuBuilder;
import org.solovyev.android.menu.MenuImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 10:58
 */
public class CalculatorDisplayOnClickListener implements View.OnClickListener {

    @NotNull
    private final Activity activity;

    public CalculatorDisplayOnClickListener(@NotNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CalculatorDisplayView) {
            final CalculatorDisplay cd = CalculatorLocatorImpl.getInstance().getCalculatorDisplay();

            final CalculatorDisplayViewState displayViewState = cd.getViewState();

            if (displayViewState.isValid()) {
                final List<CalculatorDisplayMenuItem> filteredMenuItems = new ArrayList<CalculatorDisplayMenuItem>(CalculatorDisplayMenuItem.values().length);
                for (CalculatorDisplayMenuItem menuItem : CalculatorDisplayMenuItem.values()) {
                    if (menuItem.isItemVisible(displayViewState)) {
                        filteredMenuItems.add(menuItem);
                    }
                }

                if (!filteredMenuItems.isEmpty()) {
                    AMenuBuilder.newInstance(activity, MenuImpl.newInstance(filteredMenuItems)).create(displayViewState).show();
                }

            } else {
                final String errorMessage = displayViewState.getErrorMessage();
                if (errorMessage != null) {
                    CalculatorModel.showEvaluationError(activity, errorMessage);
                }
            }
        }
    }
}
