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
import org.solovyev.android.calculator.*;
import org.solovyev.common.MutableObject;

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
    private static final String EVENT_ID_EXTRA = "eventId";

    private static final String BUTTON_ID_EXTRA = "buttonId";
    private static final String BUTTON_PRESSED_ACTION = "org.solovyev.calculator.widget.BUTTON_PRESSED";

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

    @NotNull
    private final MutableObject<Long> lastDisplayEventId = new MutableObject<Long>(0L);

    @NotNull
    private final MutableObject<Long> lastEditorEventId = new MutableObject<Long>(0L);

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
            cursorColor = Integer.toHexString(context.getResources().getColor(R.color.widget_cursor_color)).substring(2);
        }
        return cursorColor;
    }

    @Override
    public void onUpdate(@NotNull Context context,
                         @NotNull AppWidgetManager appWidgetManager,
                         @NotNull int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        updateWidget(context, appWidgetManager, appWidgetIds, CalculatorLocatorImpl.getInstance().getEditor().getViewState(), CalculatorLocatorImpl.getInstance().getDisplay().getViewState());
    }

    private void updateWidget(@NotNull Context context,
                              @NotNull CalculatorEditorViewState editorState,
                              @NotNull CalculatorDisplayViewState displayState) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, CalculatorWidgetProvider.class));
        updateWidget(context, appWidgetManager, appWidgetIds, editorState, displayState);
    }

    private void updateWidget(@NotNull Context context,
                              @NotNull AppWidgetManager appWidgetManager,
                              @NotNull int[] appWidgetIds,
                              @NotNull CalculatorEditorViewState editorState,
                              @NotNull CalculatorDisplayViewState displayState) {
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

            updateEditorState(context, views, editorState);
            updateDisplayState(context, views, displayState);

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

            final Long eventId = intent.getLongExtra(EVENT_ID_EXTRA, 0L);

            boolean updateEditor = false;
            synchronized (lastEditorEventId) {
                if (eventId > lastEditorEventId.getObject()) {
                    lastEditorEventId.setObject(eventId);
                    updateEditor = true;
                }
            }

            if (updateEditor) {
                final Serializable object = intent.getSerializableExtra(EDITOR_STATE_EXTRA);
                if (object instanceof CalculatorEditorViewState) {
                    updateWidget(context, (CalculatorEditorViewState) object, CalculatorLocatorImpl.getInstance().getDisplay().getViewState());
                }
            }
        } else if (DISPLAY_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed broadcast received!");

            final Long eventId = intent.getLongExtra(EVENT_ID_EXTRA, 0L);
            boolean updateDisplay = false;
            synchronized (lastDisplayEventId) {
                if (eventId > lastDisplayEventId.getObject()) {
                    lastDisplayEventId.setObject(eventId);
                    updateDisplay = true;
                }
            }

            if (updateDisplay) {
                final Serializable object = intent.getSerializableExtra(DISPLAY_STATE_EXTRA);
                if (object instanceof CalculatorDisplayViewState) {
                    updateWidget(context, CalculatorLocatorImpl.getInstance().getEditor().getViewState(), (CalculatorDisplayViewState) object);
                }
            }
        } else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(intent.getAction())) {
            updateWidget(context, CalculatorLocatorImpl.getInstance().getEditor().getViewState(), CalculatorLocatorImpl.getInstance().getDisplay().getViewState());
        }
    }

    private void updateDisplayState(@NotNull Context context, @NotNull RemoteViews views, @NotNull CalculatorDisplayViewState displayState) {
        if (displayState.isValid()) {
            setText(views, R.id.calculatorDisplay, displayState.getText());
            setTextColor(views, R.id.calculatorDisplay, context.getResources().getColor(R.color.default_text_color));
        } else {
            setTextColor(views, R.id.calculatorDisplay, context.getResources().getColor(R.color.display_error_text_color));
        }
    }

    private void setText(@NotNull RemoteViews views, int textViewId, @Nullable CharSequence text) {
        views.setTextViewText(textViewId, text);
    }

    private void setTextColor(@NotNull RemoteViews views, int textViewId, int textColor) {
        views.setTextColor(textViewId, textColor);
    }

    private void updateEditorState(@NotNull Context context, @NotNull RemoteViews views, @NotNull CalculatorEditorViewState editorState) {
        String text = editorState.getText();

        CharSequence newText = text;
        int selection = editorState.getSelection();
        if (selection >= 0 && selection <= text.length()) {
            // inject cursor
            newText = Html.fromHtml(text.substring(0, selection) + "<font color=\"#" + getCursorColor(context) + "\">|</font>" + text.substring(selection));
        }
        CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "New editor state: " + text);
        setText(views, R.id.calculatorEditor, newText);
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    public static void onEditorStateChanged(@NotNull Context context,
                                            @NotNull CalculatorEventData calculatorEventData,
                                            @NotNull CalculatorEditorViewState editorViewState) {

        final Intent intent = new Intent(EDITOR_STATE_CHANGED_ACTION);
        intent.setClass(context, CalculatorWidgetProvider.class);
        intent.putExtra(EVENT_ID_EXTRA, calculatorEventData.getEventId());
        intent.putExtra(EDITOR_STATE_EXTRA, editorViewState);
        context.sendBroadcast(intent);
        CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed broadcast sent");
    }

    public static void onDisplayStateChanged(@NotNull Context context,
                                             @NotNull CalculatorEventData calculatorEventData,
                                             @NotNull CalculatorDisplayViewState displayViewState) {

        final Intent intent = new Intent(DISPLAY_STATE_CHANGED_ACTION);
        intent.setClass(context, CalculatorWidgetProvider.class);
        intent.putExtra(EVENT_ID_EXTRA, calculatorEventData.getEventId());
        intent.putExtra(DISPLAY_STATE_EXTRA, displayViewState);
        context.sendBroadcast(intent);
        CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed broadcast sent");
    }


}
