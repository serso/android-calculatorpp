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
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditorViewState;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.R;

import java.io.Serializable;

/**
 * User: Solovyev_S
 * Date: 19.10.12
 * Time: 16:18
 */
public class CalculatorWidgetProvider extends AppWidgetProvider {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */
    public static final String BUTTON_ID_EXTRA = "buttonId";
    public static final String BUTTON_PRESSED_ACTION = "org.solovyev.calculator.widget.BUTTON_PRESSED";

    private static final String EDITOR_STATE_CHANGED_ACTION = "org.solovyev.calculator.widget.EDITOR_STATE_CHANGED";
    private static final String EDITOR_STATE_EXTRA = "editorState";

    private static final String DISPLAY_STATE_CHANGED_ACTION = "org.solovyev.calculator.widget.DISPLAY_STATE_CHANGED";
    private static final String DISPLAY_STATE_EXTRA = "displayState";

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
        if ( cursorColor == null ) {
            cursorColor = Integer.toHexString(context.getResources().getColor(R.color.widget_cursor_color)).substring(2);
        }
        return cursorColor;
    }

    @Override
    public void onUpdate(@NotNull Context context,
                         @NotNull AppWidgetManager appWidgetManager,
                         @NotNull int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            for (WidgetButton button : WidgetButton.values()) {
                final Intent onButtonClickIntent = new Intent(context, CalculatorWidgetProvider.class);
                onButtonClickIntent.setAction(BUTTON_PRESSED_ACTION);
                onButtonClickIntent.putExtra(BUTTON_ID_EXTRA, button.getButtonId());
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, button.getButtonId(), onButtonClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (pendingIntent != null) {
                    views.setOnClickPendingIntent(button.getButtonId(), pendingIntent);
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (CalculatorWidgetProvider.BUTTON_PRESSED_ACTION.equals(intent.getAction())) {
            final int buttonId = intent.getIntExtra(CalculatorWidgetProvider.BUTTON_ID_EXTRA, 0);

            final WidgetButton button = WidgetButton.getById(buttonId);
            if (button != null) {
                button.onClick(context);
            }
        } else if (EDITOR_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed broadcast received!");

            final Serializable object = intent.getSerializableExtra(EDITOR_STATE_EXTRA);
            if (object instanceof CalculatorEditorViewState) {
                updateEditorState(context, (CalculatorEditorViewState) object);
            }
        } else if (DISPLAY_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed broadcast received!");

            final Serializable object = intent.getSerializableExtra(DISPLAY_STATE_EXTRA);
            if (object instanceof CalculatorDisplayViewState) {
                updateDisplayState(context, (CalculatorDisplayViewState) object);
            }
        }
    }

    private void updateDisplayState(@NotNull Context context, @NotNull CalculatorDisplayViewState displayState) {
        if (displayState.isValid()) {
            setText(context, R.id.calculatorDisplay, displayState.getText());
            setTextColor(context, R.id.calculatorDisplay, context.getResources().getColor(R.color.default_text_color));
        } else {
            setTextColor(context, R.id.calculatorDisplay, context.getResources().getColor(R.color.display_error_text_color));
        }
    }

    private void setText(@NotNull Context context, int textViewId, @Nullable CharSequence text) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, CalculatorWidgetProvider.class));
        for (int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewText(textViewId, text);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void setTextColor(@NotNull Context context, int textViewId, int textColor) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, CalculatorWidgetProvider.class));
        for (int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextColor(textViewId, textColor);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void updateEditorState(@NotNull Context context, @NotNull CalculatorEditorViewState editorState) {
        String text = editorState.getText();

        CharSequence newText = text;
        int selection = editorState.getSelection();
        if (selection >= 0 && selection <= text.length() ) {
            // inject cursor
            newText = Html.fromHtml(text.substring(0, selection) + "<font color=\"#" + getCursorColor(context) + "\">|</font>" + text.substring(selection));
        }
        CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "New editor state: " + text);
        setText(context, R.id.calculatorEditor, newText);
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    public static void onEditorStateChanged(@NotNull Context context, @NotNull CalculatorEditorViewState editorViewState) {
        final Intent intent = new Intent(EDITOR_STATE_CHANGED_ACTION);
        intent.setClass(context, CalculatorWidgetProvider.class);
        intent.putExtra(EDITOR_STATE_EXTRA, editorViewState);
        context.sendBroadcast(intent);
        CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed broadcast sent");
    }

    public static void onDisplayStateChanged(@NotNull Context context, @NotNull CalculatorDisplayViewState displayViewState) {
        final Intent intent = new Intent(DISPLAY_STATE_CHANGED_ACTION);
        intent.setClass(context, CalculatorWidgetProvider.class);
        intent.putExtra(DISPLAY_STATE_EXTRA, displayViewState);
        context.sendBroadcast(intent);
        CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed broadcast sent");
    }


}
