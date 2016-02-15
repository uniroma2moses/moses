/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moses.mdal;

import org.moses.entity.User;
import org.moses.exception.BadArgumentsMDALException;
import org.moses.exception.MDALException;
import org.moses.exception.NoSuchUserMDALException;
import org.moses.exception.UserAlreadyExistsMDALException;

/**
 *
 * @author stefano
 */
public interface UsersMDALInterface {

    /**
     * Adds a user in the repository
     * @param u
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.UserAlreadyExistsMDALException
     * @throws org.moses.exception.MDALException
     */
    public int createUser(User user) throws BadArgumentsMDALException, UserAlreadyExistsMDALException, MDALException;

    /**
     * Given a username returns the corresponding User Object
     * @param username
     * @return
     * @throws org.moses.exception.NoSuchUserMDALException
     * @throws org.moses.exception.MDALException
     */
    public User readUser(String username) throws NoSuchUserMDALException, MDALException;

    /**
     * Given a user, updates informations about that user
     * @param u
     * @throws org.moses.exception.NoSuchUserMDALException
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.MDALException
     */
    public int updateUser(User user) throws  BadArgumentsMDALException, MDALException;

    /**
     * Deletes a user and all associated informations
     * @param username
     * @throws org.moses.exception.NoSuchUserMDALException
     * @throws org.moses.exception.MDALException
     */
    public int deleteUser(String username) throws BadArgumentsMDALException, MDALException;

    /**
     * Adds the registration for a class of a process in a user subtree
     * @param username
     * @param processName
     * @param processClass
     * @throws org.apache.zookeeper.KeeperException.BadArgumentsException
     * @throws org.apache.zookeeper.KeeperException
     * @throws java.lang.InterruptedException
     */
    /*public void addProcess(String username, String processName, String processClass) throws NoSuchUserMDALException,
            BadArgumentsMDALException, MDALException;
     */
    /**
     * Gets all the processes that username subscribed to
     * @param username
     * @return
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.MDALException
     */
    //public List<String> getProcesses(String username) throws BadArgumentsMDALException, MDALException;

    /**
     * Get all the classes of a process that username subscribed to
     * @param username
     * @param process
     * @return
     * @throws org.moses.exception.BadArgumentsMDALException
     * @throws org.moses.exception.MDALException
     */
    //public List<String> getClasses(String username, String process) throws BadArgumentsMDALException, MDALException;

    /**
     * Close the connection with the repository
     * @throws java.lang.MDALException
     */
    public void close() throws MDALException;
}
