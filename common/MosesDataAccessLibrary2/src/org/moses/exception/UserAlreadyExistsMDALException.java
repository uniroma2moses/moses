/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.exception;

/**
 *
 * @author stefano
 */
public class UserAlreadyExistsMDALException extends MDALException {

    public final static String  USER_EXISTS_MSG = "This user already exists";
    
    public UserAlreadyExistsMDALException() {
    }

    public UserAlreadyExistsMDALException(String message) {
        super(message);
    }
}
