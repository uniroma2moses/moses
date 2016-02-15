/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.exception;

/**
 *
 * @author francesco
 */
public class SLAAlreadyExistsMDALException extends MDALException {
    public static final String SLA_EXIST_MSG = "SLA already exists";

    public SLAAlreadyExistsMDALException() {
    }

    public SLAAlreadyExistsMDALException(String message) {
        super(message);
    }
}
