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
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class ChangePasswordForm extends Composite implements ClickHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static ChangePasswordFormUiBinder uiBinder = GWT.create(ChangePasswordFormUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    SubmitButton submitBtn;
    @UiField
    PasswordTextBox pwBox;
    @UiField
    PasswordTextBox pwConfirmBox;
//    @UiField
//    PasswordTextBox oldPw;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ChangePasswordForm object.
     */
    public ChangePasswordForm() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    //~ Methods ----------------------------------------------------------------

    @UiHandler("submitBtn")
    @Override
    public void onClick(final ClickEvent event) {
        if (pwBox.getText().equals(pwConfirmBox.getText())) {
            final BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>() {

                    @Override
                    protected void afterExecution(final Void result, final boolean operationFailed) {
                        if (!operationFailed) {
                            ProjectTrackerEntryPoint.outputBox("Password succesfully changed");
//                        oldPw.setText("");
                            pwBox.setText("");
                            pwConfirmBox.setText("");
                        }
                    }
                };
            ProjectTrackerEntryPoint.getProjectService(true)
                    .changePassword(ProjectTrackerEntryPoint.getInstance().getStaff(), pwBox.getText(), callback);
        } else {
            ProjectTrackerEntryPoint.outputBox("the confirm password doesn't match the new password");
        }
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface ChangePasswordFormUiBinder extends UiBinder<Widget, ChangePasswordForm> {
    }
}
