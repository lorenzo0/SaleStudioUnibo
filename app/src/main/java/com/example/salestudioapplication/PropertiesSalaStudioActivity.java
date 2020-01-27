package com.example.salestudioapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PropertiesSalaStudioActivity extends AppCompatActivity {

    EditText nameSS;
    EditText freeSeatSS;
    Intent intent;
    int sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_sala_studio);

        nameSS = (EditText) findViewById(R.id.NameSS);
        freeSeatSS = (EditText) findViewById(R.id.FreeSeat);
        intent = getIntent();
        sessionId = intent.getIntExtra("ID_SS", 0);

        String URL = "http://192.168.43.44/aulestudio/getFreeSeats.php?IdCamera="+sessionId;
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                        new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("pos",response);
                        freeSeatSS.setText("Are now free /n"+response+" seats");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nameSS.setText("Something didn't work!");
                Log.e("err",error.toString());
                //System.out.println(error);
            }
        });
        queue.add(stringRequest);
    }



}
