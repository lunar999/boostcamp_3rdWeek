package com.study.tedkim.registgoodplace;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    Button mPrev, mNext;
    EditText mShopName, mShopAddr, mShopPhone, mShopContents;
    TextView mContentsLen;

    Realm mRealm;

    static final int REQ_CAMERA_PERMISSION = 101;
    static final int MAX_ADDRESS_COUNT = 10;

    public RegisterFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Realm Control 획득
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        mRealm = mainApplication.getRealmInstatnce();

        // 등록화면 초기화
        initView(view);

        return view;
    }

    private void initView(View view) {

        mPrev = (Button) view.findViewById(R.id.button_prev);
        mPrev.setOnClickListener(this);

        mNext = (Button) view.findViewById(R.id.button_next);
        mNext.setOnClickListener(this);

        mShopPhone = (EditText) view.findViewById(R.id.editText_shopPhone);
        mShopPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mShopName = (EditText) view.findViewById(R.id.editText_shopName);
        mShopAddr = (EditText) view.findViewById(R.id.editText_shopAddr);

        mContentsLen = (TextView) view.findViewById(R.id.textView_contentsLength);

        mShopContents = (EditText) view.findViewById(R.id.editText_contents);
        mShopContents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mContentsLen.setText("글자수 : "+s.length()+"/500");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_prev:

                break;

            case R.id.button_next:

                // 1. Map Fragment 를 호출하기 전에 위치 정보와 관련된 Permission 들 체크
                checkPermission();

                // 2. 데이터베이스 (Realm) 에 작성된 내용을 저장
                insertData();

                // 3. 작성된 주소를 바탕으로 Map Fragment 호출
                setFragment();

                break;

        }
    }

    // 현재 위치 정보 접근을 위한 permission 체크
    private void checkPermission() {

        // 1. Permission 이 설정 되지 않았다면,
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // 1.1 Permission 요청
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CAMERA_PERMISSION);
        }
    }

    // permission 요청 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 1. 요청한 permission 들에 대해,
        if (requestCode == REQ_CAMERA_PERMISSION) {
            // 2. 권한을 체크한다
            for (int grant : grantResults) {
                // 3. ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION 중 하나라도 거부되었다면,
                if (grant == PackageManager.PERMISSION_DENIED) {

                    // TODO - 어떤 동작을 할지 생각해 볼 것
                    // 4. Activity 를 종료... 한다?
                    Toast.makeText(getContext(), "위치정보를 허용해 주셔야 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 작성된 내용들을 Realm 데이터베이스에 추가
    private void insertData() {

        // 1. 주소 유효성을 검사한다
        String address = mShopAddr.getText().toString();
        if (isAddressValid(address)) {

           mRealm.executeTransaction(new Realm.Transaction(){
                @Override
                public void execute(Realm realm) {

                    ShopInfo data = realm.createObject(ShopInfo.class, new Date().getTime());

                    data.setName(mShopName.getText().toString());
                    data.setAddress(mShopAddr.getText().toString());
                    data.setTel(mShopPhone.getText().toString());
                    data.setContents(mShopContents.getText().toString());
                }
            });
        }
    }

    // 주소정보를 가지고 Map Fragment 호출
    private void setFragment() {

        // 1. 주소 유효성을 검사한다
        String address = mShopAddr.getText().toString();
        if (isAddressValid(address)) {

            MapFragment fragment = new MapFragment();

            // 1.1 주소 정보를 Map Fragment 에 전달
            Bundle bundle = new Bundle();
            bundle.putString("ADDRESS", address);
            fragment.setArguments(bundle);

            // 1.2 Map Fragment 호출
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
            transaction.replace(R.id.layout_container, fragment);
            transaction.commit();
        }

    }

    // 입력된 주소에 대한 유효성 여부를 판단하는 메소드
    private boolean isAddressValid(String address) {

        try {
            // 1. 주소를 아예 입력하지 않았거나 존재하지 않는 주소일 경우 예외처리를 한다
            Geocoder geocoder = new Geocoder(getContext());
            if (address.isEmpty() || geocoder.getFromLocationName(address, MAX_ADDRESS_COUNT).isEmpty()) {

                Toast.makeText(getContext(), "정확한 주소를 입력해 주셔야 합니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 제대로 된 주소라면 ...
        return true;
    }
}
