/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.StaffDTO;

/**
 * A ListItem implementation that wraps StaffDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class StaffListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private StaffDTO staff;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StaffListItem object.
     *
     * @param  staff  DOCUMENT ME!
     */
    public StaffListItem(final StaffDTO staff) {
        this.staff = staff;
        setName(staff.getFirstname() + " " + staff.getName());
        setId("" + staff.getId());
    }

    /**
     * Creates a new StaffListItem object.
     *
     * @param  id    DOCUMENT ME!
     * @param  name  DOCUMENT ME!
     */
    public StaffListItem(final String id, final String name) {
        super(id, name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the staff
     */
    public StaffDTO getStaff() {
        return staff;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  staff  the staff to set
     */
    public void setStaff(final StaffDTO staff) {
        this.staff = staff;
        setName(staff.getFirstname() + " " + staff.getName());
        setId("" + staff.getId());
    }
}
