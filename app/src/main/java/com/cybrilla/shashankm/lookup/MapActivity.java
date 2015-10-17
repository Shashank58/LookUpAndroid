package com.cybrilla.shashankm.lookup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private EditText radius;
    private LatLng loc;
    private Circle c;
    private static String TAG = MapActivity.class.getSimpleName();
    private String jsonResponse;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Getting Google Play availability status
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
    }

    private void replaceMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        map = mapFragment.getMap();
        // Enable Zoom
        map.getUiSettings().setZoomGesturesEnabled(true);

        //set Map TYPE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //enable Current location Button
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        //set "listener" for changing my location
        map.setOnMyLocationChangeListener(myLocationChangeListener());
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener() {
        return new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                loc = new LatLng(location.getLatitude(), location.getLongitude());

                map.addMarker(new MarkerOptions().position(loc));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        };
    }

    public void setCircle(View v) {
        if(c != null){
            c.remove();
        }
        radius = (EditText) findViewById(R.id.radius);
        double circleRadius = Double.parseDouble(radius.getText().toString());
        c = map.addCircle(new CircleOptions().center(loc).radius(circleRadius)
                .fillColor(Color.LTGRAY).strokeColor(Color.BLUE).strokeWidth(5));
        getRequest(circleRadius);

    }

    public void getRequest(double circleRadius) {
        String requestUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + loc.toString() + "&radius=" + circleRadius + "&types=food&name=cruise&key=AIzaSyBK_IGgveKbdNOnjATETCQhnmJfnfRgzQ0";
       // showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestUrl, null,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, response.toString());

                try {

                    JSONArray results = response.getJSONArray("results");
                    for(int i= 0;i < results.length(); i++){
                        JSONObject result = (JSONObject) results.get(i);
                        JSONObject geometry = result.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double latitude = location.getDouble("lat");
                        double longitude = location.getDouble("lng");
                        LatLng place = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(place));
                    }
                    // Parsing json object response
                    // response will be a json object
//                    String name = response.getString("name");
//                    String email = response.getString("email");
//                    JSONObject phone = response.getJSONObject("phone");
//                    String home = phone.getString("home");
//                    String mobile = phone.getString("mobile");
//
//                    jsonResponse = "";
//                    jsonResponse += "Name: " + name + "\n\n";
//                    jsonResponse += "Email: " + email + "\n\n";
//                    jsonResponse += "Home: " + home + "\n\n";
//                    jsonResponse += "Mobile: " + mobile + "\n\n";

                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
              //  hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                com.android.volley.VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
               // hidepDialog();
            }
        });

    }

//    private void showpDialog() {
//        if (!pDialog.isShowing())
//            pDialog.show();
//    }
//
//    private void hidepDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//    }



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
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        replaceMapFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
       switch (item.getItemId()){
            case R.id.action_profile:
                String name = getIntent().getExtras().getString("name");
                String id = getIntent().getStringExtra("id");
                Intent i = new Intent(this, UserInfoActivity.class);
                i.putExtra("name", name);
                i.putExtra("id", id);
                startActivity(i);
                break;
        }
        return true;
    }

}
