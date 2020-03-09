package ir.proglovving.dilin.views.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ir.proglovving.dilin.ExportDatabaseCSVTask;
import ir.proglovving.dilin.R;
import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

public class DevTestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 2;
    private Button csvBackupButton, txtBackupButton, retrieveTxtButton, openMainPageButton;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_test);
        initViews();


        csvBackupButton.setOnClickListener(this);
        txtBackupButton.setOnClickListener(this);
        retrieveTxtButton.setOnClickListener(this);
        openMainPageButton.setOnClickListener(this);
    }

    private void initViews() {
        csvBackupButton = findViewById(R.id.btn_csv_backup);
        txtBackupButton = findViewById(R.id.btn_txt_backup);
        retrieveTxtButton = findViewById(R.id.btn_txt_retrieve);
        tv = findViewById(R.id.tv);
        openMainPageButton = findViewById(R.id.btn_open_main_page);
    }


    // TODO: 7/19/19 مطالعه درمورد این که تابع زیر چه کار می کند
    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_csv_backup:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
                    } else {
                        exportCSV();
                    }
                } else {
                    exportCSV();
                }
                break;
            case R.id.btn_txt_backup:
                backupDBsToTextFile();
                break;
            case R.id.btn_txt_retrieve:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
                    }
                }
                File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                retrieveTextFiles(root);

//
//
//                List<Word> vocList = getWordList(fileContent);
//
//                String tvText = "";
//                for (int i = 0; i < vocList.size(); i++) {
//                    Word voc = vocList.get(i);
//                    tvText = tvText + voc.getWord() + "     ::::::\n\n" + voc.getMeaning() + "\n\n\n\n***************\n\n\n\n";
////                    tvText = tvText + voc + "\n\n";
////                    tvText = tvText + voc + "\n\n\n\n%%%%%%%%%%%%%%%%%%%%\n\n\n\n";
//                }
//
////                String[] vocMeaningWords = getWordMeanings(fileContent);
////
////                String tvText = "";
////                for (String vocMeanWord : vocMeaningWords) {
////                    Word voc = getWordFromText(vocMeanWord);
////                    tvText = tvText + voc.getWord() + "     ::::::\n\n" + voc.getMeaning() + "\n\n\n\n***************\n\n\n\n";
//////                    tvText = tvText + voc + "\n\n";
//////                    tvText = tvText + voc + "\n\n\n\n%%%%%%%%%%%%%%%%%%%%\n\n\n\n";
////                }
//                tv.setText(tvText);
                break;
            case R.id.btn_open_main_page:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

//    private void retrieveTxtButtonClicked() {
//        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
////        File[] files = root.listFiles();
////        for (File f : files) {
////            Log.i(TAG, "btn retrieve clicked: " + f.getName());
////        }
//    }

//    private String readTxtFile(File root, String fileName) {
////        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
//        File file = new File(root, fileName);
//
//        StringBuilder text = new StringBuilder();
//
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String line;
//
//            while ((line = br.readLine()) != null) {
//                text.append(line);
//                text.append('\n');
//            }
//
//            br.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "f.u.b1", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "f.u.b2", Toast.LENGTH_SHORT).show();
//        }
//
//        return text.toString();
//    }

    private void retrieveTextFiles(File root) {
        Notebook notebook = new Notebook();

        File[] files = root.listFiles();
        for (File f : files) {
            notebook.setNoteBookName(f.getName().replace(".txt", ""));
            notebook.setFavorite(false);
            new NotebookOpenHelper(this).addNotebook(notebook);

            // TODO: 7/20/19 make a function WordsOpenHelper namely findNotebookIdByName
            new WordsOpenHelper(this, new NotebookOpenHelper(this).getLastID()).addWords(
                    getWordList(f)
            );
        }
    }

    private List<Word> getWordList(File file) {
        String fileContent = readTextFile(Uri.fromFile(file));
        fileContent = validateText(fileContent);

        List<Word> wordList = new ArrayList<>();
        Word word;

        String[] vocMeaningWords = getWordMeanings(fileContent);
        for (String vocMeanWord : vocMeaningWords) {
            word = getWordFromText(vocMeanWord);
            wordList.add(word);
        }

        return wordList;
    }

    private String validateText(String input) {
        input = input.replace("n:", "<n>");
        input = input.replace("n :", "<n>");

        input = input.replace("v:", "<v>");
        input = input.replace("v :", "<v>");

        input = input.replace("adj:", "<adj>");
        input = input.replace("adj :", "<adj>");

        input = input.replace("adv :", "<adv>");
        input = input.replace("adv :", "<adv>");

        return input;
    }

    private String reverseMeaningToOrigin(String meaning) {
        meaning = meaning.replace("<n>", "n :");
        meaning = meaning.replace("<v>", "v :");
        meaning = meaning.replace("<adj>", "adj :");
        meaning = meaning.replace("<adv>", "adv :");

        return meaning;
    }

    private String readTextFile(Uri uri) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    private String[] getWordMeanings(String input) {
        return input.split("\n\n\n--------------------\n\n\n");
    }

    private Word getWordFromText(String vocMeaningWord) {
        Word word = new Word();

        String[] ss = vocMeaningWord.split(":\n");
        for (int i = 0; i < ss.length; i++) {
            if (i == 0) {
                word.setWord(ss[i]);
            } else {
                word.setMeaning(reverseMeaningToOrigin(ss[i]));

            }

            if (word.getMeaning() == null) {
                word.setMeaning("");
            }
        }

        return word;
    }

    private void backupDBsToTextFile() {
        String notebookName;
        int notebookId;
        String txtContent = "";
        List<Notebook> notebookList = new NotebookOpenHelper(this).getNotebookList();
        for (int i = 0; i < notebookList.size(); i++) {
            txtContent = "";
            notebookName = notebookList.get(i).getNoteBookName();
            notebookId = notebookList.get(i).getId();

            List<Word> wordList = new WordsOpenHelper(this, notebookId).getWordList(false);
            for (int j = 0; j < wordList.size(); j++) {
                Word word = wordList.get(j);
                txtContent += word.getWord();
                txtContent += ":\n";
                txtContent += word.getMeaning();
                txtContent += "\n\n\n--------------------\n\n\n";
            }

            generateNoteOnSD(this, notebookName + ".txt", txtContent);
        }
    }

    private void exportCSV() {
        ExportDatabaseCSVTask csvTask;
        String notebookName;
        int notebookId;


        List<Notebook> notebooks = new NotebookOpenHelper(this).getNotebookList();
        for (int i = 0; i < notebooks.size(); i++) {
            notebookName = notebooks.get(i).getNoteBookName();
            notebookId = notebooks.get(i).getId();
            csvTask = new ExportDatabaseCSVTask(this, new WordsOpenHelper(this, notebookId).getWordList(false), notebookName);
            csvTask.execute();
        }

    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, DevTestActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportCSV();
                } else {
                    Toast.makeText(this, getString(R.string.you_have_to_permit_for_app_correctly_working), Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onClick(retrieveTxtButton);
                } else {
                    Toast.makeText(this, getString(R.string.you_have_to_permit_for_app_correctly_working), Toast.LENGTH_LONG).show();
                }
                break;
        }

    }
}
