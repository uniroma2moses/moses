/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author francesco
 */
public class NoSuchAbstractServiceMDALException extends MDALException {
    public static final String NO_ABSTRACT_SERVICE_MSG = "No such abstract service";

    public NoSuchAbstractServiceMDALException() {
    }

    public NoSuchAbstractServiceMDALException(String message) {
        super(message);
    }

}
