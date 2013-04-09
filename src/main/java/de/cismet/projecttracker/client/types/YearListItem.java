/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

/**
 * A ListItem implementation that wraps years.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class YearListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    int year;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new YearListItem object.
     *
     * @param  year  DOCUMENT ME!
     */
    public YearListItem(final int year) {
        super("" + year, "" + year);
        this.year = year;
    }

    /**
     * Creates a new YearListItem object.
     *
     * @param  id    DOCUMENT ME!
     * @param  name  DOCUMENT ME!
     */
    public YearListItem(final String id, final String name) {
        super(id, name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getYear() {
        return year;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  year  DOCUMENT ME!
     */
    public void setYear(final int year) {
        this.year = year;
        setId("" + year);
        setName("" + year);
    }
}
