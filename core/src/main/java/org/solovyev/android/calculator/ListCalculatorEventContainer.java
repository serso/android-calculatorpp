package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.ListListenersContainer;

import java.util.Arrays;
import java.util.List;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:42
 */
public class ListCalculatorEventContainer implements CalculatorEventContainer {

    @NotNull
    private static final String TAG = "CalculatorEventData";

    @NotNull
    private final ListListenersContainer<CalculatorEventListener> listeners = new ListListenersContainer<CalculatorEventListener>();

    @Override
    public void addCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
        listeners.addListener(calculatorEventListener);
    }

    @Override
    public void removeCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
        listeners.removeListener(calculatorEventListener);
    }

    @Override
    public void fireCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        fireCalculatorEvents(Arrays.asList(new CalculatorEvent(calculatorEventData, calculatorEventType, data)));
    }

    @Override
    public void fireCalculatorEvents(@NotNull List<CalculatorEvent> calculatorEvents) {
        final List<CalculatorEventListener> listeners = this.listeners.getListeners();

        //final CalculatorLogger logger = Locator.getInstance().getLogger();

        for (CalculatorEvent e : calculatorEvents) {
            //Locator.getInstance().getLogger().debug(TAG, "Event fired: " + e.getCalculatorEventType());
            for (CalculatorEventListener listener : listeners) {
                /*long startTime = System.currentTimeMillis();*/
                listener.onCalculatorEvent(e.getCalculatorEventData(), e.getCalculatorEventType(), e.getData());
/*                long endTime = System.currentTimeMillis();
                long totalTime = (endTime - startTime);
                if ( totalTime > 300 ) {
                    logger.debug(TAG + "_" + e.getCalculatorEventData().getEventId(), "Started event: " + e.getCalculatorEventType() + " with data: " + e.getData() + " for: " + listener.getClass().getSimpleName());
                    logger.debug(TAG + "_" + e.getCalculatorEventData().getEventId(), "Total time, ms: " + totalTime);
                }*/
            }
        }
    }
}
