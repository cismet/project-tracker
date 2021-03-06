/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.listener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;

import de.cismet.projecttracker.client.MessageConstants;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.LoadingPanel;
import de.cismet.projecttracker.client.exceptions.FullStopException;
import de.cismet.projecttracker.client.exceptions.NoSessionException;

/**
 * The Basis class for all Callback objects.
 *
 * @author   Thorsten
 * @version  $Revision$, $Date$
 */
public class BasicAsyncCallback<E> implements AsyncCallback<E> {

    //~ Static fields/initializers ---------------------------------------------

    protected static final MessageConstants MESSAGES = (MessageConstants)GWT.create(MessageConstants.class);
    private static boolean criticalErrorAppeared = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BasicAsyncCallback object.
     */
    public BasicAsyncCallback() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void onFailure(final Throwable caught) {
        // display error text if we can't get the quote:
        afterExecution(null, true);
        if (!criticalErrorAppeared) {
            try {
                LoadingPanel.getInstance().hideLoadingAnimation();
                throw caught;
            } catch (FullStopException e) {
                ProjectTrackerEntryPoint.outputBox(MESSAGES.workPackageOverbookedString(e.getMessage()));
            } catch (IncompatibleRemoteServiceException e) {
                ProjectTrackerEntryPoint.outputBox(MESSAGES.clientIsNotCompatiblerWithServerString());
                criticalErrorAppeared = true;
            } catch (InvocationException e) {
                ProjectTrackerEntryPoint.outputBox(MESSAGES.callDidNotCompleteCleanlyString() + "\n"
                            + e.getLocalizedMessage());
                criticalErrorAppeared = true;
            } catch (NoSessionException e) {
                if (ProjectTrackerEntryPoint.getInstance().isLoggedIn()) {
                    ProjectTrackerEntryPoint.getInstance().onClick(null);
                    ProjectTrackerEntryPoint.outputBox(MESSAGES.sessionExpiredString());
                    criticalErrorAppeared = true;
                }
            } catch (Throwable e) {
                ProjectTrackerEntryPoint.outputBox(e.getMessage());
                criticalErrorAppeared = true;
            }
        }
    }

    @Override
    public void onSuccess(final E result) {
        afterExecution(result, false);
        criticalErrorAppeared = false;
        LoadingPanel.getInstance().hideLoadingAnimation();
    }

    /**
     * This method will be invoked after the response of an async call was received. You should override this method
     * instead of the methods onFailure and onSuccess in order to react on the success or failure of an async server
     * call.
     *
     * @param  result           the result of the call
     * @param  operationFailed  true, if the call was successfully, false otherwise
     */
    protected void afterExecution(final E result, final boolean operationFailed) {
    }
}
