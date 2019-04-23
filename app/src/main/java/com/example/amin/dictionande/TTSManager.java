package com.example.amin.dictionande;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTSManager {
    private static TextToSpeech textToSpeech;
    private static boolean initSuccess = false;

    public static void initilizeTTS(Context context, final Locale locale){
        if(textToSpeech == null){
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS){
                        initSuccess = true;

                        int ttsLang = textToSpeech.setLanguage(locale);
                        if(ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED){
                            initSuccess = false;
                        }
                    }else{
                        initSuccess = false;
                    }
                }
            });
//            textToSpeech.setSpeechRate(0.9f);
        }
    }

    public static void speak(String textForSpeech,Context context,Locale locale){
        if(textToSpeech == null){
            initilizeTTS(context,locale);
        }
        textToSpeech.stop();
        int speechStatus = textToSpeech.speak(textForSpeech,TextToSpeech.QUEUE_FLUSH,null);
    }

    public static void destroyTTS(){
        if(textToSpeech != null){
            textToSpeech.stop();
        }
    }
}
