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
import android.widget.Toast;

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
    private boolean isSortedAZ = false;
    private boolean isSortedZA = false;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        String imageName = "Image " + System.currentTimeMillis();
                        GalleryItem item = new GalleryItem(imageName, selectedImage.toString());
                        viewModel.insert(item);
                        Toast.makeText(getContext(), "Image added successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore sort state if available
        if (savedInstanceState != null) {
            isSortedAZ = savedInstanceState.getBoolean("isSortedAZ", false);
            isSortedZA = savedInstanceState.getBoolean("isSortedZA", false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Use the generic identifier to let Android choose the appropriate layout
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewGallery);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GalleryAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

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

        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        // Observe gallery items based on the previous sort state
        if (isSortedAZ) {
            viewModel.sortGalleryItems(true);
            observeSortedItems();
        } else if (isSortedZA) {
            viewModel.sortGalleryItems(false);
            observeSortedItems();
        } else {
            observeUnsortedItems();
        }

        Button btnAdd = root.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> openImagePicker());

        Button btnSortAZ = root.findViewById(R.id.btnSortAZ);
        btnSortAZ.setOnClickListener(v -> {
            isSortedAZ = true;
            isSortedZA = false;
            viewModel.sortGalleryItems(true);
            observeSortedItems();
        });

        Button btnSortZA = root.findViewById(R.id.btnSortZA);
        btnSortZA.setOnClickListener(v -> {
            isSortedZA = true;
            isSortedAZ = false;
            viewModel.sortGalleryItems(false);
            observeSortedItems();
        });

        return root;
    }

    private void observeUnsortedItems() {
        // Clear any existing observers
        viewModel.getSortedGalleryItems().removeObservers(getViewLifecycleOwner());

        // Observe unsorted gallery items
        viewModel.getAllGalleryItems().observe(getViewLifecycleOwner(), galleryItems -> {
            if (galleryItems != null && !galleryItems.isEmpty()) {
                adapter.updateGalleryItems(galleryItems);
            } else {
                Toast.makeText(getContext(), "No images in gallery", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeSortedItems() {
        // Clear any existing observers
        viewModel.getAllGalleryItems().removeObservers(getViewLifecycleOwner());

        // Switch to observing sorted items
        viewModel.getSortedGalleryItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.updateGalleryItems(items);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save sort state
        outState.putBoolean("isSortedAZ", isSortedAZ);
        outState.putBoolean("isSortedZA", isSortedZA);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}




