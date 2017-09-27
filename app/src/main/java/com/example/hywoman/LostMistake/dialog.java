package com.example.hywoman.LostMistake;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class dialog extends Activity {
    Animation translateleft;
    Animation translateright;
    LinearLayout slidinglayout;
    ListView grid;
    MySQLiteOpenHelper helper;
    XmlPullParser parser;
    XmlPullParserFactory factory;
    String key;
    ArrayList<Lost> lost;


    Lost found;
    private GpsInfo gps;
    double latitude;
    double longitude;

    private Button url;
    Handler mHandler;

    boolean openorclose = true;

    //sqlite 데이터베이스
    SQLiteDatabase sqLiteDatabase;
    String sql;
    MySQLiteOpenHelper mySQLiteOpenHelper;
    Cursor cursor1;

    //지영_추가함
    String data;

    String data2;
    InputStream is;
    DPThread dpThread;
    boolean stop_flag = false;
    LinearLayout hide;

    //지도사용
    String searchStr;
    Geocoder gc;
    private Double lat;
    private Double lon;
    String mTel; //전화걸기 연락처

    //이미지관련 코드
    ImageView img;
    ProgressDialog pDialog;

    /////지영
    int pos = -1;
    boolean open_flag = false;
    MyAdapter adapter;

    String str_color;

    //////
    String fdSndata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);


        // getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        grid = (ListView) findViewById(R.id.grid);
        //지영운영계정 인증키
        //key = "csJWEPxvxx9ERkQ6Ci2sVOH3A2LlUNTLMKgtF%2BWmQcH%2FSHhyiXjhhUk2fUbxidV56R2wEbWGPEB%2B4efWZmmCYA%3D%3D";
        //서윤운영계정
        key = "EmPUmJGpa9r%2B1f8mzDGIXQc6YESmJf4QRE709OOirpK740jr3KblZCSL5na45My8e9a%2FhtQZ3o6xGv6wKLxw9w%3D%3D";
       // key = "GDHyXKemYhB%2FDs5aQr33hXkcbm%2FcJ8MVSKtZeIIABe2xlrHjUuKFp55yu5RMPgxtU1hjT%2BuWMam45l50z3oZQA%3D%3D";

        //    Log.d("pjy_","그리드뷰에있는 아이템수="+grid.getCount());
        hide = (LinearLayout) findViewById(R.id.hide);
        //지도보기 버튼클릭시 지도 기능
        Button mapbtn = (Button) findViewById(R.id.mapbtn);
        mapbtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                searchLocation(searchStr);

                Intent intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("mydata", lat);
                intent.putExtra("mydata0", lon);
                startActivity(intent);
            }

        });
        findViewById(R.id.telbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mTel));
                startActivity(intent);
            }
        });
        gc = new Geocoder(this, Locale.KOREAN);

        ImageView finish = (ImageView) findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mHandler = new Handler() {//핸들러 객체생성  //오버라이딩해줌
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) { //보내는사람이 0 인지 체크를하고 //여러 쓰레드에서 메세지보낼때 필요한 코드!!
                    if (found == null) {
                        Log.d("pjy", "found is null");
                    }

                    TextView fdPrdtNm = (TextView) findViewById(R.id.fdPrdtNm);
                    fdPrdtNm.setText(found.getFdPrdtNm());
/////////////////////////////////////////////////////////////////////////////////////
                    if(fdPrdtNm.length() >14){
                        fdPrdtNm.setTextSize(15);
                    }
                    TextView prdtClNm = (TextView) findViewById(R.id.prdtClNm);
                    prdtClNm.setText(found.getPrdtClNm());

                    TextView fdYmd = (TextView) findViewById(R.id.fdYmd);
                    fdYmd.setText(found.getFdYmd());
                    TextView depPlace = (TextView) findViewById(R.id.depPlace);
                    depPlace.setText(found.getDepPlace());

                    TextView tel = (TextView) findViewById(R.id.tel);
                    tel.setText(found.getTel());
                    TextView csteSteNm = (TextView) findViewById(R.id.csteSteNm);
                    csteSteNm.setText(found.getCsteSteNm());

                    TextView fdPlace = (TextView) findViewById(R.id.fdPlace);
                    fdPlace.setText(found.getFdPlace());
                    TextView clrNm = (TextView) findViewById(R.id.clrNm);
                    clrNm.setText(str_color);

                    //이미지 로딩  메인 xml에 이미지 뷰있고 상세화면뜨는 맨위 거기다가 보여줌

                    //아이콘자리에 사진뜨도록

                    img = (ImageView) findViewById(R.id.img);
                    if (found.getFdFilePathImg().contains("no_img")) {
                        img.setImageResource(R.drawable.noimg);
                    } else
                        new LoadImage().execute(found.getFdFilePathImg());

                    //보관장소 지도에넘겨줌
                    searchStr = found.getDepPlace();
                    mTel = found.getTel();


                    Log.d("pjh999", "" + prdtClNm);
                    Log.d("pjh999", "" + prdtClNm.getText().toString());
                    ///눌린 목록에따라 dialog색상이 변화됨
                    if (prdtClNm.getText().toString().contains("지갑")) {
                        hide.setBackgroundColor(0xffA55151);
                    } else if (prdtClNm.getText().toString().contains("카드")) {
                        hide.setBackgroundColor(0xff7F5C6A);
                    } else if (prdtClNm.getText().toString().contains("가방")) {
                        hide.setBackgroundColor(0xff825C57);
                    } else if (prdtClNm.getText().toString().contains("증명서")) {
                        hide.setBackgroundColor(0xffEFAA29);
                    } else if (prdtClNm.getText().toString().contains("신분증")) {
                        hide.setBackgroundColor(0xffD89032);
                    } else if (prdtClNm.getText().toString().contains("휴대폰")) {
                        hide.setBackgroundColor(0xff405772);
                    } else if (prdtClNm.getText().toString().contains("현금")) {
                        hide.setBackgroundColor(0xff457C45);
                    }
                    else if (prdtClNm.getText().toString().contains("귀금속")) {
                        hide.setBackgroundColor(0xff645589);
                    } else
                        hide.setBackgroundColor(0xff66666B);


                }
            }

        };

        Intent intent = getIntent();

        data2 = intent.getStringExtra("data1");
        str_color = intent.getStringExtra("str_color");
        fdSndata =intent.getStringExtra("fdSndata");
        Log.d("pjh", "여기까지 실행했습되" + data2);

        //position = grid.setItemChecked(position,true);
        // data =lost.9get(position).getAtcId();//클릭된 아이템 관리ID (actId)를 보내야함

        Log.d("pjy5", "누른 아이템의 actID=" + data2);
        Log.d("pjy5", "누른 아이템의 position값은  " + pos);
        //2.쓰레드 실행
        dpThread = new DPThread();
        dpThread.setDaemon(true);
        dpThread.start();

    }

    class DPThread extends Thread {
        @Override
        public void run() {
            //super.run();
            Log.d("pjy", "start t");
            stream();
        }
    }

    public void stream() { //경찰청홈페이지url
        HttpURLConnection urlConnection;
        try {
            URL url = new URL("http://openapi.lost112.go.kr/openapi/service/rest/SearchMoblphonInfoInqireService/getMoblphonDetailInfo?ATC_ID=" + data2 + "&FD_SN="+fdSndata+"&serviceKey=" + key);
            Log.d("pjy", ""+url);
            urlConnection = (HttpURLConnection) url.openConnection(); //url 연결
            is = urlConnection.getInputStream();

            parsing();
            stop_flag = true;
            mHandler.sendEmptyMessage(0);
        } catch (Exception e) {
        }
    }

    /////////////////////https 보안 서버라 이미지 로딩 코딩 구현////////////////////////
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(dialog.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                byte[] data2 = HttpUtil.getHttpUtil().doGetBytes(args[0]);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.requestCancelDecode();

                options.inJustDecodeBounds = true;
                options.inSampleSize = 1;

                bitmap = BitmapFactory.decodeByteArray(data2, 0, data2.length);//(data,0,data.length, options);

                Log.d("dhkim", "...");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                img.setImageBitmap(image);
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
                Toast.makeText(dialog.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void searchLocation(String searchStr) {
        List<Address> addressList = null;
        try {
            addressList = gc.getFromLocationName(searchStr, 3);
            if (addressList != null) {
                for (int i = 0; i < addressList.size(); i++) {
                    Address a = addressList.get(0);//u를 0으로 바꿈임ㅁ의로

                    lat = a.getLatitude();
                    lon = a.getLongitude();
                }
            }
        } catch (Exception e) {
        }
    }

    public void parsing() {
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(is, "utf-8"));

            int eventType = parser.getEventType();

            Log.d("pjy", "event type = " + eventType);
            if (eventType == XmlPullParser.END_DOCUMENT) {
                Log.d("pjy4", "End Document");
                Log.d("pjy", "END_DOCUMENT 의 eventType=" + eventType);
            }
            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.d("pjy", "while");
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        Log.d("pjy", "case income" + startTag);
                        Log.d("pjy4", "START_TAG 의 eventType=" + eventType);

                        if ("response".equals(startTag))
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
                        if ("clrNm".equals(startTag))
                            found.setClrNm(parser.nextText());
                        if ("fdFilePathImg".equals(startTag))
                            found.setFdFilePathImg(parser.nextText());
                        if ("csteSteNm".equals(startTag))
                            found.setCsteSteNm(parser.nextText());
                        if ("fdPlace".equals(startTag))
                            found.setFdPlace(parser.nextText());
                        break;
                }
                eventType = parser.next();
                Log.d("pjy4", "eventType = " + eventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("pjy4", "error occured!!!");
        }
    }

}
