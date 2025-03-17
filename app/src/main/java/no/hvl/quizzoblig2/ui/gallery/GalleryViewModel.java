package no.hvl.quizzoblig2.ui.gallery;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.hvl.quizzoblig2.data.GalleryRepository;
import no.hvl.quizzoblig2.data.db.GalleryItem;

public class GalleryViewModel extends AndroidViewModel {
    private final GalleryRepository repository;
    private final MutableLiveData<List<GalleryItem>> sortedGalleryItems = new MutableLiveData<>();
    private LiveData<List<GalleryItem>> galleryItemsLiveData;
    private Observer<List<GalleryItem>> galleryItemsObserver;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        repository = new GalleryRepository(application);
        galleryItemsLiveData = repository.getAllGalleryItems();

        // Initialize the sorted items with the repository data
        galleryItemsObserver = items -> {
            if (items != null) {
                sortedGalleryItems.setValue(new ArrayList<>(items));
            }
        };
        galleryItemsLiveData.observeForever(galleryItemsObserver);
    }

    public void insert(GalleryItem item) {
        repository.insert(item);
    }

    public void delete(GalleryItem item) {
        repository.delete(item);
    }

    public LiveData<List<GalleryItem>> getAllGalleryItems() {
        return repository.getAllGalleryItems();
    }

    public LiveData<List<GalleryItem>> getSortedGalleryItems() {
        return sortedGalleryItems;
    }

    public void sortGalleryItems(boolean ascending) {
        List<GalleryItem> items = sortedGalleryItems.getValue();
        if (items != null) {
            List<GalleryItem> sortedList = new ArrayList<>(items);
            Collections.sort(sortedList, (a, b) ->
                    ascending ? a.name.compareTo(b.name) : b.name.compareTo(a.name));
            sortedGalleryItems.setValue(sortedList);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove the observer when ViewModel is cleared
        if (galleryItemsLiveData != null && galleryItemsObserver != null) {
            galleryItemsLiveData.removeObserver(galleryItemsObserver);
        }
    }
}