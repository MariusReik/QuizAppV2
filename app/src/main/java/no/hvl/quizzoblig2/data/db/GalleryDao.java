package no.hvl.quizzoblig2.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface GalleryDao {
    @Insert
    void insert(GalleryItem item);

    @Delete
    void delete(GalleryItem item);

    @Query("SELECT * FROM gallery_items")
    LiveData<List<GalleryItem>> getAllGalleryItems();

}
