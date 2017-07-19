package com.study.tedkim.registgoodplace;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    Button mPrev, mNext;
    EditText mShopName, mShopAddr, mShopPhone, mShopContents;
    TextView mContentsLen;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        initView(view);

        return view;
    }

    private void initView(View view){

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

        switch(v.getId()){

            case R.id.button_prev:

                break;

            case R.id.button_next:

                setFragment(new MapFragment());

                break;

        }
    }

    private void setFragment(Fragment fragment){

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
        transaction.replace(R.id.layout_container, fragment);
        transaction.commit();
    }
}
