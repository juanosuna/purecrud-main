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

package com.purebred.core.view.menu;

import com.purebred.core.util.MethodDelegate;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.AbstractOrderedLayout;
import org.vaadin.peter.contextmenu.ContextMenu;

import java.util.LinkedHashMap;
import java.util.Map;

public class LayoutContextMenu extends ContextMenu implements LayoutEvents.LayoutClickListener, ContextMenu.ClickListener {

    private Map<String, ContextMenuAction> actions = new LinkedHashMap<String, ContextMenuAction>();

    public LayoutContextMenu(AbstractOrderedLayout layout) {
        super();
        layout.addListener(this);
        layout.addComponent(this);
        addListener(this);
    }

    public ContextMenu.ContextMenuItem addAction(String caption, Object target, String methodName) {
        ContextMenu.ContextMenuItem item = super.addItem(caption);

        MethodDelegate methodDelegate = new MethodDelegate(target, methodName, ContextMenu.ContextMenuItem.class);
        ContextMenuAction contextMenuAction = new ContextMenuAction(item, methodDelegate);
        actions.put(caption, contextMenuAction);

        return item;
    }

    public ContextMenu.ContextMenuItem getContextMenuItem(String caption) {
        return actions.get(caption).getItem();
    }

    public boolean containsItem(String caption) {
        return actions.containsKey(caption);
    }

    @Override
    public void contextItemClick(ContextMenu.ClickEvent clickEvent) {
        ContextMenu.ContextMenuItem clickedItem = clickEvent.getClickedItem();
        ContextMenuAction action = actions.get(clickedItem.getName());
        action.getMethodDelegate().execute(clickedItem);
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        if (LayoutEvents.LayoutClickEvent.BUTTON_RIGHT == event.getButton()) {
            show(event.getClientX(), event.getClientY());
        }
    }

    public static class ContextMenuAction {
        private ContextMenu.ContextMenuItem item;
        private MethodDelegate methodDelegate;

        public ContextMenuAction(ContextMenu.ContextMenuItem item, MethodDelegate methodDelegate) {
            this.item = item;
            this.methodDelegate = methodDelegate;
        }

        public MethodDelegate getMethodDelegate() {
            return methodDelegate;
        }

        public ContextMenuItem getItem() {
            return item;
        }

        public Object execute() {
            return methodDelegate.execute(item);
        }
    }
}

