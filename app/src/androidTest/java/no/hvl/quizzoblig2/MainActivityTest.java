package no.hvl.quizzoblig2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import no.hvl.quizzoblig2.ui.main.MainActivity;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigationToQuiz() {
        Espresso.onView(ViewMatchers.withId(R.id.btnQuiz))
                .perform(ViewActions.click());

        // Sjekk at Quiz-fragmentet vises
        Espresso.onView(ViewMatchers.withId(R.id.imageViewQuestion))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationToGallery() {
        Espresso.onView(ViewMatchers.withId(R.id.btnGallery))
                .perform(ViewActions.click());

        // Sjekk at Gallery-fragmentet vises
        Espresso.onView(ViewMatchers.withId(R.id.recyclerViewGallery))
                .check(matches(isDisplayed()));
    }
}
