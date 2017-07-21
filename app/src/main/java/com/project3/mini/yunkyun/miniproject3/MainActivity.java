package com.project3.mini.yunkyun.miniproject3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    @BindView(R.id.et_name) EditText nameField;
    @BindView(R.id.et_address) TextView addressField;
    @BindView(R.id.et_phonenumber) EditText phoneNumberField;
    @BindView(R.id.et_memo) EditText memoField;
    @BindView(R.id.tv_character_counter) TextView lengthCounter;
    private Place restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        checkGPS();
        checkPermission();
        setToolbar();
        setOnViewListener();
    }

    private void checkGPS() {
        String context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(context);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertCheckGPS();
        }
    }

    private void alertCheckGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.alert_GPS)).setCancelable(false).setPositiveButton(getResources().getString(R.string.setting), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int i) {
                moveConfigGPS();
            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void moveConfigGPS() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void setOnViewListener() {
        memoField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String counterState = String.format(getResources().getString(R.string.counter_format), charSequence.length());
                lengthCounter.setText(counterState);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addressField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAddress();
            }
        });
    }

    private void registerRestaurant() {
        String nameInput = nameField.getText().toString();
        String addressInput = addressField.getText().toString();
        String phoneNumberInput = phoneNumberField.getText().toString();
        String memoInput = memoField.getText().toString();

        if(nameInput.length() != 0 && addressInput.length() != 0 && phoneNumberInput.length() != 0){
            if(restaurant != null) {
                double latitude = restaurant.getLatLng().latitude;
                double longitude = restaurant.getLatLng().longitude;
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("name", nameInput);
                intent.putExtra("address", addressInput);
                intent.putExtra("phoneNumber", phoneNumberInput);
                intent.putExtra("memo", memoInput);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_requirement), Toast.LENGTH_SHORT).show();
        }
    }

    private void searchAddress() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                restaurant = PlaceAutocomplete.getPlace(this, data);
                addressField.setText(restaurant.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                // TODO: Handle the error.
            } else if (resultCode == RESULT_CANCELED) {
                // TODO: Handle canceled operation.
            }
        }
    }

    @OnClick({R.id.btn_prev, R.id.btn_main_next})
    void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn_prev:
                Toast.makeText(this, getResources().getString(R.string.touch_btn_prev), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_main_next:
                registerRestaurant();
                break;
            default:
                break;
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
