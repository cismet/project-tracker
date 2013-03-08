package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.cismet.projecttracker.client.ImageConstants;

/**
 * This class can show a loading animation and is implemented as a singleton.
 * @author therter
 */
public class LoadingPanel {
    /**
     * contains the difference between the invocation count of the methods showLoadingAnimation()
     * and hideLoadingAnimation(). The value of this variable is always a positive integer or zero
     */
    private static int lockCount = 0;
    /**
     * this represents a transparent div that will be shown in the foreground of the
     * GUI and that prevents the usage of GUI elements.
     */
    private SimplePanel lockingPanel = new SimplePanel();
    /**
     * this panel contains the loading animation
     */
    private VerticalPanel load = new VerticalPanel();
    /**
     * contains the sole instance of this class.
     */
    private static LoadingPanel instance;

    /**
     * @return the instance of the class LoadingPanel
     */
    public static LoadingPanel getInstance() {
        if (instance == null) {
            instance = new LoadingPanel();
        }

        return instance;
    }

    /**
     * creates a new intsnce
     */
    private LoadingPanel() {
        Image img = new Image(ImageConstants.INSTANCE.loadImage());
        lockingPanel.setStyleName("Lock");
        lockingPanel.setWidth(Window.getClientWidth() + "px");
        lockingPanel.setHeight(Window.getClientHeight() + "px");

        load.setWidth(Window.getClientWidth() + "px");
        load.setHeight(Window.getClientHeight() + "px");
        load.setStyleName("LoadingImage");
        load.add(img);
        load.setCellHorizontalAlignment(img, VerticalPanel.ALIGN_CENTER);
        load.setCellVerticalAlignment(img, VerticalPanel.ALIGN_MIDDLE);
    }

    
    /**
     * Shows a loading animation on the screen and locks the screen.
     * This method counts the number of invocations.
     * If the method {@link hideLoadingAnimation()} has the same number of invocations as this method,
     * the loading animation will be removed and the screen will unlocked.
     */
    public void showLoadingAnimation() {
        ++lockCount;
//        if (lockCount == 1) {
//            RootPanel.get("containerId").add(lockingPanel);
//            RootPanel.get("containerId").add(load);
//        }
    }


    /**
     * Removes the loading animation and unlocks the screen. @see {@link showLoadingAnimation()}
     */
    public void hideLoadingAnimation() {
        --lockCount;
        if (lockCount < 0) {
            lockCount = 0;
        }

//        if (lockCount == 0) {
//            RootPanel.get("containerId").remove(lockingPanel);
//            RootPanel.get("containerId").remove(load);
//        }
    }
}
