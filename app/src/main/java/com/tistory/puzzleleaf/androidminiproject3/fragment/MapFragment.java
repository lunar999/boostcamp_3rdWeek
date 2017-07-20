package com.tistory.puzzleleaf.androidminiproject3.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import com.tistory.puzzleleaf.androidminiproject3.service.DbService;
import com.tistory.puzzleleaf.androidminiproject3.service.GeocoderService;

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

    private GoogleMap mMap = null;
    private boolean isRefreshed = false;
    private DbRefreshBroadCastReceiver dbRefresh;

    private final String DB_REFRESH_SERVICE_BROADCAST = "dbRefresh";
    private final String DB_SELECT_SERVICE_BORADCAST = "dbSelect";
    private final String GEOCODER_SERVICE_BROADCAST = "Geocoder";

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
        refreshRequest();
        mapLocationZoomInit();
        markerClickListener();
        markerDragListener();

    }
    //Location과 Zoom 버튼 설정
    private void mapLocationZoomInit(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
    //마커를 클릭하면 동작할 이벤트
    private void markerClickListener(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                //select service BroadCast
                Intent intent = new Intent(getContext(), DbService.class);
                intent.setAction(DB_SELECT_SERVICE_BORADCAST);
                intent.putExtra("select",marker.getSnippet());
                getActivity().startService(intent);
                return true;
            }
        });
    }
    //마커 드레그 리스너
    private void markerDragListener(){
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                mapAddress.setText("Loading");
            }
            @Override
            public void onMarkerDrag(Marker marker) {
                mapAddress.setText(mapAddress.getText().toString()+".");
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.hideInfoWindow();
                marker.setTitle("");
                marker.setSnippet("");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15f));

                Intent intent = new Intent(getContext(), GeocoderService.class);
                intent.setAction(GEOCODER_SERVICE_BROADCAST);
                intent.putExtra("latitude",marker.getPosition().latitude);
                intent.putExtra("longitude",marker.getPosition().longitude);
                getActivity().startService(intent);
            }
        });
    }
    //백그라운드 스레드에서 DB데이터를 읽어옴
    private void refreshRequest(){
        Intent intent = new Intent(getActivity(), DbService.class);
        intent.setAction(DB_REFRESH_SERVICE_BROADCAST);
        getActivity().startService(intent);
    }

    //읽어온 데이터를 추가
    synchronized private void refreshData() {
        //flag 처리를 안하면 빠르게 지도 Fragment를 왔다갔다 하는 경우 이전에 요청한 서비스에 의해서 2번 갱신될 수 있음
        if(isRefreshed){
            return;
        }
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
        isRefreshed = !isRefreshed; // 이미 갱신 했음을 체크
    }

    //BroadCast 등록
    private void dbBroadCastInit() {
        dbRefresh = new DbRefreshBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DB_REFRESH_SERVICE_BROADCAST);
        filter.addAction(DB_SELECT_SERVICE_BORADCAST);
        filter.addAction(GEOCODER_SERVICE_BROADCAST);
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
        if(dbRefresh!=null) {
            getActivity().unregisterReceiver(dbRefresh);
        }
    }


    private class DbRefreshBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DB_REFRESH_SERVICE_BROADCAST)) {
                  refreshData();
            }
            //마커 클릭시 DB 정보 읽어오기 위함
            else if(intent.getAction().equals(DB_SELECT_SERVICE_BORADCAST)){
                String data = intent.getStringExtra("select");
                if(data!=null) {
                    Toast.makeText(getContext(), data,Toast.LENGTH_SHORT).show();
                }
            }
            //Marker Drag시 새로운 주소를 구하기 위함
            else if(intent.getAction().equals(GEOCODER_SERVICE_BROADCAST)){
                String address = intent.getStringExtra("address");
                if(address!=null) {
                    mapAddress.setText(address);
                }
            }
        }
    }
}
