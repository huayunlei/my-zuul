/*
 * Copyright (C) 2006-2016 AiJia All rights reserved
 * Author: zhang
 * Date: 2017年5月18日
 * Description:UserVo.java 
 */
package com.ihomefnt.sky.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

	/**
	 * 主键
	 */
	private Integer id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 手机号码
	 */
	private String mobile;

	/**
	 * 用户类型：1.普通用户 2.管理员
	 */
	private Short type;

	/**
	 * 状态：0.未激活 1.可用 2.锁定
	 */
	private Integer status;

	/**
	 * 微信id
	 */
	private String wechatId;

	/**
	 * 地区
	 */
	private String region;

	/**
	 * 注册时间
	 */
	private Date regTime;

}
