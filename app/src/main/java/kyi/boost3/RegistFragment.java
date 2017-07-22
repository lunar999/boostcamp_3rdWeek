package kyi.boost3;

import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Kyu on 2017-07-18.
 */

public class RegistFragment extends BaseFragment {

    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private TextView text_count;
    private EditText edit_content, edit_name, edit_tel, edit_address;
    private Button button_back, button_next;
    private View view;
    String name, tel, contents;
    double latitude, longitude;
    private Resources res;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_regist, container, false);
        res = getResources();
        init();
        setListeners();
        return view;
    }

    private void init() {
        edit_address = (EditText) view.findViewById(R.id.edit_address);
        text_count = (TextView) view.findViewById(R.id.text_count);
        edit_content = (EditText) view.findViewById(R.id.edit_content);
        edit_name = (EditText) view.findViewById(R.id.edit_name);
        edit_tel = (EditText) view.findViewById(R.id.edit_tel);
        button_back = (Button) view.findViewById(R.id.button_back);
        button_next = (Button) view.findViewById(R.id.button_next);
        prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);
        res = getResources();
    }

    private void setListeners() {
        edit_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        Log.e("Error", "GooglePlayServicesRepairableError");
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Log.e("Error", "GooglePlayServicesNotAvailableException");
                    }
                }
            }
        });

        edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e("Error", "GooglePlayServicesRepairableError");
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e("Error", "GooglePlayServicesNotAvailableException");
                }
            }
        });

        edit_tel.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        edit_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text_count.setText(res.getString(R.string.text_Start) + s.length() + res.getString(R.string.text_end));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_name.length() == 0 || edit_address.length() == 0) {
                    Snackbar.make(view, res.getString(R.string.snackbar_regist), Snackbar.LENGTH_SHORT).setAction(res.getString(R.string.snackbar_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).setActionTextColor(Color.parseColor(res.getString(R.string.snackbar_color))).show();
                } else {
                    name = edit_name.getText().toString();
                    tel = contents = "";
                    if (edit_tel.length() != 0) {
                        tel = edit_tel.getText().toString();
                    }
                    if (edit_content.length() != 0) {
                        contents = edit_content.getText().toString();
                    }
                    editor = prefs.edit();
                    editor.putString("address", edit_address.getText().toString());
                    editor.commit();

                    markers = new MarkerData(getActivity(), "markers.db", null, 1);
                    SQLiteDatabase db = markers.getWritableDatabase();
                    Log.i("Database", name + " / " + latitude + " / " + longitude + " / " + tel + " / " + contents);
                    db.execSQL("INSERT INTO Marker VALUES(null, '" + name + "', " + latitude + ", " + longitude + ", '" + tel + "', '" + contents + "');");
                    db.close();

                    edit_name.setText("");
                    edit_address.setText("");
                    edit_tel.setText("");
                    edit_content.setText("");

                    startFragment(getFragmentManager(), MapFragment.class);
                }
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(view, res.getString(R.string.snackbar_back), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PLACE_AUTOCOMPLETE_REQUEST_CODE == requestCode) {
            if (RESULT_OK == resultCode) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                edit_address.setText(place.getAddress());
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                Log.i("PlaceAutoComplete", "Place: " + place.getName());
                Log.i("PlaceAutoComplete", "Latitude: " + latitude + " Longitude: " + longitude);
                Log.i("PlaceAutoComplete", "Address: " + place.getAddress());
            } else if (PlaceAutocomplete.RESULT_ERROR == resultCode) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);

                Log.i("PlaceAutoComplete", status.getStatusMessage());
            } else if (RESULT_CANCELED == resultCode) {
                Log.i("PlaceAutoComplete", "User canceled");
            }
        }
    }
}
