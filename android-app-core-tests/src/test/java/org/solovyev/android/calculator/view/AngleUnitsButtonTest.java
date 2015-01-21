package org.solovyev.android.calculator.view;

import android.app.Activity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.res.Attribute;
import org.robolectric.shadows.ShadowActivity;

import java.util.ArrayList;

import static jscl.AngleUnit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.solovyev.android.calculator.CalculatorTestUtils.staticSetUp;

@RunWith(RobolectricTestRunner.class)
public class AngleUnitsButtonTest {

	private AngleUnitsButton button;

	@Before
	public void setUp() throws Exception {
		staticSetUp();

		final Activity context = Robolectric.buildActivity(Activity.class).create().get();
		final ShadowActivity activity = Robolectric.shadowOf(context);
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
