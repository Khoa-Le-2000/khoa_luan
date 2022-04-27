package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import BEAN.AnswerSheetBEAN;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "detectAnswerSheet";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_QUESTION_ANSWER = "QuestionAnswer";
    public static final String TABLE_NAME_ANSWER_SHEET = "AnswerSheet";

    // TABLE QUESTION ANSWER
    public static final String QA_KEY_ID = "id";
    public static final String QA_KEY_QUESTION = "question";
    public static final String QA_KEY_ANSWER = "answer";
    public static final String QA_KEY_ANSWER_SHEET_ID = "answer_sheet_id";

    // TABLE ANSWER SHEET
    public static final String AS_KEY_ID = "id";
    public static final String AS_KEY_NAME = "name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_question_answer = String.format("CREATE TABLE %s " +
                        "( %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT NOT NULL )",
                TABLE_NAME_ANSWER_SHEET, AS_KEY_ID, AS_KEY_NAME);
        sqLiteDatabase.execSQL(create_question_answer);

        String create_answer_sheet = String.format("CREATE TABLE %s " +
                        "( %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL, " +
                        "%s INTEGER NOT NULL, " +
                        "%s INTEGER NOT NULL, " +
                        "UNIQUE(%s, %s), " +
                        "FOREIGN KEY (%s) REFERENCES %s(%s)" +
                        " ON DELETE CASCADE" +
                        " ON UPDATE NO ACTION )",
                TABLE_NAME_QUESTION_ANSWER,
                QA_KEY_ID, QA_KEY_QUESTION, QA_KEY_ANSWER, QA_KEY_ANSWER_SHEET_ID,
                QA_KEY_QUESTION, QA_KEY_ANSWER_SHEET_ID,
                QA_KEY_ANSWER_SHEET_ID, TABLE_NAME_ANSWER_SHEET, AS_KEY_ID
        );
        sqLiteDatabase.execSQL(create_answer_sheet);

        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String drop_table_question_answer = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME_QUESTION_ANSWER);
        sqLiteDatabase.execSQL(drop_table_question_answer);

        String drop_table_answer_sheet = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME_ANSWER_SHEET);
        sqLiteDatabase.execSQL(drop_table_answer_sheet);

        onCreate(sqLiteDatabase);
    }
}
