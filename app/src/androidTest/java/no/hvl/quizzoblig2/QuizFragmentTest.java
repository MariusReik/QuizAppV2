package no.hvl.quizzoblig2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.fragment.app.testing.FragmentScenario;

import org.junit.Before;
import org.junit.Test;

import no.hvl.quizzoblig2.ui.quiz.QuizFragment;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class QuizFragmentTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(QuizFragment.class);
    }

    @Test
    public void testCorrectAnswerIncreasesScore() {
        // Klikker på det første alternativet (antas å være riktig)
        Espresso.onView(ViewMatchers.withId(R.id.buttonOption1))
                .perform(ViewActions.click());

        // Sjekk at score har økt
        Espresso.onView(ViewMatchers.withId(R.id.textViewScore))
                .check(matches(withText("Score: 1")));
    }

    @Test
    public void testWrongAnswerDoesNotIncreaseScore() {
        // Klikker på det andre alternativet (antas å være feil)
        Espresso.onView(ViewMatchers.withId(R.id.buttonOption2))
                .perform(ViewActions.click());

        // Sjekk at score fortsatt er 0
        Espresso.onView(ViewMatchers.withId(R.id.textViewScore))
                .check(matches(withText("Score: 0")));
    }
}
