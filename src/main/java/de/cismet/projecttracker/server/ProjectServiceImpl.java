package de.cismet.projecttracker.server;

import de.cismet.projecttracker.utilities.DBManagerWrapper;
import de.cismet.projecttracker.utilities.DTOManager;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
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
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.types.ActivityResponseType;
import de.cismet.projecttracker.client.types.HolidayType;
import de.cismet.projecttracker.client.types.ReportType;
import de.cismet.projecttracker.client.types.TimePeriod;
import de.cismet.projecttracker.report.ProjectTrackerReport;
import de.cismet.projecttracker.report.ReportManager;
import de.cismet.projecttracker.report.commons.HolidayEvaluator;
import de.cismet.projecttracker.report.db.entities.Activity;
import de.cismet.projecttracker.report.db.entities.Company;
import de.cismet.projecttracker.report.db.entities.Contract;
import de.cismet.projecttracker.report.db.entities.ContractDocument;
import de.cismet.projecttracker.report.db.entities.CostCategory;
import de.cismet.projecttracker.report.db.entities.EstimatedComponentCost;
import de.cismet.projecttracker.report.db.entities.EstimatedComponentCostMonth;
import de.cismet.projecttracker.report.db.entities.Funding;
import de.cismet.projecttracker.report.db.entities.Project;
import de.cismet.projecttracker.report.db.entities.ProjectBody;
import de.cismet.projecttracker.report.db.entities.ProjectCategory;
import de.cismet.projecttracker.report.db.entities.ProjectCosts;
import de.cismet.projecttracker.report.db.entities.RealOverhead;
import de.cismet.projecttracker.report.db.entities.Report;
import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.db.entities.Travel;
import de.cismet.projecttracker.report.db.entities.TravelDocument;
import de.cismet.projecttracker.report.db.entities.WorkCategory;
import de.cismet.projecttracker.report.db.entities.WorkPackage;
import de.cismet.projecttracker.report.exceptions.UserNotFoundException;
import de.cismet.projecttracker.report.helper.CalendarHelper;
import de.cismet.projecttracker.utilities.LanguageBundle;
import de.cismet.projecttracker.utilities.Utilities;
import de.cismet.projecttracker.report.timetracker.TimetrackerQuery;
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
import org.hibernate.criterion.*;

/**
 *
 * @author therter
 */
public class ProjectServiceImpl extends RemoteServiceServlet implements ProjectService {

    private static final Logger logger = Logger.getLogger(ProjectServiceImpl.class);
    private static final String RECENT_ACTIVITIES_QUERY = "select max(id), workpackageid, description from activity where "
            + "staffid = %1$s and kindofactivity = %2$s group by workpackageid, description having workpackageid <> 408 "
            + "order by max(id) desc limit 30;";
    private static final String FAVOURITE_ACTIVITIES_QUERY = "select max(id), workpackageid, description from activity where "
            + "staffid = %1$s and day is null group by workpackageid, description";
    private static final String RECENT_ACTIVITIES_EX_QUERY = "select max(id), workpackageid, description from activity where "
            + "staffid <> %1$s and kindofactivity = %2$s group by workpackageid, description having workpackageid <> 408 "
            + "order by max(id) desc limit 30;";
    private static final String REAL_WORKING_TIME_QUERY = "SELECT sum(CASE WHEN workcategoryid = 4 THEN workinghours / 2 ELSE workinghours END)  FROM activity WHERE staffid = %2$s AND day >= '%3$s' AND day < '%4$s' AND workpackageid NOT IN (234, 407,408 ,409, 410,411,414);";
//    private static final String TRAVEL_TIME_QUERY = "SELECT sum(coalesce(nullif(workinghours, 0),%1$s))  FROM activity WHERE staffid = %2$s AND day >= '%3$s' AND day < '%4$s' AND workpackageid = 414";
    private static final String REAL_WORKING_TIME_ILLNESS_AND_HOLIDAY = "SELECT sum(coalesce(nullif(workinghours, 0),%1$s))  FROM activity WHERE staffid = %2$s AND day >= '%3$s' AND day < '%4$s' AND workpackageid IN (409,410,411);";
   private static final String FAVOURITE_EXISTS_QUERY = "select description, staffid, workpackageid from activity where "
           + "staffid=%1$s and day is null and workpackageid = %2$s and "
           + "case when description is null then true else description = '%3$s' end and staffid=16;";
    private static ReportManager reportManager;
    private static WarningSystem warningSystem;
    private static boolean initialised = false;
    private static DTOManager dtoManager = new DTOManager();
    private static final GregorianCalendar accountBalanceDueDate = new GregorianCalendar(2012, 2, 1);

