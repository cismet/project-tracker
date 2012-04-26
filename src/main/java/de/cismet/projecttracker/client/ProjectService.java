package de.cismet.projecttracker.client;

import com.google.gwt.user.client.rpc.RemoteService;
import de.cismet.projecttracker.client.dto.ActivityDTO;
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
import de.cismet.projecttracker.client.dto.ProjectShortDTO;
import de.cismet.projecttracker.client.dto.RealOverheadDTO;
import de.cismet.projecttracker.client.dto.ReportDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.TravelDTO;
import de.cismet.projecttracker.client.dto.TravelDocumentDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.exceptions.CreationFailedException;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This interface contains all server-side operations.
 * See <a href="http://code.google.com/webtoolkit/doc/1.6/DevGuideServerCommunication.html">RPC communication</a>
 *
 * @author therter
 */
public interface ProjectService extends RemoteService {
    /**
     * @return All ongoing projects. That are all projects, whose current timeperiod embraces the current day.
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ProjectShortDTO> getAllOngoingProjects() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * @return all staffs
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<StaffDTO> getAllEmployees() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * @return All ongoing projects. That are all projects, whose current timeperiod does not embrace the current day.
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ProjectShortDTO> getAllCompletedProjects() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * @param staffId the staff, the returned travels should be assigned to
     * @param projectId the project, the returned travels should be assigned to
     * @param year the year of the returned travels
     * @return A list with all travels of the given staff or a list with all travels of all staffs, if the given staff == 0.
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<TravelDTO> getAllTravels(long staffId, long projectId, int year) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * deletes the project with the given id
     * @param id the id of the project that should be deleted
     * @throws PersistentLayerException
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteProject(long id) throws InvalidInputValuesException, PersistentLayerException, DataRetrievalException,  PermissionDenyException, NoSessionException;

    /**
     * @param projectId the id of the project that should be returned
     * @return the project with the given id
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ProjectDTO getProject(long projectId) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * creates a new project. If no project body exists, this method will create one 
     * and asign it to the project.
     * @return the new project
     * @throws DataRetrievalException
     * @throws CreationFailedException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ProjectShortDTO createProject() throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * saves the given project in the database. This method should only be used, if the given project already exists
     * within the database. The project will be recognized by its id.
     *
     * @param project the project that should be saved
     * @return the project
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ProjectDTO saveProject(ProjectDTO project) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * @return all projects
     * @throws DataRetrievalException
     * @throws NoSessionException
     */
    public ArrayList<ProjectShortDTO> getAllProjects() throws InvalidInputValuesException, DataRetrievalException, NoSessionException;


