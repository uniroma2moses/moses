/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author arale
 */
public class NoViolationsRegisteredMDALException extends MDALException {
    public static final String NO_VIOLATION_MSG = "No violations in db";

    public NoViolationsRegisteredMDALException(){

    }

    public NoViolationsRegisteredMDALException(String message){
        super(message);
    }
}
