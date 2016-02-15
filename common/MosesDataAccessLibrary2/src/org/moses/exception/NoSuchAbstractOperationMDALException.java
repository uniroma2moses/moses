/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.exception;

/**
 *
 * @author francesco
 */
public class NoSuchAbstractOperationMDALException extends MDALException {

    public static final String NO_ABSTRACT_OPERATION_MSG = "No abstract operation";

    public NoSuchAbstractOperationMDALException() {
    }

    public NoSuchAbstractOperationMDALException(String message) {
        super(message);
    }
}
