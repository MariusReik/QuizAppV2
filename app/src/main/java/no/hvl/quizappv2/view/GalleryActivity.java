package no.hvl.quizappv2.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import no.hvl.quizappv2.R;
import no.hvl.quizappv2.adapter.GalleryAdapter;
import no.hvl.quizappv2.entity.PhotoEntry;
import no.hvl.quizappv2.viewmodel.GalleryViewModel;

public class GalleryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                PhotoEntry newEntry = new PhotoEntry("New Image", selectedImageUri.toString());
                viewModel.insert(newEntry);
            }
        }
    }
}