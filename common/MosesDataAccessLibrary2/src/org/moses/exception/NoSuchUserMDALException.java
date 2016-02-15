/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author stefano
 */
public class NoSuchUserMDALException extends MDALException{

    public static final String NO_USER_MSG = "No such user";
    
    public NoSuchUserMDALException() {
    }

    public NoSuchUserMDALException(String message) {
        super(message);
    }

}
