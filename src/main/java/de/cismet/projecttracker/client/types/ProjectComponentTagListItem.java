/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectComponentTagDTO;

/**
 * A ListItem implementation that wraps ProjectComponentTagDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectComponentTagListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private ProjectComponentTagDTO tag;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectComponentTagListItem object.
     *
     * @param  tag  DOCUMENT ME!
     */
    public ProjectComponentTagListItem(final ProjectComponentTagDTO tag) {
        super("" + tag.getId(), tag.getName());
        this.tag = tag;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the project component tag
     */
    public ProjectComponentTagDTO getTag() {
        return tag;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tag  the project component tag that should be wrapped
     */
    public void setTag(final ProjectComponentTagDTO tag) {
        this.tag = tag;
        setId("" + tag.getId());
        setName(tag.getName());
    }
}
