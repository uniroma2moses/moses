/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author dante
 */
public class ProcessAlreadyExistsMDALException extends MDALException{

    public static final String PROCESS_EXISTS = "Process already exists";
    public ProcessAlreadyExistsMDALException(String message) {
        super(message);
    }

    public ProcessAlreadyExistsMDALException() {
    }

}
