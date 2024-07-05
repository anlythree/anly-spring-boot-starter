package com.anly.common.exception;

import com.anly.common.api.IResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * 通用异常
 *
 * @author anlythree
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class AnlyException extends RuntimeException {


	@Serial
	private static final long serialVersionUID = 5782968730281544562L;

	/**
	 * 异常码
	 */
	private IResultCode errorCode;

	/**
	 * 原异常
	 */
	private Exception originException;

	public AnlyException(String message) {
		super(message);
	}

	public AnlyException(IResultCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public AnlyException(IResultCode errorCode) {
		super(errorCode.getMsg());
		this.errorCode = errorCode;
	}

	public AnlyException(Exception originException,String message) {
		super(message);
		this.originException = originException;
	}
}
