/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.exception;

/**
 *
 * @author yin.weilong
 */
public class SamePriceExistException extends Exception {

    /**
     * Creates a new instance of <code>AlreadyPaidException</code> without detail message.
     */
    public SamePriceExistException() {
    }

    /**
     * Constructs an instance of <code>AlreadyPaidException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SamePriceExistException(String msg) {
        super(msg);
    }
}
