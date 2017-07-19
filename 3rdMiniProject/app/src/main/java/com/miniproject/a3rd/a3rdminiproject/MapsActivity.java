package com.miniproject.a3rd.a3rdminiproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.miniproject.a3rd.a3rdminiproject.model.Restaurant;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private static final int PERMISSION_CODE    = 100;
    private static final int ZOOM_LEVEL         = 14;

    @BindView(R.id.map_address)
    TextView mAddressView;

    private Restaurant mCurRestaurant; // 아직 저장되지 않은 맛집 ( '다음' 버튼으로 저장 )
    private Realm mRealm;
    private GoogleMap mMap;
    private Geocoder mGeocode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        mCurRestaurant = getRestaurantFromIntent(getIntent());
        mRealm = Realm.getDefaultInstance();
        mGeocode = new Geocoder(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // intent 에서 데이터 가져오기
    private Restaurant getRestaurantFromIntent(@NonNull Intent intent) {
        String title = intent.getStringExtra(Restaurant.TITLE_NAME);
        String address = intent.getStringExtra(Restaurant.ADDRESS_NAME);
        String phone = intent.getStringExtra(Restaurant.PHONE_NAME);
        String content = intent.getStringExtra(Restaurant.CONTENT_NAME);
        return new Restaurant(title, address, phone, content);
    }

    // 맵 설정
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
        mMap.setIndoorEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        enableMyLocation();

        RealmResults<Restaurant> savedRestaurants = mRealm.where(Restaurant.class).findAll();
        for(Restaurant restaurant : savedRestaurants) {
            addRestaurantInMap(restaurant);
        }
        addRestaurantInMap(mCurRestaurant);
    }

    // '현재 위치로 이동' 버튼 추가
    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(this, getString(R.string.err_permission), Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, PERMISSION_CODE);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE) {
            enableMyLocation();
        }
    }


    private void addRestaurantInMap(Restaurant restaurant) {
        try {
            LatLng foundLatLng = getLatLngFromAddress(restaurant.getAddress());
            mMap.addMarker(new MarkerOptions().title(restaurant.getTitle())
                    .position(foundLatLng)
                    .draggable(true)).setTag(restaurant);
            changeLatLng(restaurant, foundLatLng);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.err_not_found_address), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // 주소로 위치검색
    private LatLng getLatLngFromAddress(String strAddress)
            throws IOException, NullPointerException, IndexOutOfBoundsException {
        List<Address> addresses = mGeocode.getFromLocationName(strAddress, 1);
        return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
    }

    // 위치에서 상세주소 가져오기, 맵 이동
    private void changeLatLng(final Restaurant restaurant, final LatLng latLng)
            throws IOException, NullPointerException, IndexOutOfBoundsException {
        final String strAddress = mGeocode.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                restaurant.setAddress(strAddress);
                mAddressView.setText(restaurant.getAddress());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
            }
        });
    }


    @OnClick(R.id.map_bt_next)
    void saveData() {
        final ProgressDialog progressDialog = ProgressDialog.show(this,
                getString(R.string.progress_saving), getString(R.string.progress_saving_detail), false, false);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Restaurant restaurant = realm.createObject(Restaurant.class, mCurRestaurant.getId());
                restaurant.setAll(mCurRestaurant);
                progressDialog.dismiss();
                finish();
            }
        });
    }


    // MarkerDragListener
    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        try {
            changeLatLng((Restaurant)marker.getTag(), marker.getPosition());
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.err_not_found_address), Toast.LENGTH_SHORT).show();
        }
    }


    // 닫기 메뉴 추가
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
