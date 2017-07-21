package com.study.tedkim.registgoodplace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar mToolbar;
    ImageButton mBack;
    Button mClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view component 초기화
        initView();

        // register Fragment 호출
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

            // 뒤로가기 화살표
            case R.id.imageButton_back:
                Toast.makeText(this, "뒤로 가기 클릭", Toast.LENGTH_SHORT).show();

                break;

            // '닫기 버튼
            case R.id.button_close:
                finish();   // Activity 종료

                break;
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        // TODO - Application 말고 Realm 객체를 가져 올 방법 생각해 보기
//        Realm realm = new MainApplication().getRealmInstatnce();
//        realm.close();
//    }
}
