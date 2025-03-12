package no.hvl.quizzoblig2.ui.gallery;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import no.hvl.quizzoblig2.data.GalleryRepository;
import no.hvl.quizzoblig2.data.db.GalleryItem;

public class GalleryViewModel extends AndroidViewModel {
    private final GalleryRepository repository;
    private final MutableLiveData<List<GalleryItem>> sortedGalleryItems = new MutableLiveData<>();

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        repository = new GalleryRepository(application);
        loadGalleryItems();
    }

    public void insert(GalleryItem item) {
        repository.insert(item);
        loadGalleryItems();
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
            Collections.sort(items, (a, b) -> ascending ? a.name.compareTo(b.name) : b.name.compareTo(a.name));
            sortedGalleryItems.setValue(items);
        }
    }

    private void loadGalleryItems() {
        repository.getAllGalleryItems().observeForever(sortedGalleryItems::setValue);
    }
}


