package ir.proglovving.dilin.database_open_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ir.proglovving.dilin.FirstTimeManager;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.data_model.Notebook;

public class NotebookOpenHelper extends SQLiteOpenHelper {

    private static final String NOTEBOOK_TABLE_NAME = "notebooks";
    private static final int VERSION = 1;

    private static final String COL_ID = "_id";
    private static final String COL_NOTEBOOK_NAME = "notebook_name";

    private static final String COMMAND_CREATE_NOTEBOOK_TABLE =
            "CREATE TABLE " + NOTEBOOK_TABLE_NAME + "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_NOTEBOOK_NAME + " TEXT);";
    private static final String PREF_KEY_NOTEBOOK_DB_FIRST_TIME = "notebook_db_first_time";

    private Context context;

    public NotebookOpenHelper(Context context) {
        super(context, NOTEBOOK_TABLE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COMMAND_CREATE_NOTEBOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // adding a notebook named my words if it was first time
    private void firstTimeCheck() {
        if (FirstTimeManager.isFirstTime(context, PREF_KEY_NOTEBOOK_DB_FIRST_TIME) && getRawsCount() == 0) {
            Notebook defaultNotebook = new Notebook();
            defaultNotebook.setNoteBookName(context.getString(R.string.my_words));
            addNotebook(defaultNotebook);
            FirstTimeManager.registerAsFirstTime(context, PREF_KEY_NOTEBOOK_DB_FIRST_TIME);
        }
    }

    public void addNotebook(Notebook notebook) {
        SQLiteDatabase writableSqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTEBOOK_NAME, notebook.getNoteBookName());

        writableSqLiteDatabase.insert(NOTEBOOK_TABLE_NAME, null, cv);
        writableSqLiteDatabase.close();
    }

    public List<Notebook> getNotebookList() {
        firstTimeCheck();

        List<Notebook> notebooks = new ArrayList<>();

        SQLiteDatabase readableSqLiteDatabase = getReadableDatabase();
        Cursor cursor = readableSqLiteDatabase.rawQuery("SELECT * FROM " + NOTEBOOK_TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Notebook notebook = new Notebook();
                notebook.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
                notebook.setNoteBookName(cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME)));

                WordsOpenHelper wordsOpenHelper = new WordsOpenHelper(context, notebook.getId());

                notebook.setWordsCount(wordsOpenHelper.getRawsCount());

                notebooks.add(notebook);
            } while (cursor.moveToNext());
        }

        cursor.close();
        readableSqLiteDatabase.close();

        Collections.reverse(notebooks);
        return notebooks;
    }

    public void deleteNotebook(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(NOTEBOOK_TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});

        sqLiteDatabase.close();
    }

    public void deleteDatabase(String notebookName) {
        context.deleteDatabase(notebookName);
    }

    public void update(Notebook notebook) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTEBOOK_NAME, notebook.getNoteBookName());
        sqLiteDatabase.update(NOTEBOOK_TABLE_NAME, cv, COL_ID + " = ?", new String[]{String.valueOf(notebook.getId())});
        sqLiteDatabase.close();
    }

    public void returnNotebook(Notebook notebook) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COL_NOTEBOOK_NAME, notebook.getNoteBookName());

        sqLiteDatabase.insert(NOTEBOOK_TABLE_NAME, null, cv);

        cv.put(COL_ID, notebook.getId());
        sqLiteDatabase.update(NOTEBOOK_TABLE_NAME, cv, COL_ID + " = ?", new String[]{String.valueOf(getLastID())});
//        sqLiteDatabase.update(NOTEBOOK_TABLE_NAME, cv, COL_ID + " = ?", new String[]{String.valueOf(notebook.getId())});

        sqLiteDatabase.close();
    }

    public int getLastID() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + NOTEBOOK_TABLE_NAME, null);
        cursor.moveToLast();

        int result = cursor.getInt(cursor.getColumnIndex(COL_ID));
        cursor.close();
        return result;
    }

    public int getRawsCount() {
        SQLiteDatabase readableSqLiteDatabase = getReadableDatabase();
        Cursor cursor = readableSqLiteDatabase.rawQuery("SELECT * FROM " + NOTEBOOK_TABLE_NAME, null);
        int result = cursor.getCount();
        readableSqLiteDatabase.close();
        cursor.close();
        return result;
    }

    public boolean isThereNotebook(String name) {
        boolean result = false;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + NOTEBOOK_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME)).equals(name)) {
                    result = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }
}