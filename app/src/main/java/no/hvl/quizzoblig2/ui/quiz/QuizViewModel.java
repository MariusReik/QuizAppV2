package no.hvl.quizzoblig2.ui.quiz;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.hvl.quizzoblig2.R;
import no.hvl.quizzoblig2.data.GalleryRepository;
import no.hvl.quizzoblig2.data.db.GalleryItem;

public class QuizViewModel extends AndroidViewModel {
    private final GalleryRepository repository;
    private final MutableLiveData<String> currentImage = new MutableLiveData<>();
    private final MutableLiveData<List<String>> answerOptions = new MutableLiveData<>();
    private final MutableLiveData<Integer> score = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> quizFinished = new MutableLiveData<>(false);

    private List<GalleryItem> quizItems = new ArrayList<>();
    private GalleryItem correctAnswer;
    private Observer<List<GalleryItem>> galleryItemsObserver;
    private boolean quizInitialized = false;

    public QuizViewModel(@NonNull Application application) {
        super(application);
        repository = new GalleryRepository(application);
    }

    public void startQuiz() {
        if (quizInitialized) return;

        resetQuizState();
        loadGalleryItemsAndStartQuiz();
    }

    private void resetQuizState() {
        score.setValue(0);
        quizFinished.setValue(false);
        quizItems.clear();
        correctAnswer = null;
    }

    private void loadGalleryItemsAndStartQuiz() {
        if (galleryItemsObserver != null) {
            repository.getAllGalleryItems().removeObserver(galleryItemsObserver);
        }

        galleryItemsObserver = items -> {
            if (items != null && items.size() >= 3) {
                quizItems = new ArrayList<>(items);
            } else {
                quizItems = createFallbackItems();
            }

            Collections.shuffle(quizItems);
            generateNextQuestion();
            quizInitialized = true;
        };

        repository.getAllGalleryItems().observeForever(galleryItemsObserver);
    }

    private List<GalleryItem> createFallbackItems() {
        List<GalleryItem> fallbackItems = new ArrayList<>();
        String packageName = getApplication().getPackageName();

        String dogUri = "android.resource://" + packageName + "/" + R.drawable.dog;
        String catUri = "android.resource://" + packageName + "/" + R.drawable.cat;
        String foxUri = "android.resource://" + packageName + "/" + R.drawable.fox;

        fallbackItems.add(new GalleryItem("Dog", dogUri));
        fallbackItems.add(new GalleryItem("Cat", catUri));
        fallbackItems.add(new GalleryItem("Fox", foxUri));

        return fallbackItems;
    }

    private void generateNextQuestion() {
        if (quizItems == null || quizItems.isEmpty()) {
            endQuiz();
            return;
        }

        correctAnswer = quizItems.remove(0);
        currentImage.setValue(correctAnswer.imageUri);
        answerOptions.setValue(createAnswerOptions());
    }

    private List<String> createAnswerOptions() {
        List<String> options = new ArrayList<>();
        options.add(correctAnswer.name);

        // Add alternatives from remaining items
        List<String> alternatives = new ArrayList<>();
        for (GalleryItem item : quizItems) {
            if (!item.name.equals(correctAnswer.name)) {
                alternatives.add(item.name);
            }
        }

        // Add extra options if needed
        String[] extraOptions = {"Bear", "Wolf", "Tiger", "Lion", "Elephant"};
        for (String extra : extraOptions) {
            if (!extra.equals(correctAnswer.name) && !alternatives.contains(extra)) {
                alternatives.add(extra);
            }
        }

        Collections.shuffle(alternatives);
        int count = Math.min(2, alternatives.size());
        for (int i = 0; i < count; i++) {
            options.add(alternatives.get(i));
        }

        while (options.size() < 3) {
            options.add("Animal " + options.size());
        }

        Collections.shuffle(options);
        return options;
    }

    public void answerQuestion(String selectedAnswer) {
        if (correctAnswer == null) return;

        boolean isCorrect = correctAnswer.name.equals(selectedAnswer);
        if (isCorrect) {
            int currentScore = score.getValue() != null ? score.getValue() : 0;
            score.setValue(currentScore + 1);
        }

        generateNextQuestion();
    }

    private void endQuiz() {
        quizFinished.setValue(true);
    }

    public void restartQuizIfNeeded() {
        if (Boolean.TRUE.equals(quizFinished.getValue())) {
            quizInitialized = false;
            startQuiz();
        }
    }

    // Getters
    public LiveData<String> getCurrentImage() { return currentImage; }
    public LiveData<List<String>> getAnswerOptions() { return answerOptions; }
    public LiveData<Integer> getScore() { return score; }
    public LiveData<Boolean> isQuizFinished() { return quizFinished; }

    // For testing
    public String getCurrentCorrectAnswer() {
        return correctAnswer != null ? correctAnswer.name : null;
    }

    public void setTestItem(String imageUri, String correctAnswerText) {
        correctAnswer = new GalleryItem(correctAnswerText, imageUri);
        currentImage.setValue(imageUri);

        List<String> options = new ArrayList<>();
        options.add(correctAnswerText);
        options.add("Wrong Answer 1");
        options.add("Wrong Answer 2");

        answerOptions.setValue(options);
        quizInitialized = true;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (galleryItemsObserver != null) {
            repository.getAllGalleryItems().removeObserver(galleryItemsObserver);
        }
    }
}