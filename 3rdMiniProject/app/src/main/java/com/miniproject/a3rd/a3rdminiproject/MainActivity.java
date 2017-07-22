package com.miniproject.a3rd.a3rdminiproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miniproject.a3rd.a3rdminiproject.model.Restaurant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    // Views
    @BindView(R.id.add_title) EditText mTitleView;
    @BindView(R.id.add_address) EditText mAddressView;
    @BindView(R.id.add_phone) EditText mPhoneView;
    @BindView(R.id.add_content_num) TextView mContentCounterView;
    @BindView(R.id.add_content) EditText mContentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initActionBar();
        initEditors();
    }

    // bar 설정
    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.title_name));
    }

    // 에디터 설정
    private void initEditors() {
        // Phone Format
        mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // 내용 글자 수 카운트
        final String counterHeader = getString(R.string.add_content_num_header);
        final int contentMaxNum = getResources().getInteger(R.integer.content_max_length);

        mContentCounterView.setText(counterHeader + mContentView.getText().length()+"/"+contentMaxNum);
        mContentView.setHint(String.format(getString(R.string.add_hint_content), contentMaxNum));
        mContentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mContentCounterView.setText(counterHeader + s.length()+"/"+contentMaxNum);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    // 다음 버튼 클릭
    @OnClick(R.id.add_bt_next)
    void onNextButton() {
        if(isEmptyEditors(mTitleView) || isEmptyEditors(mAddressView)
                || isEmptyEditors(mPhoneView) || isEmptyEditors(mContentView)) {
            Toast.makeText(this, getString(R.string.err_empty_any), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(Restaurant.TITLE_NAME, mTitleView.getText().toString())
                .putExtra(Restaurant.ADDRESS_NAME, mAddressView.getText().toString())
                .putExtra(Restaurant.PHONE_NAME, mPhoneView.getText().toString())
                .putExtra(Restaurant.CONTENT_NAME, mContentView.getText().toString());
        startActivity(intent);
    }

    // 빈칸 확인
    private boolean isEmptyEditors(EditText view) {
        if(TextUtils.isEmpty(view.getText().toString())) {
            view.setError(getString(R.string.err_editor_empty));
            view.requestFocus();
            return true;
        }
        return false;
    }



    // 닫기 메뉴 추가
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

    @OnClick(R.id.add_bt_prev)
    void onPreviousButton() {
        finish();
    }
}
