package de.cismet.projecttracker.utilities;

import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.PersistentLayerException;
import de.cismet.projecttracker.report.db.entities.BasicHibernateEntity;
import de.cismet.projecttracker.report.query.DBManager;
import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * This class wraps the class DBManager and replaces its exceptions with exceptions, which can be
 * handled by the client-side code.
 *
 * This object is not thread-save.
 *
 * TODO: thread-safe machen
 * @author therter
 */
public class DBManagerWrapper {
    DBManager manager = null;

    private void checkSession() {
        if (manager == null || !manager.isSessionOpen()) {
            manager = new DBManager();
        }
    }

    
    public Connection getDatabaseConnection() {
        checkSession();
        return manager.getDatabaseConnection();
    }
    
    /**
     * @param cl the class of the object that should be returned
     * @param id the id of the object that should be returned
     * @return the database object of the given class with the given id
     * @throws DataRetrievalException
     */
    public Object getObject(Class cl, long id) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObject(cl, id);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }


    /**
     *
     * @param hqlQuery the hql query that should be executed on the database
     * @return the results of the given query
     * @throws DataRetrievalException
     */
    public Object getObject(String hqlQuery) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObject(hqlQuery);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }


    /**
     * @param cl the class of the object that should be returned
     * @param id the id of the object that should be returned
     * @return the database object of the given class with the given id
     * @throws DataRetrievalException
     */
    public List getAllObjects(Class cl) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getAllObjects(cl);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }

    /**
     * reads a object from the database
     * @param cl the class of the object that should be read
     * @param attribute the attribute, that should be select the object to read. This attribute should be unique within the database.
     * @param value the object of the given class that has this value in the given attribute will be returned.
     *              If this value matches more than one object, an exception will be thrown.
     * @return the database object object
     * @throws DataRetrievalException
     */
    public Object getObjectByAttribute(Class cl, String attribute, Object value) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObjectByAttribute(cl, attribute, value);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }
    

    /**
     * reads all objects from the database, which fulfils the given restrictions
     * @param cl the class of the object that should be read
     * @param attribute the attribute, that should be select the object to read. This attribute should be unique within the database.
     * @param value the object of the given class that has this value in the given attribute will be returned.
     *              If this value matches more than one object, an exception will be thrown.
     * @return a list with all objects, which fulfils the given restrictions
     * @throws DataRetrievalException
     */
    public List getObjectsByAttribute(Class cl, String attribute, Object value) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObjectsByAttribute(cl, attribute, value);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }


    /**
     * reads any object of given class from the database
     * @param cl the class of the object that should be read
     * @return the database object
     * @throws DataRetrievalException
     */
    public Object getAnyObjectByClass(Class cl) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getAnyObjectByClass(cl);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }

    /**
     *
     * @param hqlQuery the hql query that should be executed on the database
     * @return a list, that contains the results of the given query
     * @throws DataRetrievalException
     */
    public List getObjectsByAttribute(String hqlQuery) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObjectsByAttribute(hqlQuery);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }


    /**
     * Writes the given object to the database. The object should not contained in the
     * database before.
     *
     * @param object the object that should be written to the database
     * @return the id of the given object within the database
     * @throws HibernateException
     */
    public Serializable createObject(BasicHibernateEntity object) throws PersistentLayerException {
        try {
            checkSession();
            return manager.createObject(object);
        } catch (HibernateException e) {
            throw new PersistentLayerException(e);
        }
    }

    /**
     * Writes the given object to the database. The object should already exists within the db.
     *
     * @param object the object that should be written to the database
     * @throws HibernateException
     */
    public void saveObject(BasicHibernateEntity object) throws PersistentLayerException {
        try {
            checkSession();
            manager.saveObject(object);
        } catch (HibernateException e) {
            throw new PersistentLayerException(e);
        }
    }

    /**
     * Deletes the given object from the db
     *
     * @param object the object that should be deleted from the database
     * @throws HibernateException
     */
    public void deleteObject(BasicHibernateEntity object) throws PersistentLayerException {
        try {
            checkSession();
            manager.deleteObject(object);
        } catch (HibernateException e) {
            throw new PersistentLayerException(e);
        }
    }

    /**
     * closes the hibernate session. The session was opened during the execution of the constructor
     */
    public void closeSession() {
        if (manager != null && manager.isSessionOpen()) {
            manager.closeSession();
        }
        manager = null;
    }

    public boolean isSessionOpen() {
        if (manager != null) {
            return manager.isSessionOpen();
        }
        
        return false;
    }
    
    /**
     * @return a hibernate session
     */
    public Session getSession() {
        checkSession();
        return manager.getSession();
    }

    @Override
    protected void finalize() throws Throwable {
        closeSession();
    }
}
