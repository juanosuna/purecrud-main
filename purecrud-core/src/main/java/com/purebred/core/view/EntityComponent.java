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

import com.purebred.core.view.util.MessageSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Any generic entity component that contains some results.
 * @param <T>
 */
public abstract class EntityComponent<T> extends CustomComponent {

    @Resource
    protected MessageSource entityMessageSource;

    @Resource
    protected MessageSource uiMessageSource;

    protected EntityComponent() {
    }

    /**
     * Get results.
     *
     * @return results
     */
    public abstract Results getResults();

    /**
     * Get the caption used to represent this type of entity, e.g. to be displayed in tab.
     *
     * @return display caption
     */
    public abstract String getEntityCaption();

    @Override
    public String getCaption() {
        return entityMessageSource.getMessageWithDefault(getEntityCaption());
    }

    /**
     * Called after Spring constructs this bean. Overriding methods should call super.
     */
    @PostConstruct
    public void postConstruct() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setCompositionRoot(layout);

        setCustomSizeUndefined();
    }

    private void setCustomSizeUndefined() {
        setSizeUndefined();
        getCompositionRoot().setSizeUndefined();
    }

    /**
     * Can be overridden if any initialization is required after all Spring beans have been wired.
     * Overriding methods should call super.
     */
    public void postWire() {
    }

    @Override
    public void addComponent(Component c) {
        ((ComponentContainer) getCompositionRoot()).addComponent(c);
    }
}
