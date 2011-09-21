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

package com.purebred.core.view.menu;

import com.purebred.core.util.MethodDelegate;
import com.purebred.core.util.ObjectUtil;
import com.purebred.core.util.assertion.Assert;
import com.purebred.core.view.util.MessageSource;
import com.vaadin.event.Action;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Scope("prototype")
@Component
public class ActionContextMenu implements Action.Handler {

    @Resource
    private MessageSource uiMessageSource;

    private Map<String, ContextMenuAction> actions = new LinkedHashMap<String, ContextMenuAction>();

    public void addAction(String caption, Object target, String methodName) {
        Action action = new Action(uiMessageSource.getMessage(caption));
        MethodDelegate methodDelegate = new MethodDelegate(target, methodName);
        ContextMenuAction contextMenuAction = new ContextMenuAction(action, methodDelegate);
        actions.put(caption, contextMenuAction);
    }

    public void setActionEnabled(String caption, boolean enabled) {
        actions.get(caption).setEnabled(enabled);
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        List<Action> enabledActions = new ArrayList<Action>();
        for (ContextMenuAction contextMenuAction : actions.values()) {
            if (contextMenuAction.isEnabled()) {
                enabledActions.add(contextMenuAction.getAction());
            }
        }

        return enabledActions.toArray(new Action[enabledActions.size()]);
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        for (ContextMenuAction contextMenuAction : actions.values()) {
            if (action.equals(contextMenuAction.getAction())) {
                Assert.PROGRAMMING.assertTrue(contextMenuAction.isEnabled());
                contextMenuAction.getMethodDelegate().execute();
                break;
            }
        }
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        ActionContextMenu that = (ActionContextMenu) o;
//
//        if (!ObjectUtil.isEqualDeep(actions.values(), that.actions.values())) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        return ObjectUtil.hashCodeDeep(actions.values());
//    }

    private static class ContextMenuAction {
        private Action action;
        private boolean enabled = false;
        private MethodDelegate methodDelegate;

        public ContextMenuAction(Action action, MethodDelegate methodDelegate) {
            this.action = action;
            this.methodDelegate = methodDelegate;
        }

        public Action getAction() {
            return action;
        }

        public MethodDelegate getMethodDelegate() {
            return methodDelegate;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//
//            ContextMenuAction that = (ContextMenuAction) o;
//
//            if (enabled != that.enabled) return false;
//            if (!action.equals(that.action)) return false;
//            if (!methodDelegate.equals(that.methodDelegate)) return false;
//
//            return true;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = action.hashCode();
//            result = 31 * result + (enabled ? 1 : 0);
//            result = 31 * result + methodDelegate.hashCode();
//            return result;
//        }
    }
}

