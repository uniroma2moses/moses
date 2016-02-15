/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author francesco
 */
public class AbstractServiceAlreadyExistsMDALException extends MDALException {
    public static final String ABSTRACT_SERVICE_EXISTS_MSG = "Abstract service already exists";

    public AbstractServiceAlreadyExistsMDALException() {
    }

    public AbstractServiceAlreadyExistsMDALException(String message) {
        super(message);
    }

}
