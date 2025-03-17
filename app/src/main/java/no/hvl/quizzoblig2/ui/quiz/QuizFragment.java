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
import android.widget.Toast;

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

        // Set placeholder image
        imageViewQuestion.setImageResource(android.R.drawable.ic_menu_gallery);

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

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

        // Start the quiz
        viewModel.startQuiz();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restart quiz if returning to this fragment
        if (viewModel != null && viewModel.isQuizFinished().getValue() == Boolean.TRUE) {
            viewModel.startQuiz();
        }
    }
}