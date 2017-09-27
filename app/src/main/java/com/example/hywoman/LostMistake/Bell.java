package com.example.hywoman.LostMistake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;


public class Bell extends Activity {
    Animation translateleft;
    Animation translateright;
    LinearLayout slidinglayout;
    boolean openorclose = true;

    Switch swc1;//알림 스위치
    Switch swc2;

    String prdt1;//검색하려는 물건
    String prdt2;//검색하려는 기종//핸드폰
    String color1;//검색하려는 색상
    String color2;//검색하려는 색상//핸드폰

    //DB
    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase sqLiteDatabase;
    boolean locked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bell);
        ////////////////////////////////////DB열기
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "lm53.db", null, 1000);
        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();

        getActionBar().setIcon(R.drawable.logo000);

        final LinearLayout bellhide = (LinearLayout) findViewById(R.id.belldie);
        //영구데이터 복원
        SharedPreferences sharedPreferences = getSharedPreferences("mydata",MODE_PRIVATE); //저장했을때 파일명 사용
        locked= sharedPreferences.getBoolean("locked",false); //저장된 파일에 locked라는 변수가 없다면 true로 도움말 실행하도록

        swc1 = (Switch) findViewById(R.id.switch1);
        swc1.setChecked(locked);
        if(locked){
            bellhide.setVisibility(View.VISIBLE);
        }
        else {
            bellhide.setVisibility(View.INVISIBLE);
        }



        swc1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    //On일때
                    bellhide.setVisibility(View.VISIBLE);
                } else {
                    //off일때
                    end(compoundButton);
                    bellhide.setVisibility(View.INVISIBLE);
                }
            }
        });
        //스피너 찾고 아이템세팅시켜주기
        final Spinner spinner_color1 = (Spinner) findViewById(R.id.spinner_color1);
        final Spinner spinner_color2 = (Spinner) findViewById(R.id.spinner_color2);
        final Spinner spinner_prdt1 = (Spinner) findViewById(R.id.spinner_prdt1);
        final Spinner spinner_prdt2 = (Spinner) findViewById(R.id.spinner_prdt2);

        ///////setting버튼누르면 서비스시작.
        ArrayAdapter<CharSequence> spinColor1 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinnerColor, android.R.layout.simple_spinner_item);//arrayadapter로 색상배열 설정
        spinColor1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_color1.setAdapter(spinColor1); //어댑터설정
        spinner_color1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                color1 = spinner_color1.getSelectedItem().toString();
                Log.d("pjy_bell", "내가선택한 색상1은 " + color1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<CharSequence> spinColor2 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinnerColor, android.R.layout.simple_spinner_item);
        spinColor2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_color2.setAdapter(spinColor2);
        spinner_color2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                color2 = spinner_color2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //분실물,핸드폰
        ArrayAdapter<CharSequence> spinPrdt1 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinnerPrdtClNm, android.R.layout.simple_spinner_item);
        spinPrdt1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_prdt1.setAdapter(spinPrdt1);
        spinner_prdt1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prdt1 = spinner_prdt1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ////////////////////핸드폰기종명
        ArrayAdapter<CharSequence> spinPrdt2 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinnerFdPrdtNmPhone, android.R.layout.simple_spinner_item);
        spinPrdt2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_prdt2.setAdapter(spinPrdt2);
        spinner_prdt2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prdt2 = spinner_prdt2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        swc2 = (Switch) findViewById(R.id.switch2);
        swc2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout bell = (LinearLayout) findViewById(R.id.belldie2);
                if (isChecked) {
                    //On일때
                    bell.setVisibility(View.VISIBLE);
                    start(buttonView);
                } else {
                    //off일때
                    bell.setVisibility(View.INVISIBLE);
                    end(buttonView);
                }
            }
        });
        //////////////////////////////////////////////종 누르면 서비스시작과동시에 설정한 값 intent로 보냄.
        ImageView alert1 = (ImageView) findViewById(R.id.alert1);
        alert1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(view);
            }
        });
        ////////////////////////////////////////////////////////////메뉴
        findViewById(R.id.menutext1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.menutext2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Phone.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.menutext3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                startActivity(intent);
                finish();
            }
        });
        //**소현이 추가***
        findViewById(R.id.menutext5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), menu_lost_version.class);
                startActivity(intent);
                finish();
            }
        });
        translateleft = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateright = AnimationUtils.loadAnimation(this, R.anim.translate_right);


        slidinglayout = (LinearLayout) findViewById(R.id.slidinglayout);

        translateleft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openorclose = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        translateright.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openorclose = true;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        findViewById(R.id.rlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openorclose == false) {
                    slidinglayout.startAnimation(translateright);
                    slidinglayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //서비스 시작 시키기
    public void start(View v) {
        //인텐트 객체 생성
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("prdt1", prdt1);
        intent.putExtra("prdt2", prdt2);
        intent.putExtra("color1", color1);
        intent.putExtra("color2", color2);

        //서비스 시작시키기
        startService(intent);

    }

    //서비스 종료 시키기
    public void end(View v) {
        //인텐트 객체 생성
        Intent intent = new Intent(this, MyService.class);
        //서비스 종료시키기
        stopService(intent);
    }
    //영구데이터 저장
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("mydata",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        locked =swc1.isChecked();
        editor.putBoolean("locked",locked); //boolean값으로 false를 영구데이터 저장.
        editor.commit();
    }
    ////////////////////////메뉴 inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bell, menu);
        return true;
    }

    ////////////////////////메뉴 애니메이션/////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu01:
                if (openorclose == true) {
                    slidinglayout.setVisibility(View.VISIBLE);
                    slidinglayout.startAnimation(translateleft);
                } else {
                    slidinglayout.startAnimation(translateright);
                    slidinglayout.setVisibility(View.INVISIBLE);
                }
                break;
        }
        switch (item.getItemId()) {
            case R.id.menu1:
                Intent a = new Intent(this, menu_lost_version.class);
                startActivity(a);
                break;
            case R.id.menu2:
                Intent b = new Intent(this, menu_lost_help.class);
                startActivity(b);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
