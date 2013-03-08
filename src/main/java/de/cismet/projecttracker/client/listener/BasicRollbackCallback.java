package de.cismet.projecttracker.client.listener;

import de.cismet.projecttracker.client.dto.BasicDTO;


/**
 * This callback class wraps an object and if the async call fails, the origin
 * state of the wrapped object will be restored.
 *
 * @author therter
 */
public abstract class BasicRollbackCallback<E extends BasicDTO<E>> extends BasicAsyncCallback<E> {
    protected E copy;
    protected E original;

    public BasicRollbackCallback(E object) {
        copy = object.createCopy();
        original = object;
    }


    @Override
    public final void onFailure(Throwable caught) {
        original.reset(copy);
        super.onFailure(caught);
    }

    /**
     * @return a copy of the object that was given by the constructor
     */
    public E getCopiedObject() {
        return copy;
    }
}
