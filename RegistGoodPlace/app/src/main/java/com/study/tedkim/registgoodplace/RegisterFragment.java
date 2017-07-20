package com.study.tedkim.registgoodplace;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    Button mPrev, mNext;
    EditText mShopName, mShopAddr, mShopPhone, mShopContents;
    TextView mContentsLen;

    static final int REQ_CAMERA_PERMISSION = 101;

    public RegisterFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {

        mPrev = (Button) view.findViewById(R.id.button_prev);
        mPrev.setOnClickListener(this);

        mNext = (Button) view.findViewById(R.id.button_next);
        mNext.setOnClickListener(this);

        mShopName = (EditText) view.findViewById(R.id.editText_shopName);
        mShopAddr = (EditText) view.findViewById(R.id.editText_shopAddr);
        mShopPhone = (EditText) view.findViewById(R.id.editText_shopPhone);
        mShopContents = (EditText) view.findViewById(R.id.editText_contents);

        mContentsLen = (TextView) view.findViewById(R.id.textView_contentsLength);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_prev:

                break;

            case R.id.button_next:

                // Map Fragment 를 호출하기 전에 위치 정보와 관련된 Permission 들 체크
                if (checkPermission()) {
                    // 1. Map Fragment 호출
                    String address = mShopAddr.getText().toString();
                    if (!address.isEmpty()) {
                        setFragment(new MapFragment(), address);
                    }
                    else {
                        Toast.makeText(getContext(), "주소를 입력해 주셔야 합니다.", Toast.LENGTH_SHORT).show();

                    }
                }
                else {

                    Toast.makeText(getContext(), "위치정보를 허용해 주셔야 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    // 현재 위치 정보 접근을 위한 permission 체크
    private boolean checkPermission() {

        // 1. Permission 이 설정 되지 않았다면,
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // 1.1 Permission 요청
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CAMERA_PERMISSION);
        }


        return true;
    }

    // permission 요청 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 1. 요청한 permission 들에 대해,
        if (requestCode == REQ_CAMERA_PERMISSION) {
            // 2. 권한을 체크한다
            for (int grant : grantResults) {
                // 3. ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION 중 하나라도 허용이 되지 않으면,
                if (grant == PackageManager.PERMISSION_DENIED) {

                    // TODO - 어떤 동작을 할지 생각해 볼 것
                    // 4. Activity 를 종료... 한다
                    Toast.makeText(getContext(), "허용을 눌러라", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        }
    }

    // 주소정보를 가지고 Map Fragment 호출
    private void setFragment(Fragment fragment, String address) {

        // 1. 주소 정보 전달
        Bundle bundle = new Bundle();
        bundle.putString("ADDRESS", address);
        fragment.setArguments(bundle);

        // 2. Map Fragment 호출
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
        transaction.replace(R.id.layout_container, fragment);
        transaction.commit();
    }
}
