/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author stefano
 */
public class NoSuchAgreementMDALException extends MDALException {
    public static final String NO_AGREEMENT_MSG = "No such agreement";

    public NoSuchAgreementMDALException() {
    }

    public NoSuchAgreementMDALException(String message) {
        super(message);
    }

}
