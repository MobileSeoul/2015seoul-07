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
 * Created by jiyoung on 2015-05-08.
 */
class MyAdapterPhone extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Lost> lost;
    int layoutID;
    public MyAdapterPhone(Context ctx, int layoutID, ArrayList<Lost> lost){
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
        TextView prdtClNm = (TextView) convertView.findViewById(R.id.prdtClNm);
        prdtClNm.setText(lost.get(position).getPrdtClNm());
        TextView fdYmd = (TextView) convertView.findViewById(R.id.fdYmd);
        fdYmd.setText(lost.get(position).getFdYmd());
        ImageView img0 = (ImageView)convertView.findViewById(R.id.img);

        //if(lost.get(position).getPrdtClNm().contains("삼성")){
        //    img0.setImageResource(R.drawable.samsung);
        //}
         if(lost.get(position).getPrdtClNm().contains("LG")){
            img0.setImageResource(R.drawable.lg);
        }
        else if(lost.get(position).getPrdtClNm().contains("삼성")){
            img0.setImageResource(R.drawable.samsung);
        }
        else if(lost.get(position).getPrdtClNm().contains("아이폰")){
            img0.setImageResource(R.drawable.apple);
        }
        else
            img0.setImageResource(R.drawable.folder);
        return convertView;
    }
}