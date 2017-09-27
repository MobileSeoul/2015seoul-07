package com.example.hywoman.LostMistake;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static com.example.hywoman.LostMistake.R.anim;
import static com.example.hywoman.LostMistake.R.array;
import static com.example.hywoman.LostMistake.R.id;
import static com.example.hywoman.LostMistake.R.layout.activity_search;


public class Search extends Activity {
    Animation translateleft;
    Animation translateright;
    Animation translateup;
    Animation translatedown;
    boolean upordown = false;

    LinearLayout slidinglayout;
    LinearLayout rlayout;
    boolean openorclose = true;
    ArrayAdapter<CharSequence> spin1;
    ArrayAdapter<CharSequence> spin2;
    ArrayAdapter<CharSequence> spin3;
    ArrayAdapter<CharSequence> spin4;
    ListView grid;      //그리드뷰 -> 리스트뷰로 디자인 변경해서 변수이름 grid
    //날짜
    private TextView mDateDisplay;
    private TextView mDateDisplay1;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mYear1;
    private int mMonth1;
    private int mDay1;
    static final int DATE_DAILOG_ID = 0;
    static final int DATE_DAILOG_ID1 = 1;

    //sqlite 데이터베이스
    SQLiteDatabase sqLiteDatabase;      //데이터베이스
    String sql;                         //쿼리문
    MySQLiteOpenHelper mySQLiteOpenHelper;//openhelper

    ////////검색키워드 저장변수
    String large;       //대분류
    String small;       //소분류
    String color;       //색상검색 변수
    String place;       //장소검색 변수

    //////////
    LostCursorAdapter cursorAdpater;   //검색하려는게 습득물(돈,지갑 등)이라면  세부 아이콘 설정하기위한 어댑터 setting
    Cursor cursor;
    MyCursorAdapter cursorAdapterPhone;//검색하려는게 핸드폰이라면 핸드폰 세부 아이콘 설정하기위한 어댑터 setting

    String data;            //관리id 변수
    String str_color;       //상세 dialog에 색상을 보여주기위해 변수선언


    Intent intent;
    //지도사용

    ///////검색 쿼리날짜지정
    String mDate;       //날짜1
    String str_mMonth;  //월1
    String mDate1;      //날짜2
    String str_mMonth1; //월2


