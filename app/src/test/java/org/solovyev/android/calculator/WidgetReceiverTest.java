package org.solovyev.android.calculator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class WidgetReceiverTest {

    private WidgetReceiver widgetReceiver;

    @Before
    public void setUp() throws Exception {
        widgetReceiver.keyboard = Mockito.mock(Keyboard.class);

    }

    @Test
    public void testShouldPressButtonOnIntent() throws Exception {
        final Intent intent = newButtonClickedIntent(application, four);
        widgetReceiver = new WidgetReceiver();
        widgetReceiver.onReceive(application, intent);

        verify(widgetReceiver.keyboard, times(1)).buttonPressed(Mockito.anyString());
        verify(widgetReceiver.keyboard, times(1)).buttonPressed("4");
    }

    @Test
    public void testShouldDoNothingIfButtonInvalid() throws Exception {
        final Intent intent = new Intent(application, WidgetReceiver.class);
        intent.setAction(ACTION_BUTTON_PRESSED);
        intent.putExtra(ACTION_BUTTON_ID_EXTRA, "test!@");
        widgetReceiver.onReceive(application, intent);

        verify(widgetReceiver.keyboard, times(0)).buttonPressed(Mockito.anyString());
    }
}
