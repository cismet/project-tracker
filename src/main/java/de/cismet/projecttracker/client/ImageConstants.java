package de.cismet.projecttracker.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


/**
 * Contains the paths to the images.
 * @author therter
 */
public interface ImageConstants extends ClientBundle {
    /**
    public static final String ONGOING_PROJECT_IMAGE = "images/project.png";
    public static final String COMPLETED_PROJECT_IMAGE = "images/project.png";
    public static final String ACITVE_STAFF_IMAGE = "images/project.png";
    public static final String FORMER_STAFF_IMAGE = "images/project.png";
    public static final String ONGOING_COMPANY_IMAGE = "images/project.png";
    public static final String PROJECT_BODY_IMAGE = "images/project.png";
    public static final String ADD = "images/add.png";
    public static final String DELETE = "images/delete.png";
    public static final String LOAD_IMAGE_URI = "images/ajax-loader.gif";
    public static final String OPEN_CALENDAR = "images/iCal-24x24.png";
    public static final String KEY = "images/key-16x16.png";
    public static final String SAVE = "images/save-16x16.png";
    public static final String CANCEL = "images/x-16x16.png";
    public static final String ARROW_UP = "images/arrowUp.png";
    public static final String ARROW_DOWN = "images/arrowDown.png";
    public static final String UPLOAD = "images/up-icon-16x16.png";
    public static final String DOWNLOAD = "images/down-icon-16x16.png";
    public static final String CHECK = "images/tick-icon-16x16.png";
     */
    final ImageConstants INSTANCE = (ImageConstants)GWT.create(ImageConstants.class);

    @Source("project.png")
    ImageResource ongoingProjectImage();

    @Source("project.png")
    ImageResource completedProjectImage();

    @Source("project.png")
    ImageResource activeStaffImage();

    @Source("project.png")
    ImageResource formerStaffImage();

    @Source("project.png")
    ImageResource ongoingCompanyImage();

    @Source("project.png")
    ImageResource projectBodyImage();

    @Source("add.png")
    ImageResource addImage();

    @Source("delete.png")
    ImageResource deleteImage();

    @Source("ajax-loader.gif")
    ImageResource loadImage();

    @Source("iCal-24x24.png")
    ImageResource openCalendarImage();

    @Source("key-16x16.png")
    ImageResource keyImage();

    @Source("save-16x16.png")
    ImageResource saveImage();

    @Source("x-16x16.png")
    ImageResource cancelImage();

    @Source("up-icon-16x16.png")
    ImageResource uploadImage();

    @Source("down-icon-16x16.png")
    ImageResource downloadImage();

    @Source("tick-icon-16x16.png")
    ImageResource checkImage();

    @Source("arrowUp.png")
    ImageResource arrowUpImage();

    @Source("arrowDown.png")
    ImageResource arrowDownImage();

    @Source("Reloj-arena-icon24x24.png")
    ImageResource historyImage();

    @Source("cismetLogo.png")
    ImageResource cismetLogo();
    
    @Source("c.png")
    ImageResource cLogo();
    
    @Source("magic.png")
    ImageResource magicFiller();
    
    @Source("circle_plus.png")
    ImageResource plus();
}
