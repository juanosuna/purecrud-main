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

package com.purebred.core.view.entity.tomanyrelationship;

import com.purebred.core.entity.WritableEntity;
import com.purebred.core.util.assertion.Assert;
import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.WalkableResults;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;

import javax.annotation.PostConstruct;
import java.util.Collection;

public abstract class ToManyCompositionRelationshipResults<T> extends ToManyRelationshipResults<T> implements WalkableResults {

    public abstract EntityForm<T> getEntityForm();

    private Button editButton;

    private Object currentItemId;

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        editButton = new Button(uiMessageSource.getMessage("entityResults.edit"), this, "edit");
        editButton.setDescription(uiMessageSource.getMessage("entityResults.edit.description"));
        editButton.setIcon(new ThemeResource("icons/16/edit.png"));
        editButton.setEnabled(false);
        editButton.addStyleName("small default");
        crudButtons.addComponent(editButton, 1);

        actionContextMenu.addAction("entityResults.edit", this, "edit");

        getResultsTable().addListener(new DoubleClickListener());
    }

    @Override
    public void postWire() {
        super.postWire();

        getEntityForm().setResults(this);
        getEntityForm().postWire();
//        getEntityForm().addPersistListener(this, "itemCreated");
    }

    @Override
    public void add() {
        getEntityForm().create();

        T value = getEntityForm().getEntity();
        setReferenceToParent(value);
    }

//    public void itemCreated() {
//        T entity = getEntityForm().getEntity();
////        getEntityDao().persist(entity);
//
////        setReferencesToParentAndPersist(entity);
//    }

    public void edit() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        Assert.PROGRAMMING.assertTrue(itemIds.size() == 1);
        editImpl(itemIds.iterator().next());
    }

    public void editImpl(Object itemId) {
        loadItem(itemId);
        getEntityForm().open(true);
    }

    public void loadItem(Object itemId) {
        loadItem(itemId, true);
    }

    public void loadItem(Object itemId, boolean selectFirstTab) {
        currentItemId = itemId;
        BeanItem beanItem = getResultsTable().getContainerDataSource().getItem(itemId);
        getEntityForm().load((WritableEntity) beanItem.getBean(), selectFirstTab);
    }

    public void editOrViewPreviousItem() {
        Object previousItemId = getResultsTable().getContainerDataSource().prevItemId(currentItemId);
        if (previousItemId == null && getEntityQuery().hasPreviousPage()) {
            getResultsTable().previousPage();
            previousItemId = getResultsTable().getContainerDataSource().lastItemId();
        }
        if (previousItemId != null) {
            loadItem(previousItemId, false);
        }
    }

    public boolean hasPreviousItem() {
        Object previousItemId = getResultsTable().getContainerDataSource().prevItemId(currentItemId);
        return previousItemId != null || getEntityQuery().hasPreviousPage();
    }

    public void editOrViewNextItem() {
        Object nextItemId = getResultsTable().getContainerDataSource().nextItemId(currentItemId);
        if (nextItemId == null && getEntityQuery().hasNextPage()) {
            getResultsTable().nextPage();
            nextItemId = getResultsTable().getContainerDataSource().firstItemId();
        }

        if (nextItemId != null) {
            loadItem(nextItemId, false);
        }
    }

    public boolean hasNextItem() {
        Object nextItemId = getResultsTable().getContainerDataSource().nextItemId(currentItemId);
        return nextItemId != null || getEntityQuery().hasNextPage();
    }

    @Override
    public void valuesRemoved(T... values) {
        super.valuesRemoved(values);

        for (T value : values) {
            getEntityDao().remove(value);
        }

        editButton.setEnabled(false);
    }

    @Override
    public void selectionChanged() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        if (itemIds.size() == 1) {
            actionContextMenu.setActionEnabled("entityResults.edit", true);
            actionContextMenu.setActionEnabled("entityResults.remove", true);
            getResultsTable().removeActionHandler(actionContextMenu);
            getResultsTable().addActionHandler(actionContextMenu);
            editButton.setEnabled(true);
            removeButton.setEnabled(true);
        } else if (itemIds.size() > 1) {
            actionContextMenu.setActionEnabled("entityResults.edit", false);
            actionContextMenu.setActionEnabled("entityResults.remove", true);
            getResultsTable().removeActionHandler(actionContextMenu);
            getResultsTable().addActionHandler(actionContextMenu);
            editButton.setEnabled(false);
            removeButton.setEnabled(true);
        } else {
            getResultsTable().removeActionHandler(actionContextMenu);
            editButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
    }

    public class DoubleClickListener implements ItemClickEvent.ItemClickListener {
        public void itemClick(ItemClickEvent event) {
            if (event.isDoubleClick()) {
                editImpl(event.getItemId());
            }
        }
    }
}
