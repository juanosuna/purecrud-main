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

package com.purebred.core.view.entityselect;

import com.purebred.core.MainApplication;
import com.purebred.core.view.EntryPoint;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import javax.annotation.PostConstruct;

public abstract class EntitySelect<T> extends EntryPoint<T> {

    private Window popupWindow;

    protected EntitySelect() {
        super();
    }

    public abstract EntitySelectResults<T> getResults();

    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setSizeUndefined();
        popupWindow.setHeight("95%");
    }

    @Override
    public String getEntityCaption() {
        return null;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        addStyleName("p-entity-select");
        getResults().selectPageSize(5);

        addComponent(getSearchForm());
        addComponent(getResults());
    }

    public void open() {
        popupWindow = new Window(getEntityCaption());
        popupWindow.addStyleName("p-entity-select-window");
        popupWindow.addStyleName("opaque");
        VerticalLayout layout = (VerticalLayout) popupWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        popupWindow.setSizeUndefined();
        popupWindow.setModal(true);
        popupWindow.setClosable(true);

        getResults().getEntityQuery().clear();
        getResults().selectPageSize(5);
        getResults().search();
        configurePopupWindow(popupWindow);
        popupWindow.addComponent(this);

        MainApplication.getInstance().getMainWindow().addWindow(popupWindow);
    }

    public void close() {
        MainApplication.getInstance().getMainWindow().removeWindow(popupWindow);
    }
}
