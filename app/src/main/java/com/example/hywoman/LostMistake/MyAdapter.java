package com.example.hywoman.LostMistake;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jiyoung on 2015-05-15.
 */
class MyAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Lost> lost;
    int layoutID;

    public MyAdapter(Context ctx,int layoutID, ArrayList<Lost> lost){
        this.ctx = ctx;
        this.layoutID =layoutID;
        this.lost = lost;
        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        Log.d("dhkim", lost.size() + "");
        return lost.size();
    }

    @Override
    public Object getItem(int position) {
        return lost.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layoutID, parent, false);
        }

        TextView fdPrdtNm = (TextView) convertView.findViewById(R.id.fdPrdtNm);
        fdPrdtNm.setText(lost.get(position).getFdPrdtNm());
        TextView fdYmd = (TextView) convertView.findViewById(R.id.fdYmd);
        fdYmd.setText(lost.get(position).getFdYmd());
        TextView depPlace = (TextView) convertView.findViewById(R.id.depPlace);
        depPlace.setText(lost.get(position).getDepPlace());

        ImageView img0 = (ImageView)convertView.findViewById(R.id.img);
        /*if(lost.get(position).getPrdtClNm().contains("지갑")){
            img0.setImageResource(R.drawable.wallet00);
        }
       if(lost.get(position).getPrdtClNm().contains("현금")){
            img0.setImageResource(R.drawable.money1);
        }
        if(lost.get(position).getPrdtClNm().contains("가방")){
            img0.setImageResource(R.drawable.bag1);
        }
        if(lost.get(position).getPrdtClNm().contains("휴대폰")){
            img0.setImageResource(R.drawable.phone1);
        }
       if(lost.get(position).getPrdtClNm().contains("카드")){
           img0.setImageResource(R.drawable.card1);
        }

        if(lost.get(position).getPrdtClNm().contains("기타")){
            img0.setImageResource(R.drawable.folder);
        }*/
        //else
          //  img0.setImageResource(R.drawable.folder);

        /*ImageView img00 = (ImageView)convertView.findViewById(R.id.img2);
        if(lost.get(position).getPrdtClNm().contains("지갑")){
            img00.setImageResource(R.drawable.wallet000);
        }

        if(lost.get(position).getPrdtClNm().contains("휴대폰")){
            img00.setImageResource(R.drawable.phone000);
        }
        if(lost.get(position).getPrdtClNm().contains("카드")){
            img00.setImageResource(R.drawable.card000);
        }*/

        return convertView;
    }



}
