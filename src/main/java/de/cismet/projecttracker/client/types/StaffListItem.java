package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.StaffDTO;

/**
 * A ListItem implementation that wraps StaffDTO objects
 *
 * @author therter
 */
public class StaffListItem extends ListItem {
    private StaffDTO staff;

    public StaffListItem(StaffDTO staff) {
        this.staff = staff;
        setName(staff.getFirstname() + " " + staff.getName());
        setId("" + staff.getId());
    }

    public StaffListItem(String id, String name) {
        super(id, name);
    }

    /**
     * @return the staff
     */
    public StaffDTO getStaff() {
        return staff;
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(StaffDTO staff) {
        this.staff = staff;
        setName(staff.getFirstname() + " " + staff.getName());
        setId("" + staff.getId());
    }
}
