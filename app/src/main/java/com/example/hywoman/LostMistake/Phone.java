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

////////////////////MAINACTIVITY와 구조 동일
public class Phone extends Activity {
    Animation translateleft;
    Animation translateright;
    LinearLayout slidinglayout;

    boolean openorclose = true;

    ListView grid;
    XmlPullParser parser;
    XmlPullParserFactory factory;
    String key;

    Lost found;
    String data;
    InputStream is;


    //sqlite 데이터베이스
    SQLiteDatabase sqLiteDatabase;
    String sql;
    MySQLiteOpenHelper mySQLiteOpenHelper;
    Cursor cursor;
    Geocoder gc;


    MyCursorAdapter dbAdapter;
    MyCursorAdapter dbAdapter_search;
    Cursor cursorsh; //EditText에서 검색한 결과 커서
    String str_color;
    String fdSndata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        getActionBar().setIcon(R.drawable.logo000);

        grid = (ListView) findViewById(R.id.grid);

        gc = new Geocoder(this, Locale.KOREAN);
        //운영계정 인증키
        key = "csJWEPxvxx9ERkQ6Ci2sVOH3A2LlUNTLMKgtF%2BWmQcH%2FSHhyiXjhhUk2fUbxidV56R2wEbWGPEB%2B4efWZmmCYA%3D%3D";
        Log.d("pjy_", "그리드뷰에있는 아이템수=" + grid.getCount());
        Thread t = new Thread(new Runnable() {
            @Override

            public void run() {
                Log.d("pjy", "start t");

                //메인스레드 시작
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyXmlPullParserPhone xpp = new MyXmlPullParserPhone();
                        xpp.xmlParser(sqLiteDatabase, gc);
                    }
                });
            }
        });

        t.start();
        findViewById(R.id.menutext1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.menutext3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.menutext4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Bell.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.menutext5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), menu_lost_version.class);
                startActivity(intent);
                finish();
            }
        });

        //MySQLiteOpenHelper 클래스 생성
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "lm53.db", null, 1000);
        //getWritable()클래스를 이용한 DataBase 객체 생성

        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM PHONE  order by fdYmd desc", null);
        startManagingCursor(cursor);
        dbAdapter = new MyCursorAdapter(this, cursor, 1000);
        grid.setAdapter(dbAdapter);


        final EditText edit = (EditText) findViewById(R.id.mainedit);

        Button searchbtn = (Button) findViewById(R.id.btn1);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchword = edit.getText().toString();
                sql = "select * from PHONE where fdPrdtNm like '%" + searchword + "%' order by fdYmd desc";
                Log.d("pjy00", "edittext에서 검색한 쿼리는  " + sql);

                cursorsh = sqLiteDatabase.rawQuery(sql, null);
                Log.d("pjy00", "검색된값은" + cursorsh.getCount()); //커서검색시작
                //////////
                dbAdapter_search = new MyCursorAdapter(getApplicationContext(), cursorsh, 1);
                grid.setAdapter(dbAdapter_search);

            }
        });
        ////
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

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (grid.getAdapter() == dbAdapter) {
                    data = dbAdapter.getAtcIdGrid(cursor, position);/////////////////////////////////////////
                    fdSndata = dbAdapter.getFdSnGrid(cursor, position);
                    str_color = dbAdapter.getClrNmGrid(cursor, position);
                } else if (grid.getAdapter() == dbAdapter_search) {
                    data = dbAdapter_search.getAtcIdGrid(cursorsh, position);
                    str_color = dbAdapter_search.getClrNmGrid(cursorsh, position);
                    fdSndata = dbAdapter_search.getFdSnGrid(cursor, position);
                }
                Log.d("pjy1111", "클릭된 아이템의 관리id는  " + data);
                Log.d("pjy1111", "클릭된 아이템의 색상은  " + str_color);
                Intent intent = new Intent(getApplicationContext(), phonedialog.class);
                intent.putExtra("data1", data);
                intent.putExtra("fdSndata", fdSndata);
                intent.putExtra("str_color", str_color);

                startActivity(intent);
            }
        });
        findViewById(R.id.topbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grid.setSelection(0);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone, menu);
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