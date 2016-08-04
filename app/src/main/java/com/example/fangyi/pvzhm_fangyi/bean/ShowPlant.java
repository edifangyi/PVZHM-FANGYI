package com.example.fangyi.pvzhm_fangyi.bean;

import org.cocos2d.nodes.CCSprite;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by FANGYI on 2016/8/4.
 */

public class ShowPlant {
    static Map<Integer,HashMap<String,String>> db;

    //  查询数据库 获取植物
    static{
        // 模拟了数据库
        db=new HashMap<>();
        String format= "image/fight/chose/choose_default%02d.png";
        for(int i=1;i<=9;i++){
            HashMap<String, String> value=new HashMap<>();
            value.put("path",String.format(format, i));
            value.put("sun", 50+"");
            db.put(i, value);

        }
    }
    //-----------------
    private CCSprite showSprite;
    private CCSprite bgSprite;
    public ShowPlant(int id){
        HashMap<String, String> hashMap = db.get(id);
        String path = hashMap.get("path");
        showSprite = CCSprite.sprite(path);
        showSprite.setAnchorPoint(0,0);

        bgSprite=CCSprite.sprite(path);
        bgSprite.setOpacity(150);//设置半透明
        bgSprite.setAnchorPoint(0,0);

    }

    public CCSprite getBgSprite() {
        return bgSprite;
    }

    public CCSprite getShowSprite() {
        return showSprite;
    }
}
