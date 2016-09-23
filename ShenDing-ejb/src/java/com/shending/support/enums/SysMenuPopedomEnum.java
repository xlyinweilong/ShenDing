/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.enums;

/**
 * 菜单分类
 *
 * @author yin.weilong
 */
public enum SysMenuPopedomEnum {

    COMMON, SUPER, ROOT, ALL;

    /**
     * 获取中文意思
     *
     * @return
     */
    public String getMean() {
        switch (this) {
            case ROOT:
                return "ROOT专用";
            case SUPER:
                return "超级管理员菜单";
            case ALL:
                return "所有人可用";
            default:
                return "普通菜单";
        }
    }
}
