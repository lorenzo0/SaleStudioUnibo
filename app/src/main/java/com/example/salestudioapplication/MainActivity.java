package com.example.salestudioapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //carico l'attività del main

        goesToMapsActivity(); //rendo attivo il metodo
        goesToKeyActivity();
    }

    public void goesToMapsActivity(){
        final Button button = (Button) findViewById(R.id.goesToMapsActivity); //id bottone della view
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MapsActivity.class));    //porta l'utente da una view ad un'altra
            }
        });
    }

    public void goesToKeyActivity(){
        final Button button = (Button) findViewById(R.id.goesToKeyActivity); //id bottone della view
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,KeyActivity.class));    //porta l'utente da una view ad un'altra
            }
        });
    }
}
