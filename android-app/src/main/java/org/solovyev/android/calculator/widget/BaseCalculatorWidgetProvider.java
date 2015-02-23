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
import android.text.Html;
import android.widget.RemoteViews;
import org.solovyev.android.calculator.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.content.Intent.ACTION_CONFIGURATION_CHANGED;
import static org.solovyev.android.calculator.CalculatorBroadcaster.ACTION_DISPLAY_STATE_CHANGED;
import static org.solovyev.android.calculator.CalculatorBroadcaster.ACTION_EDITOR_STATE_CHANGED;
import static org.solovyev.android.calculator.CalculatorReceiver.newButtonClickedIntent;

/**
 * User: Solovyev_S
 * Date: 19.10.12
 * Time: 16:18
 */
public abstract class BaseCalculatorWidgetProvider extends AppWidgetProvider {

	private static final String TAG = "Calculator++ Widget";
	private static final int WIDGET_CATEGORY_KEYGUARD = 2;
	private static final String OPTION_APPWIDGET_HOST_CATEGORY = "appWidgetCategory";
	private static final String ACTION_APPWIDGET_OPTIONS_CHANGED = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";

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
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

	protected BaseCalculatorWidgetProvider() {
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

	@Nonnull
	private String getCursorColor(@Nonnull Context context) {
		if (cursorColor == null) {
			cursorColor = Integer.toHexString(context.getResources().getColor(R.color.cpp_widget_cursor)).substring(2);
		}
		return cursorColor;
	}

	@Override
	public void onUpdate(@Nonnull Context context,
						 @Nonnull AppWidgetManager appWidgetManager,
						 @Nonnull int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		updateWidget(context, appWidgetManager, appWidgetIds);
	}

	public void updateState(@Nonnull Context context) {
		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getComponentClass()));
		updateWidget(context, appWidgetManager, appWidgetIds);
	}

	@Nonnull
	protected Class<? extends BaseCalculatorWidgetProvider> getComponentClass() {
		return this.getClass();
	}

	private void updateWidget(@Nonnull Context context,
							  @Nonnull AppWidgetManager appWidgetManager,
							  @Nonnull int[] appWidgetIds) {
		final CalculatorEditorViewState editorState = Locator.getInstance().getEditor().getViewState();
		final CalculatorDisplayViewState displayState = Locator.getInstance().getDisplay().getViewState();

		final Resources resources = context.getResources();
		for (int appWidgetId : appWidgetIds) {
			final RemoteViews views = new RemoteViews(context.getPackageName(), getLayout(appWidgetManager, appWidgetId, resources));

			for (CalculatorButton button : CalculatorButton.values()) {
				final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, button.getButtonId(), newButtonClickedIntent(context, button), PendingIntent.FLAG_UPDATE_CURRENT);
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

	private int getLayout(@Nonnull AppWidgetManager appWidgetManager, int appWidgetId, @Nonnull Resources resources) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			return getLayoutJellyBean(appWidgetManager, appWidgetId, resources);
		}
		return R.layout.widget_layout;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private int getLayoutJellyBean(AppWidgetManager appWidgetManager, int appWidgetId, Resources resources) {
		final Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

		if (options != null) {
			// Get the value of OPTION_APPWIDGET_HOST_CATEGORY
			final int category = options.getInt(OPTION_APPWIDGET_HOST_CATEGORY, -1);

			if (category != -1) {
				// If the value is WIDGET_CATEGORY_KEYGUARD, it's a lockscreen widget
				final boolean keyguard = category == WIDGET_CATEGORY_KEYGUARD;
				if(keyguard) {
					final int minHeightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, -1);
					final int minHeight = resources.getDimensionPixelSize(R.dimen.min_expanded_height_lock_screen);
					final boolean expanded = (minHeightDp >= minHeight / resources.getDisplayMetrics().density);
					if (expanded) {
						return R.layout.widget_layout_lockscreen;
					} else {
						return R.layout.widget_layout_lockscreen_collapsed;
					}
				}
			}
		}
		return R.layout.widget_layout;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		final String action = intent.getAction();
		if (ACTION_CONFIGURATION_CHANGED.equals(action)) {
			updateState(context);
		} else if (ACTION_EDITOR_STATE_CHANGED.equals(action)) {
			updateState(context);
		} else if (ACTION_DISPLAY_STATE_CHANGED.equals(action)) {
			updateState(context);
		} else if (ACTION_APPWIDGET_OPTIONS_CHANGED.equals(action)) {
			updateState(context);
		}
	}

	private void updateDisplayState(@Nonnull Context context, @Nonnull RemoteViews views, @Nonnull CalculatorDisplayViewState displayState) {
		if (displayState.isValid()) {
			views.setTextViewText(R.id.calculator_display, displayState.getText());
			views.setTextColor(R.id.calculator_display, context.getResources().getColor(R.color.cpp_text));
		} else {
			views.setTextColor(R.id.calculator_display, context.getResources().getColor(R.color.cpp_text_error));
		}
	}

	private void updateEditorState(@Nonnull Context context, @Nonnull RemoteViews views, @Nonnull CalculatorEditorViewState editorState) {
		final CharSequence text = editorState.getTextAsCharSequence();

		CharSequence newText = text;
		int selection = editorState.getSelection();
		if (selection >= 0 && selection <= text.length()) {
			// inject cursor
			newText = Html.fromHtml(text.subSequence(0, selection) + "<font color=\"#" + getCursorColor(context) + "\">|</font>" + text.subSequence(selection, text.length()));
		}
		Locator.getInstance().getNotifier().showDebugMessage(TAG, "New editor state: " + text);
		views.setTextViewText(R.id.calculator_editor, newText);
	}
}
