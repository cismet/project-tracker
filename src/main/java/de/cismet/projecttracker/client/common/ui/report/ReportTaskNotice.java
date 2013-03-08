/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.report;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 *
 * @author daniel
 */
public class ReportTaskNotice extends Composite {

    private FlowPanel mainPanel = new FlowPanel();
    private ActivityDTO activity;
    private Image gravatar;

    public ReportTaskNotice(ActivityDTO activity, Image gravatar) {
        this.activity = activity;
        this.gravatar = gravatar; 
        initWidget(mainPanel);
        init();
    }

    protected String getTextFromActivity() {
        if (activity.getKindofactivity() != 0) {
            String text = "";
            if (activity.getKindofactivity() == 1) {
                //begin of day
                text = "Begin of Work Activity";
                text += "<br/> at <br/>";
                text += DateHelper.formatDateTime(activity.getDay());

            } else if (activity.getKindofactivity() == 2) {
                //end of day
                text = "End of Work Activity";
                text += "<br/> at <br/>";
                text += DateHelper.formatDateTime(activity.getDay());
            } else {
                //lock
                text = "Locked Day";
                text += "<br/> at <br/>";
                text += DateHelper.formatDate(activity.getDay());
            }
            return text;
        } else {
            String desc = getDesccription(activity.getDescription());
            StringBuilder text = new StringBuilder();

            if (activity.getWorkCategory() != null && activity.getWorkCategory().getId() == WorkCategoryDTO.TRAVEL) {
                text.append("Travel: ").append(activity.getWorkPackage().getAbbreviation());
            } else {
                text.append(activity.getWorkPackage().getAbbreviation());
            }

            double hours = Math.round(activity.getWorkinghours() * 100) / 100.0;
            text.append("<br />").append(desc);
            text.append("<br/> at <br/>");
            text.append(DateHelper.formatDate(activity.getDay()));
            if (hours != 0.0) {
                text.append("<br />").append(DateHelper.doubleToHours(hours)).append(" hours");
            }

            return text.toString();
        }
    }

    protected String getDesccription(String desc) {
        if (desc == null) {
            return "";
        }
        int i = desc.indexOf("(@");

        if (i != -1) {
            String result = desc.substring(0, i);
            return result;
        } else {
            return desc;
        }
    }

    protected String getTooltipTextFromActivity() {
        if (activity.getKindofactivity() == 0) {
            StringBuilder text = new StringBuilder(activity.getWorkPackage().getProject().getName());
            text.append("\n").append(activity.getWorkPackage().getName());

            if (activity.getDescription() != null) {
                text.append("\n").append(activity.getDescription());
            }

            return text.toString();
        } else {
            return "";
        }
    }

    private void init() {
        final HorizontalPanel hrPnl = new HorizontalPanel();
        final VerticalPanel v1 = new VerticalPanel();
        v1.add(gravatar);

        final VerticalPanel v2 = new VerticalPanel();
        final Label workPackage = new Label();
        final WorkPackageDTO wp = activity.getWorkPackage();
        if (wp != null) {
            workPackage.setText(wp.getDescription());
        }
        v2.add(workPackage);
        
        final Label desc = new Label();
        desc.setText(activity.getDescription());
        v2.add(desc);
        
        final VerticalPanel v3 = new VerticalPanel();
        final Label dateLbl = new Label();
        dateLbl.setText(DateHelper.formatDate(activity.getDay()));
        v3.add(dateLbl);
        final Label hourLbl = new Label();
        hourLbl.setText(DateHelper.doubleToHours(activity.getWorkinghours()));
        v3.add(hourLbl);
        
        hrPnl.add(v1);
        hrPnl.add(v2);
        hrPnl.add(v3);
        
        mainPanel.add(hrPnl);
    }
}
