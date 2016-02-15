/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author arale
 */
public class ModuleAlreadyRegisteredMDALException extends MDALException  {
     public static final String MODULE_ALREADY_REGISTERED_MSG = "Abstract operation already exists";

    public ModuleAlreadyRegisteredMDALException(){

    }

    public ModuleAlreadyRegisteredMDALException(String message){
        super(message);
    }

}
