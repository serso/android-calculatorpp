package org.solovyev.android.calculator.view;

import android.app.Activity;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.res.Attribute;
import org.robolectric.shadows.ShadowActivity;
import org.solovyev.android.calculator.BuildConfig;

import java.util.ArrayList;

import static jscl.NumeralBase.bin;
import static jscl.NumeralBase.dec;
import static jscl.NumeralBase.hex;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class NumeralBasesButtonTest {

    private NumeralBasesButton button;

    @Before
    public void setUp() throws Exception {
        final Activity context = Robolectric.buildActivity(Activity.class).create().get();
        final ShadowActivity activity = Shadows.shadowOf(context);
        button = new NumeralBasesButton(context, activity.createAttributeSet(new ArrayList<Attribute>(), NumeralBasesButton.class));
    }

    @Test
    public void testIsCurrentNumeralBase() throws Exception {
        button.setNumeralBase(dec);
        assertTrue(button.isCurrentNumberBase(dec.name()));
        assertFalse(button.isCurrentNumberBase(hex.name()));
        assertFalse(button.isCurrentNumberBase(bin.name()));
    }

    @Test
    public void testInvalidateShouldBeCalledOnlyWhenChangeIsDone() throws Exception {
        button.setNumeralBase(dec);

        button = Mockito.spy(button);

        button.setNumeralBase(hex);
        verify(button, times(1)).invalidate();

        button.setNumeralBase(hex);
        verify(button, times(1)).invalidate();
    }
}
