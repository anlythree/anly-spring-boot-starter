package com.anly.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 环境常量
 *
 * @author anlythree
 */

@Getter
@AllArgsConstructor
public enum EnvType {

	/**
	 * 环境变量
	 * LOCAL 本地
	 * DEV 开发
	 * TEST 测试
	 * PROD 生产
	 * DOCKER Docker
	 */
	LOCAL("local"),
	DEV("dev"),
	TEST("test"),
	GRAY("gray"),
	PRERELEASE("prerelease"),
	PROD("prod"),
	DOCKER("docker");

	private final String value;
}
