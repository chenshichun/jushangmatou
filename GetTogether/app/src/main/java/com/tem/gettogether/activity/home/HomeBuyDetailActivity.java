package com.tem.gettogether.activity.home;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tem.gettogether.R;
import com.tem.gettogether.ShowImageDetail;
import com.tem.gettogether.activity.my.BuyMessageActivity;
import com.tem.gettogether.base.BaseActivity;
import com.tem.gettogether.base.BaseApplication;
import com.tem.gettogether.base.BaseConstant;
import com.tem.gettogether.base.BaseRVAdapter;
import com.tem.gettogether.base.BaseViewHolder;
import com.tem.gettogether.base.URLConstant;
import com.tem.gettogether.bean.WaiMaoQiuGouBean;
import com.tem.gettogether.rongyun.RongTalk;
import com.tem.gettogether.utils.SharedPreferencesUtils;
import com.tem.gettogether.utils.xutils3.MyCallBack;
import com.tem.gettogether.utils.xutils3.XUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.duduhuo.custoast.CusToast;

@ContentView(R.layout.recycler_qglb_iteam)
public class HomeBuyDetailActivity extends BaseActivity {
    @ViewInject(R.id.tv_name)
    private TextView tv_name;
    @ViewInject(R.id.tv_shopName)
    private TextView tv_shopName;
    @ViewInject(R.id.tv_shop_ms)
    private TextView tv_shop_ms;
    @ViewInject(R.id.tv_qglx)
    private TextView tv_qglx;
    @ViewInject(R.id.tv_jhTime)
    private TextView tv_jhTime;
    @ViewInject(R.id.tv_qgNum)
    private TextView tv_qgNum;
    @ViewInject(R.id.tv_fbTime)
    private TextView tv_fbTime;
    @ViewInject(R.id.recy_image)
    private RecyclerView recy_image;
    @ViewInject(R.id.tv_zxgt)
    private TextView tv_zxgt;

    private List<WaiMaoQiuGouBean.ResultEntity> waiMaoQiuGouBeans = new ArrayList<>();
    private String trade_id;

    @Override
    protected void initData() {
        x.view().inject(this);
        trade_id = getIntent().getStringExtra("trade_id");
        initDatas();
    }

    @Override
    protected void initView() {

    }

    private void initDatas() {
        Map<String, Object> map = new HashMap<>();
        String yuyan = SharedPreferencesUtils.getLanguageString(this, BaseConstant.SPConstant.language, "");
        if (yuyan != null) {
            map.put("language", yuyan);
        }
        map.put("trade_id", trade_id);
        Log.d("chenshichun","-----trade_id--"+trade_id);
        showDialog();
        XUtil.Post(URLConstant.HOMEQIUGOUDETAIL, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                closeDialog();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");

                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        WaiMaoQiuGouBean waiMaoQiuGouBean = gson.fromJson(result, WaiMaoQiuGouBean.class);
                        waiMaoQiuGouBeans = waiMaoQiuGouBean.getResult();
                        initViews();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
                closeDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                closeDialog();
                ex.printStackTrace();
            }
        });
    }

    private void initViews() {
        tv_name.setText(getResources().getText(R.string.user_name)+""+waiMaoQiuGouBeans.get(0).getMobile());
        tv_shopName.setText(getText(R.string.product_name) + waiMaoQiuGouBeans.get(0).getGoods_name());
        tv_shop_ms.setText(getText(R.string.chugouguojia) + waiMaoQiuGouBeans.get(0).getCountry_name());
        tv_qglx.setText(getText(R.string.buy_style) + waiMaoQiuGouBeans.get(0).getRelease_type());
        tv_jhTime.setText(getText(R.string.buy_time) + waiMaoQiuGouBeans.get(0).getAttach_time());
        tv_qgNum.setText(getText(R.string.purchase_quantity) + waiMaoQiuGouBeans.get(0).getGoods_num());
        tv_fbTime.setText(getText(R.string.release_time) + waiMaoQiuGouBeans.get(0).getAdd_time());

        tv_zxgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                        if (!SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.CHAT_ID, "0").equals("")) {

                            if (waiMaoQiuGouBeans != null && waiMaoQiuGouBeans.get(0).getUser_id() != null) {
                                RongTalk.doConnection(HomeBuyDetailActivity.this, SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.CHAT_ID, "0")
                                        , waiMaoQiuGouBeans.get(0).getUser_id(), "",
                                        "", "",null);
                            } else {
                                CusToast.showToast(getText(R.string.the_store_is_invalid));
                            }
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                    CusToast.showToast(getText(R.string.the_store_is_invalid));
                }
            }
        });

        recy_image.setLayoutManager(new GridLayoutManager(HomeBuyDetailActivity.this, 3, LinearLayoutManager.VERTICAL, false));
        if (waiMaoQiuGouBeans.get(0).getGoods_logo().size() > 0) {
            recy_image.setAdapter(new BaseRVAdapter(HomeBuyDetailActivity.this, waiMaoQiuGouBeans.get(0).getGoods_logo()) {
                @Override
                public int getLayoutId(int viewType) {
                    return R.layout.recyqgxx_image_item;
                }

                @Override
                public void onBind(BaseViewHolder holder, int position) {
                    ImageView iv_iamge_qg = holder.getImageView(R.id.iv_iamge_qg);
                    if(!waiMaoQiuGouBeans.get(0).getGoods_logo().get(position).equals("")) {
                        Glide.with(HomeBuyDetailActivity.this).load(waiMaoQiuGouBeans.get(0).getGoods_logo().get(position)).error(R.mipmap.myy322x).into(iv_iamge_qg);
                    }

                    iv_iamge_qg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HomeBuyDetailActivity.this, ShowImageDetail.class);
                            intent.putStringArrayListExtra("paths", (ArrayList<String>) waiMaoQiuGouBeans.get(0).getGoods_logo());
                            intent.putExtra("index", String.valueOf(waiMaoQiuGouBeans.get(0)));
                            startActivity(intent);
                        }
                    });
                }

            });

        }
    }

}
