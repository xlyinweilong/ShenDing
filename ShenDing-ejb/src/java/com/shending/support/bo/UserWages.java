/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.bo;

/**
 * 用户工资BO
 *
 * @author yin
 */
public class UserWages {

    /**
     * 用户
     */
    private Long uid;

    /**
     * 名称
     */
    private String name;

    /**
     * 余额
     */
    private String balance;

    /**
     * 押金
     */
    private String deposit;

    /**
     * 银行类型
     */
    private String bankType;

    /**
     * 银行卡号
     */
    private String bankCardCode;

    /**
     * 广告提成
     */
    private String userAmount = "0";

    /**
     * 广告返还
     */
    private String userBalanceAmount = "0";

    /**
     * 产品工资
     */
    private String porducetAmount = "0";

    /**
     * 推荐费用
     */
    private String recommendAmount = "0";

    /**
     * 化妆品提成
     */
    private String cosmeticsAmount = "0";

    /**
     * 大满贯提成
     */
    private String grandSlamAmount = "0";

    /**
     * 代理的地区
     */
    private String userOrderNames;

    /**
     * 是否含有回收
     */
    private String back;

    /**
     * 获取广告的提成
     *
     * @return
     */
    public String getAdAmount() {
        return Double.parseDouble(userAmount) + Double.parseDouble(userBalanceAmount) + "";
    }

    /**
     * 获取总共的提成
     *
     * @return
     */
    public String getTotalAmount() {
        return Double.parseDouble(this.getAdAmount()) + Double.parseDouble(porducetAmount) + Double.parseDouble(recommendAmount) + Double.parseDouble(cosmeticsAmount) + Double.parseDouble(grandSlamAmount) + "";
    }

    public String getUserOrderNames() {
        return userOrderNames;
    }

    public void setUserOrderNames(String userOrderNames) {
        this.userOrderNames = userOrderNames;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getGrandSlamAmount() {
        return grandSlamAmount;
    }

    public void setGrandSlamAmount(String grandSlamAmount) {
        this.grandSlamAmount = grandSlamAmount;
    }

    public String getCosmeticsAmount() {
        return cosmeticsAmount;
    }

    public void setCosmeticsAmount(String cosmeticsAmount) {
        this.cosmeticsAmount = cosmeticsAmount;
    }

    public String getRecommendAmount() {
        return recommendAmount;
    }

    public void setRecommendAmount(String recommendAmount) {
        this.recommendAmount = recommendAmount;
    }

    public String getPorducetAmount() {
        return porducetAmount;
    }

    public void setPorducetAmount(String porducetAmount) {
        this.porducetAmount = porducetAmount;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankCardCode() {
        return bankCardCode;
    }

    public void setBankCardCode(String bankCardCode) {
        this.bankCardCode = bankCardCode;
    }

    public String getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(String userAmount) {
        this.userAmount = userAmount;
    }

    public String getUserBalanceAmount() {
        return userBalanceAmount;
    }

    public void setUserBalanceAmount(String userBalanceAmount) {
        this.userBalanceAmount = userBalanceAmount;
    }

}
