package ir.proglovving.dilin.database_open_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.data_model.Word;

public class ShowWordsWidgetOpenHelper extends SQLiteOpenHelper {

    private static final String SHOW_WORDS_TABLE_NAME = "show_words_table";
    private static final int VERSION = 1;

    private static final String COL_ID = "_id";
    private static final String COL_NOTEBOOK_NAME = "notebook_name";
    private static final String COL_CURRENT_WORD_ID = "current_word_id";
    private static final String COL_IS_CURRENT_NOTEBOOK = "is_current_notebook";

    private static final String COMMAND_CREATE_SHOW_WORDS_TABLE =
            "CREATE TABLE " + SHOW_WORDS_TABLE_NAME +
                    "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_NOTEBOOK_NAME + " TEXT," +
                    COL_CURRENT_WORD_ID + " INTEGER," +
                    COL_IS_CURRENT_NOTEBOOK + " INTEGER);";

    private Context context;

    public ShowWordsWidgetOpenHelper(Context context) {
        super(context, SHOW_WORDS_TABLE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COMMAND_CREATE_SHOW_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveNotebookInList(String notebookName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTEBOOK_NAME, notebookName);
        cv.put(COL_CURRENT_WORD_ID, 0);
        cv.put(COL_IS_CURRENT_NOTEBOOK, false);

        sqLiteDatabase.insert(SHOW_WORDS_TABLE_NAME, null, cv);

        sqLiteDatabase.close();
    }

    public Word getPlayWord() {
        Word word = new Word();

        if (getRowsCount() == 0) {
            word.setWord(context.getString(R.string.there_is_nothing_to_play));
            word.setMeaning(context.getString(R.string.play_list_is_empty));
            return word;
        }

        int vocId = getNextWordId();



        return
                new WordsOpenHelper(context, getCurrentNotebookId())
                        .getWord(vocId);
    }

    private String getCurrentNotebookName() {
        String result;

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SHOW_WORDS_TABLE_NAME + " WHERE " + COL_IS_CURRENT_NOTEBOOK + " =?",
                new String[]{String.valueOf(1)});
        if (!cursor.moveToFirst()) { // اگر هیچ دفتری به عنوان مپخوش انتخاب نشده بود اوّلی را به عنوان مپخوش انتخاب کن
            cursor = database.rawQuery("SELECT * FROM " + SHOW_WORDS_TABLE_NAME, null);
            cursor.moveToFirst();
            setNoteBookAsCurrent(getFirstId());
        }

        result = cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME));

        database.close();
        cursor.close();

        return result;
    }

    private int getCurrentNotebookId(){
        int result;

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+SHOW_WORDS_TABLE_NAME+" WHERE "+COL_IS_CURRENT_NOTEBOOK + " =?",
                new String[]{String.valueOf(1)});
        cursor.moveToFirst();

        result = cursor.getInt(cursor.getColumnIndex(COL_CURRENT_WORD_ID));
        return result;
    }


    private void setNoteBookAsCurrent(int id) {
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SHOW_WORDS_TABLE_NAME + " WHERE " + COL_ID + " =?",
                new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        ContentValues cv = new ContentValues();
//        putCursorOnContentValues(cursor, cv);
        cv.put(COL_NOTEBOOK_NAME, cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME)));
        cv.put(COL_CURRENT_WORD_ID, cursor.getString(cursor.getColumnIndex(COL_CURRENT_WORD_ID)));
        cv.put(COL_IS_CURRENT_NOTEBOOK, true);
        database.update(SHOW_WORDS_TABLE_NAME, cv, COL_ID + " =?", new String[]{String.valueOf(id)});

        cursor.close();
        database.close();
    }

