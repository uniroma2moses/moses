/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author arale
 */
public class NoSuchAddressMDALException extends MDALException {
    public static final String NO_SUCH_ADDRESS_MSG = "No address like this in table";

    public NoSuchAddressMDALException(){}

    public NoSuchAddressMDALException(String message){
        super(message);
    }

}
