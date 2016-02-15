/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.entity;

import java.util.List;
import org.moses.mdal.AgreementsMDALInterface;
import org.moses.mdal.MDAL;

/**
 *
 * @author francesco
 */
public class User {

    private String username;
    private String password;
    private String name;
    private String surname;
    private List<Agreement> agreements;

    public User() {
        this.username = null;
        this.password = null;
        this.name = null;
        this.surname = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<Agreement> getAgreements() {
        if (agreements == null) {        //Lazy Loading. If agreements is null maybe the user doesn't has associated agreements
            try {
                MDAL mdal = MDAL.getInstance();
                AgreementsMDALInterface ami = mdal.getAgreementsMDAL();
                agreements = ami.readAgreementsByUser(this);
                ami.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return agreements;
    }

    public void setAgreements(List<Agreement> agreements) {
        this.agreements = agreements;
    }
}
