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

package com.purebred.sample.view.role.related;

import com.purebred.core.dao.ToManyRelationshipQuery;
import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.DisplayFields;
import com.purebred.core.view.entity.field.FormFields;
import com.purebred.core.view.entity.tomanyrelationship.ToManyCompositionRelationshipResults;
import com.purebred.core.view.entity.tomanyrelationship.ToManyRelationship;
import com.purebred.sample.dao.PermissionDao;
import com.purebred.sample.entity.security.Permission;
import com.purebred.sample.entity.security.Role;
import com.vaadin.data.Property;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

@Component
@Scope("prototype")
public class RelatedPermissions extends ToManyRelationship<Permission> {

    @Resource
    private RelatedPermissionsResults relatedPermissionsResults;

    @Override
    public String getEntityCaption() {
        return "Permissions";
    }

    @Override
    public RelatedPermissionsResults getResultsComponent() {
        return relatedPermissionsResults;
    }

    @Component
    @Scope("prototype")
    public static class RelatedPermissionsResults extends ToManyCompositionRelationshipResults<Permission> {

        @Resource
        private PermissionDao permissionDao;

        @Resource
        private PermissionForm permissionForm;

        @Resource
        private RelatedPermissionsQuery relatedPermissionsQuery;

        @Override
        public PermissionDao getEntityDao() {
            return permissionDao;
        }

        @Override
        public PermissionForm getEntityForm() {
            return permissionForm;
        }

