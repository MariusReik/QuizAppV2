package no.hvl.quizappv2.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import no.hvl.quizappv2.data.PhotoEntryRepository;
import no.hvl.quizappv2.entity.PhotoEntry;

import java.util.List;

public class GalleryViewModel extends AndroidViewModel {

    private PhotoEntryRepository repository;
    private MutableLiveData<List<PhotoEntry>> allEntries;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        repository = new PhotoEntryRepository(application);
        allEntries = new MutableLiveData<>();
        loadEntries(); // Last inn oppføringer med standard sortering
    }

    public LiveData<List<PhotoEntry>> getAllEntries() {
        return allEntries;
    }

    private void loadEntries() {
        allEntries.setValue(repository.getAllEntries().getValue());
    }

    public void sortByNameAsc() {
        allEntries.setValue(repository.getAllEntriesSortedByNameAsc().getValue());
    }

    public void sortByNameDesc() {
        allEntries.setValue(repository.getAllEntriesSortedByNameDesc().getValue());
    }

    public void insert(PhotoEntry photoEntry) {
        repository.insert(photoEntry);
    }

    public void deleteById(long id) {
        repository.deleteById(id);
    }
}