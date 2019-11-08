package com.example.andyyuan.androidmui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allenliu.versionchecklib.callback.OnCancelListener;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.NotificationBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadFailedListener;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadingDialogListener;
import com.allenliu.versionchecklib.v2.callback.CustomVersionDialogListener;
import com.google.gson.Gson;
import com.im.androidimcore.mobileim4a.core.LocalUDPDataSender;
import com.im.androidimcore.mobileimserver_dto.com.eva.framework.dto.DataFromServer;
import com.im.androidimcore.mobileimserver_dto.com.eva.framework.dto.LoginInfo2;
import com.im.androidimcore.mobileimserver_dto.com.x52im.imchat.http.dto.RosterElementEntity;
import com.im.eva.widget.DataLoadingAsyncTask;
import com.im.eva.widget.WidgetUtils;
import com.im.rainbowchat.MyApplication;
import com.im.rainbowchat.bean.AutoUpdateInfoFromServerBean;
import com.im.rainbowchat.logic.chat_friend.meta.BusinessIntelligence;
import com.im.rainbowchat.logic.chat_friend.vv.VVP2PProvider;
import com.im.rainbowchat.logic.launch.LoginActivity;
import com.im.rainbowchat.logic.launch.OnLoginIMServerDialogProgress;
import com.im.rainbowchat.network.http.HttpRestHelper;
import com.im.rainbowchat.utils.IntentFactory;
import com.im.utils.BaseDialog;
import com.im.utils.LoginUtils;
import com.im.utils.MD5Utils;
import com.im.utils.ToastUtils;
import com.im.utils.VersionManagementUtil;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by AndyYuan on time at 2019/11/7.
 */

public class MyLoginUtil {

    // 提交用户登陆信息认证和版本检查
    private Context context;
    private Context contexts;
    private String account;
    private String passWord;
    private LoginUtils loginUtils;

    //无效的登录信息，用户名或者密码输入错误
    public View popUpError;
    public TextView ErrorDialogText;
    public PopupWindow popupWindowError;
    private DownloadBuilder builder;

    public MyLoginUtil(Context context) {
        this.context = context;
    }


    public void setLogin(Context context, String account, String passWord, int versionCode) {
        this.context = context;
        this.contexts = context;
        this.account = account;
        this.passWord = passWord;
        //这里做网络的检查工作，如果没有网络则不能直接显示在启动页的白屏页面，要跳转到登录界面并加以提示
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(0);
        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(1);
        NetworkInfo ethernetInfo = connectMgr.getNetworkInfo(9);
        if ((mobNetInfo == null || !mobNetInfo.isConnected()) && (wifiNetInfo == null || !wifiNetInfo.isConnected()) && (ethernetInfo == null || !ethernetInfo.isConnected())) {
            Log.e("NetWorkConnection", "【IMCORE】【本地网络通知】检测本地网络连接断开了!");
            ToastUtils.show("网络请求处理失败！", Toast.LENGTH_SHORT);
            context.startActivity(IntentFactory.createLoginIntent(context));
        } else {
            new MyLoginUtil.Login$CheckUpdateAsyc().execute(constructLoginInfo(versionCode));
        }
    }


    private LoginInfo2 constructLoginInfo(int versionCode) {
        BusinessIntelligence bi = new BusinessIntelligence(context);
        String biObj = JSON.toJSONString(bi, true);
        final String uidOrMail = String.valueOf(account).trim().toLowerCase();
        LoginInfo2 ai = new LoginInfo2();
        ai.setLoginName(uidOrMail);
        String trim = String.valueOf(passWord).trim();
        String md5Pwd = MD5Utils.encode(trim);
        ai.setLoginPsw(md5Pwd);
//        ai.setClientVersion("" + getAPKVersionCode());//本字段时存放的就是客户端APK的真正版本码
        ai.setClientVersion("" + versionCode);
        ai.setDeviceInfo(biObj);
        ai.setOsType("0");// 0：Android客户端，1：iOS客户端

        return ai;
    }

    /**
     * 登陆认证和版本更新检查异步实现类。
     * <p>
     * 为了能减少一次独立的版本检查网络请求，所以将其合并到了登陆认证代码中，仅此而已。
     */
    protected class Login$CheckUpdateAsyc extends DataLoadingAsyncTask<Object, Integer, DataFromServer> {
        public Login$CheckUpdateAsyc() {
            super(contexts, $$(com.im.R.string.login_form_loading_login));
        }


