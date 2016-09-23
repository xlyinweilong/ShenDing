/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.restful.json;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 返回的JSON
 *
 * @author yin.weilong
 */
public class BaseJson {

    private String success = "0";
    private String msg;
    private String data;

    public static BaseJson getBaseJson() {
        return new BaseJson();
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
