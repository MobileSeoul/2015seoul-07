package com.example.hywoman.LostMistake;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.Locale;

import static android.view.View.VISIBLE;

public class MainActivity extends Activity {
    Animation translateleft;
    Animation translateright;
    LinearLayout slidinglayout;
    ListView grid;
    XmlPullParser parser;
    XmlPullParserFactory factory;
    String key;

    Lost found;

    boolean openorclose = true;

    //sqlite 데이터베이스
    SQLiteDatabase sqLiteDatabase;
    String sql;
    MySQLiteOpenHelper mySQLiteOpenHelper;
    Cursor cursor1;

    //지영_추가함
    String data;

    InputStream is;
    boolean stop_flag = false;

    //지도사용
    Geocoder gc;
    Double lat;
    Double lon;
    String mTel; //전화걸기 연락처
    /////지영
    int pos = -1;
    boolean open_flag = false;
    LostCursorAdapter dbAdapter1;

    String str_color;
    LostCursorAdapter dbAdapter_edit;
    Cursor cursorsh;
    String fdSndata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setIcon(R.drawable.logo000);

        grid = (ListView) findViewById(R.id.grid);
        //지영운영계정 인증키
        key = "csJWEPxvxx9ERkQ6Ci2sVOH3A2LlUNTLMKgtF%2BWmQcH%2FSHhyiXjhhUk2fUbxidV56R2wEbWGPEB%2B4efWZmmCYA%3D%3D";
        //서윤 운영계정
        // key="EmPUmJGpa9r%2B1f8mzDGIXQc6YESmJf4QRE709OOirpK740jr3KblZCSL5na45My8e9a%2FhtQZ3o6xGv6wKLxw9w%3D%3D";
        //진희 운영인증키
        // //key="smaeGgPW1J%2FHeKRyvWmLrEmRgCyYBwxjSbT8yEcOWTEvFD1YsbtrUVRsqCjIqnMVJnZGB8T0L1PawNuVdOz7sQ%3D%3D";
        Log.d("pjy_", "그리드뷰에있는 아이템수=" + grid.getCount());

        gc = new Geocoder(this, Locale.KOREA);
        Thread t = new Thread(new Runnable() {
            @Override

            public void run() {
                Log.d("pjy", "start t");
                //메인스레드 시작
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyXmlPullParser xp = new MyXmlPullParser();     //MyXmlPullParser 객체 생성
                        xp.xmlParser(sqLiteDatabase, gc);               //MyXmlPullParser의 xmlParser 함수 호출 데이터베이스,지오코딩 매개변수
                    }
                });
            }
        });
        t.start();  //스레드 시작

        findViewById(R.id.menutext2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Phone.class);
                startActivity(intent);
                // finish();
            }
        });
        findViewById(R.id.menutext3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                startActivity(intent);
                //finish();
            }
        });
        findViewById(R.id.menutext4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Bell.class);
                startActivity(intent);
                // finish();
            }
        });
        findViewById(R.id.menutext5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), menu_lost_version.class);
                startActivity(intent);
                //finish();
            }
        });
        //MySQLiteOpenHelper 클래스 생성
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "lm53.db", null, 1000); //데이터베이스 생성,열기
        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase(); //getWritable()클래스를 이용한 DataBase 객체 생성
        cursor1 = sqLiteDatabase.rawQuery("SELECT * FROM LOST order by fdYmd desc", null);  //습득물 날짜 내림차순정렬하여 LOST 테이블 검색
        startManagingCursor(cursor1);
        dbAdapter1 = new LostCursorAdapter(this, cursor1, 600);     //습득물(LOST)테이블을 연결하는 LostCursorAdapter어댑터 객체생성
        grid.setAdapter(dbAdapter1);       //리스트뷰 어댑터 연결


        final EditText edit = (EditText) findViewById(R.id.mainedit);   //EditText

        Button searchbtn = (Button) findViewById(R.id.searchbtn);   //검색 버튼
        searchbtn.setOnClickListener(new View.OnClickListener() {          //검색창에서 간단한 키워드로 검색
            @Override
            public void onClick(View view) {
                String searchword = edit.getText().toString();      //EditText에 입력한 text값 변수로 저장
                sql = "select * from LOST where fdPrdtNm like '%" + searchword + "%' order by fdYmd desc";  //입력된 키워드로 검색하는 쿼리문
                Log.d("pjy00", "edittext에서 검색한 쿼리는  " + sql);

                cursorsh = sqLiteDatabase.rawQuery(sql, null);  //select쿼리문 실행
                Log.d("pjy00", "검색된값은" + cursorsh.getCount()); //검색된 결과 갯수
                dbAdapter_edit = new LostCursorAdapter(getApplicationContext(), cursorsh, 1);   //EditText로 검색한 결과를 뿌리기위한 어댑터 객체 생성
                grid.setAdapter(dbAdapter_edit);        //리스트뷰에 어댑터연결

            }
        });
        /////////애니메이션//////////////
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

        //리스트뷰 아이템 클릭리스너- 클릭후 상세화면 정보에 필요한 값들 변수에 저장후 INTENT로 이동
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (grid.getAdapter() == dbAdapter1) {      //연결된 어댑터가 dbAdapter1라는 검색전 어댑터라면
                    data = dbAdapter1.getAtcIdGrid(cursor1, position);      //클릭아이템의 관리ID값 변수에저장
                    fdSndata = dbAdapter1.getFdSnGrid(cursor1, position);   //클릭아이템의 습득순번값 변수에 저장
                    str_color = dbAdapter1.getClrNmGrid(cursor1, position); //클릭아이템의 색상값 변수에 저장
                }
                else if (grid.getAdapter() == dbAdapter_edit) { //연결된 어댑터가 dbAdapter_edit라는 검색후 설정한 어댑터라면
                    data = dbAdapter_edit.getAtcIdGrid(cursorsh, position);
                    str_color = dbAdapter_edit.getClrNmGrid(cursorsh, position);
                    fdSndata = dbAdapter_edit.getFdSnGrid(cursor1, position);
                }

                Log.d("pjy1111", "클릭된 아이템의 관리id는  " + data);

                Intent intent = new Intent(getApplicationContext(), dialog.class);  //상세화면액티비티로 이동하기위한 INTENT 객체 생성
                intent.putExtra("data1", data);         //관리ID를 저장한 변수를 인텐트 객체에 넣음
                intent.putExtra("str_color", str_color);//색상을 저장한 변수를 인텐트 객체에 넣음
                intent.putExtra("fdSndata", fdSndata);  //습득순번을 저장한 변수를 인텐트 객체에 넣음
                startActivity(intent);                  //인텐트 시작
                Log.d("pjh", "인텐트 넘어감");

            }

        });
        findViewById(R.id.topbtn).setOnClickListener(new View.OnClickListener() {   //탑버튼 클릭 리스너
            @Override
            public void onClick(View view) {
                grid.setSelection(0);       //0번째로 이동
            }
        });
    }

    ////////////////////////////////////////////////메뉴 inflater 및 애니메이션//////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu01:
                if (openorclose == true) {
                    slidinglayout.setVisibility(VISIBLE);
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

