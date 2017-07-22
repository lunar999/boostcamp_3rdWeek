package com.project3.mini.yunkyun.miniproject3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YunKyun on 2017-07-20.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    @BindView(R.id.tv_map_address) TextView addressView;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private RestaurantDAO restaurantDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        restaurantDAO = new RestaurantDAO(this);

        setToolbar();
        setMapFragment();
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void setMapFragment() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.btn_map_next)
    void onButtonClick(View view) {
        Toast.makeText(this, getResources().getString(R.string.touch_btn_next), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener(){
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });

        loadRestaurants();
        addRestaurant();
    }

    private void loadRestaurants() {
        List<Restaurant> restaurants = restaurantDAO.findAll();
        for(Restaurant restaurant : restaurants) {
            String name = restaurant.getName();
            String memo = restaurant.getMemo();
            double latitude = restaurant.getLatitude();
            double longitude = restaurant.getLongitude();
            LatLng location = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions()
                    .title(name)
                    .snippet(memo)
                    .position(location)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }

    private void addRestaurant() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String memo = intent.getStringExtra("memo");
        double latitude = intent.getDoubleExtra("latitude", 0.0);
        double longitude = intent.getDoubleExtra("longitude", 0.0);

        Restaurant restaurant = new Restaurant(name, address, phoneNumber, memo, latitude, longitude);
        restaurantDAO.insertRestaurant(restaurant);

        addressView.setText(address);

        LatLng location = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions()
                .title(name)
                .snippet(memo)
                .position(location)
                .draggable(true));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_close:
                Toast.makeText(this, getResources().getString(R.string.touch_menu_close), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}
