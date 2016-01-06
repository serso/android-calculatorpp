/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.*;
import android.widget.RemoteViews;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.Preferences.SimpleTheme;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
import static android.content.Intent.ACTION_CONFIGURATION_CHANGED;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static org.solovyev.android.calculator.CalculatorBroadcaster.*;
import static org.solovyev.android.calculator.CalculatorReceiver.newButtonClickedIntent;

public class CalculatorWidget extends AppWidgetProvider {

    private static final String TAG = App.subTag("Widget");
    private static final int WIDGET_CATEGORY_KEYGUARD = 2;
    private static final String OPTION_APPWIDGET_HOST_CATEGORY = "appWidgetCategory";
    private static final String ACTION_APPWIDGET_OPTIONS_CHANGED = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";

    @Nullable
    private SpannedString cursorString;

    public CalculatorWidget() {
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        initCursorString(context);
    }

    @Nonnull
    private SpannedString getCursorString(@Nonnull Context context) {
        return initCursorString(context);
    }

    @Nonnull
    private SpannedString initCursorString(@Nonnull Context context) {
        if (cursorString == null) {
            cursorString = new SpannedString(App.colorString("|", ContextCompat.getColor(context, R.color.cpp_widget_cursor)));
        }
        return cursorString;
    }

    @Override
    public void onUpdate(@Nonnull Context context,
                         @Nonnull AppWidgetManager appWidgetManager,
                         @Nonnull int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        updateWidget(context, appWidgetManager, appWidgetIds);
    }

    public void updateState(@Nonnull Context context) {
        final AppWidgetManager manager = AppWidgetManager.getInstance(context);
        final int[] widgetIds = manager.getAppWidgetIds(new ComponentName(context, CalculatorWidget.class));
        updateWidget(context, manager, widgetIds);
    }

    private void updateWidget(@Nonnull Context context,
                              @Nonnull AppWidgetManager manager,
                              @Nonnull int[] widgetIds) {
        final CalculatorEditorViewState editorState = Locator.getInstance().getEditor().getViewState();
        final CalculatorDisplayViewState displayState = Locator.getInstance().getDisplay().getViewState();

        final Resources resources = context.getResources();
        final SimpleTheme theme = App.getWidgetTheme().resolveThemeFor(App.getTheme());
        for (int widgetId : widgetIds) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), getLayout(manager, widgetId, resources, theme));

            for (CalculatorButton button : CalculatorButton.values()) {
                final int buttonId;
                if (button == CalculatorButton.settings_widget) {
                    // overriding default settings button behavior
                    buttonId = CalculatorButton.settings.getButtonId();
                } else {
                    buttonId = button.getButtonId();
                }
                final PendingIntent intent = PendingIntent.getBroadcast(context, buttonId, newButtonClickedIntent(context, button), PendingIntent.FLAG_UPDATE_CURRENT);
                if (intent != null) {
                    views.setOnClickPendingIntent(buttonId, intent);
                }
            }

            updateEditorState(context, views, editorState, theme);
            updateDisplayState(context, views, displayState, theme);

            views.setTextViewText(R.id.cpp_button_multiplication, Locator.getInstance().getEngine().getMultiplicationSign());

            manager.updateAppWidget(widgetId, views);
        }
    }

    private int getLayout(@Nonnull AppWidgetManager manager, int widgetId, @Nonnull Resources resources, @Nonnull SimpleTheme theme) {
        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            return getLayoutJellyBean(manager, widgetId, resources, theme);
        }
        return getDefaultLayout(theme);
    }

    private int getDefaultLayout(@Nonnull SimpleTheme theme) {
        return theme.getWidgetLayout(App.getTheme());
    }

    @TargetApi(JELLY_BEAN)
    private int getLayoutJellyBean(@Nonnull AppWidgetManager manager, int widgetId, Resources resources, @Nonnull SimpleTheme theme) {
        final Bundle options = manager.getAppWidgetOptions(widgetId);
        if (options == null) {
            return getDefaultLayout(theme);
        }

        final int category = options.getInt(OPTION_APPWIDGET_HOST_CATEGORY, -1);
        if (category == -1) {
            return getDefaultLayout(theme);
        }

        final boolean keyguard = category == WIDGET_CATEGORY_KEYGUARD;
        if (!keyguard) {
            return getDefaultLayout(theme);
        }

        final int widgetMinHeight = Views.toPixels(resources.getDisplayMetrics(), options.getInt(OPTION_APPWIDGET_MIN_HEIGHT, 0));
        final int lockScreenMinHeight = resources.getDimensionPixelSize(R.dimen.min_expanded_height_lock_screen);
        final boolean expanded = widgetMinHeight >= lockScreenMinHeight;
        if (expanded) {
            return R.layout.widget_layout_lockscreen;
        } else {
            return R.layout.widget_layout_lockscreen_collapsed;
        }
    }

    @Override
    public void onReceive(@Nonnull Context context, @Nonnull Intent intent) {
        super.onReceive(context, intent);

        final String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        switch (action) {
            case ACTION_CONFIGURATION_CHANGED:
            case ACTION_EDITOR_STATE_CHANGED:
            case ACTION_DISPLAY_STATE_CHANGED:
            case ACTION_APPWIDGET_OPTIONS_CHANGED:
            case ACTION_THEME_CHANGED:
                updateState(context);
                break;
        }
    }

    private void updateDisplayState(@Nonnull Context context, @Nonnull RemoteViews views, @Nonnull CalculatorDisplayViewState displayState, @Nonnull SimpleTheme theme) {
        final boolean error = !displayState.isValid();
        if (!error) {
            views.setTextViewText(R.id.calculator_display, displayState.getText());
        }
        views.setTextColor(R.id.calculator_display, ContextCompat.getColor(context, theme.getDisplayTextColor(error)));
    }

    private void updateEditorState(@Nonnull Context context, @Nonnull RemoteViews views, @Nonnull CalculatorEditorViewState editorState, @Nonnull SimpleTheme theme) {
        final CharSequence text = editorState.getTextAsCharSequence();

        final boolean unspan = App.getTheme().light != theme.light;
        CharSequence newText = text;
        int selection = editorState.getSelection();
        if (selection >= 0 && selection <= text.length()) {
            // inject cursor
            final SpannableStringBuilder result = new SpannableStringBuilder();
            final CharSequence beforeCursor = text.subSequence(0, selection);
            result.append(unspan ? unspan(beforeCursor) : beforeCursor);
            result.append(getCursorString(context));
            final CharSequence afterCursor = text.subSequence(selection, text.length());
            result.append(unspan ? unspan(afterCursor) : afterCursor);
            newText = result;
        }
        Locator.getInstance().getNotifier().showDebugMessage(TAG, "New editor state: " + text);
        views.setTextViewText(R.id.calculator_editor, newText);
    }

    @NonNull
    private String unspan(@Nonnull CharSequence spannable) {
        return spannable.toString();
    }
}
