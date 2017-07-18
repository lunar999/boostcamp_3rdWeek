package kyi.boost3;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MapActivity extends AppCompatActivity {
//    private RegistFragment regist;
//    private MapFragment map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        regist = new RegistFragment();
//        map = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new RegistFragment()).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.container, map).commit();


    }


}
