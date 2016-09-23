/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support;

/**
 *
 * @author yin.weilong
 */
public class NoPermException extends Exception {

    public NoPermException() {
    }

    public NoPermException(String msg) {
        super(msg);
    }
}
