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

import com.purebred.sample.entity.*;
import com.purebred.sample.view.contact.ContactQuery;
import com.google.i18n.phonenumbers.NumberParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.Resource;
import java.util.List;

public class ContactDaoTest extends AbstractDomainTest {

    @Resource
    private ContactDao contactDao;

    @Resource
    private AddressDao addressDao;

    @Resource
    private StateDao stateDao;

    @Resource
    private CountryDao countryDao;

    @Resource
    private ContactQuery contactQuery;

    @Before
    public void createContact() throws NumberParseException {

        Country country = new Country("XX");
        countryDao.persist(country);
        State state = new State("XX-NC", "North Carolina", country);
        stateDao.persist(state);

        Contact contact = new Contact();
        contact.setFirstName("Juan");
        contact.setLastName("Osuna");
        contact.setMainPhone(new Phone("(704) 555-1212", "US"));
        contact.getMainPhone().setPhoneType(PhoneType.BUSINESS);

        Address address = new Address(AddressType.MAILING);
        address.setStreet("100 Main St.");
        address.setCity("Charlotte");
        address.setState(state);
        address.setCountry(country);
        addressDao.persist(address);
        contact.setMailingAddress(address);
        contact.setOtherAddress(null);
        contactDao.persist(contact);
    }

    @Test
    public void findByName() throws NumberParseException {
        contactQuery.setLastName("Osuna");
        List<Contact> contacts = contactQuery.execute();
        Assert.assertNotNull(contacts);
        Assert.assertTrue(contacts.size() > 0);
        Assert.assertEquals("Osuna", contacts.get(0).getLastName());
    }
}
