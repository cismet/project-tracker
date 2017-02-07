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
package de.cismet.projecttracker.client.common.ui.report;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ReportTaskNotice extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static final ReportTaskNoticeUiBinder uiBinder = GWT.create(ReportTaskNoticeUiBinder.class);
    private static final String MAIN_STYLES = "alert alert-block timebox";
    private static final String[] ADDITIONAL_PROJECT_STYLES = {
            "proj1",
            "proj2",
            "proj3",
            "proj4",
            "proj5",
            "proj6",
            "proj7"
        };
    private static final HashMap<Long, Integer> projectStyle = new HashMap<Long, Integer>();

    static {
        projectStyle.put((long)1, 0);
        projectStyle.put((long)2, 1);
        projectStyle.put((long)18, 2);
        projectStyle.put((long)21, 3);
        projectStyle.put((long)20, 4);
        projectStyle.put((long)10, 5);
    }

    //~ Instance fields --------------------------------------------------------

    @UiField
    HTMLPanel imagePanel;
    @UiField
    HTMLPanel descrPanel;
    @UiField
    HTMLPanel datePanel;
    @UiField
    HTMLPanel hoursPanel;
    @UiField
    HTMLPanel containerPanel;
    private ActivityDTO activity;
    private Image gravatar = new Image();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportTaskNotice object.
     *
     * @param  activity  DOCUMENT ME!
     */
    public ReportTaskNotice(final ActivityDTO activity) {
        initWidget(uiBinder.createAndBindUi(this));
        this.activity = activity;
        if ((activity != null) && (activity.getStaff() != null) && (activity.getStaff().getEmail() != null)) {
            this.gravatar.setUrl(ProjectTrackerEntryPoint.GRAVATAR_URL_PREFIX
                        + ProjectTrackerEntryPoint.getInstance().md5(activity.getStaff().getEmail())
                        + "?s=32");
            imagePanel.add(gravatar);
        }
        final Label workPackage = new Label();
        final WorkPackageDTO wp = activity.getWorkPackage();
        if (wp != null) {
            workPackage.setText(wp.getName());
        }
        descrPanel.add(workPackage);

        final Label desc = new Label();
        final String descr = (activity.getDescription() == null) ? " " : activity.getDescription();
        desc.setText(descr);
        descrPanel.add(desc);

        datePanel.add(new Label(DateHelper.formatDate(activity.getDay())));
        datePanel.add(new Label(DateHelper.doubleToHours(activity.getWorkinghours()) + " h"));

        setColour();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void setColour() {
        if ((activity.getWorkPackage() != null) && (activity.getWorkPackage().getProject() != null)) {
            final List<ProjectDTO> proj = ProjectTrackerEntryPoint.getInstance().getProjects();
            if (proj != null) {
                final long projectId = activity.getWorkPackage().getProject().getId();
                Integer index = projectStyle.get(projectId);

                if (index == null) {
                    index = 6;
                }

                if ((index < 0) || (index >= ADDITIONAL_PROJECT_STYLES.length)) {
                    index = ADDITIONAL_PROJECT_STYLES.length - 1;
                }

                containerPanel.setStyleName(MAIN_STYLES + " " + ADDITIONAL_PROJECT_STYLES[index]);
            } else {
                final Timer t = new Timer() {

                        @Override
                        public void run() {
                            setColour();
                        }
                    };

                t.schedule(2000);
            }
        }
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface ReportTaskNoticeUiBinder extends UiBinder<Widget, ReportTaskNotice> {
    }
}
