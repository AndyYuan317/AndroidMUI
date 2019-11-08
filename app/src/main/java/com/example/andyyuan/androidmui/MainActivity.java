package com.example.andyyuan.androidmui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        findViewById(R.id.testbutton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(MainActivity.this, AppStart.class);
////                startActivity(intent);
//                doLogin();
//            }
//        });
//
//        findViewById(R.id.alarm_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //startActivity(IntentFactory.createPortalIntent(MainActivity.this));
//                Intent intent = new Intent(MainActivity.this,AlarmActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        findViewById(R.id.group_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(MainActivity.this, GroupsActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        findViewById(R.id.address_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, RosterActivity_2_5.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    public void doLogin(){
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (PreferenceUtil.getInstance().getAccount(MainActivity.this) != null && PreferenceUtil.getInstance().getPwd(MainActivity.this) != null
//                        && PreferenceUtil.getInstance().getAccount(MainActivity.this).length() != 0 && PreferenceUtil.getInstance().getPwd(MainActivity.this).length() != 0) {
//                    com.im.rainbowchat.MyApplication.getInstance(MainActivity.this).getIMClientManager().initMobileIMSDK();
//                    MyLoginUtil loginUtils = new MyLoginUtil(MainActivity.this);
//                    int versionCode;
//                    try {
//                        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
//                        versionCode = packageInfo.versionCode;
//                        Log.e(">>>>>>>>>>>>>>>>>","自动登录获取的版本号为："+versionCode);
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                        versionCode=100;
//                    }
//
//                    loginUtils.setLogin(MainActivity.this, PreferenceUtil.getInstance().getAccount(MainActivity.this), PreferenceUtil.getInstance().getPwd(MainActivity.this),versionCode);
//                } else {
//                    startActivity(IntentFactory.createLoginIntent(MainActivity.this));
//                    finish();
//                }
//
//            }
//        }, TIME_LOAD_MIN);
    }
}
