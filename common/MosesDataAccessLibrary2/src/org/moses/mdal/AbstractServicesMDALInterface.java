/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal;

import java.util.Hashtable;
import java.util.List;
import org.moses.entity.Process;
import org.moses.entity.AbstractService;
import org.moses.exception.AbstractOperationAlreadyExistsMDALException;
import org.moses.exception.AbstractServiceAlreadyExistsMDALException;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;
import org.moses.exception.NoSuchProcessMDALException;

/**
 *
 * @author dante
 */
public interface AbstractServicesMDALInterface {

    public void createAbstractService(AbstractService abstractSer) throws AbstractOperationAlreadyExistsMDALException, AbstractServiceAlreadyExistsMDALException, BadArgumentsMDALException, MDALException;

    public AbstractService readAbstractService(String serviceName) throws NoSuchAbstractServiceMDALException, BadArgumentsMDALException, MDALException;

    public Hashtable<AbstractService, Float> readAbstractServicesByPr(Process process) throws NoSuchProcessMDALException, BadArgumentsMDALException, MDALException;

    public int updateAbstractService(AbstractService abstractSer) throws BadArgumentsMDALException, MDALException;

    public int deleteAbstractService(String serviceName) throws BadArgumentsMDALException, MDALException;

    public void close() throws MDALException;

}
