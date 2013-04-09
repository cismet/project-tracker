/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.utilities;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.Serializable;

import java.sql.Connection;

import java.util.List;

import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.PersistentLayerException;

import de.cismet.projecttracker.report.db.entities.BasicHibernateEntity;
import de.cismet.projecttracker.report.query.DBManager;

/**
 * This class wraps the class DBManager and replaces its exceptions with exceptions, which can be handled by the
 * client-side code.
 *
 * <p>This object is not thread-save.</p>
 *
 * <p>TODO: thread-safe machen</p>
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DBManagerWrapper {

    //~ Instance fields --------------------------------------------------------

    DBManager manager = null;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void checkSession() {
        if ((manager == null) || !manager.isSessionOpen()) {
            manager = new DBManager();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Connection getDatabaseConnection() {
        checkSession();
        return manager.getDatabaseConnection();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cl  the class of the object that should be returned
     * @param   id  the id of the object that should be returned
     *
     * @return  the database object of the given class with the given id
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    public Object getObject(final Class cl, final long id) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObject(cl, id);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   hqlQuery  the hql query that should be executed on the database
     *
     * @return  the results of the given query
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    public Object getObject(final String hqlQuery) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObject(hqlQuery);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cl  the class of the object that should be returned
     *
     * @return  the database object of the given class with the given id
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    public List getAllObjects(final Class cl) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getAllObjects(cl);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }

    /**
     * reads a object from the database.
     *
     * @param   cl         the class of the object that should be read
     * @param   attribute  the attribute, that should be select the object to read. This attribute should be unique
     *                     within the database.
     * @param   value      the object of the given class that has this value in the given attribute will be returned. If
     *                     this value matches more than one object, an exception will be thrown.
     *
     * @return  the database object object
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    public Object getObjectByAttribute(final Class cl, final String attribute, final Object value)
            throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObjectByAttribute(cl, attribute, value);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }

    /**
     * reads all objects from the database, which fulfils the given restrictions.
     *
     * @param   cl         the class of the object that should be read
     * @param   attribute  the attribute, that should be select the object to read. This attribute should be unique
     *                     within the database.
     * @param   value      the object of the given class that has this value in the given attribute will be returned. If
     *                     this value matches more than one object, an exception will be thrown.
     *
     * @return  a list with all objects, which fulfils the given restrictions
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    public List getObjectsByAttribute(final Class cl, final String attribute, final Object value)
            throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObjectsByAttribute(cl, attribute, value);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }

    /**
     * reads any object of given class from the database.
     *
     * @param   cl  the class of the object that should be read
     *
     * @return  the database object
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    public Object getAnyObjectByClass(final Class cl) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getAnyObjectByClass(cl);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   hqlQuery  the hql query that should be executed on the database
     *
     * @return  a list, that contains the results of the given query
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    public List getObjectsByAttribute(final String hqlQuery) throws DataRetrievalException {
        try {
            checkSession();
            return manager.getObjectsByAttribute(hqlQuery);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            throw new DataRetrievalException(e);
        }
    }

    /**
     * Writes the given object to the database. The object should not contained in the database before.
     *
     * @param   object  the object that should be written to the database
     *
     * @return  the id of the given object within the database
     *
     * @throws  PersistentLayerException  DOCUMENT ME!
     */
    public Serializable createObject(final BasicHibernateEntity object) throws PersistentLayerException {
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
     * @param   object  the object that should be written to the database
     *
     * @throws  PersistentLayerException  DOCUMENT ME!
     */
    public void saveObject(final BasicHibernateEntity object) throws PersistentLayerException {
        try {
            checkSession();
            manager.saveObject(object);
        } catch (HibernateException e) {
            throw new PersistentLayerException(e);
        }
    }

    /**
     * Deletes the given object from the db.
     *
     * @param   object  the object that should be deleted from the database
     *
     * @throws  PersistentLayerException  DOCUMENT ME!
     */
    public void deleteObject(final BasicHibernateEntity object) throws PersistentLayerException {
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
        if ((manager != null) && manager.isSessionOpen()) {
            manager.closeSession();
        }
        manager = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSessionOpen() {
        if (manager != null) {
            return manager.isSessionOpen();
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  a hibernate session
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
