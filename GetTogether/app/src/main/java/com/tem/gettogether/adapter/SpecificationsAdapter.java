package com.tem.gettogether.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tem.gettogether.R;
import com.tem.gettogether.bean.ServiceProviderBean;

import java.util.List;

/**
 * @ProjectName: GetTogether
 * @Package: com.tem.gettogether.adapter
 * @ClassName: SpecificationsAdapter
 * @Author: csc
 * @CreateDate: 2019/9/9 13:50
 * @Description:
 */
public class SpecificationsAdapter extends RecyclerView.Adapter<SpecificationsAdapter.ViewHolder> {

    private Context context;
    private List<ServiceProviderBean.ResultEntity> mDatas;
    private SpecificationsAdapter.OnItemClickListener mOnItemClickListener;
    private SparseBooleanArray selectLists = new SparseBooleanArray();

    public SpecificationsAdapter(Context context, List<ServiceProviderBean.ResultEntity> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public SpecificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_specifications, null);
        return new SpecificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SpecificationsAdapter.ViewHolder holder, final int position) {
        if (!selectLists.get(position)) {
            holder.text_tv.setBackgroundColor(context.getResources().getColor(R.color.line));
        } else {
            holder.text_tv.setBackgroundColor(context.getResources().getColor(R.color.home_red));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectLists.get(position)){
                    selectLists.put(position, false);
                }else{
                    selectLists.put(position, true);
                }
                mOnItemClickListener.onItemClick(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 21/*mDatas.size()*/;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            text_tv = itemView.findViewById(R.id.text_tv);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickItem(SpecificationsAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public SparseBooleanArray getSelectedItem() {
        return selectLists;
    }

}