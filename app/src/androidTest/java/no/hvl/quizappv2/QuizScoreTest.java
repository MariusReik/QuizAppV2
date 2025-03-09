package no.hvl.quizappv2;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import no.hvl.quizappv2.view.QuizActivity;

@RunWith(AndroidJUnit4.class)
public class QuizScoreTest {

    @Test
    public void testScoreUpdate() {
        // Start QuizActivity direkte (uten å gå gjennom MainActivity)
        ActivityScenario<QuizActivity> scenario = ActivityScenario.launch(QuizActivity.class);

        // Svar på et spørsmål (antakeligvis riktig svar)
        Espresso.onView(ViewMatchers.withId(R.id.answerButton1)).perform(ViewActions.click());

        // Sjekk at scoren er oppdatert
        Espresso.onView(ViewMatchers.withId(R.id.scoreTextView))
                .check(ViewAssertions.matches(ViewMatchers.withText("1")));
    }
}