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
package de.cismet.projecttracker.client.common.ui;

import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class HolidayTaskNotice extends TaskNotice {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HolidayTaskNotice object.
     *
     * @param  activity  DOCUMENT ME!
     */
    public HolidayTaskNotice(final ActivityDTO activity) {
        super(activity, true);
    }

    /**
     * Creates a new HolidayTaskNotice object.
     *
     * @param  activity  DOCUMENT ME!
     */
    public HolidayTaskNotice(final ActivityDTO activity, boolean favorite) {
        super(activity, true, favorite);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getTextFromActivity() {
        final String desc = getDesccription(activity.getDescription());
        final StringBuilder text = new StringBuilder("");

        if (activity.getKindofactivity() == ActivityDTO.HALF_HOLIDAY) {
            text.append("Half Holiday");
        } else {
            text.append("Holiday");
        }

        text.append("<br />").append(desc);

        return text.toString();
    }

    @Override
    protected String getDesccription(final String desc) {
        if (desc == null) {
            return "";
        } else {
            return desc;
        }
    }

    @Override
    protected String getTooltipTextFromActivity() {
        final StringBuilder text = new StringBuilder("");
        if (activity.getKindofactivity() == ActivityDTO.HALF_HOLIDAY) {
            text.append("\n").append("Half Holiday");
        } else {
            text.append("\n").append("Holiday");
        }

        if (activity.getDescription() != null) {
            text.append("\n").append(activity.getDescription());
        }

        return text.toString();
    }
}
