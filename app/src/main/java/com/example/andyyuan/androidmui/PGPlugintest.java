package com.example.andyyuan.androidmui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

import static android.content.ContentValues.TAG;


/**
 * 5+ SDK 扩展插件示例
 * 5+ 扩扎插件在使用时需要以下两个地方进行配置
 * 1  WebApp的mainfest.json文件的permissions节点下添加JS标识
 * 2  assets/data/properties.xml文件添加JS标识和原生类的对应关系
 * 本插件对应的JS文件在 assets/apps/H5Plugin/js/test.js
 * 本插件对应的使用的HTML assest/apps/H5plugin/index.html
 * <p>
 * 更详细说明请参考文档http://ask.dcloud.net.cn/article/66
 **/
public class PGPlugintest extends StandardFeature {

    public void onStart(Context pContext, Bundle pSavedInstanceState, String[] pRuntimeArgs) {

        /**
         * 如果需要在应用启动时进行初始化，可以继承这个方法，并在properties.xml文件的service节点添加扩展插件的注册即可触发onStart方法
         * */
    }

    //
//    public void onStart(Context pContext, Bundle pSavedInstanceState, String[] pRuntimeArgs) {
//
//    /**
//     * 如果需要在应用启动时进行初始化，可以继承这个方法，并在properties.xml文件的service节点添加扩展插件的注册即可触发onStart方法
//     * */
//    }
//
    public void PluginTestFunction(IWebview pWebview, JSONArray array)


    {


        Log.e(TAG, String.format("webview = %s, jsonArray = %s", pWebview, array));
        // Toast.makeText(mApplicationContext, array.toString(), Toast.LENGTH_SHORT).show();


        // 原生代码中获取JS层传递的参数，
        // 参数的获取顺序与JS层传递的顺序一致
        String CallBackID = array.optString(0);
        JSONArray newArray = new JSONArray();


        newArray.put(array.optString(1));
        newArray.put(array.optString(2));
        newArray.put(array.optString(3));
        newArray.put(array.optString(4));
        // 调用方法将原生代码的执行结果返回给js层并触发相应的JS层回调函数
        JSUtil.execCallback(pWebview, CallBackID, newArray, JSUtil.OK, false);


    }


    //
//    public void PluginTestFunctionArrayArgu(IWebview pWebview, JSONArray array)
//    {
//        String ReturnString = null;
//        String CallBackID =  array.optString(0);
//        JSONArray newArray = null;
//        try {
//
//            newArray = new JSONArray( array.optString(1));
//            String inValue1 = newArray.getString(0);
//            String inValue2 = newArray.getString(1);
//            String inValue3 = newArray.getString(2);
//            String inValue4 = newArray.getString(3);
//            ReturnString = inValue1 + "-" + inValue2 + "-" + inValue3 + "-" + inValue4;
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        JSUtil.execCallback(pWebview, CallBackID, ReturnString, JSUtil.OK, false);
//    }
//
//    public String PluginTestFunctionSyncArrayArgu(IWebview pWebview, JSONArray array)
//    {
//        JSONArray newArray = null;
//        JSONObject retJSONObj = null;
//        try {
//
//            newArray = array.optJSONArray(0);
//            String inValue1 = newArray.getString(0);
//            String inValue2 = newArray.getString(1);
//            String inValue3 = newArray.getString(2);
//            String inValue4 = newArray.getString(3);
//
//            retJSONObj = new JSONObject();
//            retJSONObj.putOpt("RetArgu1", inValue1);
//            retJSONObj.putOpt("RetArgu2", inValue2);
//            retJSONObj.putOpt("RetArgu3", inValue3);
//            retJSONObj.putOpt("RetArgu4", inValue4);
//
//        } catch (JSONException e1) {
//            e1.printStackTrace();
//        }
//
//        return JSUtil.wrapJsVar(retJSONObj);
//    }
//
//    public String PluginTestFunctionSync(IWebview pWebview, JSONArray array)
//    {
//        String inValue1 = array.optString(0);
//        String inValue2 = array.optString(1);
//        String inValue3 = array.optString(2);
//        String inValue4 = array.optString(3);
//
//        String ReturnValue = inValue1 + "-" + inValue2 + "-" + inValue3 + "-" + inValue4;
//        // 只能返回String类型到JS层。
//        return JSUtil.wrapJsVar(ReturnValue,true);
//    }
//    Function(IWebview pWebview, JSONArray array)
//
//
//    {
//
//
//        Log.e(TAG, String.format("webview = %s, jsonArray = %s", pWebview, array));
//        Toast.makeText(mApplicationContext, array.toString(), Toast.LENGTH_SHORT).show();
//       mApplicationContext.startActivity(new Intent(mApplicationContext,MainActivity.class));
//
//        // 原生代码中获取JS层传递的参数，
//    	// 参数的获取顺序与JS层传递的顺序一致
//        String CallBackID = array.optString(0);
//        JSONArray newArray = new JSONArray();
//        newArray.put(array.optString(1));
//        newArray.put(array.optString(2));
//        newArray.put(array.optString(3));
//        newArray.put(array.optString(4));
//        // 调用方法将原生代码的执行结果返回给js层并触发相应的JS层回调函数
//        JSUtil.execCallback(pWebview, CallBackID, newArray, JSUtil.OK, false);
//
//    }
//
//    public void PluginTestFunctionArrayArgu(IWebview pWebview, JSONArray array)
//    {
//        String ReturnString = null;
//        String CallBackID =  array.optString(0);
//        JSONArray newArray = null;
//        try {
//
//            newArray = new JSONArray( array.optString(1));
//            String inValue1 = newArray.getString(0);
//            String inValue2 = newArray.getString(1);
//            String inValue3 = newArray.getString(2);
//            String inValue4 = newArray.getString(3);
//            ReturnString = inValue1 + "-" + inValue2 + "-" + inValue3 + "-" + inValue4;
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        JSUtil.execCallback(pWebview, CallBackID, ReturnString, JSUtil.OK, false);
//    }
//
    public String PluginTestFunctionSyncArrayArgu(IWebview pWebview, JSONArray array) {
        JSONArray newArray = null;
        JSONObject retJSONObj = null;
        try {

            newArray = array.optJSONArray(0);
            String inValue1 = newArray.getString(0);
            String inValue2 = newArray.getString(1);
            String inValue3 = newArray.getString(2);
            String inValue4 = newArray.getString(3);

            retJSONObj = new JSONObject();
            retJSONObj.putOpt("RetArgu1", inValue1);
            retJSONObj.putOpt("RetArgu2", inValue2);
            retJSONObj.putOpt("RetArgu3", inValue3);
            retJSONObj.putOpt("RetArgu4", inValue4);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return JSUtil.wrapJsVar(retJSONObj);
    }

    public String PluginTestFunctionSync(IWebview pWebview, JSONArray array) {
        String inValue1 = array.optString(0);
        String inValue2 = array.optString(1);
        String inValue3 = array.optString(2);
        String inValue4 = array.optString(3);

        String ReturnValue = inValue1 + "-" + inValue2 + "-" + inValue3 + "-" + inValue4;
        // 只能返回String类型到JS层。
        return JSUtil.wrapJsVar(ReturnValue, true);
    }


}