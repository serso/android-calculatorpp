package org.solovyev.android.calculator.onscreen;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditorViewState;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.external.AndroidExternalListenersContainer;
import org.solovyev.android.calculator.external.DefaultExternalCalculatorIntentHandler;
import org.solovyev.android.calculator.external.ExternalCalculatorIntentHandler;
import org.solovyev.android.calculator.external.ExternalCalculatorStateUpdater;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 9:42 PM
 */
public class CalculatorOnscreenService extends Service implements ExternalCalculatorStateUpdater, OnscreenViewListener {

	private static final int NOTIFICATION_ID = 9031988; // my birthday =)

	@Nonnull
	private final ExternalCalculatorIntentHandler intentHandler = new DefaultExternalCalculatorIntentHandler(this);
	public static final Class<CalculatorOnscreenBroadcastReceiver> INTENT_LISTENER_CLASS = CalculatorOnscreenBroadcastReceiver.class;

	@Nullable
	private static String cursorColor;

	@Nonnull
	private CalculatorOnscreenView view;

	private boolean compatibilityStart = true;

	private boolean viewCreated = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private void createView() {
		if (!viewCreated) {
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

			view = CalculatorOnscreenView.newInstance(this, CalculatorOnscreenViewState.newInstance(width, height, -1, -1), getCursorColor(this), this);
			view.show();

			startCalculatorListening();

			viewCreated = true;
		}
	}

	private int getHeight(int width) {
		return 4 * width / 3;
	}

	private void startCalculatorListening() {
		Locator.getInstance().getExternalListenersContainer().addExternalListener(getIntentListenerClass());
	}

	@Nonnull
	private static Class<?> getIntentListenerClass() {
		return INTENT_LISTENER_CLASS;
	}

	private void stopCalculatorListening() {
		Locator.getInstance().getExternalListenersContainer().removeExternalListener(getIntentListenerClass());
	}

	@Override
	public void onDestroy() {
		stopCalculatorListening();
		if (viewCreated) {
			this.view.hide();
		}
		super.onDestroy();
	}

	@Override
	public void updateState(@Nonnull Context context, @Nonnull CalculatorEditorViewState editorState, @Nonnull CalculatorDisplayViewState displayState) {
		view.updateDisplayState(displayState);
		view.updateEditorState(editorState);
	}

	@Nonnull
	private static String getCursorColor(@Nonnull Context context) {
		if (cursorColor == null) {
			cursorColor = Integer.toHexString(context.getResources().getColor(R.color.cpp_onscreen_cursor_color)).substring(2);
		}
		return cursorColor;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if (this.compatibilityStart) {
			handleStart(intent);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		final int result;
		try {
			this.compatibilityStart = false;
			result = super.onStartCommand(intent, flags, startId);
			handleStart(intent);
		} finally {
			this.compatibilityStart = true;
		}

		return result;
	}

	private void handleStart(@Nullable Intent intent) {
		if (intent != null) {

			if (isInitIntent(intent)) {

				boolean createView = intent.getBooleanExtra(AndroidExternalListenersContainer.INIT_ACTION_CREATE_VIEW_EXTRA, false);
				if (createView) {
					hideNotification();
					createView();
				} else {
					showNotification();
				}
			}

			if (viewCreated) {
				intentHandler.onIntent(this, intent);
			}

		}
	}

	private boolean isInitIntent(@Nonnull Intent intent) {
		return intent.getAction().equals(AndroidExternalListenersContainer.INIT_ACTION);
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

		final Intent intent = createShowOnscreenViewIntent(this);
		builder.setContentIntent(PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

		final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, builder.getNotification());
	}

	public static void showNotification(@Nonnull Context context) {
		final Intent intent = new Intent(AndroidExternalListenersContainer.INIT_ACTION);
		intent.setClass(context, getIntentListenerClass());
		context.sendBroadcast(intent);
	}

	public static void showOnscreenView(@Nonnull Context context) {
		final Intent intent = createShowOnscreenViewIntent(context);
		context.sendBroadcast(intent);
	}

	@Nonnull
	private static Intent createShowOnscreenViewIntent(@Nonnull Context context) {
		final Intent intent = new Intent(AndroidExternalListenersContainer.INIT_ACTION);
		intent.setClass(context, getIntentListenerClass());
		intent.putExtra(AndroidExternalListenersContainer.INIT_ACTION_CREATE_VIEW_EXTRA, true);
		return intent;
	}
}

