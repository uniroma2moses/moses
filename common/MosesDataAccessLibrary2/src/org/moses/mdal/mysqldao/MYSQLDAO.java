/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal.mysqldao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.moses.exception.MDALException;

/**
 *
 * @author dante
 */
public class MYSQLDAO {

    protected static final int DUPLICATE_ENTRY_FOR_KEY = 1062;
    private String hostname = null;
    private int port = 0;
    private int sessionTimeout = 0;
    private String userName = "";
    private String password = "";
    private String DBName = "";
    private static Context ctx;
    protected Connection db = null;
    private static final String CONFIG_FILE = "mdal.properties";
    private static final String HOST_KEY = "host";
    private static final String HOST_PORT = "port";
    private static final String SESSION_TIMEOUT = "timeout";
    private static final String USERNAME_KEY = "userName";
    private static final String PASSWORD_KEY = "password";
    private static final String DBNAME_KEY = "DBName";
    protected static final String STANDARD_REPLIES = "standardReplies";
    protected static final String SUFFIX_MODULE_REPLIES = "Replies";
    protected Properties properties;
    private static DataSource ds = null;
    protected static final String startTransaction = "BEGIN;";
    protected static final String commitTransaction = "COMMIT;";

    //Trying to get a datasource from the application server connection pool
    static {
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/mosesdb");
        } catch (Exception e) {
            System.out.println("No connection pool. We will fall back to standard connection");
        }
    }

    public MYSQLDAO() throws MDALException {
        FileInputStream fis = null;

        //If we previously succesfully got the datasource from the application server connection pool,
        //we try to establish a connection to the databse through this datasource...
        if (ds != null) {
            try {
                db = ds.getConnection();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        //... otherwise we try to read a config file and we try to establish a database connection
        //using the parameters read from the config file
        else {
            try {
                String endpoint = "";
                fis = new FileInputStream(CONFIG_FILE);
                properties = new Properties();
                properties.load(fis);
                fis.close();
                this.hostname = properties.getProperty(HOST_KEY);
                this.port = Integer.parseInt(properties.getProperty(HOST_PORT));
                this.sessionTimeout = Integer.parseInt(properties.getProperty(SESSION_TIMEOUT));
                this.userName = properties.getProperty(USERNAME_KEY);
                this.password = properties.getProperty(PASSWORD_KEY);
                this.DBName = properties.getProperty(DBNAME_KEY);

                if (!DBName.equals("")) {
                    DriverManager.setLoginTimeout(sessionTimeout);
                    endpoint = "jdbc:mysql://" + hostname + ":" + port + "/" + DBName;
                    if (userName.equals("")) {
                        db = DriverManager.getConnection(endpoint);
                    } else {
                        if (password.equals("")) {
                            db = DriverManager.getConnection(endpoint + "?user=" + userName + "&autoDeserialize=true");
                        } else {
                            db = DriverManager.getConnection(endpoint + "?user=" + userName + "&password=" + password + "&autoDeserialize=true");
                        }
                    }
                }
                System.out.println("Standard connection established");
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() throws MDALException {
        try {
            db.close();
            db = null;
        } catch (java.sql.SQLException ex) {
            throw new MDALException(ex.getMessage());
        }
    }
}
