package org.solovyev.android.calculator.ga;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.CalculatorButton;
import org.solovyev.android.calculator.CalculatorClipboard;
import org.solovyev.android.calculator.CalculatorEngineImpl;
import org.solovyev.android.calculator.CalculatorImpl;
import org.solovyev.android.calculator.CalculatorKeyboard;
import org.solovyev.android.calculator.CalculatorNotifier;
import org.solovyev.android.calculator.CalculatorPreferenceService;
import org.solovyev.android.calculator.CalculatorSpecialButton;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.SystemOutCalculatorLogger;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.plot.CalculatorPlotter;

import java.util.Arrays;

import javax.annotation.Nonnull;

import jscl.math.function.CustomFunction;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class GaButtonFilterTest {

	@Nonnull
	private CalculatorEngineImpl engine;

	@Before
	public void setUp() throws Exception {
		engine = CalculatorTestUtils.newCalculatorEngine();
		Locator.getInstance().init(new CalculatorImpl(), engine, Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(CalculatorHistory.class), new SystemOutCalculatorLogger(), Mockito.mock(CalculatorPreferenceService.class), Mockito.mock(CalculatorKeyboard.class), Mockito.mock(CalculatorPlotter.class), null);
		Locator.getInstance().getEngine().init();
	}

	@Test
	public void testShouldIncludeOnlySystemFunctions() throws Exception {
		final GaButtonFilter filter = new GaButtonFilter();
		engine.getFunctionsRegistry().add(new CustomFunction.Builder(true, "ok", Arrays.asList("x"), "x"));
		engine.getFunctionsRegistry().add(new CustomFunction.Builder(false, "fail", Arrays.asList("x"), "x"));
		Assert.assertEquals("ok", filter.filter("ok"));
		Assert.assertEquals("ok", filter.filter("ok()"));
		Assert.assertNotEquals("fail", filter.filter("fail"));
		Assert.assertNotEquals("fail", filter.filter("fail()"));
	}

	@Test
	public void testShouldIncludeButtons() throws Exception {
		final GaButtonFilter filter = new GaButtonFilter();
		final String copyText = CalculatorButton.erase.getOnClickText();
		final String copyLongText = CalculatorButton.erase.getOnLongClickText();
		Assert.assertEquals(copyText, filter.filter(copyText));
		Assert.assertEquals(copyLongText, filter.filter(copyLongText));
	}

	@Test
	public void testShouldIncludeSpecialButtons() throws Exception {
		final GaButtonFilter filter = new GaButtonFilter();
		final String label = CalculatorSpecialButton.functions_detached.getActionCode();
		Assert.assertEquals(label, filter.filter(label));
	}

	@Test
	public void testShouldIncludePostfixFunctions() throws Exception {
		final GaButtonFilter filter = new GaButtonFilter();
		Assert.assertEquals("!", filter.filter("!"));
		Assert.assertEquals("%", filter.filter("%"));
	}

	@Test
	public void testShouldIncludeOperations() throws Exception {
		final GaButtonFilter filter = new GaButtonFilter();
		Assert.assertEquals("*", filter.filter("*"));
		Assert.assertEquals("×", filter.filter("×"));
		Assert.assertEquals("-", filter.filter("-"));
		Assert.assertEquals("−", filter.filter("−"));
		Assert.assertEquals("^", filter.filter("^"));
		Assert.assertEquals("^2", filter.filter("^2"));
		Assert.assertEquals("+", filter.filter("+"));
		Assert.assertEquals("/", filter.filter("/"));
	}

	@Test
	public void testShouldInclude() throws Exception {
		final GaButtonFilter filter = new GaButtonFilter();
		Assert.assertEquals("i", filter.filter("i"));
		Assert.assertEquals("ln", filter.filter("ln"));
	}
}