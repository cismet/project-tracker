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
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.cismet.projecttracker.client.common.ui.report.LargeReportResultAlert;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class LoadingSpinner extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static LoadingSpinnerUiBinder uiBinder = GWT.create(LoadingSpinnerUiBinder.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LoadingSpinner object.
     */
    public LoadingSpinner() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface LoadingSpinnerUiBinder extends UiBinder<Widget, LoadingSpinner> {
    }
}
