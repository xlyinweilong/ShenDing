package com.shending.support.enums;

/**
 * 广告级别
 *
 * @author yin.weilong
 */
public enum AdLevelEnum {

    PRIMARY_300, //300初级
    PRIMARY_400, //400初级
    SUPER_300,//300高级
    SUPER_400; //400高级

    public String getMean() {
        switch (this) {
            case PRIMARY_300:
                return "300初级";
            case PRIMARY_400:
                return "400初级";
            case SUPER_300:
                return "300高级";
            case SUPER_400:
                return "400高级";
            default:
                return "";
        }
    }
}
