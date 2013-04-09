/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.apache.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.*;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.cismet.projecttracker.client.ProjectService;
import de.cismet.projecttracker.client.common.ui.FavouriteTaskStory;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.BasicDTO;
import de.cismet.projecttracker.client.dto.CompanyDTO;
import de.cismet.projecttracker.client.dto.ContractDTO;
import de.cismet.projecttracker.client.dto.ContractDocumentDTO;
import de.cismet.projecttracker.client.dto.CostCategoryDTO;
import de.cismet.projecttracker.client.dto.EstimatedComponentCostDTO;
import de.cismet.projecttracker.client.dto.EstimatedComponentCostMonthDTO;
import de.cismet.projecttracker.client.dto.FundingDTO;
import de.cismet.projecttracker.client.dto.ProfileDTO;
import de.cismet.projecttracker.client.dto.ProjectBodyDTO;
import de.cismet.projecttracker.client.dto.ProjectCategoryDTO;
import de.cismet.projecttracker.client.dto.ProjectComponentTagDTO;
import de.cismet.projecttracker.client.dto.ProjectCostsDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.ProjectShortDTO;
import de.cismet.projecttracker.client.dto.RealOverheadDTO;
import de.cismet.projecttracker.client.dto.ReportDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.TravelDTO;
import de.cismet.projecttracker.client.dto.TravelDocumentDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.dto.WorkPackagePeriodDTO;
import de.cismet.projecttracker.client.dto.WorkPackageProgressDTO;
import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.FullStopException;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.exceptions.LoginFailedException;
import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;
import de.cismet.projecttracker.client.exceptions.PersistentLayerException;
import de.cismet.projecttracker.client.exceptions.ReportNotFoundException;
import de.cismet.projecttracker.client.types.ActivityResponseType;
import de.cismet.projecttracker.client.types.HolidayType;
import de.cismet.projecttracker.client.types.ReportType;
import de.cismet.projecttracker.client.types.TimePeriod;

import de.cismet.projecttracker.report.ProjectTrackerReport;
import de.cismet.projecttracker.report.ReportManager;
import de.cismet.projecttracker.report.commons.HolidayEvaluator;
import de.cismet.projecttracker.report.db.entities.*;
import de.cismet.projecttracker.report.exceptions.UserNotFoundException;
import de.cismet.projecttracker.report.helper.CalendarHelper;
import de.cismet.projecttracker.report.timetracker.TimetrackerQuery;

import de.cismet.projecttracker.utilities.DBManagerWrapper;
import de.cismet.projecttracker.utilities.DTOManager;
import de.cismet.projecttracker.utilities.EmailTaskNotice;
import de.cismet.projecttracker.utilities.LanguageBundle;
import de.cismet.projecttracker.utilities.Utilities;

