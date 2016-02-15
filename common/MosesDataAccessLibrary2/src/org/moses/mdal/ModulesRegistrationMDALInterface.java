/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal;

import java.util.Vector;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.ModuleAlreadyRegisteredMDALException;
import org.moses.exception.NoModulesRegisteredMDALException;
import org.moses.exception.NoSuchAddressMDALException;
import org.moses.mdal.monitor.Address;

/**
 *
 * @author arale
 */
public interface ModulesRegistrationMDALInterface  {
    
    public void registerModule(String address, String moduleName) throws BadArgumentsMDALException, ModuleAlreadyRegisteredMDALException, MDALException ;

    public int deregisterModule(String address) throws MDALException, BadArgumentsMDALException, NoSuchAddressMDALException;

    public Vector<Address> getAddresses() throws NoModulesRegisteredMDALException, MDALException;

    public Vector<Address> getAddresses(String moduleName) throws BadArgumentsMDALException, NoModulesRegisteredMDALException, MDALException;

    public void close() throws MDALException;
}
