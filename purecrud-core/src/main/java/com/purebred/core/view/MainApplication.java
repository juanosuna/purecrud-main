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

import com.vaadin.Application;
import com.vaadin.addon.chameleon.ChameleonTheme;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.DefaultConfirmDialogFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainApplication extends Application implements HttpServletRequestListener {

    private static ThreadLocal<MainApplication> threadLocal = new ThreadLocal<MainApplication>();

    @Resource(name = "uiMessageSource")
    private MessageSource messageSource;

    @Resource
    private MainEntryPoints mainEntryPoints;

    public static MainApplication getInstance() {
        return threadLocal.get();
    }

    public static void setInstance(MainApplication application) {
        if (getInstance() == null) {
            threadLocal.set(application);
        }
    }

    @Override
    public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
        MainApplication.setInstance(this);
    }

    @Override
    public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        threadLocal.remove();
    }

    @Override
    public void init() {
        setInstance(this);

        setTheme(mainEntryPoints.getTheme());
        customizeConfirmDialogStyle();

        Window mainWindow = new Window(messageSource.getMessage("mainApplication.caption"));
        mainWindow.getContent().setSizeUndefined();
        setMainWindow(mainWindow);

        mainWindow.addComponent(mainEntryPoints);
    }

    private void customizeConfirmDialogStyle() {
        ConfirmDialog.Factory confirmDialogFactory = new DefaultConfirmDialogFactory() {
            @Override
            public ConfirmDialog create(String caption, String message,
                                        String okCaption, String cancelCaption) {
                ConfirmDialog confirmDialog;
                confirmDialog = super.create(caption, message, okCaption, cancelCaption);
                confirmDialog.setStyleName(ChameleonTheme.WINDOW_OPAQUE);
                confirmDialog.getOkButton().addStyleName("small default");
                confirmDialog.getCancelButton().addStyleName("small default");

                return confirmDialog;
            }
        };
        ConfirmDialog.setFactory(confirmDialogFactory);
    }

    @Override
    public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
        super.terminalError(event);
        if (event.getThrowable().getCause() instanceof AccessDeniedException) {
            getMainWindow().showNotification(
                    messageSource.getMessage("mainApplication.accessDenied"),
                    Window.Notification.TYPE_ERROR_MESSAGE);
        } else {
            String fullStackTrace = ExceptionUtils.getFullStackTrace(event.getThrowable());
            openErrorWindow(fullStackTrace);
        }
    }

    public void openErrorWindow(String message) {
        Window errorWindow = new Window("Error");
        errorWindow.addStyleName("opaque");
        VerticalLayout layout = (VerticalLayout) errorWindow.getContent();
        layout.setSpacing(true);
        layout.setWidth("100%");
        errorWindow.setWidth("100%");
        errorWindow.setModal(true);
        Label label = new Label(message);
        label.setContentMode(Label.CONTENT_PREFORMATTED);
        layout.addComponent(label);
        errorWindow.setClosable(true);
        errorWindow.setScrollable(true);
        MainApplication.getInstance().getMainWindow().addWindow(errorWindow);
    }
}
