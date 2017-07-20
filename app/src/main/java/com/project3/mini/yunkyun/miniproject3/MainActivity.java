package com.project3.mini.yunkyun.miniproject3;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.et_name) EditText name;
    @BindView(R.id.et_address) EditText address;
    @BindView(R.id.et_phonenumber) EditText phoneNumber;
    @BindView(R.id.et_memo) EditText memo;
    @BindView(R.id.tv_character_counter) TextView lengthCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setToolbar();

        memo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String counterState = String.format(getResources().getString(R.string.counter_format), charSequence.length());
                lengthCounter.setText(counterState);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setToolbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @OnClick({R.id.btn_prev, R.id.btn_next})
    void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn_prev:
                Toast.makeText(this, getResources().getString(R.string.touch_btn_prev), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_next:
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_close:
                Toast.makeText(this, getResources().getString(R.string.touch_menu_close), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
