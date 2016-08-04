package com.example.fangyi.pvzhm_fangyi.layer;

import com.example.fangyi.pvzhm_fangyi.utils.CommonUtils;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCSprite;

/**
 * Created by FANGYI on 2016/8/4.
 */

public class MenuLayer extends BaseLayer {
    public MenuLayer() {
        init();
    }

    private void init() {
        CCSprite menuBg = CCSprite.sprite("image/menu/main_menu_bg.jpg");
        menuBg.setAnchorPoint(0, 0);
        this.addChild(menuBg);


        CCSprite normalSprite = CCSprite.sprite("image/menu/start_adventure_default.png");
        CCSprite selectedSprite = CCSprite.sprite("image/menu/start_adventure_press.png");

        //菜单 1：默认显示的精灵, 2：选中时候的精灵 3:对象 4:方法
        CCMenuItem items = CCMenuItemSprite.item(normalSprite, selectedSprite, this, "click");//点击了
        CCMenu menu = CCMenu.menu(items);
        menu.setScale(0.5f);//缩放
        menu.setPosition(winSize.width / 2-23, winSize.height / 2 - 115);
        menu.setRotation(4.5f);//设置旋转角度
        this.addChild(menu);
    }

    // 要想菜单能够正常反射该方法, 该方法必须有一个参数 Object类型
    public void click(Object object){ // 参数 具体代表点击的是哪个条目
        System.out.println("我被点击了...");
        CommonUtils.changeLayer(new FightLayer());
    }
}
