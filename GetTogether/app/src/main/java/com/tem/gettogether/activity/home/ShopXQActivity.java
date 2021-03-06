package com.tem.gettogether.activity.home;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tem.gettogether.R;
import com.tem.gettogether.base.BaseActivity;
import com.tem.gettogether.base.BaseApplication;
import com.tem.gettogether.base.BaseConstant;
import com.tem.gettogether.base.URLConstant;
import com.tem.gettogether.bean.ShopXQBean;
import com.tem.gettogether.rongyun.RongTalk;
import com.tem.gettogether.utils.SharedPreferencesUtils;
import com.tem.gettogether.utils.StatusBarUtil;
import com.tem.gettogether.utils.xutils3.MyCallBack;
import com.tem.gettogether.utils.xutils3.XUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import cc.duduhuo.custoast.CusToast;

@ContentView(R.layout.activity_shop_xq)
public class ShopXQActivity extends BaseActivity {
    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    @ViewInject(R.id.tv_shop_name)
    private TextView tv_shop_name;
    @ViewInject(R.id.tv_shop_pf)
    private TextView tv_shop_pf;
    @ViewInject(R.id.tv_gz_num)
    private TextView tv_gz_num;
    @ViewInject(R.id.tv_isgz)
    private TextView tv_isgz;
    @ViewInject(R.id.tv_shopping_num)
    private TextView tv_shopping_num;
    @ViewInject(R.id.tv_xp_num)
    private TextView tv_xp_num;
    @ViewInject(R.id.tv_pj_num)
    private TextView tv_pj_num;
    @ViewInject(R.id.tv_dt_num)
    private TextView tv_dt_num;
    @ViewInject(R.id.tv_swh)
    private TextView tv_swh;
    @ViewInject(R.id.tv_jyz)
    private TextView tv_jyz;
    @ViewInject(R.id.tv_bq)
    private TextView tv_bq;
    @ViewInject(R.id.tv_shop_jj)
    private TextView tv_shop_jj;
    @ViewInject(R.id.tv_shop_address)
    private TextView tv_shop_address;
    @ViewInject(R.id.tv_shop_time)
    private TextView tv_shop_time;
    @ViewInject(R.id.iv_gz)
    private ImageView iv_gz;
    @ViewInject(R.id.iv_shop_head)
    private ImageView iv_shop_head;
    @ViewInject(R.id.ll_lxdh)
    private LinearLayout ll_lxdh;
    @ViewInject(R.id.iv_image_4)
    private ImageView iv_image_4;
    @ViewInject(R.id.iv_image_5)
    private ImageView iv_image_5;
    @ViewInject(R.id.status_bar_id)
    private View status_bar_id;
    private String store_user_id;
    private String store_id;
    private String is_collect;
    private ShopXQBean.ResultBean resultBean = new ShopXQBean.ResultBean();

