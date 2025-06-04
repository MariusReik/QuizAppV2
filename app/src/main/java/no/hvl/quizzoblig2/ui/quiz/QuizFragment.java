package no.hvl.quizzoblig2.ui.quiz;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import no.hvl.quizzoblig2.R;

public class QuizFragment extends Fragment {
    private static final String TAG = "QuizFragment";

    private QuizViewModel viewModel;
    private ImageView imageViewQuestion;
    private TextView textViewQuestion;
    private Button buttonOption1, buttonOption2, buttonOption3;
    private TextView textViewScore;

    // Bygger fragment UI og setter opp alle komponenter
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quiz, container, false);

        initializeViews(root);
        setupViewModel();
        setupClickListeners();

        return root;
    }

    // Kobler alle UI-elementer til variabler og setter standard tekst
    private void initializeViews(View root) {
        imageViewQuestion = root.findViewById(R.id.imageViewQuestion);
        textViewQuestion = root.findViewById(R.id.textViewQuestion);
        buttonOption1 = root.findViewById(R.id.buttonOption1);
        buttonOption2 = root.findViewById(R.id.buttonOption2);
        buttonOption3 = root.findViewById(R.id.buttonOption3);
        textViewScore = root.findViewById(R.id.textViewScore);

        textViewQuestion.setText("What animal is this?");
        imageViewQuestion.setImageResource(android.R.drawable.ic_menu_gallery);
    }

    // Setter opp ViewModel og observerer alle data-endringer
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        // Observerer hvilket bilde som skal vises
        viewModel.getCurrentImage().observe(getViewLifecycleOwner(), this::loadImage);

        // Observerer svaralternativer og oppdaterer knapper
        viewModel.getAnswerOptions().observe(getViewLifecycleOwner(), this::updateOptions);

        // Observerer score og oppdaterer visning
        viewModel.getScore().observe(getViewLifecycleOwner(), score ->
                textViewScore.setText("Score: " + score));

        // Observerer når quiz er ferdig og viser dialog
        viewModel.isQuizFinished().observe(getViewLifecycleOwner(), this::handleQuizFinished);

        // Starter quizen
        viewModel.startQuiz();
    }

    // Setter opp click listeners for alle svarknapper
    private void setupClickListeners() {
        buttonOption1.setOnClickListener(v -> answerQuestion(buttonOption1.getText().toString()));
        buttonOption2.setOnClickListener(v -> answerQuestion(buttonOption2.getText().toString()));
        buttonOption3.setOnClickListener(v -> answerQuestion(buttonOption3.getText().toString()));
    }

    // Laster inn bilde fra URI og håndterer feil
    private void loadImage(String imageUri) {
        if (imageUri == null || imageUri.isEmpty()) {
            Log.w(TAG, "Tom bilde URI mottatt");
            imageViewQuestion.setImageResource(android.R.drawable.ic_dialog_alert);
            return;
        }

        Log.d(TAG, "Laster bilde: " + imageUri);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert);

        Glide.with(this)
                .load(Uri.parse(imageUri))
                .apply(options)
                .into(imageViewQuestion);
    }

    // Oppdaterer tekst på svarknappene og aktiverer dem
    private void updateOptions(java.util.List<String> options) {
        if (options == null || options.size() != 3) {
            Log.w(TAG, "Ugyldige svaralternativer mottatt");
            return;
        }

        buttonOption1.setText(options.get(0));
        buttonOption2.setText(options.get(1));
        buttonOption3.setText(options.get(2));

        setButtonsEnabled(true);
    }

    // Sender brukerens svar til ViewModel for behandling
    private void answerQuestion(String selectedAnswer) {
        viewModel.answerQuestion(selectedAnswer);
    }

    // Håndterer når quizen er ferdig og viser resultat-dialog
    private void handleQuizFinished(Boolean isFinished) {
        if (!isFinished) return;

        setButtonsEnabled(false);

        Integer currentScore = viewModel.getScore().getValue();
        String message = getQuizCompletionMessage(currentScore);

        new AlertDialog.Builder(getContext())
                .setTitle("Quiz ferdig!")
                .setMessage(message)
                .setPositiveButton("Tilbake til hovedmeny", (dialog, which) ->
                        requireActivity().getSupportFragmentManager().popBackStack())
                .setCancelable(false)
                .show();
    }

    // Lager passende melding basert på brukerens score
    private String getQuizCompletionMessage(Integer score) {
        if (score == null || score < 0) {
            return "Du trenger minst 3 bilder i galleriet for å spille quiz. " +
                    "Legg til flere bilder først.";
        }
        return "Din endelige score: " + score;
    }

    // Aktiverer eller deaktiverer alle svarknapper
    private void setButtonsEnabled(boolean enabled) {
        buttonOption1.setEnabled(enabled);
        buttonOption2.setEnabled(enabled);
        buttonOption3.setEnabled(enabled);
    }

    // Starter quiz på nytt når brukeren kommer tilbake fra andre skjermer
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.restartQuizIfNeeded();
        }
    }
}