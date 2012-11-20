package org.solovyev.android.calculator.external;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.*;

import java.util.HashSet;
import java.util.Set;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 10:14 PM
 */
public class ExternalCalculatorHelper {
    /*
        **********************************************************************
        *
        *                           CONSTANTS
        *
        **********************************************************************
        */
    public static final String EVENT_ID_EXTRA = "eventId";
    public static final String EDITOR_STATE_CHANGED_ACTION = "org.solovyev.calculator.widget.EDITOR_STATE_CHANGED";
    public static final String EDITOR_STATE_EXTRA = "editorState";
    public static final String DISPLAY_STATE_CHANGED_ACTION = "org.solovyev.calculator.widget.DISPLAY_STATE_CHANGED";
    public static final String DISPLAY_STATE_EXTRA = "displayState";

    private static final String TAG = ExternalCalculatorHelper.class.getSimpleName();

    private static final Set<Class<?>> externalListeners = new HashSet<Class<?>>();

    public static void onEditorStateChanged(@NotNull Context context,
                                            @NotNull CalculatorEventData calculatorEventData,
                                            @NotNull CalculatorEditorViewState editorViewState) {

        for (Class<?> externalListener : externalListeners) {
            final Intent intent = new Intent(EDITOR_STATE_CHANGED_ACTION);
            intent.setClass(context, externalListener);
            intent.putExtra(EVENT_ID_EXTRA, calculatorEventData.getEventId());
            intent.putExtra(EDITOR_STATE_EXTRA, (Parcelable) new ParcelableCalculatorEditorViewState(editorViewState));
            context.sendBroadcast(intent);
            CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed broadcast sent");
        }
    }

    public static void onDisplayStateChanged(@NotNull Context context,
                                             @NotNull CalculatorEventData calculatorEventData,
                                             @NotNull CalculatorDisplayViewState displayViewState) {
        for (Class<?> externalListener : externalListeners) {
            final Intent intent = new Intent(DISPLAY_STATE_CHANGED_ACTION);
            intent.setClass(context, externalListener);
            intent.putExtra(EVENT_ID_EXTRA, calculatorEventData.getEventId());
            intent.putExtra(DISPLAY_STATE_EXTRA, (Parcelable) new ParcelableCalculatorDisplayViewState(displayViewState));
            context.sendBroadcast(intent);
            CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed broadcast sent");
        }
    }

    public static void addExternalListener(@NotNull Class<?> externalCalculatorClass) {
        externalListeners.add(externalCalculatorClass);
    }

    public static boolean removeExternalListener(@NotNull Class<?> externalCalculatorClass) {
        return externalListeners.remove(externalCalculatorClass);
    }
}
