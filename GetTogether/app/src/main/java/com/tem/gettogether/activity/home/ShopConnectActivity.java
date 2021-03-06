package com.tem.gettogether.activity.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tem.gettogether.R;
import com.tem.gettogether.base.BaseActivity;
import com.tem.gettogether.base.BaseFragment;
import com.tem.gettogether.fragment.ShopMainDPFragment;
import com.tem.gettogether.fragment.ShoppingFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_shop_connect)
public class ShopConnectActivity extends BaseActivity {
    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    @ViewInject(R.id.tv_shopping)
    private TextView tv_shopping;
    @ViewInject(R.id.line1)
    private  View line1;
    @ViewInject(R.id.tv_dianp)
    private TextView tv_dianp;
    @ViewInject(R.id.line2)
    private  View line2;
    public static final int NICKNAME_TYPE = 201;//商品
    public static final int BIRTHDAY_TYPE = 202;//店铺
    private ShoppingFragment shoppingFragment;
    private ShopMainDPFragment shopDPFragment;
    private BaseFragment baseFragment;
    private int type;
    private String category_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initData();
        shoppingFragment=new ShoppingFragment();
        shopDPFragment=new ShopMainDPFragment();
        if (savedInstanceState != null) {
            baseFragment = (BaseFragment) getSupportFragmentManager().getFragment(savedInstanceState, baseFragment.getClass().getSimpleName());
        } else {
            switch (type) {
                case NICKNAME_TYPE:
                    tv_shopping.setTextColor(getResources().getColor(R.color.home_red));
                    line1.setBackgroundColor(getResources().getColor(R.color.home_red));
                    tv_dianp.setTextColor(getResources().getColor(R.color.text3));
                    line2.setBackgroundColor(getResources().getColor(R.color.white));
                    baseFragment = ShoppingFragment.newInstance();
                    break;
                case BIRTHDAY_TYPE:
                    tv_dianp.setTextColor(getResources().getColor(R.color.home_red));
                    tv_shopping.setTextColor(getResources().getColor(R.color.white));
                    line1.setBackgroundColor(getResources().getColor(R.color.white));
                    line2.setBackgroundColor(getResources().getColor(R.color.home_red));
                    baseFragment = ShopMainDPFragment.newInstance();
                    break;
            }
        }
        if (!baseFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_item, baseFragment, baseFragment.getClass().getSimpleName()).show(baseFragment).commit();
        }

        initView();
    }

    @Override
    protected void initData() {
        String title=getIntent().getStringExtra("title");
        category_id=getIntent().getStringExtra("category_id");
        tv_title.setText(title+"");
        type = getIntent().getIntExtra("type", 0);

    }

    @Override
    protected void initView() {

    }
    @Event(value = {R.id.rl_close,R.id.ll_title,R.id.ll_title2}, type = View.OnClickListener.class)
    private void getEvent(View view) {
        Bundle bundle=new Bundle();

        switch (view.getId()) {
            case R.id.rl_close:
                finish();
                break;
            case R.id.ll_title:
                tv_shopping.setTextColor(getResources().getColor(R.color.home_red));
                line1.setBackgroundColor(getResources().getColor(R.color.home_red));
                tv_dianp.setTextColor(getResources().getColor(R.color.text3));
                line2.setBackgroundColor(getResources().getColor(R.color.white));
                turnToFragment(baseFragment.getClass(), ShoppingFragment.class, null);

                break;
            case R.id.ll_title2:
                tv_dianp.setTextColor(getResources().getColor(R.color.home_red));
                tv_shopping.setTextColor(getResources().getColor(R.color.text3));
                line1.setBackgroundColor(getResources().getColor(R.color.white));
                line2.setBackgroundColor(getResources().getColor(R.color.home_red));
                bundle.putString("category_id",category_id);
                turnToFragment(baseFragment.getClass(), ShopMainDPFragment.class, bundle);

                break;

        }
    }
    public void turnToFragment(Class<? extends BaseFragment> fromFragmentClass, Class<? extends BaseFragment> toFragmentClass, Bundle args) {

        FragmentManager fm = getSupportFragmentManager();

        //被切换的Fragment标签
        String fromTag = fromFragmentClass.getSimpleName();
        //切换到的Fragment标签
        String toTag = toFragmentClass.getSimpleName();
        //查找切换的Fragment
        Fragment fromFragment = fm.findFragmentByTag(fromTag);
        Fragment toFragment = fm.findFragmentByTag(toTag);
        //如果要切换到的Fragment不存在，则创建
        if (toFragment == null) {
            try {
                toFragment = toFragmentClass.newInstance();
                toFragment.setArguments(args);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //如果有参数传递，
        if (args != null && !args.isEmpty()) {
            toFragment.getArguments().putAll(args);
        }
        //Fragment事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置Fragment切换效果
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        /**
         * 如果要切换到的Fragment没有被Fragment事务添加，则隐藏被切换的Fragment，添加要切换的Fragment
         * 否则，则隐藏被切换的Fragment，显示要切换的Fragment
         */
        if (!toFragment.isAdded()) {
            ft.hide(fromFragment).add(R.id.fragment_item, toFragment, toTag);
        } else {
            ft.hide(fromFragment).show(toFragment);
        }
        baseFragment = (BaseFragment) toFragment;
        //添加到返回堆栈
        //        ft.addToBackStack(tag);
        //不保留状态提交事务
        ft.commitAllowingStateLoss();
    }


}
