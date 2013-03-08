package de.cismet.projecttracker.client.common.ui.listener;

import java.util.Date;

/**
 * This class can be used to register parties so, that they will be notified when
 * the user has a date selected
 *
 * @author therter
 */
public interface DateSelectionListener {
    /**
     *
     * @param date the selected date
     */
    public void setDate(Date date);
}
