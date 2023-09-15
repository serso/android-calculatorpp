package org.solovyev.android.calculator.plot;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.TextUtils;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.functions.CppFunction;
import org.solovyev.android.calculator.functions.FunctionParamsView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BasePlotTest {
    @Rule
    public ActivityTestRule<PlotActivity> rule = new ActivityTestRule<>(PlotActivity.class);

    protected void addFunction(@NonNull CppFunction function) {
        openFunctionEditor();

        if (!TextUtils.isEmpty(function.getName())) {
            onView(withId(R.id.function_name)).perform(typeText(function.getName()));
        }

        for (String parameter : function.getParameters()) {
            onView(withId(R.id.function_params_add)).perform(click());
            onView(allOf(hasFocus(), withTagValue(Matchers.<Object>equalTo(FunctionParamsView.PARAM_VIEW_TAG)))).perform(click(), typeTextIntoFocusedView(parameter));
        }

        onView(withId(R.id.function_body)).perform(typeText(function.getBody()));
        onView(withText(R.string.cpp_done)).perform(click());
    }

    protected final void openFunctionEditor() {
        onView(withId(R.id.plot_view_frame)).perform(ViewActions.click());
        onView(withId(R.id.plot_add_function)).perform(click());
    }

    protected final void openFunctionsList() {
        onView(withId(R.id.plot_view_frame)).perform(ViewActions.click());
        onView(withId(R.id.plot_functions)).perform(click());
    }
}
