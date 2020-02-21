package com.example.salestudioapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.sql.Time;

public class PropertiesSalaStudioActivity extends AppCompatActivity {

    EditText nameSS, TodayOpening, Adress;
    TextView freeSeatSS;
    ImageView SSphoto;
    Intent intent;
    int sessionId;
    Time checkOpenHour, checkCloseHour;
    String urlImage, resultHttpRequest;
    String[] OpenHourFromJson, CloseHourFromJson;
    SalaStudio salaStudio = new SalaStudio();

    public androidx.appcompat.widget.Toolbar toolbar1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_sala_studio);

        nameSS = (EditText) findViewById(R.id.NameSS);
        freeSeatSS = (TextView) findViewById(R.id.FreeSeat);
        SSphoto = (ImageView) findViewById(R.id.SSphoto);
        TodayOpening = (EditText) findViewById(R.id.TodayOpening);
        Adress = (EditText) findViewById(R.id.adressSS);


        intent = getIntent();
        sessionId = intent.getIntExtra("ID_SS", 0);
        urlImage = intent.getStringExtra("Image");
        //Log.d("tagimg", urlImage);

        final String URL = "https://aulestudiounibo.altervista.org/aulestudio/getFreeSeats.php?IDStudyRoom=" + sessionId;
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String result) {
                        //Log.d("pos",response);
                        //freeSeatSS.setText("Are now free /n"+result+" seats");
                        resultHttpRequest = result;

                        //Log.d("resultHTTP", resultHttpRequest);

                        try {
                            JSONObject obj = new JSONObject(resultHttpRequest);

                            JSONObject jsonObjectSalaStudio = obj.getJSONObject("SalaStudio");

                            String id = (String) jsonObjectSalaStudio.getString("ID");
                            String name = (String) jsonObjectSalaStudio.getString("name");
                            String addressStreet = (String) jsonObjectSalaStudio.getString("addressStreet");
                            String addressNumber = (String) jsonObjectSalaStudio.getString("addressNumber");
                            String latitude = (String) jsonObjectSalaStudio.getString("latitude");
                            String longitude = (String) jsonObjectSalaStudio.getString("longitude");

                            String totalSeats = (String) jsonObjectSalaStudio.getString("totalSeats");
                            String occupiedSeats = (String) jsonObjectSalaStudio.getString("occupiedSeats");

                            String dayOfTheWeek = (String) jsonObjectSalaStudio.getString("dayOfTheWeek");
                            String openingHour = (String) jsonObjectSalaStudio.getString("openingHour");
                            String closingHour = (String) jsonObjectSalaStudio.getString("closingHour");



                            nameSS.setText(name);
                            Adress.setText(addressStreet + ", " +addressNumber );

                            Picasso.get().load(urlImage).into(SSphoto);

                            OpenHourFromJson = openingHour.split(":");
                            CloseHourFromJson = closingHour.split(":");

                            TodayOpening.setText(salaStudio.getCurrentDayOfTheWeek() + ", From " +OpenHourFromJson[0] + ":" +OpenHourFromJson[1]
                                                + " to " +CloseHourFromJson[0] + ":" +CloseHourFromJson[1]);

                            int freeSeat = Integer.parseInt(totalSeats) - Integer.parseInt(occupiedSeats);

                                if(freeSeat>=10)
                                {
                                    freeSeatSS.setText("Are now free " + freeSeat + " seats, go and get them!");
                                }else if(freeSeat<10){
                                    freeSeatSS.setText("Not many seats are free, only " + freeSeat + ", go and get them");
                                    freeSeatSS.setBackgroundColor(-256); //yellow
                                }else if(freeSeat==2) {
                                    freeSeatSS.setText("Are you with a friend? There's only two seats available!");
                                    freeSeatSS.setBackgroundColor(-256); //yellow
                                }else if(freeSeat==1) {
                                    freeSeatSS.setText("Are you alone? There's only one seat available!");
                                    freeSeatSS.setBackgroundColor(-256); //yellow
                                }else if(freeSeat==0){
                                    freeSeatSS.setText("No seats available, try another study room");
                                    freeSeatSS.setBackgroundColor(-65536); //red
                                }

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
