package com.example.test;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.databinding.FragmentMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Vector;


public class MapsFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FragmentMapsBinding binding;
    Vector<MarkerOptions> markerOptions;
    MarkerOptions marker;
    LatLng centerlocation;


    private String URL="http://myhazard.epizy.com/all.php";
    RequestQueue requestQueue;
    Gson gson;
    Information[] information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);

        binding = FragmentMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gson= new GsonBuilder().create();

        centerlocation = new LatLng(3.0, 101);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        centerlocation = new LatLng(3.0,101);

        markerOptions = new Vector<>();
}

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        for(MarkerOptions mark: markerOptions) {
            mMap.addMarker(mark);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerlocation,8));
        enableMyLocation();
        sendRequest();

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                Log.d("Maps","permission granted");
            }
        } else {
            String perms[] = {"android.permission.ACCESS_FINE_LOCATION"};
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this,perms,200);
        }
    }

    public void sendRequest(){
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,URL,onSuccess,onError);
        requestQueue.add(stringRequest);
    }

    public Response.Listener<String> onSuccess = new Response.Listener<String>(){
        @Override
        public void onResponse(String response) {

            information = gson.fromJson(response, Information[].class);
            Log.d("Information", "Number of Information Data Point : " +information.length);

            if(information.length <1){
                Toast.makeText(getApplicationContext(),"Problem Retrieving JSON data", Toast.LENGTH_LONG).show();
                return;
            }

            for (Information info: information) {
                double lat = Double.parseDouble(info.lat);
                double lng = Double.parseDouble(info.lng);
                String title = "Hazard : "+ info.hazard;
                //   String snippet= info.time;
                //  String snippets = info.date;
                String snippet= "Reported by : "+ info.people;

                MarkerOptions marker = new MarkerOptions().title(title)
                        .position(new LatLng(lat,lng))
                        .snippet(snippet)
                        //.snippet(snippets)
                        //  .snippet(snippetss)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                mMap.addMarker(marker);

            }

        }
    };

    public Response.ErrorListener onError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

}