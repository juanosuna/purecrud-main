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

package com.purebred.core.view.tomanyrelationship;

import com.purebred.core.MainApplication;
import com.purebred.core.dao.ToManyRelationshipQuery;
import com.purebred.core.security.SecurityService;
import com.purebred.core.util.BeanPropertyType;
import com.purebred.core.util.assertion.Assert;
import com.purebred.core.view.util.MessageSource;
import com.purebred.core.view.Results;
import com.purebred.core.view.menu.ActionContextMenu;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import org.apache.commons.beanutils.PropertyUtils;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public abstract class ToManyRelationshipResults<T> extends Results<T> {

    @Resource(name = "uiMessageSource")
    protected MessageSource uiMessageSource;

    @Resource
    protected ActionContextMenu actionContextMenu;

    @Resource
    private SecurityService securityService;

    protected HorizontalLayout crudButtons;

    private Button addButton;
    protected Button removeButton;

    protected ToManyRelationshipResults() {
        super();
    }

    public abstract String getEntityCaption();

    public abstract String getChildPropertyId();

    public abstract String getParentPropertyId();

    @Override
    public abstract ToManyRelationshipQuery getEntityQuery();

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        crudButtons = new HorizontalLayout();
        crudButtons.setMargin(false);
        crudButtons.setSpacing(true);

        addButton = new Button(uiMessageSource.getMessage("entityResults.add"), this, "add");
        addButton.setDescription(uiMessageSource.getMessage("entityResults.add.description"));
        addButton.setIcon(new ThemeResource("icons/16/add.png"));
        addButton.addStyleName("small default");
        crudButtons.addComponent(addButton);

        removeButton = new Button(uiMessageSource.getMessage("entityResults.remove"), this, "remove");
        removeButton.setDescription(uiMessageSource.getMessage("entityResults.remove.description"));
        removeButton.setIcon(new ThemeResource("icons/16/delete.png"));
        removeButton.setEnabled(false);
        removeButton.addStyleName("small default");
        crudButtons.addComponent(removeButton);

        getCrudButtons().addComponent(crudButtons, 0);
        getCrudButtons().setComponentAlignment(crudButtons, Alignment.MIDDLE_LEFT);

        getResultsTable().setMultiSelect(true);

        actionContextMenu.addAction("entityResults.remove", this, "remove");
        actionContextMenu.setActionEnabled("entityResults.remove", true);
        addSelectionChangedListener(this, "selectionChanged");
    }

    public abstract void add();

    public void setReferencesToParentAndPersist(T... values) {
        for (T value : values) {
            T referenceValue = getEntityDao().getReference(value);
            setReferenceToParent(referenceValue);
            getEntityDao().persist(referenceValue);
        }
        searchImpl(false);
    }

    public void setReferenceToParent(T value) {
        try {
            BeanPropertyType beanPropertyType = BeanPropertyType.getBeanPropertyType(getEntityType(), getParentPropertyId());
            Assert.PROGRAMMING.assertTrue(!beanPropertyType.isCollectionType(),
                    "Parent property id (" + getEntityType() + "." + getParentPropertyId() + ") must not be a collection type");
            PropertyUtils.setProperty(value, getParentPropertyId(), getEntityQuery().getParent());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void setReadOnly(boolean isReadOnly) {
        addButton.setVisible(!isReadOnly);
        removeButton.setVisible(!isReadOnly);
    }

    public void applySecurityIsEditable() {
        boolean isEditable = securityService.getCurrentUser().isEditAllowed(getEntityType().getName(), getChildPropertyId());
        addButton.setVisible(isEditable);
        removeButton.setVisible(isEditable);

        if (isEditable) {
            getResultsTable().addActionHandler(actionContextMenu);
        } else {
            getResultsTable().removeActionHandler(actionContextMenu);
        }
    }

    public void valuesRemoved(T... values) {
        for (T value : values) {
            value = getEntityDao().getReference(value);
            try {
                PropertyUtils.setProperty(value, getParentPropertyId(), null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            getEntityDao().persist(value);
        }
        searchImpl(false);
        removeButton.setEnabled(false);
    }

    public void removeImpl() {
        Collection<T> selectedValues = getSelectedValues();
        valuesRemoved((T[]) selectedValues.toArray());
    }

    public void remove() {
        ConfirmDialog.show(MainApplication.getInstance().getMainWindow(),
                uiMessageSource.getMessage("entityResults.confirmationCaption"),
                uiMessageSource.getMessage("entityResults.confirmationPrompt"),
                uiMessageSource.getMessage("entityResults.confirmationYes"),
                uiMessageSource.getMessage("entityResults.confirmationNo"),
                new ConfirmDialog.Listener() {
                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            removeImpl();
                        }
                    }
                });
    }

    public void selectionChanged() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        if (itemIds.size() > 0) {
            getResultsTable().addActionHandler(actionContextMenu);
            removeButton.setEnabled(true);
        } else {
            getResultsTable().removeActionHandler(actionContextMenu);
            removeButton.setEnabled(false);
        }
    }
}
