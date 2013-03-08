/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 *
 * @author therter
 */
public class RestorePickupDragController extends com.allen_sauer.gwt.dnd.client.PickupDragController {

    public RestorePickupDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
        super(boundaryPanel, allowDroppingOnBoundaryPanel);
        setBehaviorDragStartSensitivity(5);
        setBehaviorDragProxy(true);
    }
};

