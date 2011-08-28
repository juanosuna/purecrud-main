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

import com.purebred.core.entity.WritableEntity;
import com.purebred.core.security.SecurityService;
import com.purebred.core.util.assertion.Assert;
import com.purebred.core.view.MainApplication;
import com.purebred.core.view.MessageSource;
import com.purebred.core.view.entity.field.FormField;
import com.purebred.core.view.entity.util.ActionContextMenu;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;

public abstract class Results<T> extends ResultsComponent<T> implements WalkableResults {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Resource
    private ActionContextMenu actionContextMenu;

    @Resource
    private SecurityService securityService;

    private Button newButton;
    private Button editButton;
    private Button viewButton;
    private Button deleteButton;

    private Object currentItemId;

    protected Results() {
        super();
    }

    public abstract EntityForm<T> getEntityForm();

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        getResultsTable().setMultiSelect(true);

        HorizontalLayout crudButtons = new HorizontalLayout();
        crudButtons.setMargin(false);
        crudButtons.setSpacing(true);

        newButton = new Button(uiMessageSource.getMessage("entityResults.new"), this, "create");
        newButton.setDescription(uiMessageSource.getMessage("entityResults.new.description"));
        newButton.setIcon(new ThemeResource("icons/16/add.png"));
        newButton.addStyleName("small default");
        crudButtons.addComponent(newButton);

        viewButton = new Button(uiMessageSource.getMessage("entityResults.view"), this, "view");
        viewButton.setDescription(uiMessageSource.getMessage("entityResults.view.description"));
        viewButton.setIcon(new ThemeResource("icons/16/view.png"));
        viewButton.setEnabled(false);
        viewButton.addStyleName("small default");
        crudButtons.addComponent(viewButton);

        editButton = new Button(uiMessageSource.getMessage("entityResults.edit"), this, "edit");
        editButton.setDescription(uiMessageSource.getMessage("entityResults.edit.description"));
        editButton.setIcon(new ThemeResource("icons/16/edit.png"));
        editButton.setEnabled(false);
        editButton.addStyleName("small default");
        crudButtons.addComponent(editButton);

        deleteButton = new Button(uiMessageSource.getMessage("entityResults.delete"), this, "delete");
        deleteButton.setDescription(uiMessageSource.getMessage("entityResults.delete.description"));
        deleteButton.setIcon(new ThemeResource("icons/16/delete.png"));
        deleteButton.setEnabled(false);
        deleteButton.addStyleName("small default");
        crudButtons.addComponent(deleteButton);

        addSelectionChangedListener(this, "selectionChanged");
        actionContextMenu.addAction("entityResults.view", this, "view");
        actionContextMenu.addAction("entityResults.edit", this, "edit");
        actionContextMenu.addAction("entityResults.delete", this, "delete");

        applySecurityToCRUDButtons();
        getCrudButtons().addComponent(crudButtons, 0);
        getCrudButtons().setComponentAlignment(crudButtons, Alignment.MIDDLE_LEFT);

