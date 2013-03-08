/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.common.ui.listener;

import de.cismet.projecttracker.client.common.ui.event.TaskStoryEvent;

/**
 *
 * @author thorsten
 */
public interface TaskStoryListener {
    public void taskNoticeCreated(TaskStoryEvent e);
    public void taskNoticeChanged(TaskStoryEvent e);
    public void taskNoticeDeleted(TaskStoryEvent e);
}
