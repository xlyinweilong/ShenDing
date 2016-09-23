package com.shending.support.enums;

/**
 * 产品
 *
 * @author yin.weilong
 */
public enum ProductEnum {

    MA_KA,//玛咖加强型 
    MA_KA_WEN_HE,//玛咖温和型
    LAN_MEI_HUA_QING_SU;//蓝莓花青素

    /**
     * 获取文字含义
     *
     * @return
     */
    public String getMean() {
        switch (this) {
            case MA_KA:
                return "玛咖加强型";
            case MA_KA_WEN_HE:
                return "玛咖温和型";
            case LAN_MEI_HUA_QING_SU:
                return "蓝莓花青素";
            default:
                return "";
        }
    }
}
