/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.exception;

/**
 *
 * @author yin.weilong
 */
public class DoublePendingSealException extends Exception {

    /**
     * Creates a new instance of <code>FundCollectionStatusException</code> without detail message.
     */
    public DoublePendingSealException() {
    }

    /**
     * Constructs an instance of <code>FundCollectionStatusException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DoublePendingSealException(String msg) {
        super(msg);
    }
}
