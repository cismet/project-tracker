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
package de.cismet.projecttracker.client.common.ui.event;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class MenuEvent {

    //~ Instance fields --------------------------------------------------------

    private Object source;
    private int number;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  source  the source to set
     */
    public void setSource(final Object source) {
        this.source = source;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  number  the number to set
     */
    public void setNumber(final int number) {
        this.number = number;
    }
}
