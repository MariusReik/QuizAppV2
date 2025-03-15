package no.hvl.quizzoblig2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.ViewModelProvider;

import org.junit.Before;
import org.junit.Test;

import no.hvl.quizzoblig2.ui.quiz.QuizFragment;
import no.hvl.quizzoblig2.ui.quiz.QuizViewModel;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class QuizFragmentTest {

    private FragmentScenario<QuizFragment> scenario;

    @Before
    public void setUp() {
        scenario = FragmentScenario.launchInContainer(QuizFragment.class);
    }

    @Test
    public void testCorrectAnswerIncreasesScore() {
        // Get the correct answer from the ViewModel
        final String[] correctAnswer = new String[1];
        scenario.onFragment(fragment -> {
            QuizViewModel viewModel = new ViewModelProvider(fragment).get(QuizViewModel.class);
            // Set a simple test item with known correct answer
            viewModel.setTestItem("test_image_uri", "Correct Answer");
            correctAnswer[0] = "Correct Answer";
        });

        // Now click the button with the correct answer
        Espresso.onView(ViewMatchers.withText(correctAnswer[0]))
                .perform(ViewActions.click());

        // Check score has increased
        Espresso.onView(ViewMatchers.withId(R.id.textViewScore))
                .check(matches(withText("Score: 1")));
    }

    @Test
    public void testWrongAnswerDoesNotIncreaseScore() {
        // Click a wrong answer
        // This assumes there's at least one wrong answer option displayed
        Espresso.onView(ViewMatchers.withId(R.id.buttonOption2))
                .perform(ViewActions.click());

        // Assert score remains 0
        Espresso.onView(ViewMatchers.withId(R.id.textViewScore))
                .check(matches(withText("Score: 0")));
    }
}


