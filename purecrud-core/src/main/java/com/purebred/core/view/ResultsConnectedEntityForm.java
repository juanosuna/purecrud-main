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

import com.purebred.core.util.MethodDelegate;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * An entity form connected to results, allowing user to walk through the current
 * results using next or previous buttons.
 *
 * @param <T> type of entity in the entity form and results
 */
public class ResultsConnectedEntityForm<T> extends CustomComponent {

    private EntityForm<T> entityForm;
    private WalkableResults results;

    private Button nextButton;
    private Button previousButton;

    private List<MethodDelegate> walkListeners = new ArrayList<MethodDelegate>();

    public ResultsConnectedEntityForm(EntityForm entityForm, WalkableResults results) {
        this.entityForm = entityForm;
        this.results = results;

        initialize();
    }

    private void initialize() {
        setCompositionRoot(createNavigationFormLayout());
        setSizeUndefined();
    }

    private HorizontalLayout createNavigationFormLayout() {
        HorizontalLayout navigationFormLayout = new HorizontalLayout();
        navigationFormLayout.setSizeUndefined();

        VerticalLayout previousButtonLayout = new VerticalLayout();
        previousButtonLayout.setSizeUndefined();
        previousButtonLayout.setMargin(false);
        previousButtonLayout.setSpacing(false);
        Label spaceLabel = new Label("</br></br></br>", Label.CONTENT_XHTML);
        spaceLabel.setSizeUndefined();
        previousButtonLayout.addComponent(spaceLabel);

        previousButton = new Button(null, this, "previousItem");
        previousButton.setDescription(entityForm.uiMessageSource.getMessage("entityForm.previous.description"));
        previousButton.setSizeUndefined();
        previousButton.addStyleName("borderless");
        previousButton.setIcon(new ThemeResource("icons/16/previous.png"));

        if (entityForm.getViewableToManyRelationships().size() == 0) {
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

        navigationFormLayout.addComponent(entityForm);

        VerticalLayout nextButtonLayout = new VerticalLayout();
        nextButtonLayout.setSizeUndefined();
        nextButtonLayout.setMargin(false);
        nextButtonLayout.setSpacing(false);
        spaceLabel = new Label("</br></br></br>", Label.CONTENT_XHTML);
        spaceLabel.setSizeUndefined();
        previousButtonLayout.addComponent(spaceLabel);
        nextButtonLayout.addComponent(spaceLabel);

        nextButton = new Button(null, this, "nextItem");
        nextButton.setDescription(entityForm.uiMessageSource.getMessage("entityForm.next.description"));
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

        return navigationFormLayout;
    }

    void refreshNavigationButtonStates() {
        if (entityForm.isEntityPersistent()) {
            previousButton.setEnabled(results.hasPreviousItem());
            nextButton.setEnabled(results.hasNextItem());
        } else {
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }

    /**
     * Go to previous item in the current results
     */
    public void previousItem() {
        results.editOrViewPreviousItem();
        refreshNavigationButtonStates();
        onWalk();
    }

    /**
     * Go to next item in the current results
     */
    public void nextItem() {
        results.editOrViewNextItem();
        refreshNavigationButtonStates();
        onWalk();
    }

    /**
     * Get entity form that is connected to results
     */
    public EntityForm<T> getEntityForm() {
        return entityForm;
    }

    private void onWalk() {
        for (MethodDelegate walkListener : walkListeners) {
            walkListener.execute();
        }
    }

    /**
     * Add a listener to detect any time user goes to next or previous records in results
     *
     * @param target target object to invoke
     * @param methodName name of method to invoke
     */
    public void addWalkListener(Object target, String methodName) {
        walkListeners.add(new MethodDelegate(target, methodName));
    }
}
