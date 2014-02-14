/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.report;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import com.sun.org.apache.xerces.internal.dom.DeferredCommentImpl;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class LargeReportResultAlert extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static LargeReportResultAlertUiBinder uiBinder = GWT.create(LargeReportResultAlertUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    Button okButton;
    @UiField
    Button cancelButton;
    @UiField
    AlertBlock alert;
    private ReportResultPanel resPan;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LargeReportResultAlert object.
     */
    public LargeReportResultAlert() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Creates a new LargeReportResultAlert object.
     *
     * @param  p  DOCUMENT ME!
     */
    public LargeReportResultAlert(final ReportResultPanel p) {
        initWidget(uiBinder.createAndBindUi(this));
        resPan = p;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("cancelButton")
    void onCancelButtonClick(final ClickEvent event) {
        alert.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("okButton")
    void onOkButtonClick(final ClickEvent event) {
        if (resPan != null) {
            Scheduler.get().scheduleDeferred(new Command() {

                    @Override
                    public void execute() {
                        resPan.proceedWithLastSearchResult();
                    }
                });
        }
        alert.close();
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface LargeReportResultAlertUiBinder extends UiBinder<Widget, LargeReportResultAlert> {
    }
}
