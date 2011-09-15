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

import com.purebred.core.dao.EntityDao;
import com.purebred.core.dao.EntityQuery;
import com.purebred.core.util.ReflectionUtil;
import com.purebred.core.view.MessageSource;
import com.purebred.core.view.entity.field.DisplayFields;
import com.purebred.core.view.entity.field.LabelDepot;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;

public abstract class ResultsComponent<T> extends CustomComponent {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Resource
    private LabelDepot labelDepot;

    private ResultsTable resultsTable;
    private DisplayFields displayFields;
    private TextField firstResultTextField;
    private Label resultCountLabel;

    private Select pageSizeMenu;
    private Button firstButton;
    private Button previousButton;
    private Button nextButton;
    private Button lastButton;

    private HorizontalLayout crudButtons;

    protected ResultsComponent() {
    }

    public abstract void configureFields(DisplayFields displayFields);

    public DisplayFields getDisplayFields() {
        return displayFields;
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public abstract EntityDao<T, ? extends Serializable> getEntityDao();

    public ResultsTable getResultsTable() {
        return resultsTable;
    }

    public abstract EntityQuery<T> getEntityQuery();

    public HorizontalLayout getCrudButtons() {
        return crudButtons;
    }

    @Resource
    public void setDisplayFields(DisplayFields displayFields) {
        this.displayFields = displayFields;
        displayFields.setEntityType(getEntityType());
    }

    @PostConstruct
    public void postConstruct() {
        addStyleName("p-results-component");

        configureFields(displayFields);
        resultsTable = new ResultsTable(this);
        configureTable(resultsTable);

        VerticalLayout verticalLayout = new VerticalLayout();
        setCompositionRoot(verticalLayout);

        crudButtons = new HorizontalLayout();
        HorizontalLayout navigationLine = createNavigationLine();
        if (resultsTable.getWidth() > 0) {
            navigationLine.setWidth(resultsTable.getWidth(), resultsTable.getWidthUnits());
        }
        addComponent(crudButtons);
        addComponent(navigationLine);

        addComponent(resultsTable);

        setCustomSizeUndefined();

        labelDepot.trackLabels(displayFields);
    }

    public void configureTable(ResultsTable resultsTable) {
    }

    @Override
    public void addComponent(Component c) {
        ((ComponentContainer) getCompositionRoot()).addComponent(c);
    }

    public void setCustomSizeUndefined() {
        setSizeUndefined();
        getCompositionRoot().setSizeUndefined();
    }

    private HorizontalLayout createNavigationLine() {

        HorizontalLayout resultCountDisplay = new HorizontalLayout();
        Label showingLabel = new Label(uiMessageSource.getMessage("entityResults.showing")
                + " &nbsp ", Label.CONTENT_XHTML);
        showingLabel.setSizeUndefined();
        showingLabel.addStyleName("small");
        resultCountDisplay.addComponent(showingLabel);
        firstResultTextField = createFirstResultTextField();
        firstResultTextField.addStyleName("small");
        firstResultTextField.setSizeUndefined();
        resultCountDisplay.addComponent(firstResultTextField);
        resultCountLabel = new Label("", Label.CONTENT_XHTML);
        resultCountLabel.setSizeUndefined();
        resultCountLabel.addStyleName("small");
        resultCountDisplay.addComponent(resultCountLabel);

        Label spaceLabel = new Label(" &nbsp; ", Label.CONTENT_XHTML);
        spaceLabel.setSizeUndefined();
        resultCountDisplay.addComponent(spaceLabel);

        Button refreshButton = new Button(null, getResultsTable(), "refresh");
        refreshButton.setDescription(uiMessageSource.getMessage("entityResults.refresh.description"));
        refreshButton.setSizeUndefined();
        refreshButton.addStyleName("borderless");
        refreshButton.setIcon(new ThemeResource("icons/16/refresh-blue.png"));
        resultCountDisplay.addComponent(refreshButton);

        HorizontalLayout navigationButtons = new HorizontalLayout();
        navigationButtons.setMargin(false, true, false, false);
        navigationButtons.setSpacing(true);

        String perPageText = uiMessageSource.getMessage("entityResults.pageSize");
        pageSizeMenu = new Select();
        pageSizeMenu.addStyleName("small");
        pageSizeMenu.addItem(5);
        pageSizeMenu.setItemCaption(5, "5 " + perPageText);
        pageSizeMenu.addItem(10);
        pageSizeMenu.setItemCaption(10, "10 " + perPageText);
        pageSizeMenu.addItem(25);
        pageSizeMenu.setItemCaption(25, "25 " + perPageText);
        pageSizeMenu.addItem(50);
        pageSizeMenu.setItemCaption(50, "50 " + perPageText);
        pageSizeMenu.addItem(100);
        pageSizeMenu.setItemCaption(100, "100 " + perPageText);
        pageSizeMenu.setFilteringMode(Select.FILTERINGMODE_OFF);
        pageSizeMenu.setNewItemsAllowed(false);
        pageSizeMenu.setNullSelectionAllowed(false);
        pageSizeMenu.setImmediate(true);
        pageSizeMenu.setWidth(8, UNITS_EM);
        navigationButtons.addComponent(pageSizeMenu);

        firstButton = new Button(null, getResultsTable(), "firstPage");
        firstButton.setDescription(uiMessageSource.getMessage("entityResults.first.description"));
        firstButton.setSizeUndefined();
        firstButton.addStyleName("borderless");
        firstButton.setIcon(new ThemeResource("icons/16/first.png"));
        navigationButtons.addComponent(firstButton);

        previousButton = new Button(null, getResultsTable(), "previousPage");
        previousButton.setDescription(uiMessageSource.getMessage("entityResults.previous.description"));
        previousButton.setSizeUndefined();
        previousButton.addStyleName("borderless");
        previousButton.setIcon(new ThemeResource("icons/16/previous.png"));
        navigationButtons.addComponent(previousButton);

        nextButton = new Button(null, getResultsTable(), "nextPage");
        nextButton.setDescription(uiMessageSource.getMessage("entityResults.next.description"));
        nextButton.setSizeUndefined();
        nextButton.addStyleName("borderless");
        nextButton.setIcon(new ThemeResource("icons/16/next.png"));
        navigationButtons.addComponent(nextButton);

        lastButton = new Button(null, getResultsTable(), "lastPage");
        lastButton.setDescription(uiMessageSource.getMessage("entityResults.last.description"));
        lastButton.setSizeUndefined();
        lastButton.addStyleName("borderless");
        lastButton.setIcon(new ThemeResource("icons/16/last.png"));
        navigationButtons.addComponent(lastButton);

        HorizontalLayout navigationLine = new HorizontalLayout();
        navigationLine.setWidth("100%");
        navigationLine.setMargin(true, true, true, false);

        navigationLine.addComponent(resultCountDisplay);
        navigationLine.setComponentAlignment(resultCountDisplay, Alignment.BOTTOM_LEFT);

        navigationLine.addComponent(navigationButtons);
        navigationLine.setComponentAlignment(navigationButtons, Alignment.BOTTOM_RIGHT);

        return navigationLine;
    }

    private TextField createFirstResultTextField() {
        TextField firstResultTextField = new TextField();
        firstResultTextField.setImmediate(true);
        firstResultTextField.setInvalidAllowed(true);
        firstResultTextField.setInvalidCommitted(false);
        firstResultTextField.setWriteThrough(true);
        firstResultTextField.addValidator(new IntegerValidator(uiMessageSource.getMessage("entityResults.firstResult.invalid")) {
            @Override
            protected boolean isValidString(String value) {
                boolean isValid = super.isValidString(value);
                if (!isValid) {
                    return false;
                } else {
                    Integer intValue = Integer.parseInt(value);
                    if (getEntityQuery().getResultCount() > 0) {
                        return intValue >= 1 && intValue <= getEntityQuery().getResultCount();
                    } else {
                        return intValue == 0;
                    }
                }
            }
        });
        firstResultTextField.setPropertyDataSource(new MethodProperty(getResultsTable(), "firstResult"));
        firstResultTextField.setWidth(3, Sizeable.UNITS_EM);

        return firstResultTextField;
    }

    void refreshNavigationButtonStates() {
        firstButton.setEnabled(getEntityQuery().hasPreviousPage());
        previousButton.setEnabled(getEntityQuery().hasPreviousPage());
        lastButton.setEnabled(getEntityQuery().hasNextPage());
        nextButton.setEnabled(getEntityQuery().hasNextPage());

        pageSizeMenu.setEnabled(getEntityQuery().getResultCount() > 10);
        firstResultTextField.setEnabled(getEntityQuery().getResultCount() > 10);
    }

    public void postWire() {
        MethodProperty pageProperty = new MethodProperty(this, "pageSize");
        pageSizeMenu.setPropertyDataSource(pageProperty);
        pageSizeMenu.addListener(Property.ValueChangeEvent.class, this, "search");
        getEntityQuery().postWire();
    }

    public void selectPageSize(Integer size) {
        pageSizeMenu.select(size);
    }

    public int getPageSize() {
        return getEntityQuery().getPageSize();
    }

    public void setPageSize(int pageSize) {
        getEntityQuery().setPageSize(pageSize);
    }

    public void addSelectionChangedListener(Object target, String methodName) {
        resultsTable.addListener(Property.ValueChangeEvent.class, target, methodName);
    }

    public Object getSelectedValue() {
        return getResultsTable().getValue();
    }

    public Collection getSelectedValues() {
        return (Collection) getResultsTable().getValue();
    }

    public void search() {
        searchImpl(true);
    }

    protected void searchImpl(boolean clearSelection) {
        getEntityQuery().firstPage();
        getResultsTable().executeCurrentQuery();

        if (clearSelection) {
            getResultsTable().clearSelection();
        }
    }

    protected void refreshResultCountLabel() {
        EntityQuery query = getEntityQuery();
        String caption = uiMessageSource.getMessage("entityResults.caption",
                new Object[]{
                        query.getResultCount() == 0 ? 0 : query.getLastResult(),
                        query.getResultCount()});
        firstResultTextField.setWidth(Math.max(3, query.getResultCount().toString().length() - 1), Sizeable.UNITS_EM);
        resultCountLabel.setValue(caption);
    }
}
