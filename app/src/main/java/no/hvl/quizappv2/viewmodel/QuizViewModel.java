package no.hvl.quizappv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import no.hvl.quizappv2.data.PhotoEntryRepository;
import no.hvl.quizappv2.entity.PhotoEntry;

import java.util.Collections;
import java.util.List;

public class QuizViewModel extends AndroidViewModel {

    private PhotoEntryRepository repository;
    private MutableLiveData<QuizQuestion> currentQuestion = new MutableLiveData<>();
    private MutableLiveData<Integer> score = new MutableLiveData<>(0);
    private List<PhotoEntry> allEntries;
    private int correctAnswerIndex;

    public QuizViewModel(Application application) {
        super(application);
        repository = new PhotoEntryRepository(application);
        allEntries = repository.getAllEntries().getValue();
    }

    public LiveData<QuizQuestion> getCurrentQuestion() {
        return currentQuestion;
    }

    public LiveData<Integer> getScore() {
        return score;
    }

    public void loadNextQuestion() {
        if (allEntries != null && !allEntries.isEmpty()) {
            Collections.shuffle(allEntries);
            PhotoEntry correctEntry = allEntries.get(0);

            List<PhotoEntry> wrongEntries = allEntries.subList(1, Math.min(3, allEntries.size()));
            String[] answers = new String[3];
            answers[0] = correctEntry.getName();
            answers[1] = wrongEntries.get(0).getName();
            answers[2] = wrongEntries.get(1).getName();
            Collections.shuffle(List.of(answers));

            correctAnswerIndex = List.of(answers).indexOf(correctEntry.getName());

            currentQuestion.setValue(new QuizQuestion(
                    "Hvem er dette?",
                    correctEntry.getPhotoUri(),
                    answers
            ));
        }
    }

    public void submitAnswer(int selectedAnswerIndex) {
        if (selectedAnswerIndex == correctAnswerIndex) {
            score.setValue(score.getValue() + 1);
        }
        loadNextQuestion();
    }

    // Legg til denne metoden
    public void finishQuiz() {
        // Logikk for å avslutte quizen og vise resultatet
        score.setValue(0); // Nullstill score
        currentQuestion.setValue(null); // Nullstill spørsmålet
    }

    public static class QuizQuestion {
        private String questionText;
        private String photoUri;
        private String[] answers;

        public QuizQuestion(String questionText, String photoUri, String[] answers) {
            this.questionText = questionText;
            this.photoUri = photoUri;
            this.answers = answers;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String getPhotoUri() {
            return photoUri;
        }

        public String[] getAnswers() {
            return answers;
        }
    }
}