/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.Widget;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.Date;
import java.util.List;

/**
 *
 * @author therter
 */
public class TaskDragHandler {
//implements DragHandler {
//    @Override
//    public void onPreviewDragStart(DragStartEvent event)
//            throws VetoDragException {
//    }
//
//    @Override
//    public void onPreviewDragEnd(DragEndEvent event)
//            throws VetoDragException {
//
//    }
//
//    @Override
//    public void onDragStart(DragStartEvent event) {
//    }
//
//    @Override
//    public void onDragEnd(DragEndEvent event) {
//        TaskStory.RestorePickupDragController o = (TaskStory.RestorePickupDragController)event.getContext().dragController;
//        final Widget dropTarget = event.getContext().finalDropController.getDropTarget();
//        List<Widget> selectedWidgets = event.getContext().selectedWidgets;
//
//        if (dropTarget instanceof FlowPanelWithSpacer) {
//            final List<TaskNotice> taskList = taskMap.get((FlowPanelWithSpacer)dropTarget);
//
//            if (taskList != null && selectedWidgets != null) {
//                for (Widget tmp : selectedWidgets) {
//                    if (tmp instanceof TaskNotice) {
//                        o.restore();
//                        if (tmp.getParent() != dropTarget) {
//                            TaskNotice origNotice = (TaskNotice)tmp;
//
//                            //save the changes on the server
//                            int days = 6;
//
//                            if (dropTarget == monday) {
//                                days = 0;
//                            } else if (dropTarget == tuesday) {
//                                days = 1;
//                            } else if (dropTarget == wednesday) {
//                                days = 2;
//                            } else if (dropTarget == thursday) {
//                                days = 3;
//                            } else if (dropTarget == friday) {
//                                days = 4;
//                            } else if (dropTarget == saturday) {
//                                days = 5;
//                            }
//                            final ActivityDTO activity = origNotice.getActivity().createCopy();
//                            Date newDate = (Date)firstDayOfWeek.clone();
//                            DateHelper.addDays(newDate, days);
//
//                            activity.setId(0);
//                            activity.setWorkinghours(0.0);
//                            activity.setDay(newDate);
//                            BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>() {
//
//                                @Override
//                                protected void afterExecution(Long result, boolean operationFailed) {
//                                    if (!operationFailed) {
//                                        activity.setId(result);
//                                        addTask(activity, (FlowPanelWithSpacer)dropTarget);
//                                    }
//                                }
//
//                            };
//                            activity.setDay(newDate);
//                            ProjectTrackerEntryPoint.getProjectService(true).createActivity(activity, callback);
//                        }
//                    }
//                }
//            }
//        }
//    }
}
