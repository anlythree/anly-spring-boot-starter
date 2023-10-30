package com.anly.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回码实现
 *
 * @author anlythree
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {

	SUCCESS(200, "操作成功"),

	FAILURE(400, "业务异常"),

	PARAM_ERROR(4000, "参数错误"),

	UNAUTHORIZED(401,"需要身份验证"),

	NOT_FOUND(404, "请求资源不存在"),

	TOO_MANY_REQUESTS(429, "Too Many Requests"),

	SERVICE_UNAVAILABLE(503, "服务不可用"),

	ERROR(500, "服务异常"),

	;

	/**
	 * 状态码
	 */
	final int code;
	/**
	 * 消息内容
	 */
	final String msg;
}
