package com.example.fangyi.pvzhm_fangyi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.fangyi.pvzhm_fangyi.layer.FightLayer;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

/**
 * Created by FANGYI on 2016/8/4.
 */
public class MainActivity extends AppCompatActivity {

    private CCDirector ccDirector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CCGLSurfaceView surfaceView = new CCGLSurfaceView(this);
        setContentView(surfaceView);

        ccDirector = CCDirector.sharedDirector();
        ccDirector.attachInView(surfaceView);//开启线程

        ccDirector.setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);//设置游戏水平方向
        ccDirector.setDisplayFPS(true);//展示帧率
        ccDirector.setScreenSize(480,320);//自动屏幕适配

        CCScene ccScene = CCScene.node();
        ccScene.addChild(new FightLayer());


        ccDirector.runWithScene(ccScene);//运行场景
    }

    @Override
    protected void onResume() {
        super.onResume();
        ccDirector.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ccDirector.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ccDirector.end();
    }
}
