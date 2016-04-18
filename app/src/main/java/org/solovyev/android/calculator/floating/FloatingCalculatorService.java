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

package org.solovyev.android.calculator.floating;

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
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.ga.Ga;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;

public class FloatingCalculatorService extends Service implements FloatingViewListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String SHOW_WINDOW_ACTION = "org.solovyev.android.calculator.floating.SHOW_WINDOW";
    private static final String SHOW_NOTIFICATION_ACTION = "org.solovyev.android.calculator.floating.SHOW_NOTIFICATION";
    private static final int NOTIFICATION_ID = 9031988; // my birthday =)

    private FloatingCalculatorView view;

    @Inject
    Bus bus;
    @Inject
    Editor editor;
    @Inject
    Display display;
    @Inject
    Ga ga;
    @Inject
    SharedPreferences preferences;

    public static void show(@Nonnull Context context) {
        context.sendBroadcast(createShowWindowIntent(context));
    }

    @Nonnull
    private static Intent createShowWindowIntent(@Nonnull Context context) {
        final Intent intent = new Intent(SHOW_WINDOW_ACTION);
        intent.setClass(context, FloatingCalculatorBroadcastReceiver.class);
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
        final android.view.Display dd = wm.getDefaultDisplay();

        //noinspection deprecation
        final int maxWidth = 2 * Math.min(dd.getWidth(), dd.getHeight()) / 3;
        final int desiredWidth = App.toPixels(dm, 300);

        final int width = Math.min(maxWidth, desiredWidth);
        final int height = getHeight(width);

        final FloatingCalculatorView.State state = new FloatingCalculatorView.State(width, height, -1, -1);
        view = new FloatingCalculatorView(this, state, this);
        view.show();
        view.updateEditorState(editor.getState());
        view.updateDisplayState(display.getState());

        bus.register(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    private int getHeight(int width) {
        return 4 * width / 3;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cast(getApplication()).getComponent().inject(this);
    }

    @Override
    public void onDestroy() {
        if (view != null) {
            preferences.unregisterOnSharedPreferenceChangeListener(this);
            bus.unregister(this);
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
            ga.onFloatingCalculatorOpened();
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
        nm.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Check.isNotNull(view);
        if (Preferences.Gui.theme.isSameKey(key) || Preferences.Onscreen.theme.isSameKey(key)) {
            stopSelf();
            FloatingCalculatorService.show(this);
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