        @Override
        protected DataFromServer doInBackground(Object... parems) {
            LoginInfo2 ai = (LoginInfo2) parems[0];
            return HttpRestHelper.submitLoginToServer(ai);
        }


        protected void onPostExecuteImpl(Object result) {
            if (result instanceof String) {
                JSONObject nwObj = JSONObject.parseObject((String) result);
                String updateInfoJson = nwObj.getString("update_info");
                String userInfoJson = nwObj.getString("authed_info");

                System.out.println("服务端返回>>updateInfoJson=" + updateInfoJson + ", userInfoJson=" + userInfoJson);

                // 登陆验证失败
                if (userInfoJson == null) {
                    // FIXBUG: google统计到较大量IllegalArgumentException (@LoginActivity$UpdateClientAsyncTask:onPostExecuteImpl:1075) {main}
                    //         错误，此if语句是为了保证延迟线程里不会因Activity已被关闭而此处却要非法地执行show的情况（此判断可趁为安全的show方法哦！）
                    if (((Activity) context) != null && !((Activity) context).isFinishing()) {
                        //跳出弹窗无效的登录信息，情输入正确的用户名和密码
                        initPopUpErrorDialog();//下面是之前定义的
//                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                }
                // 登陆验证成功
                else {
                    final AutoUpdateInfoFromServerBean aui = new Gson().fromJson(updateInfoJson, AutoUpdateInfoFromServerBean.class);
                    final RosterElementEntity userAuthedInfo = HttpRestHelper.parseLoginFromServer(userInfoJson);


                    //检查版本更新判断
                    if (aui.isNeedUpdate()) {
                        if (aui.getLatestClientAPKVercionCode() != null) {
                            String versionName = VersionManagementUtil.getVersionName((Activity) context);
                            if (VersionManagementUtil.VersionComparison(aui.getLatestClientAPKVercionCode(), versionName) == 1) {
                                String newVersionDowloadURL = aui.getLatestClientAPKURL();
                                String explain = aui.getExplain();
                                dialogShow((Activity) context, newVersionDowloadURL, userAuthedInfo, explain);
                            } else {
                                ToastUtils.showLong(com.im.R.string.this_is_latest_version);
                            }
                        } else {
                            return;
                        }
                    } else {
                        // 没有强制更新需求，无条件连接IM服务器
                        //---------------------------------------------------------------------------- [1]处代码与[2]是一样的 S
                        // 把本地用户信息保存起来备用哦
                        MyApplication.getInstance(context)
                                .getIMClientManager().setLocalUserInfo(userAuthedInfo);

                        // 更新检查（处理）完成后开始进入连接聊天服务器的过程
                        doLoginIMServer(userAuthedInfo.getUser_uid(), userAuthedInfo.getToken());
                        //---------------------------------------------------------------------------- [1]处代码与[2]是一样的 E
                    }
                    //---------------------------------------------------------------------------- [1]处代码与[2]是一样的 E
//                    }
                }
            } else {
//                Log.e(TAG, "服务端返回了无效的登陆反馈信息，result=" + result);
            }
        }
    }


    /**
     * 本方法是本IM中唯一正确的连接到IM服务器的途径。
     *
     * @param loginUserId 用于连接IM服务器时作为唯一用户id使用
     * @param loginToken  用于连接IM服务器时作为身份验证之用（此token通常由先前的
     *                    SSO单点登陆接口返回并定义接下来的验证策略）
     */
    @SuppressLint("StaticFieldLeak")
    protected void doLoginIMServer(final String loginUserId, final String loginToken) {
        // * 发送IM连接请求
        new LocalUDPDataSender.SendLoginDataAsync(context
                , loginUserId, loginToken) {
            @Override
            protected void fireAfterSendLogin(int code) {
                if (code == 0) {
                    // * 显示登陆连接IM的进度条
                    OnLoginIMServerDialogProgress.getInstance((Activity) context).showProgressDialogForPairing(true);
                    // 当登陆连接IM时间超过超时间后就判定为本次登陆超时，超时时将通知本观察者
                    OnLoginIMServerDialogProgress.getInstance((Activity) context).setRetryObsrver(new Observer() {
                        @Override
                        public void update(Observable observable, Object data) {
                            // 本观察者中由用户选择是否重试连接或者取消连接重试
                            new AlertDialog.Builder(context)
                                    .setTitle($$(com.im.R.string.login_form_connect_to_chatserver_timeout_title))
                                    .setMessage($$(com.im.R.string.login_form_connect_to_chatserver_timeout_content))
                                    .setPositiveButton($$(com.im.R.string.login_form_connect_to_chatserver_timeout_retry), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 重试时（再次尝试连接哦）
                                            doLoginIMServer(loginUserId, loginToken);
                                        }
                                    })
                                    .setNegativeButton($$(com.im.R.string.general_cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // * 取消显示连接进度条
                                            OnLoginIMServerDialogProgress.getInstance((Activity) context).showProgressDialogForPairing(false);
                                        }
                                    })
                                    .show();
                        }
                    });
                    // * 设置登陆连接IM界面的观察者（当客户端收到服务端反馈过来的IM连接成功消息时将被通知）
                    MyApplication.getInstance((Activity) context).getIMClientManager().getBaseEventListener()
                            .setLoginOkForLaunchObserver(new Observer() {
                                @Override
                                public void update(Observable observable, Object data) {
                                    // * 取消显示连接IM进度条
                                    OnLoginIMServerDialogProgress.getInstance((Activity) context).showProgressDialogForPairing(false);

                                    int code = (Integer) data;
                                    // IM连接成功
                                    if (code == 0) {
                                        afterLoginIMServerSucess();
                                    } else {
                                        new AlertDialog.Builder((Activity) context)
                                                .setTitle(com.im.R.string.general_error)
                                                .setMessage("Connected to chat server failure(" + code + ")")
                                                .setPositiveButton(com.im.R.string.general_ok, null).show();
                                    }
                                }
                            });

                    //
//					Toast.makeText(getApplicationContext(), R.string.login_form_send_data_successful, Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "登陆IM服务器的信息已成功发出！");
                } else {
//					Toast.makeText(getApplicationContext(), "数据发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
                    WidgetUtils.showToast(context, "Connect failed, please check your network.", WidgetUtils.ToastType.ERROR);
                    // * 出错时当然无条件取消显示连接IM的进度条
                    OnLoginIMServerDialogProgress.getInstance((Activity) context).showProgressDialogForPairing(false);
                }
            }
        }.execute();
    }

    /**
     * 用户成功登陆后将立即调用本方法.
     * 默认情况下本方法什么也不做，子类可自行实现之.
     */
    protected void afterLoginIMServerSucess() {

        // 前往门户主页
        //context.startActivity(IntentFactory.createPortalIntent(((Activity) context)));//IntentFactory.createChatIntent(LoginActivity.this));
        Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
        //############################################################################ 新音视频框架！ S
        try // TODO 此try catch要删除哦，目前是为了避免新音视频框架中的bug而临时为之（就是错误地转换ipv6地址的bug）
        {
            // 登陆音视频聊天服务器
            VVP2PProvider.getInstance(MyApplication.getInstance(context))
                    .login(MyApplication.getInstance(context).getIMClientManager().getLocalUserInfo().getUser_uid());
        } catch (Exception e) {
        }
        //############################################################################ 新音视频框架！ E

        //登录成功
//        ((Activity) context).finish();
    }

    public String $$(int id) {
        return context.getResources().getString(id);
    }

    /**
     * 点击删除聊天，退出群聊按钮
     */
    private void initPopUpErrorDialog() {
        initError();
        showPopupError();
    }

    private void showPopupError() {
        if (popupWindowError.isShowing()) {
            popupWindowError.dismiss();
        }
        popupWindowError.showAtLocation(popUpError, Gravity.BOTTOM, 0, 0);
    }

    private void initError() {
        popUpError = LayoutInflater.from(context).inflate(com.im.R.layout.pop_login_error_dialog, null);
        popupWindowError = new PopupWindow(popUpError, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindowError.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        initOutEvent();
    }

    public void initOutEvent() {
        //点击事件

        popUpError.findViewById(com.im.R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowError.dismiss();
            }
        });
        popUpError.findViewById(com.im.R.id.sure_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowError.dismiss();
                context.startActivity(new Intent((Activity) context, LoginActivity.class));

            }
        });
    }

    private void dialogShow(Activity activity, String newVersionDowloadURL, final RosterElementEntity userAuthedInfo, String explain) {

        builder = AllenVersionChecker
                .getInstance()
                .downloadOnly(crateUIData(newVersionDowloadURL, explain));
        builder.setShowDownloadingDialog(true);
        builder.setShowNotification(true);
        builder.setNotificationBuilder(createCustomNotification());
        builder.setShowDownloadFailDialog(true);
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel() {
                // 没有强制更新需求，无条件连接IM服务器
                //---------------------------------------------------------------------------- [2]处代码与[1]是一样的 S
                // 把本地用户信息保存起来备用哦
                MyApplication.getInstance(context)
                        .getIMClientManager().setLocalUserInfo(userAuthedInfo);

                // 更新检查（处理）完成后开始进入连接聊天服务器的过程
                doLoginIMServer(userAuthedInfo.getUser_uid(), userAuthedInfo.getToken());
                //---------------------------------------------------------------------------- [2]处代码与[1]是一样的 E

            }
        });
        builder.setCustomVersionDialogListener(createCustomDialogTwo());
        builder.setCustomDownloadingDialogListener(createCustomDownloadingDialog());
        builder.setCustomDownloadFailedListener(createCustomDownloadFailedDialog());

        builder.setDownloadAPKPath(Environment.getExternalStorageDirectory() + "/AppUpdate/");

        builder.excuteMission(activity);
    }

    private NotificationBuilder createCustomNotification() {
        return NotificationBuilder.create()
                .setRingtone(true)
                .setIcon(com.im.R.mipmap.ic_launcher)
                .setTicker("custom_ticker")
                .setContentTitle(context.getString(com.im.R.string.app_name))
                .setContentText(context.getString(com.im.R.string.custom_content_text));
    }

    /**
     * 务必用库传回来的context 实例化你的dialog
     *
     * @return
     */
    private CustomDownloadFailedListener createCustomDownloadFailedDialog() {
        return (context, versionBundle) -> {
            BaseDialog baseDialog = new BaseDialog(context, com.im.R.style.BaseDialog, com.im.R.layout.custom_download_failed_dialog);
            return baseDialog;
        };
    }

    private CustomVersionDialogListener createCustomDialogTwo() {
        return (context, versionBundle) -> {
            BaseDialog baseDialog = new BaseDialog(context, com.im.R.style.BaseDialog, com.im.R.layout.custom_download_dialog_layout);
            TextView textView = baseDialog.findViewById(com.im.R.id.tv_msg);
            textView.setText(versionBundle.getContent());
            baseDialog.setCanceledOnTouchOutside(true);
            return baseDialog;
        };
    }

    /**
     * 自定义下载中对话框，下载中会连续回调此方法 updateUI
     * 务必用库传回来的context 实例化你的dialog
     *
     * @return
     */
    private CustomDownloadingDialogListener createCustomDownloadingDialog() {
        return new CustomDownloadingDialogListener() {
            @Override
            public Dialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle) {
                BaseDialog baseDialog = new BaseDialog(context, com.im.R.style.BaseDialog, com.im.R.layout.custom_download_layout);
                return baseDialog;
            }

            @Override
            public void updateUI(Dialog dialog, int progress, UIData versionBundle) {
                TextView tvProgress = dialog.findViewById(com.im.R.id.tv_progress);
                ProgressBar progressBar = dialog.findViewById(com.im.R.id.pb);
                progressBar.setProgress(progress);
                tvProgress.setText(context.getString(com.im.R.string.versionchecklib_progress, progress));
            }
        };
    }

    /**
     * @return
     * @important 使用请求版本功能，可以在这里设置downloadUrl
     * 这里可以构造UI需要显示的数据
     * UIData 内部是一个Bundle
     */
    private UIData crateUIData(String newVersionDowloadURL, String explain) {
        UIData uiData = UIData.create();
        uiData.setTitle(context.getString(com.im.R.string.login_form_have_latest_version));
        uiData.setDownloadUrl(newVersionDowloadURL);
        uiData.setContent(explain);
        return uiData;
    }
}
