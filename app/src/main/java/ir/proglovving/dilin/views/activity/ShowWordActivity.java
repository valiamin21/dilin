package ir.proglovving.dilin.views.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.TooltipCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import ir.proglovving.dilin.R;
import ir.proglovving.dilin.custom_views.ToolTip;

public class ShowWordActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private ImageButton deleteImageButton, bookmarkImageButton, editImageButton, ukSpeakImageButton, usSpeakImageButton, rotateImageButton;
    private FrameLayout ukSpeechFrameLayout, usSpeechFrameLayout;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ShowWordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_word);

        initViews();

    }

    private void initViews() {
        deleteImageButton = findViewById(R.id.btn_delete);
        bookmarkImageButton = findViewById(R.id.btn_bookmark);
        editImageButton = findViewById(R.id.btn_edit);
        ukSpeakImageButton = findViewById(R.id.img_speech_uk);
        usSpeakImageButton = findViewById(R.id.img_speech_us);
        rotateImageButton = findViewById(R.id.btn_rotate);
        usSpeechFrameLayout = findViewById(R.id.frm_us);
        ukSpeechFrameLayout = findViewById(R.id.frm_uk);

        deleteImageButton.setOnClickListener(this);
        bookmarkImageButton.setOnClickListener(this);
        editImageButton.setOnClickListener(this);
        ukSpeakImageButton.setOnClickListener(this);
        usSpeakImageButton.setOnClickListener(this);
        rotateImageButton.setOnClickListener(this);

//        deleteImageButton.setOnLongClickListener(this);
//        bookmarkImageButton.setOnLongClickListener(this);
//        editImageButton.setOnLongClickListener(this);
//        ukSpeakImageButton.setOnLongClickListener(this);
//        usSpeakImageButton.setOnLongClickListener(this);
//        rotateImageButton.setOnLongClickListener(this);

        TooltipCompat.setTooltipText(deleteImageButton,"delete");
        TooltipCompat.setTooltipText(bookmarkImageButton,"bookmark");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {

        if (v.getContentDescription() != null) {
            new ToolTip(this, v.getContentDescription().toString(), v).show();
        }

        return true;
    }
}
