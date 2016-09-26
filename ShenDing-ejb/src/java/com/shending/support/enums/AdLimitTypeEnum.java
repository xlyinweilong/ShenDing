package com.shending.support.enums;

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
                return "半个月";
            case MONTH_1:
                return "一个月";
            case MONTH_2:
                return "两个月";
            case MONTH_3:
                return "一个季度";
            default:
                return "";
        }
    }
}
