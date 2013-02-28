/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.utilities;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.TaskNotice;
import de.cismet.projecttracker.client.common.ui.TaskStory;
import de.cismet.projecttracker.client.common.ui.event.TaskStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TaskStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ContractDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.uicomps.SheetsPanel;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel
 */
public class IllnessChecker implements TaskStoryListener {
//    private static final Logger LOG = new Logger

    private TaskStory story;

    public IllnessChecker(TaskStory story) {
        this.story = story;
    }

    @Override
    public void taskNoticeCreated(TaskStoryEvent e) {
        checkIllnesConstraints(e.getTaskNotice());
    }

    @Override
    public void taskNoticeChanged(TaskStoryEvent e) {
        checkIllnesConstraints(e.getTaskNotice());
    }

    @Override
    public void taskNoticeDeleted(TaskStoryEvent e) {
        checkIllnesConstraints(e.getTaskNotice());
    }

    private void checkIllnesConstraints(final TaskNotice taskNotice) {
        //check if there are illnes activites 
        final ActivityDTO newActivity = taskNotice.getActivity();
        final Date day = newActivity.getDay();
        BasicAsyncCallback<Boolean> cb = new BasicAsyncCallback<Boolean>() {
            @Override
            protected void afterExecution(Boolean result, boolean operationFailed) {
                if(!result){
                    performIllnessCheck(taskNotice);
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, newActivity.getStaff(), cb);
        
    }
    
    private void performIllnessCheck(final TaskNotice taskNotice){
        final ActivityDTO newActivity = taskNotice.getActivity();
        final Date day = newActivity.getDay();
        List<TaskNotice> tasks = story.getTasksForDay(day.getDay());
        try {
            final ContractDTO contract = ProjectTrackerEntryPoint.getInstance().getContractForStaff(day);
            if (contract == null) {
                Logger.getLogger(IllnessChecker.class.getName()).log(Level.SEVERE, "Could not get valid "
                        + "Contract for Staff " + ProjectTrackerEntryPoint.getInstance().getStaff()
                        + " and date" + day.toString());
                return;
            }
            final double whow = contract.getWhow() / 5;
            double whNonIllnes = 0;
            final List<TaskNotice> illnessAct = new LinkedList<TaskNotice>();
            boolean needed = false;
            for (TaskNotice tn : tasks) {
                if (tn.getActivity().getWorkPackage() != null) {
                    if (tn.getActivity().getWorkPackage().getId() == ActivityDTO.ILLNESS_ID) {
                        illnessAct.add(tn);
                    } else if (tn.getActivity().getWorkPackage().getId() != ActivityDTO.PAUSE_ID) {
                        needed = true;
                        whNonIllnes += tn.getActivity().getWorkinghours();
                    }
                }
            }
            final int illnessActCount = illnessAct.size();
            if (illnessActCount >= 1 && needed) {
                final double time = (whow - whNonIllnes) / illnessActCount;
                boolean hasChanged = false;
                for (final TaskNotice tn : illnessAct) {
                    if (time > 0) {
                        if (Math.abs(tn.getActivity().getWorkinghours() - time) > 0.01) {
                            hasChanged = true;
                        }
                        tn.getActivity().setWorkinghours(time);
                    } else {
                        if (tn.getActivity().getWorkinghours() != -1) {
                            hasChanged = true;
                        }
                        tn.getActivity().setWorkinghours(-1);
                    }
                    
                    if (hasChanged) {
                        BasicAsyncCallback<ActivityDTO> cb = new BasicAsyncCallback<ActivityDTO>() {
                            @Override
                            protected void afterExecution(ActivityDTO result, boolean operationFailed) {
                                tn.refresh();
                                ProjectTrackerEntryPoint.getInstance().getSheetsPanel().refreshAccountBalance();
                                ProjectTrackerEntryPoint.getInstance().getSheetsPanel().refreshWeeklyHoursOfWork();
                            }
                        };
                        ProjectTrackerEntryPoint.getProjectService(true).saveActivity(tn.getActivity(), cb);
                    }
                }
            }
        } catch (InvalidInputValuesException ex) {
            Logger.getLogger(IllnessChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
