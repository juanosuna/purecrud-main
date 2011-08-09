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

package com.purebred.sample.view.select;

import com.purebred.core.view.entity.entityselect.EntitySelect;
import com.purebred.core.view.entity.entityselect.EntitySelectResults;
import com.purebred.core.view.entity.field.DisplayFields;
import com.purebred.sample.dao.OpportunityDao;
import com.purebred.sample.entity.Opportunity;
import com.purebred.sample.view.opportunity.OpportunityQuery;
import com.purebred.sample.view.opportunity.OpportunitySearchForm;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class OpportunitySelect extends EntitySelect<Opportunity> {

    @Resource
    private OpportunitySearchForm opportunitySearchForm;

    @Resource
    private OpportunitySelectResults opportunitySelectResults;

    @Override
    public OpportunitySearchForm getSearchForm() {
        return opportunitySearchForm;
    }

    @Override
    public OpportunitySelectResults getResultsComponent() {
        return opportunitySelectResults;
    }

    @Component
    @Scope("prototype")
    public static class OpportunitySelectResults extends EntitySelectResults<Opportunity> {

        @Resource
        private OpportunityDao opportunityDao;

        @Resource
        private OpportunityQuery opportunityQuery;

        @Override
        public OpportunityDao getEntityDao() {
            return opportunityDao;
        }

        @Override
        public OpportunityQuery getEntityQuery() {
            return opportunityQuery;
        }

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "name",
                    "salesStage",
                    "amountWeightedInUSDFormatted",
                    "expectedCloseDate"
            });

        }
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(58, Sizeable.UNITS_EM);
        popupWindow.setHeight("95%");
    }
}