//    private void putCursorOnContentValues(Cursor cursor, ContentValues cv) {
//        // TODO: 1/31/19 put cursor values in cv
//        cv.put(COL_NOTEBOOK_NAME, cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME)));
//        cv.put(COL_CURRENT_WORD_ID, cursor.getString(cursor.getColumnIndex(COL_CURRENT_WORD_ID)));
//        cv.put(COL_IS_CURRENT_NOTEBOOK, cursor.getString(cursor.getColumnIndex(COL_IS_CURRENT_NOTEBOOK)));
//    }

    private int getNextWordId() {
        int currentWordID = getCurrentWordId();
        int nextWordId = new WordsOpenHelper(context, getCurrentNotebookId()).getNextId(currentWordID);
        if (currentWordID == nextWordId) { // اگر این دوتا با هم برابر باشند یعنی پخش دفتر به پایان رسیده است و باید به دفتر بعدی رفت
            goToNextNotebookInList();
            return getNextWordId();
        } else {
//            setCurrentWordId(getCurrentNotebookId(),nextWordId);
            return nextWordId;
        }
    }

    private int getCurrentWordId() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery
                ("SELECT * FROM " + SHOW_WORDS_TABLE_NAME + " WHERE " + COL_IS_CURRENT_NOTEBOOK + " =?"
                        , new String[]{String.valueOf(1)});
        if (!cursor.moveToFirst()) { // اگر هیچ دفتری به عنوان مپخوش انتخاب نشده بود اوّلی را مپخوش کن
            setNoteBookAsCurrent(getFirstId());
            return getCurrentWordId();
        }
//        cursor.moveToFirst();
        int result = cursor.getInt(cursor.getColumnIndex(COL_CURRENT_WORD_ID));

        cursor.close();
        database.close();

        return result;
    }

    private int getFirstId() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SHOW_WORDS_TABLE_NAME, null);
        cursor.moveToFirst();

        int result = cursor.getInt(cursor.getColumnIndex(COL_ID));

        cursor.close();
        database.close();

        return result;
    }

    private void goToNextNotebookInList() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SHOW_WORDS_TABLE_NAME, null);

        cursor.moveToFirst();

        do {

            if (cursor.getInt(cursor.getColumnIndex(COL_IS_CURRENT_NOTEBOOK)) == 1) { // اگر دفتر مپخوش بود برو به دفتر بعدی

                // این قسمت از کد دفتر جاری را به عنوان غیرمپخوش انتخاب می کند
                ContentValues cv = new ContentValues();
                int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
//                putCursorOnContentValues(cursor,cv);
                cv.put(COL_NOTEBOOK_NAME, cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME)));
                cv.put(COL_CURRENT_WORD_ID, 0);
                cv.put(COL_IS_CURRENT_NOTEBOOK, false);
                database.update(SHOW_WORDS_TABLE_NAME, cv, COL_ID + " =?", new String[]{String.valueOf(id)});

                if(cursor.isLast()){// اگر دفتر آخر لیست بود اوّلین دفتر لیست را به عنوان مپخوش انتخاب کن
                    setNoteBookAsCurrent(getFirstId());
                    return;
                }
                cursor.moveToNext();

                id = cursor.getInt(cursor.getColumnIndex(COL_ID));
//                putCursorOnContentValues(cursor,cv);
                cv.put(COL_NOTEBOOK_NAME, cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME)));
                cv.put(COL_CURRENT_WORD_ID, cursor.getString(cursor.getColumnIndex(COL_CURRENT_WORD_ID)));
                cv.put(COL_IS_CURRENT_NOTEBOOK, true);
                database.update(SHOW_WORDS_TABLE_NAME, cv, COL_ID + " =?", new String[]{String.valueOf(id)});

                database.close();
                cursor.close();
                return;
            }else if(cursor.isLast()){ // اگر دفتر آخر لیست بود و مپخوش نبود اوّلین دفتر لیست را به عنوان مپخوش انتخاب کن.
                setNoteBookAsCurrent(getFirstId());
                return;
            }

        } while (cursor.moveToNext());

    }

    public int getRowsCount() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + SHOW_WORDS_TABLE_NAME, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    private void setCurrentWordId(int notebookId, int wordId){
        // TODO: 1/31/19 اگر به خاطر پر نکردن کامل کانتنت ولیو اروری رخ داد این قسمت اصلاح شود
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor cursor = database.rawQuery("SELECT * FROM "+SHOW_WORDS_TABLE_NAME+ " WHERE "+COL_ID+" =?",
                new String[]{String.valueOf(notebookId)});
        cursor.moveToFirst();
        cv.put(COL_NOTEBOOK_NAME,cursor.getString(cursor.getColumnIndex(COL_NOTEBOOK_NAME)));
        cv.put(COL_IS_CURRENT_NOTEBOOK,cursor.getString(cursor.getColumnIndex(COL_IS_CURRENT_NOTEBOOK)));
        cv.put(COL_CURRENT_WORD_ID,wordId);

        database.update(SHOW_WORDS_TABLE_NAME,cv,COL_ID + " =?",new String[]{String.valueOf(notebookId)});

        database.close();

    }
}
