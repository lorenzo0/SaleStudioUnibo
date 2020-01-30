package com.example.salestudioapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import org.json.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class PropertiesSalaStudioActivity extends AppCompatActivity {

    EditText nameSS, freeSeatSS, TotalSeatSS, Adress;
    ImageView SSphoto;
    Intent intent;
    int sessionId, checkSeats;
    String urlImage, resultHttpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_sala_studio);

        nameSS = (EditText) findViewById(R.id.NameSS);
        freeSeatSS = (EditText) findViewById(R.id.FreeSeat);
        SSphoto = (ImageView) findViewById(R.id.SSphoto);
        TotalSeatSS = (EditText) findViewById(R.id.TotalSeat);
        Adress = (EditText) findViewById(R.id.adressSS);


        intent = getIntent();
        sessionId = intent.getIntExtra("ID_SS", 0);
        urlImage = intent.getStringExtra("Image");
        //Log.d("tagimg", urlImage);

        final String URL = "http://10.201.5.87/aulestudio/getFreeSeats.php?IdCamera=" + sessionId;
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
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
                            String adress = (String) jsonObjectSalaStudio.getString("adress");


                            TotalSeatSS.setText("Total seats of the study room: " + totalSeat);
                            Adress.setText(adress);

                            checkSeats = Integer.parseInt(freeSeat);

                            if(checkSeats>=10)
                            {
                                freeSeatSS.setText("Are now free " + freeSeat + " seats,\n go and get them!");
                                freeSeatSS.setBackgroundColor(-16711936); //green
                            }else if(checkSeats<10){
                                freeSeatSS.setText("Not many seats are free, only " + freeSeat + ", go and get them");
                                freeSeatSS.setBackgroundColor(-256); //yellow
                            }else if(checkSeats==2) {
                                freeSeatSS.setText("Are you with a friend? There's only two seats available!");
                                freeSeatSS.setBackgroundColor(-256); //yellow
                            }else if(checkSeats==1) {
                                freeSeatSS.setText("Are you alone? There's only one seat available!");
                                freeSeatSS.setBackgroundColor(-256); //yellow
                            }else if(checkSeats==0){
                                freeSeatSS.setText("No seats available, try another study room");
                                freeSeatSS.setBackgroundColor(-65536); //red
                            }


                            Picasso.get().load(urlImage).into(SSphoto);

                            nameSS.setText(name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nameSS.setText("Something didn't work!");
                //Log.e("err",error.toString());
            }
        });
        queue.add(stringRequest);
    }

}
