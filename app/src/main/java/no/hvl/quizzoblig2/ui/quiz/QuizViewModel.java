package no.hvl.quizzoblig2.ui.quiz;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.hvl.quizzoblig2.data.GalleryRepository;
import no.hvl.quizzoblig2.data.db.GalleryItem;

public class QuizViewModel extends AndroidViewModel {
    private static final String TAG = "QuizViewModel";

    private final GalleryRepository repository;
    private final MutableLiveData<String> currentImage = new MutableLiveData<>();
    private final MutableLiveData<List<String>> answerOptions = new MutableLiveData<>();
    private final MutableLiveData<Integer> score = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> quizFinished = new MutableLiveData<>(false);

    private List<GalleryItem> quizItems = new ArrayList<>();
    private GalleryItem correctAnswer;
    private Observer<List<GalleryItem>> galleryItemsObserver;

    public QuizViewModel(@NonNull Application application) {
        super(application);
        repository = new GalleryRepository(application);
    }

    public void startQuiz() {
        // Reset quiz state
        score.setValue(0);
        quizFinished.setValue(false);

        // Remove previous observer if exists
        if (galleryItemsObserver != null) {
            repository.getAllGalleryItems().removeObserver(galleryItemsObserver);
        }

        // Create new observer
        galleryItemsObserver = items -> {
            Log.d(TAG, "Loaded gallery items: " + (items != null ? items.size() : 0));

            if (items != null && !items.isEmpty()) {
                // Make a copy to avoid modification issues
                quizItems = new ArrayList<>(items);

                if (quizItems.size() < 3) {
                    Log.w(TAG, "Not enough images for quiz (minimum 3): " + quizItems.size());
                    // Not enough items, show message
                    quizFinished.setValue(true);
                    score.setValue(-1);
                } else {
                    Log.d(TAG, "Starting quiz with " + quizItems.size() + " items");
                    // Start the quiz
                    score.setValue(0);
                    generateNextQuestion();
                }
            } else {
                Log.w(TAG, "No images available for quiz");
                quizFinished.setValue(true);
                score.setValue(-1);
            }
        };

        // Start observing
        repository.getAllGalleryItems().observeForever(galleryItemsObserver);
    }

    private void generateNextQuestion() {
        if (quizItems == null || quizItems.isEmpty()) {
            Log.d(TAG, "No more quiz items, ending quiz");
            endQuiz();
            return;
        }

        // Shuffle the remaining items
        Collections.shuffle(quizItems);

        // Get one item as the correct answer
        correctAnswer = quizItems.remove(0);
        Log.d(TAG, "Selected question: " + correctAnswer.name + ", URI: " + correctAnswer.imageUri);

        // Update the image
        currentImage.setValue(correctAnswer.imageUri);

        // Prepare answer options
        List<String> options = new ArrayList<>();
        options.add(correctAnswer.name);

        // Get random alternatives from remaining items
        List<GalleryItem> tempItems = new ArrayList<>(quizItems);
        Collections.shuffle(tempItems);

        for (int i = 0; i < Math.min(2, tempItems.size()); i++) {
            options.add(tempItems.get(i).name);
        }

        // If we don't have enough alternatives, add dummy options
        while (options.size() < 3) {
            options.add("Option " + (options.size() + 1));
        }

        // Randomize option order
        Collections.shuffle(options);
        Log.d(TAG, "Answer options: " + options);

        // Update the UI
        answerOptions.setValue(options);
    }

    public void answerQuestion(String selectedAnswer) {
        if (correctAnswer != null && correctAnswer.name.equals(selectedAnswer)) {
            int currentScore = score.getValue() != null ? score.getValue() : 0;
            score.setValue(currentScore + 1);
            Log.d(TAG, "Correct answer! Score: " + (currentScore + 1));
        } else {
            Log.d(TAG, "Wrong answer. Selected: " + selectedAnswer + ", Correct: " +
                    (correctAnswer != null ? correctAnswer.name : "null"));
        }

        // Move to next question
        generateNextQuestion();
    }

    private void endQuiz() {
        Log.d(TAG, "Quiz finished. Final score: " + score.getValue());
        quizFinished.setValue(true);
    }

    public LiveData<String> getCurrentImage() {
        return currentImage;
    }

    public LiveData<List<String>> getAnswerOptions() {
        return answerOptions;
    }

    public LiveData<Integer> getScore() {
        return score;
    }

    public LiveData<Boolean> isQuizFinished() {
        return quizFinished;
    }

    // Add this method for testing
    public void setTestItem(String imageUri, String correctAnswerText) {
        // For testing only
        correctAnswer = new GalleryItem(correctAnswerText, imageUri);
        currentImage.setValue(imageUri);

        List<String> options = new ArrayList<>();
        options.add(correctAnswerText);
        options.add("Wrong Answer 1");
        options.add("Wrong Answer 2");

        answerOptions.setValue(options);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up observer when ViewModel is cleared
        if (galleryItemsObserver != null) {
            repository.getAllGalleryItems().removeObserver(galleryItemsObserver);
        }
    }
}




