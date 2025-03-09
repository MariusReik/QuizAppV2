package no.hvl.quizappv2.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import no.hvl.quizappv2.R;
import no.hvl.quizappv2.viewmodel.QuizViewModel;

public class QuizActivity extends AppCompatActivity {

    private QuizViewModel viewModel;
    private TextView questionTextView;
    private ImageView photoImageView;
    private Button[] answerButtons = new Button[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.questionTextView);
        photoImageView = findViewById(R.id.photoImageView);
        answerButtons[0] = findViewById(R.id.answerButton1);
        answerButtons[1] = findViewById(R.id.answerButton2);
        answerButtons[2] = findViewById(R.id.answerButton3);

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        viewModel.getCurrentQuestion().observe(this, question -> {
            if (question != null) {
                questionTextView.setText(question.getQuestionText());
                Glide.with(this).load(question.getPhotoUri()).into(photoImageView);

                for (int i = 0; i < answerButtons.length; i++) {
                    answerButtons[i].setText(question.getAnswers()[i]);
                    int finalI = i;
                    answerButtons[i].setOnClickListener(v -> checkAnswer(finalI));
                }
            }
        });

        viewModel.getScore().observe(this, score -> {
            Toast.makeText(this, "Score: " + score, Toast.LENGTH_SHORT).show();
        });

        viewModel.loadNextQuestion();
    }

    private void checkAnswer(int selectedAnswerIndex) {
        viewModel.submitAnswer(selectedAnswerIndex);
    }
}