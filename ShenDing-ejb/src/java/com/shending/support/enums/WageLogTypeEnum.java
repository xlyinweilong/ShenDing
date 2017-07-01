package com.shending.support.enums;

import org.apache.commons.lang.StringUtils;

/**
 * 广告日志记录类型
 *
 * @author yin.weilong
 */
public enum WageLogTypeEnum {

    PRODUCT,//产品销售
    RECOMMEND,//推荐代理
    GRAND_SLAM,//大满贯
    MIN_SHENG_BANK,//民生銀行
    SEND_AND_ACCEPT,//发送，接受
    SEND, //发送者
    ACCEPT; //接广告的人，业务员

    public String getMean() {
        switch (this) {
            case SEND:
                return "发";
            case ACCEPT:
                return "接";
            case SEND_AND_ACCEPT:
                return "自接自发";
            case RECOMMEND:
                return "推荐代理";
            case PRODUCT:
                return "产品销售";
            case GRAND_SLAM:
                return "大满贯";
            case MIN_SHENG_BANK:
                return "民生銀行";
            default:
                return "";
        }
    }

    public static WageLogTypeEnum getEnum(String mean) {
        switch (StringUtils.trim(mean)) {
            case "发":
                return SEND;
            case "接":
                return ACCEPT;
            case "自接自发":
                return SEND_AND_ACCEPT;
            case "推荐代理":
                return RECOMMEND;
            case "产品销售":
                return PRODUCT;
            case "大满贯":
                return GRAND_SLAM;
            case "民生銀行":
                return MIN_SHENG_BANK;
            default:
                return null;
        }
    }
}