    String searchTable; //검색하려는 테이블 저장한 변수
    String fdSndata;    //습득순번 저장한 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_search);
        getActionBar().setIcon(R.drawable.logo000); //액션바 로고 설정
        //메뉴이동 인텐트 코딩
        findViewById(id.menutext1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(id.menutext2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Phone.class);
                startActivity(intent);
            }
        });

        findViewById(id.menutext4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Bell.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.menutext5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), menu_lost_version.class);
                startActivity(intent);
                finish();
            }
        });
        //기간 설정할 DateDisplay 생성
        mDateDisplay = (TextView) findViewById(id.first);
        mDateDisplay1 = (TextView) findViewById(id.second);
        //클릭 리스너
        mDateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DAILOG_ID);

            }
        });
        mDateDisplay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DAILOG_ID1);
            }
        });
        ////////////////////////////////////////////////////////////////달력코딩////////////////////////////////////////////////////////////
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        updateDisplay();  //설정한 날짜로 변경시킴

        final Calendar c1 = Calendar.getInstance();
        mYear1 = c1.get(Calendar.YEAR);
        mMonth1 = c1.get(Calendar.MONTH);
        mDay1 = c1.get(Calendar.DAY_OF_MONTH);

        updateDisplay1();

        final RelativeLayout rela = (RelativeLayout) findViewById(R.id.rela);  //검색설정부분 레이아웃
        final Button finishbtn = (Button) findViewById(id.finishbtn);   //접기|열기 버튼
        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //접기|열기버튼이 클릭되었을때
                if (upordown == true) {
                    rela.startAnimation(translatedown);  //내려오면서
                    rela.setVisibility(View.VISIBLE);   //레이아웃 보이기
                    finishbtn.setBackgroundResource(R.drawable.ttop);   //접어올리도록 위로된화살표 이미지 설정
                } else {
                    //올라가면서
                    rela.setVisibility(View.GONE);  //레이아웃 숨기기
                    rela.startAnimation(translateup);
                    finishbtn.setBackgroundResource(R.drawable.ddown);  //내려오도록 아애방향 화살표 이미지 설정
                }

            }
        });
        ////////////////////////////////////////////////////////////////DB코딩////////////////////////////////////////////////////////////
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "lm53.db", null, 1000);   //DB열기
        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();                  //검색가능하도록 getWritableDatabase() 함수 호출


        ///////////////////////////////////////////////////////애니메이션 코딩//////////////////////////////////////////////////
        translateleft = AnimationUtils.loadAnimation(this, anim.translate_left);
        translateright = AnimationUtils.loadAnimation(this, anim.translate_right);
        translateup = AnimationUtils.loadAnimation(this, anim.translate_up);
        translatedown = AnimationUtils.loadAnimation(this, anim.translate_down);
        slidinglayout = (LinearLayout) findViewById(id.slidinglayout);
        translateup.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                upordown = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        translatedown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                upordown = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
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

        //전체 뷰를 눌러도 메뉴 없어지게 하는것
        findViewById(id.rlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openorclose == false) {
                    slidinglayout.startAnimation(translateright);
                    slidinglayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        //상세검색 누를시 검색화면 등장
        rlayout = (LinearLayout) findViewById(R.id.rlayout);
        grid = (ListView) findViewById(R.id.grid);

        final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);  //스피너1을 찾아옴
        spin1 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinnerPrdtClNm, android.R.layout.simple_spinner_item);//대분류 분실물 배열을 보여주는 adapter 생성
        spin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  //아이템 디자인 설정
        spinner1.setAdapter(spin1);  //스피너1에 어댑터 연결
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        //대분류 스피너 설정
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);// 선택된 스피너의 색상이 하얀색
                //Cursor cursor = (Cursor)MyCursorAdapter.getItem(position);
                switch (position) {       //선택한 position값이
                    case 0:
                        break;
                    case 1:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmBag, android.R.layout.simple_spinner_item); // 배열중 spinnerFdPrdtNmBag 라는 배열을 연결시켜 어댑터를 생성한다.
                        spinner2.setAdapter(spin2); //생성한 adapter를 스피너에 연결한다
                        large = "가방 > ";        //대분류설정
                        break;
                    case 2:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmWallet, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "지갑 > ";
                        break;
                    case 3:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmCard, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "카드 > ";
                        break;
                    case 4:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmPhone, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "휴대폰 > ";
                        break;
                    case 5:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmNoteBook, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "노트북 > ";
                        break;
                    case 6:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmElectro, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "전자기기 > ";
                        break;
                    case 7:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmPaper, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "서류 > ";
                    case 8:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmAcc, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "귀금속 > ";
                        break;
                    case 9:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmShop, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "쇼핑백 > ";
                        break;
                    case 10:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmClothes, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "의류 > ";
                        break;
                    case 11:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmSport, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "스포층용품 > ";
                        break;
                    case 12:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmIdenty, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "증명서 > ";
                        break;
                    case 13:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmCar, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "자동차 > ";
                        break;
                    case 14:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmBook, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "도서 > ";
                        break;
                    case 15:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmCash, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "현금 > ";
                        break;
                    case 16:
                        spin2 = ArrayAdapter.createFromResource(getApplicationContext(), array.spinnerFdPrdtNmEtc, android.R.layout.simple_spinner_item);
                        spinner2.setAdapter(spin2);
                        large = "기타물품 > ";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //소분류 스피너 설정
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                //Cursor cursor = (Cursor)MyCursorAdapter.getItem(position);
                switch (position) {
                    case 0: //첫번쨰 아이템인 선택이라는 게 선택되었을경우
                        break;
                    case 1: //두번째아이템이 선택되었을경우
                        if (spinner1.getSelectedItem().equals("가방"))
                            small = "여성용가방";
                        if (spinner1.getSelectedItem().equals("지갑"))
                            small = "여성용 지갑";
                        if (spinner1.getSelectedItem().equals("카드"))
                            small = "신용카드";
                        if (spinner1.getSelectedItem().equals("휴대폰"))
                            small = "삼성휴대폰";
                        if (spinner1.getSelectedItem().equals("노트북"))
                            small = "삼성노트북";
                        if (spinner1.getSelectedItem().equals("전자기기"))
                            small = "mp3";
                        if (spinner1.getSelectedItem().equals("서류"))
                            small = "서류";
                        if (spinner1.getSelectedItem().equals("악세사리"))
                            small = "반지";
                        if (spinner1.getSelectedItem().equals("쇼핑백"))
                            small = "쇼핑백";
                        if (spinner1.getSelectedItem().equals("옷"))
                            small = "여성의류";
                        if (spinner1.getSelectedItem().equals("스포츠"))
                            small = "자전거";
                        if (spinner1.getSelectedItem().equals("증명서"))
                            small = "신분증";
                        if (spinner1.getSelectedItem().equals("자동차"))
                            small = "자동차열쇠";
                        if (spinner1.getSelectedItem().equals("도서"))
                            small = "학습서적";
                        if (spinner1.getSelectedItem().equals("현금"))
                            small = "현금";
                        if (spinner1.getSelectedItem().equals("기타물품"))
                            small = "기타";

                        break;
                    case 2:  //세번째아이템이 선택되었을경우
                        if (spinner1.getSelectedItem().equals("가방"))
                            small = "남성용가방";
                        if (spinner1.getSelectedItem().equals("지갑"))
                            small = "남성용 지갑";
                        if (spinner1.getSelectedItem().equals("카드"))
                            small = "기타카드";
                        if (spinner1.getSelectedItem().equals("휴대폰"))
                            small = "LG휴대폰";
                        if (spinner1.getSelectedItem().equals("노트북"))
                            small = "LG노트북";
                        if (spinner1.getSelectedItem().equals("전자기기"))
                            small = "카메라";
                        if (spinner1.getSelectedItem().equals("서류"))
                            small = "기타";
                        if (spinner1.getSelectedItem().equals("악세사리"))
                            small = "목걸이";
                        if (spinner1.getSelectedItem().equals("옷"))
                            small = "남성의류";
                        if (spinner1.getSelectedItem().equals("증명서"))
                            small = "면허증";
                        if (spinner1.getSelectedItem().equals("자동차"))
                            small = "자동차번호판";
                        if (spinner1.getSelectedItem().equals("도서"))
                            small = "소설";
                        if (spinner1.getSelectedItem().equals("현금"))
                            small = "수표";
                        break;
                    case 3:
                        if (spinner1.getSelectedItem().equals("가방"))
                            small = "기타가방";
                        if (spinner1.getSelectedItem().equals("지갑"))
                            small = "기타 지갑";
                        if (spinner1.getSelectedItem().equals("휴대폰"))
                            small = "아이폰";
                        if (spinner1.getSelectedItem().equals("노트북"))
                            small = "삼보노트북";
                        if (spinner1.getSelectedItem().equals("전자기기"))
                            small = "기타";
                        if (spinner1.getSelectedItem().equals("악세사리"))
                            small = "시계";
                        if (spinner1.getSelectedItem().equals("옷"))
                            small = "기타의류";
                        if (spinner1.getSelectedItem().equals("증명서"))
                            small = "여권";
                        if (spinner1.getSelectedItem().equals("도서"))
                            small = "만화책";
                        break;
                    case 4:

                        if (spinner1.getSelectedItem().equals("휴대폰"))
                            small = "기타";
                        if (spinner1.getSelectedItem().equals("노트북"))
                            small = "기타";
                        if (spinner1.getSelectedItem().equals("전자기기"))
                            small = "기타";
                        if (spinner1.getSelectedItem().equals("악세사리"))
                            small = "귀걸이";
                        if (spinner1.getSelectedItem().equals("증명서"))
                            small = "기타";
                        if (spinner1.getSelectedItem().equals("도서"))
                            small = "기타서적";
                        break;
                    case 5:
                        if (spinner1.getSelectedItem().equals("악세사리"))
                            small = "귀걸이";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //장소별 검색 스피너 코딩
        final Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        spin3 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinnerArea, android.R.layout.simple_spinner_item);
        spin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(spin3);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        place = "서울";
                        break;
                    case 2:
                        place = "강원";
                        break;
                    case 3:
                        place = "경기";
                        break;
                    case 4:
                        place = "경상남도";
                        break;
                    case 5:
                        place = "경상북도";
                        break;
                    case 6:
                        place = "광주";
                        break;
                    case 7:
                        place = "대구";
                        break;
                    case 8:
                        place = "대전";
                        break;
                    case 9:
                        place = "부산";
                        break;
                    case 10:
                        place = "울산";
                        break;
                    case 11:
                        place = "인천";
                        break;
                    case 12:
                        place = "전라남도";
                        break;
                    case 13:
                        place = "전라북도";
                        break;
                    case 14:
                        place = "충청남도";
                        break;
                    case 15:
                        place = "충청북도";
                        break;
                    case 16:
                        place = "제주";
                        break;
                    case 17:
                        place = "세종";
                        break;
                    case 18:
                        place = "해외";
                        break;
                    case 19:
                        place = "기타";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ///색상별 검색 스피너 코딩
        final Spinner spinner4 = (Spinner) findViewById(R.id.spinner4);
        spin4 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinnerColor, android.R.layout.simple_spinner_item);
        spin4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(spin4);

        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                //Cursor cursor = (Cursor)MyCursorAdapter.getItem(position);
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        color = "검";
                        break;
                    case 2:
                        color = "흰";
                        break;
                    case 3:
                        color = "빨";
                        break;
                    case 4:
                        color = "노";
                        break;
                    case 5:
                        color = "초";
                        break;
                    case 6:
                        color = "파";
                        break;
                    case 7:
                        color = "분";
                        break;
                    case 8:
                        color = "갈";
                        break;
                    case 9:
                        color = "기타";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button searchbtn = (Button) findViewById(R.id.searchbtn);

        Log.d("pjy00", "클릭전"); //커서검색시작
        searchbtn.setOnClickListener(new View.OnClickListener() {
            ////////////////////////////////////////////////////////////지영 고침/////////////////////////////////////////////////////////////////
            @Override
            public void onClick(View view) {
                //////////////////////쿼리문 1월 1일 => 01월 01일로 설정해 검색이 가능하도록//////////////////////////
                if (mMonth < 9)
                    str_mMonth = "0" + (mMonth + 1);
                else
                    str_mMonth = String.valueOf((mMonth + 1));
                if (mDay < 10)
                    mDate = "0" + mDay;
                else
                    mDate = String.valueOf(mDay);
                if (mMonth1 < 9)
                    str_mMonth1 = "0" + (mMonth1 + 1);
                else
                    str_mMonth1 = String.valueOf((mMonth1 + 1));
                if (mDay1 < 10)
                    mDate1 = "0" + mDay1;
                else
                    mDate1 = String.valueOf(mDay1);

                //검색할 table 설정
                if (spinner1.getSelectedItemPosition() == 4)
                    searchTable = "PHONE";
                else
                    searchTable = "LOST";

                //스피너 설정 경우의수
                if (spinner1.getSelectedItemPosition() != 0 && spinner2.getSelectedItemPosition() == 0 && spinner3.getSelectedItemPosition() == 0 && spinner4.getSelectedItemPosition() == 0) { //분류명만선택
                    sql = "select * from " + searchTable + " where prdtClNm like '" + large + "%' and fdYmd between '" + mYear + "-" + str_mMonth + "-" + mDate + "' and '" + mYear1 + "-" + str_mMonth1 + "-" + mDate1 + "'";
                    Log.d("pjy00", sql);
                } else if (spinner1.getSelectedItemPosition() != 0 && spinner2.getSelectedItemPosition() == 0 && spinner3.getSelectedItemPosition() != 0 && spinner4.getSelectedItemPosition() == 0) {//분류명 장소 선택
                    sql = "select * from " + searchTable + " where prdtClNm like '" + large + "%' and depPlaceFull like '%" + place + "%' and fdYmd between '" + mYear + "-" + str_mMonth + "-" + mDate + "' and '" + mYear1 + "-" + str_mMonth1 + "-" + mDate1 + "'";
                    Log.d("pjy00", sql);

                } else if (spinner1.getSelectedItemPosition() != 0 && spinner2.getSelectedItemPosition() == 0 && spinner3.getSelectedItemPosition() == 0 && spinner4.getSelectedItemPosition() != 0) {//분류명  색상선택
                    sql = "select * from+" + searchTable + " where prdtClNm like '" + large + "%' and clrNm like '%" + color + "%' and fdYmd between '" + mYear + "-" + str_mMonth + "-" + mDate + "' and '" + mYear1 + "-" + str_mMonth1 + "-" + mDate1 + "'";
                    Log.d("pjy00", sql);
                } else if (spinner1.getSelectedItemPosition() != 0 && spinner2.getSelectedItemPosition() != 0 && spinner3.getSelectedItemPosition() == 0 && spinner4.getSelectedItemPosition() == 0) { //분류명 물건명만선택
                    sql = "select * from " + searchTable + " where prdtClNm like '" + large + small + "%' and fdYmd between '" + mYear + "-" + str_mMonth + "-" + mDate + "' and '" + mYear1 + "-" + str_mMonth1 + "-" + mDate1 + "'";
                    Log.d("pjy00", sql);
                } else if (spinner1.getSelectedItemPosition() != 0 && spinner2.getSelectedItemPosition() != 0 && spinner3.getSelectedItemPosition() == 0 && spinner4.getSelectedItemPosition() != 0) {//분류명 물건명 색상 선택
                    sql = "select * from" + searchTable + " where prdtClNm like '" + large + small + "%' and clrNm like '%" + color + "%' and fdYmd between '" + mYear + "-" + str_mMonth + "-" + mDate + "' and '" + mYear1 + "-" + str_mMonth1 + "-" + mDate1 + "'";
                    Log.d("pjy00", sql);
                } else if (spinner1.getSelectedItemPosition() != 0 && spinner2.getSelectedItemPosition() != 0 && spinner3.getSelectedItemPosition() != 0 && spinner4.getSelectedItemPosition() == 0) {//물건명 분류명  장소만 선택
                    sql = "select * from " + searchTable + " where prdtClNm like '" + large + small + "%' and depPlaceFull like '%" + place + "%' and  fdYmd between '" + mYear + "-" + str_mMonth + "-" + mDate + "' and '" + mYear1 + "-" + str_mMonth1 + "-" + mDate1 + "'";
                    Log.d("pjy00", sql);

                } else if (spinner1.getSelectedItemPosition() != 0 && spinner2.getSelectedItemPosition() != 0 && spinner3.getSelectedItemPosition() != 0 && spinner4.getSelectedItemPosition() != 0) {//물건명 분류명 장소 색상모두선택
                    sql = "select * from " + searchTable + " where prdtClNm like '" + large + small + "%' and depPlaceFull like '%" + place + "%' and clrNm like '%" + color + "%' and fdYmd between '" + mYear + "-" + str_mMonth + "-" + mDate + "' and '" + mYear1 + "-" + str_mMonth1 + "-" + mDate1 + "'";
                    Log.d("pjy00", sql);
                }

                cursor = sqLiteDatabase.rawQuery(sql, null);
                Log.d("pjy00", "검색된값은" + cursor.getCount()); //커서검색시작
                if (searchTable.equals("LOST")) {                   //검색 테이블이 LOST 라면
                    cursorAdpater = new LostCursorAdapter(getApplicationContext(), cursor, 1);
                    grid.setAdapter(cursorAdpater);                                             //LostCursorAdapter 연결
                } else if (searchTable.equals("PHONE")) {           //검색 테이블이 PHONE 이라면
                    cursorAdapterPhone = new MyCursorAdapter(getApplicationContext(), cursor, 1);
                    grid.setAdapter(cursorAdapterPhone);                                        //MyCursorAdapter 연결
                }

                //검색된값이 없다면 없다고 알려주기
                if (cursor.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "검색된 결과가 없습니다.", Toast.LENGTH_SHORT).show();    //토스트 띄우기
                }
            }
        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (grid.getAdapter() == cursorAdpater) {                   //LOST테이블검색시
                    data = cursorAdpater.getAtcIdGrid(cursor, position);
                    str_color = cursorAdpater.getClrNmGrid(cursor, position);
                    fdSndata=cursorAdpater.getFdSnGrid(cursor,position);        //습득순번데이터
                    intent = new Intent(getApplicationContext(), dialog.class);
                } else if (grid.getAdapter() == cursorAdapterPhone) {       //PHONE 테이블 검색시
                    data = cursorAdapterPhone.getAtcIdGrid(cursor, position);
                    str_color = cursorAdapterPhone.getClrNmGrid(cursor, position);
                    fdSndata=cursorAdapterPhone.getFdSnGrid(cursor,position);
                    intent = new Intent(getApplicationContext(), phonedialog.class);
                }
                intent.putExtra("data1", data);         //관리ID 데이터 인텐트객체에 넣기
                intent.putExtra("str_color", str_color);//색상 데이터 인텐트객체에 넣기
                intent.putExtra("fdSndata",fdSndata);   //습득순번 데이터 인텐트 객체에 넣기
                startActivity(intent);                  //인텐트 시작

                Log.d("pjh", "인텐트 넘어감");

            }

        });
    }
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DAILOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
            case DATE_DAILOG_ID1:
                return new DatePickerDialog(this, mDateSetListener1, mYear1, mMonth1, mDay1);
        }

        return null;
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfyear;
            mDay = dayOfMonth;
            updateDisplay();
        }
    };

    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfMonth) {
            mYear1 = year;
            mMonth1 = monthOfyear;
            mDay1 = dayOfMonth;
            updateDisplay();
        }
    };

    private void updateDisplay() {
        mDateDisplay.setText(new StringBuilder()
                .append(mYear).append("-")
                .append(mMonth + 1).append("-") /////(mMonth + 1)
                .append(mDay).append(" "));
    }

    private void updateDisplay1() {

        mDateDisplay1.setText(new StringBuilder()
                .append(mYear1).append("-")
                .append(mMonth1 + 1).append("-")
                .append(mDay1).append(" "));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case id.menu01:
                if (openorclose == true) {
                    slidinglayout.setVisibility(View.VISIBLE);
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