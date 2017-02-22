package com.gdgst.androidfire.ui.main.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gdgst.androidfire.R;
import com.gdgst.androidfire.app.AppApplication;
import com.gdgst.androidfire.app.AppConstant;
import com.gdgst.androidfire.bean.TabEntity;
import com.gdgst.androidfire.ui.main.fragment.FourMainFragment;
import com.gdgst.androidfire.ui.main.fragment.NewsMainFragment;
import com.gdgst.androidfire.ui.main.fragment.PhotosMainFragment;
import com.gdgst.androidfire.ui.main.fragment.PractiseMainFragment;
import com.gdgst.androidfire.ui.main.fragment.VideoMainFragment;
import com.gdgst.common.base.BaseActivity;
import com.gdgst.common.baseapp.AppConfig;
import com.gdgst.common.commonutils.LogUtils;
import com.gdgst.common.daynightmodeutils.ChangeModeController;

import java.util.ArrayList;

import butterknife.Bind;
import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import rx.functions.Action1;

/**
 * des:主界面
 * Created by xsf
 * on 2016.09.15:32
 */
public class MainActivity extends BaseActivity {

    private static int SCREEN_HEIGHT = 0;
    private static int SCREEN_WIDTH = 0;

    @Bind(R.id.tab_layout)
    CommonTabLayout tabLayout;

    private String[] mTitles = {"首页", "练字","视频","我的"};
    private int[] mIconUnselectIds = {
            R.mipmap.ic_home_normal,R.mipmap.ic_girl_normal,R.mipmap.ic_video_normal,R.mipmap.ic_care_normal};
    private int[] mIconSelectIds = {
            R.mipmap.ic_home_selected,R.mipmap.ic_girl_selected, R.mipmap.ic_video_selected,R.mipmap.ic_care_selected};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private NewsMainFragment newsMainFragment;
    private PhotosMainFragment photosMainFragment;
    private VideoMainFragment videoMainFragment;
    private FourMainFragment careMainFragment;

    private PractiseMainFragment practiseMainFragment;
    private static int tabLayoutHeight;

    /**
     * 退出时间
     */
    private long mExitTime;
    /**
     * 退出间隔
     */
    private static final int INTERVAL = 2000;

    protected AppApplication myApplication;

