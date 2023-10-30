package com.anly.common.exception;

import com.anly.common.api.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

	private static final long serialVersionUID = 5782968730281544562L;

	private ResultCode errorCode;

	private Exception originException;

	private int status = INTERNAL_SERVER_ERROR.value();

	public AnlyException(String message) {
		super(message);
	}

	public AnlyException(ResultCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public AnlyException(Exception originException,String message) {
		super(message);
		this.originException = originException;
	}
}
