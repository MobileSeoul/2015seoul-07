package com.example.hywoman.LostMistake;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Created by hywoman on 2015-06-11.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    XmlPullParser parser;
    XmlPullParserFactory factory;
    String sql;

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
        super(context, name, factory, version);

    }
    //데이터베이스 Create
    @Override
    public void onCreate(SQLiteDatabase db) {
       createNewTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        //데이터베이스 열기
        super.onOpen(db);

    }
    //습득물과 핸드폰목록을 저장하기위한 두개의 테이블 생성
    private void createNewTable(SQLiteDatabase db) {
        sql = "CREATE TABLE IF NOT EXISTS LOST(_id integer primary key, atcId TEXT," +
                " fdPrdtNm TEXT, depPlace TEXT, depPlaceFull TEXT, clrNm TEXT, fdYmd DATE, prdtClNm TEXT, fdFilePathImg TEXT, fdSn TEXT)";
        Log.d("pjy9", "실행할쿼리는 " + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS PHONE(_id integer primary key, atcId TEXT," +
                " fdPrdtNm TEXT, depPlace TEXT, depPlaceFull TEXT, clrNm TEXT, fdYmd DATE, prdtClNm TEXT, fdFilePathImg TEXT, fdSn TEXT)";
        //커서어댑터가 내부적으로 _id를 사용해야해서 _id 써줘야함
        Log.d("pjy9", "실행할쿼리는 " + sql);
        db.execSQL(sql);
    }
}