        @Override
        public ToManyRelationshipQuery getEntityQuery() {
            return relatedPermissionsQuery;
        }

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "entityType",
                    "field",
                    "lastModified",
                    "modifiedBy"
            });
        }

        @Override
        public String getChildPropertyId() {
            return "permissions";
        }

        @Override
        public String getParentPropertyId() {
            return "role";
        }

        @Override
        public String getEntityCaption() {
            return "Permissions";
        }
    }

    @Component
    @Scope("prototype")
    public static class RelatedPermissionsQuery extends ToManyRelationshipQuery<Permission, Role> {

        @Resource
        private PermissionDao permissionDao;

        private Role role;

        @Override
        public void setParent(Role role) {
            this.role = role;
        }

        @Override
        public Role getParent() {
            return role;
        }

        @Override
        public List<Permission> execute() {
            return permissionDao.execute(this);
        }

        @Override
        public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<Permission> rootEntity) {
            List<Predicate> criteria = new ArrayList<Predicate>();

            if (!isEmpty(role)) {
                ParameterExpression<Role> p = builder.parameter(Role.class, "role");
                criteria.add(builder.equal(rootEntity.get("role"), p));
            }

            return criteria;
        }

        @Override
        public void setParameters(TypedQuery typedQuery) {
            if (!isEmpty(role)) {
                typedQuery.setParameter("role", role);
            }
        }

        @Override
        public Path buildOrderBy(Root<Permission> rootEntity) {
            return null;
        }

        @Override
        public void addFetchJoins(Root<Permission> rootEntity) {
            rootEntity.fetch("role", JoinType.LEFT);
        }

        @Override
        public String toString() {
            return "RelatedPermissions{" +
                    "role='" + role + '\'' +
                    '}';
        }
    }

    @Component
    @Scope("prototype")
    public static class PermissionForm extends EntityForm<Permission> {

        @Resource
        private PermissionDao permissionDao;

        @Override
        public void configureFields(FormFields formFields) {
            formFields.setPosition("entityType", 1, 1);
            formFields.setPosition("entityTypeLabel", 1, 2);

            formFields.setPosition("field", 2, 1);
            formFields.setPosition("fieldLabel", 2, 2);

            formFields.setPosition("view", 3, 1);
            formFields.setPosition("edit", 3, 2);

            formFields.setPosition("create", 4, 1);
            formFields.setPosition("delete", 4, 2);

            formFields.setField("entityType", new Select());
            formFields.addValueChangeListener("entityType", this, "entityTypeChanged");

            formFields.setField("field", new Select());

            formFields.addValueChangeListener("view", this, "syncCRUDCheckboxes");
            formFields.addValueChangeListener("field", this, "syncCRUDCheckboxes");
        }

        public void syncCRUDCheckboxes(Property.ValueChangeEvent event) {
            Field viewField = getFormFields().getFormField("view").getField();
            Boolean isViewChecked = (Boolean) viewField.getValue();

            Field fieldField = getFormFields().getFormField("field").getField();
            Boolean isFieldSelected = fieldField.getValue() != null;

            getFormFields().setEnabled("create", isViewChecked && !isFieldSelected);
            getFormFields().setValue("create", isViewChecked && !isFieldSelected);
            getFormFields().setEnabled("edit", isViewChecked);
            getFormFields().setValue("edit", isViewChecked && !isFieldSelected);
            getFormFields().setEnabled("delete", isViewChecked && !isFieldSelected);
            getFormFields().setValue("delete", isViewChecked && !isFieldSelected);
        }

        @Override
        public void postWire() {
            super.postWire();

            getFormFields().setSelectItems("entityType", getAvailableEntityTypeItems());
        }

        @Override
        public void create() {
            super.create();

            getFormFields().setSelectItems("entityType", getAvailableEntityTypeItems());
        }

        public Map<Object, String> getAvailableEntityTypeItems() {
            Map<Object, String> allEntityTypeItems = getEntityTypeItems();
            Set<String> existingEntityTypeItems = getExistingEntityTypeItems();

            return subtractExistingEntityTypeItems(allEntityTypeItems,
                    existingEntityTypeItems);
        }

        public Map<Object, String> getEntityTypeItems() {
            Map<Object, String> entityTypeItems = new LinkedHashMap<Object, String>();
            Map<String, String> entityTypes = labelDepot.getEntityTypeLabels();
            for (String entityType : entityTypes.keySet()) {
                entityTypeItems.put(entityType, entityType);
            }

            return entityTypeItems;
        }

        private Map<Object, String> subtractExistingEntityTypeItems(Map<Object, String> allEntityTypeItems,
                                                                    Set<String> existingEntityTypeItems) {
            Map<Object, String> entityTypeItems = new LinkedHashMap<Object, String>();

            for (Object entityTypeItem : allEntityTypeItems.keySet()) {
                if (!existingEntityTypeItems.contains(entityTypeItem)) {
                    entityTypeItems.put(entityTypeItem, allEntityTypeItems.get(entityTypeItem));
                }
            }

            return entityTypeItems;
        }

        private Set<String> getExistingEntityTypeItems() {
            List<Permission> existingPermissions = getExistingPermissionsForParentRole();

            Set<String> existingEntityTypeItems = new HashSet<String>();
            for (Permission existingPermission : existingPermissions) {
                if (existingPermission.getField() == null) {
                    existingEntityTypeItems.add(existingPermission.getEntityType());
                }
            }

            return existingEntityTypeItems;
        }

        public void entityTypeChanged(Property.ValueChangeEvent event) {
            String newEntityType = (String) event.getProperty().getValue();

            if (newEntityType != null) {
                Map<Object, String> fieldItems = getFieldItems(newEntityType);
                getFormFields().setSelectItems("field", fieldItems, "All");
            }
        }

        private List<Permission> getExistingPermissionsForParentRole() {
            RelatedPermissionsResults results = (RelatedPermissionsResults) getResults();
            Role role = (Role) results.getEntityQuery().getParent();
            return permissionDao.findByRole(role);
        }

        public Map<Object, String> getFieldItems(String entityType) {
            List<Permission> existingPermissions = getExistingPermissionsForParentRole();

            Map<Object, String> fieldItems = labelDepot.getPropertyIds(entityType);
            for (Permission existingPermission : existingPermissions) {
                if (existingPermission.getEntityType().equals(entityType) && existingPermission.getField() != null
                        && !existingPermission.equals(getEntity())) {
                    fieldItems.remove(existingPermission.getField());
                }
            }

            return fieldItems;
        }

        @Override
        public String getEntityCaption() {
            return "Permission Form";
        }

        @Override
        public void configurePopupWindow(Window popupWindow) {
            popupWindow.setWidth("95%");
            popupWindow.setHeight(25, Sizeable.UNITS_EM);
        }
    }
}
