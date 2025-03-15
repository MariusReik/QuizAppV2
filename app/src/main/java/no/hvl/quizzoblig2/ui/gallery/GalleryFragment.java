package no.hvl.quizzoblig2.ui.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import no.hvl.quizzoblig2.R;
import no.hvl.quizzoblig2.data.db.GalleryItem;

public class GalleryFragment extends Fragment {
    private GalleryViewModel viewModel;
    private GalleryAdapter adapter;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        String imageName = "New Image " + System.currentTimeMillis();
                        viewModel.insert(new GalleryItem(imageName, selectedImage.toString()));
                    }
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewGallery);
        adapter = new GalleryAdapter(getContext(), new ArrayList<>());

        // Set the long click listener for deletion
        adapter.setOnItemLongClickListener(item -> {
            // Show confirmation dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        viewModel.delete(item);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        viewModel.getAllGalleryItems().observe(getViewLifecycleOwner(), galleryItems -> {
            adapter.updateGalleryItems(galleryItems);
        });

        Button btnAdd = root.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> openImagePicker());

        Button btnSortAZ = root.findViewById(R.id.btnSortAZ);
        btnSortAZ.setOnClickListener(v -> viewModel.sortGalleryItems(true));

        Button btnSortZA = root.findViewById(R.id.btnSortZA);
        btnSortZA.setOnClickListener(v -> viewModel.sortGalleryItems(false));

        return root;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}




