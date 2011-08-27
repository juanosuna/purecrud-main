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

package com.purebred.core.view.entity;

import com.purebred.core.view.MainApplication;
import com.purebred.core.view.entity.field.LabelDepot;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

public abstract class MainEntryPoint<T> extends EntryPoint<T> {

    @Resource
    private LabelDepot labelDepot;

    private Button logoutButton;

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        labelDepot.putEntityLabel(getEntityType().getName(), getEntityCaption());

        HorizontalLayout searchAndLogout = new HorizontalLayout();
        searchAndLogout.setSizeFull();
        searchAndLogout.addComponent(getSearchForm());

        logoutButton = new Button(null);
        logoutButton.setDescription(uiMessageSource.getMessage("mainApplication.logout"));
        logoutButton.setSizeUndefined();
        logoutButton.addStyleName("borderless");
        logoutButton.setIcon(new ThemeResource("icons/16/logout.png"));

        searchAndLogout.addComponent(logoutButton);
        searchAndLogout.setComponentAlignment(logoutButton, Alignment.TOP_RIGHT);

        addComponent(searchAndLogout);
        addComponent(getResultsComponent());
    }

    @Override
    public void postWire() {
        super.postWire();

        MainApplication.getInstance().setLogoutURL("mvc/login.do");
        logoutButton.addListener(Button.ClickEvent.class, MainApplication.getInstance(), "logout");
    }
}
