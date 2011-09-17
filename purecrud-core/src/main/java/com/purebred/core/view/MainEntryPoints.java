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
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.purebred.core.view;

import com.purebred.core.entity.security.AbstractUser;
import com.purebred.core.security.SecurityService;
import com.vaadin.ui.TabSheet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Main entry points into the PureCRUD application, presented as a Vaadin Tabsheet.
 *
 * Each entry point is presented as a Vaadin Tab.
 */
public abstract class MainEntryPoints extends TabSheet {

    @Resource
    private SecurityService securityService;

    /**
     * Get all the entry points of the application, including those the user doesn't
     * have permission to view.
     *
     * Implementer should return all entry points and let PureCRUD take care of
     * security handling.
     *
     * @return all entry points into the application
     */
    public abstract List<MainEntryPoint> getEntryPoints();

    /**
     * Get all entry points that the user has security permission to view.
     *
     * @return all entry points user is permitted to view
     */
    public final List<MainEntryPoint> getViewableEntryPoints() {
        List<MainEntryPoint> entryPoints = getEntryPoints();
        List<MainEntryPoint> viewableEntryPoints = new ArrayList<MainEntryPoint>();

        for (MainEntryPoint entryPoint : entryPoints) {
            AbstractUser user = securityService.getCurrentUser();

            if (user.isViewAllowed(entryPoint.getEntityType().getName())
                    && !entryPoint.getResults().getDisplayFields().getViewablePropertyIds().isEmpty()) {

                viewableEntryPoints.add(entryPoint);
            }
        }

        return viewableEntryPoints;
    }

    /**
     * Name of the Vaadin theme used to style this application.
     * Default is "pureCrudTheme." Implementer can override this name and provide
     * their custom theme.
     *
     * @return name of Vaadin theme
     */
    public String getTheme() {
        return "pureCrudTheme";
    }

    /**
     * Called after Spring constructs this bean. Overriding methods should call super.
     */
    @PostConstruct
    protected void postConstruct() {
        setSizeUndefined();
        List<MainEntryPoint> entryPoints = getViewableEntryPoints();
        for (MainEntryPoint entryPoint : entryPoints) {
            addTab(entryPoint);
        }

        addListener(new TabChangeListener());
        if (entryPoints.size() > 0) {
            entryPoints.get(0).getResults().search();
        }
    }

    /**
     * Can be overridden if any initialization is required after all Spring beans have been wired.
     * Overriding methods should call super.
     */
    public void postWire() {
        List<MainEntryPoint> entryPoints = getViewableEntryPoints();
        for (EntryPoint entryPoint : entryPoints) {
            entryPoint.postWire();
        }
    }

    private class TabChangeListener implements SelectedTabChangeListener {

        @Override
        public void selectedTabChange(SelectedTabChangeEvent event) {
            MainEntryPoint entryPoint = (MainEntryPoint) getSelectedTab();
            entryPoint.getResults().search();
            if (entryPoint.getResults() instanceof CrudResults) {
                ((CrudResults) entryPoint.getResults()).applySecurityToCRUDButtons();
            }
        }
    }
}
