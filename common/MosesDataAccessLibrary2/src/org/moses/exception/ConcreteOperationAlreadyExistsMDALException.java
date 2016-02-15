/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author dante
 */
public class ConcreteOperationAlreadyExistsMDALException extends MDALException{

    public static final String CONCRETE_OPERATION_EXISTS_MSG = "Concrete operation already exists";

    public ConcreteOperationAlreadyExistsMDALException() {
    }

    public ConcreteOperationAlreadyExistsMDALException(String message) {
        super(message);
    }

}
