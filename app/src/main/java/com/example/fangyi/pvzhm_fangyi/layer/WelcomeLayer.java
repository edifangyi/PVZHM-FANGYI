package com.example.fangyi.pvzhm_fangyi.layer;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.example.fangyi.pvzhm_fangyi.utils.CommonUtils;
import com.socks.library.KLog;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

/**
 * Created by FANGYI on 2016/8/4.
 */

public class WelcomeLayer extends BaseLayer {

    private static CCSprite start;

    public WelcomeLayer() {


        init();


        new AsyncTask<Void, Void, Void>() {
            //  在子线程中执行的
            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(6000);// 模拟后台耗时的操作
                return null;
            }
            // 在子线程之后执行的代码
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                KLog.e("后台加载完成了...");
                WelcomeLayer.start.setVisible(true);
                setIsTouchEnabled(true);// 打开触摸事件
            }
        }.execute();
    }

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        CGPoint point = this.convertTouchToNodeSpace(event);
        CGRect boundingBox = start.getBoundingBox();
        if (CGRect.containsPoint(boundingBox, point)) {
            // 处理点击事件
            CommonUtils.changeLayer(new MenuLayer());
        }
        return super.ccTouchesBegan(event);
    }

    private void init() {
        logo();

    }

    public void logo() {
        CCSprite logo = CCSprite.sprite("image/popcap_logo.png");

        logo.setPosition(winSize.width / 2, winSize.height / 2);
        this.addChild(logo);//添加到图层
        // 让logo执行动作
        CCHide ccHide = CCHide.action();// 隐藏
        CCDelayTime delayTime = CCDelayTime.action(1);//停留一秒钟
        CCSequence ccSequence = CCSequence.actions(delayTime, delayTime, ccHide, delayTime, CCCallFunc.action(this, "loadWelcome"));
        logo.runAction(ccSequence);
    }

    //当logo动作执行完以后调用
    public void loadWelcome() {
        CCSprite bg = CCSprite.sprite("image/welcome.jpg");
        bg.setAnchorPoint(0, 0);
        this.addChild(bg);

        loading();
    }

    /**
     * 加载进度条
     */
    private void loading() {
        CCSprite loading = CCSprite.sprite("image/loading/loading_01.png");
        loading.setPosition(winSize.width / 2, 30);
        this.addChild(loading);


        CCAction animate = CommonUtils.getAnimate("image/loading/loading_%02d.png", 9, false);
        loading.runAction(animate);

        start = CCSprite.sprite("image/loading/loading_start.png");
        start.setPosition(winSize.width/2, 30);
        start.setVisible(false);// 暂时不可见
        this.addChild(start);



    }
}
