package org.solovyev.android.calculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class CalculatorBroadcaster implements CalculatorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String ACTION_INIT = "org.solovyev.android.calculator.INIT";
    public static final String ACTION_EDITOR_STATE_CHANGED = "org.solovyev.android.calculator.EDITOR_STATE_CHANGED";
    public static final String ACTION_DISPLAY_STATE_CHANGED = "org.solovyev.android.calculator.DISPLAY_STATE_CHANGED";
    public static final String ACTION_THEME_CHANGED = "org.solovyev.android.calculator.THEME_CHANGED";

    @Nonnull
    private final Context context;

    @Nonnull
    private final Intents intents = new Intents();

    public CalculatorBroadcaster(@Nonnull Context context, @Nonnull SharedPreferences preferences) {
        this.context = context;
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType) {
            case editor_state_changed:
            case editor_state_changed_light:
                sendBroadcastIntent(ACTION_EDITOR_STATE_CHANGED);
                break;
            case display_state_changed:
                sendBroadcastIntent(ACTION_DISPLAY_STATE_CHANGED);
                break;
        }
    }

    public void sendInitIntent() {
        sendBroadcastIntent(ACTION_INIT);
    }

    public void sendBroadcastIntent(@Nonnull String action) {
        context.sendBroadcast(intents.get(action));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Preferences.Gui.theme.isSameKey(key) || Preferences.Widget.theme.isSameKey(key)) {
            sendBroadcastIntent(ACTION_THEME_CHANGED);
        }
    }

    private static final class Intents {
        @Nonnull
        private Map<String, Intent> map = new HashMap<>();

        @Nonnull
        Intent get(@Nonnull String action) {
            Intent intent = map.get(action);
            if (intent != null) {
                return intent;
            }
            intent = new Intent(action);
            map.put(action, intent);
            return intent;
        }
    }
}
