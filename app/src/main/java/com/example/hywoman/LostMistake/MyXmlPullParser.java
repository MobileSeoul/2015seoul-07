package com.example.hywoman.LostMistake;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by jiyoung on 2015-10-01.
 */
public class MyXmlPullParser { //////파싱진행 함수+보관장소 역지오코딩 함수가 존재하는 클래스
    XmlPullParser parser;
    XmlPullParserFactory factory;
    //운영계정 인증키
    String key = "csJWEPxvxx9ERkQ6Ci2sVOH3A2LlUNTLMKgtF%2BWmQcH%2FSHhyiXjhhUk2fUbxidV56R2wEbWGPEB%2B4efWZmmCYA%3D%3D";
    //서윤 운영계정
    //String key="EmPUmJGpa9r%2B1f8mzDGIXQc6YESmJf4QRE709OOirpK740jr3KblZCSL5na45My8e9a%2FhtQZ3o6xGv6wKLxw9w%3D%3D";
    //String key="GDHyXKemYhB%2FDs5aQr33hXkcbm%2FcJ8MVSKtZeIIABe2xlrHjUuKFp55yu5RMPgxtU1hjT%2BuWMam45l50z3oZQA%3D%3D";
    Lost found;
    String str_color;
    //sqlite 데이터베이스
    String sql;
    Cursor cursor_atc;

    //지영_추가함
    String data;

    InputStream is;

    //알림을하기위한관리id
    String str_atcId;
    String str_fdSn;
    public void searchLocation(SQLiteDatabase sqLiteDatabase, String searchatc, String searchStr, Geocoder gc) throws IOException { ////여기서 insert 시키지말고 return값으로 보관장소를 변환한것을 보내 insert를 같이하도록 한다.

        List<Address> addressList = null;
        addressList = gc.getFromLocationName(searchStr, 3);
        Log.d("pjy_searchLocation", "보관장소파싱후 함수 호출됨" + addressList);

        try {
            if (addressList != null) {
                for (int i = 0; i < addressList.size(); i++) {
                    Address a = addressList.get(i);
                    String address = a.getAddressLine(0);                   //보관장소의 full주소를 address에 저장
                    Log.d("pjy000", "경찰서의 풀주소는 " + address);
                    sql = "update LOST set depPlaceFull= '" + address + "' where atcId = '" + searchatc + "'"; //보관장소 검색을위해 full주소를 update시킴
                    Log.d("pjy_gc", sql);
                    sqLiteDatabase.execSQL(sql);  //update 쿼리문 실행
                    // Log.d("pjy111", "DB에 존재하면 1 아니면 0 : " + cursor1.getCount());
                    Cursor cursor_gc = sqLiteDatabase.rawQuery(sql, null);           //지오코딩 커서 검색
                    Log.d("pjy_gc", "넣어진것의 1번째 값은" + cursor_gc.getString(1)); //잘들어갔나 첫컬럼값을 확인
                }
            }
        } catch (Exception e) {
        }
    }

    public void xmlParser(SQLiteDatabase sqLiteDatabase, Geocoder gc) {

        Log.d("pjy_parsing", "파싱함수 들어옴옴");
        try {
            URL url = new URL("http://openapi.lost112.go.kr/openapi/service/rest/LosfundInfoInqireService/getLosfundInfoAccToClAreaPd?numOfRows=100&pageNo=1" +
                    "&serviceKey=" + key);  //파싱할 url
            Log.d("pjy", "파싱클래스  " + url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); //url 연결
            urlConnection.setConnectTimeout(200000);
            urlConnection.setReadTimeout(20000000);
            InputStream is = urlConnection.getInputStream();      //url 연결 url입력값

            if (is == null) {
                //Toast.makeText(getApplicationContext(), "is null", Toast.LENGTH_LONG).show();
                Log.d("pjy","url 읽어올수없음");
            }
            factory = XmlPullParserFactory.newInstance();     //xmlpullparser 생성을위한 factory생성
            parser = factory.newPullParser();                   //
            parser.setInput(new InputStreamReader(is, "utf-8")); //파싱할 url  setting

            int eventType = parser.getEventType();

            Log.d("pjy", "event type = " + eventType);
            if (eventType == XmlPullParser.END_DOCUMENT) {     //문서가 다끝났으면
                Log.d("pjy", "End Document");
            }
            while (eventType != XmlPullParser.END_DOCUMENT) {  //다끝난게 아니면
                Log.d("pjy", "while");
                switch (eventType) {
                    case XmlPullParser.START_TAG:             //시작태그
                        String startTag = parser.getName();
                        Log.d("pjy", startTag);
                        if ("item".equals(startTag))         //<item> 일 때
                            found = new Lost();
                        if ("atcId".equals(startTag)) {          //<atcId> 일때
                            str_atcId = parser.nextText();
                            found.setAtcId(str_atcId);
                        }
                        if ("fdYmd".equals(startTag))
                            found.setFdYmd(parser.nextText());
                        if ("clrNm".equals(startTag)) {
                            str_color = parser.nextText();
                            found.setClrNm(str_color);
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
                            sql = "select * from LOST where atcId = '" + str_atcId + "' and fdSn = '" + str_fdSn +"'";
                            cursor_atc = sqLiteDatabase.rawQuery(sql, null);
                            Log.d("pjy111", "DB에 존재하면 1 아니면 0 : " + cursor_atc.getCount()); //커서검색시작
                            if (cursor_atc.getCount() != 0) {     //관리id값이 기존에 있을경우 while문 continue로 나옴
                                continue;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:  //</> 종료태그 시에
                        String endTag = parser.getName();  //태그명 읽어옴
                        if ("atcId".equals(endTag)) {     //</atcId>  라면
                            parser.next();             //다음 줄 읽도록
                        }
                        if ("item".equals(endTag)) {  //</item> 라면
                            sql = "insert into LOST(atcId,fdPrdtNm,depPlace,clrNm,fdYmd,prdtClNm,fdFilePathImg,fdSn) values " +
                                    "('" + found.getAtcId() + "','"
                                    + found.getFdPrdtNm() + "','"
                                    + found.getDepPlace() + "','"
                                    + found.getClrNm() + "','"
                                    + found.getFdYmd() + "','"
                                    + found.getPrdtClNm() + "','"
                                    + found.getFdFilePathImg() + "','"
                                     + found.getFdSn() + "')";                //읽은 item의 관리id,대분류,보관장소,색상,습득일,물건명,이미지url 들을 테이블에 추가.
                            Log.d("pjy_parsing", "입력된데이터의 관리ID는" + found.getAtcId());
                            Log.d("pjy_parsing", "입력된데이터의 습득순번은" + found.getFdSn());
                            Log.d("pjy_parsing", sql);
                            sqLiteDatabase.execSQL(sql);
                            searchLocation(sqLiteDatabase, found.getAtcId(), found.getDepPlace(), gc); //////보관장소의 full주소를 DB에 저장하기위해 관리아이디,보관장소, geo코더를 매개변수로 넘겨준다.
                        }
                }
                eventType = parser.next(); //다음단계 진행
            }
        } catch (Exception e) { //예외처리 구문
            e.printStackTrace();
            Log.d("pjy", "error occured!!!");
        }
    }
}
