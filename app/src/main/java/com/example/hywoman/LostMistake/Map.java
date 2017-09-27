package com.example.hywoman.LostMistake;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hywoman.LostMistake.navermap.NMapPOIflagType;
import com.example.hywoman.LostMistake.navermap.NMapViewerResourceProvider;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.List;


public class Map extends NMapActivity implements NMapView.OnMapStateChangeListener, NMapView.OnMapViewTouchEventListener {
    Animation translateleft;
    Animation translateright;
    LinearLayout slidinglayout;
    RelativeLayout rlayout;
    boolean openorclose = true;


    Lost found;


    public static final String API_KEY = "82cfb28f54c34965344d24a83c08f446";
    private NMapView mMapView;
    private NMapController mMapController;

    //////////지도오버레이코딩
    NMapOverlayManager mOverlayManager;
    NMapViewerResourceProvider mMapViewerResourceProvider;

    XmlPullParser parser;
    XmlPullParserFactory factory;
    InputStream is;

    //경찰청 위치 위도,경도
    Geocoder gc;
    Double lat;
    Double lon;
    String searchStr;
    GpsInfo gps;

    double latitude;  //위도
    double longitude;  //경도


    /////////////////
    Intent intent_road;
    String str;
    String str1;
    String str2;
    String str3;
    String str4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        LinearLayout linear = (LinearLayout) findViewById(R.id.map);
        getActionBar().setIcon(R.drawable.logo000);
        Intent intent = getIntent();

        lat = intent.getDoubleExtra("mydata", 0.0);
        lon = intent.getDoubleExtra("mydata0", 0.0);


        mMapView = new NMapView(this);
        //지도에서 ZoomControll을ZoomControll을 보여준다


        //네이버 OPEN API 사이트에서 할당받은 KEY를 입력하여 준다.
        mMapView.setApiKey(API_KEY);


        //이 페이지의 레이아웃을 네이버 MapView로 설정해준다.
        // setContentView(mMapView);
        //ImageView linear = (ImageView)findViewById(R.id.map);

        mMapView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
          linear.addView(mMapView);

        //네이버 지도의 클릭이벤트를 처리 하도록한다.
        mMapView.setClickable(true);
        mMapView.setBuiltInZoomControls(true, null);
        ///////////////////////////////////////////////////////////

        //길찾기
        // txtLat =(TextView)findViewById(R.id.txtlat);
        // txtLon=(TextView)findViewById(R.id.txtlon);

        gps = new GpsInfo(Map.this);
        if (gps.isGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            //double latitude = intent.getDoubleExtra("mydata",0.0);
            //double longitude = intent.getDoubleExtra("mydata0",0.0);


            String lat_ = String.valueOf(latitude);
            String lon_ = String.valueOf(longitude);

            Log.d("csy", "gps 위도는" + lat_ + ", 경도는 " + lon_ + "입니다.");

            // txtLon.setText(String.valueOf(lon));
            Toast.makeText(getApplicationContext(), "당신의 위치 " + latitude + " 경도 " + longitude, Toast.LENGTH_LONG).show(); //Toast 창 띄움

        } else {
            //gps.showSettingsAlert();
            Toast.makeText(getApplicationContext(), "GPS를 켜주세요.", Toast.LENGTH_LONG).show(); //Toast
        }

        //URL 이동
        //final String str = "http://map.naver.com/index.nhn?slat="+latitude+"&slng="+longitude+"&stext=출발지&elat=37.4963836&elng=127.123509&etext=도착지&menu=route&pathType=1\n";

        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        // create overlay manager
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        // 오버레이 리소스 관리객체 할당
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        // 오버레이 관리자 추가
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        // 오버레이들을 관리하기 위한 id값 생성
        int markerId = NMapPOIflagType.PIN;

        // 표시할 위치 데이터를 지정한다. -- 마지막 인자가 오버레이를 인식하기 위한 id값
        NMapPOIdata poiData = new NMapPOIdata(0, mMapViewerResourceProvider);//1 >0
        poiData.beginPOIdata(0);//초기값 : 2


        // poiData.addPOIitem(127.061, 37.51, "위치2", markerId, 0); //< 초기값. 지우지 말것 !!
        //poiData.endPOIdata();  //=>여기고침

        /* -> 여기 고침
        // 위치 데이터를 사용하여 오버레이 생성
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        // id값이 0으로 지정된 모든 오버레이가 표시되고 있는 위치로 지도의 중심과 ZOOM을 재설정
        poiDataOverlay.showAllPOIdata(11); //0
*/
        ////////////////////////////////////////////////////

        // set POI data
        //NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        //poiData.beginPOIdata(2);
        final NMapPOIitem item = poiData.addPOIitem(longitude, latitude,   //이곳에 무엇을 넣어야 할 것인가~~~~~~????
                "길찾기", NMapPOIflagType.FROM, 0);

        poiData.addPOIitem((double) lon, (double) lat,
                "도착", NMapPOIflagType.TO, 1);        //경찰청
        poiData.endPOIdata();


