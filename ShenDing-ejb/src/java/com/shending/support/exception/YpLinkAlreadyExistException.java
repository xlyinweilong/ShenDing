/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.exception;

/**
 *
 * @author yin.weilong
 */
public class YpLinkAlreadyExistException extends Exception {

    public YpLinkAlreadyExistException() {
    }

    public YpLinkAlreadyExistException(String msg) {
        super(msg);
    }
}