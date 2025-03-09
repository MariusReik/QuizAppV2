package no.hvl.quizappv2.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import no.hvl.quizappv2.R;
import no.hvl.quizappv2.adapter.GalleryAdapter;
import no.hvl.quizappv2.viewmodel.GalleryViewModel;

public class GalleryActivity extends AppCompatActivity {

    private GalleryViewModel viewModel;
    private GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Oppsett av RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new GalleryAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Hent ViewModel
        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        // oppdater RecyclerView
        viewModel.getAllEntries().observe(this, adapter::setEntries);

        // Knapp for å legge til nye bilder
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            // Logikk for å legge til et nytt bilde
        });

        // Knapp for sortering A-Å
        FloatingActionButton fabSortAsc = findViewById(R.id.fabSortAsc);
        fabSortAsc.setOnClickListener(v -> viewModel.sortByNameAsc());

        // Knapp for sortering Å-A
        FloatingActionButton fabSortDesc = findViewById(R.id.fabSortDesc);
        fabSortDesc.setOnClickListener(v -> viewModel.sortByNameDesc());
    }
}