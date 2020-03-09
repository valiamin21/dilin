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
import ir.proglovving.dilin.data_model.Word;

public class WordsOpenHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String COL_ID = "_id";
    private static final String COL_WORD = "vocabulary";
    private static final String COL_MEANING = "meaning";
    private static final String COL_BOOKMARKED = "bookmarked";

    private String words_table_name;
    private int notebookId;

    public static List<Word> getAllWords(Context context, boolean isBookmarked) {
        List<Word> detailedWords = new ArrayList<>();

        List<Notebook> notebookList = new NotebookOpenHelper(context).getNotebookList();
        for (Notebook notebook : notebookList) {
            detailedWords.addAll(
                    new WordsOpenHelper(context,notebook.getId()).getWordList(isBookmarked)
            );
        }

        return detailedWords;
    }


    public WordsOpenHelper(Context context, int notebookId) {
        super(context, "_" + notebookId, null, DB_VERSION);
        this.words_table_name = "_" + notebookId;
        this.notebookId = notebookId;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(creating_words_table_command());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addWord(Word word) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_WORD, word.getWord());
        cv.put(COL_MEANING, word.getMeaning());
        cv.put(COL_BOOKMARKED, word.isBookmark());

        sqLiteDatabase.insert(words_table_name, null, cv);

        sqLiteDatabase.close();
    }

    public void addWords(List<Word> words) {
        for (int i = 0; i < words.size(); i++) {
            addWord(words.get(i));
        }
    }

    public void update(Word word, int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_WORD, word.getWord());
        cv.put(COL_MEANING, word.getMeaning());
        cv.put(COL_BOOKMARKED, word.isBookmark());
        sqLiteDatabase.update(words_table_name, cv, COL_ID + " =?", new String[]{String.valueOf(id)});

        sqLiteDatabase.close();
    }

    public void deleteWord(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(words_table_name, COL_ID + " = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
    }

    public Word getWord(int id) {
        Word word = new Word();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + words_table_name + " WHERE " + COL_ID + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        setValuesOfCursorInWord(cursor, word);

        cursor.close();
        sqLiteDatabase.close();

        return word;
    }

    public List<Word> getSearchedWordList(String searchText) {
        List<Word> wordList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        //شروع جست و جو در لغات انگلیسی
        Cursor cursor =
                sqLiteDatabase.rawQuery("SELECT * FROM " + words_table_name + " WHERE " + COL_WORD + " LIKE '%" + searchText + "%'", null);

        if (cursor.moveToFirst()) {
            do {
                Word word = new Word();
                setValuesOfCursorInWord(cursor, word);
                wordList.add(word);
            } while (cursor.moveToNext());
        }
        //پایان جست و جو در لغات انگلیسی


        // شروع جست و جو در لغات فارسی
        cursor =
                sqLiteDatabase.rawQuery("SELECT * FROM " + words_table_name + " WHERE " + COL_MEANING + " LIKE '%" + searchText + "%'", null);
        if (cursor.moveToFirst()) {
            do {
                Word word = new Word();
                setValuesOfCursorInWord(cursor, word);
                wordList.add(word);
            } while (cursor.moveToNext());
        }
        // پایان جست و جو در لغات فارسی


        sqLiteDatabase.close();
        cursor.close();

        return wordList;
    }

    public List<Word> getWordList(boolean isBookmarked) {
        List<Word> wordList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        if (!sqLiteDatabase.isOpen()) {
            return wordList;
        }
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + words_table_name, null);
        if (cursor.moveToFirst()) {
            do {
                Word word = new Word();
                setValuesOfCursorInWord(cursor, word);

                if(isBookmarked){
                    if(word.isBookmark()){
                        wordList.add(word);
                    }
                }else{
                    wordList.add(word);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return wordList;
    }

    public int getRawsCount() {
        SQLiteDatabase readableSqliSqLiteDatabase = getReadableDatabase();
        Cursor cursor = readableSqliSqLiteDatabase.rawQuery("SELECT * FROM " + words_table_name, null);
        int result = cursor.getCount();

        cursor.close();
        readableSqliSqLiteDatabase.close();

        return result;
    }

    public int getBookmarkRawsCount() {
        SQLiteDatabase readableSqliteDatabase = getReadableDatabase();
        Cursor cursor = readableSqliteDatabase.rawQuery("SELECT * FROM " + words_table_name + " WHERE " + COL_BOOKMARKED + " = 1", null);

        int result = cursor.getCount();

        cursor.close();
        readableSqliteDatabase.close();

        return result;
    }

    private void setValuesOfCursorInWord(Cursor cursor, Word word) {
        word.setNotebookId(notebookId);
        word.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
        word.setWord(cursor.getString(cursor.getColumnIndex(COL_WORD)));
        word.setMeaning(cursor.getString(cursor.getColumnIndex(COL_MEANING)));

        if (cursor.getInt(cursor.getColumnIndex(COL_BOOKMARKED)) == 0) {
            word.setBookmark(false);
        } else {
            word.setBookmark(true);
        }
    }

    private String creating_words_table_command() {
        return
                "CREATE TABLE " + words_table_name +
                        "(" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COL_WORD + " TEXT," +
                        COL_MEANING + " TEXT," +
                        COL_BOOKMARKED + " INTEGER);";
    }

    public boolean isThereWord(String word) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + words_table_name, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COL_WORD)).equals(word)) {

                    closeSqlAndCursor(sqLiteDatabase, cursor);

                    return true;
                }
            } while (cursor.moveToNext());
        }

        closeSqlAndCursor(sqLiteDatabase, cursor);
        return false;
    }

    public int getNextId(int id) {
        int result = id;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + words_table_name, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex(COL_ID)) == id) {
                    if (cursor.moveToNext()) {
                        result = cursor.getInt(cursor.getColumnIndex(COL_ID));
                    }
                    break;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return result;
    }

    // وظیفه ی این تابع بستن دیتابیس(sqliteDatabase) و کورسور(cursor) بعد از خواندن اطّلاعات است(به دلیل استفاده ی زیاد این دو خط کد)
    private void closeSqlAndCursor(SQLiteDatabase sql, Cursor cursor) {
        sql.close();
        cursor.close();
    }

}
