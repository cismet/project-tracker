/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.ServerDataChangeListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.uicomps.SheetsPanel;
import de.cismet.projecttracker.client.utilities.ChangeChecker;
import java.util.Date;

/**
 *
 * @author dmeiers
 */
public class BeginOfWorkDialog extends Composite {

    private static BeginOfWorkUiBinder uiBinder = GWT.create(BeginOfWorkUiBinder.class);
    private DialogBox form;
    private ActivityDTO newActivity;
    private SheetsPanel sheets;
    @UiField
    Button closeButton;
    @UiField
    Button okButton;


    interface BeginOfWorkUiBinder extends UiBinder<Widget, BeginOfWorkDialog> {
    }

    public BeginOfWorkDialog(DialogBox form, SheetsPanel sheets) {
        this.form = form;
        this.sheets = sheets;
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    private void init() {
        okButton.setText("Ok");
        okButton.setFocus(true);
        closeButton.setText("Cancel");
    }

    @UiHandler("okButton")
    void onOkButtonClick(ClickEvent event) {
        newActivity = new ActivityDTO();
        newActivity.setKindofactivity(ActivityDTO.BEGIN_OF_DAY);
        Date d = new Date();
        newActivity.setDay(new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(),d.getMinutes()));
        newActivity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
        BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>() {

            @Override
            protected void afterExecution(Long result, boolean operationFailed) {
                if (!operationFailed) {
                    newActivity.setId(result);
                    sheets.refresh();
                    form.hide();
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).createActivity(newActivity, callback);

    }

    @UiHandler("closeButton")
    void onCloseButtonClick(ClickEvent event) {
        form.hide();
    }

}
