/*
 * BROWN BAG CONFIDENTIAL
 *
 * Copyright (c) 2011 Brown Bag Consulting LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.purebred.core.util;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationServlet;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class SpringApplicationServlet extends ApplicationServlet {

    private final Logger log = Logger.getLogger(getClass());

    private WebApplicationContext webApplicationContext;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        log.debug("initializing SpringApplicationServlet");
        try {
            webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletConfig.getServletContext());
        } catch (IllegalStateException e) {
            throw new ServletException(e);
        }
    }

    protected final WebApplicationContext getWebApplicationContext() throws ServletException {
        if (webApplicationContext == null) {
            throw new ServletException("init() must be invoked before WebApplicationContext can be retrieved");
        }
        return webApplicationContext;
    }

    protected final AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws ServletException {
        try {
            return getWebApplicationContext().getAutowireCapableBeanFactory();
        } catch (IllegalStateException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request) throws ServletException {
        Class<? extends Application> applicationClass;
        try {
            applicationClass = getApplicationClass();
        } catch (ClassNotFoundException e) {
            throw new ServletException(e);
        }
        AutowireCapableBeanFactory beanFactory = getAutowireCapableBeanFactory();
        try {
            Application application = beanFactory.createBean(applicationClass);
            log.debug("Created new Application of type: " + applicationClass);
            return application;
        } catch (BeansException e) {
            throw new ServletException(e);
        }
    }
}

