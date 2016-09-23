package com.shending.support.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品的状态
 *
 * @author yin.weilong
 */
public enum GoodsStatusEnum {

    DRAFT, //草稿
    SALE, //出售中
    RESERVE,//预定
    LOCKED,//锁定
    WAIT_SIGN_CONTRACT,//等待签约合同
    SOLD_OUT,//已经出售
    INVENTORY;//不在售

    public String getMean() {
        switch (this) {
            case DRAFT:
                return "草稿";
            case SALE:
                return "出售中";
            case RESERVE:
                return "预定";
            case LOCKED:
                return "锁定";
            case WAIT_SIGN_CONTRACT:
                return "等待签约合同";
            case SOLD_OUT:
                return "已经出售";
            case INVENTORY:
                return "不在售";
            default:
                return "";
        }
    }

    public static List getList() {
        List list = new ArrayList();
        for (GoodsStatusEnum gse : GoodsStatusEnum.values()) {
            if (GoodsStatusEnum.SOLD_OUT.equals(gse) || GoodsStatusEnum.LOCKED.equals(gse) || GoodsStatusEnum.RESERVE.equals(gse)) {
                continue;
            }
            Map map = new HashMap();
            map.put("key", gse.name());
            map.put("name", gse.getMean());
            list.add(map);
        }
        return list;
    }

}
