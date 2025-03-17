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
    private Button btnGallery, btnQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGallery = findViewById(R.id.btnGallery);
        btnQuiz = findViewById(R.id.btnQuiz);

        btnGallery.setOnClickListener(v -> openFragment(new GalleryFragment()));
        btnQuiz.setOnClickListener(v -> openFragment(new QuizFragment()));

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            // Show buttons only if there are no active fragments
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                btnGallery.setVisibility(View.VISIBLE);
                btnQuiz.setVisibility(View.VISIBLE);
            }
        });

        // Add sample images if database is empty
        addSampleImages();
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

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);  // Add to back stack so we can go back
        transaction.commit();

        // Hide buttons when a fragment opens
        btnGallery.setVisibility(View.GONE);
        btnQuiz.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();  // Go back to previous fragment
        } else {
            super.onBackPressed();  // Close app if we're on the main screen
        }
    }
}
