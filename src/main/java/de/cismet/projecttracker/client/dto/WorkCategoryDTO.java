/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 *
 * @author therter
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@JsonWorkCategoryId")
public class WorkCategoryDTO extends BasicDTO<WorkCategoryDTO> {
    public static final long WORK = 3;
    public static final long TRAVEL = 4;
    private String name;
    private boolean workpackagerelated = true;

    public WorkCategoryDTO() {
    }

     

    public WorkCategoryDTO(long id, String name, boolean workpackagerelated) {
        this.id = id;
        this.name = name;
        this.workpackagerelated = workpackagerelated;
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
     * @return the workpackageonly
     */
    public boolean getWorkpackagerelated() {
        return workpackagerelated;
    }

    /**
     * @param workpackageonly the workpackageonly to set
     */
    public void setWorkpackagerelated(boolean workpackagerelated) {
        this.workpackagerelated = workpackagerelated;
    }

    @Override
    public WorkCategoryDTO createCopy() {
        return new WorkCategoryDTO(id, name, workpackagerelated);
    }

    @Override
    public void reset(WorkCategoryDTO obj) {
        this.id = obj.id;
        this.name = obj.name;
        this.workpackagerelated = obj.workpackagerelated;
    }
}
