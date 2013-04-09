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
package de.cismet.projecttracker.client.uicomps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.cismet.projecttracker.client.ImageConstants;
import de.cismet.projecttracker.client.MessageConstants;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.LoginPanel;
import de.cismet.projecttracker.client.common.ui.event.MenuEvent;
import de.cismet.projecttracker.client.common.ui.listener.MenuListener;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TopPanel extends Composite implements ClickHandler, ChangeHandler {

    //~ Static fields/initializers ---------------------------------------------

    public static final int SHEETS = 1;
    public static final int REPORTS = 2;
    public static final int PROFILE = 3;
    private static final MessageConstants MESSAGES = (MessageConstants)GWT.create(MessageConstants.class);

    //~ Instance fields --------------------------------------------------------

    private Image logo = new Image(ImageConstants.INSTANCE.cLogo());
//    private HorizontalPanel topPanel = new HorizontalPanel();
    private FlowPanel topPanel = new FlowPanel();
//    private FlowPanel fillPanel = new FlowPanel();
    private FlowPanel containerPanel = new FlowPanel();
    private LoginPanel loginPanel = new LoginPanel();
    private FlowPanel userPanel = new FlowPanel();
    private Label sheetsLab = new Label("Sheets");
    private Label reportsLab = new Label("Reports");
    private Label profileLab = new Label("Profile");
    private Label logoLab = new Label("cismet Tracker");
    private Label activeLab = sheetsLab;
    private ListBox user = new ListBox();
    private List<MenuListener> listener = new ArrayList<MenuListener>();
    private List<StaffDTO> userList = new ArrayList<StaffDTO>();
    private ProfilePanel profilePanel = null;
    private ReportsPanel reportsPanel = null;
    private boolean loggedIn = false;
    private List<ChangeHandler> changeListeners = new ArrayList<ChangeHandler>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TopPanel object.
     */
    public TopPanel() {
        init();
        topPanel.setWidth("100%");
        setActiveLab();
        initWidget(topPanel);
        user.addChangeHandler(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        user.setWidth("150px");
        containerPanel.add(logo);
        containerPanel.add(logoLab);
        containerPanel.add(sheetsLab);
        containerPanel.add(reportsLab);
        containerPanel.add(profileLab);
        containerPanel.add(userPanel);
        containerPanel.add(loginPanel);
        topPanel.add(containerPanel);

        logo.setStyleName("cismet-logo");
        logoLab.setStyleName("brand");
        loginPanel.setStyleName("pull-right loginPanel");
        userPanel.setStyleName("pull-left userList");
        containerPanel.setStyleName("container");
        sheetsLab.setStyleName("LabelLink");
        reportsLab.setStyleName("LabelLink");
        profileLab.setStyleName("LabelLink");
        topPanel.setStyleName("navbar-inner");

        sheetsLab.addClickHandler(this);
        profileLab.addClickHandler(this);
        reportsLab.addClickHandler(this);
    }

    /**
     * DOCUMENT ME!
     */
    public void fillUser() {
        user.clear();
        if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
            return;
        }

        final BasicAsyncCallback<ArrayList<StaffDTO>> callback = new BasicAsyncCallback<ArrayList<StaffDTO>>() {

                @Override
                protected void afterExecution(final ArrayList<StaffDTO> result, final boolean operationFailed) {
                    int i = 0;
                    int index = 0;
                    Collections.sort(result);
                    final StaffDTO loggedInStaff = ProjectTrackerEntryPoint.getInstance().getStaff();
                    for (final StaffDTO staff : result) {
                        user.addItem(staff.getFirstname() + " " + staff.getName(), staff.getId() + "");
                        if (staff.getId() == loggedInStaff.getId()) {
                            index = i;
                        }
                        ++i;
                    }
                    user.setSelectedIndex(index);
                    // fire the change event since setSelected item does not
                    Scheduler.get().scheduleDeferred(new Command() {

                            @Override
                            public void execute() {
                                DomEvent.fireNativeEvent(Document.get().createChangeEvent(), user);
                            }
                        });
                    userList = result;
                }
            };

        ProjectTrackerEntryPoint.getProjectService(true).getCurrentEmployees(callback);
        userPanel.add(user);
    }

    /**
     * DOCUMENT ME!
     */
    private void setActiveLab() {
        sheetsLab.removeStyleDependentName("active");
        reportsLab.removeStyleDependentName("active");
        profileLab.removeStyleDependentName("active");

        activeLab.addStyleDependentName("active");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  handler  DOCUMENT ME!
     */
    public void addSignOutListener(final ClickHandler handler) {
//        signOut.addClickHandler(handler);
    }

    @Override
    public void onClick(final ClickEvent event) {
        if (loggedIn) {
            activeLab = (Label)event.getSource();
            setActiveLab();
            final MenuEvent newEvent = new MenuEvent();
            newEvent.setSource(this);

            if (activeLab == sheetsLab) {
                newEvent.setNumber(SHEETS);
            } else if (activeLab == reportsLab) {
                if (reportsPanel == null) {
                    reportsPanel = new ReportsPanel();
                    this.addMenuListener(reportsPanel);
                }
                newEvent.setNumber(REPORTS);
            } else if (activeLab == profileLab) {
                if (profilePanel == null) {
                    profilePanel = new ProfilePanel();
                    this.addChangeListener(profilePanel);
                    this.addMenuListener(profilePanel);
                }
//                RootPanel.get("contentId").clear();
//                RootPanel.get("contentId").add(new ProfilePanel());
                newEvent.setNumber(PROFILE);
            }

            fireMenuChangeEvent(newEvent);
        } else {
            ProjectTrackerEntryPoint.outputBox("Please first log in!");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void addMenuListener(final MenuListener l) {
        listener.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeMenuListener(final MenuListener l) {
        listener.remove(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    public void fireMenuChangeEvent(final MenuEvent e) {
        for (final MenuListener l : listener) {
            l.menuChangeEvent(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void addChangeListener(final ChangeHandler l) {
        changeListeners.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeChangeListener(final ChangeHandler l) {
        changeListeners.remove(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    public void fireChangeEvent(final ChangeEvent e) {
        for (final ChangeHandler l : changeListeners) {
            l.onChange(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  loggedIn  DOCUMENT ME!
     * @param  user      DOCUMENT ME!
     */
    public void setLoggedIn(final boolean loggedIn, final String user) {
        loginPanel.setLoggedIn(loggedIn, user);
        if (loggedIn == false) {
            activeLab = sheetsLab;
            setActiveLab();
        } else {
            this.loggedIn = true;
        }
        profilePanel = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    public void setGravatar(final String url) {
        loginPanel.setGravatar(url);
    }

    @Override
    public void onChange(final ChangeEvent event) {
        ProjectTrackerEntryPoint.getInstance().setStaff(getSelectedStaff());
        final MenuEvent e = new MenuEvent();
        e.setSource(this);
        fireMenuChangeEvent(e);
        fireChangeEvent(event);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private StaffDTO getSelectedStaff() {
        try {
            final long value = Long.parseLong(user.getValue(user.getSelectedIndex()));
            for (final StaffDTO staff : userList) {
                if (staff.getId() == value) {
                    return staff;
                }
            }
        } catch (NumberFormatException e) {
            // should not happen
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     */
    public void removeUserList() {
        userPanel.remove(user);
    }
}