import de.cismet.web.timetracker.types.HoursOfWork;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.*;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectServiceImpl extends RemoteServiceServlet implements ProjectService {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(ProjectServiceImpl.class);
    private static final String RECENT_ACTIVITIES_QUERY =
        "select max(id), workpackageid, description from activity where "
                + "staffid = %1$s and kindofactivity = %2$s group by workpackageid, description having workpackageid <> 408 "
                + "order by max(id) desc limit 30;";
    private static final String FAVOURITE_ACTIVITIES_QUERY =
        "select max(id), workpackageid, description from activity where "
                + "staffid = %1$s and day is null group by workpackageid, description";
    private static final String RECENT_ACTIVITIES_EX_QUERY =
        "select max(id), workpackageid, description from activity where "
                + "staffid <> %1$s and kindofactivity = %2$s group by workpackageid, description having workpackageid <> 408 "
                + "order by max(id) desc limit 30;";
    private static final String REAL_WORKING_TIME_QUERY =
        "SELECT sum(workinghours)  FROM activity WHERE staffid = %2$s AND date_trunc('day', day) >= '%3$s' AND date_trunc('day', day) < '%4$s' AND workpackageid NOT IN (234, 407,408 ,409, 410,411,414, 419);";
//    private static final String TRAVEL_TIME_QUERY = "SELECT sum(coalesce(nullif(workinghours, 0),%1$s))  FROM activity WHERE staffid = %2$s AND day >= '%3$s' AND day < '%4$s' AND workpackageid = 414";
    private static final String REAL_WORKING_TIME_ILLNESS_AND_HOLIDAY =
        "SELECT sum(case workinghours  when -1 then 0 when 0 then %1$s else workinghours end)  FROM activity WHERE staffid = %2$s AND date_trunc('day', day) >= '%3$s' AND date_trunc('day', day) < '%4$s' AND workpackageid IN (409,410,411,419);";
    private static final String FAVOURITE_EXISTS_QUERY =
        "select description, staffid, workpackageid from activity where "
                + "staffid=%1$s and day is null and workpackageid = %2$s and "
                + "case when description is null then true else description = '%3$s' end";
    private static final String CHECK_BEGIN_OF_DAY_QUERY =
        "select max(day) from activity where staffid = %1$s and date_trunc('day', day) = '%2$s' and kindofactivity=1";
    private static final String CHECK_KIND_OF_LAST_ACTIVITY =
        "select kindofactivity, day from activity where staffid = %1$s and day = (select max(day) from activity where staffid = %1$s and date_trunc('day',day) ='%2$s')";
    private static final String COUNT_VACATION_DAYS =
        "select * from activity where staffid=%1$s and workpackageid = 409 and day>='%2$s' and day <='%3$s'";
    private static final String UNLOCKED_DAYS =
        "select distinct(date_trunc('day',day))  from \"public\".activity  where staffid = %1$s and day>='01-03-2012' except select distinct(date_trunc('day',day)) from \"public\".activity where staffid=%1$s and day>='01-03-2012' and kindofactivity = 3 order by date_trunc desc;";
    private static ReportManager reportManager;
    private static WarningSystem warningSystem;
    private static boolean initialised = false;
    private static DTOManager dtoManager = new DTOManager();
    private static final GregorianCalendar accountBalanceDueDate = new GregorianCalendar(2012, 2, 1);
//    private static final int PAUSE_CHECKER_DAYS = 2;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        super.init();
        ConfigurationManager.getInstance().setContext(getServletContext());
        if (!initialised) {
            initialised = true;
            Utilities.initLogger(getServletContext().getRealPath("/"));
            if (logger.isDebugEnabled()) {
                logger.debug("init PersistentBeanManager");
            }
            warningSystem = new WarningSystem();

            // check if the language resource bundle contains all required messages
            try {
                LanguageBundle.class.newInstance();
            } catch (Exception e) {
                logger.error("the language resource bundle is not complete.", e);
            }

            reportManager = new ReportManager(getServletContext().getRealPath("/"), ConfigurationManager.getInstance().getConfBaseDir());

            // start the timer that checks pause tasks

// PauseChecker pauseChecker = new PauseChecker(this, PAUSE_CHECKER_DAYS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectShortDTO> getAllOngoingProjects() throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        checkAdminPermission();
        if (logger.isDebugEnabled()) {
            logger.debug("get all ongoing projects");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        try {
            final List<Project> result = dbManager.getObjectsByAttribute(
                    "select proj from Project as proj left join proj.projectPeriods as period where period.id in (Select pp.id from ProjectPeriod as pp where pp.asof in (select max(pper.asof) as masof from ProjectPeriod as pper group by pper.project)) and (period.todate=null or period.todate>current_timestamp) or period=null");

            // convert the server version of the Project type to the client version of the Project type
            final ArrayList<ProjectShortDTO> clonedList = new ArrayList<ProjectShortDTO>();

            for (final Project o : result) {
                final ProjectDTO pro = ((ProjectDTO)dtoManager.clone(o));
                clonedList.add(pro.toShortVersion());
            }

            return clonedList;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectShortDTO> getAllCompletedProjects() throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        checkAdminPermission();
        if (logger.isDebugEnabled()) {
            logger.debug("get all completed projects");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        try {
            final List<Project> result = dbManager.getObjectsByAttribute(
                    "select proj from Project as proj left join proj.projectPeriods as period where period.id in (Select pp.id from ProjectPeriod as pp where pp.asof in (select max(pper.asof) as masof from ProjectPeriod as pper group by pper.project)) and period.todate<current_timestamp");

            // convert the server version of the Project type to the client version of the Project type
            final ArrayList<ProjectShortDTO> clonedList = new ArrayList<ProjectShortDTO>();

            for (final Project o : result) {
                final ProjectDTO pro = ((ProjectDTO)dtoManager.clone(o));
                clonedList.add(pro.toShortVersion());
            }

            return clonedList;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<StaffDTO> getCurrentEmployees() throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        checkSession();
        if (logger.isDebugEnabled()) {
            logger.debug("get current employees");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<Staff> result = dbManager.getObjectsByAttribute(
                    "select distinct staff from Staff as staff left join staff.contracts as contract where contract.todate=null or contract.todate>current_timestamp");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<StaffDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<StaffDTO> getAllFormerEmployees() throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        checkSession();
        if (logger.isDebugEnabled()) {
            logger.debug("get all former employees");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<Staff> result = dbManager.getObjectsByAttribute(
                    "select distinct staff from Staff as staff left join staff.contracts as contract where contract.todate!=null and contract.todate<=current_timestamp and staff.id not in (select staff1.id from Staff as staff1 left join staff1.contracts as contract1 where contract1.todate=null or contract1.todate>current_timestamp)");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<StaffDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<StaffDTO> getAllEmployees() throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        checkSession();
        if (logger.isDebugEnabled()) {
            logger.debug("get alle mployees");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<Staff> result = (List<Staff>)dbManager.getAllObjects(Staff.class);

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<StaffDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectShortDTO> getAllProjects() throws InvalidInputValuesException,
        DataRetrievalException,
        NoSessionException {
        checkSession();
        if (logger.isDebugEnabled()) {
            logger.debug("get all projects");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<Project> result = (List<Project>)dbManager.getAllObjects(Project.class);

            // convert the server version of the Project type to the client version of the Project type
            final ArrayList<ProjectShortDTO> clonedList = new ArrayList<ProjectShortDTO>();

            for (final Project o : result) {
                final ProjectDTO pro = ((ProjectDTO)dtoManager.clone(o));
                clonedList.add(pro.toShortVersion());
            }

            return clonedList;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectCategoryDTO> getAllProjectCategories() throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        checkAdminPermission();
        if (logger.isDebugEnabled()) {
            logger.debug("get all project categories");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<ProjectCategory> result = (List<ProjectCategory>)dbManager.getAllObjects(ProjectCategory.class);
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " project categories found");
            }

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ProjectCategoryDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectDTO> getAllProjectsFull() throws InvalidInputValuesException,
        DataRetrievalException,
        NoSessionException {
        checkSession();
        if (logger.isDebugEnabled()) {
            logger.debug("get all projects full");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<Project> result = (List<Project>)dbManager.getAllObjects(Project.class);

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ProjectDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProject(final long id) throws InvalidInputValuesException,
        PersistentLayerException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete project");
        }
        checkAdminPermission();
        Project project = null;
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            project = (Project)dbManager.getObject(Project.class, id);

            final Query query = dbManager.getSession()
                        .createQuery(
                            "select proj from Project as proj inner join proj.workPackages as wp inner join wp.activityWorkPackages as activity where proj.id="
                            + id);
            final Object result = query.setMaxResults(1).uniqueResult();

            if (result != null) {
                throw new PersistentLayerException(LanguageBundle.EXISTING_ACTIVITIES_FOR_PC_FOUND);
            }

            if (project != null) {
                dbManager.deleteObject(project);
            }
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectDTO getProject(final long projectId) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get project with id " + projectId);
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Project result = (Project)dbManager.getObject(Project.class, projectId);

            // convert the server version of the Project type to the client version of the Project type
            return (ProjectDTO)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectDTO saveProject(final ProjectDTO project) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save project");
        }
        checkAdminPermission();

        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            if (project.getProjectPeriods() != null) {
                for (final ProjectPeriodDTO pp : project.getProjectPeriods()) {
                    if (pp.getAsof() == null) {
                        pp.setAsof(new Date());
                    }
                }
            }

            final ProjectPeriodDTO projectPeriod = project.determineMostRecentPeriod();

            if (projectPeriod != null) {
                for (final WorkPackageDTO tmp : project.getWorkPackages()) {
                    final WorkPackagePeriodDTO period = tmp.determineMostRecentPeriod();
                    if (period != null) {
                        if (period.getFromdate().before(projectPeriod.getFromdate())) {
                            throw new InvalidInputValuesException(
                                LanguageBundle.PROJECT_START_BEFORE_PROJECT_COMPONENT_START
                                        + " "
                                        + tmp.getAbbreviation());
                        }
                        if ((period.getTodate() != null) && (projectPeriod.getTodate() != null)
                                    && period.getTodate().after(projectPeriod.getTodate())) {
                            throw new InvalidInputValuesException(LanguageBundle.PROJECT_END_AFTER_PROJECT_COMPONENT_END
                                        + " " + tmp.getAbbreviation());
                        }
                    }
                }
            }
            // convert the client version of the Project type to the server version of the Project type
            final Project projectHib = (Project)dtoManager.merge(project);

            // TODO wird das if noch benoetigt
            // if no parent project is set, the project object refers to an
            // parent project with the id 0. This leads to an hibernate failure
            if ((projectHib.getProject() != null) && (projectHib.getProject().getId() == 0)) {
                System.out.println("projectHib.setProject(null); wird benötigt");
                projectHib.setProject(null);
            }

            dbManager.saveObject(projectHib);

            // the project object must be returned, so that the client side has
            // the generated IDs of the new objects
            return (ProjectDTO)dtoManager.clone(projectHib);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectShortDTO createProject() throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create project");
        }
        checkAdminPermission();
        if (logger.isDebugEnabled()) {
            logger.debug("create new project");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Project project = new Project();
            project.setName("Project" + (new Date()).getTime());

            // set body
            ProjectBody body = getAnyProjectBody(dbManager);
            if (body == null) {
                body = new ProjectBody();
                body.setName("dummyBody");
                final Set<Project> projects = new HashSet<Project>();
                projects.add(project);
                body.setProjects(projects);
                // save the body in the db
                final long id = (Long)dbManager.createObject(body);
                body.setId(id);
            }
            project.setProjectBody(body);

            final long projectId = (Long)dbManager.createObject(project);
            project.setId(projectId);
            if (logger.isDebugEnabled()) {
                logger.debug("project id of the new project: " + projectId);
            }
            final ProjectDTO newProject = (ProjectDTO)dtoManager.clone(project);

            return newProject.toShortVersion();
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    public WorkPackageDTO getWorkpackageData(final long workpackageId) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        checkAdminPermission();
        if (logger.isDebugEnabled()) {
            logger.debug("getProjectData for project id: " + workpackageId);
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final WorkPackage result = (WorkPackage)dbManager.getObject(WorkPackage.class, workpackageId);

            // convert the server version of the Project type to the client version of the Project type
            return (WorkPackageDTO)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkPackageDTO saveWorkPackage(final WorkPackageDTO workpackage) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save work package");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // set the timestamps of the new periods
            if (workpackage.getWorkPackagePeriods() != null) {
                for (final WorkPackagePeriodDTO tmp : workpackage.getWorkPackagePeriods()) {
                    if (tmp.getAsof() == null) {
                        // the period is a new period, so the asOf value will be set
                        final ProjectPeriodDTO period = workpackage.getProject().determineMostRecentPeriod();
                        tmp.setAsof(new Date());

                        // checks if the new time period of the work package is within the current project period
                        if ((period != null) && tmp.getFromdate().before(period.getFromdate())) {
                            throw new InvalidInputValuesException(
                                LanguageBundle.WORK_PACKAGE_START_BEFORE_PROJECT_START);
                        } else if ((period != null) && (period.getTodate() != null) && (tmp.getTodate() != null)
                                    && tmp.getTodate().after(period.getTodate())) {
                            throw new InvalidInputValuesException(LanguageBundle.WORK_PACKAGE_END_AFTER_PROJECT_END);
                        }
                    }
                }
            }

            // set the timestamps of the new progresses
            if (workpackage.getWorkPackageProgresses() != null) {
                for (final WorkPackageProgressDTO tmp : workpackage.getWorkPackageProgresses()) {
                    if (tmp.getTime() == null) {
                        tmp.setTime(new Date());
                    }
                }
            }

            // check if there exists any activity that is not within the project period
            final WorkPackagePeriodDTO period = workpackage.determineMostRecentPeriod();
            final WorkPackage wp = (WorkPackage)dtoManager.merge(workpackage);
            Criterion expression;

            if (period.getTodate() != null) {
                expression = Restrictions.or(Restrictions.gt("day", period.getTodate()),
                        Restrictions.lt("day", period.getFromdate()));
            } else {
                expression = Restrictions.lt("day", period.getFromdate());
            }

            final Criteria crit = dbManager.getSession()
                        .createCriteria(Activity.class)
                        .add(
                            Restrictions.and(Restrictions.eq("workPackage", wp), expression))
                        .setMaxResults(1);
            final Activity act = (Activity)crit.uniqueResult();

            if (act != null) {
                if (act.getDay().before(period.getFromdate())) {
                    throw new InvalidInputValuesException(LanguageBundle.ACTIVITY_BEFORE_PERIOD_START);
                } else {
                    throw new InvalidInputValuesException(LanguageBundle.ACTIVITY_AFTER_PERIOD_END);
                }
            }

            dbManager.saveObject(wp);

            return (WorkPackageDTO)dtoManager.clone(wp);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkPackageDTO createWorkPackage(WorkPackageDTO workpackage) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create work package");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final WorkPackage wp = (WorkPackage)dtoManager.merge(workpackage);

            if (wp.getCostCategory() == null) {
                wp.setCostCategory(getAnyCostCategory(wp.getProject(), dbManager));
            }

            final long id = (Long)dbManager.createObject(wp);
            wp.setId(id);

            // the workpackage object must be returned, so that the client side has
            // the generated IDs of the new objects
            workpackage = (WorkPackageDTO)dtoManager.clone(wp);

            return workpackage;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWorkPackage(final WorkPackageDTO object) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete work package");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final WorkPackage wp = (WorkPackage)dbManager.getObject(WorkPackage.class, object.getId());

            if ((wp.getActivityWorkPackages() != null) && (wp.getActivityWorkPackages().size() > 0)) {
                throw new PersistentLayerException(LanguageBundle.EXISTING_ACTIVITIES_FOR_PC_FOUND);
            }

            dbManager.deleteObject(wp);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   project    DOCUMENT ME!
     * @param   dbManager  DOCUMENT ME!
     *
     * @return  any cost category for the given project. If the given project have no cost category, a new one will be
     *          created.
     *
     * @throws  DataRetrievalException    DOCUMENT ME!
     * @throws  PersistentLayerException  DOCUMENT ME!
     */
    private CostCategory getAnyCostCategory(final Project project, final DBManagerWrapper dbManager)
            throws DataRetrievalException, PersistentLayerException {
        if (project != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("get any cost category");
            }

            CostCategory result = (CostCategory)dbManager.getObjectByAttribute(CostCategory.class, "project", project);
            if (logger.isDebugEnabled()) {
                logger.debug(((result == null) ? "no" : "any") + " cost category found");
            }

            if (result == null) {
                result = new CostCategory();
                result.setProject(project);
                result.setName("dummy");
                result.setFundingrate(0.0);
                dbManager.createObject(result);
            }

            return result;
        } else {
            logger.error("Project argument of method getRandomCostCategory is null. This should never happen");
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dbManager  DOCUMENT ME!
     *
     * @return  any project body. If no one exists, null will be returned
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    private ProjectBody getAnyProjectBody(final DBManagerWrapper dbManager) throws DataRetrievalException {
        if (logger.isDebugEnabled()) {
            logger.debug("get any project body");
        }
        final ProjectBody result = (ProjectBody)dbManager.getAnyObjectByClass(ProjectBody.class);
        if (logger.isDebugEnabled()) {
            logger.debug(((result == null) ? "no" : "any") + " project body found");
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createStaff(final StaffDTO staff) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create staff");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final long id = (Long)dbManager.createObject(dtoManager.merge(staff));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StaffDTO saveStaff(final StaffDTO staff) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save staff");
        }
        checkUserOrAdminPermission(staff.getId());

        try {
            final Staff user = getStaffByUsername(staff.getUsername());
            final SessionInformation sessionInfo = getCurrentSession();

            if ((user != null) && (user.getId() != staff.getId())) {
                throw new PersistentLayerException(LanguageBundle.USERNAME_ALREADY_EXISTS);
            }

            if (!sessionInfo.isAdmin()) {
                staff.setPermissions(sessionInfo.getCurrentUser().getPermissions());
            }
        } catch (DataRetrievalException e) {
            logger.error(e.getMessage(), e);
            throw new PersistentLayerException(e.getMessage());
        }

        final DBManagerWrapper dbManager = new DBManagerWrapper();
        final Staff staffHib;

        try {
            for (final ContractDTO c : staff.getContracts()) {
                if (c.getId() != 0) {
                    dbManager.saveObject(dtoManager.merge(c));
                } else {
                    // convert the client version of the Project type to the server version of the Project type
                    final Contract contract = (Contract)dtoManager.merge(c);
                    final long id = (Long)dbManager.createObject(contract);
                    c.setId(id);
                }
            }

            staffHib = (Staff)dtoManager.merge(staff);
            // the password must be copied to the staff object that should be saved, because the password
            // was never sent to the client-side and so the received staff object doesn't contain the password
            final Staff original = getStaffById(staff.getId());
            staffHib.setPassword(original.getPassword());

            if (staff.getProfile() != null) {
                if (staff.getProfile().getId() != 0) {
                    dbManager.saveObject(dtoManager.merge(staff.getProfile()));
                } else {
                    final Profile p = (Profile)dtoManager.merge(staff.getProfile());
                    final long id = (Long)dbManager.createObject(p);
                    p.setId(id);
                    staffHib.setProfile(p);
                }
            }

            dbManager.saveObject(staffHib);
        } finally {
            dbManager.closeSession();
        }
        return (StaffDTO)dtoManager.clone(staffHib);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteStaff(final StaffDTO staff) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete staff");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            dbManager.deleteObject(dtoManager.merge(staff));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TravelDTO createTravelExpenseReport(final TravelDTO travel) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create travel expense report");
        }
        checkUserOrAdminPermission(travel.getStaff().getId());
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            travel.setDate(new Date());
            final Travel travelHib = (Travel)dtoManager.merge(travel);
            dbManager.createObject(travelHib);
            return (TravelDTO)dtoManager.clone(travelHib);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TravelDTO saveTravelExpenseReport(final TravelDTO travel) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save travel expense report");
        }
        checkUserOrAdminPermission(travel.getStaff().getId());
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // TODO: Reise darf nicht mehr verändert werden, wenn sie bereits bezahlt wurde
            final Travel travelHib = (Travel)dtoManager.merge(travel);
            dbManager.saveObject(travelHib);

            return (TravelDTO)dtoManager.clone(travelHib);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTravelExpenseReport(final TravelDTO travel) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete travel expense report");
        }
        checkUserOrAdminPermission(travel.getStaff().getId());
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // TODO serverseitig abfragen, ob paymentday gesetzt ist
            if (travel.getPaymentdate() != null) {
                throw new PersistentLayerException(LanguageBundle.TRAVEL_IS_ALREADY_PAYED);
            }
            dbManager.deleteObject(dtoManager.merge(travel));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changePassword(final StaffDTO staff, final String newPassword) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("change password");
        }
        checkUserOrAdminPermission(staff.getId());
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Staff staffHib = getStaffById(staff.getId());
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(newPassword.getBytes());

            staffHib.setPassword(md.digest());
            dbManager.saveObject(staffHib);
        } catch (NoSuchAlgorithmException e) {
            throw new PersistentLayerException("Cannot find the SHA1 algorithm.", e);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<CompanyDTO> getCompanies() throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        checkSession();
        if (logger.isDebugEnabled()) {
            logger.debug("get companies");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<Company> result = dbManager.getAllObjects(Company.class);
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " companies found");
            }

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<CompanyDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<EstimatedComponentCostDTO> getEstimatedWorkPackageCostForWP(final WorkPackageDTO workPackage)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkAdminPermission();
        if (logger.isDebugEnabled()) {
            logger.debug("get estimated work package cost");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<EstimatedComponentCost> result = dbManager.getObjectsByAttribute(
                    EstimatedComponentCost.class,
                    "workPackage",
                    dtoManager.merge(workPackage));
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " estimations found");
            }

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<EstimatedComponentCostDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstimatedComponentCostMonthDTO saveEstimatedWorkPackageCostMonth(
            final EstimatedComponentCostMonthDTO estimation) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save estimation month");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final EstimatedComponentCostMonth est = (EstimatedComponentCostMonth)dtoManager.merge(estimation);
            dbManager.saveObject(est);

            return (EstimatedComponentCostMonthDTO)dtoManager.clone(est);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstimatedComponentCostMonthDTO createEstimatedWorkPackageCostMonth(
            final EstimatedComponentCostMonthDTO estimation) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create estimation month");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final EstimatedComponentCostMonth est = (EstimatedComponentCostMonth)dtoManager.merge(estimation);
            final long id = (Long)dbManager.createObject(est);
            est.setId(id);

            return (EstimatedComponentCostMonthDTO)dtoManager.clone(est);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstimatedComponentCostDTO createEstimatedWorkPackageCost(final EstimatedComponentCostDTO estimation)
            throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create estimation");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            estimation.setCreationtime(new Date());
            final EstimatedComponentCost est = (EstimatedComponentCost)dtoManager.merge(estimation);
            final long id = (Long)dbManager.createObject(est);
            est.setId(id);

            return (EstimatedComponentCostDTO)dtoManager.clone(est);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEstimatedWorkPackageCost(final EstimatedComponentCostDTO estimation)
            throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete estimation");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            dbManager.deleteObject(dtoManager.merge(estimation));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContract(final ContractDTO contract) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete contract");
        }
        checkUserOrAdminPermission(contract.getStaff().getId());
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            dbManager.deleteObject(dtoManager.merge(contract));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createCompany(final CompanyDTO company) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create company");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final long id = (Long)dbManager.createObject(dtoManager.merge(company));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompanyDTO saveCompany(final CompanyDTO company) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save company");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // convert the client version of the Company object to the server version of the Company object
            final Company comp = (Company)dtoManager.merge(company);

            for (final RealOverhead overhead : comp.getRealOverheads()) {
                if (overhead.getId() != 0) {
                    dbManager.saveObject(overhead);
                } else {
                    final long id = (Long)dbManager.createObject(overhead);
                    overhead.setId(id);
                }
            }

            dbManager.saveObject(comp);

            return (CompanyDTO)dtoManager.clone(comp);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCompany(final CompanyDTO company) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete company");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Company comp = (Company)dbManager.getObject(Company.class, company.getId());

            if ((comp.getContracts()
                            == null)
                        || (comp.getContracts().size() == 0)) {
                dbManager.deleteObject(comp);
            } else {
                throw new PersistentLayerException(LanguageBundle.THERE_ARE_STILL_CONTRACTS_ASSIGNED_TO_THE_COMPANY);
            }
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRealOverhead(final RealOverheadDTO overhead) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete real overhead");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            dbManager.deleteObject(dtoManager.merge(overhead));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectBodyDTO> getProjectBodies() throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get project bodies");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<ProjectBodyDTO> result = dbManager.getAllObjects(ProjectBody.class);
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " project bodies found");
            }

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ProjectBodyDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createProjectBody(final ProjectBodyDTO projectBody) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create project bodies");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final long id = (Long)dbManager.createObject(dtoManager.merge(projectBody));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectBodyDTO saveProjectBody(final ProjectBodyDTO projectBody) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save project bodies");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final ProjectBody body = (ProjectBody)dtoManager.merge(projectBody);
            dbManager.saveObject(body);
            return (ProjectBodyDTO)dtoManager.clone(body);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProjectBody(final ProjectBodyDTO projectBody) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete project bodies");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // convert the client version of the Project type to the server version of the Project type
            final ProjectBody body = (ProjectBody)dtoManager.merge(projectBody);
            final Project pro = (Project)dbManager.getObjectByAttribute(Project.class, "projectBody", body);

            if (pro
                        != null) {
                throw new PersistentLayerException(LanguageBundle.CANNOT_DELETE_PROJECT_BODY);
            }

            dbManager.deleteObject(body);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createCostCategory(final CostCategoryDTO costCategory) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create cost category");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final long id = (Long)dbManager.createObject(dtoManager.merge(costCategory));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CostCategoryDTO saveCostCategory(final CostCategoryDTO costCategory) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save cost category");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final CostCategory cat = (CostCategory)dtoManager.merge(costCategory);
            dbManager.saveObject(cat);
            return (CostCategoryDTO)dtoManager.clone(cat);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCostCategory(final CostCategoryDTO costCategory) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete cost category");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            dbManager.deleteObject(dtoManager.merge(costCategory));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createProjectComponentTag(final ProjectComponentTagDTO tag) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create project component tag");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final long id = (Long)dbManager.createObject(dtoManager.merge(tag));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProjectComponentTag(final ProjectComponentTagDTO tag) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete project component tag");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            dbManager.deleteObject(dtoManager.merge(tag));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createProjectCosts(final ProjectCostsDTO projectCosts) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create project costs");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final long id = (Long)dbManager.createObject(dtoManager.merge(projectCosts));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectCostsDTO saveProjectCosts(final ProjectCostsDTO projectCosts) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save project costs");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final ProjectCosts costs = (ProjectCosts)dtoManager.merge(projectCosts);
            dbManager.saveObject(costs);

            return (ProjectCostsDTO)dtoManager.clone(costs);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProjectCosts(final ProjectCostsDTO projectCosts) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete project costs");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            dbManager.deleteObject(dtoManager.merge(projectCosts));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectCostsDTO> getProjectCostsByProject(final ProjectDTO project)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get project costs");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<ProjectCostsDTO> result = dbManager.getObjectsByAttribute(
                    ProjectCosts.class,
                    "project",
                    dtoManager.merge(project));
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " project costs found");
            }

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ProjectCostsDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ActivityDTO> getActivitiesByWeek(final StaffDTO staff, final int year, final int week)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get activities by week: " + year + "-" + week);
        }
        Session hibernateSession = null;
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff)dtoManager.merge(staff);
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final GregorianCalendar from = getFirstDayOfWeek(year, week);
            final GregorianCalendar till = getLastDayOfWeek(year, week);
            till.add(GregorianCalendar.DAY_OF_MONTH, 1);
