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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.HashMap;

import de.cismet.projecttracker.client.dto.ActivityDTO;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class FillButtonPreview extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static FillButtonPreview.FillButtonPreviewUiBinder uiBinder = GWT.create(
            FillButtonPreview.FillButtonPreviewUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    FlowPanelWithSpacer prevPanel;
    @UiField
    FlowPanelWithSpacer realPanel;
    @UiField
    Button closeButton;
    @UiField
    Button okButton;
    @UiField
    Label before;
    @UiField
    Label after;
    @UiField
    HorizontalPanel outerPanel;
    private DialogBox form;
    private HashMap<Long, TaskNotice> previewTasks;
    private ArrayList<TaskNotice> realTasks;
    private TaskStory taskStory;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FillButtonPreview object.
     *
     * @param  form                 DOCUMENT ME!
     * @param  negativeFillPreview  DOCUMENT ME!
     * @param  real                 DOCUMENT ME!
     * @param  ts                   DOCUMENT ME!
     */
    FillButtonPreview(final DialogBox form,
            final ArrayList<TaskNotice> negativeFillPreview,
            final ArrayList<TaskNotice> real,
            final TaskStory ts) {
        this.form = form;
        initWidget(uiBinder.createAndBindUi(this));
        realTasks = real;
        before.setStyleName("TimeHeader");
        before.setText("Before");
        after.setStyleName("TimeHeader");
        after.setText("After");
        outerPanel.setStyleName("fillPreviewHorizontalPanel", true);
        // reduce the heigth of the spacer
        final Widget prevPanelSpacer = prevPanel.getWidget(0);
        prevPanelSpacer.setHeight("5px");
        final Widget realPanelspacer = realPanel.getWidget(0);
        realPanelspacer.setHeight("5px");

        previewTasks = new HashMap<Long, TaskNotice>();
        this.taskStory = ts;
        for (final TaskNotice tn : negativeFillPreview) {
            prevPanel.add(tn);
            previewTasks.put(tn.getActivity().getId(), tn);
        }
        for (final TaskNotice tn : realTasks) {
            realPanel.add(new TaskNotice(tn.getActivity().createCopy()));
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("okButton")
    void onOkButtonClick(final ClickEvent event) {
        for (final TaskNotice tn : realTasks) {
            final ActivityDTO realAct = tn.getActivity();
            final double newWhow = previewTasks.get(realAct.getId()).getActivity().getWorkinghours();
            realAct.setWorkinghours(newWhow);
            tn.refresh();
            tn.save();
            taskStory.taskChanged(tn);
        }
        form.hide();
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
    interface FillButtonPreviewUiBinder extends UiBinder<Widget, FillButtonPreview> {
    }
}
