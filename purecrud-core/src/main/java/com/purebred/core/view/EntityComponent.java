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

package com.purebred.core.view;

import com.purebred.core.view.util.MessageSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Any generic entity component that contains some results.
 * @param <T>
 */
public abstract class EntityComponent<T> extends CustomComponent {

    @Resource
    protected MessageSource entityMessageSource;

    @Resource
    protected MessageSource uiMessageSource;

    protected EntityComponent() {
    }

    /**
     * Get results.
     *
     * @return results
     */
    public abstract Results getResults();

    /**
     * Get the caption used to represent this type of entity, e.g. to be displayed in tab.
     *
     * @return display caption
     */
    public abstract String getEntityCaption();

    @Override
    public String getCaption() {
        return entityMessageSource.getMessageWithDefault(getEntityCaption());
    }

    /**
     * Called after Spring constructs this bean. Overriding methods should call super.
     */
    @PostConstruct
    public void postConstruct() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setCompositionRoot(layout);

        setCustomSizeUndefined();
    }

    private void setCustomSizeUndefined() {
        setSizeUndefined();
        getCompositionRoot().setSizeUndefined();
    }

    /**
     * Can be overridden if any initialization is required after all Spring beans have been wired.
     * Overriding methods should call super.
     */
    public void postWire() {
    }

    @Override
    public void addComponent(Component c) {
        ((ComponentContainer) getCompositionRoot()).addComponent(c);
    }
}
