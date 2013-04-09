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
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 *
 * @author dmeiers
 */
public class LoginPanel extends Composite implements ClickHandler, KeyUpHandler {

    private static LoginPanel.LoginPanelUiBinder uiBinder = GWT.create(LoginPanel.LoginPanelUiBinder.class);
    private TextBox username = new TextBox();
    private PasswordTextBox password = new PasswordTextBox();
    private Button login = new Button("Sign in");
    private Button logout = new Button("Sign out");
    private Label userDataLab = new Label("");
    private Image gravatar = new Image();
    @UiField
    FlowPanel loginPanel;

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == login) {
            login();
        } else if (event.getSource() == logout) {
            BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>();
            ProjectTrackerEntryPoint.getProjectService(true).logout(callback);
            ProjectTrackerEntryPoint.getInstance().logout();
        }
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ProjectTrackerEntryPoint.getInstance().isLoggedIn()) {
            login();
        }
    }

    interface LoginPanelUiBinder extends UiBinder<Widget, LoginPanel> {
    }

    public LoginPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        init();
//        final NavLink link1 = new NavLink("Foo");
//        add(link1);
    }

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
    }

    /**
     * executes a login
     */
    private void login() {
        BasicAsyncCallback<StaffDTO> callback = new BasicAsyncCallback<StaffDTO>() {

            @Override
            protected void afterExecution(StaffDTO result, boolean operationFailed) {
                if (!operationFailed) {
                    ProjectTrackerEntryPoint.getInstance().login(result, (result.getPermissions() & 0x1) == 0x1);
                    username.setText("");
                    password.setText("");
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).login(username.getText(), password.getText(), callback);
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

    public void setGravatar(String url) {
        gravatar.setUrl(url);
    }
}
