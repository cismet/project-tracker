/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.utilities;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.LockPanel;
import de.cismet.projecttracker.client.common.ui.TaskStory;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author dmeiers
 */
public class ClientSidePauseChecker {

    public static void isPausePolicyFulfilled(final Date day, StaffDTO staff, TaskStory taskStory, final LockPanel caller) {

        BasicAsyncCallback<ArrayList<ActivityDTO>> callback = new BasicAsyncCallback<ArrayList<ActivityDTO>>() {

            @Override
            protected void afterExecution(ArrayList<ActivityDTO> result, boolean operationFailed) {
                if (!operationFailed && !check(result)) {
                        caller.refuseLock(day);
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).getActivityByDay(staff, day, callback);

    }

    private static boolean check(ArrayList<ActivityDTO> activities) {
        Date lastBeginActivityTime = null;
        Date lastEndActivityTime = null;
        boolean pauseActivityNeeded = false;
        double pauseTimesFromActivity = 0;
        double activityWorkingHours = 0;
        //the time beetween two time slots
        double slotPauseTime = 0;
        for (ActivityDTO act : activities) {
            if (act.getKindofactivity() == ActivityDTO.BEGIN_OF_DAY) {
                lastBeginActivityTime = act.getDay();
                if (lastEndActivityTime != null) {
                    slotPauseTime += (act.getDay().getTime() - lastEndActivityTime.getTime()) / 1000 / 60 / 60d;
                }
            } else if (act.getKindofactivity() == ActivityDTO.END_OF_DAY) {
                lastEndActivityTime = act.getDay();
                if (lastBeginActivityTime != null) {
                    final long workingTimeSlot = act.getDay().getTime() - lastBeginActivityTime.getTime();
                    if (workingTimeSlot > 6 * 60 * 60 * 1000) {
                        pauseActivityNeeded = true;
                    }
                }
            } else if (act.getKindofactivity() == ActivityDTO.ACTIVITY) {
                final long wpId = act.getWorkPackage().getId();
                if (wpId == ActivityDTO.PAUSE_ID) {
                    pauseTimesFromActivity += act.getWorkinghours();
                } else if (!(wpId == ActivityDTO.ILLNESS_ID || wpId == ActivityDTO.SPARE_TIME_ID || wpId == ActivityDTO.Travel_ID || wpId == ActivityDTO.HOLIDAY_ID || wpId == ActivityDTO.SPECIAL_HOLIDAY_ID)) {
                    activityWorkingHours += act.getWorkinghours();
                }
            }
        }

        //there is a time slot greate than 6 hours so check if a there is a pause activity of at least 45 min
        if (pauseActivityNeeded) {
            if (pauseTimesFromActivity >= 0.75d) {
                return true;
            } else {
                return false;
            }
        }else{
            if(activityWorkingHours > 6d ){
                if(pauseTimesFromActivity + slotPauseTime >=0.75d){
                    return true;
                }else{
                    return false;
                }
            }else{
                return true;
            }
        }
    }
}
