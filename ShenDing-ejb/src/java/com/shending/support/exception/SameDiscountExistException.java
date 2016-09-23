

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support.exception;

import javax.ejb.ApplicationException;

/**
 *
 * @author yin.weilong
 */
@ApplicationException(rollback = true) 
public class SameDiscountExistException extends Exception {

    /**
     * Creates a new instance of <code>AlreadyPaidException</code> without detail message.
     */
    public SameDiscountExistException() {
    }

    /**
     * Constructs an instance of <code>AlreadyPaidException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SameDiscountExistException(String msg) {
        super(msg);
    }
}
