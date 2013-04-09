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
package de.cismet.projecttracker.client.utilities;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class IllnessChecker implements TaskStoryListener {
    //~ Instance fields --------------------------------------------------------

// private static final Logger LOG = new Logger

    private TaskStory story;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new IllnessChecker object.
     *
     * @param  story  DOCUMENT ME!
     */
    public IllnessChecker(final TaskStory story) {
        this.story = story;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void taskNoticeCreated(final TaskStoryEvent e) {
        checkIllnesConstraints(e.getTaskNotice());
    }

    @Override
    public void taskNoticeChanged(final TaskStoryEvent e) {
        checkIllnesConstraints(e.getTaskNotice());
    }

    @Override
    public void taskNoticeDeleted(final TaskStoryEvent e) {
        checkIllnesConstraints(e.getTaskNotice());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  taskNotice  DOCUMENT ME!
     */
    private void checkIllnesConstraints(final TaskNotice taskNotice) {
        // check if there are illnes activites
        final ActivityDTO newActivity = taskNotice.getActivity();
        final Date day = newActivity.getDay();
        final BasicAsyncCallback<Boolean> cb = new BasicAsyncCallback<Boolean>() {

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!result) {
                        performIllnessCheck(taskNotice);
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, newActivity.getStaff(), cb);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  taskNotice  DOCUMENT ME!
     */
    private void performIllnessCheck(final TaskNotice taskNotice) {
        final ActivityDTO newActivity = taskNotice.getActivity();
        final Date day = newActivity.getDay();
        final List<TaskNotice> tasks = story.getTasksForDay(day.getDay());
        try {
            final ContractDTO contract = ProjectTrackerEntryPoint.getInstance().getContractForStaff(day);
            if (contract == null) {
                Logger.getLogger(IllnessChecker.class.getName())
                        .log(
                            Level.SEVERE,
                            "Could not get valid "
                            + "Contract for Staff "
                            + ProjectTrackerEntryPoint.getInstance().getStaff()
                            + " and date"
                            + day.toString());
                return;
            }
            final double whow = contract.getWhow() / 5;
            double whNonIllnes = 0;
            final List<TaskNotice> illnessAct = new LinkedList<TaskNotice>();
            boolean needed = false;
            for (final TaskNotice tn : tasks) {
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
            if ((illnessActCount >= 1) && needed) {
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
                        final BasicAsyncCallback<ActivityDTO> cb = new BasicAsyncCallback<ActivityDTO>() {

                                @Override
                                protected void afterExecution(final ActivityDTO result, final boolean operationFailed) {
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
