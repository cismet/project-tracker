/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;

import java.security.MessageDigest;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.BasicDTO;
import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.exceptions.LoginFailedException;
import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;

import de.cismet.projecttracker.report.db.entities.Activity;
import de.cismet.projecttracker.report.db.entities.Project;
import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.db.entities.StaffExtern;
import de.cismet.projecttracker.report.db.entities.WorkCategory;
import de.cismet.projecttracker.report.db.entities.WorkPackage;
import de.cismet.projecttracker.report.helper.CalendarHelper;
import de.cismet.projecttracker.report.query.DBManager;

import de.cismet.projecttracker.utilities.DTOManager;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class Search extends BasicServlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(Search.class);

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param   request   servlet request
     * @param   response  servlet response
     *
     * @throws  ServletException  if a servlet-specific error occurs
     * @throws  IOException       if an I/O error occurs
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param   request   servlet request
     * @param   response  servlet response
     *
     * @throws  ServletException  if a servlet-specific error occurs
     * @throws  IOException       if an I/O error occurs
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return  a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    } // </editor-fold>

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
        IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");                                // NOI18N
        resp.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS"); // NOI18N
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");     // NOI18N

        super.service(req, resp); // To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param   request   servlet request
     * @param   response  servlet response
     *
     * @throws  ServletException  if a servlet-specific error occurs
     * @throws  IOException       if an I/O error occurs
     */
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final String username = request.getParameter("username");
        final String password = request.getParameter("password");
        final String workpackage = request.getParameter("workpackage");
        final String project = request.getParameter("project");
        final String user = request.getParameter("user");
        final String description = request.getParameter("description");
        final String dateFilter = request.getParameter("datefilter");
        final String details = request.getParameter("details");
        final String fieldList = request.getParameter("fields");
        final DBManager dbManager = new DBManager(ConfigurationManager.getInstance().getConfBaseDir());
        response.setCharacterEncoding("UTF-8");
        final PrintWriter out = response.getWriter();

        try {
            final Object staff = checklogin(username, password, request.getSession(), dbManager);

            if (staff != null) {
                List<Activity> activities = null;
                final Session hibernateSession = dbManager.getSession();
                Staff userObject = null;
                WorkPackage wp = null;
                Project pr = null;

                if (user != null) {
                    userObject = (Staff)hibernateSession.createCriteria(Staff.class)
                                .add(Restrictions.eq("username", user))
                                .uniqueResult();

                    if (userObject == null) {
                        response.setStatus(400);
                        out.print("user is not valid");
                        return;
                    }
                }

                if (project != null) {
                    pr = (Project)hibernateSession.createCriteria(Project.class).add(Restrictions.eq("name", project))
                                .uniqueResult();

                    if (pr == null) {
                        response.setStatus(400);
                        out.print("workpackage is not valid");
                        logger.warn("Project is not valid " + project);
                        return;
                    }
                }

                if (workpackage != null) {
                    if (pr != null) {
                        wp = (WorkPackage)hibernateSession.createCriteria(WorkPackage.class)
                                    .add(Restrictions.eq("name", workpackage))
                                    .add(Restrictions.eq("project", pr))
                                    .uniqueResult();
                    } else {
                        wp = (WorkPackage)hibernateSession.createCriteria(WorkPackage.class)
                                    .add(Restrictions.eq("name", workpackage))
                                    .uniqueResult();
                    }

                    if (wp == null) {
                        response.setStatus(400);
                        logger.warn("Workpackage is not valid: " + workpackage);
                        out.print("workpackage is not valid");
                        return;
                    }
                }

                if (wp != null) {
                    if (isProjectValidForUser(staff, wp)) {
                        activities = getActivitiesByCriteria(userObject, wp, description, dbManager);
                    } else {
                        response.setStatus(400);
                        out.print("Permission denied");
                        return;
                    }
                } else {
                    response.setStatus(400);
                    out.print("no valid workpackage");
                    return;
                }

                final List<ActivityDTO> validActivities = new ArrayList<ActivityDTO>();
                double totalTime = 0.0;
                final DTOManager dtoManager = new DTOManager();

                if (dateFilter != null) {
                    if (isDateFilterValid(dateFilter)) {
                        final GregorianCalendar fromDate = parseDate(dateFilter, true);
                        final GregorianCalendar tillDate = parseDate(dateFilter, false);

                        for (final Activity a : activities) {
                            final GregorianCalendar d = new GregorianCalendar();
                            d.setTime(a.getDay());

                            if (CalendarHelper.isDateLessOrEqual(fromDate, d)
                                        && CalendarHelper.isDateLessOrEqual(d, tillDate)) {
                                validActivities.add((ActivityDTO)dtoManager.clone(a));
                            }
                        }
                    } else {
                        response.setStatus(400);
                        out.print("date filter is not valid.");
                        return;
                    }
                } else {
                    for (final Activity a : activities) {
                        validActivities.add((ActivityDTO)dtoManager.clone(a));
                    }
                }

                for (final ActivityDTO tmp : validActivities) {
                    totalTime += tmp.getWorkinghours();
                }

                if ((details == null) || !details.equals("true")) {
                    out.print(totalTime);
                } else {
                    JsonFactory f = new JsonFactory();
                    final ObjectMapper mapper = new ObjectMapper();
                    
                    final SimpleModule regularModule = new SimpleModule("NOIOC", new Version(1, 0, 0, null, null, null));
                    regularModule.addSerializer(new DTOSerializer(fieldList));
                    mapper.registerModule(regularModule);
                    
                    final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                    final String json = writer.writeValueAsString(validActivities);
                    out.print(json);
                }
            } else {
                response.setStatus(400);
                out.print("The username/password is not correct.");
            }
        } catch (Exception e) {
            logger.error("login error", e);
            e.printStackTrace();
            response.setStatus(400);
            out.print(e.getMessage());
        } finally {
            dbManager.closeSession();
            out.close();
        }
    }

    /**
     * Validates the given date filter.
     *
     * @param   dateFilter  the date filter to validate
     *
     * @return  true, iff the given date filter is valid
     */
    private boolean isDateFilterValid(final String dateFilter) {
        return parseDate(dateFilter, true) != null;
    }

    /**
     * Extracts the date from the given date filter.
     *
     * @param   dateFilter  the date filter to parse
     * @param   returnFrom  true, if the from date should be returned. the till date will be returned, otherwise.
     *
     * @return  the extracted date or null, if the given date filter is invalid
     */
    private GregorianCalendar parseDate(final String dateFilter, final boolean returnFrom) {
        final StringTokenizer st = new StringTokenizer(dateFilter, ":");

        if (st.countTokens() == 2) {
            final String from = st.nextToken();
            final String till = st.nextToken();

            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            try {
                final Date fromDate = df.parse(from);
                final Date tillDate = df.parse(till);
                final GregorianCalendar result = new GregorianCalendar();

                if (returnFrom) {
                    result.setTime(fromDate);
                } else {
                    result.setTime(tillDate);
                }

                return result;
            } catch (ParseException e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Checks, if the given user has the permission to check the given project.
     *
     * @param   staff  DOCUMENT ME!
     * @param   pr     DOCUMENT ME!
     *
     * @return  true, iff the user has the permission
     */
    private boolean isProjectValidForUser(final Object staff, final WorkPackage pr) {
        if (staff instanceof Staff) {
            return true;
        } else if (staff instanceof StaffExtern) {
            final Set<WorkPackage> wps = ((StaffExtern)staff).getWorkpackages();

            return wps.contains(pr);
        } else {
            return false;
        }
    }

    /**
     * Determines all activities, which fulfil the given criterias.
     *
     * @param   user         staff the staff, whose activities should be retrieved
     * @param   criteria     an further criteria for the activities, which shluld be retrieved. This can be an object of
     *                       the type WorkPackage, WorkCategory or Project
     * @param   description  DOCUMENT ME!
     * @param   dbManager    DOCUMENT ME!
     *
     * @return  all activities, which fulfil the given criterias
     *
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     * @throws  DataRetrievalException       DOCUMENT ME!
     * @throws  PermissionDenyException      DOCUMENT ME!
     * @throws  NoSessionException           DOCUMENT ME!
     */
    private List<Activity> getActivitiesByCriteria(final Staff user,
            final Object criteria,
            final String description,
            final DBManager dbManager) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get activities: " + criteria.getClass().getName());
        }
        Session hibernateSession = null;

        try {
            hibernateSession = dbManager.getSession();
            Criteria crit = null;

            if (user != null) {
                crit = hibernateSession.createCriteria(Activity.class).add(Restrictions.eq("staff", user));
            }

            if (criteria instanceof Project) {
                if (crit != null) {
                    crit.createCriteria("workPackage").add(Restrictions.eq("project", criteria));
                } else {
                    crit = hibernateSession.createCriteria(Activity.class).add(Restrictions.eq("project", criteria));
                }
            } else if (criteria instanceof WorkPackage) {
                if (crit != null) {
                    crit.add(Restrictions.eq("workPackage", criteria));
                } else {
                    crit = hibernateSession.createCriteria(Activity.class)
                                .add(Restrictions.eq("workPackage", criteria));
                }
            } else if (criteria instanceof WorkCategory) {
                if (crit != null) {
                    crit.add(Restrictions.eq("workCategory", criteria));
                } else {
                    crit = hibernateSession.createCriteria(Activity.class)
                                .add(Restrictions.eq("workCategory", criteria));
                }
            } else {
                throw new DataRetrievalException("The criteria has a not supported type");
            }

            if (description != null) {
                crit.add(Restrictions.ilike("description", "%" + description + "%"));
            }
            crit.add(Restrictions.isNotNull("day")).addOrder(Order.asc("day"));
            final List<Activity> result = crit.list();
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " activities found");
            }

            // convert the server version of the Project type to the client version of the Project type
            return result;
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        }
    }

    /**
     * Check the login data.
     *
     * @param   username   DOCUMENT ME!
     * @param   pasword    DOCUMENT ME!
     * @param   session    DOCUMENT ME!
     * @param   dbManager  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  LoginFailedException    DOCUMENT ME!
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    public Object checklogin(final String username,
            final String pasword,
            final HttpSession session,
            final DBManager dbManager) throws LoginFailedException, DataRetrievalException {
        try {
            final Session hibernateSession = dbManager.getSession();

            final MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(pasword.getBytes());
            final byte[] sha1 = md.digest();

            final Staff staff = (Staff)hibernateSession.createCriteria(Staff.class)
                        .add(Restrictions.and(
                                    Restrictions.eq("username", username),
                                    Restrictions.eq("password", sha1)))
                        .uniqueResult();

            if (staff == null) {
                final StaffExtern staffExtern = (StaffExtern)hibernateSession.createCriteria(StaffExtern.class)
                            .add(Restrictions.and(
                                        Restrictions.eq("username", username),
                                        Restrictions.eq("password", sha1)))
                            .uniqueResult();
//                final StaffExtern staffExtern = (StaffExtern)hibernateSession.createCriteria(StaffExtern.class)
//                            .add(Restrictions.eq("username", username))
//                            .uniqueResult();

                return staffExtern;
            }
            return staff;
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        }
    }
    
    
    public class DTOSerializer extends StdSerializer<BasicDTO> {
        private final List<String> fieldList = new ArrayList<String>();
        //~ Constructors -----------------------------------------------------------

        /**
         * Creates a new CidsAttributeJsonSerializer object.
         */
        public DTOSerializer(String fieldList) {
            super(BasicDTO.class);
            
            if (fieldList != null) {
                for (String s : fieldList.split(",")) {
                    this.fieldList.add(s);
                }
            }
        }

        //~ Methods ----------------------------------------------------------------
        @Override
        public void serialize(final BasicDTO dto, final JsonGenerator _jg, final SerializerProvider sp) throws IOException,
            JsonGenerationException {
            serialize(dto, _jg, sp, "");
        }
        
        public void serialize(final BasicDTO dto, final JsonGenerator _jg, final SerializerProvider sp, String object) throws IOException,
            JsonGenerationException {
            _jg.writeStartObject();

            Method[] methods = dto.getClass().getMethods();

            for (Method tmpMethod : methods) {
                String name = tmpMethod.getName();
                
                if (name.startsWith("get") && !name.equals("getClass") && name.length() > 3) {
                    Class[] parameter = tmpMethod.getParameterTypes();
                    
                    if (parameter.length == 0) {
                        Class retType = tmpMethod.getReturnType();
                        Object o = null;
                        
                        try {
                            o = tmpMethod.invoke(dto, (Object[])null);
                        } catch (Exception e) {
                            //nothing to do
                        }
                        
                        if (retType.isPrimitive()) {
                            if (isFieldValid(o, object, name)) {
                                _jg.writeObjectField(getAttributeName(name), o);
                            }
                        } else if (String.class.isAssignableFrom(retType)) {
                            if (isFieldValid(o, object, name)) {
                                _jg.writeObjectField(getAttributeName(name), (String)o);
                            }
                        } else if (BasicDTO.class.isAssignableFrom(retType)){
                            if (isSubObjectValid(o, object, name)) {
                                _jg.writeFieldName(getAttributeName(name));
                                serialize((BasicDTO)o, _jg, sp, (object.equals("") ? "" : object + ".") + getAttributeName(name) );
                            }
                        } else if (retType.isArray() && o instanceof BasicDTO) {
                            if (isSubObjectValid(o, object, name)) {
                                _jg.writeFieldName(getAttributeName(name));
                                _jg.writeStartArray();
                                serialize((BasicDTO)o, _jg, sp, (object.equals("") ? "" : object + ".") + getAttributeName(name) );
                                _jg.writeEndArray();
                            }
                        }
                    }
                }
            }
            
            _jg.writeEndObject();
        }
        
        private boolean isFieldValid(Object value, String subObject, String methodName) {
            return value != null && (fieldList.isEmpty() || fieldList.contains((subObject.equals("") ? "" : subObject + ".") + getAttributeName(methodName)));
        }
        
        private boolean isSubObjectValid(Object value, String subObject, String methodName) {
            return value != null && ( fieldList.isEmpty() || isValueThatStartsWithContained((subObject.equals("") ? "" : subObject + ".") + getAttributeName(methodName)) );
        }
        
        private boolean isValueThatStartsWithContained(String val) {
            for (String tmp : fieldList) {
                if (tmp.startsWith(val)) {
                    return true;
                }
            }
        
            return false;
        }
        
        private String getAttributeName(String name) {
            return name.substring(3, 4).toLowerCase() + name.substring(4);
        }

        @Override
        protected PropertyFilter findPropertyFilter(SerializerProvider provider, Object filterId, Object valueToFilter) throws JsonMappingException {
            return super.findPropertyFilter(provider, filterId, valueToFilter);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        protected boolean isIntraObjectCacheEnabled() {
            return false;
        }
    }
}
