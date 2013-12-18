/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker;

import java.io.IOException;

import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link Filter} to add cache control headers for GWT generated files to ensure that the correct files get cached.
 *
 * @author   See Wah Cheng
 * @version  $Revision$, $Date$
 * @created  24 Feb 2009
 */
public class GWTCacheControlFilter implements Filter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public void destroy() {
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        final String requestURI = httpRequest.getRequestURI();

        if (requestURI.contains(".nocache.")) {
            final Date now = new Date();
            final HttpServletResponse httpResponse = (HttpServletResponse)response;
            httpResponse.setDateHeader("Date", now.getTime());
            // one day old
            httpResponse.setDateHeader("Expires", now.getTime() - 86400000L);
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        }

        filterChain.doFilter(request, response);
    }
}
