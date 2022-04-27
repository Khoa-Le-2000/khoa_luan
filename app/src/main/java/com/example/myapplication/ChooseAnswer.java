package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import BEAN.AnswerSheetBEAN;
import BEAN.QuestionAnswerBEAN;
import BO.AnswerSheetBO;
import BO.QuestionAnswerBO;
import global.Global;

public class ChooseAnswer extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    LinearLayout linearLayoutCreate, linearLayoutOld, linearLayoutOldAnswer;
    ScrollView scrollViewCreate, scrollViewOld;

    Button btnCreate;

    private final int NUMBER_QUESTION = 10;

    private int[][] saveState = new int[NUMBER_QUESTION][4];
    private int[] idQuestionNumber = new int[NUMBER_QUESTION];
    private boolean[][] saveChoose = new boolean[NUMBER_QUESTION][4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_answer);

        scrollViewCreate = findViewById(R.id.scrollViewCreate);
        scrollViewOld = findViewById(R.id.scrollViewOld);
        btnCreate = findViewById(R.id.btnCreate);

        linearLayoutCreate = findViewById(R.id.linearScrollViewCreate);
        linearLayoutOld = findViewById(R.id.linearScrollViewOld);
        linearLayoutOldAnswer = findViewById(R.id.linearScrollViewOldAnswer);

        setOnCreateScreen();
        viewScreenCreate();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.newAnswer:
                        setOnCreateScreen();
                        viewScreenCreate();
                        return true;

                    case R.id.oldAnswer:
                        setOffCreateScreen();
                        viewScreenOld();
                        return true;

                    default:
                        return false;
                }
            }
        });

        handleCreateAnswer();
    }

    private void viewScreenOld() {
        setViewListOld();
        linearLayoutOld.removeAllViews();

        AnswerSheetBO answerSheetBO = new AnswerSheetBO(ChooseAnswer.this);
        ArrayList<AnswerSheetBEAN> answerSheetBEANS = answerSheetBO.getAllAnswerSheet();

        HashMap<Integer, AnswerSheetBEAN> mapIdAnswerSheet = new HashMap<Integer, AnswerSheetBEAN>();

        for (AnswerSheetBEAN answerSheetBEAN :
                answerSheetBEANS) {

            int id = ViewCompat.generateViewId();
            mapIdAnswerSheet.put(id, answerSheetBEAN);

            View view = getLayoutInflater().inflate(R.layout.old_answer_sheet, linearLayoutOld, false);
            TextView textView = view.findViewById(R.id.txtName);
            textView.setText(answerSheetBEAN.getName());
            view.setId(id);

            linearLayoutOld.addView(view);
        }

        addClickScreenOld(mapIdAnswerSheet);
    }

    private void addClickScreenOld(HashMap<Integer, AnswerSheetBEAN> mapIdAnswerSheet) {
        QuestionAnswerBO questionAnswerBO = new QuestionAnswerBO(ChooseAnswer.this);
        for (Map.Entry<Integer, AnswerSheetBEAN> entry :
                mapIdAnswerSheet.entrySet()) {
            View view = findViewById(entry.getKey());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handlerViewAnswer(questionAnswerBO.getQuestionAnswerList(entry.getValue().getAnswerSheetId()),
                            entry.getValue().getAnswerSheetId());
                }
            });
        }
    }

    private void handlerViewAnswer(QuestionAnswerBEAN[] listAnswer, long answerSheetId) {
        setViewListOldAnswer();
        linearLayoutOldAnswer.removeAllViews();

//        Arrays.sort(listAnswer, (a, b) -> {
//            if (a.getQuestion() > b.getQuestion()) return 1;
//            else if (a.getQuestion() < b.getQuestion()) return -1;
//            return 0;
//        });

        for (int i = 0; i < listAnswer.length; i++) {
            View view = getLayoutInflater().inflate(R.layout.answer_question, linearLayoutOldAnswer, false);
            TextView textView = view.findViewById(R.id.txtQuestion);
            textView.setText("Question " + (i + 1) + ":");

            switch (listAnswer[i].getAnswer()) {
                case 0: {
                    MaterialButton button = view.findViewById(R.id.answerA);
                    button.setBackgroundColor(Color.BLACK);
                    button.setTextColor(Color.WHITE);
                    break;
                }

                case 1: {
                    MaterialButton button = view.findViewById(R.id.answerB);
                    button.setBackgroundColor(Color.BLACK);
                    button.setTextColor(Color.WHITE);
                    break;
                }

                case 2: {
                    MaterialButton button = view.findViewById(R.id.answerC);
                    button.setBackgroundColor(Color.BLACK);
                    button.setTextColor(Color.WHITE);
                    break;
                }

                case 3: {
                    MaterialButton button = view.findViewById(R.id.answerD);
                    button.setBackgroundColor(Color.BLACK);
                    button.setTextColor(Color.WHITE);
                    break;
                }
            }

            linearLayoutOldAnswer.addView(view);
        }

        View view = getLayoutInflater().inflate(R.layout.button_cancel_apply, linearLayoutOldAnswer, false);
        Button button = view.findViewById(R.id.btnCancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewListOld();
            }
        });

        button = view.findViewById(R.id.btnAlly);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] correctAnswer = new int[listAnswer.length];
                for (int i = 0; i < listAnswer.length; i++) {
                    correctAnswer[i] = listAnswer[i].getAnswer();
                }
                Global.setCorrectAnswer(correctAnswer);
                Toast.makeText(ChooseAnswer.this, R.string.applied, Toast.LENGTH_SHORT)
                        .show();
                startActivity(new Intent(ChooseAnswer.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        button = view.findViewById(R.id.btnDelete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnswerSheetBO answerSheetBO = new AnswerSheetBO(ChooseAnswer.this);
                if (answerSheetBO.deleteAnswerSheet(answerSheetId)) {
                    Toast.makeText(ChooseAnswer.this, R.string.applied, Toast.LENGTH_SHORT)
                            .show();
                    viewScreenOld();
                } else {
                    Toast.makeText(ChooseAnswer.this, R.string.some_thing_wrong, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        linearLayoutOldAnswer.addView(view);
    }

    private void setViewListOld() {
        linearLayoutOld.setVisibility(View.VISIBLE);
        linearLayoutOldAnswer.setVisibility(View.GONE);
    }

    private void setViewListOldAnswer() {
        linearLayoutOld.setVisibility(View.GONE);
        linearLayoutOldAnswer.setVisibility(View.VISIBLE);
    }

    private void setOnCreateScreen() {
        scrollViewCreate.setVisibility(View.VISIBLE);
        scrollViewOld.setVisibility(View.INVISIBLE);
    }

    private void setOffCreateScreen() {
        scrollViewCreate.setVisibility(View.INVISIBLE);
        scrollViewOld.setVisibility(View.VISIBLE);
    }

    private void viewScreenCreate() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(
                dpTpPx(10, ChooseAnswer.this),
                dpTpPx(10, ChooseAnswer.this),
                0,
                0
        );

        LinearLayout.LayoutParams paramsNoMargin = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams paramsMarginLeftRight = new LinearLayout.LayoutParams(
//                                dpTpPx(50, ChooseAnswer.this),
//                                dpTpPx(50, ChooseAnswer.this)
                130, 160
        );
        paramsMarginLeftRight.setMargins(
                0,
                0,
                dpTpPx(10, ChooseAnswer.this),
                0
        );

        linearLayoutCreate.removeAllViews();
        for (int i = 0; i < NUMBER_QUESTION; i++) {
            LinearLayout linearLayoutItem = new LinearLayout(ChooseAnswer.this);
            linearLayoutItem.setLayoutParams(params);
            linearLayoutItem.setOrientation(LinearLayout.HORIZONTAL);
            linearLayoutItem.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

            TextView textView = new TextView(ChooseAnswer.this);
            textView.setText("Question " + (i + 1) + ":");
            textView.setTextSize(20);
            textView.setTextColor(Color.BLACK);
            int idText = ViewCompat.generateViewId();
            textView.setId(idText);
            idQuestionNumber[i] = idText;

            linearLayoutItem.addView(textView);

            LinearLayout linearLayoutABCD = new LinearLayout(ChooseAnswer.this);
            linearLayoutABCD.setLayoutParams(paramsNoMargin);
            linearLayoutABCD.setOrientation(LinearLayout.HORIZONTAL);
            linearLayoutABCD.setGravity(Gravity.RIGHT);
            String[] arr = {"A", "B", "C", "D"};
            for (int j = 0; j < 4; j++) {
                MaterialButton button = new MaterialButton(ChooseAnswer.this);
                button.setLayoutParams(paramsMarginLeftRight);
//                                button.setBackground(ContextCompat.getDrawable(ChooseAnswer.this, R.drawable.button_states));
//                                button.setCornerRadius(dpTpPx(100,ChooseAnswer.this));
                button.setCornerRadius(150);
                button.setBackgroundColor(Color.WHITE);
                button.setTextColor(Color.BLACK);
                button.setText(arr[j]);
                int id = ViewCompat.generateViewId();
                button.setId(id);
                saveState[i][j] = id;
                saveChoose[i][j] = false;

//                                linearLayoutItem.addView(button);
                linearLayoutABCD.addView(button);
            }

            linearLayoutItem.addView(linearLayoutABCD);

            linearLayoutCreate.addView(linearLayoutItem);
        }
        addClickListener();
    }

    private void handleCreateAnswer() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AnswerSheetBEAN answerSheetBEAN = new AnswerSheetBEAN("test", new QuestionAnswerBEAN[]{});
                AnswerSheetBO answerSheetBO = new AnswerSheetBO(ChooseAnswer.this);

                EditText editText = findViewById(R.id.txtName);
                String name = editText.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(ChooseAnswer.this, "Please enter name answer sheet", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }


                switch (answerSheetBO.addAnswerSheet(saveChoose, name, NUMBER_QUESTION)) {
                    case ADDED:
                        Toast.makeText(ChooseAnswer.this, "Saved", Toast.LENGTH_SHORT)
                                .show();
                        return;

                    case NOT_CHOOSE_ALL:
                        Toast.makeText(ChooseAnswer.this, "Please choose all answer", Toast.LENGTH_SHORT)
                                .show();
                        markedAnswerNotChoose();
                        return;

                    case SAME_NAME:
                        AlertDialog alertDialog = new AlertDialog.Builder(ChooseAnswer.this).create();
                        alertDialog.setTitle("Warning");
                        alertDialog.setTitle("The name has existed!\nDo you want to continue?");
                        alertDialog.setCancelable(true);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        answerSheetBO.addAnswerSheet(saveChoose, name, NUMBER_QUESTION, false);
                                        dialogInterface.dismiss();
                                        Toast.makeText(ChooseAnswer.this, "Saved", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialog.show();
                        return;
                }
//                long id = answerSheetBO.addAnswerSheet(saveChoose, NUMBER_QUESTION);
//                Log.i("OpenCVCamera", "Check DB: " + id);
            }
        });
    }

    private void markedAnswerNotChoose() {
        for (int i = 0; i < NUMBER_QUESTION; i++) {
            boolean check = false;
            for (int j = 0; j < 4; j++) {
                if (saveChoose[i][j]) {
                    check = true;
                    break;
                }
            }
            if (!check) {
                TextView textView = findViewById(idQuestionNumber[i]);
                textView.setTextColor(Color.RED);
            }
        }
    }

    private void addClickListener() {
        for (int question = 0; question < NUMBER_QUESTION; question++) {
            for (int answer = 0; answer < 4; answer++) {
                Button button = findViewById(saveState[question][answer]);
                ButtonOnClick buttonOnClick = new ButtonOnClick(question, answer, saveChoose,
                        saveState, idQuestionNumber, button, ChooseAnswer.this);
                button.setOnClickListener(buttonOnClick);
                Log.i("OpenCVCamera", "CHECK: " + question + " " + answer + " " + buttonOnClick == null ? "NULL" : "NOT NULL");
            }
        }
    }

    public int dpTpPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}