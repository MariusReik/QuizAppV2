package no.hvl.quizappv2.viewmodel;

package com.example.quizapp.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.quizapp.data.PhotoEntryRepository;
import com.example.quizapp.entity.PhotoEntry;

import java.util.List;

public class GalleryViewModel extends AndroidViewModel {
    private PhotoEntryRepository repository;
    private LiveData<List<PhotoEntry>> allEntries;

    public GalleryViewModel(Application application) {
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
}