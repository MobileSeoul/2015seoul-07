package com.example.hywoman.LostMistake;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;


public class SplashActivity extends Activity {

    int splashSceneNumber;

    RelativeLayout splashLayout;

    Handler mHandler;

    boolean isClick;
    SQLiteDatabase sqLiteDatabase;
    String sql;
    MySQLiteOpenHelper mySQLiteOpenHelper;

    Geocoder gc;
    Thread t;
    Thread t_phone;
    /**
     * Called when the activity is first created.
     */
    boolean locked=true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getActionBar();
      //  if (actionBar.isShowing()) {
      //      actionBar.hide();
       // }

        // TODO Auto-generated method stub

        // xml 소스 참조
        splashLayout = (RelativeLayout) findViewById(R.id.splashLayout);

        // 처음화면 0
        splashSceneNumber = 0;
       /* mySQLiteOpenHelper = new MySQLiteOpenHelper(this,"lm53.db",null,1000); //데이터베이스 생성,열기
        //getWritable()클래스를 이용한 DataBase 객체 생성
        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();*/
        // 클릭 이벤트가 있었는지 확인
        isClick = true;


        //영구데이터 복원
        SharedPreferences sharedPreferences = getSharedPreferences("mydata",MODE_PRIVATE); //저장했을때 파일명 사용
        locked= sharedPreferences.getBoolean("locked",true); //저장된 파일에 locked라는 변수가 없다면 true로 도움말 실행하도록
        /*
        t = new Thread(new Runnable() {
            @Override

            public void run() {
                Log.d("pjy", "start t");

                //메인스레드 시작
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyXmlPullParser xp = new MyXmlPullParser();
                        xp.xmlParser(sqLiteDatabase,gc);
                        MyXmlPullParserPhone xpp = new MyXmlPullParserPhone();
                        xpp.xmlParser(sqLiteDatabase, gc);
                    }
                });
            }
        });
        t.start();
*/
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (splashSceneNumber) {
                    case 0:
                        // 두번째 화면
                        splashSceneNumber = 1;

                        mHandler.sendEmptyMessage(splashSceneNumber);
                        break;

                    case 1:
                        splashSceneNumber = 2;
                        mHandler.sendEmptyMessageDelayed(splashSceneNumber, 2000);
                        break;

                    case 2:
                        // 엑티비티 종료
                        SplashActivity.this.finish();
                        //불린값이 트루면 도움말 첫실행, false면 이미실행한 기록이 있으므로 mainactivity로 바로이동
                        //if(locked){
                        Intent intent = new Intent(getApplicationContext(), doum.class);
                        startActivity(intent);
                       // }
                        /*else if(locked==false){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        }*/
                        break;

                    case 3:
                        // 딜레이이벤트 클리기 없을경우 바로 0 이벤트로 보낸다..
                        if (isClick && splashSceneNumber == 0) {
                            splashSceneNumber = 0;
                            mHandler.sendEmptyMessage(splashSceneNumber);
                        }
                        break;
                }
            }
        };
        mHandler.sendEmptyMessageDelayed(3, 3000);


    }
    //도움말 첫실행만 실행하기위해 영구데이터 저장
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("mydata",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("locked",false); //boolean값으로 false를 영구데이터 저장.
        editor.commit();
    }

    public void hn_splashOnclick(View v) {

        switch (splashSceneNumber) {
            case 0:
                splashSceneNumber = 0;

                isClick = false;
                mHandler.sendEmptyMessage(splashSceneNumber);
                break;

            case 1:
                splashSceneNumber = 2;
                mHandler.sendEmptyMessage(splashSceneNumber);
                break;
        }
    }


}