package ir.proglovving.dilin.views.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ir.proglovving.dilin.R;

public class DonateActivity extends AppCompatActivity {

    private static final String DONATE_URL = "https://idpay.ir/amin-vali";

    private FloatingActionButton coffeeFAB;
    private Button coffeeButton;

    public static void start(Context context) {
        context.startActivity(new Intent(context, DonateActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        initViews();


        coffeeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donateCoffee();
            }
        });

        coffeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donateCoffee();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {
        coffeeFAB.setVisibility(View.GONE);
        super.onBackPressed();
    }

    private void initViews() {
        coffeeFAB = findViewById(R.id.coffee_image);
        coffeeButton = findViewById(R.id.coffee_button);
    }

    private void donateCoffee() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(DONATE_URL));
        startActivity(intent);
    }

}
