/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.helper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import de.cismet.projecttracker.client.CalendarMessages;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.WorkPackagePeriodDTO;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * This class contains some static methods to use Date objects.
 * @author therter
 */
public class DateHelper {
    private final static CalendarMessages MESSAGES = (CalendarMessages)GWT.create(CalendarMessages.class);
    public final static String[] DAYS_OF_WEEK = {MESSAGES.mondayAbbreviation(), MESSAGES.tuesdayAbbreviation(), MESSAGES.wednesdayAbbreviation(),
                                     MESSAGES.thursdayAbbreviation(), MESSAGES.fridayAbbreviation(), MESSAGES.saturdayAbbreviation(),
                                     MESSAGES.sundayAbbreviation()};
    public final static String[] DAYS_OF_WEEK_LONG = {MESSAGES.mondayAbbreviationLong(), MESSAGES.tuesdayAbbreviationLong(), MESSAGES.wednesdayAbbreviationLong(),
                                     MESSAGES.thursdayAbbreviationLong(), MESSAGES.fridayAbbreviationLong(), MESSAGES.saturdayAbbreviationLong(),
                                     MESSAGES.sundayAbbreviationLong()};
    public final static String[] NAME_OF_MONTH = {MESSAGES.januaryAbbreviation(), MESSAGES.februaryAbbreviation(), MESSAGES.marchAbbreviation(),
                                      MESSAGES.aprilAbbreviation(), MESSAGES.mayAbbreviation(), MESSAGES.juneAbbreviation(),
                                      MESSAGES.julyAbbreviation(), MESSAGES.augustAbbreviation(), MESSAGES.septemberAbbreviation(),
                                      MESSAGES.octoberAbbreviation(), MESSAGES.novemberAbbreviation(), MESSAGES.decemberAbbreviation()};
    public final static long DAY_IN_MILLIS = 86400000L;
    public final static long HOUR_IN_MILLIS = 3600000L;
    public final static long MINUTE_IN_MILLIS = 60000L;
    public final static int DAYS_PER_WEEK = 7;
    public final static int SUNDAY = 0;
    public final static int MONDAY = 1;
    public final static int TUESDAY = 2;
    public final static int WEDNESDAY = 3;
    public final static int THURSDAY = 4;
    public final static int FRIDAY = 5;
    public final static int SATURDAY = 6;

    /**
     * @param date
     * @return a string representation of the given date
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }

        return MESSAGES.dateFormat( (date.getYear() + 1900), (date.getMonth() + 1), date.getDate());
    }


    public static void addDays(Date day, int days) {
        day.setTime(day.getTime() + (days * DAY_IN_MILLIS));
    }
    
    public static String doubleToHours(double ihours) {
        double hours = Math.abs( ihours );
        int minutes = (int)Math.round((hours - ((int)hours)) * 60);
        
        //vermeidet Angaben wie 7:60
        if (minutes == 60) {
            ++hours;
            minutes = 0;
        }
        
        return (ihours < 0.0 ? "-" : "") + (int)hours + ":" + IntToDoubleDigit( minutes );
    }
    
    public static double hoursToDouble(String hours) throws NumberFormatException {
        String[] st = hours.split(":");
        
        if (st.length == 2) {
            String hour = st[0];
            String minute = st[1];
            
            int hourInt = Integer.parseInt(hour);
            int minuteInt = Integer.parseInt(minute);
            
            return  hourInt + minuteInt / 60.0;
        } else if (st.length == 1) {
            try {
                return Integer.parseInt( st[0] );
            } catch (final NumberFormatException e) {
                throw new NumberFormatException(hours + " is no valid duration");
            }
        } else {
            throw new NumberFormatException(hours + " is no valid duration");
        }
    } 
    
    /**
     * @param date
     * @return a string representation of the given time
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }

        return MESSAGES.dateTimeFormat( (date.getYear() + 1900), (date.getMonth() + 1), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
    }


    /**
     * @param date
     * @return a string representation of the given time
     */
    public static String formatTime(Date date) {
        if (date == null) {
            return "";
        }

        return MESSAGES.timeFormat( date.getHours(), date.getMinutes());
    }


