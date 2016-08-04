package com.example.fangyi.pvzhm_fangyi.layer;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;
/**
 * Created by FANGYI on 2016/8/4.
 */
public class BaseLayer extends CCLayer {
	protected CGSize winSize;
	public  BaseLayer(){
		winSize = CCDirector.sharedDirector().getWinSize();
	}
}
