/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author dante
 */
public class NoSuchConcreteOperationMDALException extends MDALException {

    public static final String NO_CONCRETE_OPERATION_MSG = "No such concrete operation";

    public NoSuchConcreteOperationMDALException() {
    }

    public NoSuchConcreteOperationMDALException(String message) {
        super(message);
    }

}
