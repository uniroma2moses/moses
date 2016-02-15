/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.moses.mdal;

import java.util.List;
import org.moses.entity.Agreement;
import org.moses.entity.SLA;
import org.moses.entity.User;
import org.moses.exception.AgreementAlreadyExistsMDALException;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchSLAMDALException;
import org.moses.exception.NoSuchUserMDALException;

/**
 *
 * @author dante
 */
public interface AgreementsMDALInterface {

    public void createAgreement(Agreement agreement) throws AgreementAlreadyExistsMDALException, BadArgumentsMDALException, MDALException;

    public Agreement readAgreement(String userName, String processName, String processClass) throws NoSuchUserMDALException, NoSuchSLAMDALException, BadArgumentsMDALException, MDALException;

    public List<Agreement> readAgreementsByUser(User user) throws NoSuchUserMDALException, BadArgumentsMDALException, MDALException;

    public List<Agreement> readAgreementsBySla(SLA sla) throws NoSuchSLAMDALException, BadArgumentsMDALException, MDALException;

    public int updateAgreement(Agreement agreement) throws BadArgumentsMDALException, MDALException;

    public int deleteAgreement(String userName, String processName, String processClass) throws MDALException;

    public void close() throws MDALException;

}
