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

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Legend;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ProfileDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class AccountSettingsForm extends Composite implements ClickHandler, ValueChangeHandler<Boolean> {

    //~ Static fields/initializers ---------------------------------------------

    private static AccountSettingsFormUiBinder uiBinder = GWT.create(AccountSettingsFormUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    CheckBox autoPauseCBO;
    @UiField
    CheckBox weekLockCB;
    @UiField
    CheckBox dayLockCB;
    @UiField
    SubmitButton submitBtn;
    @UiField
    TextBox pauseDuration;
    @UiField
    Legend residualVacLegend;
    @UiField
    ControlGroup residualVacationGroup;
    @UiField
    TextBox residualVacation;
    private ProfileDTO profile;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AccountSettingsForm object.
     */
    public AccountSettingsForm() {
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    //~ Methods ----------------------------------------------------------------

    @UiHandler("autoPauseCBO")
    @Override
    public void onValueChange(final ValueChangeEvent<Boolean> event) {
        pauseDuration.setEnabled(event.getValue());
    }

    /**
     * DOCUMENT ME!
     */
    private void init() {
        final ProfileDTO p = ProjectTrackerEntryPoint.getInstance().getStaff().getProfile();

        if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
            residualVacLegend.addStyleName("noDisplay");
            residualVacationGroup.addStyleName("noDisplay");
        }

        if (p != null) {
            profile = p;
        } else {
            profile = new ProfileDTO(false, true, true, 0, 0);
        }

        autoPauseCBO.setValue(profile.getAutoPauseEnabled());
        pauseDuration.setEnabled(profile.getAutoPauseEnabled());
        if (profile.getWeekLockModeEnabled()) {
            weekLockCB.setValue(true);
        } else {
            weekLockCB.setValue(false);
        }

        if (profile.getDayLockModeEnabled()) {
            dayLockCB.setValue(true);
        } else {
            dayLockCB.setValue(false);
        }
        pauseDuration.setText(DateHelper.doubleToHours(profile.getAutoPauseDuration()));

        residualVacation.setText("" + profile.getResidualVacation());
    }

    @Override
    @UiHandler("submitBtn")
    public void onClick(final ClickEvent event) {
        if (!weekLockCB.getValue() && !dayLockCB.getValue()) {
            ProjectTrackerEntryPoint.outputBox("You have to select at least one lock option");
            return;
        }
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        profile.setAutoPauseEnabled(autoPauseCBO.getValue());
        profile.setWeekLockModeEnabled(weekLockCB.getValue());
        profile.setDayLockModeEnabled(dayLockCB.getValue());
        profile.setAutoPauseDuration(DateHelper.hoursToDouble(pauseDuration.getText()));
        if (ProjectTrackerEntryPoint.getInstance().isAdmin()) {
            profile.setResidualVacation(Double.valueOf(residualVacation.getText()));
        }
        staff.setProfile(profile);

        final BasicAsyncCallback<StaffDTO> callback = new BasicAsyncCallback<StaffDTO>() {

                @Override
                protected void afterExecution(final StaffDTO result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (result.getId() == ProjectTrackerEntryPoint.getInstance().getLoggedInStaff().getId()) {
                            ProjectTrackerEntryPoint.getInstance().setLoggedInStaff(result);
                        }
                        ProjectTrackerEntryPoint.getInstance().setStaff(result);
                        ProjectTrackerEntryPoint.outputBox("Profile succesfully updated!");
                    }
                }
            };

        ProjectTrackerEntryPoint.getProjectService(true).saveStaff(staff, callback);
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface AccountSettingsFormUiBinder extends UiBinder<Widget, AccountSettingsForm> {
    }
}
