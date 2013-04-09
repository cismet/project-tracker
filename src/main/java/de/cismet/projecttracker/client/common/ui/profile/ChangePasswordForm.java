/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.profile;

import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 *
 * @author dmeiers
 */
public class ChangePasswordForm extends Composite implements ClickHandler {

    private static ChangePasswordFormUiBinder uiBinder = GWT.create(ChangePasswordFormUiBinder.class);
    @UiField
    SubmitButton submitBtn;
    @UiField
    PasswordTextBox pwBox;
    @UiField
    PasswordTextBox pwConfirmBox;
//    @UiField
//    PasswordTextBox oldPw;

    interface ChangePasswordFormUiBinder extends UiBinder<Widget, ChangePasswordForm> {
    }

    public ChangePasswordForm() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("submitBtn")
    @Override
    public void onClick(ClickEvent event) {

        if (pwBox.getText().equals(pwConfirmBox.getText())) {
            BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>() {

                @Override
                protected void afterExecution(Void result, boolean operationFailed) {
                    if (!operationFailed) {
                        ProjectTrackerEntryPoint.outputBox("Password succesfully changed");
//                        oldPw.setText("");
                        pwBox.setText("");
                        pwConfirmBox.setText("");
                    }
                }
            };
            ProjectTrackerEntryPoint.getProjectService(true).changePassword(ProjectTrackerEntryPoint.getInstance().getStaff(), pwBox.getText(), callback);
        } else {
            ProjectTrackerEntryPoint.outputBox("the confirm password doesn't match the new password");
        }
    }
}
