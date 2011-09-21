/*
 * Copyright (c) 2011 Brown Bag Consulting.
 * This file is part of the PureCRUD project.
 * Author: Juan Osuna
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License Version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * Brown Bag Consulting, Brown Bag Consulting DISCLAIMS THE WARRANTY OF
 * NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the PureCRUD software without
 * disclosing the source code of your own applications. These activities
 * include: offering paid services to customers as an ASP, providing
 * services from a web application, shipping PureCRUD with a closed
 * source product.
 *
 * For more information, please contact Brown Bag Consulting at this
 * address: juan@brownbagconsulting.com.
 */

package com.purebred.core.view;

import com.purebred.core.dao.EntityDao;
import com.purebred.core.dao.EntityQuery;
import com.purebred.core.util.ReflectionUtil;
import com.purebred.core.view.util.MessageSource;
import com.purebred.core.view.field.DisplayFields;
import com.purebred.core.view.field.LabelDepot;
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

/**
 * Results component that is bound the results of a query.
 * Also, provides paging, sorting and adding/removing columns and re-ordering columns.
 *
 * @param <T> type of entity displayed in the results
 */
public abstract class Results<T> extends CustomComponent {

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

    protected Results() {
    }

    /**
     * Configure the fields/columns to be displayed in the results
     *
     * @param displayFields used for configuring fields/columns
     */
    public abstract void configureFields(DisplayFields displayFields);

    /**
     * Get the DAO that can be used to execute queries and perform CRUD operations
     *
     * @return DAO of the entity type for these results
     */
    public abstract EntityDao<T, ? extends Serializable> getEntityDao();

    /**
     * Get the query used to create these results
     *
     * @return query used to create these results
     */
    public abstract EntityQuery<T> getEntityQuery();

    /**
     * Get the fields to be displayed in the results.
     *
     * @return fields to be displayed in the results
     */
    public DisplayFields getDisplayFields() {
        return displayFields;
    }

    /**
     * Type of business entity for this entry point.
     *
     * @return type of business entity for this entry point
     */
    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    /**
     * Get the underlying UI table component used to display results.
     *
     * @return UI table component
     */
    public ResultsTable getResultsTable() {
        return resultsTable;
    }

    /**
     * Get horizontal layout of CRUD buttons
     *
     * @return horizontal layout of CRUD buttons, create, edit, view, delete
     */
    public HorizontalLayout getCrudButtons() {
        return crudButtons;
    }

    @Resource
    public void setDisplayFields(DisplayFields displayFields) {
        this.displayFields = displayFields;
        displayFields.setEntityType(getEntityType());
    }

    /**
     * Called after Spring constructs this bean. Overriding methods should call super.
     */
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

    private void setCustomSizeUndefined() {
        setSizeUndefined();
        getCompositionRoot().setSizeUndefined();
    }

    /**
     * Configure the results table. Maybe overridden to make any configuration changes to the Vaadin table
     *
     * @param resultsTable Vaadin table
     */
    public void configureTable(ResultsTable resultsTable) {
    }

    @Override
    public void addComponent(Component c) {
        ((ComponentContainer) getCompositionRoot()).addComponent(c);
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

    /**
     * Can be overridden if any initialization is required after all Spring beans have been wired.
     * Overriding methods should call super.
     */
    public void postWire() {
        MethodProperty pageProperty = new MethodProperty(this, "pageSize");
        pageSizeMenu.setPropertyDataSource(pageProperty);
        pageSizeMenu.addListener(Property.ValueChangeEvent.class, this, "search");
        getEntityQuery().postWire();
    }

    /**
     * Change the page size selection
     *
     * @param pageSize new page size
     */
    public void selectPageSize(Integer pageSize) {
        pageSizeMenu.select(pageSize);
    }

    /**
     * Get currently selected page size
     *
     * @return currently selected page size
     */
    public int getPageSize() {
        return getEntityQuery().getPageSize();
    }

    /**
     * Set the page size in the entity query
     *
     * @param pageSize new page size
     */
    public void setPageSize(int pageSize) {
        getEntityQuery().setPageSize(pageSize);
    }

    /**
     * Add a listener that detects row-selection changes in the results
     *
     * @param target target object to invoke listener on
     * @param methodName name of method to invoke when selection occurs
     */
    public void addSelectionChangedListener(Object target, String methodName) {
        resultsTable.addListener(Property.ValueChangeEvent.class, target, methodName);
    }

    /**
     * Get the currently selected value in the results table. Could be a single entity or a collection of entities.
     *
     * @return either single entity or collection of entities
     */
    public Object getSelectedValue() {
        return getResultsTable().getValue();
    }

    /**
     * Get the currently selected values in the results table.
     *
     * @return collection of entities
     */
    public Collection getSelectedValues() {
        return (Collection) getResultsTable().getValue();
    }

    /**
     * Execute current query and refresh results. Any existing selected rows are cleared.
     */
    public void search() {
        searchImpl(true);
    }

    /**
     * Execute current query and refresh results.
     * @param clearSelection true if row selection should be cleared
     */
    protected void searchImpl(boolean clearSelection) {
        getEntityQuery().firstPage();
        getResultsTable().executeCurrentQuery();

        if (clearSelection) {
            getResultsTable().clearSelection();
        }
    }

    /**
     * Refresh the label displaying the result count
     */
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
