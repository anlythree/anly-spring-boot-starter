package com.anly.common.enums;

import lombok.Getter;

/**
 * 是或否的枚举，一般用在数据库字段，例如del_flag字段，char(1)，填写Y或N
 *
 * @author anlythree
 */
@Getter
public enum YesOrNotEnum {

    /**
     * 是
     */
    Y("Y", 1,"是"),

    /**
     * 否
     */
    N("N", 0,"否");

    private final String codeStr;

    private final int code;

    private final String message;

    YesOrNotEnum(String codeStr, int code, String message) {
        this.codeStr = codeStr;
        this.code = code;
        this.message = message;
    }

}
