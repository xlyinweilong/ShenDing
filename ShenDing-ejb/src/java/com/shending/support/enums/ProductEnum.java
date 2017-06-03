package com.shending.support.enums;

/**
 * 产品
 *
 * @author yin.weilong
 */
public enum ProductEnum {

    MA_KA,//玛咖加强型 
    MA_KA_PU_TONG,//玛咖
    MA_KA_WEN_HE,//玛咖温和型
    LAN_MEI_HUA_QING_SU,//蓝莓花青素
    AOC_HONG_JIU,//AOC红酒
    AOP_HONG_JIU,//AOP红酒
    BIG_GIFTS,//大礼包
    SMALL_GIFTS;//小礼包

    /**
     * 获取文字含义
     *
     * @return
     */
    public String getMean() {
        switch (this) {
            case MA_KA:
                return "玛咖加强型";
            case MA_KA_PU_TONG:
                return "玛咖";
            case AOC_HONG_JIU:
                return "AOC红酒";
            case AOP_HONG_JIU:
                return "AOP红酒";
            case MA_KA_WEN_HE:
                return "玛咖温和型";
            case LAN_MEI_HUA_QING_SU:
                return "蓝莓花青素";
            case SMALL_GIFTS:
                return "小礼包";
            case BIG_GIFTS:
                return "大礼包";
            default:
                return "";
        }
    }

    public static ProductEnum getEnum(String mean) {
        switch (mean) {
            case "AOP红酒":
                return AOP_HONG_JIU;
            case "AOC红酒":
                return AOC_HONG_JIU;
            case "玛咖":
                return MA_KA_PU_TONG;
            case "玛咖加强型":
                return MA_KA;
            case "玛咖温和型":
                return MA_KA_WEN_HE;
            case "蓝莓花青素":
                return LAN_MEI_HUA_QING_SU;
            case "小礼包":
                return SMALL_GIFTS;
            case "大礼包":
                return BIG_GIFTS;
            default:
                return null;
        }
    }
}
