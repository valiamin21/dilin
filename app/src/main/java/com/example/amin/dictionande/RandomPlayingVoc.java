package com.example.amin.dictionande;

import android.content.Context;
import android.util.SparseArray;

import com.example.amin.dictionande.data_model.Notebook;
import com.example.amin.dictionande.data_model.Voc;
import com.example.amin.dictionande.database_open_helpers.NotebookOpenHelper;
import com.example.amin.dictionande.database_open_helpers.VocabularyOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomPlayingVoc {
//    private static int loopTimes = 0; // تعداد دفعات تکرار تابع getVoc در صورت خالی بودن دفتر یا کلمه

//    public static Voc getVoc(Context context){
//
//        Voc voc;
//
//        Notebook notebook = new NotebookOpenHelper(context).getRandomIsPlayingNotebook();
//        if(notebook == null){
//            voc = new Voc();
//            voc.setVoc(context.getString(R.string.there_is_nothing_to_play));
//            voc.setMeaning(context.getString(R.string.play_list_is_empty));
//            return voc;
//        }
//
//        voc = new VocabularyOpenHelper(context,notebook.getNoteBookName()).getRandomVoc();
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

//    public static Voc getVoc(Context context){
//        Voc voc;
//
//        Notebook notebook = new NotebookOpenHelper(context).getRandomIsPlayingNotebook();
//        if(notebook == null){
//            voc = new Voc();
//            voc.setVoc(context.getString(R.string.there_is_nothing_to_play));
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
//            int vocsCount = new VocabularyOpenHelper(context, notebooksNames.get(i)).getRawsCount();
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
//        voc = new VocabularyOpenHelper(context,selectedNotebookName).getRandomVoc();
//
//        return voc;
//    }

    public static Voc getVocUsingOtherMethod(Context context){
        List<Voc> vocList = new ArrayList<>();

        List<Notebook> notebooks = new NotebookOpenHelper(context).getIsPlayingNotebooks();
        for (int i = 0; i < notebooks.size(); i++) {
            List<Voc> subVocList = new VocabularyOpenHelper(context,notebooks.get(i).getNoteBookName()).getVocList();
            for (int j = 0; j < subVocList.size(); j++) {
                vocList.add(subVocList.get(j));
            }
        }

        if(vocList.size() == 0){ // اگر چیزی برای پخش موجود نبود
            Voc voc = new Voc();
            voc.setVoc(context.getString(R.string.there_is_nothing_to_play));
            voc.setMeaning(context.getString(R.string.play_list_is_empty));
            return voc;
        }else{
            return vocList.get(randInt(0,vocList.size()-1));
        }
    }
}
