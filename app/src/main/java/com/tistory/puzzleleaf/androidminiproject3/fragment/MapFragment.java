package com.tistory.puzzleleaf.androidminiproject3.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tistory.puzzleleaf.androidminiproject3.R;
import com.tistory.puzzleleaf.androidminiproject3.db.Db;
import com.tistory.puzzleleaf.androidminiproject3.item.MarkerData;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by cmtyx on 2017-07-16.
 */
//MapView 클래스 사용자는 모든 액티비티 수명 주기 메서드를 MapView 클래스의 해당 메서드로 전달해야 합니다.
//수명 주기 메서드의 예로는 onCreate(), onDestroy(), onResume(), onPause() 등이 있습니다.
//MapView 메모 - https://developers.google.com/maps/documentation/android-api/lite?hl=ko#_5

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean isRefreshed = false;
    private DbRefreshBroadCastReceiver dbRefresh;

    @BindView(R.id.map_btn) Button mapButton;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.map_address) TextView mapAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbBroadCastInit(); //BroadCastReceiver 등록
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapLocationZoomInit();
        markerClickListener();
        markerDragListener();

        //이미 초기화 되었다면 하지 않는다.
        if (!isRefreshed) {
            refreshData();
        }
    }

    //Location과 Zoom 버튼 설정
    private void mapLocationZoomInit(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        else{
            //권한설정
            setPermission();
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    //마커를 클릭하면 동작할 이벤트
    private void markerClickListener(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                mapAddress.setText(marker.getSnippet());
                Toast.makeText(getContext(), Db.dbHelper.selectName(marker.getSnippet()), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    //마커 드레그 리스너
    private void markerDragListener(){
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                Geocoder geocoder;
                List<android.location.Address> addresses;
                geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);

                    String address = addresses.get(0).getAddressLine(1) + addresses.get(0).getAddressLine(0);

                    marker.setTitle("null");
                    marker.setSnippet(address);
                    marker.hideInfoWindow();

                    mapAddress.setText(address);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15f));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //마커에 데이터를 추가
    private void refreshData() {
        LatLng mkLatLng = null;
        for (MarkerData temp : Db.markerDatas) {
            mkLatLng = new LatLng(temp.getLatitude(), temp.getLongitude());
            mMap.addMarker(new MarkerOptions().position(mkLatLng)
                    .title(temp.getName()).snippet(temp.getAddress()).draggable(true)).showInfoWindow();
        }
        if (mkLatLng != null) {
            //마지막에 추가된 지역으로 이동
            mapAddress.setText(Db.markerDatas.get(Db.markerDatas.size()-1).getAddress());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mkLatLng, 15f));
        }
    }

    //BroadCast 등록
    private void dbBroadCastInit() {
        dbRefresh = new DbRefreshBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("dbrefresh");
        getActivity().registerReceiver(dbRefresh, filter);
    }

    @OnClick(R.id.map_btn)
    public void mapButtonClick(){
        getActivity().onBackPressed();
    }

    //MapView를 위한 수명주기 설정
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        //BroadCast 해제
        getActivity().unregisterReceiver(dbRefresh);
    }

    private class DbRefreshBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 백그라운드 쓰레드에서 데이터 갱신이 완료 된 이후에 마커를 추가해야 한다.
            // 맵 초기화 보다 데이터 갱신이 빠를 수 있다. 예외 처리가 필요하다.
            if (intent.getAction().equals("dbRefresh")) {
                if (mMap != null) {
                    refreshData();
                    isRefreshed = !isRefreshed; // 이미 갱신 했음을 체크
                }
            }
        }
    }

    //권한 설정
    private void setPermission(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            getActivity().onBackPressed(); //권한이 없으면 이전 fragment 화면으로 돌아감
        }
    }
}
