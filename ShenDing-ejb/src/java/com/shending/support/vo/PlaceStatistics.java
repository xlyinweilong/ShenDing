/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.vo;

/**
 * 地理统计VO
 *
 * @author yin
 */
public class PlaceStatistics {

    /**
     * 地理名称
     */
    private String placeName;
    /**
     * 商品数量
     */
    private Long goodsCount;
    /**
     * 订单数量
     */
    private Long orderCount;
    /**
     * 用户数量
     */
    private Long uidCount;

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Long getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Long goodsCount) {
        this.goodsCount = goodsCount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getUidCount() {
        return uidCount;
    }

    public void setUidCount(Long uidCount) {
        this.uidCount = uidCount;
    }

}
