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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;

import org.apache.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;



import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.cismet.projecttracker.report.db.entities.Activity;
import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.db.entities.WorkPackage;
import de.cismet.projecttracker.report.query.DBManager;
import de.cismet.projecttracker.utilities.DevProperties;
import java.io.FileReader;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class HoursOnClosedIssues extends BasicServlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(HoursOnClosedIssues.class);
    private static final String GIT_CLOSED_ISSUES_URL = "https://api.github.com/repos/cismet/wupp/issues?state=closed&milestone=%1s&per_page=1000";
    private final Map<Integer, String> issueMap = new HashMap<Integer, String>();

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
        final String milestone = request.getParameter("milestone");
        final String workpackage = request.getParameter("workpackage");
        final DBManager dbManager = new DBManager(ConfigurationManager.getInstance().getConfBaseDir());
        response.setCharacterEncoding("UTF-8");
        final PrintWriter out = response.getWriter();
        double finalHours = 0;

        try {
            final Object staff = checklogin(username, password, request.getSession(), dbManager);

            if (staff instanceof Staff) {
                boolean isAdmin = (((Staff)staff).getPermissions() & ProjectTrackerEntryPoint.ADMIN_PERMISSION) == ProjectTrackerEntryPoint.ADMIN_PERMISSION;
                
                if (!isAdmin) {
                    response.setStatus(403);
                    LOG.warn("invalid permission");
                    out.print("forbidden");
                    return;
                }
                
                if (milestone != null) {
                    URL url = new URL(String.format(getGitQuery(), milestone));
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + getToken());
                    
                    InputStream is = connection.getInputStream();
                    final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
                    JsonNode node = mapper.readTree(is);
                    
                    if (node.isArray()) {
                        Iterator<JsonNode> it = node.iterator();
                        
                        while (it.hasNext()) {
                            JsonNode tmpNode = it.next();
                            
                            int number = tmpNode.get("number").asInt();
                            String title = tmpNode.get("title").asText();
                            
                            issueMap.put(number, title);
                        }
                    } else {
                        response.setStatus(400);
                        LOG.warn("invalid response from github");
                        out.print("invalid response from github");
                        return;
                    }
                } else {
                    response.setStatus(400);
                    LOG.warn("milestone is not set");
                    out.print("milestone not set");
                    return;
                }
                final Session hibernateSession = dbManager.getSession();
                WorkPackage wp = null;

                if (workpackage != null) {
                    wp = (WorkPackage)hibernateSession.createCriteria(WorkPackage.class)
                                .add(Restrictions.eq("name", workpackage))
                                .uniqueResult();

                    if (wp == null) {
                        response.setStatus(400);
                        LOG.warn("Workpackage is not valid: " + workpackage);
                        out.print("workpackage is not valid");
                        return;
                    }
                }

                final Criteria crit = hibernateSession.createCriteria(Activity.class).add(Restrictions.eq("workPackage.id", wp.getId()));
                final ArrayList<Activity> result = new ArrayList<Activity>();
                result.addAll(crit.list());
                
                for (Activity a : result) {
                    if ((a.getDescription() != null && a.getDescription().contains("#"))) {
                        String desc = a.getDescription();
                        String issue = getIssue(desc);
                        int issueNumber = -1;

                        if (issue.startsWith("#")) {
                            issue = issue.substring(1);
                        }
                        
                        try {
                            issueNumber = Integer.parseInt(issue);
                        } catch (NumberFormatException e) {
                            //nothing to do
                        }
                        
                        if (issueNumber != -1) {
                            if (issueMap.containsKey(issueNumber)) {
                                finalHours += a.getWorkinghours();
                            }
                        }
                    }
                }
                
                
                out.print("{\"hours\":" + finalHours + "}");
            } else {
                response.setStatus(400);
                out.print("The username/password is not correct.");
            }
        } catch (Exception e) {
            LOG.error("login error", e);
            e.printStackTrace();
            response.setStatus(400);
            out.print(e.getMessage());
        } finally {
            dbManager.closeSession();
            out.close();
        }
    }
    
    private String getToken() {
        final ServletContext context = getServletContext();
        FileReader input = null;
        final Properties apiConfig = new Properties();
        
        try {
            input = new FileReader(context.getInitParameter("confBaseDir") + System.getProperty("file.separator")
                            + "api.properties");
            apiConfig.load(input);
            
            return apiConfig.getProperty("token");
        } catch (IOException e) {
            LOG.error("Cannot open and load dev.properties file. Setting developing mode to false", e);
            DevProperties.getInstance().setDevMode(false);
            return null;
        }
    }
    
    protected String getGitQuery() {
        return GIT_CLOSED_ISSUES_URL;
    }

    
    private String getIssue(String desc) {
        RegExp regExp = RegExp.compile("#(\\d+)", "g");
        MatchResult matcher = regExp.exec(desc);
        int lastIndex = 0;
        String issueNumber = null;
        
        if (matcher != null) {
            for (int i = 0; i < matcher.getGroupCount(); i++) {
                String groupStr = matcher.getGroup(i);
                
                if (groupStr.startsWith("#")) {
                    issueNumber = groupStr.substring(0);
                    break;
                }
            }
        }
        
        return issueNumber;
    }
}
