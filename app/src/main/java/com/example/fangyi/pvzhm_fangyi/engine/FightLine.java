package com.example.fangyi.pvzhm_fangyi.engine;

import com.example.fangyi.pvzhm_fangyi.base.AttackPlant;
import com.example.fangyi.pvzhm_fangyi.base.BaseElement;
import com.example.fangyi.pvzhm_fangyi.base.Bullet;
import com.example.fangyi.pvzhm_fangyi.base.Plant;
import com.example.fangyi.pvzhm_fangyi.base.Zombies;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.types.CGPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 把一行的操作 抽取出来
 * 添加僵尸 安放僵尸 僵尸攻击植物 植物攻击僵尸
 * Created by FANGYI on 2016/8/5.
 */

public class FightLine {
    private int lineNum;

    public FightLine(int lineNum) {
        this.lineNum = lineNum;

        CCScheduler.sharedScheduler().schedule("attackPlant", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("createBullet", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("attackZombies", this, 0.1f, false);
    }

    private List<Zombies> zombiesLists = new ArrayList<>();
    private Map<Integer, Plant> plants = new HashMap<>();// 管理添加的植物
    // key当前植物所对应的列号
    private List<AttackPlant> attackPlants = new ArrayList<>();


    public void addZombies(final Zombies zombies) {
        zombiesLists.add(zombies);
        zombies.setDieListener(new BaseElement.DieListener() {

            @Override
            public void die() {
                zombiesLists.remove(zombies);
            }
        });
    }

    /**
     * 判断该列上 是否有植物
     *
     * @param row
     */
    public boolean containsRow(int row) {
        return plants.containsKey(row);
    }

    public void addPlant(final Plant plant) {
        plants.put(plant.getRow(), plant);
        if (plant instanceof AttackPlant) { // 如果发现植物是一个攻击类型的植物,添加到攻击类型植物的集合中
            attackPlants.add((AttackPlant) plant);
        }

        plant.setDieListener(new BaseElement.DieListener() {

            @Override
            public void die() {
                plants.remove(plant.getRow());
                if (plant instanceof AttackPlant) {
                    attackPlants.remove(plant);
                }
            }
        });

    }
    // 需要一直判断
    public void attackPlant(float t) {
        if (zombiesLists.size() > 0 && plants.size() > 0) { // 保证当前行上既有僵尸又有植物
            for (Zombies zombies : zombiesLists) {
                CGPoint position = zombies.getPosition();
                int row = (int) (position.x / 46 - 1); // 获取到僵尸所在的列
                Plant plant = plants.get(row);
                if (plant != null) {
                    zombies.attack(plant);//僵尸攻击植物
                }
            }
        }
    }

    //创建子弹
    public void createBullet(float t) {
        if (zombiesLists.size() > 0 && attackPlants.size() > 0) {
            // 创建子弹
            for (AttackPlant attackPlant : attackPlants) {
                attackPlant.createBullet();// 遍历所有攻击类型植物 创建子弹
            }
        }
    }

    // 判断子弹和僵尸是否产生了碰撞
    public void attackZombies(float t) {
        if (zombiesLists.size() > 0 && attackPlants.size() > 0) {
            for (Zombies zombies : zombiesLists) {
                float x = zombies.getPosition().x;
                float left = x - 20;
                float right = x + 20;
                for (AttackPlant attackPlant : attackPlants) {
                    List<Bullet> bullets = attackPlant.getBullets();
                    for (Bullet bullet : bullets) {
                        float bulletX = bullet.getPosition().x;
                        if (bulletX > left && bulletX < right) {
                            //产生了碰撞
                            zombies.attacked(bullet.getAttack());// 僵尸被攻击, 参数 攻击力
//							bullet.removeSelf()
                            bullet.setVisible(false);
                            bullet.setAttack(0);
                        }
                    }
                }
            }
        }
    }
}
