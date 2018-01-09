/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.utilities;

import org.apache.log4j.Logger;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.cismet.projecttracker.client.dto.BasicDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;

import de.cismet.projecttracker.report.db.entities.BasicHibernateEntity;

/**
 * This class converts hibernate entities to DTOs with its clone methods and DTOs to hibernate entities with its merge
 * methods.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DTOManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(DTOManager.class);
    private static final String HIBERNATE_PACKAGE = "de.cismet.projecttracker.report.db.entities";
    private static final String DTO_PACKAGE = "de.cismet.projecttracker.client.dto";
    private static final int INITIAL_CAPACITY = 256;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   original  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     */
    public Object clone(final BasicHibernateEntity original) throws InvalidInputValuesException {
        Object result = null;
        final long startTime = System.currentTimeMillis();
        final HashMap<Object, Object> convertedClasses = new HashMap<Object, Object>(INITIAL_CAPACITY);

        result = cloneInternal(original, convertedClasses);
        if (logger.isDebugEnabled()) {
            logger.debug("time to clone: " + (System.currentTimeMillis() - startTime) + "ms objects: "
                        + convertedClasses.size());
        }
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   original  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     */
    public ArrayList clone(final List original) throws InvalidInputValuesException {
        final long startTime = System.currentTimeMillis();
        final ArrayList<BasicDTO> clonedList = new ArrayList<BasicDTO>(original.size());
        final HashMap<Object, Object> convertedClasses = new HashMap<Object, Object>(INITIAL_CAPACITY);

        for (final Object o : original) {
            if (o instanceof BasicHibernateEntity) {
                clonedList.add((BasicDTO)cloneInternal((BasicHibernateEntity)o, convertedClasses));
            } else {
                logger.error("Only objects of the type " + BasicHibernateEntity.class.getName()
                            + " can be cloned. The given object is of the type " + o.getClass().getName());
                throw new InvalidInputValuesException("internal error");
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("time to clone list: " + (System.currentTimeMillis() - startTime) + "ms objects: "
                        + convertedClasses.size());
        }
        return clonedList;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   original  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     */
    public BasicHibernateEntity merge(final BasicDTO original) throws InvalidInputValuesException {
        BasicHibernateEntity result = null;
        final long startTime = System.currentTimeMillis();
        final HashMap<Object, Object> convertedClasses = new HashMap<Object, Object>(INITIAL_CAPACITY);

        result = mergeInternal(original, convertedClasses);
        if (logger.isDebugEnabled()) {
            logger.debug("time to merge: " + (System.currentTimeMillis() - startTime) + "ms objects: "
                        + convertedClasses.size());
        }
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   original          DOCUMENT ME!
     * @param   convertedClasses  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     */
    private BasicHibernateEntity mergeInternal(final BasicDTO original, final HashMap<Object, Object> convertedClasses)
            throws InvalidInputValuesException {
        try {
            BasicHibernateEntity result = (BasicHibernateEntity)convertedClasses.get(original);

            if ((result == null) && (original != null)) {
                String dtoName = original.getClass().getName();
                dtoName = HIBERNATE_PACKAGE + dtoName.substring(DTO_PACKAGE.length(), dtoName.length() - 3);

                if (dtoName.endsWith("Short")) {
                    dtoName = dtoName.replace("Short", "");
                }

                result = (BasicHibernateEntity)Class.forName(dtoName).newInstance();
                result.setId(original.getId());
                convertedClasses.put(original, result);

                final Method[] methods = result.getClass().getMethods();

                for (final Method tmpMethod : methods) {
                    final String name = tmpMethod.getName();
                    if (name.startsWith("set")) {
                        try {
                            final Method originalGetter = original.getClass().getMethod("g" + name.substring(1));
                            final Class[] parameter = tmpMethod.getParameterTypes();

                            if (!tmpMethod.isSynthetic() && (parameter.length == 1)
                                        && (originalGetter.getParameterTypes().length == 0)) {
                                final Class declaredClass = parameter[0];
                                if (BasicHibernateEntity.class.isAssignableFrom(declaredClass)
                                            && BasicDTO.class.isAssignableFrom(originalGetter.getReturnType())) {
                                    tmpMethod.invoke(
                                        result,
                                        mergeInternal((BasicDTO)originalGetter.invoke(original), convertedClasses));
                                } else if (Set.class.isAssignableFrom(declaredClass)
                                            && ArrayList.class.isAssignableFrom(originalGetter.getReturnType())) {
                                    final Set newSet = new HashSet();
                                    final ArrayList origList = (ArrayList)originalGetter.invoke(original);

                                    for (final Object tmpObj : origList) {
                                        newSet.add(mergeInternal((BasicDTO)tmpObj, convertedClasses));
                                    }

                                    tmpMethod.invoke(result, newSet);
                                } else {
                                    tmpMethod.invoke(result, originalGetter.invoke(original));
                                }
                            }
                        } catch (Exception e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("method not found " + original.getClass().getName() + "." + name);
                                // nothing to do. Field does not exist, so it should not be copied
                            }
                        }
                    }
                }
            }

            return (BasicHibernateEntity)result;
        } catch (ClassNotFoundException e) {
            logger.error(e);
            throw new InvalidInputValuesException(e);
        } catch (InstantiationException e) {
            logger.error(e);
            throw new InvalidInputValuesException(e);
        } catch (IllegalAccessException e) {
            logger.error(e);
            throw new InvalidInputValuesException(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   original          DOCUMENT ME!
     * @param   convertedClasses  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     */
    private BasicDTO cloneInternal(final BasicHibernateEntity original, final HashMap<Object, Object> convertedClasses)
            throws InvalidInputValuesException {
        try {
            BasicDTO result = (BasicDTO)convertedClasses.get(original);

            if ((result == null) && (original != null)) {
                String dtoName = original.getClass().getName();
                if (dtoName.indexOf("_$$_") != -1) {
                    dtoName = dtoName.substring(0, dtoName.indexOf("_$$_"));
                }
                dtoName = DTO_PACKAGE + dtoName.substring(HIBERNATE_PACKAGE.length()) + "DTO";

                result = (BasicDTO)Class.forName(dtoName).newInstance();
                result.setId(original.getId());
                convertedClasses.put(original, result);

                final Method[] methods = result.getClass().getMethods();

                for (final Method tmpMethod : methods) {
                    final String name = tmpMethod.getName();
                    if (name.startsWith("set")) {
                        try {
                            final Method originalGetter = original.getClass().getMethod("g" + name.substring(1));
                            final Class[] parameter = tmpMethod.getParameterTypes();

                            if (!tmpMethod.isSynthetic() && (parameter.length == 1)
                                        && (originalGetter.getParameterTypes().length == 0)) {
                                final Class declaredClass = parameter[0];
                                if (BasicDTO.class.isAssignableFrom(declaredClass)
                                            && BasicHibernateEntity.class.isAssignableFrom(
                                                originalGetter.getReturnType())) {
                                    if (declaredClass.getName().endsWith("Short")) {
                                        final Object para = cloneInternal((BasicHibernateEntity)originalGetter.invoke(
                                                    original),
                                                convertedClasses);
                                        tmpMethod.invoke(
                                            result,
                                            para.getClass().getMethod("toShortVersion").invoke(para));
                                    } else {
                                        tmpMethod.invoke(
                                            result,
                                            cloneInternal(
                                                (BasicHibernateEntity)originalGetter.invoke(original),
                                                convertedClasses));
                                    }
                                } else if (ArrayList.class.isAssignableFrom(declaredClass)
                                            && Set.class.isAssignableFrom(originalGetter.getReturnType())) {
                                    final ArrayList newSet = new ArrayList();
                                    final Set origSet = (Set)originalGetter.invoke(original);

                                    for (final Object tmpObj : origSet) {
                                        newSet.add(cloneInternal((BasicHibernateEntity)tmpObj, convertedClasses));
                                    }

                                    tmpMethod.invoke(result, newSet);
                                } else {
                                    tmpMethod.invoke(result, originalGetter.invoke(original));
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("method not found " + result.getClass().getName() + "." + name);
                            e.printStackTrace();
                            // nothing to do. Field does not exist, so it should not be copied
                        }
                    }
                }
            }

            return result;
        } catch (ClassNotFoundException e) {
            logger.error(e);
            throw new InvalidInputValuesException(e);
        } catch (InstantiationException e) {
            logger.error(e);
            throw new InvalidInputValuesException(e);
        } catch (IllegalAccessException e) {
            logger.error(e);
            throw new InvalidInputValuesException(e);
        }
    }
}
