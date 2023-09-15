package org.solovyev.android.calculator.wizard;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.solovyev.android.calculator.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static net.slideshare.mobile.test.util.OrientationChangeAction.orientationLandscape;
import static net.slideshare.mobile.test.util.OrientationChangeAction.orientationPortrait;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WizardActivityTest {

    @Rule
    public ActivityTestRule<WizardActivity> rule = new ActivityTestRule<>(WizardActivity.class);

    @Test
    public void shouldShowConfirmationDialogOnSkip() {
        onView(withText(R.string.cpp_wizard_skip)).perform(click());
        onView(withText(R.string.cpp_wizard_finish_confirmation_title)).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void testShouldGoThroughTheWizard() throws Exception {
        onView(withText(R.string.cpp_wizard_start)).perform(click());
        onView(withId(R.id.pager)).check(matches(allOf(
                hasDescendant(withText(R.string.cpp_wizard_mode_title)),
                not(hasDescendant(withText(R.string.cpp_wizard_welcome_title))))));

        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).check(matches(hasDescendant(withText(R.string.cpp_wizard_theme_title))));

        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).check(matches(hasDescendant(withText(R.string.cpp_wizard_onscreen_description))));

        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).check(matches(hasDescendant(withText(R.string.cpp_wizard_dragbutton_description))));

        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).check(matches(hasDescendant(withText(R.string.cpp_wizard_final_done))));

        onView(withText(R.string.cpp_wizard_final_done)).perform(click());
    }

    @Test
    public void testShouldPreserveStepOnScreenRotation() throws Exception {
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(isRoot()).perform(orientationLandscape());
        onView(withId(R.id.pager)).check(matches(hasDescendant(withText(R.string.cpp_wizard_mode_title))));
        onView(isRoot()).perform(orientationPortrait());
        onView(withId(R.id.pager)).check(matches(hasDescendant(withText(R.string.cpp_wizard_mode_title))));
    }
}
