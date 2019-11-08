package com.example.andyyuan.androidmui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static io.dcloud.common.util.ShortCutUtil.TAG;

/**
 * Created by AndyYuan on 2019/11/06.
 * 描述:     TODO
 */

public class OpenActivityUtils {

    private static Context mContext;
    private static String mToken = "";
    private static String mUserId = "";
    public static String mBaseUrl = "";
    private static Activity mActivity;
    private static String lngAndLat;
    private static Intent intent;

    /**
     * 请求权限的回调
     */
    public interface CallBack {
        void loadDataFromNativeToH5(String lngAndLat);
    }

    private static int GPS_REQUEST_CODE = 10;

    /**
     * 初始化工具类 * @param context
     */
    public static void initUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void setActivity(Activity activity) {
        mActivity = activity;
    }

    /**
     * 从H5中接收数据和方法回调
     *
     * @param jsonObject
     * @param callBack
     */
    public static void loadDataFromH5ToNative(String jsonObject, CallBack callBack) {
        try {
            //初次登录，判断是否开启定位服务
            Log.d(TAG, "openActivitUtils: " + jsonObject);
            JSONObject jsonObj = new JSONObject(jsonObject);
            String methodType = jsonObj.getString("methodType");
            Log.e(TAG,"获取的H5传来的"+jsonObj.getString("jsonObject"));
            JSONObject JsonBean = null;
            switch (methodType) {
                case "openGridPatrol":
                    JsonBean = new JSONObject(jsonObj.getString("jsonObject"));
                    String name=JsonBean.getString("identity");
                    String age = JsonBean.getString("token");
                    Log.d(TAG, "openActivitUtils:===JsonBean= " + JsonBean);
                    Intent intent = null;
                    Log.d(TAG, "loadDataFromH5ToNative: " + name);
                    Log.d(TAG, "loadDataFromH5ToNative: " + age);
                    intent = new Intent(MyApplication.getInstance(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getInstance().startActivity(intent);
                    //测试小讯集成方案
                    //MyApplication.getInstance().startActivity(new Intent(MyApplication.getInstance(), TestIMActivity.class));
                    break;
                case "openLocationAddress":
                    Intent intents = null;
                    String title = jsonObj.getString("title");
                    break;
                case "loadGPSInfo":
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "error==" + e.getMessage());

        }
    }
}
