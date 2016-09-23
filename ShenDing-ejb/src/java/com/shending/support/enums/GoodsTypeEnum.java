package com.shending.support.enums;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品的平台类型
 *
 * @author yin.weilong
 */
public enum GoodsTypeEnum {

    GOVERNMENT_DIRECTLY, //直辖市
    PROVINCIAL_CAPITAL, //省会
    HOT,//热点
    MUNICIPAL_DISTRICTS,//直辖区
    PREFECTURE,//地级市
    COUNTY_CITY,//县级市
    SELF_COUNTY,//自治县
    COUNTY;//县

    public String getMean() {
        switch (this) {
            case GOVERNMENT_DIRECTLY:
                return "直辖市";//市辖区
            case PROVINCIAL_CAPITAL:
                return "省会";
            case HOT:
                return "热点";
            case PREFECTURE:
                return "地级市";
            case COUNTY_CITY:
                return "县级市";
            case COUNTY:
                return "县";
            case SELF_COUNTY:
                return "自治县";
            case MUNICIPAL_DISTRICTS:
                return "直辖区";
            default:
                return "";
        }
    }

    public BigDecimal getRate() {
        if (this.equals(GOVERNMENT_DIRECTLY) || this.equals(PROVINCIAL_CAPITAL)) {
            return new BigDecimal("0.3");
        } else {
            return new BigDecimal("0.15");
        }
    }

    public String getPrice() {
        switch (this) {
            case GOVERNMENT_DIRECTLY:
            case PROVINCIAL_CAPITAL:
                return "9000";
            case HOT:
            case PREFECTURE:
                return "8000";
            case COUNTY_CITY:
            case MUNICIPAL_DISTRICTS:
            case COUNTY:
            case SELF_COUNTY:
                return "6000";
            default:
                return "0";
        }
    }

    public String getPriceMakeFriends() {
        switch (this) {
            case GOVERNMENT_DIRECTLY:
                return "9000";
            case PROVINCIAL_CAPITAL:
                return "8000";
            case HOT:
                return "7000";
            case MUNICIPAL_DISTRICTS:
                return "7000";
            case PREFECTURE:
                return "6000";
            case COUNTY_CITY:
                return "5000";
            case COUNTY:
            case SELF_COUNTY:
                return "4000";
            default:
                return "0";
        }
    }

    public static List getList() {
        List list = new ArrayList();
        for (GoodsTypeEnum gte : GoodsTypeEnum.values()) {
            Map map = new HashMap();
            map.put("key", gte.name());
            map.put("name", gte.getMean());
            map.put("price", Long.parseLong(gte.getPrice()));
            list.add(map);
        }
        return list;
    }
}
