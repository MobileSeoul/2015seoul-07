package com.example.hywoman.LostMistake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Detail extends Activity {
    Animation translateleft;
    Animation translateright;
    LinearLayout slidinglayout;
RelativeLayout rlayout;
    boolean openorclose=true;


    XmlPullParser parser;
    XmlPullParserFactory factory;

    String key;
    StringBuffer sb;
    String readLine, tv;
    int eventType;
    String tagName;
    boolean flag;

    String data; //클릭된 받아온 actId


    Lost found;//클릭시 저장된데이터들

    Handler mHandler;
    //
    InputStream is;
    DPThread dpThread;
    boolean stop_flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getActionBar().setIcon(R.drawable.logogogo);

        findViewById(R.id.menutext1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.menutext2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Phone.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.menutext3).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Search.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.menutext4).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Bell.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btnmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Map.class);
                startActivity(intent);
            }
        });
        translateleft = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateright = AnimationUtils.loadAnimation(this, R.anim.translate_right);


        slidinglayout = (LinearLayout)findViewById(R.id.slidinglayout);

        translateleft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openorclose=false;
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
                openorclose=true;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.rlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(openorclose == false){
                    slidinglayout.startAnimation(translateright);
                    slidinglayout.setVisibility(View.INVISIBLE);
                }


            }
        });

        Intent intent = getIntent();

        data = intent.getStringExtra("mydata");
        Log.d("pjy", "누른 아이템의 actID=" + data);
        key = "CC65RkSad7HcOKFtpZCTKahZH3boyDZnEyLRoIxYigcB8x4YOgVPowjyBDxH0r2oAU%2FCdjt0QInd67TfiYGMmQ%3D%3D";


        mHandler = new Handler(){//핸들러 객체생성  //오버라이딩해줌
            //5.메세지 도착시 처리하기 alt+insert로 함수추가
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 0) { //보내는사람이 0 인지 체크를하고 //여러 쓰레드에서 메세지보낼때 필요한 코드!!
                    if(found==null){
                        Log.d("dhkim","found is null");
                    }

                    TextView fdPrdtNm = (TextView) findViewById(R.id.fdPrdtNm);
                    fdPrdtNm.setText(found.getFdPrdtNm());

                    TextView prdtClNm = (TextView) findViewById(R.id.prdtClNm);
                    prdtClNm.setText(found.getPrdtClNm());
                    TextView fdYmd = (TextView) findViewById(R.id.fdYmd);
                    fdYmd.setText(found.getFdYmd());
                    TextView depPlace = (TextView) findViewById(R.id.depPlace);
                    depPlace.setText(found.getDepPlace());
                    TextView tel = (TextView) findViewById(R.id.tel);
                    tel.setText(found.getTel());
                    //TextView clrNm = (TextView) findViewById(R.id.clrNm);
                    //clrNm.setText(found.getClrNm());
                }
            }

        };

        //2.쓰레드 실행
        dpThread = new DPThread();
        dpThread.setDaemon(true);
        dpThread.start();


    }
    class DPThread extends Thread{
        @Override
        public void run() {
            //super.run();

            Log.d("dhkim", "start t");

            // runOnUiThread(new Runnable() { //메인스레드
            //    @Override
            //     public void run() {
            //        Toast.makeText(getApplicationContext(), "dd", Toast.LENGTH_SHORT).show();

            //    }
            // });
            stream();
        }
    }
    public void parsing(){
        try {

            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(is, "utf-8"));

            int eventType = parser.getEventType();

            Log.d("dhkim", "event type = " + eventType);
            if(eventType == XmlPullParser.END_DOCUMENT){
                Log.d("dhkim","End Document");
                Log.d("pjy","END_DOCUMENT 의 eventType="+eventType);
            }

            while(eventType != XmlPullParser.END_DOCUMENT){

                Log.d("dhkim","while");
                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        Log.d("pjy","START_TAG 의 eventType="+eventType);
                        if("response".equals(startTag))
                            found = new Lost();
                        if ("fdPrdtNm".equals(startTag))
                            found.setFdPrdtNm(parser.nextText());
                        if ("prdtClNm".equals(startTag))
                            found.setPrdtClNm(parser.nextText());
                        if ("fdYmd".equals(startTag))
                            found.setFdYmd(parser.nextText());
                        if ("depPlace".equals(startTag))
                            found.setDepPlace(parser.nextText());
                        if ("tel".equals(startTag))
                            found.setTel(parser.nextText());


                        break;

                }
                eventType = parser.next();
                Log.d("dhkim3", "eventType = " + eventType);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("dhkim","error occured!!!");
        }
        //Log.d("dhkim","data number "+ allLost.size());
        // mHandler.sendEmptyMessage(0); //결과값을 obj에 저장하지 않고 편지 보낼경우 이런코드사용.
    }
    public void stream(){ //경찰청홈페이지 정보 읽어들여 파서로 넘겨주기위한 메서드
        HttpURLConnection urlConnection;
        try {
            URL url = new URL("http://openapi.lost112.go.kr/openapi/service/rest/SearchMoblphonInfoInqireService/getMoblphonDetailInfo?ATC_ID=" + data + "&FD_SN=1&serviceKey=" + key);
            Log.d("dhkim3","http://openapi.lost112.go.kr/openapi/service/rest/SearchMoblphonInfoInqireService/getMoblphonDetailInfo?ATC_ID=" + data + "&FD_SN=1&serviceKey=" + key);
            urlConnection = (HttpURLConnection) url.openConnection(); //url 연결

            is = urlConnection.getInputStream();

            parsing();
            stop_flag=true;
            mHandler.sendEmptyMessage(0);
        }catch (Exception e){
        }
    }

/////////////////////////////////////////메뉴 inflater 전개 및 애니메이션//////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu01:
                if(openorclose == true){
                    slidinglayout.setVisibility(View.VISIBLE);
                    slidinglayout.startAnimation(translateleft);
                }
                else{
                    slidinglayout.startAnimation(translateright);
                    slidinglayout.setVisibility(View.INVISIBLE);
                }
                break;
        }
        switch(item.getItemId()){
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
