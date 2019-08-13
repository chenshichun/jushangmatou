package com.tem.gettogether.activity.home;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.Footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.tem.gettogether.R;
import com.tem.gettogether.adapter.HomeHotSellSecondAdapter;
import com.tem.gettogether.adapter.HomeLianMengSecondAdapter;
import com.tem.gettogether.base.BaseActivity;
import com.tem.gettogether.base.BaseConstant;
import com.tem.gettogether.base.URLConstant;
import com.tem.gettogether.bean.HomeHotSellBean;
import com.tem.gettogether.bean.HomeLianMengBean;
import com.tem.gettogether.utils.SharedPreferencesUtils;
import com.tem.gettogether.utils.xutils3.MyCallBack;
import com.tem.gettogether.utils.xutils3.XUtil;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

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

@ContentView(R.layout.activity_home_lianmeng)
public class HomeLianMengActivity extends BaseActivity {
    @ViewInject(R.id.sell_RecyclerView)
    private RecyclerView sell_RecyclerView;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.rl_close)
    private RelativeLayout rl_close;
    @ViewInject(R.id.refreshLayout)
    private TwinklingRefreshLayout refreshLayout;

    private HomeLianMengSecondAdapter mHomeLianMengSecondAdapter;
    private List<HomeLianMengBean.ResultEntity> homeDataBean = new ArrayList<>();
    private int currentPage = 1;

    @Override
    protected void initData() {
        x.view().inject(this);
        tv_title.setText(getResources().getText(R.string.waimaolianmeng));
        initDatas(1, true, false);
        initRefresh();
    }

    @Override
    protected void initView() {

    }

    @Event(value = {R.id.rl_close}, type = View.OnClickListener.class)
    private void getEvent(View view) {
        switch (view.getId()) {
            case R.id.rl_close:
                finish();
                break;
        }
    }

    private void initDatas(final int currentPage,final boolean isNormal, final boolean isLoadMore) {
        Map<String, Object> map = new HashMap<>();
        String yuyan = SharedPreferencesUtils.getString(this, BaseConstant.SPConstant.language, "");
        if (yuyan != null) {
            map.put("language", yuyan);
            map.put("page", currentPage);
        }
        showDialog();
        XUtil.Post(URLConstant.HONEALLIANCEDATA, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                closeDialog();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        if (isNormal) {
                            homeDataBean = gson.fromJson(result, HomeLianMengBean.class).getResult();
                            setData();
                        } else {
                            if(isLoadMore){

                                if(gson.fromJson(result, HomeLianMengBean.class).getResult().size()>0) {
                                    homeDataBean.addAll(gson.fromJson(result, HomeLianMengBean.class).getResult());
                                    mHomeLianMengSecondAdapter.notifyDataSetChanged();
                                }
                            }else{
                                    homeDataBean.clear();
                                    homeDataBean.addAll(gson.fromJson(result, HomeLianMengBean.class).getResult());
                                    mHomeLianMengSecondAdapter.notifyDataSetChanged();
                            }
                        }
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


    private void setData() {
        mHomeLianMengSecondAdapter = new HomeLianMengSecondAdapter(getContext(), homeDataBean);
        sell_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sell_RecyclerView.setAdapter(mHomeLianMengSecondAdapter);
    }

    private void initRefresh() {
        SinaRefreshView headerView = new SinaRefreshView(getContext());
        headerView.setTextColor(0xff745D5C);
        refreshLayout.setHeaderView(headerView);
        LoadingView loadingView = new LoadingView(getContext());
        refreshLayout.setBottomView(loadingView);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                initDatas(1, false,false);
                refreshLayout.finishRefreshing();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                currentPage++;
                initDatas(currentPage, false,true);
                refreshLayout.finishLoadmore();
            }

            @Override
            public void onFinishRefresh() {
                super.onFinishRefresh();
            }

            @Override
            public void onFinishLoadMore() {
                super.onFinishLoadMore();
            }
        });
    }

}