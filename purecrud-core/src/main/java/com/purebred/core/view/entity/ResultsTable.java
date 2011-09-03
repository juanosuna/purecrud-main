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

import com.purebred.core.dao.EntityQuery;
import com.purebred.core.entity.WritableEntity;
import com.purebred.core.view.entity.field.DisplayField;
import com.purebred.core.view.entity.field.format.EmptyPropertyFormatter;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.EnhancedBeanItemContainer;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;

public class ResultsTable extends Table {

    private ResultsComponent results;

    protected ResultsTable(ResultsComponent results) {
        this.results = results;
        addStyleName("strong striped");
        initialize();
    }

    public void initialize() {
        setSizeUndefined();
        setEditable(true);
        setTableFieldFactory(new TableButtonLinkFactory());

        EnhancedBeanItemContainer dataSource = new EnhancedBeanItemContainer(results.getEntityType(),
                results.getDisplayFields());
        dataSource.setNonSortablePropertyIds(results.getDisplayFields().getNonSortablePropertyIds());
        String[] propertyIds = results.getDisplayFields().getViewablePropertyIdsAsArray();
        for (String propertyId : propertyIds) {
            dataSource.addNestedContainerProperty(propertyId);
        }
        setContainerDataSource(dataSource);

        setSelectable(true);
        setImmediate(true);
        setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        setCacheRate(1);

        setVisibleColumns(results.getDisplayFields().getViewablePropertyIdsAsArray());
        setColumnHeaders(results.getDisplayFields().getViewableLabelsAsArray());
    }

    @Override
    public BeanItemContainer getContainerDataSource() {
        return (BeanItemContainer) super.getContainerDataSource();
    }

    @Override
    public void sort(Object[] propertyId, boolean[] ascending) throws UnsupportedOperationException {
        if (propertyId.length > 1) {
            throw new RuntimeException("Cannot sort on more than one column");
        } else if (propertyId.length == 1) {
            if (results.getDisplayFields().getField(propertyId[0].toString()).isSortable()) {
                results.getEntityQuery().setOrderByPropertyId(propertyId[0].toString());
                if (ascending[0]) {
                    results.getEntityQuery().setOrderDirection(EntityQuery.OrderDirection.ASC);
                } else {
                    results.getEntityQuery().setOrderDirection(EntityQuery.OrderDirection.DESC);
                }
                firstPage();
            } else {
                throw new UnsupportedOperationException("No sorting on this column");
            }
        }
    }

    public int getFirstResult() {
        EntityQuery query = results.getEntityQuery();
        return query.getResultCount() == 0 ? 0 : query.getFirstResult() + 1;
    }

    public void setFirstResult(int firstResult) {
        clearSelection();
        results.getEntityQuery().setFirstResult(firstResult - 1);
        executeCurrentQuery();
    }

    public void refresh() {
        clearSelection();
        executeCurrentQuery();
    }

    public void firstPage() {
        clearSelection();
        results.getEntityQuery().firstPage();
        executeCurrentQuery();
    }

    public void previousPage() {
        clearSelection();
        results.getEntityQuery().previousPage();
        executeCurrentQuery();
    }

    public void nextPage() {
        clearSelection();
        results.getEntityQuery().nextPage();
        executeCurrentQuery();
    }

    public void lastPage() {
        clearSelection();
        results.getEntityQuery().lastPage();
        executeCurrentQuery();
    }

    public void executeCurrentQuery() {
        List entities = results.getEntityQuery().execute();
        getContainerDataSource().removeAllItems();
        getContainerDataSource().addAll(entities);

        results.refreshResultCountLabel();
        results.refreshNavigationButtonStates();
        setPageLength(Math.min(entities.size(), results.getPageSize()));
    }

    public void clearSelection() {
        if (isMultiSelect()) {
            setValue(new HashSet());
        } else {
            setValue(null);
        }
    }

    @Override
    protected String formatPropertyValue(Object rowId, Object colId, Property property) {

        if (property.getValue() != null) {
            DisplayField displayField = results.getDisplayFields().getField(colId.toString());
            PropertyFormatter propertyFormatter = displayField.getPropertyFormatter();

            if (EmptyPropertyFormatter.class.equals(propertyFormatter.getClass())) {
                return super.formatPropertyValue(rowId, colId, property);
            } else {
                return propertyFormatter.format(property.getValue());
            }
        } else {
            return super.formatPropertyValue(rowId, colId, property);
        }
    }

    public class TableButtonLinkFactory implements TableFieldFactory {
        public Field createField(Container container, Object itemId,
                                 Object propertyId, Component uiContext) {

            DisplayField.FormLink formLink = results.getDisplayFields().getField(propertyId.toString()).getFormLink();

            if (formLink != null) {
                BeanItem item = getContainerDataSource().getItem(itemId);
                Button button = new ButtonLink(item.getItemProperty(propertyId));
                button.addListener(new ButtonLinkClickListener(formLink, item));
                return button;
            }

            return null;
        }
    }

    public class ButtonLinkClickListener implements Button.ClickListener {
        private DisplayField.FormLink formLink;
        private BeanItem item;

        public ButtonLinkClickListener(DisplayField.FormLink formLink, BeanItem item) {
            this.formLink = formLink;
            this.item = item;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            Object parentBean = item.getBean();
            try {
                WritableEntity propertyBean = (WritableEntity) PropertyUtils.getProperty(parentBean,
                        formLink.getPropertyId());
                EntityForm entityForm = formLink.getEntityForm();
                entityForm.addCloseListener(results, "search");
                entityForm.load(propertyBean);
                entityForm.open(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ButtonLink extends Button {
        private Property itemProperty;

        public ButtonLink(Property itemProperty) {
            this.itemProperty = itemProperty;
            setStyleName(BaseTheme.BUTTON_LINK);
        }

        @Override
        public String getCaption() {
            if (itemProperty.getValue() == null) {
                return null;
            } else {
                return itemProperty.getValue().toString();
            }
        }

        @Override
        protected void setInternalValue(Object newValue) {
            super.setInternalValue(false);
        }

        @Override
        protected void setValue(Object newValue, boolean repaintIsNotNeeded) throws ReadOnlyException, ConversionException {
            super.setValue(false, repaintIsNotNeeded);
        }

        @Override
        public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
            super.setValue(false);
        }

        @Override
        public void setPropertyDataSource(Property newDataSource) {
            itemProperty = newDataSource;
        }
    }
}
