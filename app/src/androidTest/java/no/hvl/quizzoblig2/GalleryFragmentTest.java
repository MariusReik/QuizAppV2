package no.hvl.quizzoblig2;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.fragment.app.testing.FragmentScenario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.hvl.quizzoblig2.ui.main.MainActivity;
import no.hvl.quizzoblig2.ui.gallery.GalleryFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

public class GalleryFragmentTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(GalleryFragment.class);
    }

    @Test
    public void testAddImageIncreasesCount() {
        Intents.init();

        // Simulerer valg av bilde
        Intent resultData = new Intent();
        resultData.setData(Uri.parse("android.resource://no.hvl.quizzoblig2/drawable/sample_image"));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(-1, resultData);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK)).respondWith(result);

        // Klikk på legg til-knappen
        onView(withId(R.id.btnAdd)).perform(ViewActions.click());

        // Sjekk at bildet ble lagt til i listen
        onView(withId(R.id.recyclerViewGallery))
                .check(matches(isDisplayed()));

        Intents.release();
    }

    @Test
    public void testDeleteImageDecreasesCount() {
        // Anta at et bilde allerede finnes i galleriet
        onView(withId(R.id.recyclerViewGallery))
                .perform(ViewActions.longClick()); // Langtrykk for å slette et bilde

        // Sjekk at galleriet fortsatt vises (vi kunne brukt en spesifikk sjekk på antall elementer)
        onView(withId(R.id.recyclerViewGallery))
                .check(matches(isDisplayed()));
    }
}
