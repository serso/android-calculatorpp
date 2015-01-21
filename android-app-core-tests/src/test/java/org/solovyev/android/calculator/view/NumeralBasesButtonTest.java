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

import static jscl.NumeralBase.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.solovyev.android.calculator.CalculatorTestUtils.staticSetUp;

@RunWith(RobolectricTestRunner.class)
public class NumeralBasesButtonTest {

	private NumeralBasesButton button;

	@Before
	public void setUp() throws Exception {
		staticSetUp();

		final Activity context = Robolectric.buildActivity(Activity.class).create().get();
		final ShadowActivity activity = Robolectric.shadowOf(context);
		button = new NumeralBasesButton(context, activity.createAttributeSet(new ArrayList<Attribute>(), NumeralBasesButton.class));
	}

	@Test
	public void testShouldReturnDifferentColorsForDifferentNumeralBase() throws Exception {
		button.setNumeralBase(dec);

		assertEquals(button.getDirectionTextColor(dec.name()), button.getDirectionTextColor(dec.name()));
		assertEquals(button.getDirectionTextColor(hex.name()), button.getDirectionTextColor(bin.name()));
		assertNotSame(button.getDirectionTextColor(dec.name()), button.getDirectionTextColor(bin.name()));
		assertNotSame(button.getDirectionTextColor(dec.name()), button.getDirectionTextColor(hex.name()));
		assertNotSame(button.getDirectionTextColor(dec.name()), button.getDirectionTextColor(oct.name()));
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
