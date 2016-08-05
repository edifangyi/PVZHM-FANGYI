package com.example.fangyi.pvzhm_fangyi.base;
/**
 * 防御类型植物
 * @author Administrator
 *
 */
public abstract class DefancePlant extends Plant {

	public DefancePlant(String filepath) {
		super(filepath);
		life = 200;
	}

}