    @Override
    protected void initData() {
        x.view().inject(this);
        StatusBarUtil.setTranslucentStatus(this);
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) status_bar_id.getLayoutParams();
        linearParams.height = getStatusBarHeight(getContext());
        status_bar_id.setLayoutParams(linearParams);
        tv_title.setText(getText(R.string.store_details));
        store_id = getIntent().getStringExtra("store_id");
        is_collect = getIntent().getStringExtra("is_collect");
        store_user_id = getIntent().getStringExtra("store_user_id");
        upShopXQData();
    }

    @Override
    protected void initView() {

    }

    private void initviews() {

        tv_shop_name.setText(resultBean.getStore_name());
        tv_gz_num.setText(resultBean.getFcount() + getText(R.string.people));
        tv_shopping_num.setText(resultBean.getStore_count());
        tv_xp_num.setText(resultBean.getStore_new_count());
        tv_dt_num.setText(resultBean.getOcount());

        if (resultBean.getLevel().equals("7")) {
            tv_shop_pf.setText(getText(R.string.tourist));
        } else if (resultBean.getLevel().equals("1")) {
            tv_shop_pf.setText(getText(R.string.ordinary_member));
        } else if (resultBean.getLevel().equals("2")) {
            tv_shop_pf.setText(getText(R.string.senior_member));
        }

        tv_swh.setText("");
        tv_jyz.setText(resultBean.getContacts_name());
        Glide.with(this).load(resultBean.getStore_logo()).error(R.mipmap.myy322x).into(iv_shop_head);
        tv_bq.setText(resultBean.getContacts_mobile());
        tv_shop_jj.setText(resultBean.getSeo_description());
        tv_shop_address.setText(resultBean.getLocation());

        if (resultBean.getFactory_scene().get(0) != null) {
            Glide.with(getContext()).load(resultBean.getFactory_scene().get(0) + "").error(R.mipmap.myy322x).centerCrop().into(iv_image_4);
        }
        if (resultBean.getFactory_scene().get(1) != null) {
            Glide.with(getContext()).load(resultBean.getFactory_scene().get(1) + "").error(R.mipmap.myy322x).centerCrop().into(iv_image_5);
        }

    }


    @Event(value = {R.id.rl_close, R.id.rl_zhaq, R.id.ll_lxdh}, type = View.OnClickListener.class)
    private void getEvent(View view) {
        switch (view.getId()) {
            case R.id.rl_close:
                finish();
                break;
            case R.id.ll_lxdh:
                showPop(ll_lxdh);

                break;
            case R.id.rl_zhaq:
                if (SharedPreferencesUtils.getString(this, BaseConstant.SPConstant.ROLE_TYPE, "1").equals("1")) {
                    CusToast.showToast(getText(R.string.supplier_does_not_have_this_feature));
                    return;
                }
                try {

                    //发消息
                    if (!SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.CHAT_ID, "0").equals("")) {

                        if (resultBean != null && store_id != null) {
                            RongTalk.doConnection(ShopXQActivity.this, SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.CHAT_ID, "0")
                                    , store_id, resultBean.getStore_name(),
                                    resultBean.getStore_logo(), resultBean.getStore_id(), null);
                        } else {
                            CusToast.showToast(getText(R.string.the_store_is_invalid));
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    CusToast.showToast(getText(R.string.the_store_is_invalid));
                }
                break;
        }
    }

    private PopupWindow mPop;

    //初始化弹窗
    private void initPop() {
        if (mPop == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.pop_layout, null);
            mPop = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //点击弹窗外消失mPop
            mPop.setFocusable(true);
            mPop.setOutsideTouchable(true);
            //设置背景，才能使用动画效果
            mPop.setBackgroundDrawable(new BitmapDrawable());
            //设置动画
            mPop.setAnimationStyle(R.style.PopWindowAnim);
            //设置弹窗消失监听
            mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1f;
                    getWindow().setAttributes(lp);
                }
            });
            //设置弹窗内的点击事件
            setPopClickListener(view);
        }
    }

    //显示弹窗
    private void showPop(View v) {
        initPop();
        if (mPop.isShowing())
            return;
        //设置弹窗底部位置
        mPop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().setAttributes(lp);
    }

    private void setPopClickListener(View view) {
        TextView tv_iteam1, photo, cancle;
        photo = view.findViewById(R.id.photo);
        cancle = view.findViewById(R.id.cancle);
        tv_iteam1 = view.findViewById(R.id.tv_iteam1);
        tv_iteam1.setText(R.string.kefudian);
        photo.setText(getText(R.string.call) + resultBean.getContacts_mobile());
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri datav = Uri.parse(getText(R.string.tel_tv) + resultBean.getContacts_mobile());
                intent.setData(datav);
                startActivity(intent);
                mPop.dismiss();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPop.dismiss();
            }
        });
    }

    private void upShopXQData() {
        Map<String, Object> map = new HashMap<>();
        map.put("store_id", store_id);
        showDialog();
        XUtil.Post(URLConstant.SHOPXIANGQINGDATA, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                closeDialog();
                Log.i("====店铺详情===", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        ShopXQBean shopTopBean = gson.fromJson(result, ShopXQBean.class);
                        resultBean = shopTopBean.getResult();
                        initviews();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
                closeDialog();
                initView();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                closeDialog();

            }
        });
    }
}
