package org.solovyev.android.calculator.view;

import android.app.Activity;

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
import static jscl.AngleUnit.turns;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.solovyev.android.calculator.CalculatorTestUtils.staticSetUp;

@Config(constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class AngleUnitsButtonTest {

    private AngleUnitsButton button;

    @Before
    public void setUp() throws Exception {
        staticSetUp();

        final Activity context = Robolectric.buildActivity(Activity.class).create().get();
        final ShadowActivity activity = Shadows.shadowOf(context);
        button = new AngleUnitsButton(context, activity.createAttributeSet(new ArrayList<Attribute>(), AngleUnitsButton.class));
    }

    @Test
    public void testShouldReturnDifferentColorsForDifferentAngleUnits() throws Exception {
        button.setAngleUnit(deg);

        assertEquals(button.getDirectionTextColor(deg.name()), button.getDirectionTextColor(deg.name()));
        assertEquals(button.getDirectionTextColor(grad.name()), button.getDirectionTextColor(rad.name()));
        assertNotSame(button.getDirectionTextColor(deg.name()), button.getDirectionTextColor(rad.name()));
        assertNotSame(button.getDirectionTextColor(deg.name()), button.getDirectionTextColor(grad.name()));
        assertNotSame(button.getDirectionTextColor(deg.name()), button.getDirectionTextColor(turns.name()));
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
