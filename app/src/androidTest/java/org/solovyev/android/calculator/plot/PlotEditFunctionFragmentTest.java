package org.solovyev.android.calculator.plot;

import org.junit.Test;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.functions.CppFunction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class PlotEditFunctionFragmentTest extends BasePlotTest {

    @Test
    public void testShouldAddFunction() throws Exception {
        final CppFunction function = CppFunction.builder("", "x + y").withParameters("x", "y").build();
        addFunction(function);

        openFunctionsList();
        onView(withId(R.id.function_name)).check(matches(withText("x+y")));
    }
}