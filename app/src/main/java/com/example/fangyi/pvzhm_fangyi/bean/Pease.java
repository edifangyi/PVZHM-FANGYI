package com.example.fangyi.pvzhm_fangyi.bean;

import com.example.fangyi.pvzhm_fangyi.base.Bullet;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;



public class Pease extends Bullet {

	public Pease() {
		super("image/fight/bullet.png");
		setScale(0.65f);
	}

	@Override
	public void move() {
		//获取到当前子弹的坐标
		CGPoint position = getPosition();
		CGPoint targetPostion=CCNode.ccp(CCDirector.sharedDirector().winSize().width, position.y);
		float t=CGPointUtil.distance(position, targetPostion)/speed;
		CCMoveTo moveTo=CCMoveTo.action(t, targetPostion);
		CCSequence sequence=CCSequence.actions(moveTo, CCCallFunc.action(this, "destroy"));
		
		this.runAction(sequence);
	}

}
