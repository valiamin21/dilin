package ir.proglovving.dilin.database_open_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ir.proglovving.dilin.data_model.Notebook;

public class NotebookOpenHelper extends SQLiteOpenHelper {

    private static final String NOTEBOOK_TABLE_NAME = "notebooks";
    private static final int VERSION = 1;

    private static final String COL_ID = "_id";
    private static final String COL_NOTEBOOK_NAME = "notebook_name";
    private static final String COL_FAVORITE = "favorite";

    private static final String COMMAND_CREATE_NOTEBOOK_TABLE =
            "CREATE TABLE " + NOTEBOOK_TABLE_NAME + "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_NOTEBOOK_NAME + " TEXT," +
                    COL_FAVORITE + " INTEGER);";
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

    public void addNotebook(Notebook notebook) {
        SQLiteDatabase writableSqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTEBOOK_NAME, notebook.getNoteBookName());
        cv.put(COL_FAVORITE, notebook.isFavorite());

        writableSqLiteDatabase.insert(NOTEBOOK_TABLE_NAME, null, cv);
        writableSqLiteDatabase.close();
    }

    public List<Notebook> getNotebookList() {
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

                if (cursor.getInt(cursor.getColumnIndex(COL_FAVORITE)) == 0) {
                    notebook.setFavorite(false);
                } else {
                    notebook.setFavorite(true);
                }

                notebooks.add(notebook);
            } while (cursor.moveToNext());
        }

        cursor.close();
        readableSqLiteDatabase.close();

        return notebooks;
    }

    // TODO: 1/16/19 این تابع بعدا به صورت استاندارد پیاده سازی شود
    public List<Notebook> getFavoriteNotebookList() {
        List<Notebook> resultNotebooks = new ArrayList<>();

        List<Notebook> notebooks = getNotebookList();
        for (int i = 0; i < notebooks.size(); i++) {
            if (notebooks.get(i).isFavorite()) {
                resultNotebooks.add(notebooks.get(i));
            }
        }

        return resultNotebooks;
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
        cv.put(COL_FAVORITE, notebook.isFavorite());
        sqLiteDatabase.update(NOTEBOOK_TABLE_NAME, cv, COL_ID + " = ?", new String[]{String.valueOf(notebook.getId())});
        sqLiteDatabase.close();
    }

    public void returnNotebook(Notebook notebook) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COL_NOTEBOOK_NAME, notebook.getNoteBookName());
        cv.put(COL_FAVORITE, notebook.isFavorite());

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
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + NOTEBOOK_TABLE_NAME, null);
        int result = cursor.getCount();
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