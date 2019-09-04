package com.tem.gettogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tem.gettogether.R;
import com.tem.gettogether.activity.home.HomeBuyDetailNewActivity;
import com.tem.gettogether.bean.QiuGouListBean;
import com.tem.gettogether.utils.SizeUtil;

import java.util.List;

public class HomeBuyListAdapter extends RecyclerView.Adapter<HomeBuyListAdapter.ViewHolder> {

    private Context context;
    private List<QiuGouListBean.ResultBean> mDatas;
    private onClickView onClickView;

    public HomeBuyListAdapter(Context context, List<QiuGouListBean.ResultBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public HomeBuyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_buying, null);
        return new HomeBuyListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeBuyListAdapter.ViewHolder holder, final int position) {
        int imageSize = SizeUtil.dp2px(context, 110);
        Glide.with(context).load(mDatas.get(position).getGoods_logo().get(0)).asBitmap().placeholder(R.mipmap.myy322x)
                .error(R.mipmap.myy322x).override(imageSize, imageSize).into(holder.pic_iv);
        holder.product_title.setText(mDatas.get(position).getGoods_name());
        holder.buy_style_tv.setText(mDatas.get(position).getRelease_type());
        holder.buy_time_tv.setText(mDatas.get(position).getAttach_time());
        holder.chukou_tv.setText(mDatas.get(position).getCountry_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*context.startActivity(new Intent(context, HomeBuyDetailNewActivity.class)
                        .putExtra("trade_id", mDatas.get(position).getTrade_id())
                        .putExtra("witch_page",1));*/
                onClickView.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView pic_iv;
        public TextView product_title;
        public TextView buy_time_tv;
        public TextView buy_style_tv;
        public TextView chukou_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            pic_iv = itemView.findViewById(R.id.pic_iv);
            product_title = itemView.findViewById(R.id.product_title);
            buy_time_tv = itemView.findViewById(R.id.buy_time_tv);
            buy_style_tv = itemView.findViewById(R.id.buy_style_tv);
            chukou_tv = itemView.findViewById(R.id.chukou_tv);
        }
    }
    public interface onClickView {
        public void onClick(int position);
    }

    public void setOnClickView(onClickView onClickView) {
        this.onClickView = onClickView;
    }
}
