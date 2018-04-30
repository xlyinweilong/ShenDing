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
public class PlaceWages {

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;


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
     * 会员提成
     */
    private String vipAmount = "0";


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
        return Double.parseDouble(this.getAdAmount()) + Double.parseDouble(porducetAmount) + Double.parseDouble(recommendAmount) + Double.parseDouble(cosmeticsAmount) + Double.parseDouble(grandSlamAmount) + Double.parseDouble(vipAmount) + "";
    }

    public String getVipAmount() {
        return vipAmount;
    }

    public void setVipAmount(String vipAmount) {
        this.vipAmount = vipAmount;
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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    
    

}
