package com.example.fangyi.pvzhm_fangyi.bean;

import com.example.fangyi.pvzhm_fangyi.base.DefancePlant;
import com.example.fangyi.pvzhm_fangyi.utils.CommonUtils;

import org.cocos2d.actions.base.CCAction;



public class Nut extends DefancePlant {

	public Nut() {
		super("image/plant/nut/p_3_01.png");
		baseAction();
	}

	@Override
	public void baseAction() {
		CCAction animate = CommonUtils.getAnimate("image/plant/nut/p_3_%02d.png", 11, true);
		this.runAction(animate);
	}

}
