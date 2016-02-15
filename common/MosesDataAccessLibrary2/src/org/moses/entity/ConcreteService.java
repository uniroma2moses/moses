/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.moses.mdal.ConcreteOperationsMDALInterface;
import org.moses.mdal.ConcreteServicesMDALInterface;
import org.moses.mdal.MDAL;

/**
 *
 * @author francesco
 */
public class ConcreteService {

    private Integer id;
    private String endpointURL;
    private String wsdlURL;
    private Date expireDate;
    private AbstractService service;
    private List<ConcreteOperation> operations;

    public ConcreteService() {
        this.id = null;
        this.endpointURL = null;
        this.wsdlURL = null;
        this.expireDate = null;
        this.service = null;
        this.operations = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setId(String s) {
        this.id = new Integer(s);
    }

    public AbstractService getService() {
        if(service == null) {
           try {
                MDAL mdal = MDAL.getInstance();
                ConcreteServicesMDALInterface cmi = mdal.getConcreteServicesMDAL();
                service = cmi.readAbstractServiceByConServ(this);
                cmi.close();
            } catch(Exception e) {
            }
        }
        return service;
    }

    public void setService(AbstractService service) {
        this.service = service;
    }

    public String getEndpointURL() {
        return endpointURL;
    }

    public void setEndpointURL(String endpointURL) {
        this.endpointURL = endpointURL;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

     public void setExpireDate(String s) throws ParseException {
         SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
         this.expireDate = sdf.parse(s);
    }

    public String getWsdlURL() {
        return wsdlURL;
    }

    public void setWsdlURL(String wsdlURL) {
        this.wsdlURL = wsdlURL;
    }

    public List<ConcreteOperation> getOperations() {
        if(operations == null) {     //Lazy Loading.
            try {
                MDAL mdal = MDAL.getInstance();
                ConcreteOperationsMDALInterface cmi = mdal.getConcreteOperationsMDAL();
                operations = cmi.readConcreteOperationsByConServ(this);
                cmi.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return operations;
    }

    public void setOperations(List<ConcreteOperation> operations) {
        this.operations = operations;
    }
}
