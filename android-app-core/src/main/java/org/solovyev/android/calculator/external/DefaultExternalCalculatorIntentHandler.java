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

package org.solovyev.android.calculator.external;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import javax.annotation.Nonnull;

import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditorViewState;
import org.solovyev.android.calculator.Locator;
import org.solovyev.common.MutableObject;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 10:34 PM
 */
public class DefaultExternalCalculatorIntentHandler implements ExternalCalculatorIntentHandler {

	private static final String TAG = ExternalCalculatorIntentHandler.class.getSimpleName();

	@Nonnull
	private final MutableObject<Long> lastDisplayEventId = new MutableObject<Long>(0L);

	@Nonnull
	private final MutableObject<Long> lastEditorEventId = new MutableObject<Long>(0L);

	@Nonnull
	private final ExternalCalculatorStateUpdater stateUpdater;

	public DefaultExternalCalculatorIntentHandler(@Nonnull ExternalCalculatorStateUpdater stateUpdater) {
		this.stateUpdater = stateUpdater;
	}

	@Override
	public void onIntent(@Nonnull Context context, @Nonnull Intent intent) {

		if (AndroidExternalListenersContainer.EDITOR_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			Locator.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed broadcast received!");

			final Long eventId = intent.getLongExtra(AndroidExternalListenersContainer.EVENT_ID_EXTRA, 0L);

			boolean updateEditor = false;
			synchronized (lastEditorEventId) {
				if (eventId > lastEditorEventId.getObject()) {
					lastEditorEventId.setObject(eventId);
					updateEditor = true;
				}
			}

			if (updateEditor) {
				final Parcelable object = intent.getParcelableExtra(AndroidExternalListenersContainer.EDITOR_STATE_EXTRA);
				if (object instanceof CalculatorEditorViewState) {
					onEditorStateChanged(context, (CalculatorEditorViewState) object);
				}
			}
		} else if (AndroidExternalListenersContainer.DISPLAY_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			Locator.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed broadcast received!");

			final Long eventId = intent.getLongExtra(AndroidExternalListenersContainer.EVENT_ID_EXTRA, 0L);
			boolean updateDisplay = false;
			synchronized (lastDisplayEventId) {
				if (eventId > lastDisplayEventId.getObject()) {
					lastDisplayEventId.setObject(eventId);
					updateDisplay = true;
				}
			}

			if (updateDisplay) {
				final Parcelable object = intent.getParcelableExtra(AndroidExternalListenersContainer.DISPLAY_STATE_EXTRA);
				if (object instanceof CalculatorDisplayViewState) {
					onDisplayStateChanged(context, (CalculatorDisplayViewState) object);
				}
			}
		} else if (AndroidExternalListenersContainer.INIT_ACTION.equals(intent.getAction())) {
			updateState(context, Locator.getInstance().getEditor().getViewState(), Locator.getInstance().getDisplay().getViewState());
		}
	}

	protected void updateState(@Nonnull Context context,
							   @Nonnull CalculatorEditorViewState editorViewState,
							   @Nonnull CalculatorDisplayViewState displayViewState) {
		stateUpdater.updateState(context, editorViewState, displayViewState);
	}

	protected void onDisplayStateChanged(@Nonnull Context context, @Nonnull CalculatorDisplayViewState displayViewState) {
		updateState(context, Locator.getInstance().getEditor().getViewState(), displayViewState);
	}

	protected void onEditorStateChanged(@Nonnull Context context, @Nonnull CalculatorEditorViewState editorViewState) {
		updateState(context, editorViewState, Locator.getInstance().getDisplay().getViewState());
	}
}
