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

package com.purebred.sample.view.opportunity;

import com.purebred.core.view.entity.SearchForm;
import com.purebred.core.view.entity.field.FormFields;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class OpportunitySearchForm extends SearchForm<OpportunityQuery> {

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("accountName", 1, 1);
        formFields.setPosition("salesStages", 1, 2);

        formFields.setMultiSelectDimensions("salesStages", 3, 10);
    }

    @Override
    public String getEntityCaption() {
        return "Opportunity Search Form";
    }
}
