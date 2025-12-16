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


import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import org.apache.log4j.Logger;


import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.query.DBManager;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class OverviewWorkinghours extends BasicServlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(OverviewWorkinghours.class);
    private static final String QUERY = "select\n" +
            "  	EXTRACT(YEAR FROM a.day) as jahr,\n" +
            "	s.\"name\",\n" +
            "	s.firstname,\n" +
            "	case when p.\"name\" ilike 'MV' then 'MV' when p.name ilike 'Wuppertal' then 'Wuppertal' else 'sonstige' end as projekt,\n" +
            "	case when cc.\"name\" = 'fakturierbar' then true else false end as fakturierbar,\n" +
            "	sum(case when  (wp.name ilike 'Urlaub' or p.name ilike 'Urlaub') and a.workinghours = 0.0 then 8.0 else a.workinghours end) as Stunden \n" +
            "from\n" +
            "	activity a	\n" +
            "	join staff s on (a.staffid  = s.id)\n" +
            "	join work_package wp on (a.workpackageid = wp.id)\n" +
            "	join project p on (wp.project = p.id)\n" +
            "	join cost_category cc on (wp.costcategoryid = cc.id)\n" +
            "where a.\"day\" >= '%1s-01-01' and a.\"day\"::DATE <= '%2s'::DATE and s.firstname <> 'Auftrag und Korrektur'\n" +
            "	and wp.name not in ('Kinderkrankenschein', 'Sonderurlaub', 'krank', '*Freizeitausgleich', '*Pause', 'Freistellung', 'Elternzeit')\n" +
            "	and p.name not in ('Sonderurlaub', '*Freizeitausgleich', '*Pause', 'Krank', 'bitte prüfen', 'Abgleich Zeitkonto')\n" +
            "	and s.name not in ('Admin')\n" +
            "	and exists (select st.id, sum(workinghours) from activity ac join staff st on (ac.staffid  = st.id) join work_package wp on (ac.workpackageid = wp.id) join cost_category cca on (wp.costcategoryid = cca.id) where st.id = s.id and cca.\"name\" = 'fakturierbar' and EXTRACT(YEAR FROM ac.day) = EXTRACT(YEAR FROM a.day) group by 1 having sum(workinghours) > 1)\n" +
            "group by 1, 2, 3, 4, 5\n" +
            "having sum(case when (wp.name ilike 'Urlaub' or p.name ilike 'Urlaub') and a.workinghours = 0.0 then 8.0 else a.workinghours end)  > 1\n" +
            "order by 1, 2, 3, 4";

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
        String startYear = request.getParameter("from");
        String till = request.getParameter("till");
        final DBManager dbManager = new DBManager(ConfigurationManager.getInstance().getConfBaseDir());
        response.setCharacterEncoding("UTF-8");
        final PrintWriter out = response.getWriter();
        double finalHours = 0;
        Connection con = null;
        
        if (startYear == null) {
            startYear = "2020";
        } else {
            try {
                Integer.parseInt(startYear);
            } catch (NumberFormatException e) {
                //invalid year, use 2020
                startYear = "2020";
            }
        }
        
        if (till == null) {
            GregorianCalendar now = new GregorianCalendar();
            
            till = now.get(GregorianCalendar.YEAR) + "-" + (now.get(GregorianCalendar.MONTH) + 1) + "-" + now.get(GregorianCalendar.DAY_OF_MONTH);
        }
        

        try {
            final Object staff = checklogin(username, password, request.getSession(), dbManager);

            if (staff != null) {
                boolean isAdmin = (((Staff)staff).getPermissions() & ProjectTrackerEntryPoint.ADMIN_PERMISSION) == ProjectTrackerEntryPoint.ADMIN_PERMISSION;
                
                if (!isAdmin) {
                    response.setStatus(403);
                    LOG.warn("invalid permission");
                    out.print("forbidden");
                    return;
                }

                con = dbManager.getDatabaseConnection();
                Statement statement = con.createStatement();
                ResultSet rs = statement.executeQuery(String.format(QUERY, startYear, till));
                
                out.println("\"jahr\";\"name\",\"firstname\";\"projekt\";\"fakturierbar\";\"stunden\"");
                
                if (rs != null) {
                    while (rs.next()) {
                        Integer year = rs.getInt(1);
                        String name = rs.getString(2);
                        String firstName = rs.getString(3);
                        String project = rs.getString(4);
                        boolean fac = rs.getBoolean(5);
                        Double hours = rs.getDouble(6);
                        
                        StringBuilder sb = new StringBuilder( (year != null ? year.toString() : "") );
                        sb.append(";").append(name).append(";").append(firstName).append(";").append(project).append(";").append(fac).append(";").append(hours);
                        
                        out.println(sb.toString());
                    }
                }
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
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    LOG.error("Cannot close db connection", ex);
                }
            }
            dbManager.closeSession();
            out.close();
        }
    }
}
