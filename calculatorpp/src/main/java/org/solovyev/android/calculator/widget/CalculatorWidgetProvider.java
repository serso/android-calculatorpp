package org.solovyev.android.calculator.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.R;

/**
 * User: Solovyev_S
 * Date: 19.10.12
 * Time: 16:18
 */
public class CalculatorWidgetProvider extends AppWidgetProvider {

    public static final String BUTTON_PRESSED = "org.solovyev.calculator.widget.BUTTON_PRESSED";


    @Override
    public void onUpdate(@NotNull Context context,
                         @NotNull AppWidgetManager appWidgetManager,
                         @NotNull int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        final Intent onButtonClickIntent = new Intent(context, CalculatorWidgetProvider.class);
        onButtonClickIntent.setAction(CalculatorWidgetController.BUTTON_PRESSED_ACTION);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, onButtonClickIntent, 0);
        views.setOnClickPendingIntent(R.id.oneDigitButton, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if ( BUTTON_PRESSED.equals(intent.getAction()) ) {
            Toast.makeText(CalculatorApplication.getInstance(), "Button pressed!", Toast.LENGTH_SHORT).show();
        }
    }
}
