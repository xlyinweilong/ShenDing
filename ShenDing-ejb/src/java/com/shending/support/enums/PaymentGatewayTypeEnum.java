/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.enums;

/**
 * 网关类型
 *
 * @author yin.weilong
 */
public enum PaymentGatewayTypeEnum {

    ALIPAY,
    ALIPAY_BANK,
    UNIONPAY,
    CASH,
    POST_CARD,//刷卡
    ICBC,//工行
    ABC,//农行
    CCBC,//建行
    WECHAT_PAY,//微信支付
    BANK_TRANSFER;

    public String getMean() {
        switch (this) {
            case ALIPAY:
                return "支付宝";
            case POST_CARD:
                return "刷卡";
            case WECHAT_PAY:
                return "微信";
            case ICBC:
                return "工行";
            case ABC:
                return "农行";
            case CCBC:
                return "建行";
            case CASH:
                return "现金";
            case ALIPAY_BANK:
                return "支付宝网银";
            case BANK_TRANSFER:
                return "银行转账";
            case UNIONPAY:
                return "银联在线";
            default:
                return "";
        }
    }

}
