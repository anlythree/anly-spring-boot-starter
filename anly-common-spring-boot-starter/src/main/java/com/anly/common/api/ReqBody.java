package com.anly.common.api;

import com.anly.common.utils.AnlyTimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 请求体
 *
 * @author anlythree
 */
@Data
@Getter
@Schema(description = "公共请求体")
public class ReqBody implements Serializable {

    @Serial
    private static final long serialVersionUID = 259053381547310211L;

    /**
     * 客户端 (0:IOS，1:IpadOS，2:华为，3:小米，4:三星，5:魅族，6:索尼，7:web,8:未知)
     */
    @NotEmpty(message = "st为空")
    private Integer st;

    /**
     * app版本号 ，未知为-1
     */
    @NotEmpty(message = "cv为空")
    private String cv;

    /**
     * 客户端唯一标识
     */
    private String imei;

    /**
     * 设备型号
     */
    private String sn;

    /**
     * 请求发起时间
     */
    private String requestTime;

    public void setDefaultBasicField(){
        this.st = 0;
        this.cv = "-1";
        this.imei = "88-A4-C2-2F-7D-7E";
        this.sn = "iphone 15 pro";
        this.requestTime = AnlyTimeUtil.timeToString(LocalDateTime.now());
    }
}
