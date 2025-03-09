package no.hvl.quizappv2;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import no.hvl.quizappv2.view.MainActivity;
import no.hvl.quizappv2.view.QuizActivity;

@RunWith(AndroidJUnit4.class)
public class MainActivityNavigationTest {

    @Test
    public void testNavigationToQuizActivity() {
        // Start MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Klikk på "Quiz"-knappen
        Espresso.onView(ViewMatchers.withId(R.id.quizButton)).perform(ViewActions.click());

        // Sjekk at QuizActivity er åpen
        Espresso.onView(ViewMatchers.withId(R.id.questionTextView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}