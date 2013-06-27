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
import org.solovyev.android.calculator.external.CalculatorExternalListenersContainer;
import org.solovyev.android.calculator.external.ExternalCalculatorIntentHandler;
import org.solovyev.android.calculator.external.ExternalCalculatorStateUpdater;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	@Nonnull
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

		final CalculatorExternalListenersContainer externalListenersContainer = Locator.getInstance().getExternalListenersContainer();
		// NOTE: null might be in tests, now robolectric creates widget provider before application
		if (externalListenersContainer != null) {
			externalListenersContainer.addExternalListener(componentClass);
		}
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

		updateWidget(context, appWidgetManager, appWidgetIds, Locator.getInstance().getEditor().getViewState(), Locator.getInstance().getDisplay().getViewState());
	}

	@Override
	public void updateState(@Nonnull Context context,
							@Nonnull CalculatorEditorViewState editorState,
							@Nonnull CalculatorDisplayViewState displayState) {
		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getComponentClass()));
		updateWidget(context, appWidgetManager, appWidgetIds, editorState, displayState);
	}


	@Nonnull
	protected Class<? extends AbstractCalculatorWidgetProvider> getComponentClass() {
		return this.getClass();
	}

	private void updateWidget(@Nonnull Context context,
							  @Nonnull AppWidgetManager appWidgetManager,
							  @Nonnull int[] appWidgetIds,
							  @Nonnull CalculatorEditorViewState editorState,
							  @Nonnull CalculatorDisplayViewState displayState) {
		for (int appWidgetId : appWidgetIds) {
			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			for (CalculatorButton button : CalculatorButton.values()) {
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
