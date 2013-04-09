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
import de.cismet.projecttracker.client.dto.ActivityDTO;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author dmeiers
 */
public class FillButtonPreview extends Composite {

    private static FillButtonPreview.FillButtonPreviewUiBinder uiBinder = GWT.create(FillButtonPreview.FillButtonPreviewUiBinder.class);
    private DialogBox form;
    private HashMap<Long,TaskNotice> previewTasks;
    private ArrayList<TaskNotice> realTasks;
    private TaskStory taskStory;
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

    FillButtonPreview(DialogBox form, ArrayList<TaskNotice> negativeFillPreview, ArrayList<TaskNotice> real, TaskStory ts) {
        this.form = form;
        initWidget(uiBinder.createAndBindUi(this));
        realTasks = real;
        before.setStyleName("TimeHeader");
        before.setText("Before");
        after.setStyleName("TimeHeader");
        after.setText("After");
        outerPanel.setStyleName("fillPreviewHorizontalPanel",true);
        //reduce the heigth of the spacer
        final Widget prevPanelSpacer = prevPanel.getWidget(0);
        prevPanelSpacer.setHeight("5px");
        final Widget realPanelspacer = realPanel.getWidget(0);
        realPanelspacer.setHeight("5px");
        
        previewTasks = new HashMap<Long, TaskNotice>();
        this.taskStory = ts;
        for (TaskNotice tn : negativeFillPreview) {
            prevPanel.add(tn);
            previewTasks.put(tn.getActivity().getId(), tn);
        }
        for (TaskNotice tn : realTasks) {
            realPanel.add(new TaskNotice(tn.getActivity().createCopy()));
        }
    }

    interface FillButtonPreviewUiBinder extends UiBinder<Widget, FillButtonPreview> {
    }

    @UiHandler("okButton")
    void onOkButtonClick(ClickEvent event) {
        for (TaskNotice tn : realTasks) {
            final ActivityDTO realAct = tn.getActivity();
            final double newWhow = previewTasks.get(realAct.getId()).getActivity().getWorkinghours();
            realAct.setWorkinghours(newWhow);
            tn.refresh();
            tn.save();
            taskStory.taskChanged(tn);
        }
        form.hide();
    }

    @UiHandler("closeButton")
    void onCloseButtonClick(ClickEvent event) {
        form.hide();
    }
}
