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

import no.hvl.quizzoblig2.R;
import no.hvl.quizzoblig2.data.db.GalleryItem;

public class GalleryFragment extends Fragment {
    private GalleryViewModel viewModel;
    private GalleryAdapter adapter;
    private boolean isSortedAZ = false;
    private boolean isSortedZA = false;

    // Håndterer resultat fra bilde-velger
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
        if (savedInstanceState != null) {
            isSortedAZ = savedInstanceState.getBoolean("isSortedAZ", false);
            isSortedZA = savedInstanceState.getBoolean("isSortedZA", false);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        setupRecyclerView(root);
        setupViewModel();
        setupButtons(root);

        return root;
    }


    private void setupRecyclerView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewGallery);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GalleryAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemLongClickListener(item -> {
            showDeleteDialog(item);
        });
    }


    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        // Gjenopprett sorteringstilstand hvis nødvendig
        if (isSortedAZ) {
            viewModel.sortGalleryItems(true);
            observeSortedItems();
        } else if (isSortedZA) {
            viewModel.sortGalleryItems(false);
            observeSortedItems();
        } else {
            observeUnsortedItems();
        }
    }


    private void observeUnsortedItems() {
        viewModel.getAllGalleryItems().observe(getViewLifecycleOwner(), galleryItems -> {
            if (galleryItems != null && !galleryItems.isEmpty()) {
                adapter.updateGalleryItems(galleryItems);
            } else {
                adapter.updateGalleryItems(new ArrayList<>());
                Toast.makeText(getContext(), "No images in gallery", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void observeSortedItems() {
        viewModel.getSortedGalleryItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.updateGalleryItems(items);
            }
        });
    }


    private void setupButtons(View root) {
        Button btnAdd = root.findViewById(R.id.btnAdd);
        Button btnSortAZ = root.findViewById(R.id.btnSortAZ);
        Button btnSortZA = root.findViewById(R.id.btnSortZA);

        btnAdd.setOnClickListener(v -> openImagePicker());

        btnSortAZ.setOnClickListener(v -> {
            isSortedAZ = true;
            isSortedZA = false;
            viewModel.sortGalleryItems(true);
            observeSortedItems();
        });

        btnSortZA.setOnClickListener(v -> {
            isSortedZA = true;
            isSortedAZ = false;
            viewModel.sortGalleryItems(false);
            observeSortedItems();
        });
    }

    private void showDeleteDialog(GalleryItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Yes", (dialog, which) -> viewModel.delete(item))
                .setNegativeButton("No", null)
                .show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSortedAZ", isSortedAZ);
        outState.putBoolean("isSortedZA", isSortedZA);
    }
}