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

import com.purebred.core.view.entity.EntryPoint;
import com.vaadin.ui.TabSheet;

import javax.annotation.PostConstruct;
import java.util.List;

public abstract class MainEntryPoints extends TabSheet {

    public abstract List<EntryPoint> getEntryPoints();

    public String getTheme() {
        return "pureCrudTheme";
    }

    @PostConstruct
    public void postConstruct() {
        setSizeUndefined();
        List<EntryPoint> entryPoints = getEntryPoints();
        for (EntryPoint entryPoint : entryPoints) {
            addTab(entryPoint);
        }

        addListener(new TabChangeListener());
        entryPoints.get(0).getResultsComponent().search();
    }

    private class TabChangeListener implements SelectedTabChangeListener {

        @Override
        public void selectedTabChange(SelectedTabChangeEvent event) {
            EntryPoint entryPoint = (EntryPoint) getSelectedTab();
            entryPoint.getResultsComponent().search();
        }
    }
}
