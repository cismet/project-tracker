/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.utilities;

import java.util.ResourceBundle;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class LanguageBundle {

    //~ Static fields/initializers ---------------------------------------------

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("de.cismet.projecttracker.serverLang");
    public static final String CANNOT_REMOVE_PROJECT_CATEGORY = BUNDLE.getString("cannotRemoveProjectCategory");
    public static final String CANNOT_CHANGE_ACTIVITY = BUNDLE.getString("cannotChangeActivity");
    public static final String REPORT_PLUGIN_NOT_FOUND = BUNDLE.getString("reportPluginNotFound");
    public static final String REPORT_NOT_FOUND = BUNDLE.getString("reportNotFound");
    public static final String END_IS_BEFORE_START = BUNDLE.getString("endIsBeforeStart");
    public static final String LOGIN_FAILED = BUNDLE.getString("loginFailed");
    public static final String ONLY_ALLOWED_FOR_ADMIN = BUNDLE.getString("onlyAllowedForAdmin");
    public static final String FILE_NOT_FOUND = BUNDLE.getString("fileNotFound");
    public static final String DOCUMENT_NOT_FOUND = BUNDLE.getString("documentNotFound");
    public static final String EXISTING_ACTIVITIES_FOR_PC_FOUND = BUNDLE.getString("existingActivitiesForPCFound");
    public static final String USERNAME_ALREADY_EXISTS = BUNDLE.getString("usernameAlreadyExists");
    public static final String TRAVEL_IS_ALREADY_PAYED = BUNDLE.getString("travelIsAlreadyPayed");
    public static final String PAYLOAD_NOT_FOUND = BUNDLE.getString("payloadNotFound");
    public static final String FILE_NAME_NOT_FOUND = BUNDLE.getString("fileNameNotFound");
    public static final String PARENT_ID_NOT_FOUND = BUNDLE.getString("parentIdNotFound");
    public static final String DOCUMENT_TYPE_NOT_FOUND = BUNDLE.getString("documentTypeNotFound");
    public static final String CANNOT_DELETE_PROJECT_BODY = BUNDLE.getString("cannotDeleteProjectBody");
    public static final String WARN_EMAIL_SUBJECT = BUNDLE.getString("warnEMailSubject");
    public static final String CRITICAL_WARN_EMAIL = BUNDLE.getString("criticalWarnEMail");
    public static final String FULL_STOP_EMAIL = BUNDLE.getString("fullStopEMail");
    public static final String EMAIL_BODY = BUNDLE.getString("emailBody");
    public static final String ACTIVITY_MUST_HAVE_A_PROJECTCOMPONENT = BUNDLE.getString(
            "activityMustHaveAProjectComponent");
    public static final String THERE_ARE_STILL_CONTRACTS_ASSIGNED_TO_THE_COMPANY = BUNDLE.getString(
            "thereAreStillContractsAssignedToTheCompany");
    public static final String THE_WORK_CATEGORY_IS_USED_BY_ACTIVITIES = BUNDLE.getString(
            "theWorkCategoryIsUsedByActivities");
    public static final String ACTIVITY_BEFORE_PERIOD_START = BUNDLE.getString("activityBeforePeriodStart");
    public static final String ACTIVITY_AFTER_PERIOD_END = BUNDLE.getString("activityAfterPeriodEnd");
    public static final String PROJECT_START_BEFORE_PROJECT_COMPONENT_START = BUNDLE.getString(
            "projectStartBeforeProjectComponentStart");
    public static final String PROJECT_END_AFTER_PROJECT_COMPONENT_END = BUNDLE.getString(
            "projectEndAfterProjectComponentEnd");
    public static final String WORK_PACKAGE_START_BEFORE_PROJECT_START = BUNDLE.getString(
            "workPackageStartBeforeProjectStart");
    public static final String WORK_PACKAGE_END_AFTER_PROJECT_END = BUNDLE.getString("workPackageEndAfterProjectEnd");
    public static final String ACTIVITY_BEFORE_START_OF_THE_PROJECT_COMPONENT = BUNDLE.getString(
            "activityBeforeStartOfTheProjectComponent");
    public static final String ACTIVITY_AFTER_END_OF_THE_PROJECT_COMPONENT = BUNDLE.getString(
            "activityAfterEndOfTheProjectComponent");
}
