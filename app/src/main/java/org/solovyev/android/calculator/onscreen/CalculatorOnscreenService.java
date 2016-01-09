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

package org.solovyev.android.calculator.onscreen;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.squareup.otto.Subscribe;
import org.solovyev.android.Check;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CalculatorOnscreenService extends Service implements OnscreenViewListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final Class<CalculatorOnscreenBroadcastReceiver> INTENT_LISTENER_CLASS = CalculatorOnscreenBroadcastReceiver.class;
    private static final String SHOW_WINDOW_ACTION = "org.solovyev.android.calculator.onscreen.SHOW_WINDOW";
    private static final String SHOW_NOTIFICATION_ACTION = "org.solovyev.android.calculator.onscreen.SHOW_NOTIFICATION";
    private static final int NOTIFICATION_ID = 9031988; // my birthday =)

    private CalculatorOnscreenView view;

    @Nonnull
    private static Class<?> getIntentListenerClass() {
        return INTENT_LISTENER_CLASS;
    }

    public static void showNotification(@Nonnull Context context) {
        final Intent intent = new Intent(SHOW_NOTIFICATION_ACTION);
        intent.setClass(context, getIntentListenerClass());
        context.sendBroadcast(intent);
    }

    public static void showOnscreenView(@Nonnull Context context) {
        final Intent intent = createShowWindowIntent(context);
        context.sendBroadcast(intent);
    }

    @Nonnull
    private static Intent createShowWindowIntent(@Nonnull Context context) {
        final Intent intent = new Intent(SHOW_WINDOW_ACTION);
        intent.setClass(context, getIntentListenerClass());
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createView() {
        if (view != null) {
            return;
        }
        final WindowManager wm = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE));

        final DisplayMetrics dm = getResources().getDisplayMetrics();

        int twoThirdWidth = 2 * wm.getDefaultDisplay().getWidth() / 3;
        int twoThirdHeight = 2 * wm.getDefaultDisplay().getHeight() / 3;

        twoThirdWidth = Math.min(twoThirdWidth, twoThirdHeight);
        twoThirdHeight = Math.max(twoThirdWidth, getHeight(twoThirdWidth));

        final int baseWidth = Views.toPixels(dm, 300);
        final int width0 = Math.min(twoThirdWidth, baseWidth);
        final int height0 = Math.min(twoThirdHeight, getHeight(baseWidth));

        final int width = Math.min(width0, height0);
        final int height = Math.max(width0, height0);

        view = CalculatorOnscreenView.create(this, CalculatorOnscreenViewState.create(width, height, -1, -1), this);
        view.show();
        view.updateEditorState(Locator.getInstance().getEditor().getState());
        view.updateDisplayState(Locator.getInstance().getDisplay().getState());

        App.getBus().register(this);
        App.getPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private int getHeight(int width) {
        return 4 * width / 3;
    }

    @Override
    public void onDestroy() {
        if (view != null) {
            App.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
            App.getBus().unregister(this);
            view.hide();
            view = null;
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int result = super.onStartCommand(intent, flags, startId);
        handleStart(intent);
        return result;
    }

    private void handleStart(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        if (isShowWindowIntent(intent)) {
            hideNotification();
            createView();
            App.getGa().onFloatingCalculatorOpened();
        } else if (isShowNotificationIntent(intent)) {
            showNotification();
        }
    }

    private boolean isShowNotificationIntent(@Nonnull Intent intent) {
        return intent.getAction().equals(SHOW_NOTIFICATION_ACTION);
    }

    private boolean isShowWindowIntent(@Nonnull Intent intent) {
        return intent.getAction().equals(SHOW_WINDOW_ACTION);
    }

    private void hideNotification() {
        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onViewMinimized() {
        showNotification();
        stopSelf();
    }

    @Override
    public void onViewHidden() {
        stopSelf();
    }

    private void showNotification() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.kb_logo);
        builder.setContentTitle(getText(R.string.c_app_name));
        builder.setContentText(getString(R.string.open_onscreen_calculator));
        builder.setOngoing(true);

        final Intent intent = createShowWindowIntent(this);
        builder.setContentIntent(PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.getNotification());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Check.isNotNull(view);
        if (Preferences.Gui.theme.isSameKey(key) || Preferences.Onscreen.theme.isSameKey(key)) {
            stopSelf();
            CalculatorOnscreenService.showOnscreenView(this);
        }
    }

    @Subscribe
    public void onEditorChanged(@Nonnull Editor.ChangedEvent e) {
        Check.isNotNull(view);
        view.updateEditorState(e.newState);
    }

    @Subscribe
    public void onCursorMoved(@Nonnull Editor.CursorMovedEvent e) {
        Check.isNotNull(view);
        view.updateEditorState(e.state);
    }

    @Subscribe
    public void onDisplayChanged(@Nonnull Display.ChangedEvent e) {
        Check.isNotNull(view);
        view.updateDisplayState(e.newState);
    }
}

