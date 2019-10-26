package ir.proglovving.dilin.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ir.proglovving.dilin.R;

public class DictionarySearchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_search);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, DictionarySearchActivity.class));
    }
}
