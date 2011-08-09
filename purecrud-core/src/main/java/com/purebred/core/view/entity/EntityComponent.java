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

import com.purebred.core.view.MessageSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

public abstract class EntityComponent<T> extends CustomComponent {

    @Resource
    protected MessageSource entityMessageSource;

    @Resource
    protected MessageSource uiMessageSource;

    protected EntityComponent() {
    }

    public abstract ResultsComponent getResultsComponent();

    public abstract String getEntityCaption();

    public String getCaption() {
        return entityMessageSource.getMessageWithDefault(getEntityCaption());
    }

    @PostConstruct
    public void postConstruct() {
        wireRelationships();

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setCompositionRoot(layout);

        setCustomSizeUndefined();
    }

    private void wireRelationships() {
        getResultsComponent().postWire();
    }

    public void setCustomSizeUndefined() {
        setSizeUndefined();
        getCompositionRoot().setSizeUndefined();
    }

    @Override
    public void addComponent(Component c) {
        ((ComponentContainer) getCompositionRoot()).addComponent(c);
    }
}
