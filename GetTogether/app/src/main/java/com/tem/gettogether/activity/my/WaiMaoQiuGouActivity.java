package com.tem.gettogether.activity.my;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.Footer.LoadingView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.tem.gettogether.R;
import com.tem.gettogether.activity.home.HomeBuyDetailNewActivity;
import com.tem.gettogether.adapter.HomeBuyAdapter;
import com.tem.gettogether.adapter.HomeBuyListAdapter;
import com.tem.gettogether.base.BaseActivity;
import com.tem.gettogether.base.BaseConstant;
import com.tem.gettogether.base.URLConstant;
import com.tem.gettogether.bean.QiuGouListBean;
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

@ContentView(R.layout.activity_waimaoqiugou)
public class WaiMaoQiuGouActivity extends BaseActivity {
    @ViewInject(R.id.sell_RecyclerView)
    private RecyclerView sell_RecyclerView;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.rl_close)
    private RelativeLayout rl_close;
    @ViewInject(R.id.status_bar_id)
    private View status_bar_id;
    @ViewInject(R.id.refreshLayout)
    private TwinklingRefreshLayout refreshLayout;
    @ViewInject(R.id.ll_empty)
    private RelativeLayout ll_empty;
    private HomeBuyListAdapter mHomeBuyAdapter;
    private List<QiuGouListBean.ResultBean> homeDataBean = new ArrayList<>();

    private int currentPage = 1;

    @Override
    protected void initData() {
        x.view().inject(this);
        StatusBarUtil.setTranslucentStatus(this);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) status_bar_id.getLayoutParams();
        linearParams.height = getStatusBarHeight(getContext());
        status_bar_id.setLayoutParams(linearParams);
        tv_title.setText(getResources().getText(R.string.waimaoqiugou));
        initDatas(1, false);
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

    private void initDatas(final int currentPage, final boolean isLoadMore) {
        Map<String, Object> map = new HashMap<>();
        String yuyan = SharedPreferencesUtils.getLanguageString(this, BaseConstant.SPConstant.language, "");
        if (yuyan != null) {
            map.put("language", yuyan);
            map.put("page", currentPage);
        }
        showDialog();
        XUtil.Post(URLConstant.HONEBUYDATA, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                closeDialog();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        if (!isLoadMore) {
                            homeDataBean = gson.fromJson(result, QiuGouListBean.class).getResult();
                            if (homeDataBean.size() == 0) {
                                ll_empty.setVisibility(View.VISIBLE);
                            } else {
                                ll_empty.setVisibility(View.GONE);
                                setData();
                            }
                        } else {
                            if (gson.fromJson(result, QiuGouListBean.class).getResult().size() > 0) {
                                homeDataBean.addAll(gson.fromJson(result, QiuGouListBean.class).getResult());
                                mHomeBuyAdapter.notifyDataSetChanged();
                            } else {
                                CusToast.showToast(getResources().getText(R.string.no_more_data));
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
                refreshLayout.finishRefreshing();
                refreshLayout.finishLoadmore();
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
        mHomeBuyAdapter = new HomeBuyListAdapter(getContext(), homeDataBean);
        sell_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sell_RecyclerView.setAdapter(mHomeBuyAdapter);
        mHomeBuyAdapter.setOnClickView(new HomeBuyListAdapter.onClickView() {
            @Override
            public void onClick(int position) {
                getContext().startActivity(new Intent(getContext(), HomeBuyDetailNewActivity.class)
                        .putExtra("trade_id", homeDataBean.get(position).getTrade_id())
                        .putExtra("witch_page", 1)
                        .putExtra("page", 0));
            }
        });
    }

    private void initRefresh() {
        SinaRefreshView headerView = new SinaRefreshView(getContext());
        headerView.setPullDownStr(getString(R.string.pull_down_refresh));
        headerView.setReleaseRefreshStr(getString(R.string.release_refresh));
        headerView.setRefreshingStr(getString(R.string.refreshing));
        headerView.setTextColor(0xff745D5C);
        refreshLayout.setHeaderView(headerView);
        LoadingView loadingView = new LoadingView(getContext());
        refreshLayout.setBottomView(loadingView);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                initDatas(1, false);
                currentPage = 1;
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                currentPage++;
                initDatas(currentPage, true);
            }

            @Override
            public void onFinishRefresh() {
                super.onFinishRefresh();
            }

            @Override
            public void onFinishLoadMore() {
                super.onFinishLoadMore();
                refreshLayout.setAutoLoadMore(true);
            }
        });
    }
}
