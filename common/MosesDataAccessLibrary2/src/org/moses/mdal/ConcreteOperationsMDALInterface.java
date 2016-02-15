/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal;
import java.util.List;
import org.moses.entity.AbstractOperation;
import org.moses.entity.ConcreteOperation;
import org.moses.entity.ConcreteService;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.ConcreteOperationAlreadyExistsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchAbstractOperationMDALException;
import org.moses.exception.NoSuchAbstractServiceMDALException;
import org.moses.exception.NoSuchConcreteServiceMDALException;
/**
 *
 * @author dante
 */
public interface ConcreteOperationsMDALInterface {

    public void createConcreteOperation(ConcreteOperation operation) throws BadArgumentsMDALException, ConcreteOperationAlreadyExistsMDALException, MDALException;

    public ConcreteOperation readConcreteOperation(int id, String name) throws NoSuchAbstractServiceMDALException, MDALException;

    public List<ConcreteOperation> readConcreteOperationsByAbOp(AbstractOperation aop) throws BadArgumentsMDALException, NoSuchAbstractOperationMDALException, NoSuchConcreteServiceMDALException, MDALException;
 
    public List<ConcreteOperation> readConcreteOperationsByConServ(ConcreteService cserv) throws NoSuchConcreteServiceMDALException, NoSuchAbstractOperationMDALException, MDALException;

    public int updateConcreteOperation(ConcreteOperation operation) throws BadArgumentsMDALException, MDALException;

    public int updateLoadProbabilty(ConcreteOperation operation) throws BadArgumentsMDALException, MDALException;

    public int deleteConcreteOperation(int id, String name) throws MDALException;

    public void close() throws MDALException;
    
}
