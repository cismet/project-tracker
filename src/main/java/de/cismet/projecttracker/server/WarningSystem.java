/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.server;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.FullStopException;
import de.cismet.projecttracker.client.exceptions.PersistentLayerException;

import de.cismet.projecttracker.report.db.entities.Activity;
import de.cismet.projecttracker.report.db.entities.EstimatedComponentCost;
import de.cismet.projecttracker.report.db.entities.EstimatedComponentCostMonth;
import de.cismet.projecttracker.report.db.entities.Warning;
import de.cismet.projecttracker.report.db.entities.WorkPackage;
import de.cismet.projecttracker.report.helper.QueryHelper;

import de.cismet.projecttracker.utilities.DBManagerWrapper;
import de.cismet.projecttracker.utilities.DevProperties;
import de.cismet.projecttracker.utilities.LanguageBundle;
import de.cismet.projecttracker.utilities.Utilities;

/**
 * This class implements the work packages warn system, which informs the work package responsibles about almost
 * ovrebooked and overbooked work packages. The following three warn level exists:<br />
 * warn - notification to the work package responsible<br />
 * critical - notification to the work package responsible<br />
 * full stop - notification to the work package responsible and if a activity tries to book additional hours on the work
 * package concerned, a FullStopException will be thrown. But it is still possible to remove hours from the work package
 * concerned<br />
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WarningSystem {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(WarningSystem.class);
    private static final int WARNING = 1;
    private static final int CRITICAL = 2;
    private static final int FULL_STOP = 3;

    //~ Methods ----------------------------------------------------------------

    /**
     * This method should be invoked, before a new activity is added to the database and if necessary, a warn email will
     * be sent to the work package responsible.
     *
     * @param   activity  the activity
     *
     * @throws  FullStopException  Will be thrown, if the work package of the given activity is already overbooked.
     */
    public synchronized void addActivity(final Activity activity) throws FullStopException {
        if (activity.getWorkPackage() != null) {
            final long wpId = activity.getWorkPackage().getId();
            final DBManagerWrapper dbManager = new DBManagerWrapper();

            try {
                final Double estimatedHours = getEstimatedWpHours(wpId, dbManager);
                final Double hours = getWpHours(wpId, dbManager);

                if ((estimatedHours != null) && (hours != null)) {
                    double warnLevel = activity.getWorkPackage().getWarnlevel();
                    double criticalLevel = activity.getWorkPackage().getCriticallevel();
                    double fullStopLevel = activity.getWorkPackage().getFullstoplevel();

                    if (warnLevel == 0.0) {
                        warnLevel = activity.getWorkPackage().getProject().getWarnlevel();
                    }
                    if (criticalLevel == 0.0) {
                        criticalLevel = activity.getWorkPackage().getProject().getCriticallevel();
                    }
                    if (fullStopLevel == 0.0) {
                        fullStopLevel = activity.getWorkPackage().getProject().getFullstoplevel();
                    }

                    final double percentage = (hours + activity.getWorkinghours()) * 100 / estimatedHours;

                    if ((percentage > fullStopLevel) && (fullStopLevel != 0.0)) {
                        sendWarning(activity.getWorkPackage(), FULL_STOP, dbManager);
                        throw new FullStopException(activity.getWorkPackage().getName());
                    } else if ((percentage > criticalLevel) && (criticalLevel != 0.0)) {
                        sendWarning(activity.getWorkPackage(), CRITICAL, dbManager);
                    } else if ((percentage > warnLevel) && (warnLevel != 0.0)) {
                        sendWarning(activity.getWorkPackage(), WARNING, dbManager);
                    }
                }
            } catch (DataRetrievalException e) {
                logger.error("DB Error. The warning system cannot work properly.", e);
            } catch (PersistentLayerException e) {
                logger.error("DB Error. The warning system cannot work properly.", e);
            } finally {
                dbManager.closeSession();
            }
        }
    }

    /**
     * This method should be invoked, before a activity is saved to the database and if necessary, a warn email will be
     * sent to the work package responsible.
     *
     * @param   activity  the activity
     *
     * @throws  FullStopException  DOCUMENT ME!
     */
    public synchronized void saveActivity(final Activity activity) throws FullStopException {
        if (activity.getWorkPackage() != null) {
            final long wpId = activity.getWorkPackage().getId();
            final DBManagerWrapper dbManager = new DBManagerWrapper();

            try {
                final Activity originalActivity = (Activity)dbManager.getObject(Activity.class, activity.getId());
                final Double estimatedHours = getEstimatedWpHours(wpId, dbManager);
                final Double hours = getWpHours(wpId, dbManager);

                if ((estimatedHours != null) && (hours != null) && (estimatedHours != 0.0)) {
                    // contains the difference between the old state of the activity and the new state
                    double additionalHours = 0.0;
                    final double percentage;
                    double warnLevel = activity.getWorkPackage().getWarnlevel();
                    double criticalLevel = activity.getWorkPackage().getCriticallevel();
                    double fullStopLevel = activity.getWorkPackage().getFullstoplevel();

                    if (warnLevel == 0.0) {
                        warnLevel = activity.getWorkPackage().getProject().getWarnlevel();
                    }
                    if (criticalLevel == 0.0) {
                        criticalLevel = activity.getWorkPackage().getProject().getCriticallevel();
                    }
                    if (fullStopLevel == 0.0) {
                        fullStopLevel = activity.getWorkPackage().getProject().getFullstoplevel();
                    }

                    if (originalActivity.getWorkPackage().getId() != wpId) {
                        additionalHours = activity.getWorkinghours();
                    } else {
                        // originalActivity.getWorkPackage().getId() == wpId ||
                        // !activity.getWorkCategory().getWorkpackagerelated()
                        additionalHours = activity.getWorkinghours() - originalActivity.getWorkinghours();
                    }

                    percentage = (hours) * 100 / estimatedHours;

                    if ((fullStopLevel != 0.0) && (percentage > fullStopLevel)) {
                        sendWarning(activity.getWorkPackage(), FULL_STOP, dbManager);
                        if (additionalHours > 0) {
                            throw new FullStopException(activity.getWorkPackage().getName());
                        }
                    } else if ((criticalLevel != 0.0) && (percentage > criticalLevel)) {
                        sendWarning(activity.getWorkPackage(), CRITICAL, dbManager);
                    } else if ((warnLevel != 0.0) && (percentage > warnLevel)) {
                        sendWarning(activity.getWorkPackage(), WARNING, dbManager);
                    }
                }
            } catch (DataRetrievalException e) {
                logger.error("DB Error. The warning system cannot work properly.", e);
            } catch (PersistentLayerException e) {
                logger.error("DB Error. The warning system cannot work properly.", e);
            } finally {
                dbManager.closeSession();
            }
        }
    }

    /**
     * sends a warning of the given level to the work package responsible, if the warning was not send, yet.
     *
     * @param   wp         the work package, that has the warning caused
     * @param   level      the level of the warning. See the constants {@link #WARNING}, {@link #CRITICAL},
     *                     {@link #FULL_STOP}
     * @param   dbManager  the dbManager to connect the database
     *
     * @throws  DataRetrievalException    DOCUMENT ME!
     * @throws  PersistentLayerException  DOCUMENT ME!
     */
    private synchronized void sendWarning(final WorkPackage wp, final int level, final DBManagerWrapper dbManager)
            throws DataRetrievalException, PersistentLayerException {
        if (!DevProperties.getInstance().isDevMode()) {
            final List<Warning> warns = dbManager.getObjectsByAttribute(Warning.class, "workPackage", wp);
            boolean messageAlreadySent = false;

            for (final Warning warn : warns) {
                if (warn.getLevel() >= level) {
                    messageAlreadySent = true;
                }
            }

            if (!messageAlreadySent) {
                String email = null;
                if (wp.getResponsiblestaff() != null) {
                    email = wp.getResponsiblestaff().getEmail();
                } else if (wp.getProject().getResponsiblestaff() != null) {
                    email = wp.getProject().getResponsiblestaff().getEmail();
                }

                if (email == null) {
                    logger.warn("Cannot send a warn email, because there was no responsible person found.");
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("send warning");
                    }
                    String subject = null;

                    if (level == 1) {
                        subject = String.format(LanguageBundle.WARN_EMAIL_SUBJECT, wp.getName());
                    } else if (level == 2) {
                        subject = String.format(LanguageBundle.CRITICAL_WARN_EMAIL, wp.getName());
                    } else {
                        subject = String.format(LanguageBundle.FULL_STOP_EMAIL, wp.getName());
                    }

                    final Warning warning = new Warning();
                    warning.setWorkPackage(wp);
                    warning.setLevel(level);
                    warning.setTime(new Date());
                    dbManager.createObject(warning);
                    Utilities.sendEmail(email, subject, LanguageBundle.EMAIL_BODY);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wpId       the work package id
     * @param   dbManager  the dbManager to connect the database
     *
     * @return  the hours, which are worked on the given wp
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    private synchronized Double getWpHours(final long wpId, final DBManagerWrapper dbManager)
            throws DataRetrievalException {
        try {
            return (Double)dbManager.getObject("select sum(workinghours) from Activity act where act.workPackage = "
                            + wpId + " AND workCategory.workpackagerelated=true");
        } catch (DataRetrievalException e) {
            logger.error("Cannot calculate the hours of work for the work package with the id " + wpId
                        + ". The warning system cannot work properly.",
                e);
            throw e;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wpId       the work package id
     * @param   dbManager  the dbManager to connect the database
     *
     * @return  the estimated hours for the given wp
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    private synchronized Double getEstimatedWpHours(final long wpId, final DBManagerWrapper dbManager)
            throws DataRetrievalException {
        try {
            final WorkPackage wp = (WorkPackage)dbManager.getObject(WorkPackage.class, wpId);
            final EstimatedComponentCost estimation = QueryHelper.getMostRecentEstimation(wp);
            double hours = 0.0;

            if (estimation != null) {
                for (final EstimatedComponentCostMonth tmp : estimation.getEstimatedWorkPackageCostMonth()) {
                    hours += tmp.getWorkinghours();
                }
            }

            return hours;
        } catch (DataRetrievalException e) {
            logger.error("Cannot find work package with the id " + wpId + ". The warning system cannot work properly.",
                e);
            throw e;
        }
    }
}
