package ir.proglovving.dilin;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.data_model.NotebookWord;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

public class RandomPlayingWord {

    private static int randInt(int min, int max) { // خود مین و ماکس هم جزو خروجی باشه
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static NotebookWord getARandomWord(Context context) {
        List<NotebookWord> wordList = new ArrayList<>();

        List<Notebook> notebooks = new NotebookOpenHelper(context).getNotebookList();
        for (int i = 0; i < notebooks.size(); i++) {
            List<NotebookWord> subWordList = new WordsOpenHelper(context, notebooks.get(i).getId()).getWordList();
            wordList.addAll(subWordList);
        }

        if (wordList.size() == 0) { // اگر چیزی برای پخش موجود نبود
            NotebookWord word = new NotebookWord();
            word.setWord(context.getString(R.string.there_is_nothing_to_play));
            word.setMeaning(context.getString(R.string.play_list_is_empty));
            return word;
        } else {
            return wordList.get(randInt(0, wordList.size() - 1));
        }
    }
}
