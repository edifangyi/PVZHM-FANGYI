package com.example.fangyi.pvzhm_fangyi.layer;

import android.view.MotionEvent;

import com.example.fangyi.pvzhm_fangyi.bean.ShowPlant;
import com.example.fangyi.pvzhm_fangyi.bean.ShowZombies;
import com.example.fangyi.pvzhm_fangyi.utils.CommonUtils;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by FANGYI on 2016/8/4.
 */
public class FightLayer extends BaseLayer {

    private CCTMXTiledMap map;
    private List<CGPoint> zombilesPoints;
    private CCSprite choose; // 玩家可选植物的容器
    private CCSprite chose; // 玩家已选植物的容器
    private List<ShowPlant> showPlatns; // 展示用的植物的集合
    private List<ShowPlant> selectPlants = new CopyOnWriteArrayList<>();// 已经选中植物的集合
    boolean isLock;

    public FightLayer() {
        init();
    }

    private void init() {
        loadMap();
        parserMap();
        showZombies();
        moveMap();
    }

    //加载展示用的僵尸
    private void showZombies() {
        for (int i = 0; i < zombilesPoints.size(); i++) {
            CGPoint cgPoint = zombilesPoints.get(i);
            ShowZombies showZombies = new ShowZombies();
            showZombies.setPosition(cgPoint);// 给展示用的僵尸设置了位置
            map.addChild(showZombies);// 注意: 把僵尸加载到地图上
        }
    }

    private void parserMap() {
        zombilesPoints = CommonUtils.getMapPoints(map, "zombies");
    }

    // 移动地图
    private void moveMap() {
        int x = (int) (winSize.width - map.getContentSize().width);
        CCMoveBy moveBy = CCMoveBy.action(3, ccp(x, 0));
        CCSequence sequence = CCSequence
                .actions(CCDelayTime.action(2), moveBy, CCDelayTime.action(1),
                        CCCallFunc.action(this, "loadContainer"));
        map.runAction(sequence);
    }

    private void loadMap() {
        map = CCTMXTiledMap.tiledMap("image/fight/map_day.tmx");
        map.setAnchorPoint(0.5f, 0.5f);
        CGSize contentSize = map.getContentSize();
        map.setPosition(contentSize.width / 2, contentSize.height / 2);
        this.addChild(map);
    }

    // 加载两个容器
    public void loadContainer() {
        chose = CCSprite.sprite("image/fight/chose/fight_chose.png");
        chose.setAnchorPoint(0, 1);
        chose.setPosition(0, winSize.height);// 设置位置是屏幕的左上角
        this.addChild(chose);

        choose = CCSprite.sprite("image/fight/chose/fight_choose.png");
        choose.setAnchorPoint(0, 0);
        this.addChild(choose);

        loadShowPlant();
    }

    // 加载展示用的植物
    private void loadShowPlant() {
        showPlatns = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            ShowPlant plant = new ShowPlant(i); // 创建了展示的植物

            CCSprite bgSprite = plant.getBgSprite();
            bgSprite.setPosition(16 + ((i - 1) % 4) * 54,
                    175 - ((i - 1) / 4) * 59);
            choose.addChild(bgSprite);

            CCSprite showSprite = plant.getShowSprite();// 获取到了展示的精灵
            // 设置坐标
            showSprite.setPosition(16 + ((i - 1) % 4) * 54,
                    175 - ((i - 1) / 4) * 59);
            choose.addChild(showSprite); // 添加到了容器上

            showPlatns.add(plant);
        }
        setIsTouchEnabled(true);
    }

    //解锁
    public void unlock() {
        isLock = false;
    }


    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        // 需要把Android坐标系中的点 转换成Cocos2d坐标系中的点
        CGPoint point = this.convertTouchToNodeSpace(event);
        CGRect boundingBox = choose.getBoundingBox();
        CGRect choseBox = chose.getBoundingBox();

        // 玩家有可能反选植物
        if (CGRect.containsPoint(choseBox, point)) {
            for (ShowPlant plant : selectPlants) {
                CGRect selectPlantBox = plant.getShowSprite().getBoundingBox();
                if (CGRect.containsPoint(selectPlantBox, point)) {
                    CCMoveTo moveTo = CCMoveTo.action(1, plant.getBgSprite().getPosition());
                    plant.getShowSprite().runAction(moveTo);
                    selectPlants.remove(plant);//移除已经选中的植物
                }
            }

        } else if (CGRect.containsPoint(boundingBox, point)) {
            if (selectPlants.size() < 5 && !isLock) {  //如果已经选择满了 就不能再选择了，所选植物小于5，并且未上锁
                // 有可能 选择植物
                for (ShowPlant plant : showPlatns) {
                    CGRect boundingBox2 = plant.getShowSprite()
                            .getBoundingBox();
                    if (CGRect.containsPoint(boundingBox2, point)) {// 如果点恰好落在植物展示的精灵矩形之中
                        // 当前植物被选中了
                        isLock = true;//不加锁有bug,就是前一个对象移动的时候，又点了一下，就又占用了一个格子

                        CCMoveTo moveTo = CCMoveTo.action(
                                0.5f, ccp(75 + selectPlants.size() * 53, 255));
                        CCSequence sequence = CCSequence.actions(moveTo, CCCallFunc.action(this, "unlock"));//移动完成解锁
                        plant.getShowSprite().runAction(sequence);
                        selectPlants.add(plant);
                    }
                }
            }

        }

        return super.ccTouchesBegan(event);
    }


}
