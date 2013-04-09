/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;

import de.cismet.projecttracker.client.MessageConstants;

/**
 * This is the basic class for for all Data Transfer Object. Data transfer objects are used to transfer hibernate
 * database entities from the server to the client and vice versa. <a
 * href="http://code.google.com/webtoolkit/articles/using_gwt_with_hibernate.html">Article about hibernate and DTOs</a>
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public abstract class BasicDTO<T extends BasicDTO> implements IsSerializable {

    //~ Static fields/initializers ---------------------------------------------

    private static MessageConstants MESSAGES_INSTANCE = null;

    //~ Instance fields --------------------------------------------------------

    protected long id;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the id
     */
    public long getId() {
        return id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  id  the id to set
     */
    public void setId(final long id) {
        this.id = id;
    }

    /**
     * creates a shallow copy of the object.
     *
     * @return  DOCUMENT ME!
     */
    public abstract T createCopy();

    /**
     * set the object to the values of the given object.
     *
     * @param  obj  DOCUMENT ME!
     */
    public abstract void reset(T obj);

    //J- jalopy has issues with this method
    protected boolean isSameDTOEntity(final BasicDTO entity, final BasicDTO otherEntity) {
        if (entity == null && otherEntity == null) {
            return true;
        }

        if (entity == null || otherEntity == null) {
            return false;
        }

        return (entity.getId() == otherEntity.getId());
    }
    //J+

    @Override
    public boolean equals(final Object obj) {
        if (obj.getClass().getName().equals(this.getClass().getName())) {
            // sometimes, it happens, that objects will be added to a set, which has no id (id = 0).
            // In order to prevent that only the first object without id can stay within the set, the
            // equals method return false if both objects which should be compared has no id
            return (this.id == ((BasicDTO)obj).getId()) && (this.id != 0) && (((BasicDTO)obj).getId() != 0);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (53 * hash) + (int)(this.id ^ (this.id >>> 32));
        return hash;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  a instance of the MessageConstants class or null, if the code will not be executed in the GWT client
     *          mode
     */
    protected MessageConstants getMessagesInstance() {
        // the method GWT.create() is only available in the client mode
        if (GWT.isClient()) {
            if (MESSAGES_INSTANCE == null) {
                MESSAGES_INSTANCE = (MessageConstants)GWT.create(MessageConstants.class);
            }

            return MESSAGES_INSTANCE;
        } else {
            return null;
        }
    }
}
