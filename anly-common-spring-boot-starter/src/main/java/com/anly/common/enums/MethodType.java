package com.anly.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 请求类型
 *
 * @author anlythree
 */
@Getter
@AllArgsConstructor
public enum MethodType {

	/**
	 * 请求类型
	 * GET
	 * PUT
	 * POST
	 * DELETE
	 * OPTIONS
	 */
	GET(false),
	PUT(true),
	POST(true),
	DELETE(false),
	HEAD(false),
	OPTIONS(false);

	private final boolean hasContent;

}
