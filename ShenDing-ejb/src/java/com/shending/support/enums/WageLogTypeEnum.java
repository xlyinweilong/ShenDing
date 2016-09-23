package com.shending.support.enums;

/**
 * 广告日志记录类型
 *
 * @author yin.weilong
 */
public enum WageLogTypeEnum {

    PRODUCT,//产品销售
    RECOMMEND,//推荐代理
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
            default:
                return "";
        }
    }
}
