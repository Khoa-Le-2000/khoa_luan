package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import BEAN.QuestionAnswerBEAN;

public class QuestionAnswerDAO {

    private final Context context;

    public QuestionAnswerDAO(Context context) {
        this.context = context;
    }

    public long addAnswerSheet(int question, int answer, long answerSheetId) {
        DatabaseHandler handler = new DatabaseHandler(this.context);
        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.QA_KEY_QUESTION, question);
        values.put(DatabaseHandler.QA_KEY_ANSWER, answer);
        values.put(DatabaseHandler.QA_KEY_ANSWER_SHEET_ID, answerSheetId);

        long res = db.insert(DatabaseHandler.TABLE_NAME_QUESTION_ANSWER, "", values);

        db.close();

        return res;
    }

    public QuestionAnswerBEAN[] getQuestionAnswer(long answerSheetId) {
        DatabaseHandler handler = new DatabaseHandler(this.context);
        SQLiteDatabase db = handler.getReadableDatabase();

        String sql = "SELECT * FROM QuestionAnswer WHERE answer_sheet_id = ? ORDER BY question ASC";

        Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(answerSheetId)});

        ArrayList<QuestionAnswerBEAN> questionAnswerBEANArrayList = new ArrayList<QuestionAnswerBEAN>();
        if (cursor.moveToFirst()) {
            int questionIndex = cursor.getColumnIndex("question");
            int answerIndex = cursor.getColumnIndex("answer");
            do {
                int question = cursor.getInt(questionIndex);
                int answer = cursor.getInt(answerIndex);
                questionAnswerBEANArrayList.add(new QuestionAnswerBEAN(question,answer));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        QuestionAnswerBEAN[] questionAnswerBEANS = new QuestionAnswerBEAN[questionAnswerBEANArrayList.size()];
        questionAnswerBEANS = questionAnswerBEANArrayList.toArray(questionAnswerBEANS);

        return  questionAnswerBEANS;
    }
}
