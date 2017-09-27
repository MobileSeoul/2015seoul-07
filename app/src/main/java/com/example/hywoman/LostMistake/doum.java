package com.example.hywoman.LostMistake;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class doum extends Activity {
    ViewPager pager;
    ImageView doumfinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doum);

        pager= (ViewPager)findViewById(R.id.pager);

        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                doum.this.finish();
            }
        });
        //ViewPager에 설정할 Adapter 객체 생성
        //ListView에서 사용하는 Adapter와 같은 역할.
        //다만. ViewPager로 스크롤 될 수 있도록 되어 있다는 것이 다름
        //PagerAdapter를 상속받은 CustomAdapter 객체 생성
        //CustomAdapter에게 LayoutInflater 객체 전달
        CustomAdapter adapter= new CustomAdapter(getLayoutInflater());
       // LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LinearLayout linear = (LinearLayout)inflater.inflate(R.layout.viewpager_childview, null);
       /* doumfinish = (ImageView)findViewById(R.id.doumfinish);
        doumfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doum.this.finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });*/
        //ViewPager에 Adapter 설정
        pager.setAdapter(adapter);

       /* doumfinish = (ImageView)findViewById(R.id.doumfinish);
        doumfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doum.this.finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
*/

    }


}
