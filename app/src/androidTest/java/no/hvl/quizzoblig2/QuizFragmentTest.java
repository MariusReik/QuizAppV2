package no.hvl.quizzoblig2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import no.hvl.quizzoblig2.data.GalleryRepository;
import no.hvl.quizzoblig2.data.db.GalleryItem;
import no.hvl.quizzoblig2.ui.quiz.QuizFragment;
import no.hvl.quizzoblig2.ui.quiz.QuizViewModel;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class QuizFragmentTest {
    private FragmentScenario<QuizFragment> scenario;
    private GalleryRepository repository;

    @Before
    public void setUp() {
        repository = new GalleryRepository(ApplicationProvider.getApplicationContext());

        repository.insert(new GalleryItem("Dog", "android.resource://no.hvl.quizzoblig2/drawable/dog"));
        repository.insert(new GalleryItem("Cat", "android.resource://no.hvl.quizzoblig2/drawable/cat"));
        repository.insert(new GalleryItem("Fox", "android.resource://no.hvl.quizzoblig2/drawable/fox"));


        scenario = FragmentScenario.launchInContainer(QuizFragment.class);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCorrectAnswerIncreasesScore() {
        onView(withId(R.id.buttonOption1)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOption2)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOption3)).check(matches(isDisplayed()));

        final String[] correctAnswer = new String[1];
        scenario.onFragment(fragment -> {
            QuizViewModel viewModel = new ViewModelProvider(fragment).get(QuizViewModel.class);
            viewModel.setTestItem("test_image_uri", "Correct Answer");
            correctAnswer[0] = "Correct Answer";
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText(correctAnswer[0])).perform(click());
        onView(withId(R.id.textViewScore)).check(matches(withText("Score: 1")));
    }

    @Test
    public void testWrongAnswerDoesNotIncreaseScore() {
        onView(withId(R.id.buttonOption1)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOption2)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOption3)).check(matches(isDisplayed()));

        scenario.onFragment(fragment -> {
            QuizViewModel viewModel = new ViewModelProvider(fragment).get(QuizViewModel.class);
            viewModel.setTestItem("test_image_uri", "Right Answer");
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Wrong Answer 1")).perform(click());
        onView(withId(R.id.textViewScore)).check(matches(withText("Score: 0")));
    }
}

