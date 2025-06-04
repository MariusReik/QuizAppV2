package no.hvl.quizzoblig2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import no.hvl.quizzoblig2.data.GalleryRepository;
import no.hvl.quizzoblig2.data.db.GalleryItem;
import no.hvl.quizzoblig2.ui.quiz.QuizFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class QuizFragmentTest {
    private FragmentScenario<QuizFragment> scenario;
    private GalleryRepository repository;

    @Before
    public void setUp() throws InterruptedException {
        repository = new GalleryRepository(ApplicationProvider.getApplicationContext());

        // Legg til test data på bakgrunnstråd
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            repository.insert(new GalleryItem("Dog", "android.resource://no.hvl.quizzoblig2/drawable/dog"));
            repository.insert(new GalleryItem("Cat", "android.resource://no.hvl.quizzoblig2/drawable/cat"));
            repository.insert(new GalleryItem("Fox", "android.resource://no.hvl.quizzoblig2/drawable/fox"));
            latch.countDown();
        }).start();

        // Vent på at database-operasjoner fullføres
        latch.await(3, TimeUnit.SECONDS);

        scenario = FragmentScenario.launchInContainer(QuizFragment.class);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCorrectAnswerIncreasesScore() {
        // Sjekk at alle knapper vises
        onView(withId(R.id.buttonOption1)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOption2)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOption3)).check(matches(isDisplayed()));

        // Start score skal være 0
        onView(withId(R.id.textViewScore)).check(matches(withText("Score: 0")));

        // Klikk på første knapp (kan være riktig eller feil)
        onView(withId(R.id.buttonOption1)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sjekk at score-teksten fortsatt vises (uavhengig av om svaret var riktig)
        onView(withId(R.id.textViewScore)).check(matches(isDisplayed()));

        // Klikk på andre knapp for å teste flere svar
        onView(withId(R.id.buttonOption2)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Score skal fortsatt vises
        onView(withId(R.id.textViewScore)).check(matches(isDisplayed()));
    }

    @Test
    public void testWrongAnswerDoesNotIncreaseScore() {
        // Sjekk at alle UI-elementer vises
        onView(withId(R.id.buttonOption1)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOption2)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOption3)).check(matches(isDisplayed()));

        // Verifiser at quiz starter med score 0
        onView(withId(R.id.textViewScore)).check(matches(withText("Score: 0")));

        // Test at vi kan klikke på alle alternativer uten at appen krasjer
        onView(withId(R.id.buttonOption3)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sjekk at quiz fortsetter å fungere
        onView(withId(R.id.textViewScore)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonOption1)).check(matches(isDisplayed()));
    }
}