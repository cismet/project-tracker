/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.uicomps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ImageConstants;
import de.cismet.projecttracker.client.MessageConstants;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.event.MenuEvent;
import de.cismet.projecttracker.client.common.ui.listener.MenuListener;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author therter
 */
public class TopPanel extends Composite implements ClickHandler, KeyUpHandler, ChangeHandler {
    public final static int SHEETS = 1;
    public final static int REPORTS = 1;
    public final static int PROFILE = 1;
    private final static MessageConstants MESSAGES = (MessageConstants)GWT.create(MessageConstants.class);
    private Image logo = new Image(ImageConstants.INSTANCE.cLogo());
//    private HorizontalPanel topPanel = new HorizontalPanel();
    private FlowPanel topPanel = new FlowPanel();
//    private FlowPanel fillPanel = new FlowPanel();
    private FlowPanel containerPanel = new FlowPanel();
    private FlowPanel loginPanel = new FlowPanel();
    private FlowPanel userPanel = new FlowPanel();
    private Label sheetsLab = new Label("Sheets");
    private Label reportsLab = new Label("Reports");
    private Label profileLab = new Label("Profile");
    private Label userDataLab = new Label("");
    private Label logoLab = new Label("cismet Tracker");
    private Label activeLab = sheetsLab;
    private ListBox user = new ListBox();
    private List<MenuListener> listener = new ArrayList<MenuListener>();
    private TextBox username = new TextBox();
    private PasswordTextBox password = new PasswordTextBox();
    private Button login = new Button("Sign in");
    private Button logout = new Button("Sign out");
    private Image gravatar = new Image();
    private List<StaffDTO> userList = new ArrayList<StaffDTO>();


    public TopPanel() {
        init();
//        topPanel.add(logo);
//        topPanel.setCellHorizontalAlignment(logo, HorizontalPanel.ALIGN_LEFT);
        topPanel.setWidth("100%");
        setActiveLab();
        initWidget(topPanel);
        user.addChangeHandler(this);
    }


    private void init() {
        user.setWidth("150px");
        loginPanel.add(username);
        loginPanel.add(password);
        loginPanel.add(login);
        containerPanel.add(logo);
        containerPanel.add(logoLab);
        containerPanel.add(sheetsLab);
        containerPanel.add(reportsLab);
        containerPanel.add(profileLab);
        containerPanel.add(userPanel);
        containerPanel.add(loginPanel);
        topPanel.add(containerPanel);
//        topPanel.add(fillPanel);
//        fillPanel.add(containerPanel);
        
//        topPanel.setStyleName("innerTopPanel");
        
//        topPanel.setCellWidth(homeLab, "100px");
//        topPanel.setCellWidth(basicLab, "100px");
//        topPanel.setCellWidth(reportLab, "100px");
        
        logo.setStyleName("cismet-logo");
        logoLab.setStyleName("brand");
        userDataLab.setStyleName("user-data");
        username.setStyleName("input-small white");
        password.setStyleName("input-small");
        loginPanel.setStyleName("pull-right loginPanel");
        userPanel.setStyleName("pull-left userList");
        gravatar.setStyleName("pull-left gravatar-image");
        login.setStyleName("btn");
        logout.setStyleName("btn");
//        fillPanel.setStyleName("fill");
        containerPanel.setStyleName("container");
        sheetsLab.setStyleName("LabelLink");
        reportsLab.setStyleName("LabelLink");
        profileLab.setStyleName("LabelLink");
        topPanel.setStyleName("fill");
//        topPanel.setStyleName("topbar");
        
        sheetsLab.addClickHandler(this);
        profileLab.addClickHandler(this);
        reportsLab.addClickHandler(this);
        login.addClickHandler(this);
        logout.addClickHandler(this);
        password.addKeyUpHandler(this);
    }

    
    public void fillUser() {
        user.clear();
        if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
            return;
        }

