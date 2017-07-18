package com.miniproject.a3rd.a3rdminiproject;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TITLE = "TITLE";
    public static final String ADDRESS = "ADDRESS";
    private static final int ZOOM_LEVEL = 16;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.title_name));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setIndoorEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng foundLatLng = new LatLng(-34, 151); // default : 시드니(template default value)
        String strAddress = getIntent().getStringExtra(ADDRESS);
        if( !TextUtils.isEmpty(strAddress)) {
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addresses = geocoder.getFromLocationName(strAddress,5);
                foundLatLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        googleMap.addMarker(new MarkerOptions().position(foundLatLng).title(getIntent().getStringExtra(TITLE)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(foundLatLng, ZOOM_LEVEL));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : case R.id.menu_exit :
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
