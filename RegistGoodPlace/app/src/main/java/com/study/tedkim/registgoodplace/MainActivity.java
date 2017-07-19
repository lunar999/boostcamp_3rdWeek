package com.study.tedkim.registgoodplace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar mToolbar;
    ImageButton mBack;
    Button mClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        setFragment(new RegisterFragment());
    }

    private void initView(){

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setContentInsetsAbsolute(0,0);

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mClose = (Button) findViewById(R.id.button_close);
        mClose.setOnClickListener(this);

    }

    private void setFragment(Fragment fragment){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_container, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.imageButton_back:


                break;

            case R.id.button_close:

                finish();

                break;
        }
    }
}
