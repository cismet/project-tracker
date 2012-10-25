/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.utilities;

//import de.cismet.projecttracker.client.common.ui.*;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.report.db.entities.Activity;
import de.cismet.projecttracker.report.db.entities.Project;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author therter
 */
public class EmailTaskNotice {

//    private static final String MAIN_STYLES = "alert-message block-message timebox";
    private static final String MAIN_STYLES = "alert alert-block timebox";
    private static final String[] ADDITIONAL_PROJECT_STYLES = {"proj1", "proj2", "proj3", "proj4", "proj5", "proj6", "proj7"};
    private static final HashMap<Long, Integer> projectStyle = new HashMap<Long, Integer>();
    private String styles;
    protected Activity activity;

    static {
        projectStyle.put((long) 1, 0);
        projectStyle.put((long) 2, 1);
        projectStyle.put((long) 18, 2);
        projectStyle.put((long) 21, 3);
        projectStyle.put((long) 20, 4);
        projectStyle.put((long) 10, 5);
    }

    public EmailTaskNotice(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
//        lab = new HTML();
        setColour();
    }

    private void setColour() {
        if (activity.getKindofactivity() >= 1) {
            if (activity.getKindofactivity() == 1) {
                styles = MAIN_STYLES + " beginOfDay";
            } else if (activity.getKindofactivity() == 2) {
                styles = MAIN_STYLES + " endOfDay";
            } else {
                styles = MAIN_STYLES + " lockDay";
            }
        } else {
            if (activity.getWorkPackage() != null && activity.getWorkPackage().getProject() != null) {

                List<ProjectDTO> proj = null;
                DBManagerWrapper dbManager = new DBManagerWrapper();

                try {
                    List<Project> result = (List<Project>) dbManager.getAllObjects(Project.class);

                    // convert the server version of the Project type to the client version of the Project type
                    proj = (ArrayList<ProjectDTO>) new DTOManager().clone(result);
                } catch (DataRetrievalException ex) {
                    Logger.getLogger(EmailTaskNotice.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidInputValuesException ex) {
                    Logger.getLogger(EmailTaskNotice.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    dbManager.closeSession();
                }

                if (proj != null) {
                    long projectId = activity.getWorkPackage().getProject().getId();
                    Integer index = projectStyle.get(projectId);

                    if (index == null) {
                        index = 6;
                    }

                    if (index < 0 || index >= ADDITIONAL_PROJECT_STYLES.length) {
                        index = ADDITIONAL_PROJECT_STYLES.length - 1;
                    }
//                    String a = mainPanel.getStyleName();
                    styles = MAIN_STYLES + " "+ADDITIONAL_PROJECT_STYLES[index];
                } else {
//                    Timer t = new Timer() {
//                        @Override
//                        public void run() {
//                            setColour();
//                        }
//                    };
//
//                    t.schedule(2000);
                }
            }
        }
    }


    protected String getTextFromActivity() {
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        if (activity.getKindofactivity() != 0) {
            String text = "";
            if (activity.getKindofactivity() == 1) {
                //begin of day
                text = "Begin of Work Activity";
                text += "<br/> at <br/>";
                text += dateTimeFormat.format(activity.getDay());

            } else if (activity.getKindofactivity() == 2) {
                //end of day
                text = "End of Work Activity";
                text += "<br/> at <br/>";
                text += dateTimeFormat.format(activity.getDay());
            } else {
                //lock
                text = "Locked Day";
                text += "<br/> at <br/>";
                text += dateFormat.format(activity.getDay());
            }
            return text;
        } else {
            String desc = getDesccription(activity.getDescription());
            StringBuilder text = new StringBuilder();

            if (activity.getWorkCategory() != null && activity.getWorkCategory().getId() == WorkCategoryDTO.TRAVEL) {
                text.append("Travel: ").append(activity.getWorkPackage().getAbbreviation());
            } else {
                text.append(activity.getWorkPackage().getAbbreviation());
            }

            double hours = Math.round(activity.getWorkinghours() * 100) / 100.0;
            text.append("<br />").append(desc);
            if (hours != 0.0) {
                text.append("<br />").append(doubleToHours(hours)).append(" hours");
            }

            return text.toString();
        }
    }
    
    private String doubleToHours(double ihours) {
        double hours = Math.abs(ihours);
        int minutes = (int) Math.round((hours - ((int) hours)) * 60);

        //vermeidet Angaben wie 7:60
        if (minutes == 60) {
            ++hours;
            minutes = 0;
        }

        return (ihours < 0.0 ? "-" : "") + (int) hours + ":" + IntToDoubleDigit(minutes);
    }
    
        private static String IntToDoubleDigit(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }

    protected String getDesccription(String desc) {
        if (desc == null) {
            return "";
        }
        int i = desc.indexOf("(@");

        if (i != -1) {
            String result = desc.substring(0, i);
            return result;
        } else {
            return desc;
        }
    }

    protected String getTooltipTextFromActivity() {
        if (activity.getKindofactivity() == 0) {
            StringBuilder text = new StringBuilder(activity.getWorkPackage().getProject().getName());
            text.append("\n").append(activity.getWorkPackage().getName());

            if (activity.getDescription() != null) {
                text.append("\n").append(activity.getDescription());
            }

            return text.toString();
        } else {
            return "";
        }
    }


    @Override
    public String toString() {

        String result = "<div  title=\"" + getTooltipTextFromActivity() + "\" class=\"" + styles + "\">"
                + "<div title=\"" + getTooltipTextFromActivity() + "\">" + getTextFromActivity() + "</div>"
                + "</div>";
        return result;
    }
}
