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

package com.purebred.sample.view.opportunity;

import com.purebred.core.view.CrudResults;
import com.purebred.core.view.ResultsTable;
import com.purebred.core.view.field.DisplayFields;
import com.purebred.sample.dao.OpportunityDao;
import com.purebred.sample.entity.Opportunity;
import com.vaadin.terminal.Sizeable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
@SuppressWarnings({"serial"})
public class OpportunityResults extends CrudResults<Opportunity> {

    @Resource
    private OpportunityDao opportunityDao;

    @Resource
    private OpportunityQuery opportunityQuery;

    @Resource
    private OpportunityForm opportunityForm;

    @Override
    public OpportunityDao getEntityDao() {
        return opportunityDao;
    }

    @Override
    public OpportunityQuery getEntityQuery() {
        return opportunityQuery;
    }

    @Override
    public OpportunityForm getEntityForm() {
        return opportunityForm;
    }

    @Override
    public void configureFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "name",
                "salesStage",
                "amountWeightedInUSDFormatted",
                "expectedCloseDate",
                "lastModified",
                "modifiedBy"
        });

        displayFields.setLabel("amountWeightedInUSDFormatted", "Weighted Amount");
    }

    @Override
    public void configureTable(ResultsTable resultsTable) {
        resultsTable.setWidth(70, Sizeable.UNITS_EM);
    }
}
