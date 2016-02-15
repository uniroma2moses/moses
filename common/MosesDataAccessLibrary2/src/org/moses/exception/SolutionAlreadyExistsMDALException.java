/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author dante
 */
public class SolutionAlreadyExistsMDALException extends MDALException{

    public static final String SOLUTION_EXIST_MSG = "Solution already exists";

    public SolutionAlreadyExistsMDALException() {
    }

    public SolutionAlreadyExistsMDALException(String message) {
        super(message);
    }

}
