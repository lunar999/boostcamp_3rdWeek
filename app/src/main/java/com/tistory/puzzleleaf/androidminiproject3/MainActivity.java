package com.tistory.puzzleleaf.androidminiproject3;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tistory.puzzleleaf.androidminiproject3.db.DBHelper;
import com.tistory.puzzleleaf.androidminiproject3.db.Db;
import com.tistory.puzzleleaf.androidminiproject3.fragment.AddFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        beforeInit();
        fragmentStart();
    }

    private void fragmentStart(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, new AddFragment());
        transaction.commit();
    }

    private void beforeInit(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Db.dbHelper = new DBHelper(getApplicationContext(), "MarkerData.db", null, 1);
    }

    //상단 버튼
    @OnClick(R.id.add_close)
    public void close() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
