package de.cismet.projecttracker.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import de.cismet.projecttracker.client.common.ui.LoadingPanel;
import de.cismet.projecttracker.client.common.ui.event.MenuEvent;
import de.cismet.projecttracker.client.common.ui.listener.MenuListener;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.uicomps.SheetsPanel;
import de.cismet.projecttracker.client.uicomps.TopPanel;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main class of the ProjectTracker and will be automatic instantiated by
 * the Google Web Toolkit to start the ProjectTracker application.
 *
 * @author therter
 */
public class ProjectTrackerEntryPoint implements EntryPoint, ValueChangeHandler, ResizeHandler, HasResizeHandlers, ClickHandler {
    private static ProjectTrackerEntryPoint currentInstance;
    private static String GRAVATAR_URL_PREFIX = "http://www.gravatar.com/avatar/";
    private HandlerManager handlerManager = new HandlerManager(this);
    private DockPanel outer = new DockPanel();
    // saves the user, that is currently logged in
    private StaffDTO staff;
    private StaffDTO loggedInStaff;
    // true, if the curerntly logged in user has admin permission
    private boolean admin;
    private TopPanel topPanel = new TopPanel();
    private String activePanel = "Sheets";
    private SheetsPanel sheets = new SheetsPanel();
    private List<ProjectDTO> projects;
    private HandlerRegistration windowResize;
    private HandlerRegistration resize;



    /**
     * Creates a new instance of ProjectTrackerEntryPoint
     * This constructor should not be used. The only instance of this class
     * should be created by the GWT.
     * 
     * @see #getInstance() 
     */
    public ProjectTrackerEntryPoint() {
        currentInstance = this;
        // Hook the window resize event, so that we can adjust the UI.
        Window.addResizeHandler(this);
    }

    
    /**
     * @return the current ProjectTrackerEntryPoint instance
     */
    public static ProjectTrackerEntryPoint getInstance() {
        // the currentInstance variable must be initialized, because the constructor
        // was invoked first after the start of the application
        return currentInstance;
    }


    /**
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    @Override
    public void onModuleLoad() {
//        Widget root;
//
//        root = getBasicScreen();

        History.addValueChangeHandler(this);
        // Finally, add the outer panel to the RootPanel, so that it will be
        // displayed.
        if (RootPanel.get().getWidgetCount() > 0) {
            //if a widget was already added, it will be removed
            RootPanel.get().remove(0);
        }
        checkLogin();
        RootPanel.get("topbarId").add(topPanel);

        // Call the window resized handler to get the initial sizes setup. Doing
        // this in a deferred command causes it to occur after all widgets' sizes
        // have been computed by the browser.
        DeferredCommand.addCommand(new Command() {
            @Override
            public void execute() {
                validateSize();
            }
        });

        windowResize = Window.addResizeHandler(sheets);
        resize = addResizeHandler(sheets);
    }

    
    public void checkLogin() {
        BasicAsyncCallback<StaffDTO> callback = new BasicAsyncCallback<StaffDTO>() {

            @Override
            protected void afterExecution(StaffDTO result, boolean operationFailed) {
                if (!operationFailed) {
                    if (result != null) {
                        login(result, (result.getPermissions() & 0x1) == 0x1 );
                    }
                } else {
//                    RootPanel.get("contentId").add(getBasicScreen());
                }
            }
        };

        ProjectTrackerEntryPoint.getProjectService(true).checkLogin(callback);
        
    }

    /**
     * @return true, if and only if a user is currently logged in on the client side. It is possible
     * that the session was expired on the server side, but the user is still logged in on the client side.
     */
    public boolean isLoggedIn() {
        return loggedInStaff != null;
    }


    /**
     * logs in
     * @param username the user, who has logged in
     * @param admin true, if the user has admin permissions
     */
    public void login(StaffDTO staff, boolean admin) {
        this.staff = staff;
        this.loggedInStaff = staff;
        this.admin = admin;
        topPanel.setLoggedIn(true, staff.getUsername());
        
//        RootPanel ps = RootPanel.get("PleaseSignin");
//        if (ps != null) {
//            RootPanel.get("contentId").remove(ps);
//        }
        RootPanel.get("contentId").add(sheets);
        
        validateSize();
        BasicAsyncCallback<ArrayList<ProjectDTO>> callback = new BasicAsyncCallback<ArrayList<ProjectDTO>>() {
            @Override
            protected void afterExecution(ArrayList<ProjectDTO> result, boolean operationFailed) {
                projects = result;
            }
        };

        ProjectTrackerEntryPoint.getProjectService(true).getAllProjectsFull(callback);
        sheets.refresh();
        topPanel.addMenuListener(sheets);
        if (staff.getEmail() != null) {
            topPanel.setGravatar(GRAVATAR_URL_PREFIX + md5(staff.getEmail()) + "?s=30");
        }
        topPanel.fillUser();
    }

    public StaffDTO getStaff() {
        return staff;
    }
    
    public void setStaff(StaffDTO staff) {
        this.staff = staff;
    }

    public StaffDTO getLoggedInStaff() {
        return loggedInStaff;
    }

    /**
     * resizes the main window and fires a resize event to all registrated handler
     */
    public void validateSize() {
        resize(Window.getClientWidth(), Window.getClientHeight());
        ResizeEvent.fire(this, Window.getClientWidth(), Window.getClientHeight());
    }

