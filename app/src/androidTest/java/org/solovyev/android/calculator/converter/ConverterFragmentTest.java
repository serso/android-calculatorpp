package org.solovyev.android.calculator.converter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.solovyev.android.calculator.CalculatorActivity;
import org.solovyev.android.calculator.R;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConverterFragmentTest {

    @Rule
    public ActivityTestRule<CalculatorActivity> rule = new ActivityTestRule<>(CalculatorActivity.class);

    @Test
    public void openConversionDialog() {
        openActionBarOverflowOrOptionsMenu(rule.getActivity());
        onView(withText(R.string.c_conversion_tool)).perform(click());
        onView(withId(R.id.converter_edittext_from)).perform(clearText(), typeText("7"));
        onView(withId(R.id.converter_spinner_from)).perform(click());
        onView(withText("day")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.converter_spinner_to)).perform(click());
        onView(withText("week")).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.converter_edittext_to)).check(matches(withText("1E0")));
    }
}
