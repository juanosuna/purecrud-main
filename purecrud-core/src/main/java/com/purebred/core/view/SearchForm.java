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

import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import javax.annotation.PostConstruct;

/**
 * Search form bound to a POJO that contains query criteria.
 *
 * @param <T> type of POJO
 */
public abstract class SearchForm<T> extends FormComponent<T> {

    private Results results;

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        getForm().addStyleName("p-search-form");
    }

    @Override
    public void postWire() {
        super.postWire();
        BeanItem beanItem = createBeanItem(getResults().getEntityQuery());
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());
    }

    /**
     * Get results UI component connected to this search form.
     *
     * @return results UI component
     */
    public Results getResults() {
        return results;
    }

    void setResults(Results results) {
        this.results = results;
    }

    @Override
    protected void createFooterButtons(HorizontalLayout footerLayout) {
        footerLayout.setSpacing(true);
        footerLayout.setMargin(true);

        Button clearButton = new Button(uiMessageSource.getMessage("entitySearchForm.clear"), this, "clear");
        clearButton.setDescription(uiMessageSource.getMessage("entitySearchForm.clear.description"));
        clearButton.setIcon(new ThemeResource("icons/16/clear.png"));
        clearButton.addStyleName("small default");
        footerLayout.addComponent(clearButton);
        // alignment doesn't work
//        footerLayout.setComponentAlignment(clearButton, Alignment.MIDDLE_RIGHT);

        Button searchButton = new Button(uiMessageSource.getMessage("entitySearchForm.search"), this, "search");
        searchButton.setDescription(uiMessageSource.getMessage("entitySearchForm.search.description"));
        searchButton.setIcon(new ThemeResource("icons/16/search.png"));
        searchButton.addStyleName("small default");
        footerLayout.addComponent(searchButton);
//        footerLayout.setComponentAlignment(searchButton, Alignment.MIDDLE_RIGHT);
    }

    /**
     * Clear the contains of the search form and execute empty query, thus
     * returning all results.
     */
    public void clear() {
        getResults().getEntityQuery().clear();
        getResults().getResultsTable().setSortContainerPropertyId(null);
        BeanItem beanItem = createBeanItem(getResults().getEntityQuery());
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());

        getResults().search();
        requestRepaintAll();
    }

    /**
     * Execute search.
     */
    public void search() {
        getForm().commit();
        getResults().search();
    }

    @Override
    protected Component animate(Component component) {
        return super.animate(component, true);
    }
}
