package org.solovyev.android.calculator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;
import static org.solovyev.android.calculator.WidgetReceiver.ACTION_BUTTON_ID_EXTRA;
import static org.solovyev.android.calculator.WidgetReceiver.ACTION_BUTTON_PRESSED;
import static org.solovyev.android.calculator.WidgetReceiver.newButtonClickedIntent;
import static org.solovyev.android.calculator.buttons.CppButton.four;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.solovyev.android.calculator.history.History;

@RunWith(RobolectricTestRunner.class)
public class WidgetReceiverTest {

    private Keyboard keyboard;
    private History history;
    private WidgetReceiver widgetReceiver;

    @Before
    public void setUp() throws Exception {
        widgetReceiver = new WidgetReceiver();
        widgetReceiver.keyboard = keyboard = mock(Keyboard.class);
        widgetReceiver.history = history = mock(History.class);
        when(history.isLoaded()).thenReturn(true);
    }

    @Test
    public void testShouldPressButtonOnIntent() throws Exception {
        final Intent intent = newButtonClickedIntent(application, four);
        widgetReceiver.onReceive(application, intent);

        verify(keyboard).buttonPressed(Mockito.anyString());
        verify(keyboard).buttonPressed("4");
    }

    @Test
    public void testShouldDoNothingIfButtonInvalid() throws Exception {
        final Intent intent = new Intent(application, WidgetReceiver.class);
        intent.setAction(ACTION_BUTTON_PRESSED);
        intent.putExtra(ACTION_BUTTON_ID_EXTRA, "test!@");
        widgetReceiver.onReceive(application, intent);

        verify(keyboard, never()).buttonPressed(Mockito.anyString());
    }
}
