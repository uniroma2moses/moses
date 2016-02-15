/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.exception;

/**
 *
 * @author stefano
 */
public class NoSuchProcessMDALException extends MDALException {

    public static final String NO_PROCESS_MSG = "No such process";

    public NoSuchProcessMDALException() {
    }

    public NoSuchProcessMDALException(String message) {
        super(message);
    }
}
