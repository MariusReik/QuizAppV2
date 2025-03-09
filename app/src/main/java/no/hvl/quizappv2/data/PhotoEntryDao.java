package no.hvl.quizappv2.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import no.hvl.quizappv2.entity.PhotoEntry;

import java.util.List;

@Dao
public interface PhotoEntryDao {

    @Insert
    void insert(PhotoEntry photoEntry);

    @Query("SELECT * FROM photo_entries ORDER BY name ASC")
    LiveData<List<PhotoEntry>> getAllEntries();

    // Legg til denne metoden
    @Query("DELETE FROM photo_entries WHERE id = :id")
    void deleteById(long id);
}