package com.tem.gettogether.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tem.gettogether.R;
import com.tem.gettogether.base.BaseActivity;
import com.tem.gettogether.base.BaseApplication;
import com.tem.gettogether.base.BaseConstant;
import com.tem.gettogether.base.BaseRVAdapter;
import com.tem.gettogether.base.BaseViewHolder;
import com.tem.gettogether.base.URLConstant;
import com.tem.gettogether.bean.MyAddressBean;
import com.tem.gettogether.dialog.Effectstype;
import com.tem.gettogether.dialog.LogoutDialogBuilder;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.duduhuo.custoast.CusToast;

@ContentView(R.layout.activity_address_gl)
public class AddressGLActivity extends BaseActivity {
    @ViewInject(R.id.recyclerView_address)
    private RecyclerView recyclerView_address;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.status_bar_id)
    private View status_bar_id;
    private Effectstype effect;
    private List<MyAddressBean.ResultBean> resultBeans = new ArrayList<>();
    private String cart_ids;
    private int ADDRESS = 101;
    private String goods_id, unique_id, goods_num, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        StatusBarUtil.setTranslucentStatus(this);
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) status_bar_id.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.height = getStatusBarHeight(getContext());
        status_bar_id.setLayoutParams(linearParams);
        initData();
    }

    @Override
    protected void initData() {
        tv_title.setText(R.string.address);
        cart_ids = getIntent().getStringExtra("cart_ids");
        goods_id = getIntent().getStringExtra("goods_id");
        unique_id = getIntent().getStringExtra("unique_id");
        goods_num = getIntent().getStringExtra("goods_num");
        key = getIntent().getStringExtra("key");
    }

    @Override
    protected void initView() {

        recyclerView_address.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView_address.setAdapter(new BaseRVAdapter(this, resultBeans) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.recycler_address_iteam;
            }

            @Override
            public void onBind(BaseViewHolder holder, final int position) {
                TextView tv_set_Address = holder.getTextView(R.id.tv_set_Address);
                ImageView iv_address_xz = holder.getImageView(R.id.iv_address_xz);
                holder.getTextView(R.id.tv_name).setText(resultBeans.get(position).getConsignee());
                holder.getTextView(R.id.tv_phone).setText(resultBeans.get(position).getMobile());
                holder.getTextView(R.id.tv_address).setText(/*resultBeans.get(position).getProvince() + resultBeans.get(position).getCity() + resultBeans.get(position).getDistrict() + resultBeans.get(position).getTwon() +*/ resultBeans.get(position).getAddress());

                if (resultBeans.get(position).getIs_default().equals("1")) {
                    tv_set_Address.setText(getText(R.string.default_address));
                    tv_set_Address.setTextColor(getResources().getColor(R.color.my_address_xz));
                    iv_address_xz.setVisibility(View.VISIBLE);
                    holder.getTextView(R.id.tv_address_xz).setVisibility(View.GONE);
                }
                holder.getView(R.id.ll_set_mr).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        upSetMoAddressData(resultBeans.get(position).getAddress_id());
                    }
                });
                holder.getTextView(R.id.tv_bianji).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AddressGLActivity.this, AddAddressActivity.class)
                                .putExtra("address_id", resultBeans.get(position).getAddress_id()));
                    }
                });
                holder.getTextView(R.id.tv_clier).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final LogoutDialogBuilder dialogLogout = new LogoutDialogBuilder(AddressGLActivity.this, R.style.dialog_untran);
                        effect = Effectstype.SHAKE;
                        dialogLogout.isvisibiliby();
                        dialogLogout
                                .withTitleColor("#333333")                                  //def
                                .withDividerColor("#333333")                              //def
                                .withMessage(getText(R.string.sure_delete_address))                     //.withMessage(null)  no Msg
                                .withMessageColor("#333333")                                //def
                                //.withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                                .withDuration(0)                                          //def    数值约大动画越明显
                                .withEffect(effect)                                         //def Effectstype.Slidetop
                                .withButton1Text(getText(R.string.quxiao))
                                .withButton2Text(getText(R.string.queding))
                                .setButton1Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogLogout.dismiss();
                                    }
                                })
                                .setButton2Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        upRemoveAddressData(resultBeans.get(position).getAddress_id());
                                        dialogLogout.dismiss();

                                    }
                                })
                                .show();

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (unique_id != null && !unique_id.equals("")) {
                            if (unique_id.equals("2")) {
                                setResult(ADDRESS, new Intent().putExtra("unique_id", unique_id)
                                        .putExtra("cart_ids", cart_ids));
                            } else {
                                setResult(ADDRESS, new Intent().putExtra("unique_id", unique_id)
                                        .putExtra("goods_id", goods_id)
                                        .putExtra("goods_num", goods_num)
                                        .putExtra("key", key));

                            }
                            finish();
                        } else {
                            finish();
                        }
                    }
                });
            }


        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        upGetAddressData();
    }

    @Event(value = {R.id.rl_close, R.id.tv_add_address}, type = View.OnClickListener.class)
    private void getEvent(View view) {
        switch (view.getId()) {
            case R.id.rl_close:
                if (unique_id != null && !unique_id.equals("")) {
                    if (unique_id.equals("2")) {
                        setResult(ADDRESS, new Intent().putExtra("unique_id", unique_id)
                                .putExtra("cart_ids", cart_ids));
                    } else {
                        setResult(ADDRESS, new Intent().putExtra("unique_id", unique_id)
                                .putExtra("goods_id", goods_id)
                                .putExtra("goods_num", goods_num)
                                .putExtra("key", key));

                    }
                    finish();
                } else {
                    finish();
                }
                break;
            case R.id.tv_add_address:
                startActivity(new Intent(this, AddAddressActivity.class));
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (cart_ids != null && !cart_ids.equals("")) {
                if (unique_id.equals("2")) {
                    setResult(ADDRESS, new Intent().putExtra("unique_id", unique_id)
                            .putExtra("cart_ids", cart_ids));
                } else {
                    setResult(ADDRESS, new Intent().putExtra("unique_id", unique_id)
                            .putExtra("goods_id", goods_id)
                            .putExtra("goods_num", goods_num)
                            .putExtra("key", key));

                }
                finish();
            } else {
                finish();
            }
        }
        return true;
    }

    private void upGetAddressData() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.TOKEN, ""));

        showDialog();
        XUtil.Post(URLConstant.GEI_ADDRESS_LIEBIAO, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                closeDialog();
                Log.i("====获取地址列表===", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        MyAddressBean myAddressBean = gson.fromJson(result, MyAddressBean.class);
                        resultBeans = myAddressBean.getResult();
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

    private void upSetMoAddressData(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.TOKEN, ""));

        map.put("address_id", id);
        showDialog();
        XUtil.Post(URLConstant.ADDRESS_SETTINGMOREN, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                closeDialog();
                Log.i("====设置默认地址===", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    String msg = jsonObject.optString("msg");
                    CusToast.showToast(msg);
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        upGetAddressData();
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

            }
        });
    }

    private void upRemoveAddressData(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.TOKEN, ""));
        map.put("id", id);

        showDialog();
        XUtil.Post(URLConstant.REMOVE_ADDRESS_LIEBIAO, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                closeDialog();
                Log.i("====删除地址===", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    String msg = jsonObject.optString("msg");
                    CusToast.showToast(msg);
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        upGetAddressData();
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

            }
        });
    }
}
