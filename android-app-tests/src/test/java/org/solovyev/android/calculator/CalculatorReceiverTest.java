package org.solovyev.android.calculator;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Robolectric.application;
import static org.solovyev.android.calculator.CalculatorButton.four;
import static org.solovyev.android.calculator.CalculatorReceiver.ACTION_BUTTON_ID_EXTRA;
import static org.solovyev.android.calculator.CalculatorReceiver.ACTION_BUTTON_PRESSED;
import static org.solovyev.android.calculator.CalculatorReceiver.newButtonClickedIntent;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class CalculatorReceiverTest {

    @Test
    public void testShouldPressButtonOnIntent() throws Exception {
        Locator.setKeyboard(mock(CalculatorKeyboard.class));

        final Intent intent = newButtonClickedIntent(application, four);
        new CalculatorReceiver().onReceive(application, intent);

        verify(Locator.getInstance().getKeyboard(), times(1)).buttonPressed(Mockito.anyString());
        verify(Locator.getInstance().getKeyboard(), times(1)).buttonPressed("4");
    }

    @Test
    public void testShouldDoNothingIfButtonInvalid() throws Exception {
        Locator.setKeyboard(mock(CalculatorKeyboard.class));

        final Intent intent = new Intent(application, CalculatorReceiver.class);
        intent.setAction(ACTION_BUTTON_PRESSED);
        intent.putExtra(ACTION_BUTTON_ID_EXTRA, "test!@");
        new CalculatorReceiver().onReceive(application, intent);

        verify(Locator.getInstance().getKeyboard(), times(0)).buttonPressed(Mockito.anyString());
    }
}
