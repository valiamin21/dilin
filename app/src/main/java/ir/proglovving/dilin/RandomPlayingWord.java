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
//    private static int loopTimes = 0; // تعداد دفعات تکرار تابع getWord در صورت خالی بودن دفتر یا کلمه

//    public static Word getWord(Context context){
//
//        Word voc;
//
//        Notebook notebook = new NotebookOpenHelper(context).getRandomIsPlayingNotebook();
//        if(notebook == null){
//            voc = new Word();
//            voc.setWord(context.getString(R.string.there_is_nothing_to_play));
//            voc.setMeaning(context.getString(R.string.play_list_is_empty));
//            return voc;
//        }
//
//        voc = new WordsOpenHelper(context,notebook.getNoteBookName()).getRandomWord();
//
//        return voc;
//    }

//    public static int randInt(int min, int max) {
//        Random rand = new Random();
//        return rand.nextInt((max - min) + 1) + min;
//    }

    public static int randInt(int min, int max) { // خود مین و ماکس هم جزو خروجی باشه
        Random random = new Random();
        return random.nextInt(max-min+1) + min;
    }

//    private int randInt(int min, int max){
//        Random random = new Random();
//        return random.nextInt(max - min -1)+min +1;
//    }

//    private static int chancePointer;

//    public static Word getWord(Context context){
//        Word voc;
//
//        Notebook notebook = new NotebookOpenHelper(context).getRandomIsPlayingNotebook();
//        if(notebook == null){
//            voc = new Word();
//            voc.setWord(context.getString(R.string.there_is_nothing_to_play));
//            voc.setMeaning(context.getString(R.string.play_list_is_empty));
//            return voc;
//        }
//
//
//        chancePointer = 0;
//
//        //  در خط زیر اوّلی عدد ورودی و دوّمی نام دفتر است.
//        HashMap<Integer,String> hashMap = new HashMap<>();
//
//        List<String> notebooksNames = new NotebookOpenHelper(context).getNotebooksNames();
//        for (int i = 0; i < notebooksNames.size(); i++) {
//            int vocsCount = new WordsOpenHelper(context, notebooksNames.get(i)).getRawsCount();
//
//            for (int j = 0; j < vocsCount; j++) {
//                chancePointer++;
//                hashMap.put(chancePointer,notebooksNames.get(i));
//            }
//
//        }
//
//        String selectedNotebookName = hashMap.get(randInt(1,chancePointer));
//
//
//        voc = new WordsOpenHelper(context,selectedNotebookName).getRandomWord();
//
//        return voc;
//    }

    public static Word getWordUsingOtherMethod(Context context){
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
}
