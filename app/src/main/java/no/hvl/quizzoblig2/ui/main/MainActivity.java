package no.hvl.quizzoblig2.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import no.hvl.quizzoblig2.R;
import no.hvl.quizzoblig2.ui.gallery.GalleryFragment;
import no.hvl.quizzoblig2.ui.quiz.QuizFragment;

public class MainActivity extends AppCompatActivity {
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
            // Vis knappene kun hvis det ikke er aktive fragmenter
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                btnGallery.setVisibility(View.VISIBLE);
                btnQuiz.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);  // Legg til i backstack slik at vi kan gå tilbake
        transaction.commit();

        // Skjul knappene når et fragment åpnes
        btnGallery.setVisibility(View.GONE);
        btnQuiz.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();  // Gå tilbake til forrige fragment
        } else {
            super.onBackPressed();  // Lukk appen hvis vi er på hovedskjermen
        }
    }
}