    public static Date createDateObject(Date day, Date time) {
//        long dayInMillis = day.getTime();
//        dayInMillis = dayInMillis - (dayInMillis % DAY_IN_MILLIS);
//        long timeInMillis = time.getTime() % dayInMillis;
//        
//        return new Date(dayInMillis + timeInMillis);
        return new Date(day.getYear(), day.getMonth(), day.getDate(), time.getHours(), time.getMinutes(), time.getSeconds());
    }
    
    /**
     * @param date
     * @param otherDate
     * @return true, if and only if the two given date objects represents the same day
     */
    public static boolean isSameDay(Date date, Date otherDate) {
        if (date == null && otherDate == null) {
            return true;
        } else if (date == null || otherDate == null)  {
            return false;
        }

        return (date.getYear() == otherDate.getYear() &&
                date.getMonth() == otherDate.getMonth() &&
                date.getDate() == otherDate.getDate() );
    }
    
    /**
     * @param date
     * @param otherDate
     * @return true, if and only if the two given date objects represents the same day. A day starts at 4 am
     */
    public static boolean isSameDaySV(Date date, Date otherDate) {
        if (date == null && otherDate == null) {
            return true;
        } else if (date == null || otherDate == null)  {
            return false;
        }
        
        Date newDate = new Date(date.getTime() - (4 * HOUR_IN_MILLIS));
        Date newOtherDate = new Date(otherDate.getTime() - (4 * HOUR_IN_MILLIS));
        
        return (newDate.getYear() == newOtherDate.getYear() &&
                newDate.getMonth() == newOtherDate.getMonth() &&
                newDate.getDate() == newOtherDate.getDate() );
    }

    /**
     * compares two calendar objects
     * @param date1
     * @param date2
     * @return true, if and only if date1 is less or equal to date2. The result
     *               only depends on the dates, which are contained in the given GregorianCalendar objects. The times will be ignored.
     */
    public static boolean isDateLessOrEqual(Date date1, Date date2) {
        int firstDate = (date1.getYear() + 1900) * 10000 + date1.getMonth() * 100 + date1.getDate();
        int secondDate = (date2.getYear() + 1900) * 10000 + date2.getMonth() * 100 + date2.getDate();

        return (firstDate <= secondDate);
    }

    /**
     * compares two calendar objects
     * @param date1
     * @param date2
     * @return true, if and only if date1 is less or equal to date2. The result
     *               only depends on the dates, which are contained in the given GregorianCalendar objects. The times will be ignored.
     */
    public static boolean isDateGreaterOrEqual(Date date1, Date date2) {
        int firstDate = (date1.getYear() + 1900) * 10000 + date1.getMonth() * 100 + date1.getDate();
        int secondDate = (date2.getYear() + 1900) * 10000 + date2.getMonth() * 100 + date2.getDate();

        return (firstDate >= secondDate);
    }    
    
    /**
     * converts the given String object to a Date object
     *
     * @param dateString a date string with the format year-month-day (2009-10-19)
     * @return a Date object
     * @throws IllegalArgumentException
     */
    public static Date parseString(String dateString, DateTimeFormat dtf) throws IllegalArgumentException {
        if (dtf == null) {
            dtf = DateTimeFormat.getFormat("y-M-d");
        }
        return dtf.parseStrict(dateString);
    }


    /**
     * converts a number to a string that contains a double digit.
     *
     * @param number a positive integer with at most two digits
     * @return a string that contains a double digit
     */
    private static String IntToDoubleDigit(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }

    /**
     * @return the number of the current week
     */
    public static int getCurrentWeek() {
        return getWeekOfYear(new Date());
    }