        // create POI data overlay
        mOverlayManager.createPOIdataOverlay(poiData, null);


        // 위치 데이터를 사용하여 오버레이 생성
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        // id값이 0으로 지정된 모든 오버레이가 표시되고 있는 위치로 지도의 중심과 ZOOM을 재설정
        poiDataOverlay.showAllPOIdata(11); //0

        ImageView rrr = (ImageView) findViewById(R.id.load);
        rrr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GpsInfo(Map.this);
                if (gps.isGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.d("pjh777", latitude + "," + longitude);
                    String str1 = String.valueOf(latitude);
                    String str2 = String.valueOf(longitude);
                    searchLocation(searchStr);
                    String str3 = String.valueOf(lat);
                    String str4 = String.valueOf(lon);
                    Log.d("pjh777", str1 + "," + str2);
                    Log.d("pjh777", str3 + "," + str4);

                    str = "http://map.naver.com/index.nhn?slat=" + str1 + "&slng=" + str2 + "&stext=출발지&elat=" + str3 + "&elng=" + str4 + "&etext=도착지&menu=route&pathType=1";
                    Log.d("pjh777", str);

                    intent_road = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                    startActivity(intent_road);

                } else {
                    // gps.showSettingsAlert();
                }
            }
        });


        poiDataOverlay.setOnFloatingItemChangeListener(new NMapPOIdataOverlay.OnFloatingItemChangeListener() {
            @Override
            public void onPointChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
                NGeoPoint point = item.getPoint();
            }
        });


        mMapController = mMapView.getMapController();
        //맵의 상태가 변할때 이메소드를 탄다.

        mMapView.setOnMapStateChangeListener(new NMapView.OnMapStateChangeListener() {

            @Override
            public void onZoomLevelChange(NMapView arg0, int arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMapInitHandler(NMapView arg0, NMapError arg1) {
                // TODO Auto-generated method stub
                //오류없이 네이버 지도가 초기화 되었다면 특정 좌표의 특정 Zoom으로 위치를 표시한다.
                if (arg1 == null) {
                    mMapController.setMapCenter(new NGeoPoint(lon, lat), 5);
                } else {
                    Toast.makeText(getApplicationContext(), arg1.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMapCenterChangeFine(NMapView arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub

            }
        });

        translateleft = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateright = AnimationUtils.loadAnimation(this, R.anim.translate_right);


        slidinglayout = (LinearLayout) findViewById(R.id.slidinglayout);

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

    private void searchLocation(String searchStr) {
        List<Address> addressList = null;
        try {
            addressList = gc.getFromLocationName(searchStr, 3);
            if (addressList != null) {
                for (int i = 0; i < addressList.size(); i++) {
                    Address a = addressList.get(0);//u를 0으로 바꿈임ㅁ의로

                    lat = a.getLatitude();
                    lon = a.getLongitude();
                }
            }
        } catch (Exception e) {
        }
    }

    public void onMapInitHandler1(NMapView mapView, NMapError errorInfo) {
        if (errorInfo == null) { //success
            //mMapController.setMapCenter(newNGeoPoint(126.978371,37.5666091),11);//시청
            mMapController.setMapCenter(newNGeoPoint(137.552484, 49.7598453), 21);
        } else {//fail
            android.util.Log.e("NMAP", "onMapInitHandler1: error=" + errorInfo.toString());
        }
    }

    private NGeoPoint newNGeoPoint(double d, double e) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onLongPress(NMapView arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLongPressCanceled(NMapView arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(NMapView arg0, MotionEvent arg1, MotionEvent arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSingleTapUp(NMapView arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTouchDown(NMapView arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTouchUp(NMapView arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapCenterChangeFine(NMapView arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapInitHandler(NMapView arg0, NMapError arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onZoomLevelChange(NMapView arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu01:
                gps = new GpsInfo(Map.this);
                if (gps.isGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.d("pjh777", latitude + "," + longitude);
                    String str1 = String.valueOf(latitude);
                    String str2 = String.valueOf(longitude);
                    searchLocation(searchStr);
                    String str3 = String.valueOf(lat);
                    String str4 = String.valueOf(lon);
                    Log.d("pjh777", str1 + "," + str2);
                    Log.d("pjh777", str3 + "," + str4);

                    str = "http://map.naver.com/index.nhn?slat=" + str1 + "&slng=" + str2 + "&stext=출발지&elat=" + str3 + "&elng=" + str4 + "&etext=도착지&menu=route&pathType=1";
                    Log.d("pjh777", str);

                    intent_road = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                    startActivity(intent_road);

                } else {
                    // gps.showSettingsAlert();
                }
                str = "http://map.naver.com/index.nhn?slat=" + str1 + "&slng=" + str2 + "&stext=출발지&elat=" + str3 + "&elng=" + str4 + "&etext=도착지&menu=route&pathType=1";
                startActivity(intent_road);
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
