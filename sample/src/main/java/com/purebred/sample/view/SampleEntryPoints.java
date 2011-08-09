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

package com.purebred.sample.view;

import com.purebred.core.view.MainEntryPoints;
import com.purebred.core.view.entity.EntryPoint;
import com.purebred.sample.view.account.AccountEntryPoint;
import com.purebred.sample.view.contact.ContactEntryPoint;
import com.purebred.sample.view.opportunity.OpportunityEntryPoint;
import com.purebred.sample.view.user.UserEntryPoint;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("session")
public class SampleEntryPoints extends MainEntryPoints {

    @Resource
    private ContactEntryPoint contactEntryPoint;

    @Resource
    private AccountEntryPoint accountEntryPoint;

    @Resource
    private OpportunityEntryPoint opportunityEntryPoint;

    @Resource
    private UserEntryPoint userEntryPoint;

    @Override
    public List<EntryPoint> getEntryPoints() {
        List<EntryPoint> entryPoints = new ArrayList<EntryPoint>();
        entryPoints.add(contactEntryPoint);
        entryPoints.add(accountEntryPoint);
        entryPoints.add(opportunityEntryPoint);
        entryPoints.add(userEntryPoint);

        return entryPoints;
    }
}