    /**
     * @return the Policy Management Service interface
     */
    public static ProjectServiceAsync getProjectService(boolean showProgressBar) {
        if (showProgressBar) {
            LoadingPanel.getInstance().showLoadingAnimation();
        }
//        getInstance().output("");
        return ProjectServiceAsync.Util.getInstance();
    }

    /**
     * @return true, if and only if the currently signed user has admin rights
     */
    public boolean isAdmin() {
        return this.admin;
    }

    /**
     * @return the maximum width of the widget, that is shown in the middle of
     * the browser window.
     * See {@see showNewCenterPanel(Widget)}
     */
    public int getMaxWidth() {
        return Window.getClientWidth() - 10;
    }

    public int getMaxHeight() {
//        return Window.getClientHeight() - topPanel.getOffsetHeight() - menuBar.getOffsetHeight() - menuBarBottom.getOffsetHeight() - statusBar.getOffsetHeight() - 20;
        //todo eventuell korrigieren
        return Window.getClientHeight();
    }

    /**
     * @return a DockPanel that cointains the top panel, the two menu bars and the main panel in the middel.
     */
    private Widget getBasicScreen() {

//        outer = new DockPanel();
//        flow = new FlowPanel();
//        TopPanel panel = new TopPanel();
        SheetsPanel sheets = new SheetsPanel();
//        panel.addMenuListener(this);
//        flow.add(panel);
//        flow.add(sheets);
        // todo: hat an dieser Stelle nichts verloren

//        outer.setWidth( "100%");
//        outer.add(panel, DockPanel.NORTH);

        return sheets;
    }    

    /**
     * opens a popup and prints the given message
     *
     * @param msg the message, that should be printed
     */
    public static void outputBox(String msg) {
        Window.alert(msg);
    }
    
    /**
     * opens a popup and prints the given message
     * 
     * @param msg the message, that should be printed
     */
    public static native String md5(String msg) /*-{
      return $wnd.MD5(msg); // $wnd is a JSNI synonym for 'window'
    }-*/;

    /**
     * prints the given message in the status bar
     *
     * @param msg the message, that should be printed
     */
    public void output(String msg) {
//        if (statusBar != null) {
//            statusBar.setStatusMsg(msg);
//        }
    };


    /**
     * @param dockPanel
     * @return the widget from the given dockPanel, that is on the center position
     */
    private Widget getCenterPanel() {
        for (int i = 0; i < outer.getWidgetCount(); ++i) {
            Widget w = outer.getWidget(i);

            if (outer.getWidgetDirection(w).equals(DockPanel.CENTER)) {
                return w;
            }
        }

        return null;
    }


    /**
     * Adds a new ResizeHandler. This handler will be invoked by changes of the browser window size
     *
     * @param handler
     */
    @Override
    public HandlerRegistration addResizeHandler(ResizeHandler handler) {
        return handlerManager.addHandler(ResizeEvent.getType(), handler);
    }

    /**
     * fires the resize event to all registrated handler
     * @param event
     */
    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }


    /**
     * This method will be called automatically by resizing the browser window
     *
     * @param event
     */
    @Override
    public void onResize(ResizeEvent event) {
        resize(event.getWidth(), event.getHeight());
        validateSize();
    }

    /**o
     * resizes the main window
     * @param width
     * @param height
     */
    private void resize(int width, int height) {
        // Adjust the menu bar panel and detail area to take up the available room
        // in the window.
        Widget centerPanel = getCenterPanel();
        if (centerPanel != null) {
            int panelHeight = height - centerPanel.getAbsoluteTop() - 80;
            if (panelHeight < 1) {
                panelHeight = 1;
            }
            centerPanel.setHeight(panelHeight + "px");
        }

        int outerWidth = width - 20;
        outer.setWidth(outerWidth + "px");
        

        RootPanel p = RootPanel.get("contentId");
        int newHeight = height - p.getAbsoluteTop() - 105;
        if (newHeight < 150) {
            newHeight = 150;
        }
        p.setHeight((newHeight) + "px");

    }


    /**
     * This will be invoked from the sign out button within the TopPanel and this method
     * signs out the current user.
     * @param event
     */
    @Override
    public void onClick(ClickEvent event) {
//        // if the following condition is false, the user is already logged out and nothing should happen.
//        if (staff != null) {
//            topPanel.deactivateUserArea();
//            showNewCenterPanel(new LoginView());
//            staff = null;
//            admin = false;
//            menuBar.showButtons(false);
//            menuBarBottom.showButtons(false);
//
//            //send logout request to the server
//            BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>();
//
//            ProjectTrackerEntryPoint.getProjectService(true).logout(callback);
//        }
    }

    public void logout() {
        topPanel.setLoggedIn(false, "");
        RootPanel.get("contentId").remove(sheets);
        topPanel.removeMenuListener(sheets);
        topPanel.removeUserList();
        windowResize.removeHandler();
        resize.removeHandler();
        sheets = new SheetsPanel();
        loggedInStaff = null;
        windowResize = Window.addResizeHandler(sheets);
        resize = addResizeHandler(sheets);
    }
    

    @Override
    public void onValueChange(ValueChangeEvent event) {
        changePage(History.getToken());
    }
    
    private void changePage(String token) {
        RootPanel pan = RootPanel.get(activePanel + "Id");
        if (pan != null) {
            pan.setStyleName("inactive");
        }
        
        pan = RootPanel.get(token + "Id");
        if (pan != null) {
            pan.setStyleName("active");
        }
        outputBox(token);
    }

    /**
     * @return the projects
     */
    public List<ProjectDTO> getProjects() {
        return projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }
}
