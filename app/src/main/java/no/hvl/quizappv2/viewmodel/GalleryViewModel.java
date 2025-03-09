package no.hvl.quizappv2.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import no.hvl.quizappv2.data.PhotoEntryRepository;
import no.hvl.quizappv2.entity.PhotoEntry;

import java.util.List;

public class GalleryViewModel extends AndroidViewModel {

    private PhotoEntryRepository repository;
    private LiveData<List<PhotoEntry>> allEntries;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        repository = new PhotoEntryRepository(application);
        allEntries = repository.getAllEntries();
    }

    public LiveData<List<PhotoEntry>> getAllEntries() {
        return allEntries;
    }

    public void insert(PhotoEntry photoEntry) {
        repository.insert(photoEntry);
    }

    public void deleteById(long id) {
        repository.deleteById(id);
    }
}