package com.example.andyyuan.androidmui;

import android.content.Context;
import android.os.Build;

import com.im.rainbowchat.MyApplication;

import java.lang.reflect.Method;

import io.dcloud.common.adapter.util.Logger;
import io.dcloud.common.adapter.util.UEH;
import io.dcloud.common.util.BaseInfo;
import io.dcloud.common.util.PdrUtil;


/**
 * 适配mui框架Application中间件,使用此方式来解决mui框架
 * 和本地原生application不兼容的问题
 * Created by AndyYuan on time at 2019/10/28.
 */

public class ThirdApplication extends MyApplication {

    private static final String a = ThirdApplication.class.getSimpleName();
    public boolean isQihooTrafficFreeValidate = true;
    private static ThirdApplication b;
    private static Context c = null;

    public ThirdApplication() {
    }

    public static Context getInstance() {
        return c;
    }

    public static void setInstance(Context var0) {
        if(c == null) {
            c = var0;
        }

    }

    public static ThirdApplication self() {
        return b;
    }

    public void onCreate() {
        super.onCreate();
        PdrUtil.closeAndroidPDialog();
        BaseInfo.initWeex(this);
//        a.a(this);
        b = this;
        BaseInfo.sAppIsTests.init(this.getBaseContext());
        setInstance(this.getApplicationContext());
        UEH.catchUncaughtException(this.getApplicationContext());
    }

    protected void attachBaseContext(Context var1) {
        super.attachBaseContext(var1);
        if(Build.VERSION.SDK_INT < 21) {
            this.a();
        }

//        a.a(var1);
    }

    public void onLowMemory() {
        super.onLowMemory();
        Logger.e(a, "onLowMemory" + Runtime.getRuntime().maxMemory());
    }

    public void onTrimMemory(int var1) {
        super.onTrimMemory(var1);
        Logger.e(a, "onTrimMemory");
    }

    protected void a() {
        try {
            Class var1 = Class.forName("android.support.multidex.MultiDex");
            Method var2 = var1.getMethod("install", new Class[]{Context.class});
            var2.invoke((Object)null, new Object[]{this});
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
