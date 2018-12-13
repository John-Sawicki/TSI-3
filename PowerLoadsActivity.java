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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.tsi.SqliteSum.SumDbHelper;
import com.example.android.tsi.SqliteSum.SumTaskContract;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.android.tsi.SqliteSum.SumTaskContract.SummaryEntry;
import com.example.android.tsi.Widget.SummaryService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class PowerLoadsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    //TODO add preference fragment for source voltage
    //TODO add preference for pf value
    private TextView tv_line_total_1, tv_line_total_2,tv_line_total_3, tv_line_total_4,tv_line_total_5, tv_line_total_6,
            tv_line_total_7, tv_line_total_8,tv_line_total_9, tv_line_total_10,
            tv_total_power_result, tv_total_pdu_result, tv_total_ups_result,tv_breaker_result;
    private EditText et_qty_1, et_watt_1, et_qty_2, et_watt_2, et_qty_3, et_watt_3, et_qty_4, et_watt_4, et_qty_5,
            et_watt_5, et_qty_6, et_watt_6, et_qty_7, et_watt_7, et_qty_8, et_watt_8, et_qty_9, et_watt_9, et_qty_10, et_watt_10;
    private static int ZERO = 0, ONE = 1, TWO = 2, FIVE = 5, TEN = 10, FIFTEEN = 15, TWENTY =20, TWENTY_FIVE = 25, THIRTY = 30, FOURTY = 40, FIFTY = 50,
            TWELVE= 12, TWENTY_FOUR =24, FORTY_EIGHT = 48, ONE_HUNDRED_TWENTY = 120, TWO_HUNDRED_THIRTY = 230;
    private Button btn_calculate;
    private int voltage = 120,spinnerVoltage, powerTotal,intPduSize, breakerSize;
    private SQLiteDatabase mDb;
    private boolean imperial = true;
    @BindView(R.id.adViewBanner) AdView adViewBanner;
    @BindView(R.id.sp_source_voltage)Spinner sp_source_voltage;
    private static String ACTIVITY = "Power_Loads_Activity";
    private double upsSize;
    private float powerFactor =0.9f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_loads);
        ButterKnife.bind(this);
        View viewLoads = findViewById(R.id.lo_top_left);
        View viewResults = findViewById(R.id.lo_bottom_right);
        MobileAds.initialize(this, "ca-app-pub-8686454969066832~6147856904");
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBanner.loadAd(adRequest);
        setUpPreferences();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.voltages,android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_source_voltage.setAdapter(adapter);
        sp_source_voltage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerVoltage = determineVoltage(i); Log.d(ACTIVITY,"voltage "+voltage);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        et_qty_1 = viewLoads.findViewById(R.id.et_qty_1);
        et_watt_1 = viewLoads.findViewById(R.id.et_watt_1);
        tv_line_total_1 = viewLoads.findViewById(R.id.tv_line_total_1);
        et_qty_2 = viewLoads.findViewById(R.id.et_qty_2);
        et_watt_2 = viewLoads.findViewById(R.id.et_watt_2);
        tv_line_total_2 = viewLoads.findViewById(R.id.tv_line_total_2);
        et_qty_3 = viewLoads.findViewById(R.id.et_qty_3);
        et_watt_3 = viewLoads.findViewById(R.id.et_watt_3);
        tv_line_total_3 = viewLoads.findViewById(R.id.tv_line_total_3);
        et_qty_4 = viewLoads.findViewById(R.id.et_qty_4);
        et_watt_4 = viewLoads.findViewById(R.id.et_watt_4);
        tv_line_total_4 = viewLoads.findViewById(R.id.tv_line_total_4);
        et_qty_5 = viewLoads.findViewById(R.id.et_qty_5);
        et_watt_5 = viewLoads.findViewById(R.id.et_watt_5);
        tv_line_total_5 = viewLoads.findViewById(R.id.tv_line_total_5);
        et_qty_6 = viewLoads.findViewById(R.id.et_qty_6);
        et_watt_6 = viewLoads.findViewById(R.id.et_watt_6);
        tv_line_total_6 = viewLoads.findViewById(R.id.tv_line_total_6);
        et_qty_7 = viewLoads.findViewById(R.id.et_qty_7);
        et_watt_7 = viewLoads.findViewById(R.id.et_watt_7);
        tv_line_total_7 = viewLoads.findViewById(R.id.tv_line_total_7);
        et_qty_8 = viewLoads.findViewById(R.id.et_qty_8);
        et_watt_8 = viewLoads.findViewById(R.id.et_watt_8);
        tv_line_total_8 = viewLoads.findViewById(R.id.tv_line_total_8);
        et_qty_9 = viewLoads.findViewById(R.id.et_qty_9);
        et_watt_9 = viewLoads.findViewById(R.id.et_watt_9);
        tv_line_total_9 = viewLoads.findViewById(R.id.tv_line_total_9);
        et_qty_10 = viewLoads.findViewById(R.id.et_qty_10);
        et_watt_10 = viewLoads.findViewById(R.id.et_watt_10);
        tv_line_total_10 = viewLoads.findViewById(R.id.tv_line_total_10);
        tv_total_power_result = viewResults.findViewById(R.id.tv_total_power_result);
        tv_total_pdu_result = viewResults.findViewById(R.id.tv_total_pdu_result);
        tv_total_ups_result = viewResults.findViewById(R.id.tv_total_ups_result);
        tv_breaker_result = viewResults.findViewById(R.id.tv_breaker_result);
        btn_calculate = viewResults.findViewById(R.id.btn_calculate);
        if(savedInstanceState!=null){
            powerTotal =savedInstanceState.getInt("powerTotal",0);
            String saveIntText =powerTotal+"W";     tv_total_power_result.setText(saveIntText);
            intPduSize = savedInstanceState.getInt("intPduSize",0);
            saveIntText =intPduSize+"W";            tv_total_pdu_result.setText(saveIntText);
            upsSize =savedInstanceState.getDouble("upsSize",0);
            if(upsSize>(int)upsSize) upsSize+=1;//round decimal up
            saveIntText= (int)upsSize+"W";
            tv_total_ups_result.setText(saveIntText);
            breakerSize =savedInstanceState.getInt("breakerSize",0);
            if(breakerSize==0) {
                tv_breaker_result.setText(R.string.breaker_greater_than_50);
            }else{
                saveIntText = breakerSize+"A";
                tv_breaker_result.setText(saveIntText);
            }

        }
        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty1=0, qty2=0, qty3=0, qty4=0, qty5=0, qty6=0, qty7=0, qty8=0, qty9=0, qty10=0,
                        watt1=0, watt2=0, watt3=0, watt4=0, watt5=0, watt6=0, watt7=0, watt8=0, watt9=0, watt10=0;
                if (!TextUtils.isEmpty(et_qty_1.getText().toString())) qty1 = Integer.parseInt(et_qty_1.getText().toString());
                if (!TextUtils.isEmpty(et_watt_1.getText().toString())) watt1 = Integer.parseInt(et_watt_1.getText().toString());
                if (!TextUtils.isEmpty(et_qty_2.getText().toString())) qty2 = Integer.parseInt(et_qty_2.getText().toString());
                if (!TextUtils.isEmpty(et_watt_2.getText().toString())) watt2 = Integer.parseInt(et_watt_2.getText().toString());
                if (!TextUtils.isEmpty(et_qty_3.getText().toString())) qty3 = Integer.parseInt(et_qty_3.getText().toString());
                if (!TextUtils.isEmpty(et_watt_3.getText().toString())) watt3 = Integer.parseInt(et_watt_3.getText().toString());
                if (!TextUtils.isEmpty(et_qty_4.getText().toString())) qty4 = Integer.parseInt(et_qty_4.getText().toString());
                if (!TextUtils.isEmpty(et_watt_4.getText().toString())) watt4 = Integer.parseInt(et_watt_4.getText().toString());
                if (!TextUtils.isEmpty(et_qty_5.getText().toString())) qty5 = Integer.parseInt(et_qty_5.getText().toString());
                if (!TextUtils.isEmpty(et_watt_5.getText().toString())) watt5 = Integer.parseInt(et_watt_5.getText().toString());
                if (!TextUtils.isEmpty(et_qty_6.getText().toString())) qty6 = Integer.parseInt(et_qty_6.getText().toString());
                if (!TextUtils.isEmpty(et_watt_6.getText().toString())) watt6 = Integer.parseInt(et_watt_6.getText().toString());
                if (!TextUtils.isEmpty(et_qty_7.getText().toString())) qty7 = Integer.parseInt(et_qty_7.getText().toString());
                if (!TextUtils.isEmpty(et_watt_7.getText().toString())) watt7 = Integer.parseInt(et_watt_7.getText().toString());
                if (!TextUtils.isEmpty(et_qty_8.getText().toString())) qty8 = Integer.parseInt(et_qty_8.getText().toString());
                if (!TextUtils.isEmpty(et_watt_8.getText().toString())) watt8 = Integer.parseInt(et_watt_8.getText().toString());
                if (!TextUtils.isEmpty(et_qty_9.getText().toString())) qty9 = Integer.parseInt(et_qty_9.getText().toString());
                if (!TextUtils.isEmpty(et_watt_9.getText().toString())) watt9 = Integer.parseInt(et_watt_9.getText().toString());
                if (!TextUtils.isEmpty(et_qty_10.getText().toString())) qty10 = Integer.parseInt(et_qty_10.getText().toString());
                if (!TextUtils.isEmpty(et_watt_10.getText().toString())) watt10 = Integer.parseInt(et_watt_10.getText().toString());

                int line1 = qty1 *watt1;
                String line1String =line1+"W";   tv_line_total_1.setText(line1String);
                int line2 = qty2 *watt2;
                String line2String =line2+"W";   tv_line_total_2.setText(line2String);
                int line3 = qty3 *watt3;
                String line3String =line3+"W";   tv_line_total_3.setText(line3String);
                int line4 = qty4 *watt4;
                String line4String =line4+"W";   tv_line_total_4.setText(line4String);
                int line5 = qty5 *watt5;
                String line5String =line5+"W";   tv_line_total_5.setText(line5String);
                int line6 = qty6 *watt6;
                String line6String =line6+"W";   tv_line_total_6.setText(line6String);
                int line7 = qty7 *watt7;
                String line7String =line7+"W";   tv_line_total_7.setText(line7String);
                int line8 = qty8 *watt8;
                String line8String =line8+"W";   tv_line_total_8.setText(line8String);
                int line9 = qty9 *watt9;
                String line9String =line9+"W";   tv_line_total_9.setText(line9String);
                int line10 = qty10 *watt10;
                String line10String =line10+"W";   tv_line_total_10.setText(line10String);
                powerTotal = line1+line2+line3+line4+line5+line6+line7+line8+line9+line10;//save on rotate
                tv_total_power_result.setText(powerTotal+"W");
                double pduSize = powerTotal/.8;
                intPduSize = (int)pduSize+1;//save on rotate
                tv_total_pdu_result.setText(intPduSize+"W");
                if(powerFactor<0&& powerFactor>1){//check for limits for the power factor entered in preference activity
                    upsSize = powerTotal/0.9;//save on rotate
                }else{
                    upsSize = powerTotal/powerFactor;//save on rotate
                }

                if(upsSize>(int)upsSize){//if there is a decimal value, round up to the next integar
                    Log.d("Widget PwrLoads", upsSize+"");
                    upsSize+=1;    //round up the the next int
                    Log.d("Widget PwrLoads", upsSize+"");
                }
                tv_total_ups_result.setText((int)upsSize+"VA");
                breakerSize = breakerSize(powerTotal);//save on rotate
                Log.d("Widget PwrLoads", "breakerSize "+breakerSize);
                if(breakerSize==0){
                    tv_breaker_result.setText(R.string.breaker_greater_than_50);
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, R.string.max_breaker_size_error, Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    tv_breaker_result.setText(breakerSize+"A");
                }


                SumDbHelper dbHelper = new SumDbHelper(getApplicationContext());
                mDb = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(SumTaskContract.SummaryEntry.COLUMN_SYSTEM, "Power Loads");
                contentValues.put(SumTaskContract.SummaryEntry.COLUMN_SUMMARY, "UPS Size "+(int)upsSize+"W");
                Cursor cursor = mDb.query(SummaryEntry.TABLE_NAME, null, null,null,null,null,null);
                if(cursor.moveToFirst()){
                    mDb.update(SummaryEntry.TABLE_NAME, contentValues,null, null);
                    Log.d("Widget PwrLoads", "update");
                }else {
                    long insert  =mDb.insert(SummaryEntry.TABLE_NAME, null, contentValues);
                    Log.d("Widget PwrLoads", "insert");
                }cursor.close();
                SummaryService.startActionUpdateSum(getApplicationContext());
            }
        });
    }
    private int breakerSize(double bsPowerTotal){
        double mBsTotalPower = bsPowerTotal;
        double bsCurrent = mBsTotalPower/spinnerVoltage;//ex 500W load /120V = ~4.2A
        double breakerSize = bsCurrent/0.8;//20% margin
        if(breakerSize<1) return ONE;
        if(breakerSize<2) return TWO;
        if(breakerSize<5) return FIVE;
        if(breakerSize<10) return TEN;
        if(breakerSize<15) return FIFTEEN;
        if(breakerSize<20) return TWENTY;
        if(breakerSize<25) return TWENTY_FIVE;
        if(breakerSize<30) return THIRTY;
        if(breakerSize<40) return FOURTY;
        if(breakerSize<50) return FIFTY;
        return ZERO;
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
    private void setUpPreferences() {//sets up preferences when the user reopens the activity
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        imperial = sharedPref.getBoolean(getResources().getString(R.string.pref_units_key), true);
        powerFactor = sharedPref.getFloat(getResources().getString(R.string.pref_pf_key), 0.9f);
        Log.d("pref fragment", imperial+" setup");
    }
    @Override//updates the activity once the units system has changed
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getResources().getString(R.string.pref_units_key))){
            imperial = sharedPreferences.getBoolean(getResources().getString(R.string.pref_units_key), true);
            Log.d("pref fragment", imperial+" changed");
        }else if(key.equals(getResources().getString(R.string.pref_pf_key))){
            powerFactor = sharedPreferences.getFloat(getResources().getString(R.string.pref_pf_key), 0.9f);
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
            startActivity(new Intent(PowerLoadsActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("powerTotal",powerTotal);
        outState.putInt("intPduSize",intPduSize);
        outState.putDouble("upsSize",upsSize);
        outState.putInt("breakerSize",breakerSize);
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
