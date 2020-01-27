package com.example.salestudioapplication;
//mirco isdead --> da vedere
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
    //codice di richiesta per la localizzazione, se è diverso, non sono io che la sto richiedendo
    //ma un soggetto esterno (falla di sicurezza)
    private static final int Request_User_Location_Code = 99;

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
        }

        addingMarketSaleStudio(); //carico i marker delle sale studio

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



        public void addingMarketSaleStudio(){

            Marker bigiaviMarker;
            Marker paleottiMarker;

            //bigiavi
            LatLng bigiavi = new LatLng(44.4977951, 11.3496894);
            mMap.addMarker(new MarkerOptions().position(bigiavi)
                    .title("Sala Studio Bigiavi"));


            //paleotti
            LatLng paleotti = new LatLng(44.4698162, 11.3191717);
            mMap.addMarker(new MarkerOptions().position(paleotti)
                    .title("Sala Studio Paleotti"));

            mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);

        }

    public int idMap;

    //l'idea è quella di aprire una nuova activity dal click di una specifica aula studio
    //dai che è figo e forse sta venendo fuori qualcosa di buono
    @Override
    public boolean onMarkerClick(Marker marker) {

         Integer dataFromTag = (Integer) marker.getTag();

         switch (marker.getTitle()){
             case  "Sala Studio Bigiavi":
                 //dati da database di posti liberi del bigiavi
                 Toast.makeText(this, "Bigiavi!", Toast.LENGTH_LONG).show();
                 idMap = 1;
                 NextView();
                 break;

             case "Sala Studio Paleotti":
                 //dati da database di posti liberi del paleotti
                 Toast.makeText(this, "Paleotti!", Toast.LENGTH_LONG).show();
                 break;

             default:
                 //se non trova il tag non è stato ancora implementato
                 break;
         }

        return false;
    }



    public void NextView(){

        Intent intent = new Intent(MapsActivity.this, PropertiesSalaStudioActivity.class);
        intent.putExtra("ID_SS", idMap);
        startActivity(intent);

    }
}