package songwei520.map3synthesis1.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Locale;

import songwei520.map3synthesis1.R;
import songwei520.map3synthesis1.utils.SharedPreferencesHelper;
import songwei520.map3synthesis1.utils.SharedPreferencesNameFile;

public class SplashActivity extends Activity{

    private SharedPreferencesHelper sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        Spinner spinner_mapType = (Spinner) this.findViewById(R.id.spinner_mapType);
        // 高德：1      百度：2       谷歌：3       高德 + 谷歌：4
        String[] items = {"高德地图","百度地图", "google地图", "高德+Google"};
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        spinner_mapType.setAdapter(aa);

        spinner_mapType.setSelection(0);
        sp = new SharedPreferencesHelper(this);
        sp.put(SharedPreferencesNameFile.MapTypeInt,1);

        spinner_mapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                sp.put(SharedPreferencesNameFile.MapTypeInt,position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
    }

    public void enterMap(View view) {
        startActivity(new Intent(this,MainActivity.class));
    }

}
