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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.util.Date;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.ServerDataChangeListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.uicomps.SheetsPanel;
import de.cismet.projecttracker.client.utilities.ChangeChecker;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class BeginOfWorkDialog extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static BeginOfWorkUiBinder uiBinder = GWT.create(BeginOfWorkUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    Button closeButton;
    @UiField
    Button okButton;
    private DialogBox form;
    private ActivityDTO newActivity;
    private SheetsPanel sheets;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BeginOfWorkDialog object.
     *
     * @param  form    DOCUMENT ME!
     * @param  sheets  DOCUMENT ME!
     */
    public BeginOfWorkDialog(final DialogBox form, final SheetsPanel sheets) {
        this.form = form;
        this.sheets = sheets;
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        okButton.setText("Ok");
        okButton.setFocus(true);
        closeButton.setText("Cancel");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("okButton")
    void onOkButtonClick(final ClickEvent event) {
        newActivity = new ActivityDTO();
        newActivity.setKindofactivity(ActivityDTO.BEGIN_OF_DAY);
        final Date d = new Date();
        newActivity.setDay(new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(), d.getMinutes()));
        newActivity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
        final BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>() {

                @Override
                protected void afterExecution(final Long result, final boolean operationFailed) {
                    if (!operationFailed) {
                        newActivity.setId(result);
                        sheets.refresh();
                        form.hide();
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).createActivity(newActivity, callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("closeButton")
    void onCloseButtonClick(final ClickEvent event) {
        form.hide();
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface BeginOfWorkUiBinder extends UiBinder<Widget, BeginOfWorkDialog> {
    }
}
