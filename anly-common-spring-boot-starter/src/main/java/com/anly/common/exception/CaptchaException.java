package com.anly.common.exception;

/**
 * 验证码异常
 *
 * @author anlythree
 */
public class CaptchaException extends AnlyScaException {

	private static final long serialVersionUID = -6550886498142636261L;

	public CaptchaException(String message) {
		super(message);
	}
}
