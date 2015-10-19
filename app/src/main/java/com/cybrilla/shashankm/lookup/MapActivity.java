package com.cybrilla.shashankm.lookup;

import android.content.Intent;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private EditText radius;
    private LatLng loc;
    private Location myLocation;
    private static String TAG = MapActivity.class.getSimpleName();
    private String lat, lng;
    private GoogleApiClient mGoogleApiClient;
    private Circle c;
    private GeofencingRequest.Builder mGeofenceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                lat = Double.toString(location.getLatitude());
                lng = Double.toString(location.getLongitude());
                map.addMarker(new MarkerOptions().position(loc).title("My Location"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        };
    }

    //Called when user enters radius
    public void setCircle(View v) {
        if(c != null){
            c.remove();
        }
        radius = (EditText) findViewById(R.id.radius);
        if (!(radius.getText().toString().matches(""))) {
            float circleRadius = Float.valueOf(radius.getText().toString());
            c = map.addCircle(new CircleOptions().center(loc).radius(circleRadius)
                    .fillColor(android.graphics.Color.LTGRAY));
            getRequest(circleRadius);
        }
    }

    public void getRequest(double circleRadius) {
        String newCircleRadius = Double.toString(circleRadius);
        String requestUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius="+newCircleRadius+"&types=food&key=AIzaSyDeZaa4XRHsQ_34jMKFXAQyJKHKfJW_ZEw";
        RequestQueue queue = Volley.newRequestQueue(this);
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
                        String name = result.getString("name");
                        double latitude = location.getDouble("lat");
                        double longitude = location.getDouble("lng");
                        LatLng place = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(place).title(name).draggable(false)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                com.android.volley.VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjReq);
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