    /**
     * @return all project categories
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ProjectCategoryDTO> getAllProjectCategories() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * Saves the given work package. This method should only be used, if the given work package already exists
     * within the database. The work package will be recognized by its id.
     *
     * @param workpackage the work package that should be used
     * @return the saved work package
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public WorkPackageDTO saveWorkPackage(WorkPackageDTO workpackage) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * Creates a new work package in the database.
     * @param workpackage this object should contain the values of the new work package. It should not contain an ID.
     * @return the new work package
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public WorkPackageDTO createWorkPackage(WorkPackageDTO workpackage) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;



    /**
     * Deletes the given work package.
     * @param workPackage the work package that should be deleted
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteWorkPackage(WorkPackageDTO workPackage) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * Creates the new staff in the database.
     * @param staff this object should contain the values of the new staff, but it should not contain an ID.
     * @return the if of the newly created staff
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public long createStaff(StaffDTO staff) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Saves the given staff within the database. This method should only be used, if the given staff already exists
     * within the database. The staff will be recognized by the id.
     *
     * @param staff the staff that should be saved
     * @return the saved staff
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public StaffDTO saveStaff(StaffDTO staff) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given staff.
     *
     * @param staff
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteStaff(StaffDTO staff) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Creates the new travel expense report in the database.
     * @param travel this object should contain the values of the new travel expense report, but it should not contain an ID.
     * @return the id of the newly created travel expense report
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public TravelDTO createTravelExpenseReport(TravelDTO travel) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Saves the given travel expense report within the database. This method should only be used, if the given travel expense report already exists
     * within the database. The report will be recognized by the id.
     *
     * @param travel the travel expense report that should be saved
     * @return the saved travel expense report
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public TravelDTO saveTravelExpenseReport(TravelDTO travel) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given travel expense report.
     *
     * @param travel the travel expense report object that should be deleted
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteTravelExpenseReport(TravelDTO travel) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * @return all staffs, which have a contract for the current day
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<StaffDTO> getCurrentEmployees() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     * @return all staffs, which have no contract for the current day
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<StaffDTO> getAllFormerEmployees() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * Changes the password of the given user
     * @param staff the user, whose password should be changed
     * @param newPassword the new password
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void changePassword(StaffDTO staff, String newPassword) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * @return all companies
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<CompanyDTO> getCompanies() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given contract from the database.
     *
     * @param contract the contract, that shouldbe deleted
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteContract(ContractDTO contract) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * Saves the given company in the database. This method should only be used, if the given company already exists
     * within the database. The company will be recognized by its id.
     * @param company the company that should be saved
     * @return the saved company
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public CompanyDTO saveCompany(CompanyDTO company) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Creates a new company in the database.
     * @param company this object should contain the values of the new company, but it should not contain an ID.
     * @return the new created company
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public long createCompany(CompanyDTO company) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given company from the database.
     * @param company the company that should be deleted
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteCompany(CompanyDTO company) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * deletes the given RealOverhead object from the database.
     * @param overhead the object that should be deleted
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteRealOverhead(RealOverheadDTO overhead) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * @return all project bodies
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ProjectBodyDTO> getProjectBodies() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * Saves the given project body in the database. This method should only be used, if the given project body already exists
     * within the database. The project body will be recognized by its id.
     * @param projectBody the project body that should be saved.
     * @return the saved project body
     * @throws PersistentLayerExcepticon
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ProjectBodyDTO saveProjectBody(ProjectBodyDTO projectBody) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Creates a new project body in the database.
     * @param projectBody this object should contain the values of the new project body, but it should not contain an ID.
     * @return
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public long createProjectBody(ProjectBodyDTO projectBody) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given project body from the database.
     * @param projectBody the project body that should be deleted
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteProjectBody(ProjectBodyDTO projectBody) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Creates a new cost category.
     * @param costCategory this object should contain the values of the new cost category, but it should not contain an ID.
     * @return the id of the newly created object
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public long createCostCategory(CostCategoryDTO costCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Saves the given cost category. This method should only be used, if the given cost category already exists
     * within the database. The cost category will be recognized by its id.
     * @param costCategory the cost category that should be saved
     * @return the saved cost category
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public CostCategoryDTO saveCostCategory(CostCategoryDTO costCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given cost category.
     * @param costCategory the cost category that should be deleted.
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteCostCategory(CostCategoryDTO costCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Creates a new project component tag.
     * @param tag this object should contain the values of the new project component tag, but it should not contain an ID.
     * @return the id of the newly created object
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public long createProjectComponentTag(ProjectComponentTagDTO tag) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given project component tag.
     * @param tag the project component tag that should be deleted.
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteProjectComponentTag(ProjectComponentTagDTO tag) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * @return all projects
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ProjectDTO> getAllProjectsFull() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * Creates a new activity in the database.
     * If the activity contains no staff, the staff object of current user will be inserted.
     * @param activity this object should contain the values of the new activity, but it should not contain an ID.
     * @return the new created activity
     * @throws FullStopException
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public long createActivity(ActivityDTO activity) throws FullStopException, InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Saves the given activity. This method should only be used, if the given activity already exists
     * within the database. The activity will be recognized by its id.
     * If the activity contains no staff, the staff object of current user will be inserted.
     * @param activity
     * @return
     * @throws FullStopException
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ActivityDTO saveActivity(ActivityDTO activity) throws FullStopException, InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given Activity
     * @param activity the activity that should be deleted
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteActivity(ActivityDTO activity) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;
    
    /**
     * returns the account balance of the given Staff
     * @param staff the account balance of this user will be returned. 
     * @return 
     */
    public Double getAccountBalance(StaffDTO staff); 

    /**
     * @param staff the activities of this user will be returned. If the staff is null, the activities of the current users will be returned
     * @param year
     * @param week
     * @return all activities of the given user within the given week
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ActivityDTO> getActivitiesByWeek(StaffDTO staff, int year, int week) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * 
     * @param staff
     * @param year
     * @param week
     * @return all activities and holidays of the given user within the given week
     * @throws InvalidInputValuesException
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException 
     */
    public ActivityResponseType getActivityDataByWeek(StaffDTO staff, int year, int week) throws InvalidInputValuesException,DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * @param staff the activities of this user will be returned. If the staff is null, the activities of the current users will be returned
     * @param project
     * @return all activities of the given user and within the given project
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ActivityDTO> getActivitiesByProject(StaffDTO staff, ProjectDTO project) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     * @param staff the activities of this user will be returned. If the staff is null, the activities of the current users will be returned
     * @param workPackage
     * @return all activities of the given user and within the given project
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ActivityDTO> getActivitiesByWorkPackage(StaffDTO staff, WorkPackageDTO workPackage) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;



