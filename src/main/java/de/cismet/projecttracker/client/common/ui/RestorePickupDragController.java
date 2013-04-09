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

import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class RestorePickupDragController extends com.allen_sauer.gwt.dnd.client.PickupDragController {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RestorePickupDragController object.
     *
     * @param  boundaryPanel                 DOCUMENT ME!
     * @param  allowDroppingOnBoundaryPanel  DOCUMENT ME!
     */
    public RestorePickupDragController(final AbsolutePanel boundaryPanel, final boolean allowDroppingOnBoundaryPanel) {
        super(boundaryPanel, allowDroppingOnBoundaryPanel);
        setBehaviorDragStartSensitivity(5);
        setBehaviorDragProxy(true);
    }
}
;
