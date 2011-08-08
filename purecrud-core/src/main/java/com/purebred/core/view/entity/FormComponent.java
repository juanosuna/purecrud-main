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

package com.purebred.core.view.entity;

import com.purebred.core.util.ReflectionUtil;
import com.purebred.core.view.MessageSource;
import com.purebred.core.view.entity.field.FormField;
import com.purebred.core.view.entity.field.FormFields;
import com.purebred.core.view.entity.field.format.DefaultFormats;
import com.purebred.core.view.entity.util.LayoutContextMenu;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NullCapableBeanItem;
import com.vaadin.data.util.NullCapableNestedPropertyDescriptor;
import com.vaadin.data.util.VaadinPropertyDescriptor;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import org.vaadin.jouni.animator.Animator;
import org.vaadin.peter.contextmenu.ContextMenu;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FormComponent<T> extends CustomComponent {

    @Resource
    protected MessageSource uiMessageSource;

    @Resource
    protected MessageSource entityMessageSource;

    @Resource
    private DefaultFormats defaultFormat;

    private ConfigurableForm form;
    private ResultsComponent results;
    private FormFields formFields;
    private TabSheet tabSheet;
    private Map<String, Integer> tabPositions = new HashMap<String, Integer>();
    private LayoutContextMenu menu;

    public abstract String getEntityCaption();

    public abstract void configureFields(FormFields formFields);

    abstract void createFooterButtons(HorizontalLayout footerButtons);

    abstract FormFields createFormFields();

    public MessageSource getUiMessageSource() {
        return uiMessageSource;
    }

    public MessageSource getEntityMessageSource() {
        return entityMessageSource;
    }

    public DefaultFormats getDefaultFormat() {
        return defaultFormat;
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public Form getForm() {
        return form;
    }

    public FormFields getFormFields() {
        return formFields;
    }

    public ResultsComponent getResults() {
        return results;
    }

    public void setResults(ResultsComponent results) {
        this.results = results;
    }

    @PostConstruct
    public void postConstruct() {
        setSizeUndefined();
        form = new ConfigurableForm();
        form.setSizeUndefined();

        form.setWriteThrough(true);
        form.setInvalidCommitted(true);
        form.setImmediate(true);
        form.setValidationVisibleOnCommit(true);
        form.addStyleName("entityForm");

        formFields = createFormFields();
        configureFields(formFields);
        form.setFormFieldFactory(new EntityFieldFactory(formFields));

        final GridLayout gridLayout = formFields.createGridLayout();
        form.setLayout(gridLayout);

        form.getFooter().addStyleName("b-formComponent-footer");
        createFooterButtons((HorizontalLayout) form.getFooter());

        VerticalLayout tabsAndForm = new VerticalLayout();
        tabsAndForm.setSizeUndefined();
        if (formFields.getTabNames().size() > 1) {
            initializeTabs(tabsAndForm);
        }
        tabsAndForm.addComponent(form);

        Label spaceLabel = new Label("</br>", Label.CONTENT_XHTML);
        spaceLabel.setSizeUndefined();
        tabsAndForm.addComponent(spaceLabel);

        VerticalLayout formComponentLayout = new VerticalLayout();
        formComponentLayout.addComponent(animate(tabsAndForm));

        setCompositionRoot(formComponentLayout);
        setCustomSizeUndefined();
    }

    protected Component animate(Component component) {
        final Animator formAnimator = new Animator(component);
        formAnimator.setSizeUndefined();

        HorizontalLayout animatorLayout = new HorizontalLayout();
        animatorLayout.setMargin(false, false, false, false);
        animatorLayout.setSpacing(false);

        Button toggleFormButton = new Button(null, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                formAnimator.setRolledUp(!formAnimator.isRolledUp());
                if (formAnimator.isRolledUp()) {
                    event.getButton().setIcon(new ThemeResource("../pureCrudTheme/icons/expand-icon.png"));
                } else {
                    event.getButton().setIcon(new ThemeResource("../pureCrudTheme/icons/collapse-icon.png"));
                }
            }
        });
        toggleFormButton.setDescription(uiMessageSource.getMessage("entryPoint.toggleSearchForm.description"));
        toggleFormButton.setIcon(new ThemeResource("../pureCrudTheme/icons/collapse-icon.png"));
        toggleFormButton.addStyleName("borderless");

        animatorLayout.addComponent(toggleFormButton);
        animatorLayout.addComponent(formAnimator);

        return animatorLayout;
    }

    private void initializeTabs(VerticalLayout layout) {
        final Set<String> tabNames = formFields.getTabNames();

        tabSheet = new TabSheet();
        tabSheet.setSizeUndefined();
        menu = new LayoutContextMenu(layout);
        int tabPosition = 0;
        boolean hasOptionalTabs = false;
        for (String tabName : tabNames) {
            Label emptyLabel = new Label();
            emptyLabel.setSizeUndefined();
            TabSheet.Tab tab = tabSheet.addTab(emptyLabel, tabName, null);
            tabPositions.put(tabName, tabPosition++);
            if (formFields.isTabOptional(tabName)) {
                menu.addAction(uiMessageSource.getMessage("formComponent.add") + " " + tabName,
                        this, "executeContextAction").setVisible(true);
                menu.addAction(uiMessageSource.getMessage("formComponent.remove") + " " + tabName,
                        this, "executeContextAction").setVisible(false);
                setIsRequiredEnable(tabName, false);
                tab.setVisible(false);
                hasOptionalTabs = true;
            }
        }

        if (hasOptionalTabs) {
            for (String tabName : tabNames) {
                TabSheet.Tab tab = getTabByName(tabName);
                tab.setDescription(uiMessageSource.getMessage("formComponent.tab.description"));
            }
        }

        layout.addComponent(tabSheet);

        tabSheet.addListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                form.getLayout().removeAllComponents();
                FormGridLayout gridLayout = (FormGridLayout) form.getLayout();
                String tabName = getCurrentTabName();
                gridLayout.setFormColumns(formFields.getColumns(tabName));
                gridLayout.setRows(formFields.getRows(tabName));
                Set<FormField> formFields = getFormFields().getFormFields(tabName);
                for (FormField formField : formFields) {
                    String propertyId = formField.getPropertyId();
                    Field field = formField.getField();
                    form.attachField(propertyId, field);
                }
            }
        });
    }

    protected void resetTabs() {
        resetTabs(true);
    }

    protected void resetTabs(boolean selectFirstTab) {
        if (tabSheet == null) return;

        Set<String> tabNames = formFields.getTabNames();
        for (String tabName : tabNames) {
            if (formFields.isTabOptional(tabName)) {
                Set<FormField> fields = formFields.getFormFields(tabName);
                boolean isTabEmpty = true;
                for (FormField field : fields) {
                    if (field.getField().getValue() != null) {
                        isTabEmpty = false;
                        break;
                    }
                }
                setIsRequiredEnable(tabName, !isTabEmpty);
                TabSheet.Tab tab = getTabByName(tabName);
                tab.setVisible(!isTabEmpty);
            }
        }

        resetContextMenu();

        if (selectFirstTab || !getTabByName(getCurrentTabName()).isVisible()) {
            selectFirstTab();
        }

        if (this instanceof EntityForm) {
            ((EntityForm) this).syncTabAndSaveButtonErrors();
        }
    }

    private void resetContextMenu() {
        Set<String> tabNames = formFields.getTabNames();
        for (String tabName : tabNames) {
            TabSheet.Tab tab = getTabByName(tabName);

            String caption = uiMessageSource.getMessage("formComponent.add") + " " + tabName;
            if (menu.containsItem(caption)) {
                menu.getContextMenuItem(caption).setVisible(!tab.isVisible());
            }
            caption = uiMessageSource.getMessage("formComponent.remove") + " " + tabName;
            if (menu.containsItem(caption)) {
                menu.getContextMenuItem(caption).setVisible(tab.isVisible());
            }
        }
    }

    public void executeContextAction(ContextMenu.ContextMenuItem item) {
        String name = item.getName();

        if (name.startsWith(uiMessageSource.getMessage("formComponent.add") + " ")) {
            String tabName = name.substring(4);
            FormFields.AddRemoveMethodDelegate addRemoveMethodDelegate = formFields.getTabAddRemoveDelegate(tabName);
            addRemoveMethodDelegate.getAddMethodDelegate().execute();
            TabSheet.Tab tab = getTabByName(tabName);
            setIsRequiredEnable(tabName, true);
            tab.setVisible(true);
            tabSheet.setSelectedTab(tab.getComponent());
        } else if (name.startsWith(uiMessageSource.getMessage("formComponent.remove") + " ")) {
            String tabName = name.substring(7);
            FormFields.AddRemoveMethodDelegate addRemoveMethodDelegate = formFields.getTabAddRemoveDelegate(tabName);
            addRemoveMethodDelegate.getRemoveMethodDelegate().execute();
            TabSheet.Tab tab = getTabByName(tabName);
            setIsRequiredEnable(tabName, false);
            tab.setVisible(false);
        }

        BeanItem beanItem = createBeanItem(getEntity());
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());

        if (this instanceof EntityForm) {
            EntityForm entityForm = (EntityForm) this;
            entityForm.validate(false);
        }

        resetContextMenu();
    }

    private void setIsRequiredEnable(String tabName, boolean isEnabled) {
        Set<FormField> fields = formFields.getFormFields(tabName);
        for (FormField field : fields) {
            if (isEnabled) {
                field.restoreIsRequired();
            } else {
                field.disableIsRequired();
            }
        }
    }

    public TabSheet.Tab getTabByName(String tabName) {
        if (tabSheet == null) {
            return null;
        } else {
            Integer position = tabPositions.get(tabName);
            return tabSheet.getTab(position);
        }
    }

    public String getCurrentTabName() {
        if (tabSheet == null || tabSheet.getSelectedTab() == null) {
            return formFields.getFirstTabName();
        } else {
            return tabSheet.getTab(tabSheet.getSelectedTab()).getCaption();
        }
    }

    public void selectFirstTab() {
        if (tabSheet != null) {
            tabSheet.setSelectedTab(tabSheet.getTab(0).getComponent());
        }
    }

    @Override
    public void addComponent(Component c) {
        ((ComponentContainer) getCompositionRoot()).addComponent(c);
    }

    public void setCustomSizeUndefined() {
        setSizeUndefined();
        getCompositionRoot().setSizeUndefined();
    }

    public T getEntity() {
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        return (T) beanItem.getBean();
    }

    public void refreshFromDataSource() {
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());
    }

    protected BeanItem createBeanItem(Object entity) {
        List<String> propertyIds = getFormFields().getPropertyIds();
        Map<String, VaadinPropertyDescriptor> descriptors = new HashMap<String, VaadinPropertyDescriptor>();
        for (String propertyId : propertyIds) {
            VaadinPropertyDescriptor descriptor = new NullCapableNestedPropertyDescriptor(propertyId, getEntityType(),
                    getFormFields().getField(propertyId));
            descriptors.put(propertyId, descriptor);
        }
        return new NullCapableBeanItem(entity, descriptors);
    }

    public static class EntityFieldFactory implements FormFieldFactory {

        private FormFields formFields;

        public EntityFieldFactory(FormFields formFields) {
            this.formFields = formFields;
        }

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            FormField formField = formFields.getFormField(propertyId.toString());

            return formField.getField();
        }
    }

    public class ConfigurableForm extends Form {

        @Override
        public void commit() throws SourceException, Validator.InvalidValueException {
            super.commit();
        }

        @Override
        protected void attachField(Object propertyId, Field field) {
            FormGridLayout gridLayout = (FormGridLayout) form.getLayout();
            FormFields formFields = getFormFields();
            String currentTabName = getCurrentTabName();
            if (formFields.containsPropertyId(currentTabName, propertyId.toString())) {
                gridLayout.addField(getFormFields().getFormField(propertyId.toString()));
            }
        }

        @Override
        protected void detachField(final Field field) {
            FormGridLayout formGridLayout = (FormGridLayout) form.getLayout();
            FormField formField = getFormFields().findByField(field);
            formGridLayout.removeField(formField);
        }
    }
}
