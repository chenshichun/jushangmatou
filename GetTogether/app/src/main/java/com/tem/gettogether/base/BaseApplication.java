package com.tem.gettogether.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bugtags.library.Bugtags;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.tem.gettogether.bean.UserBean;
import com.tem.gettogether.bean.WeiXinBean1;
import com.tem.gettogether.bean.WeiXinMessageBean;
import com.tem.gettogether.rongyun.RongCloudEvent;
import com.tem.gettogether.utils.SharedPreferencesUtils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.youdao.sdk.app.YouDaoApplication;

import org.xutils.x;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import cc.duduhuo.custoast.CusToast;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.push.RongPushClient;
import io.rong.push.pushconfig.PushConfig;

/**
 * Created by pc on 2018/4/16.
 */

public class BaseApplication extends Application {
    public static Context context;
    public static BaseApplication mInstance;
    public UserBean userBean;
    public int isWXPay = 100;
    public final String WXAPP_ID = "wxa6f24ff3369c8d21";
    public IWXAPI api;
    public WeiXinBean1 wxbean;
    public WeiXinMessageBean bean;

    public static BaseApplication getInstance() {
        if (mInstance == null) {
            mInstance = (BaseApplication) context.getApplicationContext();
        }
        return mInstance;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        x.Ext.init(this);
        context = getApplicationContext();
        CusToast.init(this);
        api = WXAPIFactory.createWXAPI(this, WXAPP_ID);

        RongIMClient.init(this);
        PushConfig config = new PushConfig.Builder()
                .enableHWPush(true)
                .enableVivoPush(true)
//                .enableOppoPush("d672de8aaccd42d8b5c3e08652b54eb3", "97bbe55096b842b88a8f2f1eecbf018a")
                .enableMiPush("2882303761518019313", "5521801975313")
                .enableFCM(true)
                .build();
        RongPushClient.setPushConfig(config);
        RongIM.init(this);
        RongCloudEvent.init(this);
//        setInputProvider();
        UMShareAPI.get(this);
        initAppLanguage();
        Bugtags.start("42c655de1b4f612f3e488385c64f3e81", this, /*Bugtags.BTGInvocationEventBubble*/Bugtags.BTGInvocationEventNone);
        Beta.autoCheckUpgrade = true;//设置不自动检查
        Bugly.init(getApplicationContext(), "d3faa6cafc", false);// bugly

        /*有道翻译初始化*/
        if (YouDaoApplication.getApplicationContext() == null)
            YouDaoApplication.init(this, "086f7d9c1f5d5f84");

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        RongIMClient.connect(SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.CHAT_ID, "0"), new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

            }

            /**
             * 连接融云成功
             */
            @Override
            public void onSuccess(String userid) {
                Log.e("chenshichun", "---连接融云成功--");
            }

            /**
             * 连接融云失败
             */
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

    }

    {
        PlatformConfig.setQQZone("101557245", "2fe9d31228f7ccb88ffd26beb709d31e");
    }

    /*private void setInputProvider() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new ShopExtensionModule());
            }
        }
//        RongIM.registerMessageType(CustomizeMessage.class);
        RongIM.registerMessageType(CustomizeTranslationMessage.class);
        RongIM.registerMessageType(CustomizeBuyMessage.class);
//        RongIM.getInstance().registerMessageTemplate(new CustomizeMessageItemProvider());
        RongIM.getInstance().registerMessageTemplate(new CustomizeMessageTranslationItemProvider());
        RongIM.getInstance().registerMessageTemplate(new BuyCustomizeMessageItemProvider());
    }*/

    /**
     * 添加到销毁队列
     *
     * @param activity_distributor_rz 要销毁的activity
     */
    private static Map<String, Activity> destoryMap = new HashMap<>();

    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    public void removerUser() {
        Log.e("chenshichun", "--SharedPreferences---");
        SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        for (String key : keySet) {
            Activity activity = destoryMap.get(key);
            if (activity != null)
                activity.finish();

        }
    }

    public void initAppLanguage() {
        if (SharedPreferencesUtils.getLanguageString(this, BaseConstant.SPConstant.language, "").equals("zh")) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            Configuration config = getResources().getConfiguration();
            // 应用用户选择语言
            config.locale = Locale.CHINESE;
            getResources().updateConfiguration(config, dm);
        } else if (SharedPreferencesUtils.getLanguageString(this, BaseConstant.SPConstant.language, "").equals("en")) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            Configuration config = getResources().getConfiguration();
            // 应用用户选择语言
            config.locale = Locale.ENGLISH;
            getResources().updateConfiguration(config, dm);
        } else if (SharedPreferencesUtils.getLanguageString(this, BaseConstant.SPConstant.language, "").equals("ara")) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            Configuration config = getResources().getConfiguration();
            // 应用用户选择语言
            config.locale = new Locale("ar");
            getResources().updateConfiguration(config, dm);
        }
    }
}
