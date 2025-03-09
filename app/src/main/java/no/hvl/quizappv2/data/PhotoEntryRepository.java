package no.hvl.quizappv2.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import no.hvl.quizappv2.entity.PhotoEntry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoEntryRepository {

    private PhotoEntryDao photoEntryDao;
    private ExecutorService executorService;

    public PhotoEntryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        photoEntryDao = db.photoEntryDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Returnerer alle oppføringer med standard sortering (A-Å)
    public LiveData<List<PhotoEntry>> getAllEntries() {
        return photoEntryDao.getAllEntriesSortedByNameAsc();
    }

    public LiveData<List<PhotoEntry>> getAllEntriesSortedByNameAsc() {
        return photoEntryDao.getAllEntriesSortedByNameAsc();
    }

    public LiveData<List<PhotoEntry>> getAllEntriesSortedByNameDesc() {
        return photoEntryDao.getAllEntriesSortedByNameDesc();
    }

    public void insert(PhotoEntry photoEntry) {
        executorService.execute(() -> photoEntryDao.insert(photoEntry));
    }

    public void deleteById(long id) {
        executorService.execute(() -> photoEntryDao.deleteById(id));
    }
}