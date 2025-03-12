package no.hvl.quizzoblig2.ui.quiz;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public QuizViewModel(@NonNull Application application) {
        super(application);
        repository = new GalleryRepository(application);
    }

    public void startQuiz() {
        repository.getAllGalleryItems().observeForever(items -> {
            quizItems = new ArrayList<>(items);
            if (quizItems.size() < 3) {
                endQuiz();
            } else {
                score.setValue(0);
                generateNextQuestion();
            }
        });
    }

    private void generateNextQuestion() {
        if (quizItems.isEmpty()) {
            endQuiz();
            return;
        }

        Collections.shuffle(quizItems);
        correctAnswer = quizItems.remove(0);  // Fjern spilt element fra listen

        currentImage.setValue(correctAnswer.imageUri);

        List<String> options = new ArrayList<>();
        options.add(correctAnswer.name);

        // Hent tilfeldige alternativer
        List<GalleryItem> tempItems = new ArrayList<>(quizItems);
        Collections.shuffle(tempItems);
        for (int i = 0; i < 2 && i < tempItems.size(); i++) {
            options.add(tempItems.get(i).name);
        }

        Collections.shuffle(options);
        answerOptions.setValue(options);
    }

    public void answerQuestion(String selectedAnswer) {
        if (correctAnswer != null && correctAnswer.name.equals(selectedAnswer)) {
            score.setValue(score.getValue() + 1);
        }
        generateNextQuestion();
    }

    private void endQuiz() {
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
}




