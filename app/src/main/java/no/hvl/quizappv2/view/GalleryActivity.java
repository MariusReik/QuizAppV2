package no.hvl.quizappv2.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import no.hvl.quizappv2.R;
import no.hvl.quizappv2.adapter.GalleryAdapter;
import no.hvl.quizappv2.viewmodel.GalleryViewModel;

public class GalleryActivity extends AppCompatActivity {

    private GalleryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final GalleryAdapter adapter = new GalleryAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        viewModel.getAllEntries().observe(this, adapter::setEntries);
    }
}