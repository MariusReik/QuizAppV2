package no.hvl.quizzoblig2.ui.quiz;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import no.hvl.quizzoblig2.R;

public class QuizFragment extends Fragment {
    private QuizViewModel viewModel;
    private ImageView imageViewQuestion;
    private Button buttonOption1, buttonOption2, buttonOption3;
    private TextView textViewScore;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quiz, container, false);

        imageViewQuestion = root.findViewById(R.id.imageViewQuestion);
        buttonOption1 = root.findViewById(R.id.buttonOption1);
        buttonOption2 = root.findViewById(R.id.buttonOption2);
        buttonOption3 = root.findViewById(R.id.buttonOption3);
        textViewScore = root.findViewById(R.id.textViewScore);

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        viewModel.getCurrentImage().observe(getViewLifecycleOwner(), imageUri -> {
            if (imageUri != null) {
                Glide.with(this).load(imageUri).into(imageViewQuestion);
            }
        });

        viewModel.getAnswerOptions().observe(getViewLifecycleOwner(), options -> {
            if (options != null && options.size() == 3) {
                buttonOption1.setText(options.get(0));
                buttonOption2.setText(options.get(1));
                buttonOption3.setText(options.get(2));
            }
        });

        viewModel.getScore().observe(getViewLifecycleOwner(), score -> {
            textViewScore.setText("Score: " + score);
        });

        viewModel.isQuizFinished().observe(getViewLifecycleOwner(), isFinished -> {
            if (isFinished) {
                int currentScore = viewModel.getScore().getValue();
                String message;

                if (currentScore < 0) {
                    message = "You need at least 3 images in your gallery to play the quiz.";
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

        viewModel.startQuiz();

        return root;
    }
}

