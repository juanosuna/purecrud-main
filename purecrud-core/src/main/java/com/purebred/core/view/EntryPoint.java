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

import com.purebred.core.util.ReflectionUtil;

import javax.annotation.PostConstruct;

/**
 * An entry point is simply comprised of a search form and a list of results.
 *
 * @param <T> type of business entity for this entry point
 */
public abstract class EntryPoint<T> extends EntityComponent<T> {

    protected EntryPoint() {
        super();
    }

    /**
     * Get the search form component of this entry point
     *
     * @return search form component
     */
    public abstract SearchForm getSearchForm();

    /**
     * Get the results component for this entry point.
     * @return
     */
    public abstract Results<T> getResults();

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        addStyleName("p-entry-point");
    }

    @Override
    public void postWire() {
        super.postWire();
        getSearchForm().setResults(getResults());
        getSearchForm().postWire();
        getResults().postWire();
    }


    /**
     * Type of business entity for this entry point.
     *
     * @return type of business entity for this entry point
     */
    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }
}
