package no.hvl.quizappv2.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photo_entries")
public class PhotoEntry {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String photoUri;

    public PhotoEntry(String name, String photoUri) {
        this.name = name;
        this.photoUri = photoUri;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUri() {
        return photoUri;
    }
}