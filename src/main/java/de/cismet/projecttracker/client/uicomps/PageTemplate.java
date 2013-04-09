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
package de.cismet.projecttracker.client.uicomps;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class PageTemplate extends Composite implements ResizeHandler {

    //~ Instance fields --------------------------------------------------------

    private FlowPanel mainPanel = new FlowPanel();
//    private FlowPanel containerPanel = new FlowPanel();
    private FlowPanel pageHeaderPanel = new FlowPanel();
    private FlowPanel contentNodeParentPanel = new FlowPanel();
    private FlowPanel contentNodePanel = new FlowPanel();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PageTemplate object.
     */
    public PageTemplate() {
        init();
        initWidget(mainPanel);
        setStyleName("content");
//        setStyleName("container");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
//        containerPanel.setStyleName("content");
        pageHeaderPanel.setStyleName("page-header");
        contentNodeParentPanel.setStyleName("span14");
        contentNodePanel.addStyleName("pre");
        contentNodePanel.addStyleName("prettyprint");
        contentNodePanel.addStyleName("contentNode");
        contentNodeParentPanel.add(contentNodePanel);
//        containerPanel.add(pageHeaderPanel);
//        mainPanel.add(containerPanel);
        mainPanel.add(pageHeaderPanel);
        mainPanel.add(contentNodeParentPanel);
    }

    @Override
    public void onResize(final ResizeEvent event) {
        final int newHeight = event.getHeight() - contentNodePanel.getAbsoluteTop() - 150;
        contentNodePanel.setHeight(newHeight + "px");
    }
}
