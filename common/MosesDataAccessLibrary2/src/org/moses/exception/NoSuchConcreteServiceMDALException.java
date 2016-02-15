/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author dante
 */
public class NoSuchConcreteServiceMDALException extends MDALException{

    public static final String NO_CONCRETE_SERVICE_MSG = "No such concrete service";

    public NoSuchConcreteServiceMDALException() {
    }

    public NoSuchConcreteServiceMDALException(String message) {
        super(message);
    }

}
