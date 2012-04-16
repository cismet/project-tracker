/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 *
 * @author therter
 */
public class HolidayTaskNotice extends TaskNotice {

    public HolidayTaskNotice(ActivityDTO activity) {
        super(activity, true);
    }

    protected String getTextFromActivity() {
        String desc = getDesccription(activity.getDescription());
        StringBuilder text = new StringBuilder("");
        
        if (activity.getKindofactivity() == ActivityDTO.HALF_HOLIDAY) {
            text.append("Half Holiday");
        } else {
            text.append("Holiday");
        }
        
        text.append("<br />").append(desc);

        return text.toString();
    }
    
    
    protected String getDesccription(String desc) {
        if (desc == null) {
            return "";
        } else {
            return desc;
        }
    }
    
    
    protected String getTooltipTextFromActivity() {
        StringBuilder text = new StringBuilder("");
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
