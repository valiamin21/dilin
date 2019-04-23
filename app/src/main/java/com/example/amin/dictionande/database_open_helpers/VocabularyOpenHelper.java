package com.example.amin.dictionande.database_open_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.amin.dictionande.RandomPlayingVoc;
import com.example.amin.dictionande.data_model.Voc;

import java.util.ArrayList;
import java.util.List;

public class VocabularyOpenHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String COL_ID = "_id";
    private static final String COL_VOC = "vocabulary";
    private static final String COL_MEANING = "meaning";
    private static final String COL_PIC_DIRECTORY = "pic_directory";
    private static final String COL_AUDIO_DIRECTORY = "audio_directory";
    private static final String COL_BOOKMARKED = "bookmarked";

    private String vocs_table_name;


    public VocabularyOpenHelper(Context context, String vocs_table_name) {
        super(context, vocs_table_name.replace(" ", "_"), null, DB_VERSION);
        this.vocs_table_name = vocs_table_name.replace(" ", "_");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_vocs_table_command());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addVoc(Voc voc) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_VOC, voc.getVoc());
        cv.put(COL_MEANING, voc.getMeaning());
        cv.put(COL_PIC_DIRECTORY, voc.getPic_address());
        cv.put(COL_AUDIO_DIRECTORY, voc.getAudio_address());
        cv.put(COL_BOOKMARKED, voc.isBookmark());

        sqLiteDatabase.insert(vocs_table_name, null, cv);

        sqLiteDatabase.close();
    }

    public void addVocs(List<Voc> vocs){
        for (int i = 0; i < vocs.size(); i++) {
            addVoc(vocs.get(i));
        }
    }

    public void update(Voc voc, int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_VOC, voc.getVoc());
        cv.put(COL_MEANING, voc.getMeaning());
        cv.put(COL_PIC_DIRECTORY, voc.getPic_address());
        cv.put(COL_AUDIO_DIRECTORY, voc.getAudio_address());
        cv.put(COL_BOOKMARKED, (voc.isBookmark() ? 1 : 0));
        sqLiteDatabase.update(vocs_table_name, cv, COL_ID + " =?", new String[]{String.valueOf(id)});

        sqLiteDatabase.close();
    }

    public void deleteVoc(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(vocs_table_name, COL_ID + " = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
    }

    public Voc getVoc(int id) {
        Voc voc = new Voc();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + vocs_table_name + " WHERE " + COL_ID + "=?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        setValuesOfCursorInVoc(cursor, voc);

        cursor.close();
        sqLiteDatabase.close();

        return voc;
    }

    public List<Voc> getSearchedVocList(String searchText) {
        List<Voc> vocList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        //شروع جست و جو در لغات انگلیسی
        Cursor cursor =
                sqLiteDatabase.rawQuery("SELECT * FROM " + vocs_table_name + " WHERE " + COL_VOC + " LIKE '%" + searchText + "%'", null);

        if (cursor.moveToFirst()) {
            do {
                Voc voc = new Voc();
                setValuesOfCursorInVoc(cursor, voc);
                vocList.add(voc);
            } while (cursor.moveToNext());
        }
        //پایان جست و جو در لغات انگلیسی


        // شروع جست و جو در لغات فارسی
        cursor =
                sqLiteDatabase.rawQuery("SELECT * FROM " + vocs_table_name + " WHERE " + COL_MEANING + " LIKE '%" + searchText + "%'", null);
        if (cursor.moveToFirst()) {
            do {
                Voc voc = new Voc();
                setValuesOfCursorInVoc(cursor, voc);
                vocList.add(voc);
            } while (cursor.moveToNext());
        }
        // پایان جست و جو در لغات فارسی


        sqLiteDatabase.close();
        cursor.close();

        return vocList;
    }

    public List<Voc> getVocList() {
        List<Voc> vocList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        if (!sqLiteDatabase.isOpen()) {
            return vocList;
        }
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + vocs_table_name, null);
        if (cursor.moveToFirst()) {
            do {
                Voc voc = new Voc();
                setValuesOfCursorInVoc(cursor, voc);
                vocList.add(voc);
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return vocList;
    }

    public int getRawsCount() {
        SQLiteDatabase readableSqliSqLiteDatabase = getReadableDatabase();
        Cursor cursor = readableSqliSqLiteDatabase.rawQuery("SELECT * FROM " + vocs_table_name, null);
        int result = cursor.getCount();

        cursor.close();
        readableSqliSqLiteDatabase.close();

        return result;
    }

    public int getBookmarkRawsCount() {
        SQLiteDatabase readableSqliteDatabase = getReadableDatabase();
        Cursor cursor = readableSqliteDatabase.rawQuery("SELECT * FROM " + vocs_table_name + " WHERE " + COL_BOOKMARKED + " = 1", null);

        int result = cursor.getCount();

        cursor.close();
        readableSqliteDatabase.close();

        return result;
    }

    private void setValuesOfCursorInVoc(Cursor cursor, Voc voc) {
        voc.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
        voc.setVoc(cursor.getString(cursor.getColumnIndex(COL_VOC)));
        voc.setMeaning(cursor.getString(cursor.getColumnIndex(COL_MEANING)));
        voc.setPic_directory(cursor.getString(cursor.getColumnIndex(COL_PIC_DIRECTORY)));
        voc.setAudio_directory(cursor.getString(cursor.getColumnIndex(COL_AUDIO_DIRECTORY)));

        if (cursor.getInt(cursor.getColumnIndex(COL_BOOKMARKED)) == 0) {
            voc.setBookmark(false);
        } else {
            voc.setBookmark(true);
        }
    }

    private String create_vocs_table_command() {
        return
                "CREATE TABLE " + vocs_table_name +
                        "(" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COL_VOC + " TEXT," +
                        COL_MEANING + " TEXT," +
                        COL_PIC_DIRECTORY + " TEXT," +
                        COL_AUDIO_DIRECTORY + " TEXT," +
                        COL_BOOKMARKED + " INTEGER);";
    }

    public boolean isThereWord(String word) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + vocs_table_name, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(COL_VOC)).equals(word)) {

                    closeSqlAndCursor(sqLiteDatabase, cursor);

                    return true;
                }
            } while (cursor.moveToNext());
        }

        closeSqlAndCursor(sqLiteDatabase, cursor);
        return false;
    }

    public int getNextId(int id){
        int result = id;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+vocs_table_name,null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getInt(cursor.getColumnIndex(COL_ID)) == id){
                    if(cursor.moveToNext()){
                        result = cursor.getInt(cursor.getColumnIndex(COL_ID));
                    }
                    break;
                }
            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return result;
    }

    public List<Voc> getBookmarkedVocs(){
        List<Voc> result = new ArrayList<>();

        List<Voc> vocs = getVocList();
        for (int i = 0; i < vocs.size(); i++) {
            if(vocs.get(i).isBookmark()){
                result.add(vocs.get(i));
            }
        }
        return result;
    }

    public Voc getRandomVoc(){
        List<Voc> vocs = getVocList();
        return vocs.get(RandomPlayingVoc.randInt(0,vocs.size()-1));
    }
//    public int getNextId(int id) {
//        int result = id;
//
//        SQLiteDatabase database = getReadableDatabase();
//        Cursor cursor = database.
//                rawQuery("SELECT * FROM " + vocs_table_name + " WHERE " + COL_ID + " > ?"
//                        , new String[]{String.valueOf(id)});
//
//        if(cursor.moveToFirst()){
//            result = cursor.getInt(cursor.getColumnIndex(COL_ID));
//        }
//
//        cursor.close();
//        database.close();
//        return result;
//    }

    // وظیفه ی این تابع بستن دیتابیس(sqliteDatabase) و کورسور(cursor) بعد از خواندن اطّلاعات است(به دلیل استفاده ی زیاد این دو خط کد)
    private void closeSqlAndCursor(SQLiteDatabase sql, Cursor cursor) {
        sql.close();
        cursor.close();
    }

}
