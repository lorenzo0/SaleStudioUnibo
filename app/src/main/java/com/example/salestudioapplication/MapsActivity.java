package com.example.salestudioapplication;
//mirco isdead --> da vedere
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                LocationListener,
                                                                GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;


    String resultHttpRequest;
    int currentHour;
    String[] OpenHourFromJson, CloseHourFromJson;
    //codice di richiesta per la localizzazione, se è diverso, non sono io che la sto richiedendo
    //ma un soggetto esterno (falla di sicurezza)
    private static final int Request_User_Location_Code = 99;
    SalaStudio salaStudio = new SalaStudio();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);     //crea la vista nell'attività maps

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){     //l'skd del dispositivo deve essere maggiore a quella della programmazione
            checkUserLocationPermission();      //richiedo il permesso di utilizzare la posizione del dispositivo
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);    //carico la mappa
        mapFragment.getMapAsync(this);  //sincronizzo la mappa

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                                == PackageManager.PERMISSION_GRANTED){  //se l'utente mi da il permesso
            buildGoogleApiClient();     //uso l'api di google
            mMap.setMyLocationEnabled(true);    //attivo la localizzazione
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.4963128, 11.349082), 12));   //zoom automatico a Bologna nell'onCreate
        }

        LocationOnMapsRequest(); //carico i marker delle sale studio

    }

    public boolean checkUserLocationPermission()    /*metodo che chiede all'utente se mi da il permesso
                                                     per accedere al suo servizio di localizzazione*/
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
                //se non mi da il permesso, glielo richiedo, genius (?)
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            //se non me lo da per la seconda volta allora rinuncio
        return false;

        }else{
            //permesso concesso
            return true;
        }
    }

        //metodo che elabora i risultati del metodo precedente
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode){
                case Request_User_Location_Code:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                            if(googleApiClient == null){
                                buildGoogleApiClient();
                            }
                            mMap.setMyLocationEnabled(true);
                        }
                    }else{
                        Toast.makeText(this, "Permission Denied...", Toast.LENGTH_LONG).show();
                    }
                    return;
            }
        }

        /*metodo che ci permette di utilizzare l'api di google,
        lo carico solo nel momento nel quale l'utente mi da il permesso di controllare la sua localizzazione*/
        public synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                                                        .addOnConnectionFailedListener(this)
                                                        .addApi(LocationServices.API)
                                                        .build();
        googleApiClient.connect();
        }

        //metodo che aggiorna la posizione dell'utente
        public void onLocationChanged(LocationResult location) {
            lastLocation = location.getLastLocation();  //prendo l'ultima posizione dell'utente

            if(currentUserLocationMarker != null){  //se non è la prima volta che uso il marker
                currentUserLocationMarker.remove(); //allora lo cancello per poi inizializzarlo
            }

            //uso LatLng che è un tipo di variabile che richiede in input due variabili di tipo float
            //la prima è la latitudine, la seconda è la langitudine
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            //creo un nuovo marker, che mi indica l'ultima localizzazione dell'utente
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            System.out.println(latLng);
            markerOptions.title("User Current Location");
            //colore del marker, poco complicato eh?
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            //aggiungo alla mappa il marker appena creato
            currentUserLocationMarker = mMap.addMarker(markerOptions);

            //animazione per muovere la camera e zoomare esattamente nel punto appena trovato, è fighissimo
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomBy(15));

            if(googleApiClient != null){
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
    }

        //metodo che aggiorna la localizzazione dell'utente ogni 5 secondi
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        }

        @Override
        public void onLocationChanged(Location location) {
        }


        public void LocationOnMapsRequest(){
            final String URL = "https://aulestudiounibo.altervista.org/aulestudio/getLocationAuleStudio.php";
            RequestQueue queue = Volley.newRequestQueue(this);


            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(String result) {

                            resultHttpRequest = result;
                            //Log.d("resultHTTP", resultHttpRequest);

                            try {

                                JSONObject obj = new JSONObject(resultHttpRequest);
                                JSONArray jsonObjectSalaStudio = obj.getJSONArray("SalaStudio");

                                for(int i=0; i<jsonObjectSalaStudio.length(); i++) {

                                    JSONObject jsonCurrentObject = jsonObjectSalaStudio.getJSONObject(i);

                                    String name = (String) jsonCurrentObject.getString("name");

                                    String latitude = (String) jsonCurrentObject.getString("latitude");
                                    String longitude = (String) jsonCurrentObject.getString("longitude");

                                    String totalSeats = (String) jsonCurrentObject.getString("totalSeats");
                                    String occupiedSeats = (String) jsonCurrentObject.getString("occupiedSeats");

                                    String dayOfTheWeek = (String) jsonCurrentObject.getString("dayOfTheWeek");
                                    String openingHour = (String) jsonCurrentObject.getString("openingHour");
                                    String closingHour = (String) jsonCurrentObject.getString("closingHour");

                                    Log.d("idLetto", name);

                                    getRestrictionOnAddingMarker(name, latitude, longitude, totalSeats, occupiedSeats, openingHour, closingHour);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            queue.add(stringRequest);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void getRestrictionOnAddingMarker(String name, String latitude, String longitude, String totalSeats,
                                                 String occupiedSeats, String openingHour, String closingHour){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                currentHour = salaStudio.getCurrentHour();
            }

            OpenHourFromJson = openingHour.split(":");
            CloseHourFromJson = closingHour.split(":");

            if(currentHour>Integer.parseInt(OpenHourFromJson[0]) && currentHour<Integer.parseInt(CloseHourFromJson[0])
                    && Integer.parseInt(totalSeats)>Integer.parseInt(occupiedSeats)) {
                addingMarketSaleStudio(name, latitude, longitude);
            }
        }


        public void addingMarketSaleStudio(String name, String latitude, String longitude){

            LatLng newMark = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));

            this.mMap.addMarker(new MarkerOptions().position(newMark)
                    .title("Sala Studio "+name));

            this.mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        }

    public int idMap;

    //l'idea è quella di aprire una nuova activity dal click di una specifica aula studio
    //dai che è figo e forse sta venendo fuori qualcosa di buono
    @Override
    public boolean onMarkerClick(Marker marker) {

         Integer dataFromTag = (Integer) marker.getTag();
        Intent intent = new Intent(MapsActivity.this, PropertiesSalaStudioActivity.class);
        String image;

         switch (marker.getTitle()){

             case "Sala Studio Paleotti":
                 //dati da database di posti liberi del paleotti
                 Toast.makeText(this, "Paleotti!", Toast.LENGTH_LONG).show();

                 image = new String("https://aulestudiounibo.altervista.org/aulestudio/img/Paleotti.jpeg");
                 idMap = 1;

                 intent.putExtra("ID_SS", idMap);
                 intent.putExtra("Image", image);

                 NextView(intent);
                 break;

             default:
                 //se non trova il tag non è stato ancora implementato
                 break;
         }

        return false;
    }


    //metodo che cambia la view quando viene selezionato un marker
    public void NextView(Intent intent){
        startActivity(intent);
    }
}