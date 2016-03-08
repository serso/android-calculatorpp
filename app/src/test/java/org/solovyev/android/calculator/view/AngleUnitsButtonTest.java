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

import static jscl.AngleUnit.deg;
import static jscl.AngleUnit.grad;
import static jscl.AngleUnit.rad;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class AngleUnitsButtonTest {

    private AngleUnitsButton button;

    @Before
    public void setUp() throws Exception {
        final Activity context = Robolectric.buildActivity(Activity.class).create().get();
        final ShadowActivity activity = Shadows.shadowOf(context);
        button = new AngleUnitsButton(context, activity.createAttributeSet(new ArrayList<Attribute>(), AngleUnitsButton.class));
    }

    @Test
    public void testIsCurrentAngleUnits() throws Exception {
        button.setAngleUnit(rad);
        assertTrue(button.isCurrentAngleUnits(rad.name()));
        assertFalse(button.isCurrentAngleUnits(deg.name()));
        assertFalse(button.isCurrentAngleUnits(grad.name()));
    }

    @Test
    public void testInvalidateShouldBeCalledOnlyWhenChangeIsDone() throws Exception {
        button.setAngleUnit(rad);

        button = Mockito.spy(button);

        button.setAngleUnit(deg);
        verify(button, times(1)).invalidate();

        button.setAngleUnit(deg);
        verify(button, times(1)).invalidate();
    }
}
