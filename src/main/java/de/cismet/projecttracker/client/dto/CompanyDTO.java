/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import java.util.ArrayList;

/**
 *
 * @author therter
 */
public class CompanyDTO extends BasicDTO<CompanyDTO> implements Comparable<CompanyDTO> {
    private String name;
    private ArrayList<ContractDTO> contracts = new ArrayList<ContractDTO>(0);
    private ArrayList<RealOverheadDTO> realOverheads = new ArrayList<RealOverheadDTO>(0);

    public CompanyDTO() {
    }


    

    public CompanyDTO(long id, String name, ArrayList<ContractDTO> contracts, ArrayList<RealOverheadDTO> realOverheads) {
        this.id = id;
        this.name = name;
        this.contracts = contracts;
        this.realOverheads = realOverheads;
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
     * @return the contracts
     */
    public ArrayList<ContractDTO> getContracts() {
        return contracts;
    }

    /**
     * @param contracts the contracts to set
     */
    public void setContracts(ArrayList<ContractDTO> contracts) {
        this.contracts = contracts;
    }

    /**
     * @return the realOverheads
     */
    public ArrayList<RealOverheadDTO> getRealOverheads() {
        return realOverheads;
    }

    /**
     * @param realOverheads the realOverheads to set
     */
    public void setRealOverheads(ArrayList<RealOverheadDTO> realOverheads) {
        this.realOverheads = realOverheads;
    }

    @Override
    public CompanyDTO createCopy() {
        return new CompanyDTO(id, name, contracts, realOverheads);
    }

    @Override
    public void reset(CompanyDTO obj) {
        this.id = obj.id;
        this.name = obj.name;
        this.contracts = obj.contracts;
        this.realOverheads = obj.realOverheads;
    }

    @Override
    public int compareTo(CompanyDTO o) {
        return name.compareTo(o.name);
    }
}
