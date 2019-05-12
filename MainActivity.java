package com.john.android.tsi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.john.android.tsi.utilities.ApiKey;
import com.john.android.tsi.utilities.SystemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static com.john.android.tsi.utilities.ApiKey.bannerAdKey;

public class MainActivity extends AppCompatActivity implements SystemAdapter.SystemOnClickInterface, SharedPreferences.OnSharedPreferenceChangeListener {
    @BindView(R.id.rv_system_name)RecyclerView mRecyclerView;
    @BindView(R.id.adViewBanner) AdView adViewBanner;
    private SystemAdapter mSystemAdapter;
    private boolean imperial = true;
    private boolean landscape = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        landscape = getResources().getBoolean(R.bool.landscape);
        Log.d("SystemAdapter", "onCreate boolean "+landscape);
        mSystemAdapter = new SystemAdapter(this, landscape);
        mRecyclerView.setAdapter(mSystemAdapter);
        MobileAds.initialize(this, "ca-app-pub-8686454969066832~6147856904");
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBanner.loadAd(adRequest);
        setUpPreferences();
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
    public void onClick(int index) {
        Log.d("MainAct onClick", "click index "+index);
        Intent intent =new
                Intent(MainActivity.this, MainActivity.class);//had to initialize intent
        switch (index){
            case 0: intent = new
                    Intent(MainActivity.this, PagaActivity.class);
                    break;
            case 1: intent = new
                    Intent(MainActivity.this, PowerCableActivity.class);
                    break;
            case 2: intent = new
                    Intent(MainActivity.this, PowerLoadsActivity.class);
                    break;
            case 3: intent = new
                    Intent(MainActivity.this, TaskListActivity.class);
                    break;
        }
        startActivity(intent);
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
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
