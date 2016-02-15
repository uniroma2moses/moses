/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication13;

import java.io.IOException;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.UserAlreadyExistsMDALException;

/**
 *
 * @author valeryo
 */
public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, BadArgumentsMDALException, UserAlreadyExistsMDALException, MDALException {

        ESECProcess ep = new ESECProcess();
        try {
            ep.insertData();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
