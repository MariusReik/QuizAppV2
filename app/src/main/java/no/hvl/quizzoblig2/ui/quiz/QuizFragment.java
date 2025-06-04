package no.hvl.quizzoblig2.ui.quiz;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
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

import no.hvl.quizzoblig2.R;

public class QuizFragment extends Fragment {
    private QuizViewModel viewModel;
    private ImageView imageViewQuestion;
    private TextView textViewQuestion;
    private Button buttonOption1, buttonOption2, buttonOption3;
    private TextView textViewScore;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quiz, container, false);

        initializeViews(root);
        setupViewModel();
        setupClickListeners();

        return root;
    }

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

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        viewModel.getCurrentImage().observe(getViewLifecycleOwner(), this::loadImage);
        viewModel.getAnswerOptions().observe(getViewLifecycleOwner(), this::updateOptions);
        viewModel.getScore().observe(getViewLifecycleOwner(), score ->
                textViewScore.setText("Score: " + score));
        viewModel.isQuizFinished().observe(getViewLifecycleOwner(), this::handleQuizFinished);

        viewModel.startQuiz();
    }

    private void setupClickListeners() {
        buttonOption1.setOnClickListener(v -> answerQuestion(buttonOption1.getText().toString()));
        buttonOption2.setOnClickListener(v -> answerQuestion(buttonOption2.getText().toString()));
        buttonOption3.setOnClickListener(v -> answerQuestion(buttonOption3.getText().toString()));
    }

    private void loadImage(String imageUri) {
        if (imageUri == null || imageUri.isEmpty()) {
            imageViewQuestion.setImageResource(android.R.drawable.ic_dialog_alert);
            return;
        }

        Glide.with(this)
                .load(Uri.parse(imageUri))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert)
                .into(imageViewQuestion);
    }

    private void updateOptions(java.util.List<String> options) {
        if (options == null || options.size() != 3) return;

        buttonOption1.setText(options.get(0));
        buttonOption2.setText(options.get(1));
        buttonOption3.setText(options.get(2));

        setButtonsEnabled(true);
    }

    private void answerQuestion(String selectedAnswer) {
        viewModel.answerQuestion(selectedAnswer);
    }

    private void handleQuizFinished(Boolean isFinished) {
        if (!isFinished) return;

        setButtonsEnabled(false);

        Integer currentScore = viewModel.getScore().getValue();
        String message = currentScore != null && currentScore >= 0
                ? "Your final score: " + currentScore
                : "You need at least 3 images in the gallery to play quiz.";

        new AlertDialog.Builder(getContext())
                .setTitle("Quiz Complete!")
                .setMessage(message)
                .setPositiveButton("Back to main menu", (dialog, which) ->
                        requireActivity().getSupportFragmentManager().popBackStack())
                .setCancelable(false)
                .show();
    }

    private void setButtonsEnabled(boolean enabled) {
        buttonOption1.setEnabled(enabled);
        buttonOption2.setEnabled(enabled);
        buttonOption3.setEnabled(enabled);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.restartQuizIfNeeded();
        }
    }
}