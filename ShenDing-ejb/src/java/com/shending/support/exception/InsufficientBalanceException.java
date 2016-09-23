/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.exception;

/**
 *
 * @author yin.weilong
 * 余额不足
 */
public class InsufficientBalanceException extends Exception {

    public InsufficientBalanceException() {
    }

    public InsufficientBalanceException(String msg) {
        super(msg);
    }
}
