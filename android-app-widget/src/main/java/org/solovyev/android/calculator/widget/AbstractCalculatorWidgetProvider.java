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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.RemoteViews;
import org.solovyev.android.calculator.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.content.Intent.ACTION_CONFIGURATION_CHANGED;
import static org.solovyev.android.calculator.CalculatorBroadcaster.ACTION_DISPLAY_STATE_CHANGED;
import static org.solovyev.android.calculator.CalculatorBroadcaster.ACTION_EDITOR_STATE_CHANGED;

/**
 * User: Solovyev_S
 * Date: 19.10.12
 * Time: 16:18
 */
abstract class AbstractCalculatorWidgetProvider extends AppWidgetProvider {

	static final String ACTION_BUTTON_ID_EXTRA = "buttonId";
	static final String ACTION_BUTTON_PRESSED = "org.solovyev.android.calculator.BUTTON_PRESSED";

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
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

	protected AbstractCalculatorWidgetProvider() {
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
			cursorColor = Integer.toHexString(context.getResources().getColor(R.color.cpp_widget_cursor_color)).substring(2);
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
	protected Class<? extends AbstractCalculatorWidgetProvider> getComponentClass() {
		return this.getClass();
	}

	private void updateWidget(@Nonnull Context context,
							  @Nonnull AppWidgetManager appWidgetManager,
							  @Nonnull int[] appWidgetIds) {
		final CalculatorEditorViewState editorState = Locator.getInstance().getEditor().getViewState();
		final CalculatorDisplayViewState displayState = Locator.getInstance().getDisplay().getViewState();

		for (int appWidgetId : appWidgetIds) {
			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			for (CalculatorButton button : CalculatorButton.values()) {
				final Intent onButtonClickIntent = new Intent(context, getComponentClass());
				onButtonClickIntent.setAction(ACTION_BUTTON_PRESSED);
				onButtonClickIntent.putExtra(ACTION_BUTTON_ID_EXTRA, button.getButtonId());
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

		final String action = intent.getAction();
		if (ACTION_BUTTON_PRESSED.equals(action)) {
			final int buttonId = intent.getIntExtra(ACTION_BUTTON_ID_EXTRA, 0);

			final CalculatorButton button = CalculatorButton.getById(buttonId);
			if (button != null) {
				button.onClick(context);
			}
		} else if (ACTION_CONFIGURATION_CHANGED.equals(action)) {
			updateState(context);
		} else if (ACTION_EDITOR_STATE_CHANGED.equals(action)) {
			updateState(context);
		} else if (ACTION_DISPLAY_STATE_CHANGED.equals(action)) {
			updateState(context);
		}
	}

	private void updateDisplayState(@Nonnull Context context, @Nonnull RemoteViews views, @Nonnull CalculatorDisplayViewState displayState) {
		if (displayState.isValid()) {
			views.setTextViewText(R.id.calculator_display, displayState.getText());
			views.setTextColor(R.id.calculator_display, context.getResources().getColor(R.color.cpp_default_text_color));
		} else {
			views.setTextColor(R.id.calculator_display, context.getResources().getColor(R.color.cpp_display_error_text_color));
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
