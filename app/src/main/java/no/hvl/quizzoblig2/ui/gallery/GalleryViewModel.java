package no.hvl.quizzoblig2.ui.gallery;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.hvl.quizzoblig2.data.GalleryRepository;
import no.hvl.quizzoblig2.data.db.GalleryItem;

public class GalleryViewModel extends AndroidViewModel {
    private final GalleryRepository repository;
    private final MutableLiveData<Boolean> sortAscending = new MutableLiveData<>(null);
    private final MediatorLiveData<List<GalleryItem>> currentGalleryItems = new MediatorLiveData<>();


    public GalleryViewModel(@NonNull Application application) {
        super(application);
        repository = new GalleryRepository(application);
        setupDataObservation();
    }


    private void setupDataObservation() {
        currentGalleryItems.addSource(repository.getAllGalleryItems(), items -> {
            applySorting(items, sortAscending.getValue());
        });

        currentGalleryItems.addSource(sortAscending, ascending -> {
            applySorting(repository.getAllGalleryItems().getValue(), ascending);
        });
    }


    private void applySorting(List<GalleryItem> items, Boolean ascending) {
        if (items == null) return;

        List<GalleryItem> result = new ArrayList<>(items);

        if (ascending != null) {
            if (ascending) {
                Collections.sort(result, (a, b) -> a.name.compareTo(b.name));
            } else {
                Collections.sort(result, (a, b) -> b.name.compareTo(a.name));
            }
        }


        currentGalleryItems.setValue(result);
    }


    public LiveData<List<GalleryItem>> getAllGalleryItems() {
        return repository.getAllGalleryItems();
    }


    public LiveData<List<GalleryItem>> getSortedGalleryItems() {
        return currentGalleryItems;
    }


    public void sortGalleryItems(boolean ascending) {
        sortAscending.setValue(ascending);
    }


    public void resetSorting() {
        sortAscending.setValue(null);
    }


    public void insert(GalleryItem item) {
        repository.insert(item);
    }


    public void delete(GalleryItem item) {
        repository.delete(item);
    }
}