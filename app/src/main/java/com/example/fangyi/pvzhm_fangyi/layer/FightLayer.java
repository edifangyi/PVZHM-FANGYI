package com.example.fangyi.pvzhm_fangyi.layer;

import android.view.MotionEvent;

import com.example.fangyi.pvzhm_fangyi.bean.ShowPlant;
import com.example.fangyi.pvzhm_fangyi.bean.ShowZombies;
import com.example.fangyi.pvzhm_fangyi.engine.GameCotroller;
import com.example.fangyi.pvzhm_fangyi.utils.CommonUtils;
import com.socks.library.KLog;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCDirector;
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
    public static final int TAG_CHOSE = 10;

    private CCTMXTiledMap map;
    private List<CGPoint> zombilesPoints;
    private CCSprite choose; // 玩家可选植物的容器
    private CCSprite chose; // 玩家已选植物的容器
    private List<ShowPlant> showPlatns; // 展示用的植物的集合
    private List<ShowPlant> selectPlants = new CopyOnWriteArrayList<>();// 已经选中植物的集合
    boolean isLock;
    private boolean isDel;//是否删除了选中的植物
    private CCSprite start;

    public FightLayer() {
        init();
    }

    private void init() {
        KLog.e(" ====================" + isLock + " ====================");
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
        this.addChild(chose, 0, TAG_CHOSE);

        choose = CCSprite.sprite("image/fight/chose/fight_choose.png");
        choose.setAnchorPoint(0, 0);
        this.addChild(choose);

        loadShowPlant();

        //加载一起来摇滚按钮
        start = CCSprite.sprite("image/fight/chose/fight_start.png");
        start.setPosition(choose.getContentSize().width / 2, 30);

        choose.addChild(start);

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
        setIsTouchEnabled(true);
    }


    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        // 需要把Android坐标系中的点 转换成Cocos2d坐标系中的点
        CGPoint point = this.convertTouchToNodeSpace(event);

        if(GameCotroller.isStart){// 如果游戏开始了 交给GameCtoller 处理
            GameCotroller.getInstance().handleTouch(point);

            return super.ccTouchesBegan(event);
        }

        CGRect boundingBox = choose.getBoundingBox();
        CGRect choseBox = chose.getBoundingBox();


        // 玩家有可能反选植物
        if (CGRect.containsPoint(choseBox, point)) {

            isDel = false;
            for (ShowPlant plant : selectPlants) {
                CGRect selectPlantBox = plant.getShowSprite().getBoundingBox();

                if (CGRect.containsPoint(selectPlantBox, point)) {
                    CCMoveTo moveTo = CCMoveTo.action(1, plant.getBgSprite().getPosition());
                    plant.getShowSprite().runAction(moveTo);
                    selectPlants.remove(plant);//走到这一步，确实代表反选植物了
                    isDel = true;
                    continue;//跳出本次循环，继续下次循环
                }
                if (isDel) {
                    setIsTouchEnabled(false);
                    CCMoveBy ccMoveBy = CCMoveBy.action(0.5f, ccp(-53, 0));
                    CCSequence sequence = CCSequence.actions(ccMoveBy, CCCallFunc.action(this, "unlock"));//移动完成解锁
                    plant.getShowSprite().runAction(sequence);
                }

            }

        } else if (CGRect.containsPoint(boundingBox, point)) {
            if (CGRect.containsPoint(start.getBoundingBox(), point)) {
                //点击了一起来摇滚
                ready();
            } else if (selectPlants.size() < 5 && !isLock) {  //如果已经选择满了 就不能再选择了，所选植物小于5，并且未上锁
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

    /**
     * 点击了一起来摇滚
     */
    private void ready() {
        //缩小玩家已选容器
        chose.setScale(0.65f);
        //把选中的植物重新添加到 存在的容器上
        for (ShowPlant plant : selectPlants) {
            plant.getShowSprite().setScale(0.65f);// 因为父容器缩小了 孩子一起缩小

            plant.getShowSprite().setPosition(
                    plant.getShowSprite().getPosition().x * 0.65f,
                    plant.getShowSprite().getPosition().y

                            + (CCDirector.sharedDirector().getWinSize().height - plant

                            .getShowSprite().getPosition().y)
                            * 0.35f);// 设置坐标
            this.addChild(plant.getShowSprite());
        }

        choose.removeSelf();//回收容器
        //地图的平移
        int x = (int) (map.getContentSize().width - winSize.width);
        CCMoveBy moveBy = CCMoveBy.action(1, ccp(x, 0));
        CCSequence sequence = CCSequence.actions(moveBy, CCCallFunc.action(this, "preGame"));

        map.runAction(sequence);
    }

    private CCSprite ready;

    /**
     * 准备游戏
     */
    public void preGame() {
        ready = CCSprite.sprite("image/fight/startready_01.png");
        ready.setPosition(winSize.width / 2, winSize.height / 2);
        this.addChild(ready);
        String format = "image/fight/startready_%02d.png";
        CCAction animate = CommonUtils.getAnimate(format, 3, false);
        CCSequence sequence = CCSequence.actions((CCAnimate) animate, CCCallFunc.action(this, "startGame"));
        ready.runAction(sequence);
    }

    public void startGame() {
        ready.removeSelf();// 移除中间的序列帧
        GameCotroller cotroller=GameCotroller.getInstance();
        cotroller.startGame(map,selectPlants);

    }
}
