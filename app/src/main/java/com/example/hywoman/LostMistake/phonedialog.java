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


public class phonedialog extends Activity {
    ListView grid;
    XmlPullParser parser;
    XmlPullParserFactory factory;
    String key;
    ArrayList<Lost> lost;
    MyAdapterPhone adapter;

    int eventType;
    Lost found;

    Handler mHandler;

    LinearLayout cellhide;//추가
    String data;
    InputStream is;
    DPThread dpThread;
    boolean stop_flag = false;

    //sqlite 데이터베이스
    SQLiteDatabase sqLiteDatabase;
    String sql;
    MySQLiteOpenHelper mySQLiteOpenHelper;
    Cursor cursor;
    //지도사용
    String searchStr;
    Geocoder gc;
    Double lat;
    Double lon;
    String mTel; //전화걸기 연락처

    String data2;

    //이미지관련 코드
    ImageView img;
    ProgressDialog pDialog;
    /////
    int pos = -1;
    boolean open_flag = false;
    String str_color;
String fdSndata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonedialog);

        //운영계정 인증키
         // key = "csJWEPxvxx9ERkQ6Ci2sVOH3A2LlUNTLMKgtF%2BWmQcH%2FSHhyiXjhhUk2fUbxidV56R2wEbWGPEB%2B4efWZmmCYA%3D%3D";
        //서윤운영
        key = "EmPUmJGpa9r%2B1f8mzDGIXQc6YESmJf4QRE709OOirpK740jr3KblZCSL5na45My8e9a%2FhtQZ3o6xGv6wKLxw9w%3D%3D";
        //key = "EmPUmJGpa9r%2B1f8mzDGIXQc6YESmJf4QRE709OOirpK740jr3KblZCSL5na45My8e9a%2FhtQZ3o6xGv6wKLxw9w%3D%3D";
        mHandler = new Handler() {//핸들러 객체생성  //오버라이딩해줌
            //5.메세지 도착시 처리하기 alt+insert로 함수추가
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) { //보내는사람이 0 인지 체크를하고 //여러 쓰레드에서 메세지보낼때 필요한 코드!!
                    if (found == null) {
                        Log.d("dhkim", "found is null");
                    }
                    TextView fdPrdtNm = (TextView) findViewById(R.id.fdPrdtNm);
                    fdPrdtNm.setText(found.getFdPrdtNm());
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
                    img = (ImageView) findViewById(R.id.img);
                    //추가
                    TextView fdPlace = (TextView) findViewById(R.id.fdPlace);
                    fdPlace.setText(found.getFdPlace());
                    TextView clrNm = (TextView) findViewById(R.id.clrNm);
                    clrNm.setText(str_color);
                    if (found.getFdFilePathImg().contains("no_img")) {
                        img.setImageResource(R.drawable.noimg);
                    } else
                        new LoadImage().execute(found.getFdFilePathImg());

                    //보관장소 지도에넘겨줌
                    searchStr = found.getDepPlace();
                    mTel = found.getTel();
                    LinearLayout hide = (LinearLayout) findViewById(R.id.hide);

                    Log.d("pjh999", "" + prdtClNm);
                    Log.d("pjh999", "" + prdtClNm.getText().toString());

                    if (prdtClNm.getText().toString().contains("삼성")) {
                        hide.setBackgroundColor(0xff404A7F);
                    } else if (prdtClNm.getText().toString().contains("LG")) {
                        hide.setBackgroundColor(0xffB73D63);
                    } else if (prdtClNm.getText().toString().contains("아이폰")) {
                        hide.setBackgroundColor(0xff727088);
                    } else
                        hide.setBackgroundColor(0xff66666B);

                }
            }

        };

        Intent intent = getIntent();

        data2 = intent.getStringExtra("data1");
        str_color = intent.getStringExtra("str_color");
        fdSndata =intent.getStringExtra("fdSndata");
Log.d("pjy","얻어온 습득순번은"+fdSndata);
        dpThread = new DPThread();
        dpThread.setDaemon(true);
        dpThread.start();

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
    }

    class DPThread extends Thread {
        @Override
        public void run() {
            Log.d("dhkim", "start t");
            stream();
        }
    }

    private void searchLocation(String searchStr) {
        List<Address> addressList = null;
        try {
            addressList = gc.getFromLocationName(searchStr, 3);
            if (addressList != null) {
                for (int i = 0; i < addressList.size(); i++) {
                    Address a = addressList.get(0);//u를 0으로 바꿈임ㅁ의로
                    //contentsText.append("\n주소 :" + a.getAddressLine(0));
                    lat = a.getLatitude();
                    lon = a.getLongitude();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stream() {
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

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(phonedialog.this);
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
                Toast.makeText(phonedialog.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void parsing() {
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(is, "utf-8"));
            int eventType = parser.getEventType();

            Log.d("dhkim4", "event type = " + eventType);
            if (eventType == XmlPullParser.END_DOCUMENT) {
                Log.d("dhkim4", "End Document");
                Log.d("pjy", "END_DOCUMENT 의 eventType=" + eventType);
            }
            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.d("dhkim4", "while");
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        Log.d("dhkim4", "case income" + startTag);
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
                        if ("fdFilePathImg".equals(startTag))
                            found.setFdFilePathImg(parser.nextText());
                        if ("tel".equals(startTag))
                            found.setTel(parser.nextText());
                        if ("clrNm".equals(startTag))
                            found.setClrNm(parser.nextText());
                        if ("fdPlace".equals(startTag)) ////////////////////////////추가함
                            found.setFdPlace(parser.nextText());
                        if ("fdSbjt".equals(startTag))////
                            found.setFdSbjt(parser.nextText());
                        if ("csteSteNm".equals(startTag))
                            found.setCsteSteNm(parser.nextText());
                        break;
                }
                eventType = parser.next();
                Log.d("dhkim4", "eventType = " + eventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("dhkim4", "error occured!!!");
        }
    }

}