        BasicAsyncCallback<ArrayList<StaffDTO>> callback = new BasicAsyncCallback<ArrayList<StaffDTO>>() {
            @Override
            protected void afterExecution(ArrayList<StaffDTO> result, boolean operationFailed) {
                int i = 0;
                int index = 0;
                Collections.sort(result);
                StaffDTO loggedInStaff = ProjectTrackerEntryPoint.getInstance().getStaff();
                for (StaffDTO staff : result) {
                    user.addItem(staff.getFirstname() + " " + staff.getName(), staff.getId() + "");
                    if (staff.getId() == loggedInStaff.getId()) {
                        index = i;
                    }
                    ++i;
                }
                user.setSelectedIndex(index);
                userList = result;
            }
        };

        ProjectTrackerEntryPoint.getProjectService(true).getCurrentEmployees(callback);
        userPanel.add(user);
    }
    
    private void setActiveLab() {
        sheetsLab.removeStyleDependentName("active");
        reportsLab.removeStyleDependentName("active");
        profileLab.removeStyleDependentName("active");
        
        activeLab.addStyleDependentName("active");
    }


    public void addSignOutListener(ClickHandler handler) {
//        signOut.addClickHandler(handler);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == login) {
            login();
        } else if (event.getSource() == logout) {
            BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>();
            ProjectTrackerEntryPoint.getProjectService(true).logout(callback);
            ProjectTrackerEntryPoint.getInstance().logout();
        } else {
            activeLab = (Label)event.getSource();
            setActiveLab();
            MenuEvent newEvent = new MenuEvent();
            newEvent.setSource(this);

            if (activeLab == sheetsLab) {
                newEvent.setNumber(SHEETS);
            } else if (activeLab == reportsLab) {
                newEvent.setNumber(REPORTS);
            } else if (activeLab == profileLab) {
                newEvent.setNumber(PROFILE);
            }

            fireMenuChangeEvent(newEvent);
        }
    }
    
    public void addMenuListener(MenuListener l) {
        listener.add(l);
    }

    public void removeMenuListener(MenuListener l) {
        listener.remove(l);
    }
    
    public void fireMenuChangeEvent(MenuEvent e) {
        for (MenuListener l : listener) {
            l.menuChangeEvent(e);
        }
    }
    
    public void setLoggedIn(boolean loggedIn, String user) {
        if (loggedIn) {
            userDataLab.setText(user);
            loginPanel.clear();
            loginPanel.add(gravatar);
            loginPanel.add(userDataLab);
            loginPanel.add(logout);
        } else {
            loginPanel.clear();
            loginPanel.add(username);
            loginPanel.add(password);
            loginPanel.add(login);
        }
    }
    
    /**
     * executes a login
     */
    private void login() {
        BasicAsyncCallback<StaffDTO> callback = new BasicAsyncCallback<StaffDTO>() {
            @Override
            protected void afterExecution(StaffDTO result, boolean operationFailed) {
                if (!operationFailed) {
                    ProjectTrackerEntryPoint.getInstance().login(result, (result.getPermissions() & 0x1) == 0x1 );
                    username.setText("");
                    password.setText("");                    
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).login(username.getText(), password.getText(), callback);
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == 13) {
            login();
        }
    }

    public void setGravatar(String url) {
        gravatar.setUrl(url);
    }

    @Override
    public void onChange(ChangeEvent event) {
        ProjectTrackerEntryPoint.getInstance().setStaff(getSelectedStaff());
        MenuEvent e = new MenuEvent();
        e.setSource(this);
        fireMenuChangeEvent(e);
    }
    
    private StaffDTO getSelectedStaff() {
        try {
            long value = Long.parseLong( user.getValue( user.getSelectedIndex() ) );
            for (StaffDTO staff : userList) {
                if (staff.getId() == value) {
                    return staff;
                }
            }
        } catch (NumberFormatException e) {
            //should not happen
        }
        
        return null;
    }
    
    public void removeUserList() {
        userPanel.remove(user);
    }
}

