package BO;

import android.content.Context;

import java.util.ArrayList;

import BEAN.AnswerSheetBEAN;
import BEAN.QuestionAnswerBEAN;
import DAO.AnswerSheetDAO;
import type.StatusAddAnswerSheet;

public class AnswerSheetBO {
    private final Context context;
    private int NUMBER_QUESTION;

    public AnswerSheetBO(Context context) {
        this.context = context;
    }

    public ArrayList<AnswerSheetBEAN> getAllAnswerSheet() {
        AnswerSheetDAO answerSheetDAO = new AnswerSheetDAO(this.context);
        return answerSheetDAO.getAllAnswerSheet();
    }

    public StatusAddAnswerSheet addAnswerSheet(boolean[][] choose, String name, int NUMBER_QUESTION, boolean checkExistsName) {
        this.NUMBER_QUESTION = NUMBER_QUESTION;

        if (!checkSelectAll(choose)) return StatusAddAnswerSheet.NOT_CHOOSE_ALL;

        AnswerSheetDAO answerSheetDAO = new AnswerSheetDAO(context);
        if (checkExistsName && answerSheetDAO.checkNameExists(name)) return StatusAddAnswerSheet.SAME_NAME;

        AnswerSheetBEAN answerSheetBEAN = new AnswerSheetBEAN();
        answerSheetBEAN.setName(name);
        answerSheetBEAN.setListAnswer(getListQuestionAnswer(choose));

        long idAnswerSheet = answerSheetDAO.addAnswerSheet(answerSheetBEAN);

        return StatusAddAnswerSheet.ADDED;
    }

    public StatusAddAnswerSheet addAnswerSheet(boolean[][] choose, String name, int NUMBER_QUESTION) {
        return addAnswerSheet(choose, name, NUMBER_QUESTION, true);
    }

    private QuestionAnswerBEAN[] getListQuestionAnswer(boolean[][] choose) {
        QuestionAnswerBEAN[] questionAnswerBEANS = new QuestionAnswerBEAN[NUMBER_QUESTION];

        for (int i = 0; i < NUMBER_QUESTION; i++) {
            for (int j = 0; j < 4; j++) {
                if (choose[i][j]) {
                    questionAnswerBEANS[i] = new QuestionAnswerBEAN(i, j);
                    break;
                }
            }
        }

        return questionAnswerBEANS;
    }

    private boolean checkSelectAll(boolean[][] choose) {
        for (int i = 0; i < NUMBER_QUESTION; i++) {
            boolean check = false;
            for (int j = 0; j < 4; j++) {
                if (choose[i][j]) {
                    check = true;
                    break;
                }
            }
            if (!check) return false;
        }
        return true;
    }

    public boolean deleteAnswerSheet(long id) {
        AnswerSheetDAO answerSheetDAO = new AnswerSheetDAO(this.context);

        return  answerSheetDAO.deleteAnswerSheet(id) > 0;
    }
}
