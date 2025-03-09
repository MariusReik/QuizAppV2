package no.hvl.quizappv2.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import no.hvl.quizappv2.entity.PhotoEntry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoEntryRepository {

    private PhotoEntryDao photoEntryDao;
    private LiveData<List<PhotoEntry>> allEntries;
    private ExecutorService executorService;

    public PhotoEntryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        photoEntryDao = db.photoEntryDao();
        allEntries = photoEntryDao.getAllEntries();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<PhotoEntry>> getAllEntries() {
        return allEntries;
    }

    public void insert(PhotoEntry photoEntry) {
        executorService.execute(() -> photoEntryDao.insert(photoEntry));
    }

    // Legg til denne metoden
    public void deleteById(long id) {
        executorService.execute(() -> photoEntryDao.deleteById(id));
    }
}