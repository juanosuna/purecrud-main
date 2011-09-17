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

import com.purebred.core.MainApplication;
import com.purebred.core.dao.EntityDao;
import com.purebred.core.entity.WritableEntity;
import com.purebred.core.entity.security.AbstractUser;
import com.purebred.core.security.SecurityService;
import com.purebred.core.util.MethodDelegate;
import com.purebred.core.util.SpringApplicationContext;
import com.purebred.core.validation.AssertTrueForProperties;
import com.purebred.core.validation.Validation;
import com.purebred.core.view.field.FormField;
import com.purebred.core.view.field.FormFields;
import com.purebred.core.view.field.SelectField;
import com.purebred.core.view.tomanyrelationship.ToManyRelationship;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class EntityForm<T> extends FormComponent<T> {

    @Resource
    private Validation validation;

    @Resource
    private SecurityService securityService;

    private boolean isViewMode;

    private FormFields formFields;

    private Window formWindow;
    private TabSheet toManyRelationshipTabs;

    private Button nextButton;
    private Button previousButton;

    private Button saveButton;
    private Button refreshButton;
    private boolean isValidationEnabled = true;

    private List<MethodDelegate> persistListeners = new ArrayList<MethodDelegate>();
    private List<MethodDelegate> closeListeners = new ArrayList<MethodDelegate>();

    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setSizeUndefined();
        if (!getViewableToManyRelationships().isEmpty()) {
            popupWindow.setHeight("95%");
        }
    }

    public List<ToManyRelationship> getToManyRelationships() {
        return new ArrayList<ToManyRelationship>();
    }

    public List<ToManyRelationship> getViewableToManyRelationships() {
        List<ToManyRelationship> viewableToManyRelationships = new ArrayList<ToManyRelationship>();
        List<ToManyRelationship> toManyRelationships = getToManyRelationships();

        for (ToManyRelationship toManyRelationship : toManyRelationships) {
            AbstractUser user = securityService.getCurrentUser();

            if (user.isViewAllowed(toManyRelationship.getResults().getEntityType().getName())
                    && !toManyRelationship.getResults().getDisplayFields().getViewablePropertyIds().isEmpty()) {
                viewableToManyRelationships.add(toManyRelationship);
            }
        }

        return viewableToManyRelationships;
    }

    public FormFields getFormFields() {
        return formFields;
    }

    @Resource
    public void setFormFields(FormFields formFields) {
        this.formFields = formFields;
        formFields.setForm(this);
    }

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        addStyleName("p-entity-form");

        List<ToManyRelationship> toManyRelationships = getViewableToManyRelationships();
        if (toManyRelationships.size() > 0) {
            toManyRelationshipTabs = new TabSheet();
            toManyRelationshipTabs.setSizeUndefined();
            for (ToManyRelationship toManyRelationship : toManyRelationships) {
                toManyRelationshipTabs.addTab(toManyRelationship);
                labelDepot.putFieldLabel(getEntityType().getName(), toManyRelationship.getResults().getChildPropertyId(),
                        "Relationship", toManyRelationship.getResults().getEntityCaption());
            }

            Layout layout = new HorizontalLayout();
            layout.setSizeUndefined();
            Label label = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML);
            layout.addComponent(label);
            layout.addComponent(toManyRelationshipTabs);
            addComponent(layout);
        }
    }

    @Override
    public void postWire() {
        super.postWire();

        List<ToManyRelationship> toManyRelationships = getViewableToManyRelationships();
        for (ToManyRelationship toManyRelationship : toManyRelationships) {
            toManyRelationship.postWire();
        }

        Collection<FormField> formFields = getFormFields().getFormFields();
        for (FormField formField : formFields) {
            Field field = formField.getField();
            if (field instanceof SelectField) {
                ((SelectField) field).getEntitySelect().postWire();
            }
        }
    }

    public boolean isViewMode() {
        return isViewMode;
    }

    public void setViewMode(boolean viewMode) {
        isViewMode = viewMode;
    }

    @Override
    protected Component animate(Component component) {
        if (getViewableToManyRelationships().size() > 0) {
            return super.animate(component);
        } else {
            return component;
        }
    }

    @Override
    protected void createFooterButtons(HorizontalLayout footerLayout) {
        footerLayout.setSpacing(true);
        footerLayout.setMargin(true);

        Button cancelButton = new Button(uiMessageSource.getMessage("entityForm.cancel"), this, "cancel");
        cancelButton.setDescription(uiMessageSource.getMessage("entityForm.cancel.description"));
        cancelButton.setIcon(new ThemeResource("icons/16/cancel.png"));
        cancelButton.addStyleName("small default");
        footerLayout.addComponent(cancelButton);

        refreshButton = new Button(uiMessageSource.getMessage("entityForm.refresh"), this, "refresh");
        refreshButton.setDescription(uiMessageSource.getMessage("entityForm.refresh.description"));
        refreshButton.setIcon(new ThemeResource("icons/16/refresh.png"));
        refreshButton.addStyleName("small default");
        footerLayout.addComponent(refreshButton);

        saveButton = new Button(uiMessageSource.getMessage("entityForm.save"), this, "save");
        saveButton.setDescription(uiMessageSource.getMessage("entityForm.save.description"));
        saveButton.setIcon(new ThemeResource("icons/16/save.png"));
        saveButton.addStyleName("small default");
        footerLayout.addComponent(saveButton);
    }

    public void setReadOnly(boolean isReadOnly) {
        getFormFields().setReadOnly(isReadOnly);

        saveButton.setVisible(!isReadOnly);
        refreshButton.setVisible(!isReadOnly);

        List<ToManyRelationship> toManyRelationships = getToManyRelationships();
        for (ToManyRelationship toManyRelationship : toManyRelationships) {
            toManyRelationship.getResults().setReadOnly(isReadOnly);
        }
    }

    public void restoreIsReadOnly() {
        getFormFields().restoreIsReadOnly();

        saveButton.setVisible(true);
        refreshButton.setVisible(true);

        List<ToManyRelationship> toManyRelationships = getToManyRelationships();
        for (ToManyRelationship toManyRelationship : toManyRelationships) {
            toManyRelationship.getResults().setReadOnly(false);
        }
    }

    public void applySecurityIsEditable() {
        saveButton.setVisible(true);
        refreshButton.setVisible(true);
        getFormFields().applySecurityIsEditable();

        List<ToManyRelationship> toManyRelationships = getToManyRelationships();
        for (ToManyRelationship toManyRelationship : toManyRelationships) {
            toManyRelationship.getResults().applySecurityIsEditable();
        }
    }

    public void load(WritableEntity entity) {
        load(entity, true);
    }

    public boolean isValidationEnabled() {
        return isValidationEnabled;
    }

    private void setItemDataSource(Item newDataSource, Collection<?> propertyIds) {
        isValidationEnabled = false;
        getForm().setItemDataSource(newDataSource, propertyIds);
        isValidationEnabled = true;
    }

    public void load(WritableEntity entity, boolean selectFirstTab) {
        WritableEntity loadedEntity = (WritableEntity) getEntityDao().find(entity.getId());
        BeanItem beanItem = createBeanItem(loadedEntity);
        setItemDataSource(beanItem, getFormFields().getPropertyIds());
        getFormFields().autoAdjustWidths();

        validate(true);

        loadToManyRelationships();
        resetTabs(selectFirstTab);
    }

    public void selectFirstToManyTab() {
        if (toManyRelationshipTabs != null) {
            toManyRelationshipTabs.setSelectedTab(toManyRelationshipTabs.getTab(0).getComponent());
        }
    }

    private EntityDao getEntityDao() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class, getEntityType());
    }

    public void loadToManyRelationships() {
        List<ToManyRelationship> toManyRelationships = getViewableToManyRelationships();
        if (toManyRelationships.size() > 0) {
            for (ToManyRelationship toManyRelationship : toManyRelationships) {
                Object parent = getEntity();
                toManyRelationship.getResults().getEntityQuery().clear();
                toManyRelationship.getResults().getEntityQuery().setParent(parent);
                toManyRelationship.getResults().search();

            }
            toManyRelationshipTabs.setVisible(true);
            setFormAnimatorVisible(true);
        }
    }

    public void clear() {
        clearAllErrors(true);
        setItemDataSource(null, getFormFields().getPropertyIds());
    }

    public void create() {
        createImpl();
        open(false);

        if (toManyRelationshipTabs != null) {
            toManyRelationshipTabs.setVisible(false);
            setFormAnimatorVisible(false);
        }
    }

    private void createImpl() {
        Object newEntity = createEntity();
        BeanItem beanItem = createBeanItem(newEntity);
        setItemDataSource(beanItem, getFormFields().getPropertyIds());

        validate(true);

        resetTabs();
    }

    protected void resetTabs() {
        resetTabs(true);
    }

    protected void resetTabs(boolean selectFirstTab) {

        if (selectFirstTab) {
            selectFirstToManyTab();
        }

        if (!hasTabs()) return;

        Set<String> viewableTabNames = getFormFields().getViewableTabNames();
        Set<String> tabNames = getFormFields().getTabNames();
        for (String tabName : tabNames) {
            TabSheet.Tab tab = getTabByName(tabName);

            Set<FormField> fields = getFormFields().getFormFields(tabName);

            if (getFormFields().isTabOptional(tabName)) {
                boolean isTabEmpty = true;
                for (FormField field : fields) {
                    if (field.getField().getValue() != null) {
                        isTabEmpty = false;
                        break;
                    }
                }

                setIsRequiredEnable(tabName, !isTabEmpty);
                tab.setClosable(!isViewMode());
                tab.setVisible(!isTabEmpty && viewableTabNames.contains(tabName));
            } else {
                tab.setVisible(viewableTabNames.contains(tabName));
            }
        }

        resetContextMenu();

        if (selectFirstTab || !getTabByName(getCurrentTabName()).isVisible()) {
            selectFirstTab();
        }

        syncTabAndSaveButtonErrors();
    }

    @Override
    protected void resetContextMenu() {
        Set<String> tabNames = getFormFields().getViewableTabNames();
        for (String tabName : tabNames) {
            TabSheet.Tab tab = getTabByName(tabName);

            String caption = uiMessageSource.getMessage("formComponent.add") + " " + tabName;
            if (menu.containsItem(caption)) {
                menu.getContextMenuItem(caption).setVisible(!tab.isVisible() && !isViewMode());
            }
            caption = uiMessageSource.getMessage("formComponent.remove") + " " + tabName;
            if (menu.containsItem(caption)) {
                menu.getContextMenuItem(caption).setVisible(tab.isVisible() && !isViewMode());
            }
        }
    }

    private T createEntity() {
        try {
            return (T) getEntityType().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void open(boolean createNavigationButtons) {
        formWindow = new Window(getEntityCaption());
        formWindow.addStyleName("p-entity-form-window");
        formWindow.addStyleName("opaque");
        VerticalLayout layout = (VerticalLayout) formWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        formWindow.setSizeUndefined();
        formWindow.setModal(true);
        formWindow.setClosable(true);
        formWindow.setScrollable(true);

        formWindow.addComponent(createNavigationFormLayout(createNavigationButtons));

        configurePopupWindow(formWindow);
        MainApplication.getInstance().getMainWindow().addWindow(formWindow);
    }

    private HorizontalLayout createNavigationFormLayout(boolean createNavigationButtons) {
        HorizontalLayout navigationFormLayout = new HorizontalLayout();
        navigationFormLayout.setSizeUndefined();

        if (createNavigationButtons) {

            VerticalLayout previousButtonLayout = new VerticalLayout();
            previousButtonLayout.setSizeUndefined();
            previousButtonLayout.setMargin(false);
            previousButtonLayout.setSpacing(false);
            Label spaceLabel = new Label("</br></br></br>", Label.CONTENT_XHTML);
            spaceLabel.setSizeUndefined();
            previousButtonLayout.addComponent(spaceLabel);

            previousButton = new Button(null, this, "previousItem");
            previousButton.setDescription(uiMessageSource.getMessage("entityForm.previous.description"));
            previousButton.setSizeUndefined();
            previousButton.addStyleName("borderless");
            previousButton.setIcon(new ThemeResource("icons/16/previous.png"));

            if (getViewableToManyRelationships().size() == 0) {
                HorizontalLayout previousButtonHorizontalLayout = new HorizontalLayout();
                previousButtonHorizontalLayout.setSizeUndefined();
                Label horizontalSpaceLabel = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML);
                horizontalSpaceLabel.setSizeUndefined();
                previousButtonHorizontalLayout.addComponent(previousButton);
                previousButtonHorizontalLayout.addComponent(horizontalSpaceLabel);
                previousButtonLayout.addComponent(previousButtonHorizontalLayout);
            } else {
                previousButtonLayout.addComponent(previousButton);
            }

            navigationFormLayout.addComponent(previousButtonLayout);
            navigationFormLayout.setComponentAlignment(previousButtonLayout, Alignment.TOP_LEFT);
        }

        navigationFormLayout.addComponent(this);

        if (createNavigationButtons) {
            VerticalLayout nextButtonLayout = new VerticalLayout();
            nextButtonLayout.setSizeUndefined();
            nextButtonLayout.setMargin(false);
            nextButtonLayout.setSpacing(false);
            Label spaceLabel = new Label("</br></br></br>", Label.CONTENT_XHTML);
            spaceLabel.setSizeUndefined();
            nextButtonLayout.addComponent(spaceLabel);


            nextButton = new Button(null, this, "nextItem");
            nextButton.setDescription(uiMessageSource.getMessage("entityForm.next.description"));
            nextButton.setSizeUndefined();
            nextButton.addStyleName("borderless");
            nextButton.setIcon(new ThemeResource("icons/16/next.png"));

            HorizontalLayout nextButtonHorizontalLayout = new HorizontalLayout();
            nextButtonHorizontalLayout.setSizeUndefined();
            Label horizontalSpaceLabel = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML);
            horizontalSpaceLabel.setSizeUndefined();
            nextButtonHorizontalLayout.addComponent(horizontalSpaceLabel);
            nextButtonHorizontalLayout.addComponent(nextButton);

            nextButtonLayout.addComponent(nextButtonHorizontalLayout);
            navigationFormLayout.addComponent(nextButtonLayout);
            navigationFormLayout.setComponentAlignment(nextButtonLayout, Alignment.TOP_RIGHT);

            navigationFormLayout.setSpacing(false);
            navigationFormLayout.setMargin(false);

            refreshNavigationButtonStates();
        }

        return navigationFormLayout;
    }

    void refreshNavigationButtonStates() {
        if (getEntityDao().isPersistent(getEntity())) {
            previousButton.setEnabled(((WalkableResults) getResults()).hasPreviousItem());
            nextButton.setEnabled(((WalkableResults) getResults()).hasNextItem());
        } else {
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }

    public void refreshCaption() {
        formWindow.setCaption(getEntityCaption());
    }

    public void previousItem() {
        ((WalkableResults) getResults()).editOrViewPreviousItem();
        refreshNavigationButtonStates();
        refreshCaption();
    }

    public void nextItem() {
        ((WalkableResults) getResults()).editOrViewNextItem();
        refreshNavigationButtonStates();
        refreshCaption();
    }

    public void close() {
        if (getResults() != null) {
            getResults().search();
        }

        MainApplication.getInstance().getMainWindow().removeWindow(formWindow);
        formWindow = null;

        for (MethodDelegate closeListener : closeListeners) {
            closeListener.execute();
        }
    }

    public void addCloseListener(Object target, String methodName) {
        closeListeners.add(new MethodDelegate(target, methodName));
    }

    public void addPersistListener(Object target, String methodName) {
        persistListeners.add(new MethodDelegate(target, methodName));
    }

    public void cancel() {
        clearAllErrors(true);
        getForm().discard();
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        if (beanItem == null) {
            clear();
        } else {
            WritableEntity entity = (WritableEntity) beanItem.getBean();
            if (entity.getId() == null) {
                clear();
            } else {
                load(entity);
            }
        }

        close();
    }

    public void save() {
        boolean isValid = validate(false);
        if (getForm().isValid() && isValid) {
            getForm().commit();

            WritableEntity entity = (WritableEntity) getEntity();
            if (entity.getId() != null) {
                entity.updateLastModified();
                WritableEntity mergedEntity = (WritableEntity) getEntityDao().merge(entity);
                load(mergedEntity);
            } else {
                getEntityDao().persist(entity);
                load(entity);
                for (MethodDelegate persistListener : persistListeners) {
                    persistListener.execute();
                }
            }

            close();
        }
    }

    public boolean validate(boolean clearConversionErrors) {
        WritableEntity entity = (WritableEntity) getEntity();

        clearAllErrors(clearConversionErrors);

        Set<ConstraintViolation<WritableEntity>> constraintViolations = validation.validate(entity);
        for (ConstraintViolation constraintViolation : constraintViolations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();

            ConstraintDescriptor descriptor = constraintViolation.getConstraintDescriptor();
            Annotation annotation = descriptor.getAnnotation();

            if (propertyPath.isEmpty()) {
                Validator.InvalidValueException error = new Validator.InvalidValueException(constraintViolation.getMessage());
                getForm().setComponentError(error);
            } else {
                FormField field;
                if (annotation instanceof AssertTrueForProperties) {
                    if (propertyPath.lastIndexOf(".") > 0) {
                        propertyPath = propertyPath.substring(0, propertyPath.lastIndexOf(".") + 1);
                    } else {
                        propertyPath = "";
                    }
                    AssertTrueForProperties assertTrueForProperties = (AssertTrueForProperties) annotation;
                    propertyPath += assertTrueForProperties.errorProperty();
                }
                field = getFormFields().getFormField(propertyPath);
                if (!field.hasIsRequiredError()) {
                    Validator.InvalidValueException error = new Validator.InvalidValueException(constraintViolation.getMessage());
                    field.addError(error);
                }
            }
        }

        syncTabAndSaveButtonErrors();

        return constraintViolations.isEmpty();
    }

    public void clearAllErrors(boolean clearConversionErrors) {
        getFormFields().clearErrors(clearConversionErrors);
        getForm().setComponentError(null);
        saveButton.setComponentError(null);

        Set<String> tabNames = getFormFields().getViewableTabNames();
        for (String tabName : tabNames) {
            setTabError(tabName, null);
        }
    }

    public void setTabError(String tabName, ErrorMessage error) {
        TabSheet.Tab tab = getTabByName(tabName);
        if (tab != null) {
            tab.setComponentError(error);
        }
    }

    public void syncTabAndSaveButtonErrors() {
        Set<String> tabNames = getFormFields().getViewableTabNames();
        boolean formHasErrors = false;
        for (String tabName : tabNames) {
            if (getFormFields().hasError(tabName)) {
                setTabError(tabName, new UserError("Tab contains invalid values"));
                formHasErrors = true;
            } else {
                setTabError(tabName, null);
            }
        }

        if (getForm().getComponentError() != null) {
            formHasErrors = true;
        }

        if (formHasErrors) {
            saveButton.setComponentError(new UserError("Form contains invalid values"));
        } else {
            saveButton.setComponentError(null);
        }
    }

    public boolean hasError() {
        Set<String> tabNames = getFormFields().getViewableTabNames();
        for (String tabName : tabNames) {
            if (getFormFields().hasError(tabName)) {
                return true;
            }
        }

        return false;
    }

    public void refresh() {
        clearAllErrors(true);
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        if (beanItem == null) {
            createImpl();
        } else {
            WritableEntity entity = (WritableEntity) beanItem.getBean();
            if (entity.getId() == null) {
                createImpl();
            } else {
                getForm().discard();
                load(entity, false);
            }
        }
    }
}
