package no.hvl.quizzoblig2.data.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gallery_items")
public class GalleryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String imageUri;

    public GalleryItem(String name, String imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUri() {
        return imageUri;
    }
}

