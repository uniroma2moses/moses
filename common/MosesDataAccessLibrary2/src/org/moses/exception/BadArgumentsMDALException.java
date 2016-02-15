/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.exception;

/**
 *
 * @author stefano
 */
public class BadArgumentsMDALException extends MDALException {

    public static final String BAD_ARGUMENTS_MSG = "Bad arguments";

    public BadArgumentsMDALException() {
    }

    public BadArgumentsMDALException(String message) {
        super(message);
    }
}
