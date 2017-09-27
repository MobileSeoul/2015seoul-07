package com.example.hywoman.LostMistake;

/**
 * Created by 박지영 on 2015-09-29.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.Locale;

//manifest에 등록해야함.
public class MyService extends Service {
    Geocoder gc;
    //알림을 띄울 것이므로
    NotificationManager notiManager;
    ;
    //thread
    ServiceThread0 thread;
    //알림을 중복을 피하기 위한 상태값
    final int MyNoti = 0;
    String prdt1;
    String prdt2;
    String color1;
    String color2;

    //THread

    //예제로 서비스할 메세지
    SQLiteDatabase sqLiteDatabase;
    MySQLiteOpenHelper mySQLiteOpenHelper;
    //파싱
    XmlPullParser parser;
    XmlPullParserFactory factory;

    Lost found;
    ArrayList<Lost> lost;

    Intent intent;
    String atcidobj;//상세화면으로 보여줄 관리id
    @Override
    public IBinder onBind(Intent intent) {
        //할일 없음
        return null;
    }

    //서비스가 시작되면 onstartcommand가 호출된다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //데베열기
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "lm53.db", null, 1000);
        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();

        //
        gc = new Geocoder(this, Locale.KOREA);      //지오코더 생성
        prdt1 = intent.getStringExtra("prdt1");     //알림에서 선택한 물품 데이터 가져옴
        prdt2 = intent.getStringExtra("prdt2");
        color1 = intent.getStringExtra("color1");   //알림에서 선택한 색상 데이터 가져옴
        color2 = intent.getStringExtra("color2");
        Log.d("pjy_service", "선택한색상 서비스로 가져옴  = " + prdt1 + prdt2);
        notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);    //notification을 위한 매니저 생성
        //백그라운드에서 작업할 내용을 초기화 하고 시작한다.
        thread = new ServiceThread0(mHandler, sqLiteDatabase);   ////이너클래스로 쓰래드 생성
        //스레드 시작하기
        thread.start();

        return START_STICKY;         //서비스 시작 종료시 종료
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        //스레드 종료시키기
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    //백그라운드 스레드로부터 메세지를 받을 핸들러 객체
    Handler mHandler = new Handler() {//핸들러 객체생성  //오버라이딩해줌
        public void handleMessage(Message msg) {
            //what으로 메시지를 보고 obj로 메시지를 받는다.(형변환필수)

            //notification 객체 생성(상단바에 보여질 아이콘, 메세지, 도착시간 정의)

            Notification noti = new Notification
                    (R.drawable.logo000//알림창에 띄울 아이콘
                            , "습득물이 등록되었습니다.", //간단 메세지
                            System.currentTimeMillis()); //도착 시간*/

            //기본으로 지정된 소리를 내기 위해.
            noti.defaults = Notification.DEFAULT_SOUND;
            //알림 소리를 한번만 내도록
            noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            //확인하면 자동으로 알림이 제거 되도록
            noti.flags = Notification.FLAG_AUTO_CANCEL;
            //사용자가 알람을 확인하고 클릭했을때 새로운 액티비티를 시작할 인텐트 객체
