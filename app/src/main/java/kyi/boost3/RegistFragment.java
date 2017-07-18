package kyi.boost3;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

/**
 * Created by Kyu on 2017-07-18.
 */

public class RegistFragment extends BaseFragment {

    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private TextView text_address, text_count;
    private EditText edit_content, edit_name, edit_tel;
    private Resources res;
    private Button button_back, button_next;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_regist, container, false);
        res = getResources();
        initView();
        setListeners();
        return view;
    }

    private void initView() {
        text_address = (TextView) view.findViewById(R.id.text_address);
        text_count = (TextView) view.findViewById(R.id.text_count);
        edit_content = (EditText) view.findViewById(R.id.edit_content);
        edit_name = (EditText) view.findViewById(R.id.edit_name);
        edit_tel = (EditText) view.findViewById(R.id.edit_tel);
        button_back = (Button) view.findViewById(R.id.button_back);
        button_next = (Button) view.findViewById(R.id.button_next);
    }

    private void setListeners() {
        text_address.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch(GooglePlayServicesRepairableException e) {
                    Log.e("Error", "GooglePlayServicesRepairableError");
                } catch(GooglePlayServicesNotAvailableException e) {
                    Log.e("Error", "GooglePlayServicesNotAvailableException");
                }
            }
        });

        edit_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text_count.setText(res.getString(R.string.text_Start) + s.length() + res.getString(R.string.text_end));
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        button_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(edit_name.length() == 0 || text_address.length() == 0) {
                    Snackbar.make(view, "Name and Address are empty", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    startFragment(getFragmentManager(), MapFragment.class);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(PLACE_AUTOCOMPLETE_REQUEST_CODE == requestCode) {
            if(RESULT_OK == resultCode) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                text_address.setText(place.getName());
                Log.i("PlaceAutoComplete", "Place: " + place.getName());
                Log.i("PlaceAutoComplete", "Latitude: " + place.getLatLng().latitude + " Longitude: " + place.getLatLng().longitude);
            }
            else if(PlaceAutocomplete.RESULT_ERROR == resultCode) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);

                Log.i("PlaceAutoComplete", status.getStatusMessage());
            }
            else if(RESULT_CANCELED == resultCode) {
                Log.i("PlaceAutoComplete", "User canceled");
            }
        }
    }
}
