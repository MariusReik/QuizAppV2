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

    // Initialiserer ViewModel med repository og setter opp data-observering
    public GalleryViewModel(@NonNull Application application) {
        super(application);
        repository = new GalleryRepository(application);
        setupDataObservation();
    }

    // Setter opp automatisk sortering når data eller sorteringstilstand endres
    private void setupDataObservation() {
        currentGalleryItems.addSource(repository.getAllGalleryItems(), items -> {
            applySorting(items, sortAscending.getValue());
        });

        currentGalleryItems.addSource(sortAscending, ascending -> {
            applySorting(repository.getAllGalleryItems().getValue(), ascending);
        });
    }

    // Sorterer data basert på sorteringstilstand
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
        // Hvis ascending er null, beholder vi original rekkefølge

        currentGalleryItems.setValue(result);
    }

    // Returnerer alle gallery items direkte fra repository (for bakoverkompatibilitet)
    public LiveData<List<GalleryItem>> getAllGalleryItems() {
        return repository.getAllGalleryItems();
    }

    // Returnerer sorterte gallery items
    public LiveData<List<GalleryItem>> getSortedGalleryItems() {
        return currentGalleryItems;
    }

    // Sorterer gallery items basert på ascending parameter
    public void sortGalleryItems(boolean ascending) {
        sortAscending.setValue(ascending);
    }

    // Tilbakestiller til usortert tilstand
    public void resetSorting() {
        sortAscending.setValue(null);
    }

    // Legger til nytt item i databasen
    public void insert(GalleryItem item) {
        repository.insert(item);
    }

    // Sletter item fra databasen
    public void delete(GalleryItem item) {
        repository.delete(item);
    }
}