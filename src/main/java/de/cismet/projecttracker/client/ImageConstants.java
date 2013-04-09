/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Contains the paths to the images.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface ImageConstants extends ClientBundle {

    //~ Instance fields --------------------------------------------------------

    /**
     * public static final String ONGOING_PROJECT_IMAGE = "images/project.png"; public static final String
     * COMPLETED_PROJECT_IMAGE = "images/project.png"; public static final String ACITVE_STAFF_IMAGE =
     * "images/project.png"; public static final String FORMER_STAFF_IMAGE = "images/project.png"; public static final
     * String ONGOING_COMPANY_IMAGE = "images/project.png"; public static final String PROJECT_BODY_IMAGE =
     * "images/project.png"; public static final String ADD = "images/add.png"; public static final String DELETE =
     * "images/delete.png"; public static final String LOAD_IMAGE_URI = "images/ajax-loader.gif"; public static final
     * String OPEN_CALENDAR = "images/iCal-24x24.png"; public static final String KEY = "images/key-16x16.png"; public
     * static final String SAVE = "images/save-16x16.png"; public static final String CANCEL = "images/x-16x16.png";
     * public static final String ARROW_UP = "images/arrowUp.png"; public static final String ARROW_DOWN =
     * "images/arrowDown.png"; public static final String UPLOAD = "images/up-icon-16x16.png"; public static final
     * String DOWNLOAD = "images/down-icon-16x16.png"; public static final String CHECK = "images/tick-icon-16x16.png";
     */
    ImageConstants INSTANCE = (ImageConstants)GWT.create(ImageConstants.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("project.png")
    ImageResource ongoingProjectImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("project.png")
    ImageResource completedProjectImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("project.png")
    ImageResource activeStaffImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("project.png")
    ImageResource formerStaffImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("project.png")
    ImageResource ongoingCompanyImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("project.png")
    ImageResource projectBodyImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("add.png")
    ImageResource addImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("delete.png")
    ImageResource deleteImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("ajax-loader.gif")
    ImageResource loadImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("iCal-24x24.png")
    ImageResource openCalendarImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("key-16x16.png")
    ImageResource keyImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("save-16x16.png")
    ImageResource saveImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("x-16x16.png")
    ImageResource cancelImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("up-icon-16x16.png")
    ImageResource uploadImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("down-icon-16x16.png")
    ImageResource downloadImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("tick-icon-16x16.png")
    ImageResource checkImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("arrowUp.png")
    ImageResource arrowUpImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("arrowDown.png")
    ImageResource arrowDownImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("Reloj-arena-icon24x24.png")
    ImageResource historyImage();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("cismetLogo.png")
    ImageResource cismetLogo();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("c.png")
    ImageResource cLogo();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("magic.png")
    ImageResource magicFiller();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Source("circle_plus.png")
    ImageResource plus();
}
