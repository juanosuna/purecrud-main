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
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.purebred.core.view.entity.tomanyrelationship;

import com.purebred.core.dao.ToManyRelationshipQuery;
import com.purebred.core.view.MainApplication;
import com.purebred.core.view.MessageSource;
import com.purebred.core.view.entity.ResultsComponent;
import com.purebred.core.view.entity.entityselect.EntitySelect;
import com.purebred.core.view.entity.util.ActionContextMenu;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public abstract class ToManyRelationshipResults<T> extends ResultsComponent<T> {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Resource(name = "entityMessageSource")
    private MessageSource entityMessageSource;

    @Resource
    private ActionContextMenu actionContextMenu;

    private Window popupWindow;

    private Button addButton;
    private Button removeButton;

    protected ToManyRelationshipResults() {
        super();
    }

    public abstract String getEntityCaption();

    public abstract String getParentPropertyId();

    public abstract EntitySelect<T> getEntitySelect();

    @Override
    public abstract ToManyRelationshipQuery getEntityQuery();


    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        HorizontalLayout crudButtons = new HorizontalLayout();
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
        getEntitySelect().getResultsComponent().getResultsTable().setMultiSelect(true);

        actionContextMenu.addAction("entityResults.remove", this, "remove");
        actionContextMenu.setActionEnabled("entityResults.remove", true);
        addSelectionChangedListener(this, "selectionChanged");
    }

    public void add() {
        popupWindow = new Window(entityMessageSource.getMessageWithDefault(getEntityCaption()));
        popupWindow.addStyleName("opaque");
        VerticalLayout layout = (VerticalLayout) popupWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        popupWindow.setSizeUndefined();
        popupWindow.setModal(true);
        EntitySelect entitySelect = getEntitySelect();
        entitySelect.getResultsComponent().getEntityQuery().clear();
        entitySelect.getResultsComponent().search();
        popupWindow.addComponent(entitySelect);
        popupWindow.setClosable(true);
        getEntitySelect().getResultsComponent().setSelectButtonListener(this, "itemsSelected");

        entitySelect.configurePopupWindow(popupWindow);

        MainApplication.getInstance().getMainWindow().addWindow(popupWindow);
    }

    public void itemsSelected() {
        close();
        Collection<T> selectedValues = getEntitySelect().getResultsComponent().getSelectedValues();
        valuesSelected((T[]) selectedValues.toArray());
    }

    public void valuesSelected(T... values) {
        Object parent = getEntityQuery().getParent();
        for (T value : values) {
            value = getEntityDao().getReference(value);
            try {
                PropertyUtils.setProperty(value, getParentPropertyId(), parent);
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

    public void setAddButtonEnabled(boolean isEnabled) {
        addButton.setEnabled(isEnabled);
    }

    public void close() {
        MainApplication.getInstance().getMainWindow().removeWindow(popupWindow);
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
