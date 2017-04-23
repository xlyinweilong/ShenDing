package com.shending.support.enums;

import org.apache.commons.lang.StringUtils;

/**
 * 发送期限
 *
 * @author yin.weilong
 */
public enum AdLimitTypeEnum {

    DAY_10, //10天
    DAY_15, //半个月
    MONTH_1, //一个月
    MONTH_2,//两个月
    MONTH_3;//一个季度

    public String getMean() {
        switch (this) {
            case DAY_10:
                return "10天";
            case DAY_15:
                return "半月";
            case MONTH_1:
                return "一月";
            case MONTH_2:
                return "两月";
            case MONTH_3:
                return "季度";
            default:
                return "";
        }
    }

    /**
     * 根据文字获取类型
     *
     * @param mean
     * @return
     */
    public static AdLimitTypeEnum getEnum(String mean) {
        switch (StringUtils.trimToEmpty(mean)) {
            case "10天":
                return DAY_10;
            case "半月":
                return DAY_15;
            case "一月":
                return MONTH_1;
            case "两月":
                return MONTH_2;
            case "季度":
                return MONTH_3;
            default:
                return null;
        }
    }
}
