package com.example.hywoman.LostMistake;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by hywoman on 2015-06-11.
 */
public class MyCursorAdapter extends CursorAdapter {
    MyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    //어댑터로 클릭된 관리ID 값 반환하는 메소드
    public String getAtcIdGrid(Cursor cursor, int position) {
        String data;
        data = cursor.getString(cursor.getColumnIndex("atcId"));
        return data;
    }
    //어댑터로 클릭된 색상 값 반환하는 메소드
    public String getClrNmGrid(Cursor cursor, int position) {
        String str_color;
        str_color = cursor.getString(cursor.getColumnIndex("clrNm"));
        return str_color;
    }

    //어댑터로 클릭된 습득순번 값 반환하는 메소드
    public String getFdSnGrid(Cursor cursor, int position) {
        String fdSndata;
        fdSndata = cursor.getString(cursor.getColumnIndex("fdSn"));
        return fdSndata;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.griditemview, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView fdPrdtNm = (TextView) view.findViewById(R.id.fdPrdtNm);
        final TextView fdYmd = (TextView) view.findViewById(R.id.fdYmd);
        final RelativeLayout rlayout = (RelativeLayout) view.findViewById(R.id.rlayout);
        final TextView depPlace = (TextView) view.findViewById(R.id.depPlace);

        fdPrdtNm.setText(cursor.getString(cursor.getColumnIndex("fdPrdtNm")));  //물건명 textview에 설정
        fdYmd.setText(cursor.getString(cursor.getColumnIndex("fdYmd")) + "(습득일)");//습득일 textview 에 설정
        depPlace.setText(cursor.getString(cursor.getColumnIndex("depPlace")));      //보관장소 textview에 설정

        if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("삼성")) {   //검색한 분류명에 "삼성"이 포함되면//메모리문제 해결
            rlayout.setBackgroundResource(R.drawable.samsung);      //삼성이미지로 아이템 배경 이미지 설정
        } else if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("아이폰")) {
            rlayout.setBackgroundResource(R.drawable.apple);
        } else if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("LG")) {

            rlayout.setBackgroundResource(R.drawable.lg);
        } else {
            rlayout.setBackgroundResource(R.drawable.folder);
        }
    }

}


