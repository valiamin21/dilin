package ir.proglovving.dilin.database_open_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ir.proglovving.dilin.RandomPlayingWord;
import ir.proglovving.dilin.data_model.Notebook;

public class NotebookOpenHelper extends SQLiteOpenHelper {

    // TODO: 7/19/19 write a method for validate names for words table

    private static final String NOTEBOOK_TABLE_NAME = "notebooks";
    private static final int VERSION = 1;

    private static final String COL_ID = "_id";
    private static final String COL_NOTEBOOK_NAME = "notebook_name";
    private static final String COL_FAVORITE = "favorite";
    private static final String COL_PLAYING = "playing";
    private static final String COL_CURRENT_PLAYING_ID = "current_playing_id";

    private static final String COMMAND_CREATE_NOTEBOOK_TABLE =
            "CREATE TABLE " + NOTEBOOK_TABLE_NAME + "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_NOTEBOOK_NAME + " TEXT," +
                    COL_FAVORITE + " INTEGER," +
                    COL_PLAYING + " INTEGER," +
                    COL_CURRENT_PLAYING_ID + " INTEGER);";
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
        cv.put(COL_PLAYING, notebook.isPlaying()); saveInIsPlayings(notebook);
        cv.put(COL_CURRENT_PLAYING_ID, 0);

        writableSqLiteDatabase.insert(NOTEBOOK_TABLE_NAME, null, cv);

        writableSqLiteDatabase.close();
    }

    private void saveInIsPlayings(Notebook notebook) {
        if(notebook.isPlaying()){
            new ShowWordsWidgetOpenHelper(context).saveNotebookInList(notebook.getNoteBookName());
        }
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

                WordsOpenHelper wordsOpenHelper = new WordsOpenHelper(context,notebook.getId());

                notebook.setWordsCount(wordsOpenHelper.getRawsCount());
                notebook.setBookmarkedCount(wordsOpenHelper.getBookmarkRawsCount());

                // TODO: 2/5/19 بعد یه فکری به حال خط زیر بکن. هیچ کاربردی نداره
                notebook.setCurrentNumber(cursor.getInt(cursor.getColumnIndex(COL_CURRENT_PLAYING_ID)));

                if (cursor.getInt(cursor.getColumnIndex(COL_FAVORITE)) == 0) {
                    notebook.setFavorite(false);
                } else {
                    notebook.setFavorite(true);
                }

                if (cursor.getInt(cursor.getColumnIndex(COL_PLAYING)) == 0) {
                    notebook.setPlaying(false);
                } else {
                    notebook.setPlaying(true);
                }

//                VocabularyOpenHelper wordsOpenHelper = new VocabularyOpenHelper(context,notebook.getNoteBookName());
//                notebook.setWordsCount(wordsOpenHelper.getRawsCount());
//                notebook.setBookmarkedCount(wordsOpenHelper.getBookmarkRawsCount());


                notebooks.add(notebook);
            } while (cursor.moveToNext());
        }

        cursor.close();
        readableSqLiteDatabase.close();

        return notebooks;
    }

    // TODO: 1/16/19 این تابع بعدا به صورت استاندارد پیاده سازی شود
    public List<Notebook> getFavoriteNotebookList() { // بعدا این تابع به روش استاندارد پیاده سازی شود.
        List<Notebook> resultNotebooks = new ArrayList<>();

        List<Notebook> notebooks = getNotebookList();
        for (int i = 0; i < notebooks.size(); i++) {
            if (notebooks.get(i).isFavorite()) {
                resultNotebooks.add(notebooks.get(i));
            }
        }

        return resultNotebooks;
    }

    // TODO: 1/16/19 این تابع بعدا به صورت استاندارد پیاده سازی شود
    public List<Notebook> getIsPlayingNotebooks() {
        List<Notebook> resultNotebooks = new ArrayList<>();

        List<Notebook> notebooks = getNotebookList();
        for (int i = 0; i < notebooks.size(); i++) {
            if (notebooks.get(i).isPlaying()) {
                resultNotebooks.add(notebooks.get(i));
            }
        }

        return resultNotebooks;
    }

    public void deleteNotebook(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();


//        if(cursor.moveToFirst()){ //  اگر دفتری با این آیدی موجود بود
//            // TODO: 2/4/19 خط زیر اصلاح شود زیرا یک بار هم در کلاس VocabularyOpenHelper نوشته شده است.
//            String notebookName = cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME)).replace(" ","_");
//
////            context.deleteDatabase(notebookName);
//            sqLiteDatabase.delete(NOTEBOOK_TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
//        }

        sqLiteDatabase.delete(NOTEBOOK_TABLE_NAME,COL_ID +  " = ?",new String[]{String.valueOf(id)});

        sqLiteDatabase.close();
    }

    public void deleteDatabase(String notebookName){
        context.deleteDatabase(notebookName);
    }

    public void update(Notebook notebook) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTEBOOK_NAME, notebook.getNoteBookName());
        cv.put(COL_FAVORITE, notebook.isFavorite());
        cv.put(COL_PLAYING, notebook.isPlaying()); saveInIsPlayings(notebook);
        cv.put(COL_CURRENT_PLAYING_ID,notebook.getCurrentPlayingNumber());
        sqLiteDatabase.update(NOTEBOOK_TABLE_NAME, cv, COL_ID + " = ?", new String[]{String.valueOf(notebook.getId())});
    }

    public void returnNotebook(Notebook notebook) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COL_NOTEBOOK_NAME, notebook.getNoteBookName());
        cv.put(COL_FAVORITE, notebook.isFavorite());
        cv.put(COL_PLAYING, notebook.isPlaying()); saveInIsPlayings(notebook);
        cv.put(COL_CURRENT_PLAYING_ID, notebook.getCurrentPlayingNumber());

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

        return cursor.getInt(cursor.getColumnIndex(COL_ID));
    }

    public int getRawsCount(){
        return getReadableDatabase().rawQuery("SELECT * FROM "+NOTEBOOK_TABLE_NAME,null).getCount();
    }


    public boolean isThereNotebook(String name){
        boolean result = false;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+NOTEBOOK_TABLE_NAME,null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME)).equals(name)){
                    result = true;
                    break;
                }
            }while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    public Notebook getRandomIsPlayingNotebook(){
        List<Notebook> notebooks = getIsPlayingNotebooks();

        List<Notebook> valuableNotebooks = new ArrayList<>();
        for (int i = 0; i < notebooks.size(); i++) {
            if(new WordsOpenHelper(context,notebooks.get(i).getId()).getRawsCount()>0){
                valuableNotebooks.add(notebooks.get(i));
            }
        }

        if(valuableNotebooks.size() == 0){
            return null;
        }

        return valuableNotebooks.get(RandomPlayingWord.randInt(0,valuableNotebooks.size()-1));
    }


    public List<String> getNotebooksNames(){
        List<String> notebooksNames = new ArrayList<>();

        List<Notebook> notebooks = getNotebookList();
        for (int i = 0; i < notebooks.size(); i++) {
            notebooksNames.add(notebooks.get(i).getNoteBookName());
        }

        return notebooksNames;
    }
}