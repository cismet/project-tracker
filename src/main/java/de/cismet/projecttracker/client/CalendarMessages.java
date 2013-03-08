package de.cismet.projecttracker.client;

import com.google.gwt.i18n.client.Messages;

/**
 * This interface contains all messages, which are required by the popup calendar.
 * <br />
 * This interaface is used for <a href="http://code.google.com/webtoolkit/doc/1.6/DevGuideI18nAndA11y.html#DevGuideStaticStringInternationalization">
 * static string internationalization</a>
 *
 * @see de.cismet.projecttracker.client.common.ui.PopupCalendar
 * @author therter
 */
public interface CalendarMessages  extends Messages {
    String mondayAbbreviation();
    String tuesdayAbbreviation();
    String wednesdayAbbreviation();
    String thursdayAbbreviation();
    String fridayAbbreviation();
    String saturdayAbbreviation();
    String sundayAbbreviation();
    String mondayAbbreviationLong();
    String tuesdayAbbreviationLong();
    String wednesdayAbbreviationLong();
    String thursdayAbbreviationLong();
    String fridayAbbreviationLong();
    String saturdayAbbreviationLong();
    String sundayAbbreviationLong();
    String januaryAbbreviation();
    String februaryAbbreviation();
    String marchAbbreviation();
    String aprilAbbreviation();
    String mayAbbreviation();
    String juneAbbreviation();
    String julyAbbreviation();
    String augustAbbreviation();
    String septemberAbbreviation();
    String octoberAbbreviation();
    String novemberAbbreviation();
    String decemberAbbreviation();
    String dateFormat(int year, int month, int day);
    String timeFormat(int hour, int minute);
    String dateTimeFormat(int year, int month, int day, int hours, int minutes, int seconds);
}
