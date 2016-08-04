package com.example.fangyi.pvzhm_fangyi.layer;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.types.CGSize;

import java.util.ArrayList;

/**
 * Created by FANGYI on 2016/8/4.
 */

public class WelcomeLayer extends CCLayer {


    private CGSize winSize;

    public WelcomeLayer() {
        init();
    }

    private void init() {
        logo();

    }

    private void logo() {
        CCSprite logo = CCSprite.sprite("image/popcap_logo.png");
        winSize = CCDirector.sharedDirector().getWinSize();
        logo.setPosition(winSize.width / 2, winSize.height / 2);
        this.addChild(logo);
        CCHide ccHide = CCHide.action();//隐藏
        CCDelayTime delayTime = CCDelayTime.action(1);//停留一秒钟
        CCSequence ccSequence = CCSequence.actions(delayTime, ccHide, delayTime,
                CCCallFunc.action(this, "loadWelcome"));
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
        loading.setPosition(winSize.width/2, 30);

        ArrayList<CCSpriteFrame> frames = new ArrayList<>();
        String filepath = "image/loading/loading_%02d.png";
        for (int i = 1; i <= 9; i++) {
            CCSpriteFrame ccSpriteFrame = CCSprite.sprite(String.format(filepath, i)).displayedFrame();
            frames.add(ccSpriteFrame);
        }
        CCAnimation animation = CCAnimation.animation("loading", 0.2f, frames);
        //序列化一般必须永不停止的播放， 不需要永不停止播放，给传一个false
        CCAnimate animate = CCAnimate.action(animation, false);
        loading.runAction(animate);

        this.addChild(loading);
    }
}