    /**
     * 入口
     * @param activity
     */
    public static void startAction(Activity activity){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in,
                com.gdgst.common.R.anim.fade_out);
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_main;
    }

    @Override
    public void initPresenter() {

    }
    @Override
    public void initView() {
        //此处填上在http://fir.im/注册账号后获得的API_TOKEN以及APP的应用ID
        UpdateKey.API_TOKEN = AppConfig.API_FIRE_TOKEN;
        UpdateKey.APP_ID = AppConfig.APP_FIRE_ID;
        //如果你想通过Dialog来进行下载，可以如下设置
//        UpdateKey.DialogOrNotification=UpdateKey.WITH_DIALOG;
        UpdateFunGO.init(this);
        //初始化菜单
        initTab();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //切换daynight模式要立即变色的页面
        ChangeModeController.getInstance().init(this,R.attr.class);
        super.onCreate(savedInstanceState);
        //初始化frament
        initFragment(savedInstanceState);
        tabLayout.measure(0,0);
        tabLayoutHeight=tabLayout.getMeasuredHeight();
        //监听菜单显示或隐藏
        mRxManager.on(AppConstant.MENU_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                startAnimation(hideOrShow);
            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        SCREEN_HEIGHT = display.getHeight();// 设置屏幕高度
        SCREEN_WIDTH = display.getWidth();// 设置屏幕宽度
    }

    public static int  getSCREEN_HEIGHT() {
        return SCREEN_HEIGHT;
    }

    public static int getSCREEN_WIDTH() {
        return SCREEN_WIDTH;
    }


    /**
     * 初始化tab
     */
    private void initTab() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabLayout.setTabData(mTabEntities);
        //点击监听
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                SwitchTo(position);
            }
            @Override
            public void onTabReselect(int position) {
            }
        });
    }
    /**
     * 初始化碎片
     */
    private void initFragment(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (savedInstanceState != null) {
            newsMainFragment = (NewsMainFragment) getSupportFragmentManager().findFragmentByTag("newsMainFragment");
//            photosMainFragment = (PhotosMainFragment) getSupportFragmentManager().findFragmentByTag("photosMainFragment");
            practiseMainFragment =  (PractiseMainFragment) getSupportFragmentManager().findFragmentByTag("practiseMainFragment");
            videoMainFragment = (VideoMainFragment) getSupportFragmentManager().findFragmentByTag("videoMainFragment");
            careMainFragment = (FourMainFragment) getSupportFragmentManager().findFragmentByTag("careMainFragment");
            currentTabPosition = savedInstanceState.getInt(AppConstant.HOME_CURRENT_TAB_POSITION);
        } else {
            newsMainFragment = new NewsMainFragment();
//            photosMainFragment = new PhotosMainFragment();
            practiseMainFragment =new PractiseMainFragment();
            videoMainFragment = new VideoMainFragment();
            careMainFragment = new FourMainFragment();

            transaction.add(R.id.fl_body, newsMainFragment, "newsMainFragment");
//            transaction.add(R.id.fl_body, photosMainFragment, "photosMainFragment");
            transaction.add(R.id.fl_body, practiseMainFragment, "practiseMainFragment");
            transaction.add(R.id.fl_body, videoMainFragment, "videoMainFragment");
            transaction.add(R.id.fl_body, careMainFragment, "careMainFragment");
        }
        transaction.commit();
        SwitchTo(currentTabPosition);
        tabLayout.setCurrentTab(currentTabPosition);
    }

    /**
     * 切换
     */
    private void SwitchTo(int position) {
        LogUtils.logd("主页菜单position" + position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            //首页
            case 0:
                transaction.hide(practiseMainFragment);
//                transaction.hide(photosMainFragment);
                transaction.hide(videoMainFragment);
                transaction.hide(careMainFragment);
                transaction.show(newsMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //美女
            case 1:
                transaction.hide(newsMainFragment);
                transaction.hide(videoMainFragment);
                transaction.hide(careMainFragment);
//                transaction.show(photosMainFragment);
                transaction.show(practiseMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //视频
            case 2:
                transaction.hide(newsMainFragment);
//                transaction.hide(photosMainFragment);
                transaction.hide(practiseMainFragment);
                transaction.hide(careMainFragment);
                transaction.show(videoMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //关注
            case 3:
                transaction.hide(newsMainFragment);
//                transaction.hide(photosMainFragment);
                transaction.hide(practiseMainFragment);
                transaction.hide(videoMainFragment);
                transaction.show(careMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    /**
     * 菜单显示隐藏动画
     * @param showOrHide
     */
    private void startAnimation(boolean showOrHide){
        final ViewGroup.LayoutParams layoutParams = tabLayout.getLayoutParams();
        ValueAnimator valueAnimator;
        ObjectAnimator alpha;
        if(!showOrHide){
             valueAnimator = ValueAnimator.ofInt(tabLayoutHeight, 0);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 1, 0);
        }else{
             valueAnimator = ValueAnimator.ofInt(0, tabLayoutHeight);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 0, 1);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height= (int) valueAnimator.getAnimatedValue();
                tabLayout.setLayoutParams(layoutParams);
            }
        });
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator,alpha);
        animatorSet.start();
    }

    /**
     * 监听全屏视频时返回键
     */
    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
//            return true;
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //奔溃前保存位置
        LogUtils.loge("onSaveInstanceState进来了1");
        if (tabLayout != null) {
            LogUtils.loge("onSaveInstanceState进来了2");
            outState.putInt(AppConstant.HOME_CURRENT_TAB_POSITION, tabLayout.getCurrentTab());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChangeModeController.onDestory();
    }

    /**
     * 判断两次返回时间间隔,小于两秒则退出程序
     */
    private void exit() {
        if (System.currentTimeMillis() - mExitTime > INTERVAL) {
            Toast.makeText(getApplicationContext(), "再按一次返回键,可直接退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
//            if (myApplication.removerAll()) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
//            }

        }
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            if (event.getAction() == KeyEvent.ACTION_DOWN
//                    && event.getRepeatCount() == 0) {
//                exit();
//            }
//            return true;
//        }
//        return super.dispatchKeyEvent(event);
//    }
}