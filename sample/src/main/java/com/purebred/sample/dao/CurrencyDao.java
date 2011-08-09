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

package com.purebred.sample.dao;

import com.purebred.core.dao.EntityDao;
import com.purebred.sample.entity.Currency;
import com.purebred.sample.service.ecbfx.EcbfxService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.Query;
import java.util.List;

import static com.purebred.sample.dao.CacheSettings.setReadOnly;

@Repository
public class CurrencyDao extends EntityDao<Currency, String> {

    @Resource
    private EcbfxService ecbfxService;

    @Override
    public List<Currency> findAll() {
        Query query = getEntityManager().createQuery("SELECT c FROM Currency c " +
                " WHERE c.id in :currenciesWithFxRates ORDER BY c.displayName");

        query.setParameter("currenciesWithFxRates", ecbfxService.getFXRates().keySet());
        setReadOnly(query);

        return query.getResultList();
    }
}
