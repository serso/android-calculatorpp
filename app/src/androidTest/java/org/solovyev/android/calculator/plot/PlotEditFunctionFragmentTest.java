package org.solovyev.android.calculator.plot;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.functions.CppFunction;
import org.solovyev.android.calculator.functions.FunctionParamsView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

public class PlotEditFunctionFragmentTest extends BasePlotTest {

    @Test
    public void testShouldAddFunction() throws Exception {
        final CppFunction function = CppFunction.builder("", "x + y").withParameters("x", "y").build();
        addFunction(function);

        openFunctionsList();
        onView(withId(R.id.function_name)).check(matches(withText("x+y")));
    }

    @Test
    public void testShouldHaveOnlyTwoParameters() throws Exception {
        openFunctionEditor();

        onView(withId(R.id.function_params_add)).check(matches(isDisplayed()));

        onView(withId(R.id.function_params_add)).perform(click());
        onView(withId(R.id.function_params_add)).check(matches(isDisplayed()));
        onView(withId(R.id.function_params_add)).perform(click());

        onView(withId(R.id.function_params_add)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testShouldProvideDefaultParamNames() throws Exception {
        openFunctionEditor();

        onView(withId(R.id.function_params_add)).perform(click());
        onView(allOf(hasFocus(), withTagValue(Matchers.<Object>equalTo(FunctionParamsView.PARAM_VIEW_TAG)))).check(matches(withText("x")));
    }

    @Test
    public void testShouldSelectParamOnFocus() throws Exception {
        openFunctionEditor();

        onView(withId(R.id.function_params_add)).perform(click());
        // check "select-on-focus" attribute
        onView(allOf(hasFocus(), withTagValue(Matchers.<Object>equalTo(FunctionParamsView.PARAM_VIEW_TAG)))).perform(typeTextIntoFocusedView("y"));
        onView(allOf(hasFocus(), withTagValue(Matchers.<Object>equalTo(FunctionParamsView.PARAM_VIEW_TAG)))).check(matches(withText("y")));
    }
}
