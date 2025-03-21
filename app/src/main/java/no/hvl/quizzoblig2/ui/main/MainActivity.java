package no.hvl.quizzoblig2.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import no.hvl.quizzoblig2.R;
import no.hvl.quizzoblig2.data.GalleryRepository;
import no.hvl.quizzoblig2.data.db.GalleryItem;
import no.hvl.quizzoblig2.ui.gallery.GalleryFragment;
import no.hvl.quizzoblig2.ui.quiz.QuizFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String CURRENT_FRAGMENT = "current_fragment";

    private Button btnGallery, btnQuiz;
    private View mainContent;
    private View fragmentContainer;
    private String currentFragmentTag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGallery = findViewById(R.id.btnGallery);
        btnQuiz = findViewById(R.id.btnQuiz);
        mainContent = findViewById(R.id.main_content);
        fragmentContainer = findViewById(R.id.fragment_container);

        btnGallery.setOnClickListener(v -> openFragment(new GalleryFragment(), "gallery"));
        btnQuiz.setOnClickListener(v -> openFragment(new QuizFragment(), "quiz"));

        // Restore fragment state if there was one
        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT);

            // If we had an active fragment before, restore UI state
            if (currentFragmentTag != null) {
                showMainMenu(false);
            }
        }

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // Show main menu if there are no fragments
                showMainMenu(true);
                currentFragmentTag = null;
            }
        });

        // Add sample images if database is empty
        addSampleImages();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save which fragment was active
        if (currentFragmentTag != null) {
            outState.putString(CURRENT_FRAGMENT, currentFragmentTag);
        }
    }

    private void addSampleImages() {
        // Create a repository
        GalleryRepository repository = new GalleryRepository(getApplication());

        // Add demo images using Android resource URIs
        repository.getAllGalleryItems().observe(this, items -> {
            if (items == null || items.isEmpty()) {
                Log.d(TAG, "Adding sample images to database");

                // Get the resource IDs
                int dogId = getResources().getIdentifier("dog", "drawable", getPackageName());
                int catId = getResources().getIdentifier("cat", "drawable", getPackageName());
                int foxId = getResources().getIdentifier("fox", "drawable", getPackageName());

                // Convert to proper URI format
                String dogUri = "android.resource://" + getPackageName() + "/" + dogId;
                String catUri = "android.resource://" + getPackageName() + "/" + catId;
                String foxUri = "android.resource://" + getPackageName() + "/" + foxId;

                // Add the images
                repository.insert(new GalleryItem("Dog", dogUri));
                repository.insert(new GalleryItem("Cat", catUri));
                repository.insert(new GalleryItem("Fox", foxUri));

                Log.d(TAG, "Added dog, cat, and fox images to database");

                // Only observe once
                repository.getAllGalleryItems().removeObservers(this);
            }
        });
    }

    private void openFragment(Fragment fragment, String tag) {
        // Show fragment container and hide main content
        showMainMenu(false);
        currentFragmentTag = tag;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showMainMenu(boolean show) {
        mainContent.setVisibility(show ? View.VISIBLE : View.GONE);
        fragmentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}