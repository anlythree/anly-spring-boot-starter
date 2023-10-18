package com.anly.common.exception;

import com.anly.common.api.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 认证异常
 *
 * @author anlythree
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthException extends AnlyException {

	private static final long serialVersionUID = 4973608667652183916L;

	/**
	 * 认证类型 0:没有权限,1:角色不符
	 */
	private Integer authType;

	public AuthException(String message, Integer authType) {
		super(message);
		this.authType = authType;
	}
}