        getResultsTable().addListener(new DoubleClickListener());
    }

    @Override
    public void postWire() {
        super.postWire();
        getEntityForm().setResults(this);
        getEntityForm().postWire();

    }

    public void applySecurityToCRUDButtons() {
        newButton.setVisible(securityService.getCurrentUser().isCreateAllowed(getEntityType().getName()));
        editButton.setVisible(securityService.getCurrentUser().isEditAllowed(getEntityType().getName()));
        deleteButton.setVisible(securityService.getCurrentUser().isDeleteAllowed(getEntityType().getName()));
    }

    public void create() {
        getEntityForm().setViewMode(false);
        applyViewMode();
        getEntityForm().create();
    }

    public void view() {
        getEntityForm().setViewMode(true);
        editOrView();
    }

    public void edit() {
        getEntityForm().setViewMode(false);
        editOrView();
    }

    public void editOrView() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        Assert.PROGRAMMING.assertTrue(itemIds.size() == 1);
        editOrView(itemIds.iterator().next());
    }

    public void editOrView(Object itemId) {
        loadItem(itemId);
        getEntityForm().open(true);
    }

    public void loadItem(Object itemId) {
        loadItem(itemId, true);
    }

    public void loadItem(Object itemId, boolean selectFirstTab) {

        getEntityForm().restoreIsReadOnly();

        currentItemId = itemId;
        BeanItem beanItem = getResultsTable().getContainerDataSource().getItem(itemId);
        getEntityForm().load((WritableEntity) beanItem.getBean(), selectFirstTab);

        applyViewMode();
    }

    public void applyViewMode() {
        if (getEntityForm().isViewMode()) {
            getEntityForm().setReadOnly(true);
        } else {
            getEntityForm().applySecurityIsEditable();
        }
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

        // workaround to Vaadin bug, to prevent the width of selects from growing
//        Collection<FormField> formFields = getEntityForm().getFormFields().getFormFields();
//        for (FormField formField : formFields) {
//            if (formField.getField() instanceof AbstractSelect) {
//                formField.getField().requestRepaintRequests();
//            }
//        }
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

        // workaround to Vaadin bug, to prevent the width of selects from growing
//        Collection<FormField> formFields = getEntityForm().getFormFields().getFormFields();
//        for (FormField formField : formFields) {
//            if (formField.getField() instanceof AbstractSelect) {
//                formField.getField().requestRepaintRequests();
//            }
//        }
    }

    public boolean hasNextItem() {
        Object nextItemId = getResultsTable().getContainerDataSource().nextItemId(currentItemId);
        return nextItemId != null || getEntityQuery().hasNextPage();
    }

    private void deleteImpl() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        for (Object itemId : itemIds) {
            BeanItem<T> beanItem = getResultsTable().getContainerDataSource().getItem(itemId);
            T entity = beanItem.getBean();
            getEntityDao().remove(entity);
        }

        // solves tricky ConcurrentModification bug where ContextMenu handler calls delete
        // but then search removes handler
        searchImpl(false);
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        viewButton.setEnabled(false);
    }

    public void delete() {
        ConfirmDialog.show(MainApplication.getInstance().getMainWindow(),
                uiMessageSource.getMessage("entityResults.confirmationCaption"),
                uiMessageSource.getMessage("entityResults.confirmationPrompt"),
                uiMessageSource.getMessage("entityResults.confirmationYes"),
                uiMessageSource.getMessage("entityResults.confirmationNo"),
                new ConfirmDialog.Listener() {
                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            deleteImpl();
                        }
                    }
                });
    }

    public void selectionChanged() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        boolean isEditAllowed = securityService.getCurrentUser().isEditAllowed(getEntityType().getName());
        boolean isDeleteAllowed = securityService.getCurrentUser().isDeleteAllowed(getEntityType().getName());
        if (itemIds.size() == 1) {
            actionContextMenu.setActionEnabled("entityResults.view", true);
            actionContextMenu.setActionEnabled("entityResults.edit", isEditAllowed);
            actionContextMenu.setActionEnabled("entityResults.delete", isDeleteAllowed);
            getResultsTable().removeActionHandler(actionContextMenu);
            getResultsTable().addActionHandler(actionContextMenu);
            editButton.setEnabled(true);
            viewButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else if (itemIds.size() > 1) {
            actionContextMenu.setActionEnabled("entityResults.view", false);
            actionContextMenu.setActionEnabled("entityResults.edit", false);
            actionContextMenu.setActionEnabled("entityResults.delete", isDeleteAllowed);
            getResultsTable().removeActionHandler(actionContextMenu);
            getResultsTable().addActionHandler(actionContextMenu);
            editButton.setEnabled(false);
            viewButton.setEnabled(false);
            deleteButton.setEnabled(true);
        } else {
            getResultsTable().removeActionHandler(actionContextMenu);
            editButton.setEnabled(false);
            viewButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    public class DoubleClickListener implements ItemClickEvent.ItemClickListener {
        public void itemClick(ItemClickEvent event) {
            if (event.isDoubleClick()) {
                getEntityForm().setViewMode(false);
                editOrView(event.getItemId());
            }
        }
    }
}
