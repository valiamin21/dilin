package ir.proglovving.dilin.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.RandomPlayingWord;
import ir.proglovving.dilin.data_model.NotebookWord;

public class WordsWidget extends AppWidgetProvider {


    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, NotebookWord word) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.show_words_widget);
        views.setTextViewText(R.id.appwidget_word, word.getWord() + " :");
        views.setTextViewText(R.id.appwidget_meaning, word.getMeaning());

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            NotebookWord word = RandomPlayingWord.getARandomWord(context);
            if (word != null) {
                updateAppWidget(context, appWidgetManager, appWidgetId, word);
            }
        }
    }
}

