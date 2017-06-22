/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.enums;

import org.apache.commons.lang.StringUtils;

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
    MIN_SHENG,//民生銀行
    WECHAT_PAY,//微信支付
    BANK_TRANSFER;

    public String getMean() {
        switch (this) {
            case MIN_SHENG:
                return "民生银行";
            case ALIPAY:
                return "支付宝";
            case POST_CARD:
                return "刷卡";
            case WECHAT_PAY:
                return "微信支付";
            case ICBC:
                return "工行";
            case ABC:
                return "农行";
            case CCBC:
                return "建行";
            case CASH:
                return "现金支付";
            case ALIPAY_BANK:
                return "支付宝网银";
            case BANK_TRANSFER:
                return "银行汇款";
            case UNIONPAY:
                return "银联在线";
            default:
                return "";
        }
    }

    public static PaymentGatewayTypeEnum getEnum(String mean) {
        switch (StringUtils.trim(mean)) {
            case "民生银行":
                return MIN_SHENG;
            case "支付宝":
                return ALIPAY;
            case "刷卡":
                return POST_CARD;
            case "微信支付":
                return WECHAT_PAY;
            case "工行":
                return ICBC;
            case "农行":
                return ABC;
            case "建行":
                return CCBC;
            case "现金支付":
                return CASH;
            case "支付宝网银":
                return ALIPAY_BANK;
            case "银行汇款":
                return BANK_TRANSFER;
            case "银联在线":
                return UNIONPAY;
            default:
                return null;
        }
    }

}