/*
            if(msg.arg1==1){
            intent = new Intent(MyService.this, dialog.class);
            atcidobj = (String)msg.obj;
            intent.putExtra("data1",atcidobj);

            }
            else
                intent = new Intent(MyService.this, MainActivity.class);*/
            intent = new Intent(MyService.this, MainActivity.class);
            //새로운 태스크(Task) 상에서 실행되도록(보통은 태스크1에 쌓이지만 태스크2를 만들어서 전혀 다른 실행으로 관리한다)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //인텐트 객체를 포장해서 전달할 인텐트 전달자 객체
            PendingIntent pendingI = PendingIntent.getActivity(MyService.this, 0, intent, 0);
            //받아온 메시지 담기
            String arrivedMsg = (String) msg.obj;
            //상단바를 드래그 했을때 보여질 내용 정의하기
            noti.setLatestEventInfo(MyService.this, "[분명 실수였다]", arrivedMsg, pendingI);

            //알림창 띄우기(알림이 여러개일수도 있으니 알림을 구별할 상수값, 여러개라면 상수값을 달리 줘야 한다.)
            notiManager.notify(MyNoti, noti); //알림명,노티피게이션

        }
    };



    public class ServiceThread0 extends Thread {
        //서비스 객체의 핸들러
        Handler handler;
        boolean isRun = true;
        //예제로 서비스할 메세지
        String msg = "당신이 설정한 물품이 업데이트 되었습니다.";
        Geocoder gc;
        SQLiteDatabase sqLiteDatabase;
        //파싱
        XmlPullParser parser;
        XmlPullParserFactory factory;
        Lost found;
        ArrayList<Lost> lost;
        Cursor cursor;
        //생성자
        public ServiceThread0(Handler handler, SQLiteDatabase sqLiteDatabase) {
            this.handler = handler;
            this.sqLiteDatabase = sqLiteDatabase;
        }

        //스레드 정지 시키는 메소드
        public void stopForever() {
            synchronized (this) {
                this.isRun = false;
                notify();
            }
        }

        //스레드 본체
        public void run() {//String prdt1,String prdt2,String color1,String color2
            while (isRun) {
                try {
                    Thread.sleep(10000);//10초마다 진행
                    //핸들러로 들어오는 메시지별로 다르게 동작.
                    String message = msg;

                    //핸들러에 전달할 Message 객체 생성하기
                    Message m = new Message();
                    m.obj = message;

                    MyXmlPullParser xp_bell = new MyXmlPullParser();
                    xp_bell.xmlParser(sqLiteDatabase, gc);

                    Log.d("pjy_Thread", "내가선택한 분류명,색상은" + prdt1 + ",  " + color1);

                    Log.d("pjy_Thread1",xp_bell.str_atcId);
                    String sub_atcId =xp_bell.str_atcId.substring(0,9); //실제용
                   // Log.d("pjy_Thread1","관리아이디 업데이트날짜 = "+sub_atcId);
                    //String sql = "select * from LOST where atcId like '" + sub_atcId +"%' and prdtClNm like '" + prdt1 + "%' and clrNm like '%" + color1 + "%'";//실제 어플 기능용
                    String sql = "select * from LOST where prdtClNm like '" + prdt1 + "%' and clrNm like '%" + color1 + "%'";//발표용
                    Log.d("pjy00", sql);

                    Log.d("pjy_Thread", "sqLiteDatabase가 열려있나? " + sqLiteDatabase.isOpen());
                    cursor = sqLiteDatabase.rawQuery(sql, null);
                    Log.d("pjy_Thread", "cursor로 select시키고 검색된값갯수 기다림");
                    Log.d("pjy00", "검색된값은" + cursor.getCount()); //커서검색시작

                    m.arg1=cursor.getCount();
                    if (cursor.getCount() != 0) {       //내가 등록한 물건의 특징이 업데이트된 데이터중 있을경우
                        handler.sendMessage(m);         //메세지 보냄.

                    }/*
                    if(cursor.getCount() == 1){
                        String atcidobj = cursor.getString(cursor.getColumnIndex("atcId"));
                        String clrNmobj = cursor.getString(cursor.getColumnIndex("clrNm"));
                        String fdSnobj = cursor.getString(cursor.getColumnIndex("fdSn"));
                        Intent intent0 = new Intent(getApplicationContext(),MyService.class);
                        intent0.putExtra("str_color",clrNmobj);
                        intent0.putExtra("fdSndata",fdSnobj);
                        startActivity(intent0);
                        Log.d("pjy_noti","물건이 하나 업데이되었으므로 클릭시 상세화면 이동");
                        m.obj=atcidobj;

                    }*/

                } catch (Exception e) {
                }

            }
        }

    }

}




