/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.profile;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
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
import de.cismet.projecttracker.client.dto.ProfileDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 *
 * @author dmeiers
 */
public class AccountSettingsForm extends Composite implements ClickHandler {

    private static AccountSettingsFormUiBinder uiBinder = GWT.create(AccountSettingsFormUiBinder.class);
    private ProfileDTO profile;
    @UiField
    CheckBox autoPauseCBO;
    @UiField
    RadioButton weekLockRB;
    @UiField
    RadioButton dayLockRB;
    @UiField
    SubmitButton submitBtn;

    interface AccountSettingsFormUiBinder extends UiBinder<Widget, AccountSettingsForm> {
    }

    public AccountSettingsForm() {
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    private void init() {
        final ProfileDTO p = ProjectTrackerEntryPoint.getInstance().getStaff().getProfile();

        if (p != null) {
            profile = p;
        } else {
            profile = new ProfileDTO(false, false);
        }

        autoPauseCBO.setValue(profile.getAutoPauseEnabled());
        if (profile.getWeekLockMode()) {
            weekLockRB.setValue(true);
            dayLockRB.setValue(false);
        } else {
            weekLockRB.setValue(false);
            dayLockRB.setValue(true);
        }
    }

    @Override
    @UiHandler("submitBtn")
    public void onClick(ClickEvent event) {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        profile.setAutoPauseEnabled(autoPauseCBO.getValue());
        if(weekLockRB.getValue()){
            profile.setWeekLockMode(true);
        }else{
            profile.setWeekLockMode(false);
        }
        staff.setProfile(profile);
        
        BasicAsyncCallback<StaffDTO> callback = new BasicAsyncCallback<StaffDTO>(){

            @Override
            protected void afterExecution(StaffDTO result, boolean operationFailed) {
                if(!operationFailed){
                    ProjectTrackerEntryPoint.getInstance().setStaff(result);
                    ProjectTrackerEntryPoint.outputBox("Profile succesfully updated!");
                }
            }
            
        };
        
        ProjectTrackerEntryPoint.getProjectService(true).saveStaff(staff, callback);
    }
}
