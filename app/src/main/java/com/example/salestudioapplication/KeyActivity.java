package com.example.salestudioapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class KeyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        goesToMapsActivity();
    }

    public void goesToMapsActivity(){
        final Button button = (Button) findViewById(R.id.goesToMapsActivity); //id bottone della view
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KeyActivity.this,MapsActivity.class));    //porta l'utente da una view ad un'altra
            }
        });
    }
}
