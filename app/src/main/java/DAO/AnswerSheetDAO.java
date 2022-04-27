package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import BEAN.AnswerSheetBEAN;
import BEAN.QuestionAnswerBEAN;

public class AnswerSheetDAO {

    private Context context;

    public AnswerSheetDAO(Context context) {
        this.context = context;
    }

    public long addAnswerSheet(AnswerSheetBEAN answerSheetBEAN) {
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(databaseHandler.AS_KEY_NAME, answerSheetBEAN.getName());

        long res = db.insert(databaseHandler.TABLE_NAME_ANSWER_SHEET, "", values);
        db.close();

        QuestionAnswerDAO questionAnswerDAO = new QuestionAnswerDAO(this.context);
        for (QuestionAnswerBEAN answerBEAN :
                answerSheetBEAN.getListAnswer()) {
            questionAnswerDAO.addAnswerSheet(answerBEAN.getQuestion(), answerBEAN.getAnswer(), res);
        }

        return res;
    }

    public boolean checkNameExists(String name) {
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        String sql = "SELECT * FROM AnswerSheet WHERE name LIKE ?";

        Cursor cursor = db.rawQuery(sql, new String[]{name});

        boolean res = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return res;
    }

    public ArrayList<AnswerSheetBEAN> getAllAnswerSheet() {
        DatabaseHandler handler = new DatabaseHandler(this.context);
        SQLiteDatabase db = handler.getReadableDatabase();

        String sql = "SELECT * FROM AnswerSheet ORDER BY id DESC";

        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<AnswerSheetBEAN> sheetBEANS = new ArrayList<AnswerSheetBEAN>();
        if (cursor.moveToFirst()) {
            int answerSheetIdIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            do {
                long answerSheetId = cursor.getLong(answerSheetIdIndex);
                String name = cursor.getString(nameIndex);
                sheetBEANS.add(new AnswerSheetBEAN(answerSheetId, name));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return sheetBEANS;
    }

    public int deleteAnswerSheet(long id) {
        DatabaseHandler handler = new DatabaseHandler(this.context);
        SQLiteDatabase db = handler.getWritableDatabase();

        return db.delete(DatabaseHandler.TABLE_NAME_ANSWER_SHEET,
                DatabaseHandler.AS_KEY_ID + "=?",
                new String[]{Long.toString(id)});
    }
}
