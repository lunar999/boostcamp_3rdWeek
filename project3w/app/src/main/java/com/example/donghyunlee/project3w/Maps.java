package com.example.donghyunlee.project3w;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.donghyunlee.project3w.R.id.map;

public class Maps extends Fragment implements OnMapReadyCallback {

    @BindView(map)
    MapView mapview;
    @BindView(R.id.addressText)
    TextView addressText;

    private String name;
    private String content;
    private String address;
    private String phoneNum;

    private double lat;
    private double lon;
    // 드래그 시 해당 레코드인 StoreV0 객체
    private StoreVO cur_StoreV0;

    private static final String TAG = Maps.class.getSimpleName();
    private RealmResults<StoreVO> storeList;
    private Realm mRealm;
    private LatLng mLatLng;
    public Maps() {}
    public static Maps newInstance(RegItem item){
        Maps appmap = new Maps();
        Bundle args = new Bundle();
        args.putString("name", item.getName());
        args.putString("content", item.getContent());
        args.putString("address", item.getAddress());
        args.putString("phoneNum", item.getPhoneNum());
        appmap.setArguments(args);
        return appmap;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.addmap, container, false);
        ButterKnife.bind(this, v);

        /**
            Applciation에서 Realm 객체 받아오기
         */
        MainApplication mainAplication = (MainApplication) getActivity().getApplication();
        mRealm = mainAplication.getmRealm();

        init();
        /**
            지도 위 주소 정보 setting
         */
        mapview.onCreate(savedInstanceState);
        mapview.onResume();
        mapview.getMapAsync(this);
        // 뷰위에 뷰 덮어쓰기
        addressText.setText(address);
        addressText.bringToFront();
        return v;
    }
    private void init() {
        address = getArguments().getString("address");
        phoneNum = getArguments().getString("phoneNum");
        name = getArguments().getString("name");
        content = getArguments().getString("content");
    }

    @OnClick(R.id.mapback)
    void backmapFun(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(Maps.this).commit();
        fragmentManager.popBackStack();
    }
    @OnClick(R.id.mapexit)
    void mapExitFun(){
        Toast.makeText(getActivity(), R.string.exitMessage, Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.bottomnext)
    void bottomNextfun(){
        /**
         * REALM >> INSERT
         */
        insert_CRUD(name, address, phoneNum, content);
        storeList = getStoreList();
        Log.i(TAG, ">>>>>   storeList.size :  " + storeList.size()); // :1
        Log.i(TAG, ">>>>>     storeList. :  "+storeList.get(0));
        Toast.makeText(getActivity(), "설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(Maps.this).commit();
        fragmentManager.popBackStack();
    }

    private RealmResults<StoreVO> getStoreList(){
        return mRealm.where(StoreVO.class).findAll();
    }
    private void insert_CRUD(final String name, final String address, final String phoneNum, final String content) {
        // All writes must be wrapped in a transaction to facilitate safe multi threading
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                StoreVO mStroeVO = realm.createObject(StoreVO.class, new Date().getTime());
                mStroeVO.setStoreName(name);
                mStroeVO.setStoreAddress(address);
                mStroeVO.setStorePhone(phoneNum);
                mStroeVO.setStoreContent(content);
                mStroeVO.setMakerLat(lat);
                mStroeVO.setMakerLon(lon);
            }
        });
    }
    private void update_CRUD(final StoreVO cur_StoreV0, final double cur_lat, final double cur_lon) {
        // Update person in a transaction
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                try {
                    cur_StoreV0.setMakerLat(cur_lat);
                    cur_StoreV0.setMakerLon(cur_lon);
                }catch (NullPointerException e)
                {
                    Log.e(TAG, ">>>>>     update_CRUD NULLPOINTEREXCEPTION ERROR"+ "lat:"+cur_lat + "  lon:"+cur_lon);
                    Toast.makeText(getActivity(), "마커가 수정되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *  TODO 내 위치 퍼미션 요청 결과
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "11111111111111111111111111111111111111111111111111111111111111111111");
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        // 내위치 퍼미션 확인
        if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // 내 위치를 가져오는 것을 허가하며, 버튼을 Enable
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            Log.i(TAG, "Permission success");
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            map.setMyLocationEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            Log.i(TAG, "Permission fail");
        }
        // 입력된 위치의 위도 경도 가져오기.
        Geocoder mGeocoder = new Geocoder(getContext());
        try {
            List<Address> data = mGeocoder.getFromLocationName(address, 1);
            Log.e(TAG, "Geocoder lat:" + data.get(0).getLatitude() +"lon" + data.get(0).getLongitude());
            lat = data.get(0).getLatitude();
            lon = data.get(0).getLongitude();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getActivity(), "해당되는 주소가 없습니다. 주소를 다시 입력해주세요. ", Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(Maps.this).commit();
            fragmentManager.popBackStack();
            e.printStackTrace();
        }
        // 현재 등록한 카메라 셋팅
        mLatLng = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(mLatLng)
                .title(name).snippet(address).draggable(true));
        map.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.animateCamera(CameraUpdateFactory.zoomTo(14));

        // 모든마커 가져오기
        getAllMarker(map);

        // 드래그 리스너
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener(){
            @Override
            public void onMarkerDragStart(Marker marker) {

                for(StoreVO storevo : storeList){
                    Log.e(TAG, ">>>>>>>>>>>>>> " + storevo.getStoreName());
                    // 현재 드래그되는 storevo객체 찾기 (Tag를 통해)
                    if(storevo.equals(marker.getTag())){
                        cur_StoreV0 = storevo;
                    }
                }
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.i(TAG, "포지션: " +marker.getPosition());
                update_CRUD(cur_StoreV0, marker.getPosition().latitude, marker.getPosition().longitude);
                Toast.makeText(getActivity(), "마커 설정 완료", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getAllMarker(final GoogleMap map)
    {
        // Realm에 등록된 모든 마커 가져오기.
        storeList = getStoreList();
        Log.e(TAG,">>>>>   userList.size :  " + storeList.size());
        for(StoreVO storevo : storeList){
            mLatLng = new LatLng(storevo.getMakerLat(), storevo.getMakerLon());
            map.addMarker(new MarkerOptions().position(mLatLng)
                    .title(storevo.getStoreName()).snippet(storevo.getStoreAddress()).draggable(true)).setTag(storevo);
        }
    }
}
