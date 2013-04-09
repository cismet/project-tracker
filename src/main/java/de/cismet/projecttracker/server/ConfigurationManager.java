/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.server;

import javax.servlet.ServletContext;

/**
 *
 * @author daniel
 */
public class ConfigurationManager {

    private ServletContext servletContext = null;
    private static ConfigurationManager instance;
    
    private ConfigurationManager(){
        
    }
    
    public static ConfigurationManager getInstance(){
        if(instance == null){
            instance =  new ConfigurationManager();
        }
        return instance;
    } 

    public void setContext(ServletContext context){
        servletContext = context;
    }
    public String getConfBaseDir() {
       return servletContext.getInitParameter("confBaseDir");
    }
    
}
