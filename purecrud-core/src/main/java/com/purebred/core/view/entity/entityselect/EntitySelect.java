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

package com.purebred.core.view.entity.entityselect;

import com.purebred.core.view.entity.EntryPoint;
import com.vaadin.ui.Window;

import javax.annotation.PostConstruct;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntitySelect<T> extends EntryPoint<T> {

    protected EntitySelect() {
        super();
    }

    public abstract EntitySelectResults<T> getResultsComponent();

    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setSizeUndefined();
    }

    public String getEntityCaption() {
        return null;
    }

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        getResultsComponent().getEntityQuery().setPageSize(5);
    }
}
