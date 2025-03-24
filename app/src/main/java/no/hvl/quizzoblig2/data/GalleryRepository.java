package no.hvl.quizzoblig2.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import no.hvl.quizzoblig2.data.db.*;
import java.util.List;

public class GalleryRepository {
    private final GalleryDao dao;
    private final LiveData<List<GalleryItem>> galleryItems;

    public GalleryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.galleryDao();
        galleryItems = dao.getAllGalleryItems();
    }

    public void insert(GalleryItem item) {
        AppDatabase.getExecutor().execute(() -> dao.insert(item));
    }

    public void delete(GalleryItem item) {
        AppDatabase.getExecutor().execute(() -> dao.delete(item));
    }

    public LiveData<List<GalleryItem>> getAllGalleryItems() {
        return galleryItems;
    }
}
