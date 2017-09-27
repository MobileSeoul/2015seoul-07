package com.example.hywoman.LostMistake;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by jiyoung on 2015-10-06.
 */
public class MyXmlPullParserPhone {
    ListView grid;
    MySQLiteOpenHelper helper;
    XmlPullParser parser;
    XmlPullParserFactory factory;
    //지영운영계정
    String key = "csJWEPxvxx9ERkQ6Ci2sVOH3A2LlUNTLMKgtF%2BWmQcH%2FSHhyiXjhhUk2fUbxidV56R2wEbWGPEB%2B4efWZmmCYA%3D%3D";
    //서윤운영계정
    //String key="EmPUmJGpa9r%2B1f8mzDGIXQc6YESmJf4QRE709OOirpK740jr3KblZCSL5na45My8e9a%2FhtQZ3o6xGv6wKLxw9w%3D%3D";
    Lost found;

    //sqlite 데이터베이스
    String sql;
    Cursor cursor_atc;

    //지영_추가함
    String data;

    InputStream is;
    Geocoder gc;
    //운영계정 인증키
    String str_clrNm;
    String str_fdSn;
    String str_atcId;
    public void searchLocation(SQLiteDatabase sqLiteDatabase, String searchatc, String searchStr, Geocoder gc) throws IOException { ////여기서 insert 시키지말고 return값으로 보관장소를 변환한것을 보내 insert를 같이하도록 한다.


        List<Address> addressList = null;
        Log.d("pjy_searchLocation", "보관장소파싱후 함수 호출됨");

        addressList = gc.getFromLocationName(searchStr, 3);
        Log.d("pjy_searchLocation", "보관장소파싱후 함수 호출됨" + addressList);
           /*final String address= "서울특별시";

            sql="update LOST set depPlaceFull= '"+address+"' where atcId = '"+searchatc+"'";
            Log.d("pjy_gc",sql);
            sqLiteDatabase.execSQL(sql);*/

        try {
            if (addressList != null) {
                for (int i = 0; i < addressList.size(); i++) {
                    Address a = addressList.get(i);
                    String address = a.getAddressLine(0);
                    Log.d("pjy000", "경찰서의 풀주소는 " + address);
                    sql = "update PHONE set depPlaceFull= '" + address + "' where atcId = '" + searchatc + "'";
                    Log.d("pjy_gc", sql);
                    sqLiteDatabase.execSQL(sql);
                    // Log.d("pjy111", "DB에 존재하면 1 아니면 0 : " + cursor1.getCount());
                    Cursor cursor_gc = sqLiteDatabase.rawQuery(sql, null);
                    Log.d("pjy_gc", "넣어진것의 1번째 값은" + cursor_gc.getString(1));
                }
            }
        } catch (Exception e) {
        }

    }


    public void xmlParser(SQLiteDatabase sqLiteDatabase, Geocoder gc) {

        Log.d("pjy_parsing", "파싱함수 들어옴옴");
        try {
            URL url = new URL("http://openapi.lost112.go.kr/openapi/service/rest/SearchMoblphonInfoInqireService/getMoblphonAcctoKindAreaPeriodInfo?pageNo=1&numOfRows=100&serviceKey=" + key);
            Log.d("pjy", "파싱클래스  " + url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); //url 연결
            urlConnection.setConnectTimeout(200000);
            urlConnection.setReadTimeout(20000000);
            InputStream is = urlConnection.getInputStream();

            if (is == null) {
                //Toast.makeText(getApplicationContext(), "is null", Toast.LENGTH_LONG).show();

            }
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(is, "utf-8"));

            int eventType = parser.getEventType();

            Log.d("pjy", "event type = " + eventType);
            if (eventType == XmlPullParser.END_DOCUMENT) {
                Log.d("pjy", "End Document");
            }
            while (eventType != XmlPullParser.END_DOCUMENT) {

                Log.d("pjy", "while");
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        Log.d("pjy", startTag);
                        if ("item".equals(startTag))
                            found = new Lost();
                        if ("atcId".equals(startTag)) {
                            str_atcId = parser.nextText();

                            found.setAtcId(str_atcId);

                        }
                        if ("fdYmd".equals(startTag))
                            found.setFdYmd(parser.nextText());
                        if ("clrNm".equals(startTag)) {
                            str_clrNm = parser.nextText();
                            found.setClrNm(str_clrNm);
                        }
                        if ("prdtClNm".equals(startTag))
                            found.setPrdtClNm(parser.nextText());
                        if ("fdFilePathImg".equals(startTag))
                            found.setFdFilePathImg(parser.nextText());
                        if ("fdPrdtNm".equals(startTag))
                            found.setFdPrdtNm(parser.nextText());
                        if ("depPlace".equals(startTag)) {
                            String strDep = parser.nextText();
                            found.setDepPlace(strDep);
                        }
                        if ("fdSn".equals(startTag)) {
                            str_fdSn=parser.nextText();
                            found.setFdSn(str_fdSn);
                            sql = "select * from PHONE where atcId = '" + str_atcId + "' and fdSn = '" + str_fdSn +"'";
                            cursor_atc = sqLiteDatabase.rawQuery(sql, null);
                            Log.d("pjy_phone", "DB에 존재하면 1 아니면 0 : " + cursor_atc.getCount()); //커서검색시작
                            if (cursor_atc.getCount() != 0) {     //관리id값이 기존에 있을경우 while문 continue로 나옴
                                continue;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:

                        String endTag = parser.getName();
                        if ("atcId".equals(endTag)) {
                            parser.next();
                        }
                        if ("item".equals(endTag)) {
                            sql = "insert into PHONE(atcId,fdPrdtNm,depPlace,clrNm,fdYmd,prdtClNm,fdFilePathImg,fdSn) values " +
                                    "('" + found.getAtcId() + "','"
                                    + found.getFdPrdtNm() + "','"
                                    + found.getDepPlace() + "','"
                                    + found.getClrNm() + "','"
                                    + found.getFdYmd() + "','"
                                    + found.getPrdtClNm() + "','"
                                    + found.getFdFilePathImg() +  "','"
                                    + found.getFdSn() + "')";
                            Log.d("pjy_parsing", "입력된데이터의 관리ID는" + found.getAtcId());
                            Log.d("pjy_parsing", sql);
                            sqLiteDatabase.execSQL(sql);
                            searchLocation(sqLiteDatabase, found.getAtcId(), found.getDepPlace(), gc);
                        }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("pjy", "error occured!!!");
        }

    }

}
