package com.example.fangyi.pvzhm_fangyi.engine;

import com.example.fangyi.pvzhm_fangyi.base.Plant;
import com.example.fangyi.pvzhm_fangyi.bean.Nut;
import com.example.fangyi.pvzhm_fangyi.bean.PeasePlant;
import com.example.fangyi.pvzhm_fangyi.bean.PrimaryZombies;
import com.example.fangyi.pvzhm_fangyi.bean.ShowPlant;
import com.example.fangyi.pvzhm_fangyi.layer.FightLayer;
import com.example.fangyi.pvzhm_fangyi.utils.CommonUtils;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.actions.CCScheduler;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 处理游戏开始后的操作
 * Created by FANGYI on 2016/8/5.
 */

public class GameCotroller {

    private CCTMXTiledMap map;
    private List<ShowPlant> showPlants;


    private static List<FightLine> lines;

    static {
        lines = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FightLine fightLine = new FightLine(i);
            lines.add(fightLine);
        }
    }



    public GameCotroller() {
    }

    private static GameCotroller cotroller = new GameCotroller();

    public static GameCotroller getInstance() {
        return cotroller;
    }

    public static boolean isStart;//游戏是否开始

    /**
     * 开始游戏
     *
     * @param map          游戏的地图
     * @param selectPlants 玩家已选植物的集合
     */
    public void startGame(CCTMXTiledMap map, List<ShowPlant> selectPlants) {
        isStart = true;
        this.map = map;
        showPlants = selectPlants;

        loadMap();
        //添加僵尸
        // 定时器
        // 参数1 方法名(方法带float类型的参数) 参数2 调用方法的对象 参数3 间隔时间 参数4 是否暂停
        CCScheduler.sharedScheduler().schedule("addZombies", this, 4, false);
        progress();

    }

    private List<CGPoint> roadPoints;
    private CGPoint[][] towers = new CGPoint[5][9];//安放植物的点

    private void loadMap() {
        roadPoints = CommonUtils.getMapPoints(map, "road");
        for (int i = 1; i <= 5; i++) {
            List<CGPoint> mapPoints = CommonUtils.getMapPoints(map,
                    String.format("tower%02d", i));
            for (int j = 0; j < mapPoints.size(); j++) {
                towers[i - 1][j] = mapPoints.get(j);
            }
        }
    }

    /**
     * 添加僵尸
     *
     * @param t
     */
    public void addZombies(float t) {
        Random random = new Random();
        int lineNum = random.nextInt(5);// [0-5)

        PrimaryZombies primaryZombies = new PrimaryZombies(
                roadPoints.get(lineNum * 2), roadPoints.get(lineNum * 2 + 1));
        map.addChild(primaryZombies, 1);// 让僵尸一直在植物的上面
        lines.get(lineNum).addZombies(primaryZombies);// 把僵尸记录到行战场中
        progress+=5;
        progressTimer.setPercentage(progress);//设置新的进度
    }

    public void endGame() {
        isStart = false;
    }

    private ShowPlant selectPlant;//玩家选择的植物
    private Plant installPlant;//准备安放的植物
    /**
     * 当游戏开始后处理点击事件的方法
     *
     * @param point 点击到的点
     */
    public void handleTouch(CGPoint point) {
        CCSprite chose = (CCSprite) map.getParent().getChildByTag(FightLayer.TAG_CHOSE);
        if (CGRect.containsPoint(chose.getBoundingBox(), point)) {
            //认为玩家有可能选择了植物
            if (selectPlant != null) {
                selectPlant.getShowSprite().setOpacity(255);
                selectPlant = null;
            }
            for (ShowPlant showPlant : showPlants) {
                CGRect cgRect = showPlant.getShowSprite().getBoundingBox();
                if (CGRect.containsPoint(cgRect, point)){
                    //玩家选择了植物
                    selectPlant = showPlant;
                    selectPlant.getShowSprite().setOpacity(150);
                    int id = selectPlant.getId();
                    switch (id) {
                        case 1:
                            installPlant = new PeasePlant();
                            break;
                        case 4:
                            installPlant =new Nut();
                            break;

                    }
                }
            }
        } else {
            //玩家有可能安放植物
            if (selectPlant != null) {
                int row = (int) (point.x / 46) - 1; // 1-9 0-8
                int line = (int) ((CCDirector.sharedDirector().getWinSize().height - point.y) / 54) - 1;// 1-5
                // 0-4
                // 限制安放的植物的范围
                if (row >= 0 && row <= 8 && line >= 0 && line <= 4) {

                    // 安放植物
                    // selectPlant.getShowSprite().setPosition(point);
                    // installPlant.setPosition(point); // 坐标需要修改
                    installPlant.setLine(line);// 设置植物的行号
                    installPlant.setRow(row); // 设置植物的列号


                    installPlant.setPosition(towers[line][row]); // 修正了植物的坐标
                    FightLine fightLine = lines.get(line);
                    if (!fightLine.containsRow(row)) {  // 判断当前列是否已经添加了植物 如果添加了 就不能再添加了
                        fightLine.addPlant(installPlant);// 把植物记录到了行战场中
                        map.addChild(installPlant);
                    }
                }
                installPlant = null;
                selectPlant.getShowSprite().setOpacity(255);
                selectPlant = null;// 下次安装需要重新选择
            }
        }
    }

    CCProgressTimer progressTimer;
    int  progress=0;
    private void progress() {
        // 创建了进度条
        progressTimer = CCProgressTimer.progressWithFile("image/fight/progress.png");
        // 设置进度条的位置
        progressTimer.setPosition(CCDirector.sharedDirector().getWinSize().width - 80, 13);
        map.getParent().addChild(progressTimer); //图层添加了进度条
        progressTimer.setScale(0.6f);  //  设置了缩放

        progressTimer.setPercentage(0);// 每增加一个僵尸需要调整进度，增加5
        progressTimer.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarRL);  // 进度的样式

        CCSprite sprite = CCSprite.sprite("image/fight/flagmeter.png");
        sprite.setPosition(CCDirector.sharedDirector().getWinSize().width - 80, 13);
        map.getParent().addChild(sprite);
        sprite.setScale(0.6f);
        CCSprite name = CCSprite.sprite("image/fight/FlagMeterLevelProgress.png");
        name.setPosition(CCDirector.sharedDirector().getWinSize().width - 80, 5);
        map.getParent().addChild(name);
        name.setScale(0.6f);
    }

}
