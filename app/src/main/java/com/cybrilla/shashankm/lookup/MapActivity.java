package com.cybrilla.shashankm.lookup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private EditText radius;
    private LatLng loc;
    private Circle c;
    private GoogleApiClient mGoogleApiClient;
    private static final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Getting Google Play availability status
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
            public void onMyLocationChange(android.location.Location location) {
                loc = new LatLng(location.getLatitude(), location.getLongitude());

                map.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(loc));
                map.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
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
       /* try {
            com.google.android.gms.location.places.ui.PlacePicker.IntentBuilder intentBuilder =
                    new com.google.android.gms.location.places.ui.PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(loc);
            Intent intent = intentBuilder.build(getApplicationContext());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        }
        catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/
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
