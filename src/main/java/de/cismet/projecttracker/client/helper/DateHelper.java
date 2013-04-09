/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.helper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import de.cismet.projecttracker.client.CalendarMessages;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.WorkPackagePeriodDTO;

/**
 * This class contains some static methods to use Date objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DateHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final CalendarMessages MESSAGES = (CalendarMessages)GWT.create(CalendarMessages.class);
    public static final String[] DAYS_OF_WEEK = {
            MESSAGES.mondayAbbreviation(),
            MESSAGES.tuesdayAbbreviation(),
            MESSAGES.wednesdayAbbreviation(),
            MESSAGES.thursdayAbbreviation(),
            MESSAGES.fridayAbbreviation(),
            MESSAGES.saturdayAbbreviation(),
            MESSAGES.sundayAbbreviation()
        };
    public static final String[] DAYS_OF_WEEK_LONG = {
            MESSAGES.mondayAbbreviationLong(),
            MESSAGES.tuesdayAbbreviationLong(),
            MESSAGES.wednesdayAbbreviationLong(),
            MESSAGES.thursdayAbbreviationLong(),
            MESSAGES.fridayAbbreviationLong(),
            MESSAGES.saturdayAbbreviationLong(),
            MESSAGES.sundayAbbreviationLong()
        };
    public static final String[] NAME_OF_MONTH = {
            MESSAGES.januaryAbbreviation(),
            MESSAGES.februaryAbbreviation(),
            MESSAGES.marchAbbreviation(),
            MESSAGES.aprilAbbreviation(),
            MESSAGES.mayAbbreviation(),
            MESSAGES.juneAbbreviation(),
            MESSAGES.julyAbbreviation(),
            MESSAGES.augustAbbreviation(),
            MESSAGES.septemberAbbreviation(),
            MESSAGES.octoberAbbreviation(),
            MESSAGES.novemberAbbreviation(),
            MESSAGES.decemberAbbreviation()
        };
    public static final long DAY_IN_MILLIS = 86400000L;
    public static final long HOUR_IN_MILLIS = 3600000L;
    public static final long MINUTE_IN_MILLIS = 60000L;
    public static final int DAYS_PER_WEEK = 7;
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   date  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String formatShortDate(final Date date) {
        if (date == null) {
            return "";
        }

        return "" + date.getDate() + "." + (date.getMonth() + 1);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date  DOCUMENT ME!
     *
     * @return  a string representation of the given date
     */
    public static String formatDate(final Date date) {
        if (date == null) {
            return "";
        }

        return MESSAGES.dateFormat((date.getYear() + 1900), (date.getMonth() + 1), date.getDate());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day   DOCUMENT ME!
     * @param  days  DOCUMENT ME!
     */
    public static void addDays(final Date day, final int days) {
        CalendarUtil.addDaysToDate(day, days);
//        day.setTime(day.getTime() + (days * DAY_IN_MILLIS));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ihours  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String doubleToHours(final double ihours) {
        double hours = Math.abs(ihours);
        int minutes = (int)Math.round((hours - ((int)hours)) * 60);

        // vermeidet Angaben wie 7:60
        if (minutes == 60) {
            ++hours;
            minutes = 0;
        }

        return ((ihours < 0.0) ? "-" : "") + (int)hours + ":" + IntToDoubleDigit(minutes);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   hours  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NumberFormatException  DOCUMENT ME!
     */
    public static double hoursToDouble(final String hours) throws NumberFormatException {
        final String[] st = hours.split(":");

        if (st.length == 2) {
            final String hour = st[0];
            final String minute = st[1];

            final int hourInt = Integer.parseInt(hour);
            final int minuteInt = Integer.parseInt(minute);

            return hourInt + (minuteInt / 60.0);
        } else if (st.length == 1) {
            try {
                return Integer.parseInt(st[0]);
            } catch (final NumberFormatException e) {
                throw new NumberFormatException(hours + " is no valid duration");
            }
        } else {
            throw new NumberFormatException(hours + " is no valid duration");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date  DOCUMENT ME!
     *
     * @return  a string representation of the given time
     */
    public static String formatDateTime(final Date date) {
        if (date == null) {
            return "";
        }

        return MESSAGES.dateTimeFormat((date.getYear() + 1900),
                (date.getMonth() + 1),
                date.getDate(),
                date.getHours(),
                date.getMinutes(),
                date.getSeconds());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date  DOCUMENT ME!
     *
     * @return  a string representation of the given time
     */
    public static String formatTime(final Date date) {
        if (date == null) {
            return "";
        }

        return MESSAGES.timeFormat(date.getHours(), date.getMinutes());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   day   DOCUMENT ME!
     * @param   time  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Date createDateObject(final Date day, final Date time) {
//        long dayInMillis = day.getTime();
//        dayInMillis = dayInMillis - (dayInMillis % DAY_IN_MILLIS);
//        long timeInMillis = time.getTime() % dayInMillis;
//
//        return new Date(dayInMillis + timeInMillis);
        return new Date(day.getYear(),
                day.getMonth(),
                day.getDate(),
                time.getHours(),
                time.getMinutes(),
                time.getSeconds());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date       DOCUMENT ME!
     * @param   otherDate  DOCUMENT ME!
     *
     * @return  true, if and only if the two given date objects represents the same day
     */
    public static boolean isSameDay(final Date date, final Date otherDate) {
        if ((date == null) && (otherDate == null)) {
            return true;
        } else if ((date == null) || (otherDate == null)) {
            return false;
        }

        return ((date.getYear() == otherDate.getYear())
                        && (date.getMonth() == otherDate.getMonth())
                        && (date.getDate() == otherDate.getDate()));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date       DOCUMENT ME!
     * @param   otherDate  DOCUMENT ME!
     *
     * @return  true, if and only if the two given date objects represents the same day. A logical ProjectTracker day
     *          starts at 4 am.
     */
    public static boolean isSameDaySV(final Date date, final Date otherDate) {
        if ((date == null) && (otherDate == null)) {
            return true;
        } else if ((date == null) || (otherDate == null)) {
            return false;
        }
        if ((date.getYear() == otherDate.getYear()) && (date.getMonth() == otherDate.getMonth())) {
            final int dateDay = date.getDay();
            final int otherDateDay = otherDate.getDay();
            final int diff = dateDay - otherDateDay;
            if (diff == 0) {
                if (((date.getHours() < 4) && (otherDate.getHours() > 4))
                            || ((date.getHours() > 4) && (otherDate.getHours() < 4))) {
                    return false;
                }
                return true;
            } else if (diff == 1) {
                // date is one day after otherDay, check if the time
                if ((date.getHours() < 4) && (otherDate.getHours() > 4)) {
                    return true;
                }
                return false;
            } else if (diff == -1) {
                if ((otherDate.getHours() < 4) && (date.getHours() > 4)) {
                    return true;
                }
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * compares two calendar objects.
     *
     * @param   date1  DOCUMENT ME!
     * @param   date2  DOCUMENT ME!
     *
     * @return  true, if and only if date1 is less or equal to date2. The result only depends on the dates, which are
     *          contained in the given GregorianCalendar objects. The times will be ignored.
     */
    public static boolean isDateLessOrEqual(final Date date1, final Date date2) {
        final int firstDate = ((date1.getYear() + 1900) * 10000) + (date1.getMonth() * 100) + date1.getDate();
        final int secondDate = ((date2.getYear() + 1900) * 10000) + (date2.getMonth() * 100) + date2.getDate();

        return (firstDate <= secondDate);
    }

    /**
     * compares two calendar objects.
     *
     * @param   date1  DOCUMENT ME!
     * @param   date2  DOCUMENT ME!
     *
     * @return  true, if and only if date1 is less or equal to date2. The result only depends on the dates, which are
     *          contained in the given GregorianCalendar objects. The times will be ignored.
     */
    public static boolean isDateGreaterOrEqual(final Date date1, final Date date2) {
        final int firstDate = ((date1.getYear() + 1900) * 10000) + (date1.getMonth() * 100) + date1.getDate();
        final int secondDate = ((date2.getYear() + 1900) * 10000) + (date2.getMonth() * 100) + date2.getDate();

        return (firstDate >= secondDate);
    }

    /**
     * converts the given String object to a Date object.
     *
     * @param   dateString  a date string with the format year-month-day (2009-10-19)
     * @param   dtf         DOCUMENT ME!
     *
     * @return  a Date object
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static Date parseString(final String dateString, DateTimeFormat dtf) throws IllegalArgumentException {
        if (dtf == null) {
            dtf = DateTimeFormat.getFormat("y-M-d");
        }
        return dtf.parseStrict(dateString);
    }

    /**
     * converts a number to a string that contains a double digit.
     *
     * @param   number  a positive integer with at most two digits
     *
     * @return  a string that contains a double digit
     */
    private static String IntToDoubleDigit(final int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the number of the current week
     */
    public static int getCurrentWeek() {
        return getWeekOfYear(new Date());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static int getYear(final Date d) {
//        final GregorianCalendar cal = new GregorianCalendar();
//        cal.setTime(d);
//        return cal.get(GregorianCalendar.YEAR);
        return d.getYear() + 1900;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date  DOCUMENT ME!
     *
     * @return  the number of the week of the given date
     */
    public static int getWeekOfYear(final Date date) {
        final Date givenDate = new Date(date.getTime());
        final Date firstWeek = new Date(givenDate.getTime()); // this operation can change the timezone, so the
                                                              // timezoneOffset must be considered in the following
                                                              // calculations
        firstWeek.setMonth(0);
        firstWeek.setDate(4);
        // givenDate should contain the same day of week as the firstWeek but
        // should be in the same week of year as it was before
        final int dayOfWeekJanTheFourth = DateHelper.convertDayOfWeek(firstWeek.getDay());
        final int dayOfWeekNow = DateHelper.convertDayOfWeek(givenDate.getDay());
        final int diff = dayOfWeekJanTheFourth - dayOfWeekNow;
        givenDate.setTime(givenDate.getTime() + (diff * DAY_IN_MILLIS));

        final long givenDateNormalized = givenDate.getTime() - (givenDate.getTimezoneOffset() * 60000);
        final long firstWeekNormalized = firstWeek.getTime() - (firstWeek.getTimezoneOffset() * 60000);
        final long timeDistance = (givenDateNormalized - firstWeekNormalized);
        long week = (timeDistance / (DAY_IN_MILLIS * DAYS_PER_WEEK)) + 1;

        if (week == 0) {
            week = getWeekCountForYear(givenDate.getYear() + 1899);
        } else if ((week == 53) && (getWeekCountForYear(date.getYear() + 1900) == 52)) {
            week = 1;
        } else if (week > 53) {
            week = 1;
        }

        return (int)week;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getDayAbbreviation(final Date d) {
        return (d.getDay() == 0) ? DAYS_OF_WEEK_LONG[6] : DAYS_OF_WEEK_LONG[d.getDay() - 1];
    }

    /**
     * DOCUMENT ME!
     *
     * @param   start  DOCUMENT ME!
     * @param   end    DOCUMENT ME!
     *
     * @return  the count of months, which are contained within the given time interval
     */
    public static int getMonthsOfPeriod(final Date start, final Date end) {
        final int year = end.getYear() - start.getYear();
        final int months = end.getMonth() - start.getMonth();
        int day = 1;

        if (end.getDate() < start.getDate()) {
            day = 0;
        }

        return months + (year * 12) + day;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   year  DOCUMENT ME!
     *
     * @return  the count of weeks in the given year according to ISO 8601
     */
    public static int getWeekCountForYear(final int year) {
        int weekCount = 52;
        Date date = new Date(year - 1900, 0, 1);

        // according to ISO 8601, a year has 53 weeks, if and only if the first day or the last day of the year is a
        // thursday
        if (date.getDay() == DateHelper.THURSDAY) {
            weekCount = 53;
        } else {
            date = new Date(year - 1900, 11, 31);
            if (date.getDay() == DateHelper.THURSDAY) {
                weekCount = 53;
            }
        }

        return weekCount;
    }

//    public static long getTime(Date date) {
////        if (date instanceof java.sql.Timestamp || date instanceof java.sql.Date) {
////            return date.getTime() - (date.getTimezoneOffset() * MINUTE_IN_MILLIS);
////        } else {
//            return date.getTime();
////        }
//    }
    /**
     * DOCUMENT ME!
     *
     * @param   year  DOCUMENT ME!
     * @param   week  DOCUMENT ME!
     *
     * @return  the first day of the given week in the given year according to ISO 8601
     */
    public static Date getBeginOfWeek(final int year, int week) {
        final Date date = new Date(year - 1900, 0, 1);
        // set to monday
        date.setTime(date.getTime() - (convertDayOfWeek(date.getDay()) * DAY_IN_MILLIS));

        final int currentWeekOfYear = getWeekOfYear(date);
        final int origWeek = week;

        if (currentWeekOfYear == 1) {
            week -= 1;
        }

        date.setTime(date.getTime() + (week * DAY_IN_MILLIS * DAYS_PER_WEEK));

        // TODO: folgendes if entfernen. Das if ist ausschliesslich zu Debugzwecken enthalten
// if (getWeekOfYear(date) != origWeek) {
// ProjectTrackerEntryPoint.outputBox("error in method getBeginOfWeek");
// }

        return date;
    }

    /**
     * this method converts the given day of week with the sunday as 0 to a day of week with the monday as 0.
     *
     * @param   dayOfWeek  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static int convertDayOfWeek(final int dayOfWeek) {
        return mod((dayOfWeek - 1), 7);
    }

    /**
     * calculates a modulo. The result is a positive integer from 0 to <code>range</code> - 1
     *
     * @param   number  DOCUMENT ME!
     * @param   range   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static int mod(final int number, final int range) {
        // the javascript modulo operation can have a negative result
        return ((number % range) + range) % range;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   day     DOCUMENT ME!
     * @param   period  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isDayInWorkPackagePeriod(final Date day, final WorkPackagePeriodDTO period) {
        return (isDateGreaterOrEqual(day, period.getFromdate())
                        && ((period.getTodate() == null) || isDateLessOrEqual(day, period.getTodate())));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   day     DOCUMENT ME!
     * @param   period  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isDayInProjectPeriod(final Date day, final ProjectPeriodDTO period) {
        return (isDateGreaterOrEqual(day, period.getFromdate())
                        && ((period.getTodate() == null) || isDateLessOrEqual(day, period.getTodate())));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   time       DOCUMENT ME!
     * @param   otherTime  DOCUMENT ME!
     *
     * @return  the difference of the two times in hours
     */
    public static double substract(final Date time, final Date otherTime) {
        return (otherTime.getTime() - time.getTime()) / (double)HOUR_IN_MILLIS;
    }
}
