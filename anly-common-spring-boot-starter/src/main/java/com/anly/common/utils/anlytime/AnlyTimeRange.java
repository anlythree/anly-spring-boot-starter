package com.anly.common.utils.anlytime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * anly时间范围
 *
 * @DATE: 2024/7/1
 * @USER: anlythree
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnlyTimeRange {

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 通过开始时间-结束时间生成
     *
     * @param timeRangeStr
     */
    public AnlyTimeRange(String timeRangeStr) {
        if (StringUtils.isEmpty(timeRangeStr)) {
            return;
        }
        String[] timeRangeSplit = timeRangeStr.split("-");
        this.startTime = AnlyTimeUtil.stringToTime(timeRangeSplit[0]);
        this.endTime = AnlyTimeUtil.stringToTime(timeRangeSplit[1]);
    }


    /**
     * 通过开始、结束字符串和指定格式转换
     *
     * @param startTime
     * @param endTime
     */
    public AnlyTimeRange(String startTime, String endTime, String formate) {
        this.startTime = AnlyTimeUtil.stringToTime(startTime, formate);
        this.endTime = AnlyTimeUtil.stringToTime(endTime, formate);
    }

    /**
     * 当前时间范围是否包含传入时间范围（当前时间范围是否比传入范围大）
     *
     * @param timeRange
     * @return
     */
    public boolean isCover(AnlyTimeRange timeRange) {
        // this的开始时间比param的开始时间早 && this的结束时间比param的结束时间晚
        return isT1EarlyThanT2(this.startTime, timeRange.getStartTime()) &&
                isT1EarlyThanT2(timeRange.getEndTime(), this.endTime);
    }

    /**
     * 当前时间范围是否与传入时间范围有交集
     *
     * @param timeRange
     * @return
     */
    public boolean isIntersection(AnlyTimeRange timeRange) {
        // 除了 (this的开始时间比param的结束时间晚 || this的结束时间比param的开始时间早) 这两种情况，其他都有交集
        return !(isT1EarlyThanT2(timeRange.getEndTime(), this.startTime) ||
                isT1EarlyThanT2(this.endTime, timeRange.getStartTime()));
    }


    /**
     * t1是否早于t2
     * 这里的t1和t2都可能为null，为null按照正无穷来算，都为null返回true
     * 时间相同也返回true
     *
     * @param t1
     * @param t2
     * @return
     */
    private boolean isT1EarlyThanT2(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null) {
            return true;
        } else if (t2 == null) {
            return false;
        } else {
            // t1和t2都不为null,比较这两个时间的先后
            Duration duration = AnlyTimeUtil.timeInterval(t1, t2);
            return !duration.isNegative();
        }
    }

}
