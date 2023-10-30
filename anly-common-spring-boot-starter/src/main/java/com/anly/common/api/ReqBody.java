package com.anly.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 请求体
 *
 * @param <T> 　T业务入参
 * @author anlythree
 */
@Data
@Getter
@Schema(description = "请求体")
public class ReqBody<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 259053381547310211L;
    /**
     * 客户端 (0:ios,1:ipadOs,2:安卓,3:web,4:未知)
     */
    private String st;

    /**
     * app版本号
     */
    private String cv;

    @Schema(description = "业务入参")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T reqData;
}
