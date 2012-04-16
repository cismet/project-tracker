/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.listener;

import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;

/**
 *
 * @author therter
 */
public interface TimeStoryListener {
    public void timeNoticeCreated(TimeStoryEvent e);
    public void timeNoticeChanged(TimeStoryEvent e);
    public void timeNoticeDeleted(TimeStoryEvent e);
}
