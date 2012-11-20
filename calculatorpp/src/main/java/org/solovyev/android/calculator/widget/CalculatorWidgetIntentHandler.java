package org.solovyev.android.calculator.widget;

import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.external.DefaultExternalCalculatorIntentHandler;
import org.solovyev.android.calculator.external.ExternalCalculatorStateUpdater;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 10:39 PM
 */
public class CalculatorWidgetIntentHandler extends DefaultExternalCalculatorIntentHandler {

    public CalculatorWidgetIntentHandler(@NotNull ExternalCalculatorStateUpdater stateUpdater) {
        super(stateUpdater);
    }

    @Override
    public void onIntent(@NotNull Context context, @NotNull Intent intent) {
        super.onIntent(context, intent);

        if (AbstractCalculatorWidgetProvider.BUTTON_PRESSED_ACTION.equals(intent.getAction())) {
            final int buttonId = intent.getIntExtra(AbstractCalculatorWidgetProvider.BUTTON_ID_EXTRA, 0);

            final WidgetButton button = WidgetButton.getById(buttonId);
            if (button != null) {
                button.onClick(context);
            }
        } else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(intent.getAction())) {
            updateState(context, CalculatorLocatorImpl.getInstance().getEditor().getViewState(), CalculatorLocatorImpl.getInstance().getDisplay().getViewState());
        }
    }

}
