package com.project3.mini.yunkyun.miniproject3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.google.android.gms.common.api.Status;
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
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setToolbar();
        setViewListener();

    }

    private void setViewListener() {
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

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
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

    private void registerRestaurant() {
        String nameInput = nameField.getText().toString();
        String addressInput = addressField.getText().toString();
        String phoneNumberInput = phoneNumberField.getText().toString();
        String memoInput = memoField.getText().toString();
        if(nameInput.length() != 0 && addressInput.length() != 0 && phoneNumberInput.length() != 0){
            if(place != null) {
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);
                addressField.setText(place.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled operation.
            }
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
