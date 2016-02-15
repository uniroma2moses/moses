/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.exception;

/**
 *
 * @author francesco
 */
public class NoSuchSLAMDALException extends MDALException {

    public static final String NO_SLA_MSG = "No such SLA";

    public NoSuchSLAMDALException() {
    }

    public NoSuchSLAMDALException(String message) {
        super(message);
    }
}