    /**
     * @param staff the activities of this user will be returned. If the staff is null, the activities of the current users will be returned
     * @param workCategory
     * @return all activities of the given user and within the given project
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ActivityDTO> getActivitiesByWorkCategory(StaffDTO staff, WorkCategoryDTO workCategory) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     *
     * @param staff
     * @return the last activity of the given user
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ActivityDTO getLastActivityForUser(StaffDTO staff) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;

    public List<ActivityDTO> getLastActivitiesForUser(StaffDTO staff) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;
    
    
    public List<ActivityDTO> getLastActivitiesExceptForUser(StaffDTO staff) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     * Saves the given project category. This method should only be used, if the given project category already exists
     * within the database. The project category will be recognized by its id.
     * @param projectCategory the project category that should be saved
     * @return the saved project category
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ProjectCategoryDTO saveProjectCategory(ProjectCategoryDTO projectCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * Creates a new project category.
     * @param projectCategory this object should contain the values of the new project category, but it should not contain an ID.
     * @return
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public long createProjectCategory(ProjectCategoryDTO projectCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given project category.
     * @param projectCategory the project category that should be deleted.
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteProjectCategory(ProjectCategoryDTO projectCategory) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * Saves the given work category in the database. This method should only be used, if the given work category already exists
     * within the database. The work category will be recognized by its id.
     * @param workCategory the work category that should be saved
     * @return the saved work category
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public WorkCategoryDTO saveWorkCategory(WorkCategoryDTO workCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * Creates a new work category in the database.
     * @param workCategory this object should contain the values of the new work category, but it should not contain an ID.
     * @return the new created work category
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public long createWorkCategory(WorkCategoryDTO workCategory) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given work category.
     * @param workCategory the work category that should be deleted
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteWorkCategory(WorkCategoryDTO workCategory) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * @return all work categories
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<WorkCategoryDTO> getWorkCategories() throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;
    
    /**
     * @param workCategory this object should contain the values of the new work category, but it should not contain an ID.
     * @return the work category with the given id
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public WorkCategoryDTO getWorkCategory(long id) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;
    
    /**
     * Creates a new project costs.
     * @param projectCosts this object should contain the values of the new project costs, but it should not contain an ID.
     * @return
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public long createProjectCosts(ProjectCostsDTO projectCosts) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Saves the given project costs. This method should only be used, if the given project costs already exists
     * within the database. The project body will be recognized by its id.
     * @param projectCosts the project costs that should be saved
     * @return the saved cost category
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ProjectCostsDTO saveProjectCosts(ProjectCostsDTO projectCosts) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given project costs.
     * @param projectCosts the project costs that should be deleted.
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteProjectCosts(ProjectCostsDTO projectCosts) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * @param project the project costs of this project will be returned
     * @return all project costs, which are assigned to the given project
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ProjectCostsDTO> getProjectCostsByProject(ProjectDTO project) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     * 
     * @return all report generators
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ReportType> getAllAvailableReports() throws PermissionDenyException, NoSessionException;

    /**
     * Creates a new report.
     *
     * @param start the first day that should be considered within the new report.
     * @param end the last day that should be considered within the new report.
     * @param staffID the id of the staff, whose report should be created. If the report should not
     * be related to any staff, this parameter should be null. Note: There are two kinds of reports:
     * <ol><li>report which are related to staffs</li><li>reports which are not related to staffs</li></ol>
     * Some report generators only supports the first kind of reports, some report generators only supports the second
     * kind of reports and some report generators supports both kind of report. That depends on the generator and on the reports, which should be generated.
     * @param reportName the name of the report generator, that should be used to create the report
     * @return the new created report
     * @throws ReportNotFoundException if no report generator with the given name exists
     * @throws PersistentLayerException
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ReportDTO createReport(String name, Date start, Date end, long staffID, String reportName) throws InvalidInputValuesException, ReportNotFoundException, PersistentLayerException, DataRetrievalException, PermissionDenyException, NoSessionException;

    /**
     * check whether all requirements to create a report, are fullfilled.
     * @param start the first day that should be considered within the new report.
     * @param end the last day that should be considered within the new report.
     * @param staffID the id of the staff, whose report should be created. For further information see the staffID parameter of the method {@see #createReport(Date, Date, long, String)}.
     * @param reportName the name of the report generator, that should be used to create the report
     * @return a list of errors and warning
     * @throws ReportNotFoundException
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public String checkReport(Date start, Date end, long staffID, String reportName) throws InvalidInputValuesException, ReportNotFoundException, DataRetrievalException, PermissionDenyException, NoSessionException;
    
    /**
     * Deletes the given report.
     * @param report the report that should be deleted
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteReport(ReportDTO report) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * 
     * @param activityList
     * @return all reports, which used one or more of the given activities
     * @throws InvalidInputValuesException
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException 
     */
    public List<ReportDTO> getReportsForActivities(List<ActivityDTO> activityList) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * 
     * @param activity
     * @return all reports, which used one or more of the given activity
     * @throws InvalidInputValuesException
     * @throws DataRetrievalException
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException 
     */
    public List<ReportDTO> getReportsForActivity(ActivityDTO activity) throws InvalidInputValuesException, DataRetrievalException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given contract document.
     * @param document the document that should be deleted
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteContractDocument(ContractDocumentDTO document) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Deletes the given travel document.
     * @param document the document that should be deleted
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteTravelDocument(TravelDocumentDTO document) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * @param reportGenerator the name of the generator, all returned reports were created with
     * @param staff The staff, the returned reports were created for. If staff is null,
     * the reports for all staffs will be returned. The staff independent reports do not consider this parameter.
     * @param year The year, the returned reports should created in. If the value is 0, all years will be considered
     * @return all already created reports
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ReportDTO> getAllCreatedReports(String reportGenerator, StaffDTO staff, int year) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     * @return all work package estimation, which are assigned to the given work package
     * @param workPackage 
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<EstimatedComponentCostDTO> getEstimatedWorkPackageCostForWP(WorkPackageDTO workPackage) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     *
     * @param contract
     * @return all contract documents, which are assigned to the given contract
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<ContractDocumentDTO> getContractDocuments(ContractDTO contract) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     *
     * @param travel
     * @return all travel documents, which are assigned to the given travel
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<TravelDocumentDTO> getTravelDocuments(TravelDTO travel) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     *
     * @param company
     * @return all fundings for the given company
     * @throws DataRetrievalException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public ArrayList<FundingDTO> getFundingsForCompany(CompanyDTO company) throws InvalidInputValuesException, DataRetrievalException, PermissionDenyException, NoSessionException;


