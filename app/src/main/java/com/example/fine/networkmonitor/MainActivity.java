package com.example.fine.networkmonitor;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.example.fine.networkmonitor.service.MonitorService;
import com.example.fine.networkmonitor.util.ContextUtil;
import com.example.fine.networkmonitor.util.MobileDataControl;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String MOBILE_DATA = "mobile_data";
    private SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isOpen = MobileDataControl.getMobileDataStatus(view.getContext());
                String str = String.format("当前数据开关状态：%s", isOpen ? "开启" : "关闭");
                Snackbar.make(view, str, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        switchCompat = (SwitchCompat) findViewById(R.id.mobileDataSwitch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isOpen = MobileDataControl.getMobileDataStatus(buttonView.getContext());
                if (isOpen != isChecked) {
                    Log.i(TAG, "setMobileDataStatus = " + isChecked);
                    MobileDataControl.setMobileDataStatus(buttonView.getContext(), isChecked);
                }
            }
        });
        boolean isOpen = MobileDataControl.getMobileDataStatus(MainActivity.this);
        switchCompat.setChecked(isOpen);


        SwitchCompat switchCompat2 = (SwitchCompat) findViewById(R.id.networkMonitorSwitch);
        switchCompat2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isAlive = ContextUtil.isServiceAlive(buttonView.getContext(), MonitorService.ServiceName);
                if (isAlive != isChecked) {
                    Log.i(TAG, "setServiceEnabled = " + isChecked);
                    MonitorService.setServiceEnabled(buttonView.getContext(), isChecked);
                }
            }
        });

        boolean isAlive = ContextUtil.isServiceAlive(MainActivity.this, MonitorService.ServiceName);
        switchCompat2.setChecked(isAlive);

        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(MOBILE_DATA), false, observer);
    }

    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(observer);
        super.onDestroy();
    }

    private MobileDataContentObserver observer = new MobileDataContentObserver(new Handler());

    private class MobileDataContentObserver extends ContentObserver {

        public MobileDataContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            boolean isOpen = MobileDataControl.getMobileDataStatus(MainActivity.this);
            switchCompat.setChecked(isOpen);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
