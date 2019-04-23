package com.example.amin.dictionande.views.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.amin.dictionande.R;

public class DonateActivity extends AppCompatActivity {

    FloatingActionButton coffeeFAB;
    Button coffeeButton;
    String url = "https://idpay.ir/amin-vali";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        coffeeFAB = (FloatingActionButton)findViewById(R.id.coffee_image);
        coffeeButton = (Button)findViewById(R.id.coffee_button);

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

    private void donateCoffee(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
