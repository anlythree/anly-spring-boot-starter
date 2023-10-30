package com.anly.common.api;

import com.anly.common.constant.AnlyScaConstant;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应消息报文
 *
 * @param <T> 　T对象
 * @author anlythree
 */
@Data
@Getter
@Schema(description = "统一响应消息报文")
public class Result<T> implements Serializable {

	@Serial
	private static final long serialVersionUID = 5191497917447621323L;

	@Schema(description = "状态码", required = true)
	private int code;

	@Schema(description = "消息内容", required = true)
	private String msg;

	@Schema(description = "时间戳", required = true)
	private long time;

	@Schema(description = "业务数据")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	private Result() {
		this.time = System.currentTimeMillis();
	}

	private Result(IResultCode resultCode) {
		this(resultCode, null, resultCode.getMsg());
	}

	private Result(IResultCode resultCode, String msg) {
		this(resultCode, null, msg);
	}

	private Result(IResultCode resultCode, T data) {
		this(resultCode, data, resultCode.getMsg());
	}

	private Result(IResultCode resultCode, T data, String msg) {
		this(resultCode.getCode(), data, msg);
	}

	private Result(int code, T data, String msg) {
		this.code = code;
		this.data = data;
		this.msg = msg;
		this.time = System.currentTimeMillis();
	}

	public static <T> Result<T> success() {
		return new Result<>(ResultCode.SUCCESS);
	}

	public static <T> Result<T> success(IResultCode resultCode) {
		return new Result<>(resultCode);
	}

	public static <T> Result<T> success(String msg) {
		return new Result<>(ResultCode.SUCCESS, msg);
	}

	public static <T> Result<T> success(IResultCode resultCode, String msg) {
		return new Result<>(resultCode, msg);
	}

	public static <T> Result<T> data(T data) {
		return data(data, AnlyScaConstant.DEFAULT_SUCCESS_MESSAGE);
	}

	public static <T> Result<T> data(T data, String msg) {
		return data(ResultCode.SUCCESS.code, data, msg);
	}

	public static <T> Result<T> data(int code, T data, String msg) {
		return new Result<>(code, data, data == null ? AnlyScaConstant.DEFAULT_NULL_MESSAGE : msg);
	}

	public static <T> Result<T> fail() {
		return new Result<>(ResultCode.FAILURE, ResultCode.FAILURE.getMsg());
	}

	public static <T> Result<T> fail(String msg) {
		return new Result<>(ResultCode.FAILURE, msg);
	}

	public static <T> Result<T> fail(int code, String msg) {
		return new Result<>(code, null, msg);
	}

	public static <T> Result<T> fail(IResultCode resultCode) {
		return new Result<>(resultCode);
	}

	public static <T> Result<T> fail(IResultCode resultCode, String msg) {
		return new Result<>(resultCode, msg);
	}

	public static <T> Result<T> condition(boolean flag) {
		return flag ? success(AnlyScaConstant.DEFAULT_SUCCESS_MESSAGE) : fail(AnlyScaConstant.DEFAULT_FAIL_MESSAGE);
	}
}
