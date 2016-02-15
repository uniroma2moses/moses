/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.exception;

/**
 *
 * @author stefano
 */
public class NoSuchSolutionMDALException extends MDALException {

    public static final String NO_SOLUTION_MSG = "No such solution for provided parameters";

    public NoSuchSolutionMDALException() {
    }

    public NoSuchSolutionMDALException(String message) {
        super(message);
    }
}
