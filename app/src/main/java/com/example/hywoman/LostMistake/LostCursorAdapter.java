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
 * Created by hywoman on 2015-06-12.
 */
public class LostCursorAdapter extends CursorAdapter {
    LostCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.griditemview, parent, false);
        return view;
    }
    //어댑터로 클릭된 관리ID 값 반환하는 메소드
    public String getAtcIdGrid(Cursor cursor, int position) {
        String data;
        data = cursor.getString(cursor.getColumnIndex("atcId"));
        return data;
    }
    //어댑터로 클릭된 습득순번 값 반환하는 메소드
    public String getFdSnGrid(Cursor cursor, int position) {
        String fdSndata;
        fdSndata = cursor.getString(cursor.getColumnIndex("fdSn"));
        return fdSndata;
    }
    //어댑터로 클릭된 색상 값 반환하는 메소드
    public String getClrNmGrid(Cursor cursor, int position) {
        String str_color;
        str_color = cursor.getString(cursor.getColumnIndex("clrNm"));
        return str_color;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView fdPrdtNm = (TextView) view.findViewById(R.id.fdPrdtNm); //물건명 textview에 설정
        final TextView fdYmd = (TextView) view.findViewById(R.id.fdYmd);       //습득일 textview에 설정
        final RelativeLayout rlayout = (RelativeLayout) view.findViewById(R.id.rlayout);
        final TextView depPlace = (TextView) view.findViewById(R.id.depPlace);//보관장소 textview에 설정
        // 이미지
        fdPrdtNm.setText(cursor.getString(cursor.getColumnIndex("fdPrdtNm")));
        fdYmd.setText(cursor.getString(cursor.getColumnIndex("fdYmd")) + "(습득일)");
        depPlace.setText(cursor.getString(cursor.getColumnIndex("depPlace")));

        if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("지갑")) {// 검색한 분류명중에 "지갑"이 포함되면
            rlayout.setBackgroundResource(R.drawable.wallet1);                      //wallet1으로 아이템 배경 이미지 설정
        } else if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("카드")) {
            rlayout.setBackgroundResource(R.drawable.card1);
        } else if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("증명서")) {
            rlayout.setBackgroundResource(R.drawable.certification1);
        } else if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("휴대폰")) {
            rlayout.setBackgroundResource(R.drawable.phone1);
        } else if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("현금")) {
            rlayout.setBackgroundResource(R.drawable.money1);
        } else if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("옷")) {
            rlayout.setBackgroundResource(R.drawable.clothes1);
        } else if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("가방")) {
            rlayout.setBackgroundResource(R.drawable.bag1);
        } else if (cursor.getString(cursor.getColumnIndex("prdtClNm")).contains("귀금속")) {
            rlayout.setBackgroundResource(R.drawable.accessary1);
        } else
            rlayout.setBackgroundResource(R.drawable.folder);
    }
}
