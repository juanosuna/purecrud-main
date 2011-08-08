<%--
  ~ BROWN BAG CONFIDENTIAL
  ~
  ~ Brown Bag Consulting LLC
  ~ Copyright (c) 2011. All Rights Reserved.
  ~
  ~ NOTICE:  All information contained herein is, and remains
  ~ the property of Brown Bag Consulting LLC and its suppliers,
  ~ if any.  The intellectual and technical concepts contained
  ~ herein are proprietary to Brown Bag Consulting LLC
  ~ and its suppliers and may be covered by U.S. and Foreign Patents,
  ~ patents in process, and are protected by trade secret or copyright law.
  ~ Dissemination of this information or reproduction of this material
  ~ is strictly forbidden unless prior written permission is obtained
  ~ from Brown Bag Consulting LLC.
  --%>

<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>

<html>
<head>
    <title>Login</title>
</head>

<body>
<h2 align="center">Login</h2>

<p align="center">For demo, please log in as username "admin" with password "admin"</p>
<c:if test="${not empty param.login_error}">
      <span style="color: red; ">
        Your login attempt was not successful, try again.<br/><br/>
        Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
      </span>
</c:if>

<form name="f" action="<c:url value='/j_spring_security_check'/>" method="POST">
    <table align="center">
        <tr>
            <td><label for="j_username">User</label></td>
            <td><input type='text' id="j_username" name="j_username"
                       value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'/>
            </td>
            <td align="left"></td>
        </tr>
        <tr>
            <td><label for="j_password">Password</label></td>
            <td><input type='password' id="j_password" name='j_password'></td>
        </tr>
        <tr>
            <td align="right"><input type="checkbox" name="_spring_security_remember_me" value="true"></td>
            <td align="left">remember for two weeks</td>
        </tr>

        <tr>
            <td colspan='2'><input name="submit" type="submit" value="Login"></td>
        </tr>
    </table>
</form>
</body>
</html>