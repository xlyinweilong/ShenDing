package com.shending.support.enums;

/**
 * 商品的状态
 *
 * @author yin.weilong
 */
public enum OrderRecordTypeEnum {

    EARNEST, //定金
    FINAL_PAYMENT; //尾款

    public String getMean() {
        switch (this) {
            case EARNEST:
                return "定金";
            case FINAL_PAYMENT:
                return "尾款";
            default:
                return "";
        }
    }

}
