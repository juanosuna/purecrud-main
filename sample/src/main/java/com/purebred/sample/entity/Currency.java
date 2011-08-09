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

package com.purebred.sample.entity;


import com.purebred.core.entity.ReferenceEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

import static com.purebred.core.entity.ReferenceEntity.CACHE_REGION;

@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = CACHE_REGION)
public class Currency extends ReferenceEntity {

    public Currency() {
    }

    public Currency(String id) {
        super(id, id);
    }

    public Currency(String id, String displayName) {
        super(id, displayName);
    }
}
