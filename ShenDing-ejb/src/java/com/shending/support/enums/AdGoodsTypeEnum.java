package com.shending.support.enums;

/**
 * 平台级别
 *
 * @author yin.weilong
 */
public enum AdGoodsTypeEnum {

    LV_300, //300
    LV_400; //400

    public String getMean() {
        switch (this) {
            case LV_300:
                return "300";
            case LV_400:
                return "400";
            default:
                return "";
        }
    }
}
