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
 * services from a hosted web application, shipping PureCRUD with a closed
 * source product.
 *
 * For more information, please contact Brown Bag Consulting at this
 * address: juan@brownbagconsulting.com.
 */

package com.purebred.core.view.entityselect;

import com.purebred.core.view.util.MessageSource;
import com.purebred.core.view.Results;
import com.purebred.core.view.menu.ActionContextMenu;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;

public abstract class EntitySelectResults<T> extends Results<T> {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Resource
    private ActionContextMenu actionContextMenu;

    private Button selectButton;

    protected EntitySelectResults() {
        super();
    }

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();
        addSelectionChangedListener(this, "selectionChanged");

        HorizontalLayout crudButtons = new HorizontalLayout();
        crudButtons.setMargin(false);
        crudButtons.setSpacing(true);

        selectButton = new Button(uiMessageSource.getMessage("entityResults.select"));
        selectButton.setDescription(uiMessageSource.getMessage("entityResults.select.description"));
        selectButton.setEnabled(false);
        selectButton.addStyleName("small default");
        crudButtons.addComponent(selectButton);

        getCrudButtons().addComponent(crudButtons, 0);
        getCrudButtons().setComponentAlignment(crudButtons, Alignment.MIDDLE_LEFT);
    }

    public void selectionChanged() {
        Object itemId = getResultsTable().getValue();
        if (itemId instanceof Collection) {
            if (((Collection) itemId).size() > 0) {
                selectButton.setEnabled(true);
                getResultsTable().addActionHandler(actionContextMenu);
            } else {
                selectButton.setEnabled(false);
                getResultsTable().removeActionHandler(actionContextMenu);
            }
        } else {
            if (itemId != null) {
                selectButton.setEnabled(true);
                getResultsTable().addActionHandler(actionContextMenu);
            } else {
                selectButton.setEnabled(false);
                getResultsTable().removeActionHandler(actionContextMenu);
            }
        }
    }

    public void setSelectButtonListener(Object target, String methodName) {
        selectButton.removeListener(Button.ClickEvent.class, target, methodName);
        selectButton.addListener(Button.ClickEvent.class, target, methodName);
        actionContextMenu.addAction("entityResults.select", target, methodName);
        actionContextMenu.setActionEnabled("entityResults.select", true);
    }
}
