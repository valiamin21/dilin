package ir.proglovving.dilin.database_open_helpers;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ir.proglovving.dilin.Utilities;
import ir.proglovving.dilin.data_model.DictionaryWord;

public class DictionaryOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dictionaryDB.db";

    private static final String COL_ID = "_id";
    private static final String COL_EN = "en";
    private static final String COL_FA = "fa";
    private static final String COL_LIKE = "like";
    private static final String COL_WICH = "wich";

    private static final int LIMIT_COUNT = 140;


    private static final String[] e1TableLetters = new String[]{"a", "b"};
    private static final String[] e2TableLetters = new String[]{"c", "d"};
    private static final String[] e3TableLetters = new String[]{"e", "f", "g"};
    private static final String[] e4TableLetters = new String[]{"h", "i", "j", "k", "l"};
    private static final String[] e5TableLetters = new String[]{"m", "n", "o", "p"};
    private static final String[] e6TableLetters = new String[]{"q", "r", "s"};
    private static final String[] e7TableLetters = new String[]{"t", "u", "v", "w", "x", "y", "z"};

    private static final String[] f1TableLetters = new String[]{"آ", "ا", "ب"};
    private static final String[] f2TableLetters = new String[]{"پ", "ت", "ث", "ج", "چ", "ح"};
    private static final String[] f3TableLetters = new String[]{"خ", "د", "ذ", "ر", "ز", "ژ"};
    private static final String[] f4TableLetters = new String[]{"س", "ش", "ص", "ض", "ط", "ظ", "ع", "غ", "ف", "ق"};
    private static final String[] f5TableLetters = new String[]{"ک", "گ", "ل", "م"};
    private static final String[] f6TableLetters = new String[]{"ن", "و", "ه", "ی"};

    private AssetManager assets;
    private String databaseDir;

    private Cursor cursor;
    private SQLiteDatabase sqLiteDatabase;
    private DictionaryWord dictionaryWord;

    public DictionaryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        assets = context.getAssets();
        databaseDir = context.getApplicationInfo().dataDir + "/databases/";

        File file = new File(databaseDir);
        if (!file.exists()) file.mkdir();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        copyDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (!isDatabaseExist())
            copyDatabase();
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (!isDatabaseExist())
            copyDatabase();
        return super.getReadableDatabase();
    }

    // this method initializes cursor and sqliteDatabase fields
    private void init(String tableName) {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            sqLiteDatabase = getReadableDatabase();
        }
        cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + tableName
//                        + " ORDER BY " + COL_EN
                , null);
    }

    public void clear() {
        cursor.close();
        sqLiteDatabase.close();
    }

    public List<DictionaryWord> getWordList(String searchText) {
        List<DictionaryWord> dictionaryWordList = new ArrayList<>();


        String tableName = getSuitableTableName(searchText.substring(0, 1));
        if (tableName == null) {
            return dictionaryWordList;
        }

        init(tableName);

        if (cursor != null && cursor.moveToFirst()) {
            int counter = 1;
            do {
                if (cursor.getString(cursor.getColumnIndex(COL_EN)).contains(searchText)) {
                    dictionaryWord = new DictionaryWord();
                    dictionaryWord.setWord(cursor.getString(cursor.getColumnIndex(COL_EN)));
                    dictionaryWord.setMeaning(cursor.getString(cursor.getColumnIndex(COL_FA))
                            .replace("<BR>", "\n").replace("~", " "));
                    dictionaryWordList.add(dictionaryWord);

                    if (counter > LIMIT_COUNT) break;
                    counter++;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        return dictionaryWordList;
    }

    public List<DictionaryWord> getDictionaryWordList(String searchText, OnIterationListener onIterationListener) {
        searchText = searchText.toLowerCase(); // for better searching regardless of uppercase and lowercase of words

        List<DictionaryWord> dictionaryWordList = new ArrayList<>();
        DictionaryWord dictionaryWord;

        if (searchText.length() == 0) return dictionaryWordList;
        String tableName = getSuitableTableName(searchText.substring(0, 1));
        if (tableName == null) return dictionaryWordList;


        SQLiteDatabase readableSqlite = getReadableDatabase();
        Cursor cursor = readableSqlite.rawQuery("SELECT * FROM " + tableName, null);
        if (cursor.moveToFirst()) {
            int counter = 0;
            do {
                if(onIterationListener.onIterated()){
                    return null;
                }
                if (cursor.getString(cursor.getColumnIndex(COL_EN)).toLowerCase().contains(searchText)) {
                    dictionaryWord = new DictionaryWord();
                    dictionaryWord.setWord(
                            cursor.getString(cursor.getColumnIndex(COL_EN))
                    );
                    dictionaryWord.setMeaning(
                            cursor.getString(cursor.getColumnIndex(COL_FA))
                                    .replace("<BR>", "\n").replace("~", " ")
                    );

                    dictionaryWordList.add(dictionaryWord);

                    if (counter > LIMIT_COUNT) break;
                    counter++;
                }
            } while (cursor.moveToNext());
        }

        return dictionaryWordList;
    }

    private String getSuitableTableName(String letter) {
        letter = letter.toLowerCase();
        if (Utilities.existsInArray(e1TableLetters, letter)) {
            return "e1";
        } else if (Utilities.existsInArray(e2TableLetters, letter)) {
            return "e2";
        } else if (Utilities.existsInArray(e3TableLetters, letter)) {
            return "e3";
        } else if (Utilities.existsInArray(e4TableLetters, letter)) {
            return "e4";
        } else if (Utilities.existsInArray(e5TableLetters, letter)) {
            return "e5";
        } else if (Utilities.existsInArray(e6TableLetters, letter)) {
            return "e6";
        } else if (Utilities.existsInArray(e7TableLetters, letter)) {
            return "e7";
        } else if (Utilities.existsInArray(f1TableLetters, letter)) {
            return "f1";
        } else if (Utilities.existsInArray(f2TableLetters, letter)) {
            return "f2";
        } else if (Utilities.existsInArray(f3TableLetters, letter)) {
            return "f3";
        } else if (Utilities.existsInArray(f4TableLetters, letter)) {
            return "f4";
        } else if (Utilities.existsInArray(f5TableLetters, letter)) {
            return "f5";
        } else if (Utilities.existsInArray(f6TableLetters, letter)) {
            return "f6";
        }

        return null;
    }

    private boolean isDatabaseExist() {
        return new File(databaseDir + DATABASE_NAME).exists();
    }

    private void copyDatabase() {
        try {
            InputStream inputStream = assets.open("databases/" + DATABASE_NAME);

            FileOutputStream outputStream = new FileOutputStream(databaseDir + DATABASE_NAME);

            byte[] buffer = new byte[8 * 1024];

            int readed;
            while ((readed = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readed);
            }

            outputStream.flush();

            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface OnIterationListener{
        boolean onIterated();
    }
}
