package com.example.amin.dictionande.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.amin.dictionande.R;
import com.example.amin.dictionande.RandomPlayingVoc;
import com.example.amin.dictionande.data_model.Notebook;
import com.example.amin.dictionande.data_model.Voc;
import com.example.amin.dictionande.database_open_helpers.NotebookOpenHelper;
import com.example.amin.dictionande.database_open_helpers.ShowWordsWidgetOpenHelper;
import com.example.amin.dictionande.database_open_helpers.VocabularyOpenHelper;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class ShowWordsWidget extends AppWidgetProvider {


    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, Voc voc) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.show_words_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.appwidget_voc, voc.getVoc() + " :");
        views.setTextViewText(R.id.appwidget_meaning,voc.getMeaning());

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

//            ShowWordsWidgetOpenHelper openHelper = new ShowWordsWidgetOpenHelper(context);
//            Voc voc = openHelper.getPlayVoc();

//            updateAppWidget(context, appWidgetManager, appWidgetId, voc);

            Voc voc = RandomPlayingVoc.getVocUsingOtherMethod(context);
            if (voc != null) {
                updateAppWidget(context, appWidgetManager, appWidgetId, voc);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

