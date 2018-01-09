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
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import de.cismet.projecttracker.client.ImageConstants;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class LoginPanel extends Composite implements ClickHandler, KeyUpHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static LoginPanel.LoginPanelUiBinder uiBinder = GWT.create(LoginPanel.LoginPanelUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    FlowPanel loginPanel;
    private TextBox username = new TextBox();
    private PasswordTextBox password = new PasswordTextBox();
    private Button login = new Button("Sign in");
    private Button logout = new Button("Sign out");
    private Label userDataLab = new Label("");
    private Image gravatar = new Image();
    private Image slackLink = new Image(ImageConstants.INSTANCE.slackLogo());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LoginPanel object.
     */
    public LoginPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        init();
//        final NavLink link1 = new NavLink("Foo");
//        add(link1);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void onClick(final ClickEvent event) {
        if (event.getSource() == login) {
            login();
        } else if (event.getSource() == logout) {
            final BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>();
            ProjectTrackerEntryPoint.getProjectService(true).logout(callback);
            ProjectTrackerEntryPoint.getInstance().logout();
        }
    }

    @Override
    public void onKeyUp(final KeyUpEvent event) {
        if ((event.getNativeKeyCode() == KeyCodes.KEY_ENTER) && !ProjectTrackerEntryPoint.getInstance().isLoggedIn()) {
            login();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void init() {
        loginPanel.add(username);
        loginPanel.add(password);
        loginPanel.add(login);
        username.setStyleName("input-small white");
        password.setStyleName("input-small");
        password.addKeyUpHandler(this);
        login.setStyleName("btn");
        logout.setStyleName("btn");
        logout.addClickHandler(this);
        login.addKeyUpHandler(this);
        login.addClickHandler(this);
        userDataLab.setStyleName("user-data");
        gravatar.setStyleName("pull-left gravatar-image");
        slackLink.setStyleName("image-link pull-left gravatar-image");
        slackLink.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(final ClickEvent event) {
                    ProjectTrackerEntryPoint.openSlackDiaryLink("https://cismet.github.io/slackdiary/slackdiary.html");
                }
            });
    }

    /**
     * executes a login.
     */
    private void login() {
        final BasicAsyncCallback<StaffDTO> callback = new BasicAsyncCallback<StaffDTO>() {

                @Override
                protected void afterExecution(final StaffDTO result, final boolean operationFailed) {
                    if (!operationFailed) {
                        ProjectTrackerEntryPoint.getInstance()
                                .login(
                                    result,
                                    (result.getPermissions() & ProjectTrackerEntryPoint.ADMIN_PERMISSION)
                                    == ProjectTrackerEntryPoint.ADMIN_PERMISSION);
                        ProjectTrackerEntryPoint.getInstance().checkBeginOfDayBooking();
                        username.setText("");
                        password.setText("");
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).login(username.getText(), password.getText(), callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  loggedIn  DOCUMENT ME!
     * @param  user      DOCUMENT ME!
     */
    public void setLoggedIn(final boolean loggedIn, final String user) {
        if (loggedIn) {
            userDataLab.setText(user);
            loginPanel.clear();
            loginPanel.add(slackLink);
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
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    public void setGravatar(final String url) {
        gravatar.setUrl(url);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    public void setSlack(final String url) {
        slackLink.setUrl(url);
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface LoginPanelUiBinder extends UiBinder<Widget, LoginPanel> {
    }
}
