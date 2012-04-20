package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.view.drag.DirectionDragButton;
import org.solovyev.android.view.drag.DragDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 5:03 PM
 */
public class NumeralBaseButtons {

    @NotNull
    private final List<Integer> buttonIds = new ArrayList<Integer>();

    public void addButton(@NotNull DirectionDragButton button) {
        buttonIds.add(button.getId());
    }

    public void addButtonId(@NotNull Integer buttonId) {
        buttonIds.add(buttonId);
    }

    public synchronized void removeNumeralDigits(@NotNull Activity activity) {
        for (Integer id : buttonIds) {
            final DirectionDragButton button = (DirectionDragButton) activity.findViewById(id);
            if (button != null) {
                button.showDirectionText(false, DragDirection.left);
                button.invalidate();
            }
        }
    }

    public synchronized void showNumeralDigits(@NotNull Activity activity) {
        for (Integer id : buttonIds) {
            final DirectionDragButton button = (DirectionDragButton) activity.findViewById(id);
            if (button != null) {
                button.showDirectionText(true, DragDirection.left);
                button.invalidate();
            }
        }
    }

    public void clear() {
        buttonIds.clear();
    }

    public synchronized void toggleNumericDigits(@NotNull Activity activity, @NotNull SharedPreferences preferences) {
        if (CalculatorEngine.Preferences.numeralBase.getPreference(preferences) != NumeralBase.hex) {
            this.removeNumeralDigits(activity);
        } else {
            this.showNumeralDigits(activity);
        }
    }
}
