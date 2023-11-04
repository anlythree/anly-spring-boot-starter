package com.anly.common.dto;

import com.anly.common.enums.YesOrNotEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 当前登录用户
 */
@Data
public class CurrentUser implements Serializable {

    @Serial
    private static final long serialVersionUID = -1122574795037406660L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户编码
     */
    private String userNo;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mailBox;

    /**
     * 登录类型
     */
    private YesOrNotEnum loginType;


}
