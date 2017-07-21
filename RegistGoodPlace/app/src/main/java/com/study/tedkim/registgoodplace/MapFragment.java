package com.study.tedkim.registgoodplace;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener {

    GoogleMap mGoogleMap;
    CameraPosition mCameraPosition;

    Button mNext;
    TextView mCurrentAddr;

    Realm mRealm;

    Geocoder mGeocoder;

    LatLng mInitPosition;
    LatLng mCurrentPosition;

    static final int MAX_LEN = 1;

    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Realm 객체 초기화
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        mRealm = mainApplication.getRealmInstatnce();

        // Map Fragment View 초기화
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

        mNext = (Button) view.findViewById(R.id.button_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "다음 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        mCurrentAddr = (TextView) view.findViewById(R.id.textView_currentAddr);
    }

    // GoogleMap 이 최초로 로딩 될때의 동작 정의
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGeocoder = new Geocoder(getContext());

        // 1. 데이터베이스에 저장되어 있는 모든 shop 객체를 마커로 표시
        addMarkers();

        // 2. 직전에 입력받았던 주소를 바탕으로 카메라 위치 설정
        moveCamera(mInitPosition);

        // 3. UI Controls & Listeners 설정
        setUiComponents();
    }

    // Realm 데이터베이스에 저장 된 데이터들을 마커로 표시해주기 위한 메소드
    private void addMarkers() {

        // 1. 초기 카메라 position 설정을 위한 주소 data get
        Bundle bundle = getArguments();
        String address = bundle.getString("ADDRESS");
        mCurrentAddr.setText(address);

        // 2. 데이터 베이스에 존재하는 모든 Record 에 대한 Marker 생성
        RealmResults<ShopInfo> results = mRealm.where(ShopInfo.class).findAll();
        for (ShopInfo data : results) {

            try {
                // 2.1 Geocoder 를 이용한 주소 검색
                List<Address> addressList = mGeocoder.getFromLocationName(data.getAddress(), MAX_LEN);

                // 2.2 유사도가 가장 높은 주소를 기반으로 '좌표 설정'
                LatLng position = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());

                // 2.3 직전에 입력된 주소라면 초기 position 으로 설정
                if(address.equals(data.getAddress())){
                    mInitPosition = position;
                }

                // 2.4 설정 된 좌표를 기반으로 '마커 설정'
                mGoogleMap.addMarker(new MarkerOptions().position(position).draggable(true).title(data.getName())).setTag(data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 입력받은 좌표로 카메라 이동하기 위한 메소드
    private void moveCamera(LatLng currentPosition){

        mCameraPosition = CameraPosition.builder().target(currentPosition).zoom(14).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

    }

    // 지도 화면에 필요한 UI Components 들과 각종 Listener 들 설정하기 위한 메소드
    private void setUiComponents(){

        // Map 에 필요한 위젯 설정

        // 1. googleMap ui control 획득
        UiSettings uiSettings = mGoogleMap.getUiSettings();

        // 2. ui components 설정
        uiSettings.setZoomControlsEnabled(true);    // +/- 버튼 활성화
        uiSettings.setCompassEnabled(true);     // 나침반 활성화
        uiSettings.setMapToolbarEnabled(true);  // googleMap ToolBar 활성화

        // 3. MyLocation 버튼 활성화를 위한 self permission 체크
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // permission 이 허용 상태이면 MyLocation 버튼을 활성화 한다
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMyLocationButtonClickListener(this);
            uiSettings.setMyLocationButtonEnabled(true);
        }

        // 4. 마커 리스너 설정
        mGoogleMap.setOnMarkerDragListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
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
        mCurrentPosition = marker.getPosition();
        moveCamera(mCurrentPosition);

        try {
           List<Address> address = mGeocoder.getFromLocation(mCurrentPosition.latitude, mCurrentPosition.longitude, MAX_LEN);
           mCurrentAddr.setText(address.get(0).getAddressLine(0));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        mCurrentPosition = marker.getPosition();
        moveCamera(mCurrentPosition);

        ShopInfo info = (ShopInfo) marker.getTag();
        mCurrentAddr.setText(info.getAddress());

        return false;
    }

    // TODO - myLocation Button 이 클릭 되었을때 동작 정의
    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }
}
