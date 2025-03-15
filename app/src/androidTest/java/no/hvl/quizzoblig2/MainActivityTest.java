package no.hvl.quizzoblig2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import no.hvl.quizzoblig2.R;
import no.hvl.quizzoblig2.ui.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigateToGallery() {
        onView(withId(R.id.btnGallery)).perform(click());
        onView(withId(R.id.recyclerViewGallery)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToQuiz() {
        onView(withId(R.id.btnQuiz)).perform(click());
        onView(withId(R.id.imageViewQuestion)).check(matches(isDisplayed()));
    }
}