    @Override
    public void init() throws ServletException {
        super.init();

        if (!initialised) {
            initialised = true;
            Utilities.initLogger(getServletContext().getRealPath("/"));
            logger.debug("init PersistentBeanManager");
            warningSystem = new WarningSystem();

            // check if the language resource bundle contains all required messages
            try {
                LanguageBundle.class.newInstance();
            } catch (Exception e) {
                logger.error("the language resource bundle is not complete.", e);
            }

            reportManager = new ReportManager(getServletContext().getRealPath("/"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectShortDTO> getAllOngoingProjects() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkAdminPermission();
        logger.debug("get all ongoing projects");
        DBManagerWrapper dbManager = new DBManagerWrapper();
        try {
            List<Project> result = dbManager.getObjectsByAttribute("select proj from Project as proj left join proj.projectPeriods as period where period.id in (Select pp.id from ProjectPeriod as pp where pp.asof in (select max(pper.asof) as masof from ProjectPeriod as pper group by pper.project)) and (period.todate=null or period.todate>current_timestamp) or period=null");

            // convert the server version of the Project type to the client version of the Project type
            ArrayList<ProjectShortDTO> clonedList = new ArrayList<ProjectShortDTO>();

            for (Project o : result) {
                ProjectDTO pro = ((ProjectDTO) dtoManager.clone(o));
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
    public ArrayList<ProjectShortDTO> getAllCompletedProjects() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkAdminPermission();
        logger.debug("get all completed projects");
        DBManagerWrapper dbManager = new DBManagerWrapper();
        try {
            List<Project> result = dbManager.getObjectsByAttribute("select proj from Project as proj left join proj.projectPeriods as period where period.id in (Select pp.id from ProjectPeriod as pp where pp.asof in (select max(pper.asof) as masof from ProjectPeriod as pper group by pper.project)) and period.todate<current_timestamp");

            // convert the server version of the Project type to the client version of the Project type
            ArrayList<ProjectShortDTO> clonedList = new ArrayList<ProjectShortDTO>();

            for (Project o : result) {
                ProjectDTO pro = ((ProjectDTO) dtoManager.clone(o));
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
    public ArrayList<StaffDTO> getCurrentEmployees() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkSession();
        logger.debug("get current employees");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<Staff> result = dbManager.getObjectsByAttribute("select distinct staff from Staff as staff left join staff.contracts as contract where contract.todate=null or contract.todate>current_timestamp");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<StaffDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<StaffDTO> getAllFormerEmployees() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkSession();
        logger.debug("get all former employees");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<Staff> result = dbManager.getObjectsByAttribute("select distinct staff from Staff as staff left join staff.contracts as contract where contract.todate!=null and contract.todate<=current_timestamp and staff.id not in (select staff1.id from Staff as staff1 left join staff1.contracts as contract1 where contract1.todate=null or contract1.todate>current_timestamp)");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<StaffDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<StaffDTO> getAllEmployees() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkSession();
        logger.debug("get alle mployees");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<Staff> result = (List<Staff>) dbManager.getAllObjects(Staff.class);

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<StaffDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectShortDTO> getAllProjects() throws InvalidInputValuesException, DataRetrievalException, NoSessionException {
        checkSession();
        logger.debug("get all projects");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<Project> result = (List<Project>) dbManager.getAllObjects(Project.class);

            // convert the server version of the Project type to the client version of the Project type
            ArrayList<ProjectShortDTO> clonedList = new ArrayList<ProjectShortDTO>();

            for (Project o : result) {
                ProjectDTO pro = ((ProjectDTO) dtoManager.clone(o));
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
    public ArrayList<ProjectCategoryDTO> getAllProjectCategories() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkAdminPermission();
        logger.debug("get all project categories");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<ProjectCategory> result = (List<ProjectCategory>) dbManager.getAllObjects(ProjectCategory.class);
            logger.debug(result.size() + " project categories found");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ProjectCategoryDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ProjectDTO> getAllProjectsFull() throws InvalidInputValuesException, DataRetrievalException, NoSessionException {
        checkSession();
        logger.debug("get all projects full");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<Project> result = (List<Project>) dbManager.getAllObjects(Project.class);

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ProjectDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProject(long id) throws InvalidInputValuesException, PersistentLayerException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("delete project");
        checkAdminPermission();
        Project project = null;
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            project = (Project) dbManager.getObject(Project.class, id);

            Query query = dbManager.getSession().createQuery("select proj from Project as proj inner join proj.workPackages as wp inner join wp.activityWorkPackages as activity where proj.id=" + id);
            Object result = query.setMaxResults(1).uniqueResult();

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
    public ProjectDTO getProject(long projectId) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get project with id " + projectId);
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Project result = (Project) dbManager.getObject(Project.class, projectId);

            // convert the server version of the Project type to the client version of the Project type
            return (ProjectDTO) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectDTO saveProject(ProjectDTO project) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save project");
        checkAdminPermission();

        DBManagerWrapper dbManager = new DBManagerWrapper();


        try {
            if (project.getProjectPeriods() != null) {
                for (ProjectPeriodDTO pp : project.getProjectPeriods()) {
                    if (pp.getAsof() == null) {
                        pp.setAsof(new Date());
                    }
                }
            }

            ProjectPeriodDTO projectPeriod = project.determineMostRecentPeriod();

            if (projectPeriod != null) {
                for (WorkPackageDTO tmp : project.getWorkPackages()) {
                    WorkPackagePeriodDTO period = tmp.determineMostRecentPeriod();
                    if (period != null) {
                        if (period.getFromdate().before(projectPeriod.getFromdate())) {
                            throw new InvalidInputValuesException(LanguageBundle.PROJECT_START_BEFORE_PROJECT_COMPONENT_START + " " + tmp.getAbbreviation());
                        }
                        if (period.getTodate() != null && projectPeriod.getTodate() != null && period.getTodate().after(projectPeriod.getTodate())) {
                            throw new InvalidInputValuesException(LanguageBundle.PROJECT_END_AFTER_PROJECT_COMPONENT_END + " " + tmp.getAbbreviation());
                        }
                    }
                }
            }
            // convert the client version of the Project type to the server version of the Project type
            Project projectHib = (Project) dtoManager.merge(project);

            //TODO wird das if noch benoetigt
            // if no parent project is set, the project object refers to an
            // parent project with the id 0. This leads to an hibernate failure
            if (projectHib.getProject() != null && projectHib.getProject().getId() == 0) {
                System.out.println("projectHib.setProject(null); wird benötigt");
                projectHib.setProject(null);
            }

            dbManager.saveObject(projectHib);

            // the project object must be returned, so that the client side has
            // the generated IDs of the new objects
            return (ProjectDTO) dtoManager.clone(projectHib);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectShortDTO createProject() throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create project");
        checkAdminPermission();
        logger.debug("create new project");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Project project = new Project();
            project.setName("Project" + (new Date()).getTime());

            // set body
            ProjectBody body = getAnyProjectBody(dbManager);
            if (body == null) {
                body = new ProjectBody();
                body.setName("dummyBody");
                Set<Project> projects = new HashSet<Project>();
                projects.add(project);
                body.setProjects(projects);
                //save the body in the db
                long id = (Long) dbManager.createObject(body);
                body.setId(id);
            }
            project.setProjectBody(body);

            long projectId = (Long) dbManager.createObject(project);
            project.setId(projectId);

            logger.debug("project id of the new project: " + projectId);
            ProjectDTO newProject = (ProjectDTO) dtoManager.clone(project);

            return newProject.toShortVersion();
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    public WorkPackageDTO getWorkpackageData(long workpackageId) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkAdminPermission();
        logger.debug("getProjectData for project id: " + workpackageId);
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            WorkPackage result = (WorkPackage) dbManager.getObject(WorkPackage.class, workpackageId);

            // convert the server version of the Project type to the client version of the Project type
            return (WorkPackageDTO) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkPackageDTO saveWorkPackage(WorkPackageDTO workpackage) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save work package");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // set the timestamps of the new periods
            if (workpackage.getWorkPackagePeriods() != null) {
                for (WorkPackagePeriodDTO tmp : workpackage.getWorkPackagePeriods()) {
                    if (tmp.getAsof() == null) {
                        // the period is a new period, so the asOf value will be set
                        ProjectPeriodDTO period = workpackage.getProject().determineMostRecentPeriod();
                        tmp.setAsof(new Date());

                        // checks if the new time period of the work package is within the current project period
                        if (period != null && tmp.getFromdate().before(period.getFromdate())) {
                            throw new InvalidInputValuesException(LanguageBundle.WORK_PACKAGE_START_BEFORE_PROJECT_START);
                        } else if (period != null && period.getTodate() != null && tmp.getTodate() != null
                                && tmp.getTodate().after(period.getTodate())) {
                            throw new InvalidInputValuesException(LanguageBundle.WORK_PACKAGE_END_AFTER_PROJECT_END);
                        }
                    }
                }
            }

            // set the timestamps of the new progresses
            if (workpackage.getWorkPackageProgresses() != null) {
                for (WorkPackageProgressDTO tmp : workpackage.getWorkPackageProgresses()) {
                    if (tmp.getTime() == null) {
                        tmp.setTime(new Date());
                    }
                }
            }

            // check if there exists any activity that is not within the project period
            WorkPackagePeriodDTO period = workpackage.determineMostRecentPeriod();
            WorkPackage wp = (WorkPackage) dtoManager.merge(workpackage);
            Criterion expression;

            if (period.getTodate() != null) {
                expression = Restrictions.or(Restrictions.gt("day", period.getTodate()),
                        Restrictions.lt("day", period.getFromdate()));
            } else {
                expression = Restrictions.lt("day", period.getFromdate());
            }

            Criteria crit = dbManager.getSession().createCriteria(Activity.class).add(
                    Restrictions.and(Restrictions.eq("workPackage", wp), expression)).setMaxResults(1);
            Activity act = (Activity) crit.uniqueResult();

            if (act != null) {
                if (act.getDay().before(period.getFromdate())) {
                    throw new InvalidInputValuesException(LanguageBundle.ACTIVITY_BEFORE_PERIOD_START);
                } else {
                    throw new InvalidInputValuesException(LanguageBundle.ACTIVITY_AFTER_PERIOD_END);
                }
            }

            dbManager.saveObject(wp);

            return (WorkPackageDTO) dtoManager.clone(wp);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkPackageDTO createWorkPackage(WorkPackageDTO workpackage) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create work package");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            WorkPackage wp = (WorkPackage) dtoManager.merge(workpackage);

            if (wp.getCostCategory() == null) {
                wp.setCostCategory(getAnyCostCategory(wp.getProject(), dbManager));
            }

            long id = (Long) dbManager.createObject(wp);
            wp.setId(id);

            // the workpackage object must be returned, so that the client side has
            // the generated IDs of the new objects
            workpackage = (WorkPackageDTO) dtoManager.clone(wp);

            return workpackage;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWorkPackage(WorkPackageDTO object) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete work package");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            WorkPackage wp = (WorkPackage) dbManager.getObject(WorkPackage.class, object.getId());

            if (wp.getActivityWorkPackages() != null && wp.getActivityWorkPackages().size() > 0) {
                throw new PersistentLayerException(LanguageBundle.EXISTING_ACTIVITIES_FOR_PC_FOUND);
            }

            dbManager.deleteObject(wp);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * @param project
     * @return any cost category for the given project. If the given project have no cost category, a new one will be
     * created.
     */
    private CostCategory getAnyCostCategory(Project project, DBManagerWrapper dbManager) throws DataRetrievalException, PersistentLayerException {
        if (project != null) {
            logger.debug("get any cost category");

            CostCategory result = (CostCategory) dbManager.getObjectByAttribute(CostCategory.class, "project", project);
            logger.debug((result == null ? "no" : "any") + " cost category found");

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
     * @return any project body. If no one exists, null will be returned
     */
    private ProjectBody getAnyProjectBody(DBManagerWrapper dbManager) throws DataRetrievalException {
        logger.debug("get any project body");
        ProjectBody result = (ProjectBody) dbManager.getAnyObjectByClass(ProjectBody.class);

        logger.debug((result == null ? "no" : "any") + " project body found");

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createStaff(StaffDTO staff) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create staff");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            long id = (Long) dbManager.createObject(dtoManager.merge(staff));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StaffDTO saveStaff(StaffDTO staff) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save staff");
        checkUserOrAdminPermission(staff.getId());

        try {
            Staff user = getStaffByUsername(staff.getUsername());
            SessionInformation sessionInfo = getCurrentSession();

            if (user != null && user.getId() != staff.getId()) {
                throw new PersistentLayerException(LanguageBundle.USERNAME_ALREADY_EXISTS);
            }

            if (!sessionInfo.isAdmin()) {
                staff.setPermissions(sessionInfo.getCurrentUser().getPermissions());
            }
        } catch (DataRetrievalException e) {
            logger.error(e.getMessage(), e);
            throw new PersistentLayerException(e.getMessage());
        }

        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            for (ContractDTO c : staff.getContracts()) {
                if (c.getId() != 0) {
                    dbManager.saveObject(dtoManager.merge(c));
                } else {
                    // convert the client version of the Project type to the server version of the Project type
                    Contract contract = (Contract) dtoManager.merge(c);
                    long id = (Long) dbManager.createObject(contract);
                    c.setId(id);
                }
            }

            Staff staffHib = (Staff) dtoManager.merge(staff);
            // the password must be copied to the staff object that should be saved, because the password
            // was never sent to the client-side and so the received staff object doesn't contain the password
            Staff original = getStaffById(staff.getId());
            staffHib.setPassword(original.getPassword());
            dbManager.saveObject(staffHib);
            return (StaffDTO) dtoManager.clone(staffHib);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteStaff(StaffDTO staff) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete staff");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

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
    public TravelDTO createTravelExpenseReport(TravelDTO travel) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create travel expense report");
        checkUserOrAdminPermission(travel.getStaff().getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            travel.setDate(new Date());
            Travel travelHib = (Travel) dtoManager.merge(travel);
            dbManager.createObject(travelHib);
            return (TravelDTO) dtoManager.clone(travelHib);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TravelDTO saveTravelExpenseReport(TravelDTO travel) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save travel expense report");
        checkUserOrAdminPermission(travel.getStaff().getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // TODO: Reise darf nicht mehr verändert werden, wenn sie bereits bezahlt wurde
            Travel travelHib = (Travel) dtoManager.merge(travel);
            dbManager.saveObject(travelHib);

            return (TravelDTO) dtoManager.clone(travelHib);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTravelExpenseReport(TravelDTO travel) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete travel expense report");
        checkUserOrAdminPermission(travel.getStaff().getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            //TODO serverseitig abfragen, ob paymentday gesetzt ist
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
    public void changePassword(StaffDTO staff, String newPassword) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("change password");
        checkUserOrAdminPermission(staff.getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Staff staffHib = getStaffById(staff.getId());
            MessageDigest md = MessageDigest.getInstance("SHA1");
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
    public ArrayList<CompanyDTO> getCompanies() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkSession();
        logger.debug("get companies");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<Company> result = dbManager.getAllObjects(Company.class);
            logger.debug(result.size() + " companies found");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<CompanyDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<EstimatedComponentCostDTO> getEstimatedWorkPackageCostForWP(WorkPackageDTO workPackage) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        checkAdminPermission();
        logger.debug("get estimated work package cost");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<EstimatedComponentCost> result = dbManager.getObjectsByAttribute(EstimatedComponentCost.class, "workPackage", dtoManager.merge(workPackage));

            logger.debug(result.size() + " estimations found");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<EstimatedComponentCostDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstimatedComponentCostMonthDTO saveEstimatedWorkPackageCostMonth(EstimatedComponentCostMonthDTO estimation) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save estimation month");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            EstimatedComponentCostMonth est = (EstimatedComponentCostMonth) dtoManager.merge(estimation);
            dbManager.saveObject(est);

            return (EstimatedComponentCostMonthDTO) dtoManager.clone(est);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstimatedComponentCostMonthDTO createEstimatedWorkPackageCostMonth(EstimatedComponentCostMonthDTO estimation) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create estimation month");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            EstimatedComponentCostMonth est = (EstimatedComponentCostMonth) dtoManager.merge(estimation);
            long id = (Long) dbManager.createObject(est);
            est.setId(id);

            return (EstimatedComponentCostMonthDTO) dtoManager.clone(est);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EstimatedComponentCostDTO createEstimatedWorkPackageCost(EstimatedComponentCostDTO estimation) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create estimation");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            estimation.setCreationtime(new Date());
            EstimatedComponentCost est = (EstimatedComponentCost) dtoManager.merge(estimation);
            long id = (Long) dbManager.createObject(est);
            est.setId(id);

            return (EstimatedComponentCostDTO) dtoManager.clone(est);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEstimatedWorkPackageCost(EstimatedComponentCostDTO estimation) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete estimation");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

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
    public void deleteContract(ContractDTO contract) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete contract");
        checkUserOrAdminPermission(contract.getStaff().getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();

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
    public long createCompany(CompanyDTO company) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create company");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            long id = (Long) dbManager.createObject(dtoManager.merge(company));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompanyDTO saveCompany(CompanyDTO company) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save company");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // convert the client version of the Company object to the server version of the Company object
            Company comp = (Company) dtoManager.merge(company);

            for (RealOverhead overhead : comp.getRealOverheads()) {
                if (overhead.getId() != 0) {
                    dbManager.saveObject(overhead);
                } else {
                    long id = (Long) dbManager.createObject(overhead);
                    overhead.setId(id);
                }
            }

            dbManager.saveObject(comp);

            return (CompanyDTO) dtoManager.clone(comp);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCompany(CompanyDTO company) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete company");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Company comp = (Company) dbManager.getObject(Company.class, company.getId());

            if (comp.getContracts() == null || comp.getContracts().size() == 0) {
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
    public void deleteRealOverhead(RealOverheadDTO overhead) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete real overhead");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

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
    public ArrayList<ProjectBodyDTO> getProjectBodies() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get project bodies");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<ProjectBodyDTO> result = dbManager.getAllObjects(ProjectBody.class);
            logger.debug(result.size() + " project bodies found");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ProjectBodyDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createProjectBody(ProjectBodyDTO projectBody) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create project bodies");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            long id = (Long) dbManager.createObject(dtoManager.merge(projectBody));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectBodyDTO saveProjectBody(ProjectBodyDTO projectBody) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save project bodies");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            ProjectBody body = (ProjectBody) dtoManager.merge(projectBody);
            dbManager.saveObject(body);
            return (ProjectBodyDTO) dtoManager.clone(body);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProjectBody(ProjectBodyDTO projectBody) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete project bodies");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // convert the client version of the Project type to the server version of the Project type
            ProjectBody body = (ProjectBody) dtoManager.merge(projectBody);
            Project pro = (Project) dbManager.getObjectByAttribute(Project.class, "projectBody", body);

            if (pro != null) {
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
    public long createCostCategory(CostCategoryDTO costCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create cost category");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            long id = (Long) dbManager.createObject(dtoManager.merge(costCategory));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CostCategoryDTO saveCostCategory(CostCategoryDTO costCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save cost category");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            CostCategory cat = (CostCategory) dtoManager.merge(costCategory);
            dbManager.saveObject(cat);
            return (CostCategoryDTO) dtoManager.clone(cat);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCostCategory(CostCategoryDTO costCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete cost category");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

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
    public long createProjectComponentTag(ProjectComponentTagDTO tag) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create project component tag");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            long id = (Long) dbManager.createObject(dtoManager.merge(tag));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProjectComponentTag(ProjectComponentTagDTO tag) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete project component tag");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

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
    public long createProjectCosts(ProjectCostsDTO projectCosts) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create project costs");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            long id = (Long) dbManager.createObject(dtoManager.merge(projectCosts));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectCostsDTO saveProjectCosts(ProjectCostsDTO projectCosts) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save project costs");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            ProjectCosts costs = (ProjectCosts) dtoManager.merge(projectCosts);
            dbManager.saveObject(costs);

            return (ProjectCostsDTO) dtoManager.clone(costs);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProjectCosts(ProjectCostsDTO projectCosts) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete project costs");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

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
    public ArrayList<ProjectCostsDTO> getProjectCostsByProject(ProjectDTO project) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get project costs");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<ProjectCostsDTO> result = dbManager.getObjectsByAttribute(ProjectCosts.class, "project", dtoManager.merge(project));
            logger.debug(result.size() + " project costs found");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ProjectCostsDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ActivityDTO> getActivitiesByWeek(StaffDTO staff, int year, int week) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get activities by week: " + year + "-" + week);
        Session hibernateSession = null;
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff) dtoManager.merge(staff);
        }
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            GregorianCalendar from = getFirstDayOfWeek(year, week);
            GregorianCalendar till = getLastDayOfWeek(year, week);
            till.add(GregorianCalendar.DAY_OF_MONTH, 1);
//            from.set(GregorianCalendar.HOUR_OF_DAY, 4);
//            from.set(GregorianCalendar.MINUTE, 1);
//            till.set(GregorianCalendar.HOUR_OF_DAY, 4);
//            Timestamp timeFrom = new Timestamp(from.getTimeInMillis());
//            Timestamp timeTill = new Timestamp(till.getTimeInMillis());

            hibernateSession = dbManager.getSession();
            List<Activity> res = hibernateSession.createCriteria(Activity.class).
                    add(Restrictions.and(Restrictions.eq("staff", user), Restrictions.between("day", from.getTime(), till.getTime()))).
                    list();
            List<Activity> result = new ArrayList<Activity>();

            for (Activity tmp : res) {
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

            logger.debug(result.size() + " activities found!!");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ActivityDTO>) dtoManager.clone(result);
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
    public ActivityResponseType getActivityDataByWeek(StaffDTO staff, int year, int week) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get activities by week: " + year + "-" + week);
        Session hibernateSession = null;
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = getStaffById(staff.getId());
        }
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            GregorianCalendar from = getFirstDayOfWeek(year, week);
            GregorianCalendar till = getLastDayOfWeek(year, week);
            till.add(GregorianCalendar.DAY_OF_MONTH, 1);

            hibernateSession = dbManager.getSession();
            List<Activity> res = hibernateSession.createCriteria(Activity.class).
                    add(Restrictions.and(Restrictions.eq("staff", user), Restrictions.between("day", from.getTime(), till.getTime()))).
                    list();

            List<Activity> result = new ArrayList<Activity>();

            for (Activity tmp : res) {
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

            logger.debug(result.size() + " activities found!!");

            ActivityResponseType resp = new ActivityResponseType();

            resp.setActivities((List<ActivityDTO>) dtoManager.clone(result));
            resp.setHolidays(getHolidaysByWeek(year, week));

            for (HolidayType tmp : resp.getHolidays()) {
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

    private double getHoursOfWorkPerWeek(Staff user, Date day) {
        Set<Contract> c = user.getContracts();

        if (c != null) {
            for (Contract tmp : c) {
                if ((tmp.getTodate() == null || isDateLessOrEqual(day, tmp.getTodate()))
                        && isDateLessOrEqual(tmp.getFromdate(), day)) {
                    return tmp.getWhow();
                }
            }
        }

        //return default value
        return 40;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityDTO getLastActivityForUser(StaffDTO staff) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get last activity");
        Session hibernateSession = null;
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff) dtoManager.merge(staff);
        }
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            hibernateSession = dbManager.getSession();
            Activity result = (Activity) hibernateSession.createCriteria(Activity.class).
                    add(Restrictions.eq("staff", user)).
                    addOrder(Order.desc("day")).setMaxResults(1).uniqueResult();

            if (result != null) {
                logger.debug("activity with id " + result.getId() + " is last activity");
            }
            // convert the server version of the Project type to the client version of the Project type
            if (result != null) {
                return (ActivityDTO) dtoManager.clone(result);
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
    public List<ActivityDTO> getLastActivitiesForUser(StaffDTO staff) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get last activities");
        long userId;
        List<Activity> result = new ArrayList<Activity>();

        if (staff == null) {
            userId = getUserId();
        } else {
            userId = staff.getId();
        }
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Statement s = dbManager.getDatabaseConnection().createStatement();
            ResultSet rs = s.executeQuery(String.format(RECENT_ACTIVITIES_QUERY, userId, ActivityDTO.ACTIVITY));

            if (rs != null) {
                while (rs.next()) {
                    long id = rs.getLong(1);
                    result.add((Activity) dbManager.getObject(Activity.class, id));
                }
            }

            return (List) dtoManager.clone(result);
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
    public List<ActivityDTO> getLastActivitiesExceptForUser(StaffDTO staff) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get last activities ex");
        long userId;
        List<Activity> result = new ArrayList<Activity>();

        if (staff == null) {
            userId = getUserId();
        } else {
            userId = staff.getId();
        }
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Statement s = dbManager.getDatabaseConnection().createStatement();
            ResultSet rs = s.executeQuery(String.format(RECENT_ACTIVITIES_EX_QUERY, userId, ActivityDTO.ACTIVITY));

            if (rs != null) {
                while (rs.next()) {
                    long id = rs.getLong(1);
                    result.add((Activity) dbManager.getObject(Activity.class, id));
                }
            }

            return (List) dtoManager.clone(result);
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
    public ArrayList<ActivityDTO> getActivitiesByProject(StaffDTO staff, ProjectDTO project) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get activities by project: " + project.getName());

        return getActivitiesByCriteria(staff, project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ActivityDTO> getActivitiesByWorkPackage(StaffDTO staff, WorkPackageDTO workPackage) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get activities by workPackage: " + workPackage.getName());
        return getActivitiesByCriteria(staff, workPackage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ActivityDTO> getActivitiesByWorkCategory(StaffDTO staff, WorkCategoryDTO workCategory) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get activities by WorkCategory: " + workCategory.getName());
        return getActivitiesByCriteria(staff, workCategory);
    }

    /**
     *
     * @param staff the staff, whose activities should be retrieved
     * @param criteria an further criteria for the activities, which shluld be retrieved. This can be an object of the
     * type WorkPackage, WorkCategory or Project
     * @return all activities, which fulfil the given criterias
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     */
    private ArrayList<ActivityDTO> getActivitiesByCriteria(StaffDTO staff, Object criteria) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get activities: " + criteria.getClass().getName());
        Session hibernateSession = null;
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff) dtoManager.merge(staff);
        }
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            hibernateSession = dbManager.getSession();
            Criteria crit = hibernateSession.createCriteria(Activity.class).
                    add(Restrictions.eq("staff", user));

            if (criteria instanceof BasicDTO) {
                criteria = dtoManager.merge((BasicDTO) criteria);
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

            List<Activity> result = crit.list();
            logger.debug(result.size() + " activities found");

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ActivityDTO>) dtoManager.clone(result);
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
    public long createActivity(ActivityDTO activity) throws FullStopException, InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create activity");
        Activity act = (Activity) dtoManager.merge(activity);

        if (act.getStaff() == null) {
            act.setStaff(getStaffById(getUserId()));
        }
        checkUserOrAdminPermission(act.getStaff().getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();

//        checkActivityDate(activity);
        try {
            if (activity.getKindofactivity() != ActivityDTO.BEGIN_OF_DAY
                    && activity.getKindofactivity() != ActivityDTO.END_OF_DAY
                    && activity.getWorkPackage() == null) {
                throw new DataRetrievalException(LanguageBundle.ACTIVITY_MUST_HAVE_A_PROJECTCOMPONENT);
            }

            if (act.getWorkCategory() == null) {
                act.setWorkCategory((WorkCategory) dbManager.getObject(WorkCategory.class, WorkCategoryDTO.WORK));
            }
            List<Activity> actList = checkForMultiActivity(act.getDescription(), act);

            for (Activity tmp : actList) {
                dbManager.createObject(act);
            }

            warningSystem.addActivity(act);
            long id = (Long) dbManager.createObject(act);
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    private List<Activity> checkForMultiActivity(String description, Activity act) {
        List<Activity> acts = new ArrayList<Activity>();

        if (description == null) {
            return acts;
        }

        try {
            if (description.indexOf("(@") != -1) {
                String substr = description.substring(description.indexOf("(@") + 1);
                substr = substr.substring(0, substr.indexOf(")"));
                StringTokenizer st = new StringTokenizer(substr, "@, ");

                while (st.hasMoreTokens()) {
                    Staff s = getStaffByUsername(st.nextToken());

                    if (s != null) {
                        Activity newAct = new Activity();
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

    private void checkActivityDate(ActivityDTO activity) throws InvalidInputValuesException {
        if (activity.getWorkPackage() != null) {
            WorkPackagePeriodDTO period = activity.getWorkPackage().determineMostRecentPeriod();

            if (period != null) {
                if (!isDateLessOrEqual(period.getFromdate(), activity.getDay())) {
                    throw new InvalidInputValuesException(LanguageBundle.ACTIVITY_BEFORE_START_OF_THE_PROJECT_COMPONENT);
                } else if (period.getTodate() != null && !isDateLessOrEqual(activity.getDay(), period.getTodate())) {
                    throw new InvalidInputValuesException(LanguageBundle.ACTIVITY_AFTER_END_OF_THE_PROJECT_COMPONENT);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityDTO saveActivity(ActivityDTO activity) throws FullStopException, InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save activity");

        final Activity activityHib = (Activity) dtoManager.merge(activity);

        if (activityHib.getStaff() == null) {
            activityHib.setStaff(getStaffById(getUserId()));
        }
        checkUserOrAdminPermission(activityHib.getStaff().getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();
//        checkActivityDate(activity);

        try {
            warningSystem.saveActivity(activityHib);
            final Activity act = (Activity) dbManager.getObject(Activity.class, new Long(activityHib.getId()));

            if (act.getCommitted()) {
                try {
                    checkAdminPermission();
                } catch (PermissionDenyException e) {
                    throw new PersistentLayerException(LanguageBundle.CANNOT_CHANGE_ACTIVITY);
                }
            }

            // to avoid the following error message:
            // org.hibernate.NonUniqueObjectException: a different object with the same identifier value was already associated with the session
//            activityHib.setWorkCategory((WorkCategory)dtoManager.merge( getWorkCategories().get(0)));
            String actText = act.toString();
            activityHib.setReports(act.getReports());
            if (activityHib.getWorkCategory() == null) {
                activityHib.setWorkCategory((WorkCategory) dbManager.getObject(WorkCategory.class, WorkCategoryDTO.WORK));
            }

            dbManager.closeSession();
            dbManager = new DBManagerWrapper();
            dbManager.saveObject(activityHib);
            if (activityHib.getStaff().getId() != getUserId()) {
                sendChangedActivityEmail(activityHib, actText);
            }

            return activity;
        } finally {
            dbManager.closeSession();
        }
    }

    private void sendChangedActivityEmail(Activity activity, String originalActivity) {
        try {
            if (activity.getStaff().getEmail() != null) {
                Staff s = getStaffById(getUserId());
                String text = s.getFirstname() + " " + s.getName() + " has changed the following actvitiy:\n" + originalActivity;
                text += "\n\nto\n\n" + activity.toString();
                Utilities.sendEmail(activity.getStaff().getEmail(), "Activity changed", text);
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
    public void deleteActivity(ActivityDTO activity) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete activity");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Activity act = (Activity) dbManager.getObject(Activity.class, new Long(activity.getId()));
            checkUserOrAdminPermission(act.getStaff().getId());

            if (act.getCommitted()) {
                try {
                    checkAdminPermission();
                } catch (PermissionDenyException e) {
                    throw new PersistentLayerException(LanguageBundle.CANNOT_CHANGE_ACTIVITY);
                }
            }

            dbManager.deleteObject(act);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportDTO> getReportsForActivities(List<ActivityDTO> activityList) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("getReportsForActivities");
        DBManagerWrapper dbManager = new DBManagerWrapper();
        List<ReportDTO> reports = new ArrayList<ReportDTO>();

        try {
            for (ActivityDTO activity : activityList) {
                Activity act = (Activity) dbManager.getObject(Activity.class, new Long(activity.getId()));
                if (act != null) {
                    checkUserOrAdminPermission(act.getStaff().getId());

                    Set<Report> reportSet = act.getReports();
                    List<ReportDTO> tmpReports = dtoManager.clone(new ArrayList(reportSet));

                    for (ReportDTO rep : tmpReports) {
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
    public List<ReportDTO> getReportsForActivity(ActivityDTO activity) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("getReportsForActivity");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Activity act = (Activity) dbManager.getObject(Activity.class, new Long(activity.getId()));
            checkUserOrAdminPermission(act.getStaff().getId());

            Set<Report> reportSet = act.getReports();
            List<ReportDTO> reports = dtoManager.clone(new ArrayList(reportSet));

            return reports;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectCategoryDTO saveProjectCategory(ProjectCategoryDTO projectCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save project category");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            ProjectCategory cat = (ProjectCategory) dtoManager.merge(projectCategory);
            dbManager.saveObject(cat);

            return (ProjectCategoryDTO) dtoManager.clone(cat);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createProjectCategory(ProjectCategoryDTO projectCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create project category");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            long id = (Long) dbManager.createObject(dtoManager.merge(projectCategory));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProjectCategory(ProjectCategoryDTO projectCategory) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete project category");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            // convert the client version of the Project type to the server version of the Project type
            ProjectCategory cat = (ProjectCategory) dtoManager.merge(projectCategory);
            Project p = (Project) dbManager.getObjectByAttribute(Project.class, "projectCategory", cat);

            if (p != null) {
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
    public WorkCategoryDTO saveWorkCategory(WorkCategoryDTO workCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save work category");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            WorkCategory cat = (WorkCategory) dtoManager.merge(workCategory);
            dbManager.saveObject(cat);

            return (WorkCategoryDTO) dtoManager.clone(cat);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long createWorkCategory(WorkCategoryDTO workCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create work category");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            long id = (Long) dbManager.createObject(dtoManager.merge(workCategory));
            return id;
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWorkCategory(WorkCategoryDTO workCategory) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete work category");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            WorkCategory wc = (WorkCategory) dtoManager.merge(workCategory);
            Activity act = (Activity) dbManager.getObjectByAttribute(Activity.class, "workCategory", wc);

            if (act == null) {
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
    public ArrayList<WorkCategoryDTO> getWorkCategories() throws InvalidInputValuesException, DataRetrievalException, NoSessionException {
        logger.debug("get work categories");
        checkSession();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<WorkCategory> result = (List<WorkCategory>) dbManager.getAllObjects(WorkCategory.class);
            logger.debug(result.size() + " work categories found");

            // convert the server version of the WorkCategory types to the client version of the WorkCategory types
            return (ArrayList<WorkCategoryDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    @Override
    public WorkCategoryDTO getWorkCategory(long id) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get work category");
        checkSession();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            WorkCategory result = (WorkCategory) dbManager.getObject(WorkCategory.class, id);

            // convert the server version of the WorkCategory type to the client version of the WorkCategory type
            return (WorkCategoryDTO) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ContractDocumentDTO> getContractDocuments(ContractDTO contract) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get contract documents");
        checkUserOrAdminPermission(contract.getStaff().getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<ContractDocument> result = (List<ContractDocument>) dbManager.getObjectsByAttribute(ContractDocument.class, "contract", dtoManager.merge(contract));

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<ContractDocumentDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<TravelDocumentDTO> getTravelDocuments(TravelDTO travel) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get contract documents");
        checkUserOrAdminPermission(travel.getStaff().getId());
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<TravelDocumentDTO> result = (List<TravelDocumentDTO>) dbManager.getObjectsByAttribute(TravelDocument.class, "travel", dtoManager.merge(travel));

            // convert the server version of the Project type to the client version of the Project type
            return (ArrayList<TravelDocumentDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FundingDTO createFunding(FundingDTO funding) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("create funding");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Funding fund = (Funding) dtoManager.merge(funding);
            long id = (Long) dbManager.createObject(fund);
            fund.setId(id);

            return (FundingDTO) dtoManager.clone(fund);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FundingDTO saveFunding(FundingDTO funding) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("save funding");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Funding fund = (Funding) dtoManager.merge(funding);
            dbManager.saveObject(fund);

            return (FundingDTO) dtoManager.clone(fund);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<FundingDTO> getFundingsForCompany(CompanyDTO company) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get fundings");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            List<Funding> result = (List<Funding>) dbManager.getObjectsByAttribute(Funding.class, "company", dtoManager.merge(company));
            logger.debug("fundings found " + result.size());

            return (ArrayList<FundingDTO>) dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ReportDTO> getAllCreatedReports(String reportGenerator, StaffDTO staff, int year) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get all created reports");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Session hibernateSession = dbManager.getSession();

            Criteria crit = hibernateSession.createCriteria(Report.class).
                    add(Restrictions.eq("generatorname", reportGenerator));

            if (staff != null) {
                crit.add(Restrictions.or(Restrictions.eq("staff", dtoManager.merge(staff)),
                        Restrictions.isNull("staff")));
            }

            if (year != 0) {
                int dateObjectYear = year - 1900;
                crit.add(Restrictions.between("creationtime", new Date(dateObjectYear, 0, 1), new Date(dateObjectYear, 11, 31, 23, 59, 59)));
            }

            List<Report> result = crit.list();

            return dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * TODO: Methode umbenennen {@inheritDoc}
     */
    @Override
    public ArrayList<ReportType> getAllAvailableReports() throws PermissionDenyException, NoSessionException {
        logger.debug("get all available report plugins");
        checkAdminPermission();
        ArrayList<ReportType> reports = new ArrayList<ReportType>();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            ProjectTrackerReport[] reportArray = reportManager.getAvailableReports();
            for (ProjectTrackerReport tmp : reportArray) {
                reports.add(new ReportType(tmp.getReportName(), tmp.supportUserSpecificreportGeneration(), tmp.supportUserUnspecificreportGeneration()));
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
    public ArrayList<TravelDTO> getAllTravels(long staffId, long projectId, int year) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("get all travels");
        if (staffId == 0) {
            checkAdminPermission();
        } else {
            checkUserOrAdminPermission(staffId);
        }

        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Session hibernateSession = dbManager.getSession();

            Criteria crit = hibernateSession.createCriteria(Travel.class);

            if (staffId != 0) {
                Staff st = new Staff();
                st.setId(staffId);
                crit.add(Restrictions.eq("staff", st));
            }

            if (projectId != 0) {
                Project pro = new Project();
                pro.setId(projectId);
                crit.add(Restrictions.eq("project", pro));
            }

            if (year != 0) {
                int dateObjectYear = year - 1900;
                crit.add(Restrictions.between("date", new Date(dateObjectYear, 0, 1), new Date(dateObjectYear, 11, 31, 23, 59, 59)));
            }

            List<Travel> result = crit.list();

            return dtoManager.clone(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReportDTO createReport(String name, Date start, Date end, long staffID, String reportName) throws InvalidInputValuesException, ReportNotFoundException, PersistentLayerException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("create report");
        checkAdminPermission();

        GregorianCalendar startDate = new GregorianCalendar();
        GregorianCalendar endDate = new GregorianCalendar();
        startDate.setTimeInMillis(start.getTime());
        endDate.setTimeInMillis(end.getTime());
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            long id = reportManager.createReport(name, startDate, endDate, staffID, reportName);
            Report clientReport = (Report) dbManager.getObject(Report.class, id);

            return (ReportDTO) dtoManager.clone(clientReport);
        } catch (de.cismet.projecttracker.report.exceptions.DataRetrievalException e) {
            // translate the report plugin exception to the client exception. Only the exception, which are in the client package can be sent to the server
            throw new DataRetrievalException(e);
        } catch (de.cismet.projecttracker.report.exceptions.ReportNotFoundException e) {
            // translate the ReportNotFoundException to the client exception. Only the exception, which are in the client package can be sent to the server
            throw new ReportNotFoundException(e);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String checkReport(Date start, Date end, long staffID, String reportName) throws ReportNotFoundException, DataRetrievalException, PermissionDenyException, NoSessionException {
        logger.debug("check report");
        checkAdminPermission();
        if (end.before(start)) {
            throw new DataRetrievalException(LanguageBundle.END_IS_BEFORE_START);
        }
        ProjectTrackerReport report = reportManager.getReportByName(reportName);

        if (report != null) {
            GregorianCalendar startDate = new GregorianCalendar();
            GregorianCalendar endDate = new GregorianCalendar();
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
    public void deleteReport(ReportDTO report) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete report");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            //            SQLQuery query = hibernateSession.createSQLQuery("delete from activity_report where reportid=" + report.getId());
            //            query.executeUpdate();

            Report result = (Report) dbManager.getObject(Report.class, report.getId());

            dbManager.deleteObject(result);
        } finally {
            dbManager.closeSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContractDocument(ContractDocumentDTO document) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete contract document");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

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
    public void deleteTravelDocument(TravelDocumentDTO document) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException {
        logger.debug("delete travel document");
        checkAdminPermission();
        DBManagerWrapper dbManager = new DBManagerWrapper();

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
    public StaffDTO login(String username, String pasword) throws LoginFailedException, DataRetrievalException {
        Session hibernateSession = null;
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            hibernateSession = dbManager.getSession();

            logger.debug(username + " sends login request");
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(pasword.getBytes());

            Staff staff = (Staff) hibernateSession.createCriteria(Staff.class).add(Restrictions.eq("username", username)).uniqueResult();
//            Staff staff = (Staff)hibernateSession.createCriteria(Staff.class).add(Restrictions.and(Restrictions.eq("username", username), Restrictions.eq("password", md.digest()))).uniqueResult();

            if (staff != null) {
                HttpSession session = getThreadLocalRequest().getSession();
                SessionInformation sessionInfo = new SessionInformation();
                sessionInfo.setCurrentUser(staff);
                session.setAttribute("sessionInfo", sessionInfo);

                //persistentes Cookie anlegen
                Cookie sessionCookie = new Cookie("JSESSIONID", getThreadLocalRequest().getSession().getId());
                sessionCookie.setMaxAge(60 * 60 * 24 * 100);
                sessionCookie.setPath(getThreadLocalRequest().getContextPath());
                getThreadLocalResponse().addCookie(sessionCookie);

                return (StaffDTO) dtoManager.clone(staff);
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
            HttpSession session = getThreadLocalRequest().getSession();
            SessionInformation sessionInfo = (SessionInformation) session.getAttribute("sessionInfo");
            if (sessionInfo != null) {
                return (StaffDTO) dtoManager.clone(sessionInfo.getCurrentUser());
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
            HttpSession session = getThreadLocalRequest().getSession();

            if (session != null) {
                session.invalidate();
            }
        } catch (Throwable th) {
            logger.debug("error during the logout", th);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFirstReportYear() throws DataRetrievalException {
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Object result = dbManager.getObject("select min(fromdate) from Report as rep");

            if (result != null && result instanceof Timestamp) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis(((Timestamp) result).getTime());
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
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Object result = dbManager.getObject("select min(date) from Travel as trav");

            if (result != null && result instanceof Timestamp) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis(((Timestamp) result).getTime());
                return gc.get(GregorianCalendar.YEAR);
            } else {
                return (new GregorianCalendar()).get(GregorianCalendar.YEAR);
            }
        } finally {
            dbManager.closeSession();
        }
    }

    @Override
    public Double getHoursOfWork(StaffDTO staff, Date day) throws DataRetrievalException, NoSessionException, InvalidInputValuesException, PermissionDenyException {
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff) dtoManager.merge(staff);
        }

        if (user.getId() != getUserId()) {
            checkAdminPermission();
        }

        TimetrackerQuery query = new TimetrackerQuery();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(day.getTime());

        try {
            HoursOfWork how = query.getHoursOfWork(user, cal);
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
    public Date getStartOfWork(StaffDTO staff, Date day) throws DataRetrievalException, NoSessionException, InvalidInputValuesException, PermissionDenyException {
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff) dtoManager.merge(staff);
        }

        if (user.getId() != getUserId()) {
            checkAdminPermission();
        }

        TimetrackerQuery query = new TimetrackerQuery();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(day.getTime());

        try {
            GregorianCalendar res = query.getStartOfWork(user, cal);

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
    public TimePeriod getStartEndOfWork(StaffDTO staff, Date day) throws DataRetrievalException, NoSessionException, InvalidInputValuesException, PermissionDenyException {
        TimePeriod result = new TimePeriod();
        Staff user;

        if (staff == null) {
            user = getStaffById(getUserId());
        } else {
            user = (Staff) dtoManager.merge(staff);
        }

        if (user.getId() != getUserId()) {
            checkAdminPermission();
        }

        TimetrackerQuery query = new TimetrackerQuery();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(day.getTime());

        try {
            GregorianCalendar res = query.getStartOfWork(user, cal);

            if (res != null) {
                result.setStart(res.getTime());
            }
            Date now = new Date();

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

    public boolean isDataChanged() throws DataRetrievalException, NoSessionException, InvalidInputValuesException, PermissionDenyException {
        return false;
    }

    private static boolean isSameDay(Date date, Date otherDate) {
        if (date == null && otherDate == null) {
            return true;
        } else if (date == null || otherDate == null) {
            return false;
        }

        return (date.getYear() == otherDate.getYear()
                && date.getMonth() == otherDate.getMonth()
                && date.getDate() == otherDate.getDate());
    }

    private static boolean isDateLessOrEqual(Date date1, Date date2) {
        int firstDate = (date1.getYear() + 1900) * 10000 + date1.getMonth() * 100 + date1.getDate();
        int secondDate = (date2.getYear() + 1900) * 10000 + date2.getMonth() * 100 + date2.getDate();

        return (firstDate <= secondDate);
    }

    @Override
    public List<HolidayType> getHolidaysByWeek(int year, int week) throws InvalidInputValuesException, DataRetrievalException {
        GregorianCalendar cal = getFirstDayOfWeek(year, week);
        HolidayEvaluator eval = new HolidayEvaluator();
        List<HolidayType> holidays = new ArrayList<HolidayType>();

        do {
            int holTmp = eval.isHoliday(cal);

            if (holTmp != HolidayEvaluator.WORKDAY) {
                HolidayType holiday = new HolidayType();
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
     * checks if the current user has admin rights
     *
     * @throws PermissionDenyException if the current user has no admin rights
     */
    private void checkAdminPermission() throws PermissionDenyException, NoSessionException {
        try {
            SessionInformation sessionInfo = getCurrentSession();

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
     * user
     *
     * @param userId the id of the user data that should be changed
     * @throws PermissionDenyException
     */
    private void checkUserOrAdminPermission(long userId) throws PermissionDenyException, NoSessionException {
        try {
            long signedUser = getUserId();
            SessionInformation sessionInfo = getCurrentSession();

            if (signedUser != userId && !sessionInfo.isAdmin()) {
                throw new PermissionDenyException(LanguageBundle.ONLY_ALLOWED_FOR_ADMIN);
            }
        } catch (NoSessionException e) {
            throw e;
        } catch (Throwable th) {
            throw new PermissionDenyException(LanguageBundle.ONLY_ALLOWED_FOR_ADMIN);
        }
    }

    private void checkSession() throws NoSessionException {
        getCurrentSession();
    }

    /**
     *
     * @return the username of the current user
     */
    private long getUserId() throws NoSessionException {
        SessionInformation sessionInfo = getCurrentSession();

        return sessionInfo.getCurrentUser().getId();
    }

    private SessionInformation getCurrentSession() throws NoSessionException {
        HttpSession session = getThreadLocalRequest().getSession(false);
        if (session == null) {
            throw new NoSessionException();
        }

        SessionInformation sessionInfo = (SessionInformation) session.getAttribute("sessionInfo");

        if (sessionInfo == null) {
            throw new NoSessionException();
        }

        return sessionInfo;
    }

    private Staff getStaffById(long id) throws DataRetrievalException {
        logger.debug("getStaffById");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Staff result = (Staff) dbManager.getObject(Staff.class, id);

            return result;
        } finally {
            dbManager.closeSession();
        }
    }

    private Staff getStaffByUsername(String username) throws DataRetrievalException {
        logger.debug("getStaffByUsername");
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Staff result = (Staff) dbManager.getObjectByAttribute(Staff.class, "username", username);

            return result;
        } finally {
            dbManager.closeSession();
        }
    }

    private GregorianCalendar getFirstDayOfWeek(int year, int week) {
        GregorianCalendar from = new GregorianCalendar(year, 0, 1, 0, 0, 0);
        from.setMinimalDaysInFirstWeek(4);
        from.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        from.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
        from.set(GregorianCalendar.WEEK_OF_YEAR, week);

        String time = CalendarHelper.toDateString(from);
        return from;
    }

    private GregorianCalendar getLastDayOfWeek(int year, int week) {
        GregorianCalendar till = new GregorianCalendar(year, 0, 1, 23, 59, 59);
        till.setMinimalDaysInFirstWeek(4);
        till.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        till.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SUNDAY);
        till.set(GregorianCalendar.WEEK_OF_YEAR, week);

        String time = CalendarHelper.toDateString(till);
        return till;
    }

    @Override
    protected boolean shouldCompressResponse(HttpServletRequest request, HttpServletResponse response, String responsePayload) {
        return true;
    }

    @Override
    public Double getAccountBalance(StaffDTO staff) throws DataRetrievalException, NoSessionException, PermissionDenyException {

        double realWorkingTime = 0;
        double nominalWorkingTime = 0;
        //calculate the nominal workin days to determine the nominal working time
        long nominalWorkingDays = 0;
        DBManagerWrapper dbManager = new DBManagerWrapper();

        checkUserOrAdminPermission(staff.getId());

        try {
            final Session hibernateSession = dbManager.getSession();
            List contracts = hibernateSession.createCriteria(Contract.class).add(Restrictions.eq("staff.id", staff.getId())).list();
            final Iterator it = contracts.iterator();

            //iterate through all contracts and sum the real and the nominal working time
            while (it.hasNext()) {
                Contract contract = (Contract) it.next();
                GregorianCalendar contractToDateCal = new GregorianCalendar();
                //if the contract is unlimeted calculate till now
                final GregorianCalendar contractFromDateCal = new GregorianCalendar();

                if (contract.getTodate() != null && contract.getTodate().getTime() < contractToDateCal.getTimeInMillis()) {
                    contractToDateCal.setTime(contract.getTodate());
                }

                if (contractToDateCal.getTime().before(accountBalanceDueDate.getTime())) {
                    continue;
                } else if (contract.getFromdate().before(accountBalanceDueDate.getTime())) {
                    contractFromDateCal.setTime(accountBalanceDueDate.getTime());
                } else {
                    contractFromDateCal.setTime(contract.getFromdate());
                }


                final double whow = contract.getWhow();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                final GregorianCalendar queryToDate = new GregorianCalendar();
                queryToDate.setTime(contractToDateCal.getTime());
                queryToDate.add(GregorianCalendar.DATE, 1);
                //calculate the real working time exclude pause and freizeitausgleich activities
                Statement s = dbManager.getDatabaseConnection().createStatement();
                ResultSet rs = s.executeQuery(String.format(REAL_WORKING_TIME_QUERY, "" + (whow / 5), "" + staff.getId(), dateFormatter.format(contractFromDateCal.getTime()), dateFormatter.format(queryToDate.getTime())));
                if (rs != null) {
                    while (rs.next()) {
                        realWorkingTime += rs.getDouble(1);
                    }
                }

                //include holiday and illness activities
                rs = s.executeQuery(String.format(REAL_WORKING_TIME_ILLNESS_AND_HOLIDAY, "" + (whow / 5), "" + staff.getId(), dateFormatter.format(contractFromDateCal.getTime()), dateFormatter.format(queryToDate.getTime())));
                if (rs != null) {
                    while (rs.next()) {
                        realWorkingTime += rs.getDouble(1);
                    }
                }

                //traveltime just with the half time
//                rs = s.executeQuery(String.format(TRAVEL_TIME_QUERY, "" + (whow / 5), "" + staff.getId(), dateFormatter.format(contractFromDateCal.getTime()), dateFormatter.format(queryToDate.getTime())));
//                if (rs != null) {
//                    while (rs.next()) {
//                        realWorkingTime += rs.getDouble(1) / 2;
//                    }
//                }
                //calculate the nominal working time. 
                nominalWorkingDays = calculateNominalWorkingDays(contractFromDateCal, contractToDateCal);
                nominalWorkingTime += (whow / 5) * nominalWorkingDays;
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

    private long calculateNominalWorkingDays(GregorianCalendar from, GregorianCalendar to) {
        final long diff = to.getTime().getTime() - from.getTime().getTime();

        final long days = Math.round(diff / (24 * 60 * 60 * 1000d));
        final long weeks = Math.round(days / 7);
        //exclude sa and so
        long workingDays = days - (2 * weeks);

        //check if from or to date are sa or so
        if (from.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY
                || from.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY
                || to.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY
                || to.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
            workingDays--;
        }

        HolidayEvaluator hev = new HolidayEvaluator();
        final long holidays = hev.getNumberOfHolidaysOnWeekDays(from, to);

        workingDays -= holidays;

        return workingDays;
    }

    @Override
    public List<ActivityDTO> getFavouriteActivities(StaffDTO staff) throws NoSessionException, DataRetrievalException {
        logger.debug("get favourite activities");
        long userId;
        List<Activity> result = new ArrayList<Activity>();

        if (staff == null) {
            userId = getUserId();
        } else {
            userId = staff.getId();
        }
        DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            Statement s = dbManager.getDatabaseConnection().createStatement();
            ResultSet rs = s.executeQuery(String.format(FAVOURITE_ACTIVITIES_QUERY, userId));

            if (rs != null) {
                while (rs.next()) {
                    long id = rs.getLong(1);
                    result.add((Activity) dbManager.getObject(Activity.class, id));
                }
            }

            return (List) dtoManager.clone(result);
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }

    @Override
    public Boolean isExisitingFavouriteTask(ActivityDTO activity) {
        DBManagerWrapper dbManager = new DBManagerWrapper();
        try {
            Statement s = dbManager.getDatabaseConnection().createStatement();
            ResultSet rs = s.executeQuery(String.format(FAVOURITE_EXISTS_QUERY,
                    "" + activity.getStaff().getId(), "" + activity.getWorkPackage().getId(),
                    activity.getDescription()));

            if (rs != null && !rs.first()) {
                return false;
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(FavouriteTaskStory.class.getName()).log(Level.SEVERE, "Error while checkig"
                    + " if facourite task already exists. Drop action cancelled", ex);
        }

        return true;
    }
}
