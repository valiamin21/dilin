package ir.proglovving.dilin;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ir.proglovving.dilin.data_model.Notebook;
import ir.proglovving.dilin.data_model.Word;
import ir.proglovving.dilin.database_open_helpers.NotebookOpenHelper;
import ir.proglovving.dilin.database_open_helpers.WordsOpenHelper;

public class RandomPlayingWord {

    public static int randInt(int min, int max) { // خود مین و ماکس هم جزو خروجی باشه
        Random random = new Random();
        return random.nextInt(max-min+1) + min;
    }

    public static Word getRandomWordFromPlayingNotebook(Context context){
        List<Word> wordList = new ArrayList<>();

        List<Notebook> notebooks = new NotebookOpenHelper(context).getIsPlayingNotebooks();
        for (int i = 0; i < notebooks.size(); i++) {
            List<Word> subWordList = new WordsOpenHelper(context,notebooks.get(i).getId()).getWordList(false);
            for (int j = 0; j < subWordList.size(); j++) {
                wordList.add(subWordList.get(j));
            }
        }

        if(wordList.size() == 0){ // اگر چیزی برای پخش موجود نبود
            Word word = new Word();
            word.setWord(context.getString(R.string.there_is_nothing_to_play));
            word.setMeaning(context.getString(R.string.play_list_is_empty));
            return word;
        }else{
            return wordList.get(randInt(0, wordList.size()-1));
        }
    }

    public static Word getARandomWord(Context context){
        List<Word> wordList = new ArrayList<>();

        List<Notebook> notebooks = new NotebookOpenHelper(context).getNotebookList();
        for (int i = 0; i < notebooks.size(); i++) {
            List<Word> subWordList = new WordsOpenHelper(context,notebooks.get(i).getId()).getWordList(false);
            for (int j = 0; j < subWordList.size(); j++) {
                wordList.add(subWordList.get(j));
            }
        }

        if(wordList.size() == 0){ // اگر چیزی برای پخش موجود نبود
            Word word = new Word();
            word.setWord(context.getString(R.string.there_is_nothing_to_play));
            word.setMeaning(context.getString(R.string.play_list_is_empty));
            return word;
        }else{
            return wordList.get(randInt(0, wordList.size()-1));
        }
    }
}