    /**
     * Creates a new funding.
     * @param funding this object should contain the values of the new funding, but it should not contain an ID.
     * @return the newly created funding object
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public FundingDTO createFunding(FundingDTO funding) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * Saves the given funding. This method should only be used, if the given funding already exists
     * within the database. The funding will be recognized by its id.
     * @param funding the funding that should be saved
     * @return the saved funding
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public FundingDTO saveFunding(FundingDTO funding) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * Saves the given estimation. This method should only be used, if the given estimation already exists
     * within the database. The estimation will be recognized by its id.
     * @param estimation the estimation that should be saved
     * @return the saved estimation
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public EstimatedComponentCostMonthDTO saveEstimatedWorkPackageCostMonth(EstimatedComponentCostMonthDTO estimation) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * Creates a new estimation.
     * @param estimation this object should contain the values of the new estimation, but it should not contain an ID.
     * @return the newly created estimation object
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public EstimatedComponentCostMonthDTO createEstimatedWorkPackageCostMonth(EstimatedComponentCostMonthDTO estimation) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * Creates a new estimation.
     * @param estimation this object should contain the values of the new estimation, but it should not contain an ID.
     * @return the newly created estimation object
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public EstimatedComponentCostDTO createEstimatedWorkPackageCost(EstimatedComponentCostDTO estimation) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;

    /**
     * deletes the given estimation from the database.
     * @param estimation the object that should be deleted
     * @throws PersistentLayerException
     * @throws PermissionDenyException
     * @throws NoSessionException
     */
    public void deleteEstimatedWorkPackageCost(EstimatedComponentCostDTO estimation) throws InvalidInputValuesException, PersistentLayerException, PermissionDenyException, NoSessionException;


    /**
     * login
     * @param username the username of the guy, who want to login
     * @param pasword the correponding password
     * @return the staff object of the user, who is logged in now
     * @throws LoginFailedException
     * @throws DataRetrievalException
     */
    public StaffDTO login(String username, String pasword) throws InvalidInputValuesException, LoginFailedException, DataRetrievalException;

    /**
     * logout
     */
    public StaffDTO checkLogin() throws DataRetrievalException;

    /**
     * logout
     */
    public void logout();

    /**
     * @return the year, the first report was created for
     * @throws DataRetrievalException
     */
    public Integer getFirstReportYear() throws InvalidInputValuesException, DataRetrievalException;

    /**
     * @return the year, the first travel expense report was created for
     * @throws DataRetrievalException
     */
    public Integer getFirstTravelYear() throws InvalidInputValuesException, DataRetrievalException;
    
    public Double getHoursOfWork(StaffDTO staff, Date day) throws DataRetrievalException, NoSessionException, InvalidInputValuesException, PermissionDenyException;

    public Date getStartOfWork(StaffDTO staff, Date day) throws DataRetrievalException, NoSessionException, InvalidInputValuesException, PermissionDenyException;

    public TimePeriod getStartEndOfWork(StaffDTO staff, Date day) throws DataRetrievalException, NoSessionException, InvalidInputValuesException, PermissionDenyException;

    public List<HolidayType> getHolidaysByWeek(int year, int week) throws InvalidInputValuesException, DataRetrievalException;
}
