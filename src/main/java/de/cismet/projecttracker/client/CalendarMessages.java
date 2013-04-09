/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client;

import com.google.gwt.i18n.client.Messages;

/**
 * This interface contains all messages, which are required by the popup calendar.<br />
 * This interaface is used for <a
 * href="http://code.google.com/webtoolkit/doc/1.6/DevGuideI18nAndA11y.html#DevGuideStaticStringInternationalization">
 * static string internationalization</a>
 *
 * @author   therter
 * @version  $Revision$, $Date$
 * @see      de.cismet.projecttracker.client.common.ui.PopupCalendar
 */
public interface CalendarMessages extends Messages {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String mondayAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String tuesdayAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String wednesdayAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String thursdayAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String fridayAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String saturdayAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String sundayAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String mondayAbbreviationLong();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String tuesdayAbbreviationLong();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String wednesdayAbbreviationLong();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String thursdayAbbreviationLong();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String fridayAbbreviationLong();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String saturdayAbbreviationLong();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String sundayAbbreviationLong();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String januaryAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String februaryAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String marchAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String aprilAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String mayAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String juneAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String julyAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String augustAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String septemberAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String octoberAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String novemberAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String decemberAbbreviation();
    /**
     * DOCUMENT ME!
     *
     * @param   year   DOCUMENT ME!
     * @param   month  DOCUMENT ME!
     * @param   day    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String dateFormat(int year, int month, int day);
    /**
     * DOCUMENT ME!
     *
     * @param   hour    DOCUMENT ME!
     * @param   minute  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String timeFormat(int hour, int minute);
    /**
     * DOCUMENT ME!
     *
     * @param   year     DOCUMENT ME!
     * @param   month    DOCUMENT ME!
     * @param   day      DOCUMENT ME!
     * @param   hours    DOCUMENT ME!
     * @param   minutes  DOCUMENT ME!
     * @param   seconds  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String dateTimeFormat(int year, int month, int day, int hours, int minutes, int seconds);
}
