/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.exception;

/**
 *
 * @author stefano
 */
public class AgreementAlreadyExistsMDALException extends MDALException {

    public static final String AGREEMENT_EXISTS = "Agreement already exists";
    public AgreementAlreadyExistsMDALException(String message) {
        super(message);
    }

    public AgreementAlreadyExistsMDALException() {
    }
    

}
