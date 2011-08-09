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

package com.purebred.core.util.assertion;

public class Assert {

    /**
     * Used to assert programming correctness. Failure indicates programming error and should
     * never happen in production. ProgrammingExceptions should be reported as bugs.
     */
    public static final Assert PROGRAMMING = new Assert(AssertionExceptionType.PROGRAMMING_EXCEPTION);

    /**
     * Used to assert configuration correctness. Failure indicates configuration error and should
     * not happen in a running production system. ConfigurationExceptions should be reported
     * to administrator or developer, depending on nature of configuration error.
     */
    public static final Assert CONFIGURATION = new Assert(AssertionExceptionType.CONFIGURATION_EXCEPTION);

    /**
     * Used to assert system correctness. Failure indicates system failure, e.g. database or network down,
     * and may happen in production. SystemException should be reported to an administrator.
     */
    public static final Assert SYSTEM = new Assert(AssertionExceptionType.SYSTEM_EXCEPTION);

    /**
     * Used to enforce contracts with an external, client system. Failure indicates client
     * has violated a contract. ClientException should be reported to client-side developers.
     */
    public static final Assert BUSINESS = new Assert(AssertionExceptionType.BUSINESS_EXCEPTION);

    /**
     * Used to assert database correctness. Failure indicates database integrity issue.
     * DatabaseException should be reported to database administrator or developer.
     */
    public static final Assert DATABASE = new Assert(AssertionExceptionType.DATABASE_EXCEPTION);

    private AssertionExceptionType assertionExceptionType;

    private Assert(AssertionExceptionType assertionExceptionType) {
        this.assertionExceptionType = assertionExceptionType;
    }

    /**
     * Asserts condition and throws exception if condition is not true
     *
     * @param condition boolean expression
     */
    public void assertTrue(boolean condition) {
        if (!condition) {
            throw assertionExceptionType.create();
        }
    }

    /**
     * Asserts condition and throws exception if condition is not true
     *
     * @param condition boolean expression
     * @param message   to be embedded in thrown exception
     */
    public void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw assertionExceptionType.create(message);
        }
    }

    /**
     * Forces the throwing of an exception.
     *
     * @param message to embed in the exception
     * @throws RuntimeException Always thrown exception containing given description
     */
    public void fail(String message) throws RuntimeException {
        throw assertionExceptionType.create(message);
    }

    /**
     * Forces the throwing of an exception
     *
     * @param message to embed in the exception
     * @param cause   root cause for chaining to thrown exception
     * @throws RuntimeException Always thrown exception containing given description
     */
    public void fail(String message, Throwable cause) throws RuntimeException {
        throw assertionExceptionType.create(message, cause);
    }

    /**
     * Forces throw throwing of an exception
     *
     * @param cause root cause for chaining to thrown exception
     * @throws RuntimeException Always thrown exception containing given description
     */
    public void fail(Throwable cause) throws RuntimeException {
        throw assertionExceptionType.create(cause);
    }
}
