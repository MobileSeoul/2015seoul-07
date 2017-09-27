package com.example.hywoman.LostMistake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import static android.view.View.VISIBLE;


public class menu_lost_version extends Activity {

    Animation translateleft;
    Animation translateright;
    LinearLayout slidinglayout;

    boolean openorclose = true;
    /*소현추가*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lost_version);

        getActionBar().setIcon(R.drawable.logo000);

        findViewById(R.id.menutext1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.menutext2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Phone.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.menutext3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.menutext4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Bell.class);
                startActivity(intent);
                finish();
            }
        });

        slidinglayout = (LinearLayout) findViewById(R.id.slidinglayout);
        translateleft = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateright = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        translateleft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openorclose = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        translateright.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openorclose = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_lost_version, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu01:
                if (openorclose == true) {
                    slidinglayout.setVisibility(VISIBLE);
                    slidinglayout.startAnimation(translateleft);
                } else {
                    slidinglayout.startAnimation(translateright);
                    slidinglayout.setVisibility(View.INVISIBLE);
                }
                break;
        }
        switch (item.getItemId()) {
            case R.id.menu1:
                Intent a = new Intent(this, menu_lost_version.class);
                startActivity(a);
                break;
            case R.id.menu2:
                Intent b = new Intent(this, menu_lost_help.class);
                startActivity(b);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
