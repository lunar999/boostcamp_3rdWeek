package com.miniproject.a3rd.a3rdminiproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    // For 내용 글자 수 표시
    @BindString(R.string.add_content_num_header) String mStrCounterHead;
    @BindString(R.string.add_content_num_footer) String mStrCounterFoot;

    // Views
    @BindView(R.id.add_name) EditText mNameView;
    @BindView(R.id.add_address) EditText mAddressView;
    @BindView(R.id.add_phone) EditText mPhoneView;
    @BindView(R.id.add_content_num) TextView mContentCounterView;
    @BindView(R.id.add_content) EditText mContentView;

    // 다음 버튼 클릭
    @OnClick(R.id.add_bt_next)
    void onNextButton() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.TITLE, mNameView.getText().toString());
        intent.putExtra(MapsActivity.ADDRESS, mAddressView.getText().toString());
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.title_name));

        // Phone Format
        mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        // 내용 글자 수 카운트
        mContentCounterView.setText(mStrCounterHead + mContentView.getText().length()+ mStrCounterFoot);
        mContentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mContentCounterView.setText(mStrCounterHead + s.length() + mStrCounterFoot);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : case R.id.menu_exit :
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
