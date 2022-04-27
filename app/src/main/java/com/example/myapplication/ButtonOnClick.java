package com.example.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ButtonOnClick implements View.OnClickListener {

    private int question, answer;
    private boolean[][] saveChoose;
    private int[][] saveState;
    private int[] idQuestion;
    private Button button;
    private Activity activity;

    public ButtonOnClick(int question, int answer, boolean[][] saveChoose, int[][] saveState, int[] idQuestion, Button button, Activity activity) {
        this.question = question;
        this.answer = answer;
        this.saveChoose = saveChoose;
        this.saveState = saveState;
        this.idQuestion = idQuestion;
        this.button = button;
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        saveChoose[question][answer] = !saveChoose[question][answer];
        if (saveChoose[question][answer]) {
            clearChoose();
            setChoose(button);
            TextView textView = activity.findViewById(idQuestion[question]);
            textView.setTextColor(Color.BLACK);
        } else {
            setNotChoose(button);
        }
    }

    private void clearChoose() {
        for (int i = 0; i < 4; i++) {
            Button button = activity.findViewById(saveState[question][i]);
            setNotChoose(button);
        }
    }

    private void setChoose(Button button) {
        button.setBackgroundColor(Color.BLACK);
        button.setTextColor(Color.WHITE);
    }

    private void setNotChoose(Button button) {
        button.setBackgroundColor(Color.WHITE);
        button.setTextColor(Color.BLACK);
    }
}
