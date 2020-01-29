package com.example.salestudioapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.*;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PropertiesSalaStudioActivity extends AppCompatActivity {

    EditText nameSS;
    EditText freeSeatSS;
    ImageView SSphoto;
    Intent intent;
    int sessionId;
    String urlImage;
    SalaStudio SalaReturn;
    String resultHttpRequest;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_sala_studio);

        nameSS = (EditText) findViewById(R.id.NameSS);
        freeSeatSS = (EditText) findViewById(R.id.FreeSeat);
        SSphoto = (ImageView) findViewById(R.id.SSphoto);

        intent = getIntent();
        sessionId = intent.getIntExtra("ID_SS", 0);
        urlImage = intent.getStringExtra("Image");
        Log.d("tagimg", urlImage);

        final String URL = "http://10.201.13.85/aulestudio/getFreeSeats.php?IdCamera="+sessionId;
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                        new Response.Listener<String>(){
                    @Override
                    public void onResponse(String result) {
                        //Log.d("pos",response);
                        //freeSeatSS.setText("Are now free /n"+result+" seats");
                        resultHttpRequest = result;

                        Log.d("resultHTTP", resultHttpRequest);

                        try {
                            JSONObject obj = new JSONObject(resultHttpRequest);

                            JSONObject jsonObjectSalaStudio = obj.getJSONObject("SalaStudio");

                            String id = (String) jsonObjectSalaStudio.getString("id");
                            String name = (String) jsonObjectSalaStudio.getString("name");
                            String totalSeat = (String) jsonObjectSalaStudio.getString("totalSeat");
                            String freeSeat = (String) jsonObjectSalaStudio.getString("freeSeat");

                            //Log.d("resultFreeSeat", freeSeat);

                            freeSeatSS.setText("Are now free " +freeSeat+ " seats");

                            Picasso.get().load(urlImage).into(SSphoto);


                            /*try {
                                bmp = BitmapFactory.decodeStream(urlFromPrevious.openConnection().getInputStream());
                                SSphoto.setImageBitmap(bmp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/


                            nameSS.setText(name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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




/*
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, URL, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject c = response.getJSONObject(resultHttpRequest);
                    int id = (int) c.get("id");
                    String name = (String) c.get("name");
                    int totalSeat = (int) c.get("totalSeat");
                    int freeSeat = (int) c.get("freeSeat");

                    Log.i("prova", name);

                    SalaStudio SStoRet = new SalaStudio(id,name,totalSeat,freeSeat);
                    //return SStoRet;

                    Log.i("name", SalaReturn.name);
                    freeSeatSS.setText("Are now free /n" +SStoRet.freeSeat+ " seats");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //System.out.println(SalaReturn.freeSeat);

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                nameSS.setText("Something didn't work!");
                Log.e("err",error.toString());
            }

        });

        queue.add(jsonObjectRequest);*/
    }

    //private SalaStudio parseSSObject(JSONObject ss) throws JSONException {


    //}

}