//            from.set(GregorianCalendar.HOUR_OF_DAY, 4);
//            from.set(GregorianCalendar.MINUTE, 1);
//            till.set(GregorianCalendar.HOUR_OF_DAY, 4);
//            Timestamp timeFrom = new Timestamp(from.getTimeInMillis());
//            Timestamp timeTill = new Timestamp(till.getTimeInMillis());

            hibernateSession = dbManager.getSession();
            final List<Activity> res = hibernateSession.createCriteria(Activity.class)
                        .add(Restrictions.and(
                                    Restrictions.eq("staff", user),
                                    Restrictions.between("day", from.getTime(), till.getTime())))
                        .list();
            final List<Activity> result = new ArrayList<Activity>();
            for (final Activity tmp : res) {
                if (tmp.getKindofactivity() == ActivityDTO.ACTIVITY) {
                    if (!isSameDay(tmp.getDay(), till.getTime())) {
                        result.add(tmp);
                    }
                } else {
                    if (isSameDay(tmp.getDay(), from.getTime())) {
                        if (tmp.getDay().getHours() >= 4) {
                            result.add(tmp);
                        }
                    } else if (isSameDay(tmp.getDay(), till.getTime())) {
                        if (tmp.getDay().getHours() < 4) {
                            result.add(tmp);
                        }
                    } else {
                        result.add(tmp);
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " activities found!!");
            }

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ActivityDTO>)dtoManager.clone(result);
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityResponseType getActivityDataByWeek(final StaffDTO staff, final int year, final int week)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get activities by week: " + year + "-" + week);
        }
        Session hibernateSession = null;
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = getStaffById(staff.getId());
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final GregorianCalendar from = getFirstDayOfWeek(year, week);
            final GregorianCalendar till = getLastDayOfWeek(year, week);
            till.add(GregorianCalendar.DAY_OF_MONTH, 1);

            hibernateSession = dbManager.getSession();
            final List<Activity> res = hibernateSession.createCriteria(Activity.class)
                        .add(Restrictions.and(
                                    Restrictions.eq("staff", user),
                                    Restrictions.between("day", from.getTime(), till.getTime())))
                        .list();

            final List<Activity> result = new ArrayList<Activity>();
            for (final Activity tmp : res) {
                if (tmp.getKindofactivity() == ActivityDTO.ACTIVITY) {
                    if (!isSameDay(tmp.getDay(), till.getTime())) {
                        result.add(tmp);
                    }
                } else {
                    if (isSameDay(tmp.getDay(), from.getTime())) {
                        if (tmp.getDay().getHours() >= 4) {
                            result.add(tmp);
                        }
                    } else if (isSameDay(tmp.getDay(), till.getTime())) {
                        if (tmp.getDay().getHours() < 4) {
                            result.add(tmp);
                        }
                    } else {
                        result.add(tmp);
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " activities found!!");
            }

            final ActivityResponseType resp = new ActivityResponseType();

            resp.setActivities((List<ActivityDTO>)dtoManager.clone(result));
            resp.setHolidays(getHolidaysByWeek(year, week));

            for (final HolidayType tmp : resp.getHolidays()) {
                if (tmp.isHalfHoliday()) {
                    tmp.setHours(getHoursOfWorkPerWeek(user, tmp.getDate()) / 10);
                } else {
                    tmp.setHours(getHoursOfWorkPerWeek(user, tmp.getDate()) / 5);
                }
            }
            return resp;
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    @Override
    public ActivityResponseType getActivityDataByWeek(final StaffDTO staff,
            final Date firstDayOfWeek,
            final Date lastDayOfweek) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get activities by week: " + firstDayOfWeek.toString() + "-" + lastDayOfweek.toString());
        }
        Session hibernateSession = null;
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = getStaffById(staff.getId());
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final GregorianCalendar from = new GregorianCalendar();
            from.setTime(firstDayOfWeek);
            final GregorianCalendar till = new GregorianCalendar();
            till.setTime(lastDayOfweek);
            till.add(GregorianCalendar.DAY_OF_MONTH, 1);
            till.set(GregorianCalendar.HOUR_OF_DAY, 5);

            hibernateSession = dbManager.getSession();
            final List<Activity> res = hibernateSession.createCriteria(Activity.class)
                        .add(Restrictions.and(
                                    Restrictions.eq("staff", user),
                                    Restrictions.between("day", from.getTime(), till.getTime())))
                        .list();

            final List<Activity> result = new ArrayList<Activity>();
            for (final Activity tmp : res) {
                if (tmp.getKindofactivity() == ActivityDTO.ACTIVITY) {
                    if (!isSameDay(tmp.getDay(), till.getTime())) {
                        result.add(tmp);
                    }
                } else {
                    if (isSameDay(tmp.getDay(), from.getTime())) {
                        if (tmp.getDay().getHours() >= 4) {
                            result.add(tmp);
                        }
                    } else if (isSameDay(tmp.getDay(), till.getTime())) {
                        if (tmp.getDay().getHours() < 4) {
                            result.add(tmp);
                        }
                    } else {
                        result.add(tmp);
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " activities found!!");
            }

            final ActivityResponseType resp = new ActivityResponseType();

            resp.setActivities((List<ActivityDTO>)dtoManager.clone(result));
            resp.setHolidays(getHolidaysByWeek(firstDayOfWeek));

            for (final HolidayType tmp : resp.getHolidays()) {
                if (tmp.isHalfHoliday()) {
                    tmp.setHours(getHoursOfWorkPerWeek(user, tmp.getDate()) / 10);
                } else {
                    tmp.setHours(getHoursOfWorkPerWeek(user, tmp.getDate()) / 5);
                }
            }
            return resp;
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     * @param   day   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double getHoursOfWorkPerWeek(final Staff user, final Date day) {
        final Set<Contract> c = user.getContracts();

        if (c != null) {
            for (final Contract tmp : c) {
                if (((tmp.getTodate() == null) || isDateLessOrEqual(day, tmp.getTodate()))
                            && isDateLessOrEqual(tmp.getFromdate(), day)) {
                    return tmp.getWhow();
                }
            }
        }

        // return default value
        return 40;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityDTO getLastActivityForUser(final StaffDTO staff) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get last activity");
        }
        Session hibernateSession = null;
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff)dtoManager.merge(staff);
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            hibernateSession = dbManager.getSession();
            final Activity result = (Activity)hibernateSession.createCriteria(Activity.class)
                        .add(Restrictions.eq("staff", user))
                        .addOrder(Order.desc("day"))
                        .setMaxResults(1)
                        .uniqueResult();

            if (result
                        != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("activity with id " + result.getId() + " is last activity");
                }
            }
            // convert the server version of the Project type to the client version of the Project type
            if (result
                        != null) {
                return (ActivityDTO)dtoManager.clone(result);
            } else {
                return null;
            }
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityDTO> getLastActivitiesForUser(final StaffDTO staff) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get last activities");
        }
        long userId;
        final List<Activity> result = new ArrayList<Activity>();

        if (staff == null) {
            userId = getUserId();
        } else {
            userId = staff.getId();
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Statement s = dbManager.getDatabaseConnection().createStatement();
            final ResultSet rs = s.executeQuery(String.format(RECENT_ACTIVITIES_QUERY, userId, ActivityDTO.ACTIVITY));

            if (rs != null) {
                while (rs.next()) {
                    final long id = rs.getLong(1);
                    result.add((Activity)dbManager.getObject(Activity.class, id));
                }
            }

            return (List)dtoManager.clone(result);
//            List<Activity> result = hibernateSession.createCriteria(Activity.class).
//                                                     add(Restrictions.eq("staff", user)).
//                                                     add(Restrictions.eq("kindofactivity", ActivityDTO.ACTIVITY)).
//                                                     addOrder(Order.desc("id")).setMaxResults(5).list();
//
//            // convert the server version of the Project type to the client version of the Project type
//            if (result != null) {
//                return (ArrayList)dtoManager.clone(result);
//            } else {
//                return null;
//            }
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityDTO> getLastActivitiesExceptForUser(final StaffDTO staff) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get last activities ex");
        }
        long userId;
        final List<Activity> result = new ArrayList<Activity>();

        if (staff == null) {
            userId = getUserId();
        } else {
            userId = staff.getId();
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Statement s = dbManager.getDatabaseConnection().createStatement();
            final ResultSet rs = s.executeQuery(String.format(
                        RECENT_ACTIVITIES_EX_QUERY,
                        userId,
                        ActivityDTO.ACTIVITY));

            if (rs != null) {
                while (rs.next()) {
                    final long id = rs.getLong(1);
                    result.add((Activity)dbManager.getObject(Activity.class, id));
                }
            }

            return (List)dtoManager.clone(result);
//            List<Activity> result = hibernateSession.createCriteria(Activity.class).
//                                                     add(Restrictions.eq("staff", user)).
//                                                     add(Restrictions.eq("kindofactivity", ActivityDTO.ACTIVITY)).
//                                                     addOrder(Order.desc("id")).setMaxResults(5).list();
//
//            // convert the server version of the Project type to the client version of the Project type
//            if (result != null) {
//                return (ArrayList)dtoManager.clone(result);
//            } else {
//                return null;
//            }
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ActivityDTO> getActivitiesByProject(final StaffDTO staff, final ProjectDTO project)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get activities by project: " + project.getName());
        }

        return getActivitiesByCriteria(staff, project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ActivityDTO> getActivitiesByWorkPackage(final StaffDTO staff, final WorkPackageDTO workPackage)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get activities by workPackage: " + workPackage.getName());
        }
        return getActivitiesByCriteria(staff, workPackage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ActivityDTO> getActivitiesByWorkCategory(final StaffDTO staff, final WorkCategoryDTO workCategory)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get activities by WorkCategory: " + workCategory.getName());
        }
        return getActivitiesByCriteria(staff, workCategory);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   staff     the staff, whose activities should be retrieved
     * @param   criteria  an further criteria for the activities, which shluld be retrieved. This can be an object of
     *                    the type WorkPackage, WorkCategory or Project
     *
     * @return  all activities, which fulfil the given criterias
     *
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     * @throws  DataRetrievalException       DOCUMENT ME!
     * @throws  PermissionDenyException      DOCUMENT ME!
     * @throws  NoSessionException           DOCUMENT ME!
     */
    private ArrayList<ActivityDTO> getActivitiesByCriteria(final StaffDTO staff, Object criteria)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get activities: " + criteria.getClass().getName());
        }
        Session hibernateSession = null;
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff)dtoManager.merge(staff);
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            hibernateSession = dbManager.getSession();
            final Criteria crit = hibernateSession.createCriteria(Activity.class).add(Restrictions.eq("staff", user));

            if (criteria instanceof BasicDTO) {
                criteria = dtoManager.merge((BasicDTO)criteria);
            }
            if (criteria instanceof Project) {
                crit.createCriteria("workPackage").add(Restrictions.eq("project", criteria));
            } else if (criteria instanceof WorkPackage) {
                crit.add(Restrictions.eq("workPackage", criteria));
            } else if (criteria instanceof WorkCategory) {
                crit.add(Restrictions.eq("workCategory", criteria));
            } else {
                throw new DataRetrievalException("The criteria has a not supported type");
            }
            final List<Activity> result = crit.list();
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " activities found");
            }

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ActivityDTO>)dtoManager.clone(result);
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createActivity(final ActivityDTO activity) throws FullStopException,
        InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create activity");
        }
        final Activity act = (Activity)dtoManager.merge(activity);

        if (act.getStaff() == null) {
            act.setStaff(getStaffById(getUserId()));
        }
        checkUserOrAdminPermission(act.getStaff().getId());
        final DBManagerWrapper dbManager = new DBManagerWrapper();

