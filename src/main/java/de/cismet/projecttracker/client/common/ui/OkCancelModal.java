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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class OkCancelModal extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static OkCancelModalUiBinder uiBinder = GWT.create(
            OkCancelModal.OkCancelModalUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    Button closeButton;
    @UiField
    Button okButton;
    @UiField
    Label lblMessage;
    @UiField
    Label lblTitle;

    private OkCancelCallback callback;
    private DialogBox form;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new OkCancelModal object.
     *
     * @param  form     DOCUMENT ME!
     * @param  cb       DOCUMENT ME!
     * @param  title    DOCUMENT ME!
     * @param  message  DOCUMENT ME!
     */
    public OkCancelModal(final DialogBox form, final OkCancelCallback cb, final String title, final String message) {
        this.form = form;
        initWidget(uiBinder.createAndBindUi(this));
        this.callback = cb;
        lblMessage.setText(message);
        lblTitle.setText(title);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("okButton")
    void onOkButtonClick(final ClickEvent event) {
        form.hide();
        callback.onOkClicked();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("closeButton")
    void onCloseButtonClick(final ClickEvent event) {
        form.hide();
        callback.onCancelClicked();
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface OkCancelModalUiBinder extends UiBinder<Widget, OkCancelModal> {
    }
}
