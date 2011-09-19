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
import com.purebred.core.util.MethodDelegate;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Popup window for displaying entity form.
 */
public class EntityFormWindow extends Window {

    private EntityForm entityForm;

    private List<MethodDelegate> closeListeners = new ArrayList<MethodDelegate>();

    /**
     * Construct window to display entity form that is not connected to results.
     *
     * @param entityForm form to display inside window
     */
    protected EntityFormWindow(EntityForm entityForm) {
        super(entityForm.getEntityCaption());

        initialize();
        this.entityForm = entityForm;
        addComponent(entityForm);
        entityForm.addCancelListener(this, "close");
        entityForm.addCloseListener(this, "close");
        entityForm.addSaveListener(this, "refreshCaption");
    }

    /**
     * Construct window to display a results-connected entity form.
     *
     * @param resultsConnectedEntityForm results-connected form
     */
    protected EntityFormWindow(ResultsConnectedEntityForm resultsConnectedEntityForm) {
        super(resultsConnectedEntityForm.getEntityForm().getEntityCaption());

        initialize();
        this.entityForm = resultsConnectedEntityForm.getEntityForm();
        addComponent(resultsConnectedEntityForm);
        resultsConnectedEntityForm.getEntityForm().addCancelListener(this, "close");
        resultsConnectedEntityForm.getEntityForm().addCloseListener(this, "close");
        resultsConnectedEntityForm.addWalkListener(this, "refreshCaption");
        entityForm.addSaveListener(this, "refreshCaption");
        resultsConnectedEntityForm.refreshNavigationButtonStates();
    }

    void refreshCaption() {
        setCaption(entityForm.getEntityCaption());
    }

    private void initialize() {
        addStyleName("p-entity-form-window");
        addStyleName("opaque");
        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        setSizeUndefined();
        setModal(true);
        setClosable(true);
        setScrollable(true);
        addListener(CloseEvent.class, this, "onClose");

        MainApplication.getInstance().getMainWindow().addWindow(this);
    }

    /**
     * Open window to display entity form that is not connected to results
     *
     * @param resultsConnectedEntityForm results-connected form
     * @return window
     */
    public static EntityFormWindow open(ResultsConnectedEntityForm resultsConnectedEntityForm) {
        return new EntityFormWindow(resultsConnectedEntityForm);
    }

    /**
     * Open window to display entity form that is not connected to results.
     *
     * @param entityForm entityForm form to display inside window
     * @return window
     */
    public static EntityFormWindow open(EntityForm entityForm) {
        return new EntityFormWindow(entityForm);
    }

    public void onClose(CloseEvent closeEvent) {
        for (MethodDelegate closeListener : closeListeners) {
            closeListener.execute();
        }
    }

    /**
     * Add a listener to get invoked when user closes the window.
     *
     * @param target     object to invoke
     * @param methodName name of method to invoke
     */
    public void addCloseListener(Object target, String methodName) {
        closeListeners.add(new MethodDelegate(target, methodName));
    }
}