//        checkActivityDate(activity);
        try {
            if ((activity.getKindofactivity() != ActivityDTO.BEGIN_OF_DAY)
                        && (activity.getKindofactivity() != ActivityDTO.END_OF_DAY)
                        && (activity.getKindofactivity() != ActivityDTO.LOCKED_DAY)
                        && (activity.getWorkPackage() == null)) {
                throw new DataRetrievalException(LanguageBundle.ACTIVITY_MUST_HAVE_A_PROJECTCOMPONENT);
            }

            if (act.getWorkCategory() == null) {
                act.setWorkCategory((WorkCategory)dbManager.getObject(WorkCategory.class, WorkCategoryDTO.WORK));
            }
            final List<Activity> actList = checkForMultiActivity(act.getDescription(), act);

            for (final Activity tmp : actList) {
                dbManager.createObject(act);
            }

            warningSystem.addActivity(act);
            final long id = (Long)dbManager.createObject(act);
            if ((act.getStaff() != null) && (act.getStaff().getId() != getUserId()) && (act.getDay() != null)) {
                sendChangedActivityEmail(act, null, act.getStaff().getEmail());
            }
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   description  DOCUMENT ME!
     * @param   act          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<Activity> checkForMultiActivity(final String description, final Activity act) {
        final List<Activity> acts = new ArrayList<Activity>();

        if (description == null) {
            return acts;
        }

        try {
            if (description.indexOf("(@") != -1) {
                String substr = description.substring(description.indexOf("(@") + 1);
                substr = substr.substring(0, substr.indexOf(")"));
                final StringTokenizer st = new StringTokenizer(substr, "@, ");

                while (st.hasMoreTokens()) {
                    final Staff s = getStaffByUsername(st.nextToken());

                    if (s != null) {
                        final Activity newAct = new Activity();
                        newAct.setDay(act.getDay());
                        newAct.setDescription(act.getDescription());
                        newAct.setKindofactivity(act.getKindofactivity());
                        newAct.setWorkPackage(act.getWorkPackage());
                        newAct.setWorkinghours(act.getWorkinghours());
                        newAct.setWorkCategory(act.getWorkCategory());
                        newAct.setStaff(s);

                        acts.add(newAct);
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("Error during parsing of " + description, t);
        }

        return acts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   activity  DOCUMENT ME!
     *
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     */
    private void checkActivityDate(final ActivityDTO activity) throws InvalidInputValuesException {
        if (activity.getWorkPackage() != null) {
            final WorkPackagePeriodDTO period = activity.getWorkPackage().determineMostRecentPeriod();

            if (period != null) {
                if (!isDateLessOrEqual(period.getFromdate(), activity.getDay())) {
                    throw new InvalidInputValuesException(
                        LanguageBundle.ACTIVITY_BEFORE_START_OF_THE_PROJECT_COMPONENT);
                } else if ((period.getTodate() != null) && !isDateLessOrEqual(activity.getDay(), period.getTodate())) {
                    throw new InvalidInputValuesException(LanguageBundle.ACTIVITY_AFTER_END_OF_THE_PROJECT_COMPONENT);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityDTO saveActivity(final ActivityDTO activity) throws FullStopException,
        InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save activity");
        }

        final Activity activityHib = (Activity)dtoManager.merge(activity);

        if (activityHib.getStaff() == null) {
            activityHib.setStaff(getStaffById(getUserId()));
        }
        checkUserOrAdminPermission(activityHib.getStaff().getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();
//        checkActivityDate(activity);

        try {
            warningSystem.saveActivity(activityHib);
            final Activity act = (Activity)dbManager.getObject(Activity.class, new Long(activityHib.getId()));
            final ActivityDTO tmp = (ActivityDTO)dtoManager.clone(act);
            final Activity origAct = (Activity)dtoManager.merge(tmp.createCopy());

            if (act.getCommitted()) {
                try {
                    checkAdminPermission();
                } catch (PermissionDenyException e) {
                    throw new PersistentLayerException(LanguageBundle.CANNOT_CHANGE_ACTIVITY);
                }
            }
            // to avoid the following error message: org.hibernate.NonUniqueObjectException: a different object with the
            // same identifier value was already associated with the session
            // activityHib.setWorkCategory((WorkCategory)dtoManager.merge( getWorkCategories().get(0)));
            final String actText = act.toString();
            activityHib.setReports(act.getReports());
            if (activityHib.getWorkCategory() == null) {
                activityHib.setWorkCategory((WorkCategory)dbManager.getObject(
                        WorkCategory.class,
                        WorkCategoryDTO.WORK));
            }

            dbManager.closeSession();
            dbManager = new DBManagerWrapper();
            dbManager.saveObject(activityHib);
            if ((activityHib.getStaff().getId() != getUserId()) && (activityHib.getDay() != null)) {
                sendChangedActivityEmail(activityHib, origAct, activityHib.getStaff().getEmail());
            }
            return activity;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  activity      The description of the original activity. If null, a new activity was created
     * @param  origActivity  DOCUMENT ME!
     * @param  email         DOCUMENT ME!
     */
    private void sendChangedActivityEmail(final Activity activity, final Activity origActivity, final String email) {
        try {
            if (email != null) {
                final Staff s = getStaffById(getUserId());
                String text;
                text = "<div class=\"container\">"
                            + "<div style=\"margin-bottom:15px\">";
                if (activity == null) {
                    text += s.getFirstname()
                                + " "
                                + s.getName()
                                + " has <b>deleted</b> the following actvitiy:</div>";
                    text += "<div style=\"float:left\">";
                    final EmailTaskNotice tn = new EmailTaskNotice(origActivity);
                    text += tn.toString();
                    text += "</div>";
                } else if (origActivity != null) {
                    text += s.getFirstname()
                                + " "
                                + s.getName()
                                + " has <b>changed</b> the following actvitiy:</div>";
                    text += "<div style=\"float:left\">"
                                + "<div>from</div>";
                    final EmailTaskNotice origTN = new EmailTaskNotice(origActivity);
                    text += origTN.toString();
                    text += "</div>";

                    text += "<div style=\"float:right\">"
                                + "<div>to</div>";
                    final EmailTaskNotice updatedTN = new EmailTaskNotice(activity);
                    text += updatedTN.toString();
                    text += "</div>";
                } else {
                    text += s.getFirstname()
                                + " "
                                + s.getName()
                                + " has <b>added</b> the following actvitiy:</div>";
                    text += "<div style=\"float:left\">";
                    final EmailTaskNotice tn = new EmailTaskNotice(activity);
                    text += tn.toString();
                    text += "</div>";
                }

                text += "</div>";
                Utilities.sendCollectedEmail(email, "Activity changed", text);
            } else {
                logger.warn("Cannot send the email, because there is no email address");
            }
        } catch (Exception e) {
            logger.error("Error while sending email.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteActivity(final ActivityDTO activity) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete activity");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        final Activity deletedActivity = (Activity)dtoManager.merge(activity.createCopy());
        try {
            final Activity act = (Activity)dbManager.getObject(Activity.class, new Long(activity.getId()));
            checkUserOrAdminPermission(act.getStaff().getId());

            if (act.getCommitted()) {
                try {
                    checkAdminPermission();
                } catch (PermissionDenyException e) {
                    throw new PersistentLayerException(LanguageBundle.CANNOT_CHANGE_ACTIVITY);
                }
            }

            String actText = null;
            Staff st = null;
            Date day = null;

            if (act.getDay() != null) {
                actText = act.toString();
                st = act.getStaff();
                day = act.getDay();
            }
            dbManager.deleteObject(act);

            if ((st != null) && (st.getId() != getUserId()) && (day != null)) {
                sendChangedActivityEmail(null, deletedActivity, st.getEmail());
            }
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportDTO> getReportsForActivities(final List<ActivityDTO> activityList)
            throws InvalidInputValuesException,
                DataRetrievalException,
                PersistentLayerException,
                PermissionDenyException,
                NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("getReportsForActivities");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        final List<ReportDTO> reports = new ArrayList<ReportDTO>();

        try {
            for (final ActivityDTO activity : activityList) {
                final Activity act = (Activity)dbManager.getObject(Activity.class, new Long(activity.getId()));
                if (act != null) {
                    checkUserOrAdminPermission(act.getStaff().getId());

                    final Set<Report> reportSet = act.getReports();
                    final List<ReportDTO> tmpReports = dtoManager.clone(new ArrayList(reportSet));

                    for (final ReportDTO rep : tmpReports) {
                        if (!reports.contains(rep)) {
                            reports.add(rep);
                        }
                    }
                }
            }
            return reports;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportDTO> getReportsForActivity(final ActivityDTO activity) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("getReportsForActivity");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Activity act = (Activity)dbManager.getObject(Activity.class, new Long(activity.getId()));
            checkUserOrAdminPermission(act.getStaff().getId());

            final Set<Report> reportSet = act.getReports();
            final List<ReportDTO> reports = dtoManager.clone(new ArrayList(reportSet));
            return reports;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectCategoryDTO saveProjectCategory(final ProjectCategoryDTO projectCategory)
            throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save project category");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final ProjectCategory cat = (ProjectCategory)dtoManager.merge(projectCategory);
            dbManager.saveObject(cat);

            return (ProjectCategoryDTO)dtoManager.clone(cat);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createProjectCategory(final ProjectCategoryDTO projectCategory) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create project category");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final long id = (Long)dbManager.createObject(dtoManager.merge(projectCategory));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProjectCategory(final ProjectCategoryDTO projectCategory) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete project category");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // convert the client version of the Project type to the server version of the Project type
            final ProjectCategory cat = (ProjectCategory)dtoManager.merge(projectCategory);
            final Project p = (Project)dbManager.getObjectByAttribute(Project.class, "projectCategory", cat);

            if (p
                        != null) {
                throw new PersistentLayerException(LanguageBundle.CANNOT_REMOVE_PROJECT_CATEGORY);
            }

            dbManager.deleteObject(cat);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkCategoryDTO saveWorkCategory(final WorkCategoryDTO workCategory) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save work category");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final WorkCategory cat = (WorkCategory)dtoManager.merge(workCategory);
            dbManager.saveObject(cat);

            return (WorkCategoryDTO)dtoManager.clone(cat);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createWorkCategory(final WorkCategoryDTO workCategory) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create work category");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final long id = (Long)dbManager.createObject(dtoManager.merge(workCategory));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWorkCategory(final WorkCategoryDTO workCategory) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete work category");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final WorkCategory wc = (WorkCategory)dtoManager.merge(workCategory);
            final Activity act = (Activity)dbManager.getObjectByAttribute(Activity.class, "workCategory", wc);

            if (act
                        == null) {
                dbManager.deleteObject(wc);
            } else {
                throw new PersistentLayerException(LanguageBundle.THE_WORK_CATEGORY_IS_USED_BY_ACTIVITIES);
            }
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<WorkCategoryDTO> getWorkCategories() throws InvalidInputValuesException,
        DataRetrievalException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get work categories");
        }
        checkSession();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<WorkCategory> result = (List<WorkCategory>)dbManager.getAllObjects(WorkCategory.class);
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " work categories found");
            }

            // convert the server version of the WorkCategory types to the client version of the WorkCategory types
            return (ArrayList<WorkCategoryDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    @Override
    public WorkCategoryDTO getWorkCategory(final long id) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get work category");
        }
        checkSession();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final WorkCategory result = (WorkCategory)dbManager.getObject(WorkCategory.class, id);

            // convert the server version of the WorkCategory type to the client version of the WorkCategory type
            return (WorkCategoryDTO)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ContractDocumentDTO> getContractDocuments(final ContractDTO contract)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get contract documents");
        }
        checkUserOrAdminPermission(contract.getStaff().getId());
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<ContractDocument> result = (List<ContractDocument>)dbManager.getObjectsByAttribute(
                    ContractDocument.class,
                    "contract",
                    dtoManager.merge(contract));

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ContractDocumentDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<TravelDocumentDTO> getTravelDocuments(final TravelDTO travel) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get contract documents");
        }
        checkUserOrAdminPermission(travel.getStaff().getId());
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<TravelDocumentDTO> result = (List<TravelDocumentDTO>)dbManager.getObjectsByAttribute(
                    TravelDocument.class,
                    "travel",
                    dtoManager.merge(travel));

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<TravelDocumentDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FundingDTO createFunding(final FundingDTO funding) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create funding");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Funding fund = (Funding)dtoManager.merge(funding);
            final long id = (Long)dbManager.createObject(fund);
            fund.setId(id);

            return (FundingDTO)dtoManager.clone(fund);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FundingDTO saveFunding(final FundingDTO funding) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("save funding");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Funding fund = (Funding)dtoManager.merge(funding);
            dbManager.saveObject(fund);

            return (FundingDTO)dtoManager.clone(fund);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<FundingDTO> getFundingsForCompany(final CompanyDTO company) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get fundings");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final List<Funding> result = (List<Funding>)dbManager.getObjectsByAttribute(
                    Funding.class,
                    "company",
                    dtoManager.merge(company));
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "fundings found "
                            + result.size());
            }

            return (ArrayList<FundingDTO>)dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ReportDTO> getAllCreatedReports(final String reportGenerator, final StaffDTO staff, final int year)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get all created reports");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Session hibernateSession = dbManager.getSession();

            final Criteria crit = hibernateSession.createCriteria(Report.class)
                        .add(Restrictions.eq("generatorname", reportGenerator));

            if (staff
                        != null) {
                crit.add(Restrictions.or(
                        Restrictions.eq("staff", dtoManager.merge(staff)),
                        Restrictions.isNull("staff")));
            }
            if (year
                        != 0) {
                final int dateObjectYear = year
                            - 1900;
                crit.add(Restrictions.between(
                        "creationtime",
                        new Date(dateObjectYear, 0, 1),
                        new Date(dateObjectYear, 11, 31, 23, 59, 59)));
            }
            final List<Report> result = crit.list();

            return dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * TODO: Methode umbenennen {@inheritDoc}
     *
     * @return  DOCUMENT ME!
     *
     * @throws  PermissionDenyException  DOCUMENT ME!
     * @throws  NoSessionException       DOCUMENT ME!
     */
    @Override
    public ArrayList<ReportType> getAllAvailableReports() throws PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get all available report plugins");
        }
        checkAdminPermission();
        final ArrayList<ReportType> reports = new ArrayList<ReportType>();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final ProjectTrackerReport[] reportArray = reportManager.getAvailableReports();
            for (final ProjectTrackerReport tmp : reportArray) {
                reports.add(new ReportType(
                        tmp.getReportName(),
                        tmp.supportUserSpecificreportGeneration(),
                        tmp.supportUserUnspecificreportGeneration()));
            }

            return reports;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<TravelDTO> getAllTravels(final long staffId, final long projectId, final int year)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get all travels");
        }
        if (staffId == 0) {
            checkAdminPermission();
        } else {
            checkUserOrAdminPermission(staffId);
        }

        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Session hibernateSession = dbManager.getSession();

            final Criteria crit = hibernateSession.createCriteria(Travel.class);

            if (staffId != 0) {
                final Staff st = new Staff();
                st.setId(staffId);
                crit.add(Restrictions.eq("staff", st));
            }

            if (projectId != 0) {
                final Project pro = new Project();
                pro.setId(projectId);
                crit.add(Restrictions.eq("project", pro));
            }

            if (year != 0) {
                final int dateObjectYear = year
                            - 1900;
                crit.add(Restrictions.between(
                        "date",
                        new Date(dateObjectYear, 0, 1),
                        new Date(dateObjectYear, 11, 31, 23, 59, 59)));
            }

            final List<Travel> result = crit.list();

            return dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReportDTO createReport(final String name,
            final Date start,
            final Date end,
            final long staffID,
            final String reportName) throws InvalidInputValuesException,
        ReportNotFoundException,
        PersistentLayerException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("create report");
        }
        checkAdminPermission();

        final GregorianCalendar startDate = new GregorianCalendar();
        final GregorianCalendar endDate = new GregorianCalendar();
        startDate.setTimeInMillis(start.getTime());
        endDate.setTimeInMillis(end.getTime());
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final long id = reportManager.createReport(name, startDate, endDate, staffID, reportName);
            final Report clientReport = (Report)dbManager.getObject(Report.class, id);

            return (ReportDTO)dtoManager.clone(clientReport);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            // translate the report plugin exception to the client exception. Only the exception, which are in the
            // client package can be sent to the server
            throw new DataRetrievalException(e);
        } catch (de.cismet.projecttracker.report.exceptions.ReportNotFoundException e) {
            // translate the ReportNotFoundException to the client exception. Only the exception, which are in the
            // client package can be sent to the server
            throw new ReportNotFoundException(e);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String checkReport(final Date start, final Date end, final long staffID, final String reportName)
            throws ReportNotFoundException, DataRetrievalException, PermissionDenyException, NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("check report");
        }
        checkAdminPermission();
        if (end.before(start)) {
            throw new DataRetrievalException(LanguageBundle.END_IS_BEFORE_START);
        }
        final ProjectTrackerReport report = reportManager.getReportByName(reportName);

        if (report != null) {
            final GregorianCalendar startDate = new GregorianCalendar();
            final GregorianCalendar endDate = new GregorianCalendar();
            startDate.setTimeInMillis(start.getTime());
            endDate.setTimeInMillis(end.getTime());

            try {
                if (staffID == 0) {
                    return report.checkForReport(startDate, endDate);
                } else {
                    return report.checkForReport(startDate, endDate, staffID);
                }
            } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
                throw new DataRetrievalException(e);
            }
        } else {
            throw new ReportNotFoundException(LanguageBundle.REPORT_PLUGIN_NOT_FOUND);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteReport(final ReportDTO report) throws InvalidInputValuesException,
        DataRetrievalException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete report");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // SQLQuery query = hibernateSession.createSQLQuery("delete from activity_report where reportid=" +
            // report.getId()); query.executeUpdate();

            final Report result = (Report)dbManager.getObject(Report.class, report.getId());

            dbManager.deleteObject(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContractDocument(final ContractDocumentDTO document) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete contract document");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            dbManager.deleteObject(dtoManager.merge(document));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTravelDocument(final TravelDocumentDTO document) throws InvalidInputValuesException,
        PersistentLayerException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("delete travel document");
        }
        checkAdminPermission();
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            dbManager.deleteObject(dtoManager.merge(document));
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StaffDTO login(final String username, final String pasword) throws LoginFailedException,
        DataRetrievalException {
        Session hibernateSession = null;
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            hibernateSession = dbManager.getSession();
            if (logger.isDebugEnabled()) {
                logger.debug(username + " sends login request");
            }
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(pasword.getBytes());

//            Staff staff = (Staff) hibernateSession.createCriteria(Staff.class).add(Restrictions.eq("username", username)).uniqueResult();
            final Staff staff = (Staff)hibernateSession.createCriteria(Staff.class)
                        .add(Restrictions.and(
                                    Restrictions.eq("username", username),
                                    Restrictions.eq("password", md.digest())))
                        .uniqueResult();

            if (staff
                        != null) {
                final HttpSession session = getThreadLocalRequest().getSession();
                final SessionInformation sessionInfo = new SessionInformation();
                sessionInfo.setCurrentUser(staff);
                session.setAttribute("sessionInfo", sessionInfo);

                // persistentes Cookie anlegen
                final Cookie sessionCookie = new Cookie("JSESSIONID", getThreadLocalRequest().getSession().getId());
                sessionCookie.setMaxAge(60 * 60 * 24 * 100);
                sessionCookie.setPath(getThreadLocalRequest().getContextPath());
                getThreadLocalResponse().addCookie(sessionCookie);

                return (StaffDTO)dtoManager.clone(staff);
            } else {
                throw new LoginFailedException(LanguageBundle.LOGIN_FAILED);
            }
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    @Override
    public StaffDTO checkLogin() throws DataRetrievalException {
        try {
            final HttpSession session = getThreadLocalRequest().getSession();
            final SessionInformation sessionInfo = (SessionInformation)session.getAttribute("sessionInfo");
            if (sessionInfo != null) {
                return (StaffDTO)dtoManager.clone(sessionInfo.getCurrentUser());
            } else {
                return null;
            }
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout() {
        try {
            final HttpSession session = getThreadLocalRequest().getSession();

            if (session != null) {
                session.invalidate();
            }
        } catch (Throwable th) {
            if (logger.isDebugEnabled()) {
                logger.debug("error during the logout", th);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFirstReportYear() throws DataRetrievalException {
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Object result = dbManager.getObject("select min(fromdate) from Report as rep");

            if ((result != null) && (result instanceof Timestamp)) {
                final GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis(((Timestamp)result).getTime());
                return gc.get(GregorianCalendar.YEAR);
            } else {
                return (new GregorianCalendar()).get(GregorianCalendar.YEAR);
            }
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFirstTravelYear() throws DataRetrievalException {
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Object result = dbManager.getObject("select min(date) from Travel as trav");

            if ((result != null) && (result instanceof Timestamp)) {
                final GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis(((Timestamp)result).getTime());
                return gc.get(GregorianCalendar.YEAR);
            } else {
                return (new GregorianCalendar()).get(GregorianCalendar.YEAR);
            }
        } finally {
            dbManager.closeSession();
        }
    }

    @Override
    public Double getHoursOfWork(final StaffDTO staff, final Date day) throws DataRetrievalException,
        NoSessionException,
        InvalidInputValuesException,
        PermissionDenyException {
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff)dtoManager.merge(staff);
        }

        if (user.getId() != getUserId()) {
            checkAdminPermission();
        }

        final TimetrackerQuery query = new TimetrackerQuery();
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(day.getTime());

        try {
            final HoursOfWork how = query.getHoursOfWork(user, cal);
            if (how != null) {
                return how.getHours();
            } else {
                return 0.0;
            }
        } catch (final UserNotFoundException e) {
            return 0.0;
        }
    }

    @Override
    public Date getStartOfWork(final StaffDTO staff, final Date day) throws DataRetrievalException,
        NoSessionException,
        InvalidInputValuesException,
        PermissionDenyException {
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff)dtoManager.merge(staff);
        }

        if (user.getId() != getUserId()) {
            checkAdminPermission();
        }

        final TimetrackerQuery query = new TimetrackerQuery();
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(day.getTime());

        try {
            final GregorianCalendar res = query.getStartOfWork(user, cal);

            if (res != null) {
                return res.getTime();
            } else {
                return null;
            }
        } catch (final UserNotFoundException e) {
            return null;
        }
    }

    @Override
    public TimePeriod getStartEndOfWork(final StaffDTO staff, final Date day) throws DataRetrievalException,
        NoSessionException,
        InvalidInputValuesException,
        PermissionDenyException {
        final TimePeriod result = new TimePeriod();
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff)dtoManager.merge(staff);
        }

        if (user.getId() != getUserId()) {
            checkAdminPermission();
        }

        final TimetrackerQuery query = new TimetrackerQuery();
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(day.getTime());

        try {
            GregorianCalendar res = query.getStartOfWork(user, cal);

            if (res != null) {
                result.setStart(res.getTime());
            }
            final Date now = new Date();

            if (!isSameDay(day, now)) {
                res = query.getEndOfWork(user, cal);

                if (res != null) {
                    result.setEnd(res.getTime());
                }
            }
        } catch (final UserNotFoundException e) {
            return result;
        }

        return result;
    }

    @Override
    public boolean isDataChanged() throws DataRetrievalException,
        NoSessionException,
        InvalidInputValuesException,
        PermissionDenyException,
        PersistentLayerException {
        if (logger.isDebugEnabled()) {
            logger.debug("getStaffById");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Staff s = (Staff)dbManager.getObject(Staff.class, getUserId());

            if (s.getLastmodification() != null) {
                s.setLastmodification(null);
                saveStaff((StaffDTO)dtoManager.clone(s));

                return true;
            } else {
                return false;
            }
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date       DOCUMENT ME!
     * @param   otherDate  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static boolean isSameDay(final Date date, final Date otherDate) {
        if ((date == null) && (otherDate == null)) {
            return true;
        } else if ((date == null) || (otherDate == null)) {
            return false;
        }

        return ((date.getYear() == otherDate.getYear())
                        && (date.getMonth() == otherDate.getMonth())
                        && (date.getDate() == otherDate.getDate()));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date1  DOCUMENT ME!
     * @param   date2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static boolean isDateLessOrEqual(final Date date1, final Date date2) {
        final int firstDate = ((date1.getYear() + 1900) * 10000)
                    + (date1.getMonth() * 100)
                    + date1.getDate();
        final int secondDate = ((date2.getYear() + 1900) * 10000)
                    + (date2.getMonth() * 100)
                    + date2.getDate();

        return (firstDate <= secondDate);
    }

    @Override
    public List<HolidayType> getHolidaysByWeek(final int year, final int week) throws InvalidInputValuesException,
        DataRetrievalException {
        final GregorianCalendar cal = getFirstDayOfWeek(year, week);
        final HolidayEvaluator eval = new HolidayEvaluator();
        final List<HolidayType> holidays = new ArrayList<HolidayType>();

        do {
            final int holTmp = eval.isHoliday(cal);

            if (holTmp != HolidayEvaluator.WORKDAY) {
                final HolidayType holiday = new HolidayType();
                holiday.setDate(cal.getTime());
                holiday.setHalfHoliday(holTmp == HolidayEvaluator.HALF_HOLIDAY);
                holiday.setName(eval.getNameOfHoliday(cal));

                holidays.add(holiday);
            }
            cal.add(GregorianCalendar.DATE, 1);
        } while (cal.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY);

        return holidays;
    }

    @Override
    public List<HolidayType> getHolidaysByWeek(final Date firstDayOfWeek) throws InvalidInputValuesException,
        DataRetrievalException {
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(firstDayOfWeek);
        final HolidayEvaluator eval = new HolidayEvaluator();
        final List<HolidayType> holidays = new ArrayList<HolidayType>();

        do {
            final int holTmp = eval.isHoliday(cal);

            if (holTmp != HolidayEvaluator.WORKDAY) {
                final HolidayType holiday = new HolidayType();
                holiday.setDate(cal.getTime());
                holiday.setHalfHoliday(holTmp == HolidayEvaluator.HALF_HOLIDAY);
                holiday.setName(eval.getNameOfHoliday(cal));

                holidays.add(holiday);
            }
            cal.add(GregorianCalendar.DATE, 1);
        } while (cal.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY);

        return holidays;
    }

    /**
     * checks if the current user has admin rights.
     *
     * @throws  PermissionDenyException  if the current user has no admin rights
     * @throws  NoSessionException       DOCUMENT ME!
     */
    private void checkAdminPermission() throws PermissionDenyException, NoSessionException {
        try {
            final SessionInformation sessionInfo = getCurrentSession();

            if (!sessionInfo.isAdmin()) {
                throw new PermissionDenyException(LanguageBundle.ONLY_ALLOWED_FOR_ADMIN);
            }
        } catch (NoSessionException e) {
            throw e;
        } catch (Throwable th) {
            throw new PermissionDenyException(LanguageBundle.ONLY_ALLOWED_FOR_ADMIN);
        }
    }

    /**
     * this method throws an exception, if the current user has no admin rights and the given user is not the current
     * user.
     *
     * @param   userId  the id of the user data that should be changed
     *
     * @throws  PermissionDenyException  DOCUMENT ME!
     * @throws  NoSessionException       DOCUMENT ME!
     */
    private void checkUserOrAdminPermission(final long userId) throws PermissionDenyException, NoSessionException {
        try {
            final long signedUser = getUserId();
            final SessionInformation sessionInfo = getCurrentSession();

            if ((signedUser != userId) && !sessionInfo.isAdmin()) {
                throw new PermissionDenyException(LanguageBundle.ONLY_ALLOWED_FOR_ADMIN);
            }
        } catch (NoSessionException e) {
            throw e;
        } catch (Throwable th) {
            throw new PermissionDenyException(LanguageBundle.ONLY_ALLOWED_FOR_ADMIN);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  NoSessionException  DOCUMENT ME!
     */
    private void checkSession() throws NoSessionException {
        getCurrentSession();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the username of the current user
     *
     * @throws  NoSessionException  DOCUMENT ME!
     */
    private long getUserId() throws NoSessionException {
        final SessionInformation sessionInfo = getCurrentSession();

        return sessionInfo.getCurrentUser().getId();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NoSessionException  DOCUMENT ME!
     */
    private SessionInformation getCurrentSession() throws NoSessionException {
        final HttpSession session = getThreadLocalRequest().getSession(false);
        if (session == null) {
            throw new NoSessionException();
        }

        final SessionInformation sessionInfo = (SessionInformation)session.getAttribute("sessionInfo");

        if (sessionInfo == null) {
            throw new NoSessionException();
        }

        return sessionInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    private Staff getStaffById(final long id) throws DataRetrievalException {
        if (logger.isDebugEnabled()) {
            logger.debug("getStaffById");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Staff result = (Staff)dbManager.getObject(Staff.class, id);

            return result;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   username  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    private Staff getStaffByUsername(final String username) throws DataRetrievalException {
        if (logger.isDebugEnabled()) {
            logger.debug("getStaffByUsername");
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Staff result = (Staff)dbManager.getObjectByAttribute(Staff.class, "username", username);

            return result;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   year  DOCUMENT ME!
     * @param   week  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private GregorianCalendar getFirstDayOfWeek(final int year, final int week) {
        final GregorianCalendar from = new GregorianCalendar(year, 0, 1, 0, 0, 0);
        from.setMinimalDaysInFirstWeek(4);
        from.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        from.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
        from.set(GregorianCalendar.WEEK_OF_YEAR, week);

        final String time = CalendarHelper.toDateString(from);
        return from;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   year  DOCUMENT ME!
     * @param   week  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private GregorianCalendar getLastDayOfWeek(final int year, final int week) {
        final GregorianCalendar till = new GregorianCalendar(year, 0, 1, 23, 59, 59);
        till.setMinimalDaysInFirstWeek(4);
        till.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        till.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SUNDAY);
        till.set(GregorianCalendar.WEEK_OF_YEAR, week);

        final String time = CalendarHelper.toDateString(till);
        return till;
    }

    @Override
    protected boolean shouldCompressResponse(final HttpServletRequest request,
            final HttpServletResponse response,
            final String responsePayload) {
        return true;
    }

    @Override
    public Double getAccountBalance(final StaffDTO staff) throws DataRetrievalException,
        NoSessionException,
        PermissionDenyException {
        double realWorkingTime = 0;
        double nominalWorkingTime = 0;
        // calculate the nominal working days to determine the nominal working time
        long nominalWorkingDays = 0;
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        checkUserOrAdminPermission(staff.getId());

        try {
            final Session hibernateSession = dbManager.getSession();
            final List contracts = hibernateSession.createCriteria(Contract.class)
                        .add(Restrictions.eq("staff.id", staff.getId()))
                        .list();
            final Iterator it = contracts.iterator();

            // iterate through all contracts and sum the real and the nominal working time
            while (it.hasNext()) {
                final Contract contract = (Contract)it.next();
                final GregorianCalendar contractToDateCal = new GregorianCalendar();
                // if the contract is unlimeted calculate till now
                final GregorianCalendar contractFromDateCal = new GregorianCalendar();

                if ((contract.getTodate() != null)
                            && (contract.getTodate().getTime() < contractToDateCal.getTimeInMillis())) {
                    contractToDateCal.setTime(contract.getTodate());
                }

                if (contractToDateCal.getTime().before(accountBalanceDueDate.getTime())) {
                    continue;
                } else if (contract.getFromdate().before(accountBalanceDueDate.getTime())) {
                    contractFromDateCal.setTime(accountBalanceDueDate.getTime());
                } else {
                    contractFromDateCal.setTime(contract.getFromdate());
                }

                contractToDateCal.set(GregorianCalendar.HOUR_OF_DAY, 0);
                contractToDateCal.set(GregorianCalendar.MINUTE, 0);
                contractToDateCal.set(GregorianCalendar.SECOND, 0);
                contractFromDateCal.set(GregorianCalendar.HOUR_OF_DAY, 0);
                contractFromDateCal.set(GregorianCalendar.MINUTE, 0);
                contractFromDateCal.set(GregorianCalendar.SECOND, 0);

                final double whow = contract.getWhow();
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                // calculate the real working time exclude pause and freizeitausgleich activities
                final Statement s = dbManager.getDatabaseConnection().createStatement();
                ResultSet rs = s.executeQuery(String.format(
                            REAL_WORKING_TIME_QUERY,
                            ""
                                    + (whow / 5),
                            ""
                                    + staff.getId(),
                            dateFormatter.format(contractFromDateCal.getTime()),
                            dateFormatter.format(contractToDateCal.getTime())));
                if (rs != null) {
                    while (rs.next()) {
                        realWorkingTime += rs.getDouble(1);
                    }
                }

                // include holiday and illness activities
                rs = s.executeQuery(String.format(
                            REAL_WORKING_TIME_ILLNESS_AND_HOLIDAY,
                            ""
                                    + (whow / 5),
                            ""
                                    + staff.getId(),
                            dateFormatter.format(contractFromDateCal.getTime()),
                            dateFormatter.format(contractToDateCal.getTime())));
                if (rs != null) {
                    while (rs.next()) {
                        realWorkingTime += rs.getDouble(1);
                    }
                }

                // calculate the nominal working time.
                nominalWorkingDays = calculateNominalWorkingDays(contractFromDateCal, contractToDateCal);
                nominalWorkingTime += (whow / 5)
                            * nominalWorkingDays;
            }
        } catch (SQLException ex) {
            logger.error("Error during determining Real_Working_Time. AccountBalance may not be correct", ex);
            throw new DataRetrievalException(ex.getMessage());
        } finally {
            dbManager.closeSession();
        }
        final Double result = (realWorkingTime - nominalWorkingTime);

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   from  DOCUMENT ME!
     * @param   to    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int calculateNominalWorkingDays(final GregorianCalendar from, final GregorianCalendar to) {
        final GregorianCalendar fromDay = (GregorianCalendar)from.clone();
        int days = 0;

        while (CalendarHelper.isDateLess(fromDay, to)) {
            days += CalendarHelper.isWorkingDayExactly(fromDay);
            fromDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
        }

        return days;
    }

    @Override
    public List<ActivityDTO> getFavouriteActivities(final StaffDTO staff) throws NoSessionException,
        DataRetrievalException {
        if (logger.isDebugEnabled()) {
            logger.debug("get favourite activities");
        }
        long userId;
        final List<Activity> result = new ArrayList<Activity>();

        if (staff == null) {
            userId = getUserId();
        } else {
            userId = staff.getId();
        }
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            final Statement s = dbManager.getDatabaseConnection().createStatement();
            final ResultSet rs = s.executeQuery(String.format(FAVOURITE_ACTIVITIES_QUERY, userId));

            if (rs != null) {
                while (rs.next()) {
                    final long id = rs.getLong(1);
                    result.add((Activity)dbManager.getObject(Activity.class, id));
                }
            }

            return (List)dtoManager.clone(result);
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    @Override
    public Boolean isExisitingFavouriteTask(final ActivityDTO activity) {
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        try {
            final Statement s = dbManager.getDatabaseConnection().createStatement();
            final ResultSet rs = s.executeQuery(String.format(
                        FAVOURITE_EXISTS_QUERY,
                        ""
                                + activity.getStaff().getId(),
                        ""
                                + activity.getWorkPackage().getId(),
                        activity.getDescription()));

            if ((rs != null) && !rs.first()) {
                return false;
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(FavouriteTaskStory.class.getName())
                    .log(
                        Level.SEVERE,
                        "Error while checking"
                        + " if facourite task already exists. Drop action cancelled",
                        ex);
        } finally {
            dbManager.closeSession();
        }

        return true;
    }

    @Override
    public Boolean checkBeginOfDayActivityExists(final StaffDTO staff) {
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        try {
            final Statement s = dbManager.getDatabaseConnection().createStatement();
            final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            final String query = String.format(
                    CHECK_BEGIN_OF_DAY_QUERY,
                    staff.getId(),
                    dateFormatter.format(new Date()));
            ResultSet rs = s.executeQuery(query);

            // check if there exists at least one begin of day activity
            if (rs != null) {
                while (rs.next()) {
                    if (rs.getDate(1) == null) {
                        return false;
                    }
                }
            }
            rs = s.executeQuery(String.format(
                        CHECK_KIND_OF_LAST_ACTIVITY,
                        staff.getId(),
                        dateFormatter.format(new Date())));

            // check if the last activity for this day is a end of day activity
            if (rs != null) {
                while (rs.next()) {
                    if (rs.getInt(1) == ActivityDTO.END_OF_DAY) {
                        final Date helper = new Date();
                        final Date now = new Date(helper.getYear(),
                                helper.getMonth(),
                                helper.getDate(),
                                helper.getHours(),
                                helper.getMinutes());
                        final Timestamp endOfDay = rs.getTimestamp("day");
                        if (endOfDay.before(now)) {
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName())
                    .log(Level.SEVERE, "Error while checking if begin_of_day activity exists", ex);
        } finally {
            dbManager.closeSession();
        }
        return true;
    }

    @Override
    public Boolean isDayLocked(final Date day, final StaffDTO s) {
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        if (day == null) {
            // favourite tasks have null as day
            return false;
        }
        // TODO: change Time Restriction to same day and not same time...
        final Date d = new Date(day.getTime());
        d.setHours(5);
        d.setMinutes(0);
        d.setSeconds(0);

        Activity lockedDayActivity = null;
        Criteria crit = null;
        try {
            crit = dbManager.getSession().createCriteria(Activity.class)
                        .add(Restrictions.and(
                                    Restrictions.eq("staff", dtoManager.merge(s)),
                                    Restrictions.and(
                                        Restrictions.eq("kindofactivity", ActivityDTO.LOCKED_DAY),
                                        Restrictions.eq("day", d))))
                        .setMaxResults(1);
            lockedDayActivity = (Activity)crit.uniqueResult();
        } catch (InvalidInputValuesException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            dbManager.closeSession();
        }

        if (lockedDayActivity != null) {
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<ActivityDTO> getActivityByDay(final StaffDTO staff, final Date day)
            throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        final ArrayList<ActivityDTO> resultList = new ArrayList<ActivityDTO>();
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        Criteria crit = null;
        try {
            final GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(day);
            cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
            final Criterion dateRestriction = Restrictions.and(Restrictions.ge("day", day),
                    Restrictions.lt("day", cal.getTime()));
            crit = dbManager.getSession().createCriteria(Activity.class)
                        .add(Restrictions.and(Restrictions.eq("staff", dtoManager.merge(staff)), dateRestriction));
            crit.addOrder(Order.asc("day"));
            final List list = crit.list();
            final Iterator it = list.iterator();

            while (it.hasNext()) {
                final Activity activity = (Activity)it.next();
                resultList.add((ActivityDTO)dtoManager.clone(activity));
            }
        } catch (InvalidInputValuesException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            dbManager.closeSession();
        }
        return resultList;
    }

    @Override
    public Boolean isPausePolicyFullfilled(final StaffDTO staff, final Date day) {
        try {
            final ArrayList<ActivityDTO> activities = getActivityByDay(staff, day);
            Date lastBeginActivityTime = null;
            Date lastEndActivityTime = null;
            boolean pauseActivityNeeded = false;
            double pauseTimesFromActivity = 0;
            double activityWorkingHours = 0;
            // the time beetween two time slots
            double slotPauseTime = 0;
            boolean justAbsenceTasks = true;
            for (final ActivityDTO act : activities) {
                if (act.getKindofactivity() == ActivityDTO.BEGIN_OF_DAY) {
                    lastBeginActivityTime = act.getDay();
                    if (lastEndActivityTime != null) {
                        slotPauseTime += (act.getDay().getTime() - lastEndActivityTime.getTime())
                                    / 1000
                                    / 60
                                    / 60d;
                    }
                } else if (act.getKindofactivity() == ActivityDTO.END_OF_DAY) {
                    lastEndActivityTime = act.getDay();
                    if (lastBeginActivityTime != null) {
                        final long workingTimeSlot = act.getDay().getTime()
                                    - lastBeginActivityTime.getTime();
                        if (workingTimeSlot > (6 * 60 * 60 * 1000)) {
                            pauseActivityNeeded = true;
                        }
                    }
                } else if (act.getKindofactivity() == ActivityDTO.ACTIVITY) {
                    final long wpId = act.getWorkPackage().getId();
                    if (wpId == ActivityDTO.PAUSE_ID) {
                        pauseTimesFromActivity += act.getWorkinghours();
                    } else if (!((wpId == ActivityDTO.ILLNESS_ID) || (wpId == ActivityDTO.SPARE_TIME_ID)
                                    || (wpId == ActivityDTO.HOLIDAY_ID)
                                    || (wpId == ActivityDTO.LECTURE_ID)
                                    || (wpId == ActivityDTO.SPECIAL_HOLIDAY_ID))) {
                        activityWorkingHours += act.getWorkinghours();
                        justAbsenceTasks = false;
                    }
                }
            }

            // if there are only absence tasks like spare time and holiday the pause policy is always fulfilled
            if (justAbsenceTasks) {
                return true;
            } else {
//                //there is a time slot greate than 6 hours so check if a there is a pause activity of at least 45 min
//                if (pauseActivityNeeded) {
//                    if (pauseTimesFromActivity >= 0.75d) {
//                        return true;
//                    } else {
//                        return false;
//                    }
//                } else {
                if (activityWorkingHours > 6d) {
                    if ((pauseTimesFromActivity + slotPauseTime) >= 0.75d) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
//                }
            }
        } catch (InvalidInputValuesException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataRetrievalException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PermissionDenyException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSessionException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    @Override
    public ArrayList<Date> isPausePolicyFullfilled(final StaffDTO staff, final int year, final int week) {
        final GregorianCalendar from = getFirstDayOfWeek(year, week);
        final GregorianCalendar till = getLastDayOfWeek(year, week);
//        till.add(GregorianCalendar.DAY_OF_MONTH, 1);
        final ArrayList<Date> faultyDays = new ArrayList<Date>();
        for (int i = 0; i < 7; i++) {
            final boolean dayFullfilled = isPausePolicyFullfilled(staff, from.getTime());
            if (!dayFullfilled) {
//                return false;
                faultyDays.add(from.getTime());
            }
            from.add(GregorianCalendar.DAY_OF_MONTH, 1);
        }

        return faultyDays;
    }

    @Override
    public ArrayList<Date> isPausePolicyFullfilled(final StaffDTO staff, final Date from, final Date till) {
        final ArrayList<Date> faultyDays = new ArrayList<Date>();
        final GregorianCalendar fromCal = new GregorianCalendar();
        fromCal.setTime(from);
        while (fromCal.getTime().compareTo(till) < 0) {
            final boolean check = isPausePolicyFullfilled(staff, fromCal.getTime());
            if (!check) {
                faultyDays.add(fromCal.getTime());
            }
            fromCal.add(GregorianCalendar.DAY_OF_MONTH, 1);
        }
        return faultyDays;
    }

    @Override
    public Double getVacationDaysTaken(final Date d, final StaffDTO staff) {
        final Date firstDayInYear = new Date(d.getYear(), 0, 1);
        return getVacationDays(firstDayInYear, d, staff);
    }

    @Override
    public Double getVacationDaysPlanned(final Date d, final StaffDTO staff) {
        final Date lastDayInYear = new Date(d.getYear(), 11, 31);

        return getVacationDays(d, lastDayInYear, staff);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   from   DOCUMENT ME!
     * @param   to     DOCUMENT ME!
     * @param   staff  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ArrayList<ContractDTO> getContractforIntervall(final Date from, final Date to, final StaffDTO staff) {
        final ArrayList<ContractDTO> result = new ArrayList<ContractDTO>();

        final ArrayList<ContractDTO> contracts = staff.getContracts();

        for (final ContractDTO c : contracts) {
            if ((c.getFromdate().compareTo(from) >= 0) && (c.getFromdate().compareTo(to) < 0)) {
                result.add(c);
            } else if ((c.getTodate() == null) || c.getTodate().after(to)) {
                if (c.getFromdate().compareTo(from) <= 0) {
                    result.add(c);
                    break;
                }
            } else if ((c.getTodate() != null) && c.getTodate().after(from) && c.getTodate().before(to)) {
                result.add(c);
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   from   DOCUMENT ME!
     * @param   to     DOCUMENT ME!
     * @param   staff  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Double getVacationDays(final Date from, final Date to, final StaffDTO staff) {
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        final ArrayList<ContractDTO> contracts = getContractforIntervall(from, to, staff);
        double result = 0;

        for (final ContractDTO c : contracts) {
            final double whPerWeek = c.getWhow();
            Date f;
            Date t;

            if (c.getFromdate().before(from)) {
                if ((c.getTodate() != null) && c.getTodate().before(to)) {
                    // calculate from from til c.getTodate()
                    f = from;
                    t = c.getTodate();
                } else {
                    // calculate from from til to
                    f = from;
                    t = to;
                }
            } else {
                if (c.getFromdate().before(to) && (c.getTodate() != null) && c.getTodate().before(to)) {
                    // calculate from c.getFromDate til c.getTodate
                    f = c.getFromdate();
                    t = c.getTodate();
                } else {
                    // calculate from c.getFromDate til to
                    f = c.getFromdate();
                    t = to;
                }
            }

            try {
                final Statement s = dbManager.getDatabaseConnection().createStatement();
                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                final ResultSet rs = s.executeQuery(String.format(
                            COUNT_VACATION_DAYS,
                            staff.getId(),
                            df.format(f),
                            df.format(t)));

                if (rs != null) {
                    while (rs.next()) {
                        final double wh = rs.getDouble("workinghours");
                        if (wh == 0) {
                            result += 1;
                        } else {
                            result += wh
                                        / (whPerWeek / 5);
                        }
                    }
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                dbManager.closeSession();
            }
        }
        return Math.rint(result * 2)
                    / 2;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   from      DOCUMENT ME!
     * @param   to        DOCUMENT ME!
     * @param   contract  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double getPartialVacationDays(final Date from, final Date to, final ContractDTO contract) {
        final long difference = to.getTime()
                    - from.getTime();
        final double days = difference
                    / 1000
                    / 60
                    / 60
                    / 24;
        final double vacationDaysPerYear = contract.getVacation();

        return (days * vacationDaysPerYear)
                    / 365;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   year   DOCUMENT ME!
     * @param   staff  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double getTotalVacationDaysForYear(final Date year, final StaffDTO staff) {
        final Date firstDayYearBefore = new Date(year.getYear(), 0, 1);
        final Date lastDayYearBefore = new Date(year.getYear(), 11, 31);
        final ArrayList<ContractDTO> contracts = getContractforIntervall(firstDayYearBefore, lastDayYearBefore, staff);
        double totalVacationDays = 0;
        if (contracts.size() > 1) {
            for (final ContractDTO c : contracts) {
                if (c.getFromdate().before(firstDayYearBefore)) {
                    // calculate from firstDayYeaqrBefore til c.getToDate
                    totalVacationDays += getPartialVacationDays(firstDayYearBefore, c.getTodate(), c);
                } else {
                    if ((c.getTodate() != null) && c.getTodate().before(lastDayYearBefore)) {
                        // calculate from c.getFrom til c.getTo
                        totalVacationDays += getPartialVacationDays(c.getFromdate(), c.getTodate(), c);
                    } else {
                        // calculate from c.getFrom til lastDayYearBefore
                        totalVacationDays += getPartialVacationDays(c.getFromdate(), lastDayYearBefore, c);
                    }
                }
            }
        } else if (contracts.size() == 1) {
            totalVacationDays = contracts.get(0).getVacation();
        }
        return Math.rint(totalVacationDays * 2)
                    / 2;
    }

    @Override
    public Double getVacationCarryOver(final Date year, final StaffDTO staff) {
        final Date firstDayYearBefore = new Date(year.getYear() - 1, 0, 1);
        final Date lastDayYearBefore = new Date(year.getYear() - 1, 11, 31);
        double totalVacationDays = getTotalVacationDaysForYear(new Date(year.getYear() - 1, 0, 1), staff);
        // the total vacation can contain old residual vacation from the year before
        if (((year.getYear() + 1900) - 1) == 2012) {
            final ProfileDTO profile = staff.getProfile();
            if (profile != null) {
                totalVacationDays += profile.getResidualVacation();
            }
        } else {
            totalVacationDays += getVacationCarryOver(new Date(year.getYear() - 1, year.getMonth(), year.getDay()),
                    staff);
        }
        return totalVacationDays
                    - getVacationDays(firstDayYearBefore, lastDayYearBefore, staff);
    }

    @Override
    public List<Date> getUnlockedDays(final StaffDTO staff) {
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        final ArrayList<Date> result = new ArrayList<Date>();
        try {
            final Statement s = dbManager.getDatabaseConnection().createStatement();
            final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            final ResultSet rs = s.executeQuery(String.format(UNLOCKED_DAYS, staff.getId()));

            if (rs != null) {
                while (rs.next()) {
                    final Date d = rs.getDate(1);
                    result.add(d);
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            dbManager.closeSession();
        }
        return result;
    }

    @Override
    public List<ActivityDTO> getVacationActivitiesTaken(final Date d, final StaffDTO staff) {
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        Session hibernateSession = null;
        try {
            hibernateSession = dbManager.getSession();

            final WorkPackage wp = (WorkPackage)dbManager.getObject(WorkPackage.class, 409);
            final WorkPackageDTO wpDTO = (WorkPackageDTO)dtoManager.clone(wp);

            final Criterion dateRestriction = Restrictions.and(Restrictions.ge("day", new Date(d.getYear(), 0, 1)),
                    Restrictions.le("day", d));
            final Criterion wpRestriction = Restrictions.and(Restrictions.eq("staff.id", staff.getId()),
                    Restrictions.eq("workPackage.id", wpDTO.getId()));
            final Criteria crit = hibernateSession.createCriteria(Activity.class)
                        .add(Restrictions.and(wpRestriction, dateRestriction));
            crit.addOrder(Property.forName("day").asc());
            final List<Activity> result = crit.list();
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " activities found");
            }

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ActivityDTO>)dtoManager.clone(result);
        } catch (DataRetrievalException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidInputValuesException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            dbManager.closeSession();
        }
        return null;
    }

    @Override
    public List<ActivityDTO> getVacationActivitiesPlanned(final Date d, final StaffDTO staff) {
        final DBManagerWrapper dbManager = new DBManagerWrapper();
        Session hibernateSession = null;
        try {
            hibernateSession = dbManager.getSession();

            final WorkPackage wp = (WorkPackage)dbManager.getObject(WorkPackage.class, 409);
            final WorkPackageDTO wpDTO = (WorkPackageDTO)dtoManager.clone(wp);

            final Criterion dateRestriction = Restrictions.and(Restrictions.ge("day", d),
                    Restrictions.le("day", new Date(d.getYear(), 11, 31)));
            final Criterion wpRestriction = Restrictions.and(Restrictions.eq("staff.id", staff.getId()),
                    Restrictions.eq("workPackage.id", wpDTO.getId()));
            final Criteria crit = hibernateSession.createCriteria(Activity.class)
                        .add(Restrictions.and(wpRestriction, dateRestriction));

            final List<Activity> result = crit.list();
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " activities found");
            }

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ActivityDTO>)dtoManager.clone(result);
        } catch (DataRetrievalException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidInputValuesException ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            dbManager.closeSession();
        }
        return null;
    }

    @Override
    public double getTotalVacationForYear(final StaffDTO staff, final Date year) {
        return getTotalVacationDaysForYear(year, staff);
    }


    /**
     * if one of the parameters is null this parameter doesnt gets taken into account for filtering
     */
    @Override
    public ArrayList<ActivityDTO> getActivites(List<WorkPackageDTO> workpackages, List<StaffDTO> staff, Date from, Date til, String description) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        final ArrayList<ActivityDTO> result = new ArrayList<ActivityDTO>();
        DBManagerWrapper dbManager = new DBManagerWrapper();
        Session hibernateSession = null;
        Transaction tx = null;
        final Conjunction conjuction = Restrictions.conjunction();
        if (workpackages != null && !workpackages.isEmpty()) {
            final ArrayList<Long> wpIds = new ArrayList<Long>();
            for (WorkPackageDTO wp : workpackages) {
                wpIds.add(wp.getId());
            }
            conjuction.add(Restrictions.in("workPackage.id", wpIds));
        }
        
        if(staff != null && !staff.isEmpty()){
            final ArrayList<Long> staffIds = new ArrayList<Long>();
            for(StaffDTO s : staff){
                staffIds.add(s.getId());
            }
            conjuction.add(Restrictions.in("staff.id", staffIds));
        }
        conjuction.add(Restrictions.isNotNull("day"));
        if(from != null && til != null && from.compareTo(til)<0){
            conjuction.add(Restrictions.ge("day", from));
            conjuction.add(Restrictions.le("day", til));
        }
        
        if(description!=null){
            conjuction.add(Restrictions.ilike("description", description, MatchMode.ANYWHERE));
        }
        
        try {
            hibernateSession = dbManager.getSession();
            tx = hibernateSession.beginTransaction();
//            Criterion wpRestriction = Restrictions.in("workPackage.id", wpIds);
            Criteria crit = hibernateSession.createCriteria(Activity.class).
                    add((conjuction));
            result.addAll(dtoManager.clone(crit.list()));
            tx.commit();
        } 
        catch (Exception ex) {
            java.util.logging.Logger.getLogger(ProjectServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            tx.rollback();
        } 
        finally {
            dbManager.closeSession();
        }
        return result;
    }
}

