/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal;

import java.util.List;
import org.moses.entity.AbstractOperation;
import org.moses.entity.AbstractService;
import org.moses.exception.AbstractOperationAlreadyExistsMDALException;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractOperationMDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;

/**
 *
 * @author dante
 */
public interface AbstractOperationsMDALInterface {

    public void createAbstractOperation(AbstractOperation abstractOp) throws AbstractOperationAlreadyExistsMDALException, BadArgumentsMDALException, MDALException;

    public AbstractOperation readAbstractOperation(String serviceName, String operationName) throws NoSuchAbstractOperationMDALException, NoSuchAbstractServiceMDALException, BadArgumentsMDALException, MDALException;

    public List<AbstractOperation> readAbstractOperationsByAbSer(AbstractService aserv) throws NoSuchAbstractOperationMDALException, NoSuchAbstractServiceMDALException, BadArgumentsMDALException, MDALException;
    
    public int updateAbstractOperation(AbstractOperation abstractOp) throws BadArgumentsMDALException, MDALException;

    public int deleteAbstractOperation(String serviceName, String operationName) throws BadArgumentsMDALException, MDALException;

    public void close() throws MDALException;

}
