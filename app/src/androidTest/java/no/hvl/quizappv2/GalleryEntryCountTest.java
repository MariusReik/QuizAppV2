package no.hvl.quizappv2;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import no.hvl.quizappv2.view.GalleryActivity;

@RunWith(AndroidJUnit4.class)
public class GalleryEntryCountTest {

    @Test
    public void testEntryCountAfterAddAndDelete() {
        // Start GalleryActivity direkte
        ActivityScenario<GalleryActivity> scenario = ActivityScenario.launch(GalleryActivity.class);

        // Antall oppføringer før endringer
        Espresso.onView(ViewMatchers.withId(R.id.recyclerview))
                .check(ViewAssertions.matches(ViewMatchers.hasChildCount(3))); // Anta at det er 3 oppføringer

        // Legg til en ny oppføring (bruk Intent Stubbing for å returnere et bilde)
        // Dette krever at du mocker Intent-oppførselen, som vi ikke dekker her.

        // Slett en oppføring
        Espresso.onView(ViewMatchers.withText("Cute Fox")) // Anta at dette er navnet på en oppføring
                .perform(ViewActions.longClick());

        // Sjekk at antallet oppføringer er redusert med 1
        Espresso.onView(ViewMatchers.withId(R.id.recyclerview))
                .check(ViewAssertions.matches(ViewMatchers.hasChildCount(2)));
    }
}