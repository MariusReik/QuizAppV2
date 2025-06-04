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

import no.hvl.quizzoblig2.R;
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
    private boolean quizInitialized = false;

    // Initialiserer ViewModel med repository og logger start
    public QuizViewModel(@NonNull Application application) {
        super(application);
        repository = new GalleryRepository(application);
        Log.d(TAG, "QuizViewModel opprettet");
    }

    // Starter quizen eller fortsetter eksisterende quiz
    public void startQuiz() {
        if (quizInitialized) {
            Log.d(TAG, "Quiz allerede initialisert");
            return;
        }

        resetQuizState();
        loadGalleryItemsAndStartQuiz();
    }

    // Nullstiller alle quiz-verdier til startposisjon
    private void resetQuizState() {
        score.setValue(0);
        quizFinished.setValue(false);
        quizItems.clear();
        correctAnswer = null;
        Log.d(TAG, "Quiz-tilstand nullstilt");
    }

    // Laster gallery items og starter quiz med disse eller fallback bilder
    private void loadGalleryItemsAndStartQuiz() {
        if (galleryItemsObserver != null) {
            repository.getAllGalleryItems().removeObserver(galleryItemsObserver);
        }

        galleryItemsObserver = items -> {
            Log.d(TAG, "Gallery items lastet: " + (items != null ? items.size() : 0));

            if (items != null && items.size() >= 3) {
                // Bruk gallery items hvis det er nok
                quizItems = new ArrayList<>(items);
                Log.d(TAG, "Bruker gallery items for quiz");
            } else {
                // Bruk drawable fallback hvis ikke nok items
                quizItems = createFallbackItems();
                Log.d(TAG, "Bruker fallback items for quiz");
            }

            Collections.shuffle(quizItems);
            generateNextQuestion();
            quizInitialized = true;
        };

        repository.getAllGalleryItems().observeForever(galleryItemsObserver);
    }

    // Lager fallback items fra de 3 drawable ressursene
    private List<GalleryItem> createFallbackItems() {
        List<GalleryItem> fallbackItems = new ArrayList<>();

        String packageName = getApplication().getPackageName();

        // Opprett URI-er for de 3 drawable ressursene
        String dogUri = "android.resource://" + packageName + "/" + R.drawable.dog;
        String catUri = "android.resource://" + packageName + "/" + R.drawable.cat;
        String foxUri = "android.resource://" + packageName + "/" + R.drawable.fox;

        fallbackItems.add(new GalleryItem("Dog", dogUri));
        fallbackItems.add(new GalleryItem("Cat", catUri));
        fallbackItems.add(new GalleryItem("Fox", foxUri));

        Log.d(TAG, "Opprettet " + fallbackItems.size() + " fallback items");
        return fallbackItems;
    }

    // Genererer neste spørsmål med bilde og svaralternativer
    private void generateNextQuestion() {
        if (quizItems == null || quizItems.isEmpty()) {
            Log.d(TAG, "Ingen flere spørsmål, avslutter quiz");
            endQuiz();
            return;
        }

        // Velg tilfeldig item som riktig svar
        correctAnswer = quizItems.remove(0);
        currentImage.setValue(correctAnswer.imageUri);

        // Generer svaralternativer
        List<String> options = createAnswerOptions();
        answerOptions.setValue(options);

        Log.d(TAG, "Generert spørsmål for: " + correctAnswer.name);
    }

    // Lager 3 svaralternativer hvor ett er riktig
    private List<String> createAnswerOptions() {
        List<String> options = new ArrayList<>();
        options.add(correctAnswer.name);

        // Samle alternative svar fra gjenværende items
        List<String> alternatives = new ArrayList<>();
        for (GalleryItem item : quizItems) {
            if (!item.name.equals(correctAnswer.name)) {
                alternatives.add(item.name);
            }
        }

        // Legg til ekstra alternativer hvis ikke nok
        String[] extraOptions = {"Bear", "Wolf", "Tiger", "Lion", "Elephant", "Giraffe", "Zebra"};
        for (String extra : extraOptions) {
            if (!extra.equals(correctAnswer.name) && !alternatives.contains(extra)) {
                alternatives.add(extra);
            }
        }

        // Velg 2 tilfeldige alternativer
        Collections.shuffle(alternatives);
        int count = Math.min(2, alternatives.size());
        for (int i = 0; i < count; i++) {
            options.add(alternatives.get(i));
        }

        // Fyll opp til 3 hvis nødvendig
        while (options.size() < 3) {
            options.add("Animal " + options.size());
        }

        Collections.shuffle(options);
        return options;
    }

    // Behandler brukerens svar og går til neste spørsmål
    public void answerQuestion(String selectedAnswer) {
        if (correctAnswer == null) {
            Log.w(TAG, "Ingen aktivt spørsmål å svare på");
            return;
        }

        boolean isCorrect = correctAnswer.name.equals(selectedAnswer);

        if (isCorrect) {
            int currentScore = score.getValue() != null ? score.getValue() : 0;
            score.setValue(currentScore + 1);
            Log.d(TAG, "Riktig svar! Score: " + (currentScore + 1));
        } else {
            Log.d(TAG, "Feil svar. Riktig: " + correctAnswer.name + ", Valgt: " + selectedAnswer);
        }

        generateNextQuestion();
    }

    // Avslutter quizen og markerer den som ferdig
    private void endQuiz() {
        Log.d(TAG, "Quiz ferdig. Endelig score: " + score.getValue());
        quizFinished.setValue(true);
    }

    // Starter quiz på nytt hvis den er ferdig
    public void restartQuizIfNeeded() {
        if (Boolean.TRUE.equals(quizFinished.getValue())) {
            Log.d(TAG, "Starter quiz på nytt");
            quizInitialized = false;
            startQuiz();
        }
    }

    // Returnerer LiveData for gjeldende bilde
    public LiveData<String> getCurrentImage() {
        return currentImage;
    }

    // Returnerer LiveData for svaralternativer
    public LiveData<List<String>> getAnswerOptions() {
        return answerOptions;
    }

    // Returnerer LiveData for score
    public LiveData<Integer> getScore() {
        return score;
    }

    // Returnerer LiveData for quiz status
    public LiveData<Boolean> isQuizFinished() {
        return quizFinished;
    }

    // Returnerer riktig svar for gjeldende spørsmål (for testing)
    public String getCurrentCorrectAnswer() {
        return correctAnswer != null ? correctAnswer.name : null;
    }

    // Setter test-spørsmål for automatiserte tester
    public void setTestItem(String imageUri, String correctAnswerText) {
        correctAnswer = new GalleryItem(correctAnswerText, imageUri);
        currentImage.setValue(imageUri);

        List<String> options = new ArrayList<>();
        options.add(correctAnswerText);
        options.add("Wrong Answer 1");
        options.add("Wrong Answer 2");

        answerOptions.setValue(options);
        quizInitialized = true;
        Log.d(TAG, "Test-item satt: " + correctAnswerText);
    }

    // Rydder opp observers når ViewModel blir ødelagt
    @Override
    protected void onCleared() {
        super.onCleared();
        if (galleryItemsObserver != null) {
            repository.getAllGalleryItems().removeObserver(galleryItemsObserver);
        }
        Log.d(TAG, "QuizViewModel ryddet opp");
    }
}