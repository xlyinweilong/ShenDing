package com.shending.support.enums;

/**
 * 商品的状态
 *
 * @author yin.weilong
 */
public enum OrderRecordTypeEnum {

    EARNEST, //定金
    ALL_PAYMENT,//全款
    FINAL_PAYMENT; //尾款

    public String getMean() {
        switch (this) {
            case EARNEST:
                return "定金";
            case FINAL_PAYMENT:
                return "尾款";
            case ALL_PAYMENT:
                return "全款";
            default:
                return "";
        }
    }

}
