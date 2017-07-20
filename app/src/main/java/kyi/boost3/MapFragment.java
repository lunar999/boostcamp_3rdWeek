package kyi.boost3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by Kyu on 2017-07-18.
 */

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private String address;
    private TextView textAddress;
    private View view;
    private Resources res;
    private Button button_next;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        res = getResources();
        textAddress = (TextView) view.findViewById(R.id.textAddress);
        button_next = (Button) view.findViewById(R.id.map_next);
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(view, res.getString(R.string.snackbar_next), Snackbar.LENGTH_SHORT).show();
            }
        });
        prefs = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        address = prefs.getString("address", "");
        textAddress.setText(address);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        markers = new MarkerData(getActivity(), "markers.db", null, 1);
        SQLiteDatabase db = markers.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, latitude, longitude FROM Marker", null);
        while (cursor.moveToNext()) {
            Log.i("getDB", (cursor.getPosition() + 1) + " / " + cursor.getString(0) + " / " + cursor.getDouble(1) + " / " + cursor.getDouble(2));
            this.googleMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(1), cursor.getDouble(2))).title(cursor.getString(0)).draggable(true)).setTag(cursor.getPosition() + 1);
            if (cursor.isLast()) {
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cursor.getDouble(1), cursor.getDouble(2)), 17));
            }
        }
        db.close();
        cursor.close();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            Snackbar.make(view, res.getString(R.string.permission_notification), Snackbar.LENGTH_INDEFINITE).setAction(res.getString(R.string.snackbar_move), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse(res.getString(R.string.package_name)));
                    startActivity(intent);
                }
            }).setActionTextColor(Color.parseColor(res.getString(R.string.snackbar_color))).show();
        }

        this.googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                double mLatitude = marker.getPosition().latitude;
                double mLongitude = marker.getPosition().longitude;
                SQLiteDatabase db = markers.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT _id, name FROM Marker", null);
                while (cursor.moveToNext()) {
                    Log.i("query", cursor.getInt(0) + " / " + cursor.getString(1));
                }
                db.execSQL("UPDATE Marker SET latitude = " + mLatitude + ", longitude = " + mLongitude + " WHERE _id = " + marker.getTag().toString() + ";");
                db.close();
                cursor.close();

                try {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);

                    if (!addresses.isEmpty()) {
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Address address = addresses.get(0);
                                Log.i("Geocoder", address.getAddressLine(0));
                                final String str = address.getAddressLine(0);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textAddress.setText(str);
                                    }
                                });
                            }
                        });
                        t.start();
                    } else {
                        textAddress.setText(res.getString(R.string.no_address));
                    }
                } catch (Exception e) {
                    Log.e("Geocoder", e.toString());
                    textAddress.setText(res.getString(R.string.fail_address));
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}
