package com.jsmt.developer.activity.my;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.jsmt.developer.R;
import com.jsmt.developer.base.BaseActivity;
import com.jsmt.developer.base.BaseApplication;
import com.jsmt.developer.base.URLConstant;
import com.jsmt.developer.bean.CZJLBean;
import com.jsmt.developer.utils.ListUtils;
import com.jsmt.developer.utils.NetWorkUtils;
import com.jsmt.developer.utils.UiUtils;
import com.jsmt.developer.utils.xutils3.MyCallBack;
import com.jsmt.developer.utils.xutils3.XUtil;
import com.jsmt.developer.view.powerfulrecyclerview.HomeListFreshRecyclerView;

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
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

@ContentView(R.layout.activity_chong_zhi)
public class ChongZhiActivity extends BaseActivity {
    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    @ViewInject(R.id.order_rl)
    private HomeListFreshRecyclerView order_rl;
    @ViewInject(R.id.order_refresh_fragment)
    private BGARefreshLayout order_refresh_fragment;
    private int PAGE_NUM = 1;
    private List<CZJLBean.DataBean> list;
    private List<CZJLBean.DataBean> dataBeans=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initData();
        initView();
    }

    @Override
    protected void initView() {
        order_refresh_fragment.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                if (!NetWorkUtils.isNetworkAvailable(ChongZhiActivity.this)) {
                    if (order_refresh_fragment.getCurrentRefreshStatus() == BGARefreshLayout.RefreshStatus.REFRESHING) {
                        order_refresh_fragment.endRefreshing();
                    }
                    return;
                }
                PAGE_NUM=1;
                clearList(dataBeans);
                Map<String,Object> map3=new HashMap<>();
                map3.put("page",PAGE_NUM);
                map3.put("token", BaseApplication.getInstance().userBean.getToken());
                upCZJLData(map3);


            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                if (!NetWorkUtils.isNetworkAvailable(ChongZhiActivity.this)) {
                    CusToast.showToast( "请检查网络");
                    return false;
                }
                PAGE_NUM++;
                Map<String,Object> map3=new HashMap<>();
                map3.put("page",PAGE_NUM);
                map3.put("token", BaseApplication.getInstance().userBean.getToken());

                upCZJLData(map3);
                return true;
            }
        });
        order_rl.setLayoutManager(new GridLayoutManager(this, 1));
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新
        refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.color_F3F5F4);//背景色
        refreshViewHolder.setPullDownRefreshText("下拉加载");//下拉的提示文字
        refreshViewHolder.setReleaseRefreshText("松开加载");//松开的提示文字
        refreshViewHolder.setRefreshingText("加载中");//刷新中的提示文字

        // 设置下拉刷新和上拉加载更多的风格
        order_refresh_fragment.setRefreshViewHolder(refreshViewHolder);
        order_refresh_fragment.shouldHandleRecyclerViewLoadingMore(order_rl);


    }
    public void clearList(List<CZJLBean.DataBean> list) {
        if (!ListUtils.isEmpty(list)) {
            list.clear();
        }
    }
    private void  upCZJLData(Map<String,Object> map){

        XUtil.Post(URLConstant.CHONGZHI_JL,map,new MyCallBack<String>(){
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                order_refresh_fragment.endRefreshing();
                order_refresh_fragment.endLoadingMore();
                Log.i("====充值记录列表===", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");

                    if(res.equals("1")){
                        Gson gson=new Gson();
                        CZJLBean czjlBean=gson.fromJson(result,CZJLBean.class);
                        list=czjlBean.getData();
                        if (ListUtils.isEmpty(list)){
                            UiUtils.toast("没有更新的数据");
                            return;
                        }
                        dataBeans.addAll(list);
                    }else{
                        String msg = jsonObject.optString("msg");
                        CusToast.showToast(msg);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
                CZJKAdapter adapter=new CZJKAdapter(dataBeans);
                order_rl.setAdapter(adapter);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                ex.printStackTrace();

            }
        });
    }
    @Override
    protected void initData() {
        tv_title.setText("充值记录");
    }
    @Event(value = {R.id.rl_close}, type = View.OnClickListener.class)
    private void getEvent(View view) {
        switch (view.getId()) {
            case R.id.rl_close:
                finish();
                break;

        }
    }
    public class CZJKAdapter extends BaseQuickAdapter{

        public CZJKAdapter( List<CZJLBean.DataBean> data) {
            super( R.layout.recycler_wallet_iteam, data);
        }

        @Override
        protected void convert(com.chad.library.adapter.base.BaseViewHolder baseViewHolder, Object o) {
            baseViewHolder.setText(R.id.tv_time,dataBeans.get(baseViewHolder.getAdapterPosition()).getChange_time());
            baseViewHolder.setText(R.id.tv_czje,"+"+dataBeans.get(baseViewHolder.getAdapterPosition()).getPay_money());
        }
    }
}
