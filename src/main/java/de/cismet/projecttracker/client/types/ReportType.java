package de.cismet.projecttracker.client.types;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class represents a report generator
 * 
 * @author therter
 */
public class ReportType implements IsSerializable {
    private String name;
    private boolean userRelated;
    private boolean notUserRelated;

    public ReportType() {
    }

    public ReportType(String name, boolean userRelated, boolean notUserRelated) {
        this.name = name;
        this.userRelated = userRelated;
        this.notUserRelated = notUserRelated;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the userRelated
     */
    public boolean isUserRelated() {
        return userRelated;
    }

    /**
     * @param userRelated the userRelated to set
     */
    public void setUserRelated(boolean userRelated) {
        this.userRelated = userRelated;
    }

    /**
     * @return the notUserRelated
     */
    public boolean isNotUserRelated() {
        return notUserRelated;
    }

    /**
     * @param notUserRelated the notUserRelated to set
     */
    public void setNotUserRelated(boolean notUserRelated) {
        this.notUserRelated = notUserRelated;
    }
}
