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
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.purebred.core.validation;

import com.purebred.core.AbstractCoreTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import java.util.Set;

@ContextConfiguration(locations = {
        "classpath:/spring/applicationContext-test-validation.xml"
})
public class ValidationTest extends AbstractCoreTest {

    @Resource
    private Validation validation;

    @Test
    public void validateInvalidRoot() {
        RootBean rootBean = new RootBean();
        NestedBean nestedBean = new NestedBean();
        nestedBean.setProperty("12");
        rootBean.setNestedBean(nestedBean);

        Set<ConstraintViolation<RootBean>> violations = validation.validate(rootBean);
        Assert.assertTrue(violations.size() > 0);
    }

    @Test
    public void validateRootProperty() {
        RootBean rootBean = new RootBean();
        NestedBean nestedBean = new NestedBean();
        nestedBean.setProperty("12");
        rootBean.setNestedBean(nestedBean);

        Set<ConstraintViolation<RootBean>> violations = validation.validateProperty(rootBean, "nestedBean");
        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void validateInvalidRootNotNullProperty() {
        RootBean rootBean = new RootBean();

        Set<ConstraintViolation<RootBean>> violations = validation.validateProperty(rootBean, "notNullNestedBean");
        Assert.assertTrue(violations.size() == 1);
    }

    @Test
    public void validateInvalidNestedProperty() {
        RootBean rootBean = new RootBean();
        NestedBean nestedBean = new NestedBean();
        nestedBean.setProperty("12");
        rootBean.setNestedBean(nestedBean);

        Set<ConstraintViolation<RootBean>> violations = validation.validateProperty(rootBean, "nestedBean.property");
        Assert.assertTrue(violations.size() == 1);
    }

    @Test
    public void validateInvalidNestedNullProperty() {
        RootBean rootBean = new RootBean();
        NestedBean nestedBean = new NestedBean();
        rootBean.setNestedBean(nestedBean);

        Set<ConstraintViolation<RootBean>> violations = validation.validateProperty(rootBean, "nestedBean.property");
        Assert.assertTrue(violations.size() == 1);
    }

    @Test
    public void validateInvalidNotNullNullNestedProperty() {
        RootBean rootBean = new RootBean();

        Set<ConstraintViolation<RootBean>> violations = validation.validateProperty(rootBean, "notNullNestedBean.property");
        Assert.assertTrue(violations.size() == 1);
    }

    @Test
    public void validateInvalidOptionalNullNestedProperty() {
        RootBean rootBean = new RootBean();

        Set<ConstraintViolation<RootBean>> violations = validation.validateProperty(rootBean,
                "notNullNestedBean.optionalProperty");
        Assert.assertTrue(violations.size() == 1);
    }

    @Test
    public void validateNestedPropertyWithNull() {
        RootBean rootBean = new RootBean();

        Set<ConstraintViolation<RootBean>> violations = validation.validateProperty(rootBean, "nestedBean.property");
        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void validateIgnoredNestedProperty() {
        RootBean rootBean = new RootBean();
        NestedBean nestedBean = new NestedBean();
        nestedBean.setProperty("12");
        rootBean.setIgnoredNestedBean(nestedBean);

        Set<ConstraintViolation<RootBean>> violations = validation.validateProperty(rootBean, "ignoredNestedBean.property");
        Assert.assertTrue(violations.isEmpty());
    }

}
