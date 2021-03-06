package com.tem.gettogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tem.gettogether.R;
import com.tem.gettogether.activity.my.OrderXQActivity;
import com.tem.gettogether.base.BaseRVAdapter;
import com.tem.gettogether.bean.MyOrderdataBean;
import com.tem.gettogether.view.RoundImageView;

import java.util.List;

public class GongYingShangOrderAdapter extends RecyclerView.Adapter<GongYingShangOrderAdapter.ViewHolder> {

    private Context context;
    private List<MyOrderdataBean.ResultBean> resultBeans;
    private GongYingShangOrderAdapter.OnItemClickListener mOnItemClickListener;
    private int mTab;

    public GongYingShangOrderAdapter(Context context, List<MyOrderdataBean.ResultBean> mDatas, int mTab) {
        this.context = context;
        this.resultBeans = mDatas;
        this.mTab = mTab;
    }

    @Override
    public GongYingShangOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_new_fragment, null);
        return new GongYingShangOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GongYingShangOrderAdapter.ViewHolder holder, final int position) {
        holder.tv_shopName.setText(resultBeans.get(position).getStore_name());
        holder.tv_right_top.setText(resultBeans.get(position).getOrder_status_desc());
        holder.tv_shopping_num.setText(context.getString(R.string.total) + resultBeans.get(position).getGoods_all_num() + " " +
                context.getText(R.string.items) + " "+context.getText(R.string.total_tv)+"￥");
        holder.tv_all_peice.setText(resultBeans.get(position).getTotal_amount() + "（"+context.getText(R.string.freight_included)+"¥" +
                resultBeans.get(position).getShipping_price() + "）");

        if (resultBeans.get(position).getOrder_status_code() != null && resultBeans.get(position).getOrder_status_code().equals("WAITSEND")) {
            holder.tv_red_right.setText(context.getText(R.string.confirm_delivery));
            holder.tv_red_right.setVisibility(View.VISIBLE);
        } else if (resultBeans.get(position).getOrder_status_code() != null && resultBeans.get(position).getOrder_status_code().equals("WAITCCOMMENT")) {
            holder.tv_red_right.setText(context.getText(R.string.confirm_payment));
            holder.tv_red_right.setVisibility(View.VISIBLE);
        } else {
            holder.tv_red_right.setVisibility(View.GONE);
        }
        holder.tv_red_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });

        holder.recyclerView_item.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView_item.setAdapter(new BaseRVAdapter(context, resultBeans.get(position).getGoods_list()) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.recy_order_fragment_item;
            }

            @Override
            public void onBind(com.tem.gettogether.base.BaseViewHolder holder, final int position2) {
                RoundImageView iv_image_shopping = holder.getView(R.id.iv_image_shopping);
                Glide.with(context).load(resultBeans.get(position).getGoods_list().get(position2).getImage()).error(R.mipmap.myy322x).into(iv_image_shopping);

                holder.getTextView(R.id.tv_shopping_name).setText(resultBeans.get(position).getGoods_list().get(position2).getGoods_name());
                holder.getTextView(R.id.tv_shopping_qpl).setText(resultBeans.get(position).getGoods_list().get(position2).getSpec_key_name());
                holder.getTextView(R.id.tv_shopping_price).setText("¥" + resultBeans.get(position).getGoods_list().get(position2).getGoods_price() + "/"+context.getText(R.string.piece_tv));
                holder.getTextView(R.id.tv_shoping_Num).setText(context.getText(R.string.total) + resultBeans.get(position).getGoods_list().get(position2).getGoods_num() + context.getText(R.string.piece_tv));
                holder.getTextView(R.id.tv_shopping_zt).setText(resultBeans.get(position).getGoods_list().get(position2).getGoods_sn());
                holder.getView(R.id.ll_item_dd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, OrderXQActivity.class)
                                .putExtra("order_id", resultBeans.get(position).getOrder_id()));
                    }
                });
            }


        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return resultBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_shopName;
        public TextView tv_right_top;
        public RecyclerView recyclerView_item;
        public TextView tv_shopping_num;
        public TextView tv_all_peice;
        public TextView tv_red_right;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_shopName = itemView.findViewById(R.id.tv_shopName);
            tv_right_top = itemView.findViewById(R.id.tv_right_top);
            tv_shopping_num = itemView.findViewById(R.id.tv_shopping_num);
            tv_all_peice = itemView.findViewById(R.id.tv_all_peice);
            tv_red_right = itemView.findViewById(R.id.tv_red_right);
            recyclerView_item = itemView.findViewById(R.id.recyclerView_item);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickItem(GongYingShangOrderAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}