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
import com.purebred.core.view.entity.EntryPoint;
import com.purebred.core.view.entity.MainEntryPoint;
import com.purebred.core.view.entity.Results;
import com.vaadin.ui.TabSheet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public abstract class MainEntryPoints extends TabSheet {

    @Resource
    private SecurityService securityService;

    public abstract List<MainEntryPoint> getEntryPoints();

    public List<MainEntryPoint> getViewableEntryPoints() {
        List<MainEntryPoint> entryPoints = getEntryPoints();
        List<MainEntryPoint> viewableEntryPoints = new ArrayList<MainEntryPoint>();

        for (MainEntryPoint entryPoint : entryPoints) {
            AbstractUser user = securityService.getCurrentUser();

            if (user.isViewAllowed(entryPoint.getEntityType().getName())) {
                viewableEntryPoints.add(entryPoint);
            }
        }

        return viewableEntryPoints;
    }

    public String getTheme() {
        return "pureCrudTheme";
    }

    @PostConstruct
    public void postConstruct() {
        setSizeUndefined();
        List<MainEntryPoint> entryPoints = getViewableEntryPoints();
        for (MainEntryPoint entryPoint : entryPoints) {
            addTab(entryPoint);
        }

        addListener(new TabChangeListener());
        if (entryPoints.size() > 0) {
            entryPoints.get(0).getResultsComponent().search();
        }
    }

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
            entryPoint.getResultsComponent().search();
            if (entryPoint.getResultsComponent() instanceof Results) {
                ((Results) entryPoint.getResultsComponent()).applySecurityToCRUDButtons();
            }
        }
    }
}
