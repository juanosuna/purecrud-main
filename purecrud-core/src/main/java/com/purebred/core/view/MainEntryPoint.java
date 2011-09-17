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

import com.purebred.core.MainApplication;
import com.purebred.core.view.field.LabelDepot;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * A main entry point for the user to work with entities of a particular type.
 *
 * The difference between main entry point and a regular entry point: a main entry point is presented to the user
 * as a Vaadin Tab in the initial "home page" of the application, a TabSheet. A regular entry point can be
 * presented anywhere in the application, e.g. a pop-up EntitySelect  also provides a search form
 * and results for selecting a entity in a many-to-one relationship.
 *
 * @param <T> type of business entity for this entry point
 */
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
        addComponent(getResults());
    }

    @Override
    public void postWire() {
        super.postWire();

        MainApplication.getInstance().setLogoutURL("mvc/login.do");
        logoutButton.addListener(Button.ClickEvent.class, MainApplication.getInstance(), "logout");
    }
}
