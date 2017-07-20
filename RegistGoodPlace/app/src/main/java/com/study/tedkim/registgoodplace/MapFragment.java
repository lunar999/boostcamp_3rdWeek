package com.study.tedkim.registgoodplace;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    GoogleMap mGoogleMap;
    CameraPosition mCameraPosition;

    Button mNext;
    TextView mCurrentAddr;

    static final int MAX_LEN = 1;

    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {

        // GoogleMap Fragment 를 호출한다
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_MapContainer, mapFragment);
        transaction.commit();
        mapFragment.getMapAsync(this);

        // Button 동작 정의
        mNext = (Button) view.findViewById(R.id.button_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mCurrentAddr = (TextView) view.findViewById(R.id.textView_currentAddr);
    }

    // GoogleMap 이 최초로 로딩 될때의 동작 정의
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        // 1. 초기 맵 위치 설정
        Bundle bundle = getArguments();
        String address = bundle.getString("ADDRESS");
        mCurrentAddr.setText(address);
        try {

            // 1.1 Geocoder 를 이용한 주소 검색
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> addressList = geocoder.getFromLocationName(address, MAX_LEN);

            // 1.2 유사도가 가장 높은 주소를 기반으로 '좌표 설정'
            LatLng initPosition = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());

            // 1.3 설정 된 좌표를 기반으로 '마커 설정'
            mGoogleMap.addMarker(new MarkerOptions().position(initPosition).draggable(true).title(address));
            mGoogleMap.setOnMarkerDragListener(this);

            // 1.4 설정 된 좌표를 기반으로 '카메라 동작 설정'
            mCameraPosition = CameraPosition.builder().target(initPosition).zoom(14).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. Map 에 필요한 위젯 설정
        // 2.1 googleMap ui control 획득
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        // 2.2 ui components 설정
        uiSettings.setZoomControlsEnabled(true);    // +/- 버튼 활성화
        uiSettings.setCompassEnabled(true);     // 나침반 활성화
        uiSettings.setMapToolbarEnabled(true);  // googleMap ToolBar 활성화

        // 2.3 MyLocation 버튼 활성화를 위한 self permission 체크
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // permission 이 허용 상태이면 MyLocation 버튼을 활성화 한다
            mGoogleMap.setMyLocationEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
        }

    }

    // Marker 의 Drag 작업 정의
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    // Marker 가 Drop 되었을 때,
    @Override
    public void onMarkerDragEnd(Marker marker) {

        // 1. 현재 위치정보를 가져온다.
        LatLng currentPosition = marker.getPosition();
        mCameraPosition = CameraPosition.builder().target(currentPosition).zoom(14).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

        Log.e("CURRENT_MARKER", "Lat : "+currentPosition.latitude+" / Lng : "+currentPosition.longitude);

    }
}
