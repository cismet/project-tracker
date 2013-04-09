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
package de.cismet.projecttracker.client.dto;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class CompanyDTO extends BasicDTO<CompanyDTO> implements Comparable<CompanyDTO> {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private ArrayList<ContractDTO> contracts = new ArrayList<ContractDTO>(0);
    private ArrayList<RealOverheadDTO> realOverheads = new ArrayList<RealOverheadDTO>(0);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CompanyDTO object.
     */
    public CompanyDTO() {
    }

    /**
     * Creates a new CompanyDTO object.
     *
     * @param  id             DOCUMENT ME!
     * @param  name           DOCUMENT ME!
     * @param  contracts      DOCUMENT ME!
     * @param  realOverheads  DOCUMENT ME!
     */
    public CompanyDTO(final long id,
            final String name,
            final ArrayList<ContractDTO> contracts,
            final ArrayList<RealOverheadDTO> realOverheads) {
        this.id = id;
        this.name = name;
        this.contracts = contracts;
        this.realOverheads = realOverheads;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the name
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the contracts
     */
    public ArrayList<ContractDTO> getContracts() {
        return contracts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  contracts  the contracts to set
     */
    public void setContracts(final ArrayList<ContractDTO> contracts) {
        this.contracts = contracts;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the realOverheads
     */
    public ArrayList<RealOverheadDTO> getRealOverheads() {
        return realOverheads;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  realOverheads  the realOverheads to set
     */
    public void setRealOverheads(final ArrayList<RealOverheadDTO> realOverheads) {
        this.realOverheads = realOverheads;
    }

    @Override
    public CompanyDTO createCopy() {
        return new CompanyDTO(id, name, contracts, realOverheads);
    }

    @Override
    public void reset(final CompanyDTO obj) {
        this.id = obj.id;
        this.name = obj.name;
        this.contracts = obj.contracts;
        this.realOverheads = obj.realOverheads;
    }

    @Override
    public int compareTo(final CompanyDTO o) {
        return name.compareTo(o.name);
    }
}
