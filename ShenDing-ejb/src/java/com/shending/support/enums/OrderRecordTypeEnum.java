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
    
     public static OrderRecordTypeEnum getEnum(String mean) {
        switch (mean) {
            case "定金":
                return EARNEST;
            case "尾款":
                return FINAL_PAYMENT;
            case "全款":
                return ALL_PAYMENT;
            default:
                return null;
        }
    }

}
