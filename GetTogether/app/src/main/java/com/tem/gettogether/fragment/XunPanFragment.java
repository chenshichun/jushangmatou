package com.tem.gettogether.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.Footer.LoadingView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.tem.gettogether.R;
import com.tem.gettogether.ShowImageDetail;
import com.tem.gettogether.activity.home.HomeBuyDetailNewActivity;
import com.tem.gettogether.adapter.XunPanAdapter;
import com.tem.gettogether.base.BaseActivity;
import com.tem.gettogether.base.BaseConstant;
import com.tem.gettogether.base.BaseFragment;
import com.tem.gettogether.base.URLConstant;
import com.tem.gettogether.bean.QiuGouListBean;
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

@ContentView(R.layout.fragment_xunpan)
public class XunPanFragment extends BaseFragment {
    @ViewInject(R.id.sell_RecyclerView)
    private RecyclerView sell_RecyclerView;
    @ViewInject(R.id.refreshLayout)
    private TwinklingRefreshLayout refreshLayout;
    @ViewInject(R.id.ll_empty)
    private RelativeLayout ll_empty;
    private XunPanAdapter mHomeBuyAdapter;
    private List<QiuGouListBean.ResultBean> homeDataBean = new ArrayList<>();
    private int currentPage = 1;
    private BaseActivity baseActivity;
    private int typePage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
        typePage = getArguments().getInt("page");
        initDatas(1, false);
        initRefresh();
    }


    private void initDatas(final int currentPage, final boolean isLoadMore) {
        Map<String, Object> map = new HashMap<>();
        String yuyan = SharedPreferencesUtils.getLanguageString(getContext(), BaseConstant.SPConstant.language, "");
        if (yuyan != null) {
            map.put("language", yuyan);
            map.put("page", currentPage);
        }
        map.put("user_id", SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.USERID, ""));
        map.put("type", (typePage + 1));
        map.put("list_row", 10);

        baseActivity.showDialog();
        XUtil.Post(URLConstant.HONEBUYDATA, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    Log.d("chenshichun", "======推送===typePage==" + typePage + " result:: " + jsonObject.optString("result"));
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
                baseActivity.closeDialog();
                refreshLayout.finishRefreshing();
                refreshLayout.finishLoadmore();
                super.onFinished();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                baseActivity.closeDialog();
                ex.printStackTrace();
            }
        });
    }


    private void setData() {
        mHomeBuyAdapter = new XunPanAdapter(getContext(), homeDataBean);
        sell_RecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sell_RecyclerView.setAdapter(mHomeBuyAdapter);
        mHomeBuyAdapter.setOnClickView(new XunPanAdapter.onClickView() {
            @Override
            public void onClick(int position) {
                if (homeDataBean.get(position).getStatus() != null && homeDataBean.get(position).getStatus().equals("2")) {
                    ArrayList<String> resultList = new ArrayList<>();
                    resultList.add(homeDataBean.get(position).getEnd_pic());
                    Intent intent = new Intent(getContext(), ShowImageDetail.class);
                    intent.putStringArrayListExtra("paths", resultList);
                    getContext().startActivity(intent);
                } else {
                    getContext().startActivity(new Intent(getContext(), HomeBuyDetailNewActivity.class)
                            .putExtra("trade_id", homeDataBean.get(position).getTrade_id())
                            .putExtra("witch_page", 1)
                            .putExtra("page", typePage));
                }
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
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                currentPage = 1;
                initDatas(currentPage, false);
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
            }
        });
    }
}