    /**
     * @return the number of the week of the given date
     */
    public static int getWeekOfYear(Date date) {
        Date givenDate = new Date(date.getTime());
        Date firstWeek = new Date(givenDate.getTime());     // this operation can change the timezone, so the timezoneOffset must be considered in the following calculations
        firstWeek.setMonth(0);
        firstWeek.setDate(4);
        // givenDate should contain the same day of week as the firstWeek but
        // should be in the same week of year as it was before
        int dayOfWeekJanTheFourth = DateHelper.convertDayOfWeek(firstWeek.getDay());
        int dayOfWeekNow = DateHelper.convertDayOfWeek(givenDate.getDay());
        int diff = dayOfWeekJanTheFourth - dayOfWeekNow;
        givenDate.setTime( givenDate.getTime() + (diff * DAY_IN_MILLIS) );
        
        long givenDateNormalized = givenDate.getTime() - givenDate.getTimezoneOffset() * 60000;
        long firstWeekNormalized = firstWeek.getTime() - firstWeek.getTimezoneOffset() * 60000;
        long timeDistance = (givenDateNormalized - firstWeekNormalized);
        long week = timeDistance / (DAY_IN_MILLIS * DAYS_PER_WEEK) + 1;

        if (week == 0) {
            week = getWeekCountForYear(givenDate.getYear() + 1899);
        } else if (week == 53 && getWeekCountForYear(date.getYear() + 1900) == 52 ) {
            week = 1;
        } else if (week > 53) {
            week = 1;
        }

        return (int)week;
    }

    /**
     * @param start
     * @param end
     * @return the count of months, which are contained within the given time interval
     */
    public static int getMonthsOfPeriod(Date start, Date end) {
        int year = end.getYear() - start.getYear();
        int months = end.getMonth() - start.getMonth();
        int day = 1;

        if (end.getDate() < start.getDate()) {
            day = 0;
        }

        return months + (year * 12) + day;
    }

    /**
     *
     * @param year
     * @return the count of weeks in the given year according to ISO 8601
     */
    public static int getWeekCountForYear(int year) {
        int weekCount = 52;
        Date date = new Date(year - 1900, 0, 1);

        // according to ISO 8601, a year has 53 weeks, if and only if the first day or the last day of the year is a thursday
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
     *
     * @param year
     * @param week
     * @return the first day of the given week in the given year according to ISO 8601
     */
    public static Date getBeginOfWeek(int year, int week) {
        Date date = new Date(year - 1900, 0, 1);
        // set to monday
        date.setTime( date.getTime() - ( convertDayOfWeek( date.getDay() ) * DAY_IN_MILLIS) );

        int currentWeekOfYear = getWeekOfYear(date);
        int origWeek = week;
        
        if ( currentWeekOfYear == 1 ) {
            week -= 1;
        }

        date.setTime( date.getTime() + (week * DAY_IN_MILLIS * DAYS_PER_WEEK) );

        //TODO: folgendes if entfernen. Das if ist ausschliesslich zu Debugzwecken enthalten
        if (getWeekOfYear( date ) != origWeek) {
            ProjectTrackerEntryPoint.outputBox("error in method getBeginOfWeek");
        }

        return date;
    }

    /**
     * this method converts the given day of week with the sunday as 0 to a day of week with the monday as 0.
     * @return
     */
    public static int convertDayOfWeek(int dayOfWeek) {
        return mod((dayOfWeek - 1), 7);
    }

    /**
     * calculates a modulo. The result is a positive integer from 0 to <code>range</code> - 1
     * @param number
     * @param range
     * @return
     */
    private static int mod(int number, int range) {
        // the javascript modulo operation can have a negative result
        return ((number % range) + range) % range;
    }

    public static boolean isDayInWorkPackagePeriod(Date day, WorkPackagePeriodDTO period) {
        return ( isDateGreaterOrEqual(day, period.getFromdate()) && ( period.getTodate() == null || isDateLessOrEqual(day, period.getTodate()) ) );
    }

    public static boolean isDayInProjectPeriod(Date day, ProjectPeriodDTO period) {
        return ( isDateGreaterOrEqual(day, period.getFromdate()) && ( period.getTodate() == null || isDateLessOrEqual(day, period.getTodate()) ) );
    }

    /**
     * 
     * @param time
     * @param otherTime 
     * @return the difference of the two times in hours
     */
    public static double substract(Date time, Date otherTime) {
        return (otherTime.getTime() - time.getTime()) / (double)HOUR_IN_MILLIS;
    }
}
