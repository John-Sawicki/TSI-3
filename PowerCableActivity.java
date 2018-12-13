package com.example.android.tsi;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.tsi.SqliteSum.SumDbHelper;
import com.example.android.tsi.SqliteSum.SumTaskContract;
import com.example.android.tsi.Widget.SummaryService;
import com.example.android.tsi.utilities.WireGauageCalc;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PowerCableActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{


    protected Spinner sp_source_voltage;

    protected EditText et_voltage, et_wattage, et_distance, et_percent_drop;
    protected TextView tv_wire_gauge_result, tv_wire_distance_result;
    protected Button btn_calculate;
    private SQLiteDatabase mDb;
    private int voltage;
    private static int TWELVE= 12, TWENTY_FOUR =24, FORTY_EIGHT = 48, ONE_HUNDRED_TWENTY = 120, TWO_HUNDRED_THIRTY = 230;
    private double doubleZgauge = 0.078, zeroGauage = 0.0983, twoGauge = 0.1563, fourGuge = 0.2485, sixGauge = 0.3951, eightGauge = 0.6282,
            tenGauge = 0.9989,twelveGauge = 1.588, fourteenGauge = 2.525, sixteenGgauge = 4.016,eighteenGauge = 6.385;
    private int  vParent = 120 ;
    private double percentDrop = 2.5, totalVdrop, parentVdrop, childVdrop, current, resistanceMax, power = 200.0, distance = 0.0, powerFactor =0.9;
    @BindView(R.id.adViewBanner) AdView adViewBanner;
    private boolean imperial = true;
    private String[] wireGauge = new String[2];//0 index is gauge, 1 index is max distance
    private static String ACTIVITY = "Power_Cable_Activity", unitSffix = "ft",maxDisString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_cable);
        ButterKnife.bind(this);
        View viewReq = findViewById(R.id.lo_top_left);
        View viewResults = findViewById(R.id.lo_bottom_right);
        MobileAds.initialize(this, "ca-app-pub-8686454969066832~6147856904");
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBanner.loadAd(adRequest);
        setUpPreferences();
        et_voltage =viewReq.findViewById(R.id.et_voltage);
        et_wattage = viewReq.findViewById(R.id.et_wattage);
        et_distance = viewReq.findViewById(R.id.et_distance);
        et_percent_drop = viewReq.findViewById(R.id.et_percent_drop);
        tv_wire_gauge_result = viewResults.findViewById(R.id.tv_wire_gauge_result);
        tv_wire_distance_result = viewResults.findViewById(R.id.tv_wire_distance_result);
        if(savedInstanceState!=null){
            wireGauge[0]= savedInstanceState.getString("wireGauge_one");
            maxDisString = savedInstanceState.getString("maxDisString");
            Log.d(ACTIVITY, "savedInstanceState "+wireGauge[0]+" "+maxDisString);
            if(wireGauge[0]!=null) tv_wire_gauge_result.setText(wireGauge[0]);
            if(maxDisString!=null) tv_wire_distance_result.setText(maxDisString);
        }
        sp_source_voltage = viewReq.findViewById(R.id.sp_source_voltage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.voltages,android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_source_voltage.setAdapter(adapter);
        sp_source_voltage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                voltage = determineVoltage(i); Log.d(ACTIVITY,"voltage "+voltage);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn_calculate = viewResults.findViewById(R.id.btn_calculate);
        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(ACTIVITY,"onClick - voltage "+voltage);
                vParent= Integer.parseInt(  et_voltage.getText().toString()  ); Log.d(ACTIVITY,"vParent "+vParent);
                power = Double.parseDouble(  et_wattage.getText().toString() );Log.d(ACTIVITY,"power "+power);
                distance = Double.parseDouble(    et_distance.getText().toString());Log.d(ACTIVITY,"distance "+distance);
                percentDrop = Double.parseDouble( et_percent_drop.getText().toString() );Log.d(ACTIVITY,"vDrop "+percentDrop);

                current = power/ vParent; Log.d(ACTIVITY,"current "+current);
                totalVdrop = voltage*(percentDrop/100);Log.d(ACTIVITY,"totalVdrop "+totalVdrop);//max voltage drop from the source voltage
                parentVdrop = voltage - vParent;   Log.d(ACTIVITY,"parentVdrop "+parentVdrop); //how much the voltage has drop to the downstream point being calculated
                childVdrop = totalVdrop - parentVdrop; Log.d(ACTIVITY,"childVdrop "+childVdrop); //how much the the voltage can drop for the given wire
                resistanceMax =childVdrop/(power/vParent);Log.d(ACTIVITY,"resistanceMax "+resistanceMax);  //maximum wire resistance to get the required voltage drop value
                Log.d(ACTIVITY,"distance "+distance+" rmax "+resistanceMax+" imperial "+imperial );
                if(vParent>voltage){//eg 121V downstream vs 120V sourece
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, R.string.downstream_voltage_error, Toast.LENGTH_LONG);
                    toast.show();   setTbdTexts();
                }else if(totalVdrop<=parentVdrop){//eg max 3v drop and already dropped by 5V
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, R.string.downstream_voltage_drop_error, Toast.LENGTH_LONG);
                    toast.show();   setTbdTexts();
                }else{//for valid entries
                    WireGauageCalc wireGauageCalc = new WireGauageCalc();
                    wireGauge = wireGauageCalc.calculateWireGauge(distance, resistanceMax, imperial);
                    tv_wire_gauge_result.setText(wireGauge[0]);
                    double maxDisDbl = Double.valueOf(wireGauge[1]);
                    int maxDisInt = (int)maxDisDbl;//round down to the next int
                    if(imperial){
                        unitSffix = "ft";
                    }else{
                        unitSffix = "m";
                    }
                    maxDisString = maxDisInt+unitSffix;
                    tv_wire_distance_result.setText(maxDisString);
                    Log.d(ACTIVITY, wireGauge[0]+" "+wireGauge[1] );
                    SumDbHelper dbHelper = new SumDbHelper(getApplicationContext());
                    mDb = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SumTaskContract.SummaryEntry.COLUMN_SYSTEM, getString(R.string.power_cable_widget));
                    contentValues.put(SumTaskContract.SummaryEntry.COLUMN_SUMMARY, wireGauge[0]);
                    Cursor cursor = mDb.query(SumTaskContract.SummaryEntry.TABLE_NAME, null, null,null,null,null,null);
                    if(cursor.moveToFirst()){
                        mDb.update(SumTaskContract.SummaryEntry.TABLE_NAME, contentValues,null, null);
                        Log.d("Widget PwrLoads", "update");//
                    }else {
                        long insert  =mDb.insert(SumTaskContract.SummaryEntry.TABLE_NAME, null, contentValues);
                        Log.d("Widget PwrLoads", "insert");
                    }cursor.close();
                    SummaryService.startActionUpdateSum(getApplicationContext());
                }
            }
        });
    }
    private int determineVoltage(int voltageIndex){
        switch(voltageIndex){
            case 0: return TWELVE;
            case 1: return TWENTY_FOUR;
            case 2: return FORTY_EIGHT;
            case 3: return ONE_HUNDRED_TWENTY;
            case 4: return TWO_HUNDRED_THIRTY;
            default: return ONE_HUNDRED_TWENTY;
        }
    }
    private void setTbdTexts(){
        tv_wire_gauge_result.setText(R.string.tbd);
        tv_wire_distance_result.setText(R.string.tbd);
    }
    private void setUpPreferences() {//sets up preferences when the user reopens the activity
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        imperial = sharedPref.getBoolean(getResources().getString(R.string.pref_units_key), true);

        Log.d("pref fragment", imperial+" setup");
    }
    @Override//updates the activity once the units system has changed
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getResources().getString(R.string.pref_units_key))){
            imperial = sharedPreferences.getBoolean(getResources().getString(R.string.pref_units_key), true);
            Log.d("pref fragment", imperial+" changed");
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
            Log.d("menu", "menu clicked");
            startActivity(new Intent(PowerCableActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("wireGauge_one", wireGauge[0]);
        outState.putString("maxDisString",maxDisString);
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
