package com.shending.support.enums;

/**
 * 平台分类，区分是交友平台还是便民平台
 *
 * @author yin.weilong
 */
public enum CategoryEnum {

    MAKE_FRIENDS, //交友
    SERVICE_PEOPLE; //便民

    public String getMean() {
        switch (this) {
            case MAKE_FRIENDS:
                return "交友平台";
            case SERVICE_PEOPLE:
                return "便民平台";
            default:
                return "";
        }
    }

}
