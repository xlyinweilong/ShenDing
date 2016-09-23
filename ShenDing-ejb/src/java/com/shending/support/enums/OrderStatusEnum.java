/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.enums;

/**
 * 订单状态
 *
 * @author yin.weilong
 */
public enum OrderStatusEnum {

    //0.初始化 1.待支付 2.待确认 3.定金 4.支付成功 5.支付失败 6.失效(退款) 7.支付超时 8.终止 9.等待补充合同
    INIT, PENDING_PAYMENT, PENDING_PAYMENT_CONFIRM, EARNEST, SUCCESS, FAILURE, INVALID, PAYMENT_TIMEOUT, TERMINATION, WAIT_SIGN_CONTRACT;

    public String getMean() {
        switch (this) {
            case INIT:
                return "初始化";
            case EARNEST:
                return "定金";
            case PENDING_PAYMENT:
                return "待支付";
            case PENDING_PAYMENT_CONFIRM:
                return "支付待确认";
            case SUCCESS:
                return "合同完成";
            case FAILURE:
                return "支付失败";
            case INVALID:
                return "失效(退款)";
            case PAYMENT_TIMEOUT:
                return "支付超时";
            case TERMINATION:
                return "终止";
            case WAIT_SIGN_CONTRACT:
                return "等待补充合同";
            default:
                return null;
        }
    }

}
