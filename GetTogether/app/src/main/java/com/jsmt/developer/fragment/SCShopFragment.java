package com.jsmt.developer.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.jsmt.developer.R;
import com.jsmt.developer.activity.home.ShopActivity;
import com.jsmt.developer.base.BaseActivity;
import com.jsmt.developer.base.BaseApplication;
import com.jsmt.developer.base.BaseFragment;
import com.jsmt.developer.base.URLConstant;
import com.jsmt.developer.bean.ShopLBBean;
import com.jsmt.developer.dialog.Effectstype;
import com.jsmt.developer.dialog.LogoutDialogBuilder;
import com.jsmt.developer.utils.ListUtils;
import com.jsmt.developer.utils.NetWorkUtils;
import com.jsmt.developer.utils.UiUtils;
import com.jsmt.developer.utils.xutils3.MyCallBack;
import com.jsmt.developer.utils.xutils3.XUtil;
import com.jsmt.developer.view.RoundImageView;
import com.jsmt.developer.view.powerfulrecyclerview.HomeListFreshRecyclerView;

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
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_shop_d)
public class SCShopFragment extends BaseFragment {
    @ViewInject(R.id.order_rl)
    private HomeListFreshRecyclerView order_rl;
    @ViewInject(R.id.order_refresh_fragment)
    private BGARefreshLayout order_refresh_fragment;
    private int PAGE_NUM = 1;
    private BaseActivity baseActivity;
    public static SCShopFragment newInstance() {
        return new SCShopFragment();
    }
    private List<ShopLBBean.ResultBean> resultBeans=new ArrayList<>();
    private List<ShopLBBean.ResultBean> list;
    private Effectstype effect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return x.view().inject(this, inflater, container);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        baseActivity= (BaseActivity) getActivity();
        initView();
        upShopLBData(1);
        super.onActivityCreated(savedInstanceState);
    }

    public void initView() {
        order_refresh_fragment.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                if (!NetWorkUtils.isNetworkAvailable(getContext())) {
                    if (order_refresh_fragment.getCurrentRefreshStatus() == BGARefreshLayout.RefreshStatus.REFRESHING) {
                        order_refresh_fragment.endRefreshing();
                    }
                    return;
                }
                PAGE_NUM=1;
                clearList(resultBeans);
                upShopLBData(PAGE_NUM);

            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                if (!NetWorkUtils.isNetworkAvailable(getContext())) {
                    CusToast.showToast( "请检查网络");
                    return false;
                }
                PAGE_NUM++;
                upShopLBData(PAGE_NUM);
                return true;
            }
        });
        order_rl.setLayoutManager(new GridLayoutManager(getContext(), 1));
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        // 设置下拉刷新
        refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.color_F3F5F4);//背景色
        refreshViewHolder.setPullDownRefreshText("下拉加载");//下拉的提示文字
        refreshViewHolder.setReleaseRefreshText("松开加载");//松开的提示文字
        refreshViewHolder.setRefreshingText("加载中");//刷新中的提示文字

        // 设置下拉刷新和上拉加载更多的风格
        order_refresh_fragment.setRefreshViewHolder(refreshViewHolder);
        order_refresh_fragment.shouldHandleRecyclerViewLoadingMore(order_rl);

    }
    public void clearList(List<ShopLBBean.ResultBean> list) {
        if (!ListUtils.isEmpty(list)) {
            list.clear();
        }
    }
    public class ShoppDPAdapter extends BaseQuickAdapter {

        public ShoppDPAdapter(List<ShopLBBean.ResultBean> data) {
            super(R.layout.shopping_dp_item, data);
        }

        @Override
        protected void convert(final BaseViewHolder baseViewHolder, Object o) {
            RoundImageView iv_shop_head=baseViewHolder.getView(R.id.iv_shop_head);
            RoundImageView iv_image1=baseViewHolder.getView(R.id.iv_image1);
            RoundImageView iv_image2=baseViewHolder.getView(R.id.iv_image2);
            RoundImageView iv_image3=baseViewHolder.getView(R.id.iv_image3);
            baseViewHolder.setText(R.id.tv_name,resultBeans.get(baseViewHolder.getAdapterPosition()).getStore_name());
            baseViewHolder.setText(R.id.tv_address,resultBeans.get(baseViewHolder.getAdapterPosition()).getStore_address());
            Glide.with(getActivity()).load(resultBeans.get(baseViewHolder.getAdapterPosition()).getStore_logo()).error(R.mipmap.myy322x).into(iv_shop_head);
            if(resultBeans.get(baseViewHolder.getAdapterPosition()).getGoodsList().size()==1){
                Glide.with(getActivity()).load(resultBeans.get(baseViewHolder.getAdapterPosition()).getGoodsList().get(0).getGoods_logo()).error(R.mipmap.myy322x).into(iv_image1);
                iv_image2.setVisibility(View.GONE);
                iv_image3.setVisibility(View.GONE);
            }else if(resultBeans.get(baseViewHolder.getAdapterPosition()).getGoodsList().size()==2){
                Glide.with(getActivity()).load(resultBeans.get(baseViewHolder.getAdapterPosition()).getGoodsList().get(0).getGoods_logo()).error(R.mipmap.myy322x).into(iv_image1);
                Glide.with(getActivity()).load(resultBeans.get(baseViewHolder.getAdapterPosition()).getGoodsList().get(1).getGoods_logo()).error(R.mipmap.myy322x).into(iv_image2);
                iv_image3.setVisibility(View.GONE);
            } else if(resultBeans.get(baseViewHolder.getAdapterPosition()).getGoodsList().size()>=3){
                Glide.with(getActivity()).load(resultBeans.get(baseViewHolder.getAdapterPosition()).getGoodsList().get(0).getGoods_logo()).error(R.mipmap.myy322x).into(iv_image1);
                Glide.with(getActivity()).load(resultBeans.get(baseViewHolder.getAdapterPosition()).getGoodsList().get(1).getGoods_logo()).error(R.mipmap.myy322x).into(iv_image2);
                Glide.with(getActivity()).load(resultBeans.get(baseViewHolder.getAdapterPosition()).getGoodsList().get(2).getGoods_logo()).error(R.mipmap.myy322x).into(iv_image3);

            }else{
                iv_image1.setVisibility(View.GONE);
                iv_image2.setVisibility(View.GONE);
                iv_image3.setVisibility(View.GONE);
            }
            baseViewHolder.getView(R.id.tv_in_Shop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(getActivity(), ShopActivity.class)
                            .putExtra("store_id",resultBeans.get(baseViewHolder.getAdapterPosition()).getStore_id())
                            .putExtra("type", ShopActivity.SHOPNHOME_TYPE), ShopActivity.SHOPNHOME_TYPE);
                }
            });
            baseViewHolder.getView(R.id.iv_shop_head).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    removeShop(resultBeans.get(baseViewHolder.getAdapterPosition()).getStore_id());
                    return true;
                }
            });
            baseViewHolder.getView(R.id.ll_remove).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    removeShop(resultBeans.get(baseViewHolder.getAdapterPosition()).getStore_id());
                    return true;
                }
            });
            baseViewHolder.getView(R.id.ll_remove2).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    removeShop(resultBeans.get(baseViewHolder.getAdapterPosition()).getStore_id());
                    return true;
                }
            });

        }
    }
    private void removeShop(final String store_id){
        final LogoutDialogBuilder dialogLogout = new LogoutDialogBuilder(getActivity(), R.style.dialog_untran);
        effect = Effectstype.SHAKE;
        dialogLogout.isvisibiliby();
        dialogLogout
                .withTitleColor("#333333")                                  //def
                .withDividerColor("#333333")                              //def
                .withMessage("确定删除该店铺吗？")                     //.withMessage(null)  no Msg
                .withMessageColor("#333333")                                //def
                //.withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withDuration(0)                                          //def    数值约大动画越明显
                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("取消")
                .withButton2Text("确定")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogLogout.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        upisGZData(store_id);
                        dialogLogout.dismiss();

                    }
                })
                .show();
    }
    private void upisGZData(String store_id) {
        Map<String, Object> map = new HashMap<>();
        map.put("store_id", store_id);
        if (BaseApplication.getInstance().userBean == null) return;
        map.put("token", BaseApplication.getInstance().userBean.getToken());
        XUtil.Post(URLConstant.SHOPISGUANZHU, map, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    String msg = jsonObject.optString("msg");
                    Log.i("====店铺删除关注===", result + msg);
                    if (res.equals("1")) {
                        Gson gson = new Gson();
                        CusToast.showToast("删除成功");
                        upShopLBData(PAGE_NUM);
                    }else{
                        CusToast.showToast(msg);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);

            }
        });
    }

    private void upShopLBData(int page){
        Map<String,Object> map=new HashMap<>();
        map.put("token", BaseApplication.getInstance().userBean.getToken());
        map.put("page",page);

        baseActivity.showDialog();
        XUtil.Post(URLConstant.SC_SHOPLIEBIAO,map,new MyCallBack<String>(){
            @Override
            public void onSuccess(String result) {
                super.onSuccess(result);
                baseActivity.closeDialog();
                Log.i("====收藏店铺列表===", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.optString("status");
                    order_refresh_fragment.endRefreshing();
                    order_refresh_fragment.endLoadingMore();
                    if(res.equals("1")){
                        Gson gson=new Gson();
                        ShopLBBean shopLBBean=gson.fromJson(result,ShopLBBean.class);
                        list=shopLBBean.getResult();
                        if(PAGE_NUM==1){
                            resultBeans=shopLBBean.getResult();
                        }else {
                            if (ListUtils.isEmpty(list)){
                                UiUtils.toast("没有更新的数据");
                                return;
                            }
                            resultBeans.addAll(list);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
                baseActivity.closeDialog();
                ShoppDPAdapter adapter=new ShoppDPAdapter(resultBeans);
                order_rl.setAdapter(adapter);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                ex.printStackTrace();
                baseActivity.closeDialog();

            }
        });
    }
}
