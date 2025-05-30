package no.hvl.quizzoblig2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.hvl.quizzoblig2.data.GalleryRepository;
import no.hvl.quizzoblig2.data.db.GalleryItem;
import no.hvl.quizzoblig2.ui.main.MainActivity;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Add test images to ensure there are enough for quiz
        activityScenarioRule.getScenario().onActivity(activity -> {
            // Create a repository
            GalleryRepository repository = new GalleryRepository(activity.getApplication());

            // Add three test images from your resources
            repository.insert(new GalleryItem("Dog", "android.resource://no.hvl.quizzoblig2/drawable/dog"));
            repository.insert(new GalleryItem("Cat", "android.resource://no.hvl.quizzoblig2/drawable/cat"));
            repository.insert(new GalleryItem("Fox", "android.resource://no.hvl.quizzoblig2/drawable/fox"));

            // Give time for database operations to complete
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testNavigationToQuiz() {
        Espresso.onView(ViewMatchers.withId(R.id.btnQuiz))
                .perform(ViewActions.click());

        // Check if quizfragment shows
        Espresso.onView(ViewMatchers.withId(R.id.imageViewQuestion))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationToGallery() {
        Espresso.onView(ViewMatchers.withId(R.id.btnGallery))
                .perform(ViewActions.click());

        // Check if Gallery-fragmentet shows
        Espresso.onView(ViewMatchers.withId(R.id.recyclerViewGallery))
                .check(matches(isDisplayed()));
    }
}