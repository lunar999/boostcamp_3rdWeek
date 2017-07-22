package com.example.donghyunlee.project3w;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {



    @BindView(R.id.addstorename)
    EditText addstorename;
    @BindView(R.id.addstoreaddress)
    Button addstoreaddress;
    @BindView(R.id.addstorenumber)
    EditText addstorenumber;
    @BindView(R.id.addstoretext)
    EditText addstoretext;
    @BindView(R.id.lengthtext)
    TextView lengthtext;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final String[] dialogItems = {"주소검색", "직접입력", "내용지우기", "취소"};
    private EditText inputText;
    private Maps maps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        // Text 리스너
        addstoretext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lengthtext.setText(s.length()+"");
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void openFragment(final Maps fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.register_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void insertAddress() {
        AlertDialog.Builder dialog = createDialog();
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.insertaddress, (ViewGroup)findViewById(R.id.popup_root));
        inputText = (EditText)layout.findViewById(R.id.popup_input);
        dialog.setView(layout);
        dialog.show();
    }

    private AlertDialog.Builder createDialog()
    {
        AlertDialog.Builder insertDialog = new AlertDialog.Builder(this);
        insertDialog.setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.dialog_posi, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addstoreaddress.setText(inputText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.dialog_nega, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        return insertDialog;
    }

    private void searchAddress() {
        try{
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Log.e(TAG, "requestCode"+RESULT_OK+requestCode);
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                addstoreaddress.setText(place.getAddress());
                Log.e(TAG, "Place:" + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: HANDLE THE ERROR
                Log.e(TAG, status.getStatusMessage());
            } else if (requestCode == RESULT_CANCELED){
                // TODO: USER CANCLED OPPERATION.!!
            }
        }
    }

    /**
     * OnClick 리스너
     */
     @OnClick(R.id.topback)
    void topBackFun(){
        Toast.makeText(this, R.string.backMessage, Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.topexit)
    void topExitFun() { Toast.makeText(this, R.string.exitMessage, Toast.LENGTH_SHORT).show();}
    @OnClick(R.id.bottomback)
    void bottomBackfun(){ finish();}
    @OnClick(R.id.bottomnext)
    void bottomNextfun() {
        TextView[] items = {addstorename, addstoreaddress, addstorenumber, addstoretext };
        String [] items_name = {"가게 이름", "가게 주소", "가게 전화번호", "가게 정보"};
        // TODO inputText.seterror 방법으로 에러 처리 가능
        for(int i = 0 ; i < items.length ; i++) {
            if(items[i].length() <= 0) {
                if(items[i] == addstoreaddress) {
                    addressClick();
                }
                items[i].requestFocus();
                Toast.makeText(this, items_name[i] + R.string.textagainMessage, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // 프래그먼트 생성, 입력된 data 전송하기
        openFragment(maps.newInstance(new RegItem(addstorename.getText().toString(), addstoretext.getText().toString(),
                addstoreaddress.getText().toString(), addstorenumber.getText().toString())));
    }

    @OnClick(R.id.addstoreaddress)
    void addressClick()
    {
        Log.e(TAG, "addressClick");
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.search_dialog)
                .setItems(dialogItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case 0: // 주소검색
                                searchAddress();
                                break;
                            case 1: // 직접입력
                                insertAddress();
                                break;
                            case 2: // 지우기
                                addstoreaddress.setText(null);
                                break;
                            case 3: // 취소
                                break;
                        }

                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();                            // 알림창 띄우기

    }
}
