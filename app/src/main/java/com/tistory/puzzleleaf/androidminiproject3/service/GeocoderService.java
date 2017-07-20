package com.tistory.puzzleleaf.androidminiproject3.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Created by cmtyx on 2017-07-18.
 */
//주소 로딩을 위한 서비스, Activity에서 구현시 약간의 버벅임이 있어서 따로 분리
public class GeocoderService extends IntentService {

    private final String GEOCODER_SERVICE_BROADCAST = "Geocoder";

    public GeocoderService() {
        super("GeocoderService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.getAction().equals(GEOCODER_SERVICE_BROADCAST)) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longitude = intent.getDoubleExtra("longitude", 0);
            String address = "";
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                address = addresses.get(0).getAddressLine(0);
            } catch (Exception e) {
                address = "주소 로딩에 실패했습니다";
            }finally {
                sendBroadcast(new Intent(GEOCODER_SERVICE_BROADCAST).putExtra("address", address));
            }
        }
    }
}
