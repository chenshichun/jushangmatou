package com.tem.gettogether.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tem.gettogether.R;
import com.tem.gettogether.activity.my.authentication.AuthenticationActivity;
import com.tem.gettogether.activity.my.member.MemberCentreActivity;
import com.tem.gettogether.base.BaseConstant;
import com.tem.gettogether.base.BaseFragment;
import com.tem.gettogether.utils.SharedPreferencesUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.custoast.CusToast;

@ContentView(R.layout.activity_xpts)
public class XunPanTuiSongFragment extends BaseFragment {
    @ViewInject(R.id.rl_close)
    RelativeLayout rl_close;
    @ViewInject(R.id.tv_title)
    TextView tv_title;
    @ViewInject(R.id.tv_title_right)
    TextView tv_title_right;

    @ViewInject(R.id.myTab)
    private TabLayout myTab;
    @ViewInject(R.id.myView)
    private ViewPager myView;
    private List<String> myTitle;
    private List<Fragment> myFragment;
    private OnMyListener listener;
    @ViewInject(R.id.status_bar_id)
    private View status_bar_id;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnMyListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rl_close.setVisibility(View.GONE);
        tv_title.setText(R.string.tuisongxunpan);
        /*if (!SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.LEVER, "7").equals("2")) {
            tv_title_right.setVisibility(View.VISIBLE);
            tv_title_right.setTextSize(14);
            tv_title_right.setText(R.string.ckhyqx);
        }*/
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) status_bar_id.getLayoutParams();
        linearParams.height = getStatusBarHeight(getContext());
        status_bar_id.setLayoutParams(linearParams);
        initDatas();
        initViews();
    }

    @Event(value = {R.id.tv_title_right}, type = View.OnClickListener.class)
    private void getEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_title_right:
                startActivity(new Intent(getActivity(), MemberCentreActivity.class));
                break;
        }
    }

    private void initDatas() {
        myTitle = new ArrayList<>();
        String[] string = getResources().getStringArray(R.array.test1);
        for (int i = 0; i < string.length; i++) {
            myTitle.add(string[i]);
        }
        myFragment = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            XunPanFragment fragment = new XunPanFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("page", i);
            fragment.setArguments(bundle);
            myFragment.add(fragment);
        }
    }

    private void initViews() {
        //预加载
        myView.setOffscreenPageLimit(myFragment.size());
        //适配器（容器都需要适配器）
        myView.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            //选中的item
            @Override
            public Fragment getItem(int position) {
                return myFragment.get(position);
            }

            @Override
            public int getCount() {
                return myFragment.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return myTitle.get(position);
            }
        });
        myTab.setupWithViewPager(myView);

        myTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1 &&SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.SHOP_STATUS, "0").equals("2")) {
                    CusToast.showToast(getText(R.string.store_review));
                    return;
                }
                if (tab.getPosition() == 1 &&!SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.SHOP_STATUS, "0").equals("1")) {
                    CusToast.showToast(getText(R.string.please_certify_shops_first));
                    startActivityForResult(new Intent(getContext(), AuthenticationActivity.class),10000);
                    return;
                }
                if (tab.getPosition() == 1 && !SharedPreferencesUtils.getString(getContext(), BaseConstant.SPConstant.LEVER, "7").equals("2")) {
                    CusToast.showToast(getText(R.string.please_upgrade_the_premium_member_first));
                    startActivityForResult(new Intent(getContext(), MemberCentreActivity.class), 10000);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public interface OnMyListener {
        void switchMy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10000 ) {

            myTab.postDelayed(new Runnable() {
                @Override
                public void run() {
                    myTab.getTabAt(0).select();
                }
            }, 100);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
