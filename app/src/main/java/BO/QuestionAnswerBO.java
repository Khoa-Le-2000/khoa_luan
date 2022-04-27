package BO;

import android.content.Context;

import BEAN.QuestionAnswerBEAN;
import DAO.QuestionAnswerDAO;

public class QuestionAnswerBO {
    private final Context context;

    public QuestionAnswerBO(Context context) {
        this.context = context;
    }

    public QuestionAnswerBEAN[] getQuestionAnswerList(long answerSheetId) {
        QuestionAnswerDAO questionAnswerDAO = new QuestionAnswerDAO(this.context);
        return questionAnswerDAO.getQuestionAnswer(answerSheetId);
    }
}
