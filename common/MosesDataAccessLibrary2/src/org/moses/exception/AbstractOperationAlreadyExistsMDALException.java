/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author dante
 */
public class AbstractOperationAlreadyExistsMDALException extends MDALException {
    public static final String ABSTRACT_OPERATION_EXISTS_MSG = "Abstract operation already exists";

    public AbstractOperationAlreadyExistsMDALException() {
    }

    public AbstractOperationAlreadyExistsMDALException(String message) {
        super(message);
    }

}
