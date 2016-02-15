/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author arale
 */
public class NoModulesRegisteredMDALException extends MDALException {
    public static final String NO_MODULES_REGISTERED_MSG = "No modules registered";

    public NoModulesRegisteredMDALException(){

    }

    public NoModulesRegisteredMDALException(String message){
        super(message);
    }
}
