package org.solovyev.android.calculator.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.RemoteViews;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorButtons;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditorViewState;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.external.ExternalCalculatorIntentHandler;
import org.solovyev.android.calculator.external.ExternalCalculatorStateUpdater;

/**
 * User: Solovyev_S
 * Date: 19.10.12
 * Time: 16:18
 */
abstract class AbstractCalculatorWidgetProvider extends AppWidgetProvider implements ExternalCalculatorStateUpdater {

    static final String BUTTON_ID_EXTRA = "buttonId";
    static final String BUTTON_PRESSED_ACTION = "org.solovyev.android.calculator.widget.BUTTON_PRESSED";

    private static final String TAG = "Calculator++ Widget";

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    @Nullable
    private String cursorColor;

    @NotNull
    private ExternalCalculatorIntentHandler intentHandler = new CalculatorWidgetIntentHandler(this);


    /*
    **********************************************************************
    *
    *                           CONSTRUCTORS
    *
    **********************************************************************
    */

    protected AbstractCalculatorWidgetProvider() {
        final Class<? extends AppWidgetProvider> componentClass = this.getComponentClass();

        Locator.getInstance().getExternalListenersContainer().addExternalListener(componentClass);
    }

    /*
    **********************************************************************
    *
    *                           METHODS
    *
    **********************************************************************
    */

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        getCursorColor(context);
    }

    @NotNull
    private String getCursorColor(@NotNull Context context) {
        if (cursorColor == null) {
            cursorColor = Integer.toHexString(context.getResources().getColor(R.color.cpp_widget_cursor_color)).substring(2);
        }
        return cursorColor;
    }

    @Override
    public void onUpdate(@NotNull Context context,
                         @NotNull AppWidgetManager appWidgetManager,
                         @NotNull int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        updateWidget(context, appWidgetManager, appWidgetIds, Locator.getInstance().getEditor().getViewState(), Locator.getInstance().getDisplay().getViewState());
    }

    @Override
    public void updateState(@NotNull Context context,
                            @NotNull CalculatorEditorViewState editorState,
                            @NotNull CalculatorDisplayViewState displayState) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getComponentClass()));
        updateWidget(context, appWidgetManager, appWidgetIds, editorState, displayState);
    }


    @NotNull
    protected Class<? extends AbstractCalculatorWidgetProvider> getComponentClass(){
        return this.getClass();
    }

    private void updateWidget(@NotNull Context context,
                              @NotNull AppWidgetManager appWidgetManager,
                              @NotNull int[] appWidgetIds,
                              @NotNull CalculatorEditorViewState editorState,
                              @NotNull CalculatorDisplayViewState displayState) {
        for (int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            for (WidgetButton button : WidgetButton.values()) {
                final Intent onButtonClickIntent = new Intent(context, getComponentClass());
                onButtonClickIntent.setAction(BUTTON_PRESSED_ACTION);
                onButtonClickIntent.putExtra(BUTTON_ID_EXTRA, button.getButtonId());
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, button.getButtonId(), onButtonClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (pendingIntent != null) {
                    views.setOnClickPendingIntent(button.getButtonId(), pendingIntent);
                }
            }

            updateEditorState(context, views, editorState);
            updateDisplayState(context, views, displayState);

            CalculatorButtons.initMultiplicationButton(views);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        this.intentHandler.onIntent(context, intent);
    }

    private void updateDisplayState(@NotNull Context context, @NotNull RemoteViews views, @NotNull CalculatorDisplayViewState displayState) {
        if (displayState.isValid()) {
            views.setTextViewText(R.id.calculator_display, displayState.getText());
            views.setTextColor(R.id.calculator_display, context.getResources().getColor(R.color.cpp_default_text_color));
        } else {
            views.setTextColor(R.id.calculator_display, context.getResources().getColor(R.color.cpp_display_error_text_color));
        }
    }

    private void updateEditorState(@NotNull Context context, @NotNull RemoteViews views, @NotNull CalculatorEditorViewState editorState) {
        String text = editorState.getText();

        CharSequence newText = text;
        int selection = editorState.getSelection();
        if (selection >= 0 && selection <= text.length()) {
            // inject cursor
            newText = Html.fromHtml(text.substring(0, selection) + "<font color=\"#" + getCursorColor(context) + "\">|</font>" + text.substring(selection));
        }
        Locator.getInstance().getNotifier().showDebugMessage(TAG, "New editor state: " + text);
        views.setTextViewText(R.id.calculator_editor, newText);
    }
}
