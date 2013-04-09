/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.listener;

import de.cismet.projecttracker.client.dto.BasicDTO;

/**
 * This callback class wraps an object and if the async call fails, the origin state of the wrapped object will be
 * restored.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public abstract class BasicRollbackCallback<E extends BasicDTO<E>> extends BasicAsyncCallback<E> {

    //~ Instance fields --------------------------------------------------------

    protected E copy;
    protected E original;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BasicRollbackCallback object.
     *
     * @param  object  DOCUMENT ME!
     */
    public BasicRollbackCallback(final E object) {
        copy = object.createCopy();
        original = object;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public final void onFailure(final Throwable caught) {
        original.reset(copy);
        super.onFailure(caught);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  a copy of the object that was given by the constructor
     */
    public E getCopiedObject() {
        return copy;
    }
}
