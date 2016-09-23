package com.shending.support.enums;

/**
 * 发送级别
 *
 * @author yin.weilong
 */
public enum AdPushTypeEnum {

    PRIMARY, //初级
    SUPER; //高级

    public String getMean() {
        switch (this) {
            case PRIMARY:
                return "初级";
            case SUPER:
                return "高级";
            default:
                return "";
        }
    }
}
