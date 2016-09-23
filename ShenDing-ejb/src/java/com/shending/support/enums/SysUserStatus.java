package com.shending.support.enums;

/**
 * 用户状态
 *
 * @author yin.weilong
 */
public enum SysUserStatus {

    NORMAL, PEDING, DISABLE;

    public String getMean() {
        switch (this) {
            case PEDING:
                return "待审批";
            case DISABLE:
                return "禁用";
            default:
                return "正常";
        }
    }
}
