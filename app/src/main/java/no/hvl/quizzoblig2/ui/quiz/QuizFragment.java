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

import java.util.ArrayList;
import java.util.List;

import no.hvl.quizzoblig2.R;

public class QuizFragment extends Fragment {
    private static final String TAG = "QuizFragment";
    private static final String KEY_SCORE = "current_score";
    private static final String KEY_QUIZ_FINISHED = "quiz_finished";
    private static final String KEY_CURRENT_IMAGE = "current_image";
    private static final String KEY_CORRECT_ANSWER = "correct_answer";
    private static final String KEY_OPTION_1 = "option_1";
    private static final String KEY_OPTION_2 = "option_2";
    private static final String KEY_OPTION_3 = "option_3";

    private QuizViewModel viewModel;
    private ImageView imageViewQuestion;
    private TextView textViewQuestion;
    private Button buttonOption1, buttonOption2, buttonOption3;
    private TextView textViewScore;
    private int savedScore = 0;
    private boolean savedQuizFinished = false;
    private String savedCurrentImage = null;
    private String savedCorrectAnswer = null;
    private List<String> savedOptions = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            savedScore = savedInstanceState.getInt(KEY_SCORE, 0);
            savedQuizFinished = savedInstanceState.getBoolean(KEY_QUIZ_FINISHED, false);
            savedCurrentImage = savedInstanceState.getString(KEY_CURRENT_IMAGE);
            savedCorrectAnswer = savedInstanceState.getString(KEY_CORRECT_ANSWER);

            // Restore options if they exist
            if (savedInstanceState.containsKey(KEY_OPTION_1)) {
                savedOptions = new ArrayList<>();
                savedOptions.add(savedInstanceState.getString(KEY_OPTION_1));
                savedOptions.add(savedInstanceState.getString(KEY_OPTION_2));
                savedOptions.add(savedInstanceState.getString(KEY_OPTION_3));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Use the generic layout identifier
        View root = inflater.inflate(R.layout.fragment_quiz, container, false);

        imageViewQuestion = root.findViewById(R.id.imageViewQuestion);
        textViewQuestion = root.findViewById(R.id.textViewQuestion);
        buttonOption1 = root.findViewById(R.id.buttonOption1);
        buttonOption2 = root.findViewById(R.id.buttonOption2);
        buttonOption3 = root.findViewById(R.id.buttonOption3);
        textViewScore = root.findViewById(R.id.textViewScore);

        // Set the question text
        textViewQuestion.setText("What animal is this?");

        // Set placeholder image
        imageViewQuestion.setImageResource(android.R.drawable.ic_menu_gallery);

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        // Restore the saved state if we have one
        if (savedInstanceState != null) {
            viewModel.setScore(savedScore);

            // Restore current question if we have it
            if (savedCurrentImage != null && savedCorrectAnswer != null && !savedOptions.isEmpty()) {
                viewModel.restoreCurrentQuestion(savedCurrentImage, savedCorrectAnswer, savedOptions);
            }

            if (savedQuizFinished) {
                viewModel.setQuizFinished(true);
            }
        }

        viewModel.getCurrentImage().observe(getViewLifecycleOwner(), imageUri -> {
            if (imageUri != null && !imageUri.isEmpty()) {
                Log.d(TAG, "Loading question image: " + imageUri);
                try {
                    RequestOptions options = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_dialog_alert);

                    Glide.with(this)
                            .load(Uri.parse(imageUri))
                            .apply(options)
                            .into(imageViewQuestion);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading image: " + e.getMessage());
                    imageViewQuestion.setImageResource(android.R.drawable.ic_dialog_alert);
                }
            } else {
                Log.w(TAG, "Null or empty image URI");
                imageViewQuestion.setImageResource(android.R.drawable.ic_dialog_alert);
            }
        });

        viewModel.getAnswerOptions().observe(getViewLifecycleOwner(), options -> {
            if (options != null && options.size() == 3) {
                buttonOption1.setText(options.get(0));
                buttonOption2.setText(options.get(1));
                buttonOption3.setText(options.get(2));

                // Enable buttons
                buttonOption1.setEnabled(true);
                buttonOption2.setEnabled(true);
                buttonOption3.setEnabled(true);
            }
        });

        viewModel.getScore().observe(getViewLifecycleOwner(), score -> {
            textViewScore.setText("Score: " + score);
        });

        viewModel.isQuizFinished().observe(getViewLifecycleOwner(), isFinished -> {
            if (isFinished) {
                int currentScore = viewModel.getScore().getValue() != null ?
                        viewModel.getScore().getValue() : 0;
                String message;

                if (currentScore < 0) {
                    message = "You need at least 3 images in your gallery to play the quiz. " +
                            "Please add more images to your gallery first.";
                } else {
                    message = "Your final score: " + currentScore;
                }

                new AlertDialog.Builder(getContext())
                        .setTitle("Quiz Complete!")
                        .setMessage(message)
                        .setPositiveButton("Return to Main Menu", (dialog, which) -> {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        })
                        .setCancelable(false)
                        .show();

                // Disable answer buttons
                buttonOption1.setEnabled(false);
                buttonOption2.setEnabled(false);
                buttonOption3.setEnabled(false);
            }
        });

        buttonOption1.setOnClickListener(v -> viewModel.answerQuestion(buttonOption1.getText().toString()));
        buttonOption2.setOnClickListener(v -> viewModel.answerQuestion(buttonOption2.getText().toString()));
        buttonOption3.setOnClickListener(v -> viewModel.answerQuestion(buttonOption3.getText().toString()));

        // Start the quiz if we don't have saved state
        if (savedInstanceState == null) {
            viewModel.startQuiz();
        } else {
            // We have saved state, so just make sure the quiz is initialized
            viewModel.startQuiz(); // This won't restart if already initialized
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save current score and quiz state
        int currentScore = viewModel.getScore().getValue() != null ? viewModel.getScore().getValue() : 0;
        boolean isFinished = viewModel.isQuizFinished().getValue() != null && viewModel.isQuizFinished().getValue();

        outState.putInt(KEY_SCORE, currentScore);
        outState.putBoolean(KEY_QUIZ_FINISHED, isFinished);

        // Save current question state
        if (viewModel.getCurrentImage().getValue() != null) {
            outState.putString(KEY_CURRENT_IMAGE, viewModel.getCurrentImage().getValue());
        }

        outState.putString(KEY_CORRECT_ANSWER, viewModel.getCurrentCorrectAnswer());

        // Save options
        List<String> options = viewModel.getAnswerOptions().getValue();
        if (options != null && options.size() == 3) {
            outState.putString(KEY_OPTION_1, options.get(0));
            outState.putString(KEY_OPTION_2, options.get(1));
            outState.putString(KEY_OPTION_3, options.get(2));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Only restart quiz if returning to this fragment from elsewhere (not from rotation)
        // This would be triggered when returning from another fragment
        if (viewModel != null && viewModel.isQuizFinished().getValue() == Boolean.TRUE &&
                !savedQuizFinished) {
            viewModel.restartQuiz(); // Force a restart
        }
    }
}