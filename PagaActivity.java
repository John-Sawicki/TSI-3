package com.example.android.tsi;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.tsi.SqliteSum.SumDbHelper;
import com.example.android.tsi.SqliteSum.SumTaskContract;
import com.example.android.tsi.Widget.SummaryService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class PagaActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    FrameLayout mFrameBackground;
    protected ImageView ivCircle0, ivCircle1, ivCircle2, ivCircle3, ivCircle4, ivCircle5, ivCircle6, ivCircle7, ivCircle8, ivCircle9;
    protected TextView tv_db_level1, tv_distance1, tv_db_level2, tv_distance2, tv_db_level3, tv_distance3, tv_db_level4, tv_distance4, tv_db_level5, tv_distance5, tv_db_level6, tv_distance6, tv_distance_uom;
    protected Button btn_calculate;
    protected Spinner sp_source_voltage;
    protected EditText et_speaker_output;
    @BindView(R.id.adViewBanner) AdView adViewBanner;
    private boolean imperial = true;
    private SQLiteDatabase mDb;
    private String widgetDistance = "0ft",uomString="ft";
    private static String ACTIVITY ="Paga_Activity";
    int mPower=-1;//when -1 the value has not been entered by the used even after screen rotation
    double interferanceCo = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(ACTIVITY, "onCreate start");
        setContentView(R.layout.activity_paga);
        ButterKnife.bind(this);
        View viewCircle = findViewById(R.id.lo_top_right);
        View viewDb = findViewById(R.id.lo_bottom_left);
        MobileAds.initialize(this, "ca-app-pub-8686454969066832~6147856904");
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBanner.loadAd(adRequest);
        setUpPreferences();
        mFrameBackground = viewCircle.findViewById(R.id.im_background);
        ivCircle0 = viewCircle.findViewById(R.id.im_circle0);
        ivCircle1= viewCircle.findViewById(R.id.im_circle1);
        ivCircle2= viewCircle.findViewById(R.id.im_circle2);
        ivCircle3= viewCircle.findViewById(R.id.im_circle3);
        ivCircle4= viewCircle.findViewById(R.id.im_circle4);
        ivCircle5= viewCircle.findViewById(R.id.im_circle5);
        ivCircle6= viewCircle.findViewById(R.id.im_circle6);
        ivCircle7= viewCircle.findViewById(R.id.im_circle7);
        ivCircle8= viewCircle.findViewById(R.id.im_circle8);
        ivCircle9= viewCircle.findViewById(R.id.im_circle9);
        //sp_source_voltage = viewCircle.findViewById(R.id.sp_source_voltage);
        sp_source_voltage = findViewById(R.id.sp_source_voltage);//move to main activity to reduce number of items on the right side for the landscape view
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interferance,android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_source_voltage.setAdapter(adapter);
        sp_source_voltage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                interferanceCo = determineInterference(i); Log.d(ACTIVITY,"interferanceCo spinner"+interferanceCo);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        tv_db_level1 = viewDb.findViewById(R.id.tv_db_level1);
        tv_distance1 = viewDb.findViewById(R.id.tv_distance1);
        tv_db_level2 = viewDb.findViewById(R.id.tv_db_level2);
        tv_distance2 = viewDb.findViewById(R.id.tv_distance2);
        tv_db_level3 = viewDb.findViewById(R.id.tv_db_level3);
        tv_distance3 = viewDb.findViewById(R.id.tv_distance3);
        tv_db_level4 = viewDb.findViewById(R.id.tv_db_level4);
        tv_distance4 = viewDb.findViewById(R.id.tv_distance4);
        tv_db_level5 = viewDb.findViewById(R.id.tv_db_level5);
        tv_distance5 = viewDb.findViewById(R.id.tv_distance5);
        tv_db_level6 = viewDb.findViewById(R.id.tv_db_level6);
        tv_distance6 = viewDb.findViewById(R.id.tv_distance6);
        tv_distance_uom = viewDb.findViewById(R.id.tv_distance_uom);
        btn_calculate = viewCircle.findViewById(R.id.btn_calculate);
        et_speaker_output = viewCircle.findViewById(R.id.et_speaker_output);
        if(savedInstanceState!=null){//valued saved on rotate
            Log.d(ACTIVITY, "savedInstanceState!=null");
            mPower = savedInstanceState.getInt("SpeakerLevel");
            Log.d(ACTIVITY, "speaker power onCreate "+mPower);
            colorCircles(mPower); //if a value was saved on rotate, color the circles back in
        }
        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean editTextEmpty = et_speaker_output.getText().toString().trim().equals("");
                Log.d(ACTIVITY, "check for empty edit text "+editTextEmpty);
                if(!editTextEmpty){//a value was entered
                    mPower = Integer.parseInt(et_speaker_output.getText().toString());
                    Log.d(ACTIVITY, "speaker powered entered when button is pressed.");
                    colorCircles(mPower);
                }else{//the edit text still has null
                    Log.d(ACTIVITY, "no value entered when button is pressed.");
                }
            }
        });
    }
    private double determineInterference(int intIndex){
        switch(intIndex){
            case 0: return 2;
            case 1: return 2.7;
            case 2: return 3.5;
            case 3: return 4;
            case 4: return 5;
            case 5: return 6;
            case 6: return 7;
            default: return 2;
        }
    }
    private void setUpPreferences() {//sets up preferences when the user reopens the activity
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        imperial = sharedPref.getBoolean(getResources().getString(R.string.pref_units_key), true);
        Log.d(ACTIVITY, "imperial setup "+imperial);
    }
    @Override//updates the activity once the units system has changed
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getResources().getString(R.string.pref_units_key))){
            imperial = sharedPreferences.getBoolean(getResources().getString(R.string.pref_units_key), true);
            Log.d(ACTIVITY, imperial+" changed");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        if(itemClicked==R.id.shared_pref){
            Log.d(ACTIVITY, "menu clicked");
            startActivity(new Intent(PagaActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void colorCircles(int ccSpeakerPower){
       int speakerPower = ccSpeakerPower;
        if(speakerPower<=0){
            Log.d(ACTIVITY, "empty edit text");
        }else{
            Log.d(ACTIVITY, "value in edit text");
            if(speakerPower>0){//if a value was passed in from saved instance use it. EditText may be empty
                Log.d(ACTIVITY, "speakerPower>0");
                mPower = speakerPower;
            }else{//EditText isn't empty and no value has ever been entered for the speaker power
                Log.d(ACTIVITY, "!speakerPower>0 and mPower is "+mPower);
            }
            Log.d(ACTIVITY,"speaker power value used "+mPower);
            if(mPower>=200 ){
                ivCircle9.setColorFilter(getResources().getColor(R.color.circle_9_DarkRed));
                ivCircle8.setColorFilter(getResources().getColor(R.color.circle_8_Red));
                ivCircle7.setColorFilter(getResources().getColor(R.color.circle_75_DarkOrange));
                ivCircle6.setColorFilter(getResources().getColor(R.color.circle_7_Orange));
                ivCircle5.setColorFilter(getResources().getColor(R.color.circle_6_LightOrange));
                ivCircle4.setColorFilter(getResources().getColor(R.color.circle_55_DarkYellow));
                ivCircle3.setColorFilter(getResources().getColor(R.color.circle_5_Yellow));
                ivCircle2.setColorFilter(getResources().getColor(R.color.circle_45_LightYellow));
                ivCircle1.setColorFilter(getResources().getColor(R.color.circle_4_Lime));
                ivCircle0.setColorFilter(getResources().getColor(R.color.circle_25_Teal));
                mFrameBackground.setBackgroundColor(getResources().getColor(R.color.circle_2_LightBlue));
                mFrameBackground.setAlpha(1.0f);    //1 is transparent and 0 is opague
            }
            if(mPower>=50&&mPower<200){
                ivCircle9.setColorFilter(getResources().getColor(R.color.circle_8_Red));
                ivCircle8.setColorFilter(getResources().getColor(R.color.circle_75_DarkOrange));
                ivCircle7.setColorFilter(getResources().getColor(R.color.circle_7_Orange));
                ivCircle6.setColorFilter(getResources().getColor(R.color.circle_6_LightOrange));
                ivCircle5.setColorFilter(getResources().getColor(R.color.circle_55_DarkYellow));
                ivCircle4.setColorFilter(getResources().getColor(R.color.circle_5_Yellow));
                ivCircle3.setColorFilter(getResources().getColor(R.color.circle_45_LightYellow));
                ivCircle2.setColorFilter(getResources().getColor(R.color.circle_4_Lime));
                ivCircle1.setColorFilter(getResources().getColor(R.color.circle_35_LightGreen));
                ivCircle0.setColorFilter(getResources().getColor(R.color.circle_3_Green));
                mFrameBackground.setBackgroundColor(getResources().getColor(R.color.circle_25_Teal));
            }
            if(mPower>=30&&mPower<50){
                ivCircle9.setColorFilter(getResources().getColor(R.color.circle_7_Orange));
                ivCircle8.setColorFilter(getResources().getColor(R.color.circle_6_LightOrange));
                ivCircle7.setColorFilter(getResources().getColor(R.color.circle_55_DarkYellow));
                ivCircle6.setColorFilter(getResources().getColor(R.color.circle_5_Yellow));
                ivCircle5.setColorFilter(getResources().getColor(R.color.circle_45_LightYellow));
                ivCircle4.setColorFilter(getResources().getColor(R.color.circle_4_Lime));
                ivCircle3.setColorFilter(getResources().getColor(R.color.circle_35_LightGreen));
                ivCircle2.setColorFilter(getResources().getColor(R.color.circle_3_Green));
                ivCircle1.setColorFilter(getResources().getColor(R.color.circle_25_Teal));
                ivCircle0.setColorFilter(getResources().getColor(R.color.circle_2_LightBlue));
                mFrameBackground.setBackgroundColor(getResources().getColor(R.color.circle_15_Blue));
            }
            if(mPower>=15&&mPower<30){
                ivCircle9.setColorFilter(getResources().getColor(R.color.circle_6_LightOrange));
                ivCircle8.setColorFilter(getResources().getColor(R.color.circle_55_DarkYellow));
                ivCircle7.setColorFilter(getResources().getColor(R.color.circle_5_Yellow));
                ivCircle6.setColorFilter(getResources().getColor(R.color.circle_45_LightYellow));
                ivCircle5.setColorFilter(getResources().getColor(R.color.circle_4_Lime));
                ivCircle4.setColorFilter(getResources().getColor(R.color.circle_35_LightGreen));
                ivCircle3.setColorFilter(getResources().getColor(R.color.circle_3_Green));
                ivCircle2.setColorFilter(getResources().getColor(R.color.circle_25_Teal));
                ivCircle1.setColorFilter(getResources().getColor(R.color.circle_2_LightBlue));
                ivCircle0.setColorFilter(getResources().getColor(R.color.circle_15_Blue));
                mFrameBackground.setBackgroundColor(getResources().getColor(R.color.circle_1_DarkBlue));
            }
            if(mPower>0&&mPower<15){
                ivCircle9.setColorFilter(getResources().getColor(R.color.circle_5_Yellow));
                ivCircle8.setColorFilter(getResources().getColor(R.color.circle_45_LightYellow));
                ivCircle7.setColorFilter(getResources().getColor(R.color.circle_4_Lime));
                ivCircle6.setColorFilter(getResources().getColor(R.color.circle_35_LightGreen));
                ivCircle5.setColorFilter(getResources().getColor(R.color.circle_3_Green));
                ivCircle4.setColorFilter(getResources().getColor(R.color.circle_25_Teal));
                ivCircle3.setColorFilter(getResources().getColor(R.color.circle_2_LightBlue));
                ivCircle2.setColorFilter(getResources().getColor(R.color.circle_15_Blue));
                ivCircle1.setColorFilter(getResources().getColor(R.color.circle_1_DarkBlue));
                ivCircle0.setColorFilter(getResources().getColor(R.color.circle_1_DarkBlue));
                mFrameBackground.setBackgroundColor(getResources().getColor(R.color.circle_1_DarkBlue));
            }
            double spl =118;
            spl = 118+10*Math.log10(mPower);Log.d("paga1 spl", spl+"");
            double unifCo;
            if(imperial){
                uomString ="ft";
                unifCo =1; tv_distance_uom.setText(R.string.distance_imperial);
            }else{
                uomString ="m";
                unifCo = 0.3048; tv_distance_uom.setText(R.string.distance_metric);
            }
            double dbDistance1, dbDistance2, dbDistance3, dbDistance4, dbDistance5, dbDistance6;
            String stringDist1, stringDist2, stringDist3, stringDist4, stringDist5, stringDist6;
            if(mPower>300){Log.d(ACTIVITY,"mPower>300 text values");

                tv_db_level1.setText(R.string.one_hundred_ten_db);  Log.d(ACTIVITY,"interferanceCo circle "+interferanceCo);
                dbDistance1 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-110)/10) ) /(4*3.14159) ));
                stringDist1 = ""+(int)dbDistance1; //round down to lower int
                tv_distance1.setText(stringDist1);
                tv_db_level2.setText(R.string.one_hundred_db);
                dbDistance2 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-100)/10) ) /(4*3.14159) ));
                stringDist2 = ""+(int)dbDistance2;  tv_distance2.setText(stringDist2);
                tv_db_level3.setText(R.string.ninty_db);
                dbDistance3 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-90)/10) ) /(4*3.14159) ));
                stringDist3 = ""+(int)dbDistance3;  tv_distance3.setText(stringDist3);
                tv_db_level4.setText(R.string.eighty_db);
                dbDistance4 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-80)/10) ) /(4*3.14159) ));
                stringDist4 = ""+(int)dbDistance4;      tv_distance4.setText(stringDist4);
                tv_db_level5.setText(R.string.seventy_db);
                dbDistance5 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-70)/10) ) /(4*3.14159) ));
                stringDist5 = ""+(int)dbDistance5;      tv_distance5.setText(stringDist5);
                tv_db_level6.setText(R.string.sixty_db);
                dbDistance6 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-60)/10) ) /(4*3.14159) ));
                stringDist6 = ""+(int)dbDistance6;      tv_distance6.setText(stringDist6);
                widgetDistance = stringDist6;
            }
            if(mPower>50&&mPower<=300){ Log.d(ACTIVITY,"mPower>50&&mPower<=300 text values");
                tv_db_level1.setText(R.string.one_hundred_db);
                dbDistance1 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-100)/10) ) /(4*3.14159) ));
                stringDist1 = ""+(int)dbDistance1;      tv_distance1.setText(stringDist1);
                tv_db_level2.setText(R.string.ninty_db);
                dbDistance2 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-90)/10) ) /(4*3.14159) ));
                stringDist2 = ""+(int)dbDistance2;      tv_distance2.setText(stringDist2);
                tv_db_level3.setText(R.string.eighty_db);
                dbDistance3 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-80)/10) ) /(4*3.14159) ));
                stringDist3 = ""+(int)dbDistance3;      tv_distance3.setText(stringDist3);
                tv_db_level4.setText(R.string.seventy_db);
                dbDistance4 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-70)/10) ) /(4*3.14159) ));
                stringDist4 = ""+(int)dbDistance4;      tv_distance4.setText(stringDist4);
                tv_db_level5.setText(R.string.sixty_db);
                dbDistance5 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-60)/10) ) /(4*3.14159) ));
                stringDist5 = ""+(int)dbDistance5;      tv_distance5.setText(stringDist5);
                tv_db_level6.setText(R.string.fifty_db);
                dbDistance6 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-50)/10) ) /(4*3.14159) ));
                stringDist6 = ""+(int)dbDistance6;      tv_distance6.setText(stringDist6);
                widgetDistance = stringDist6;
            }if(mPower>0&&mPower<=50){  Log.d(ACTIVITY,"mPower>0&&mPower<=50 text values");
                tv_db_level1.setText(R.string.ninty_db);
                dbDistance1 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-90)/10) ) /(4*3.14159) ));
                stringDist1 = ""+(int)dbDistance1;      tv_distance1.setText(stringDist1);
                tv_db_level2.setText(R.string.eighty_db);
                dbDistance2 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-80)/10) ) /(4*3.14159) ));
                stringDist2 = ""+(int)dbDistance2;      tv_distance2.setText(stringDist2);
                tv_db_level3.setText(R.string.seventy_db);
                dbDistance3 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-70)/10) ) /(4*3.14159) ));
                stringDist3 = ""+(int)dbDistance3;      tv_distance3.setText(stringDist3);
                tv_db_level4.setText(R.string.sixty_db);
                dbDistance4 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-60)/10) ) /(4*3.14159) ));
                stringDist4 = ""+(int)dbDistance4;      tv_distance4.setText(stringDist4);
                tv_db_level5.setText(R.string.fifty_db);
                dbDistance5 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-50)/10) ) /(4*3.14159) ));
                stringDist5 = ""+(int)dbDistance5;      tv_distance5.setText(stringDist5);
                tv_db_level6.setText(R.string.forty_db);
                dbDistance6 = (2/interferanceCo)*Math.round( unifCo*sqrt(  pow(10, ((spl-40)/10) ) /(4*3.14159) ));
                stringDist6 = ""+(int)dbDistance6;      tv_distance6.setText(stringDist6);
                widgetDistance = stringDist6;
            }
            SumDbHelper dbHelper = new SumDbHelper(getApplicationContext());
            mDb = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(SumTaskContract.SummaryEntry.COLUMN_SYSTEM, getString(R.string.widget_paga));
            contentValues.put(SumTaskContract.SummaryEntry.COLUMN_SUMMARY, widgetDistance+uomString);
            Cursor cursor = mDb.query(SumTaskContract.SummaryEntry.TABLE_NAME, null, null,null,null,null,null);
            if(cursor.moveToFirst()){
                mDb.update(SumTaskContract.SummaryEntry.TABLE_NAME, contentValues,null, null);
                Log.d("Widget PwrLoads", "update");
            }else {
                long insert  =mDb.insert(SumTaskContract.SummaryEntry.TABLE_NAME, null, contentValues);
                Log.d("Widget PwrLoads", "insert");
            }
            SummaryService.startActionUpdateSum(getApplicationContext());
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SpeakerLevel",mPower);
    }
    @Override
    public void onPause() {
        if (adViewBanner != null) adViewBanner.pause();
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (adViewBanner != null) adViewBanner.resume();
    }
    @Override
    public void onDestroy() {
        if (adViewBanner != null) adViewBanner.destroy();
        super.onDestroy();
    }
}
