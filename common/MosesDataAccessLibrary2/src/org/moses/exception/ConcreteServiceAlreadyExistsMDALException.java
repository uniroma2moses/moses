/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author dante
 */
public class ConcreteServiceAlreadyExistsMDALException extends MDALException{

    public static final String CONCRETE_SERVICE_EXISTS_MSG = "Concrete service already exists";

    public ConcreteServiceAlreadyExistsMDALException() {
    }

    public ConcreteServiceAlreadyExistsMDALException(String message) {
        super(message);
    }

}
