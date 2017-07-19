package com.study.tedkim.registgoodplace;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;

    Button mNext;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initView(view);

        return view;
    }

    private void initView(View view){

        SupportMapFragment mapFragment = new SupportMapFragment();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_googleMap, mapFragment);
        transaction.commit();

        mapFragment.getMapAsync(this);

        mNext = (Button) view.findViewById(R.id.button_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        LatLng initPosition = new LatLng(-34, 151);
        mGoogleMap.addMarker(new MarkerOptions().position(initPosition).title("Marker In Sydney"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(initPosition));

    }

}
