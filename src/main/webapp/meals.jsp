<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
</body>
<body>
<table border="1">
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
    </tr>
    <c:forEach var="user" items="${mealToList}">
        <tr>
            <td><c:out value="${user.description}" /></td>
            <td><c:out value="${user.calories}" /></td>
            <td><c:out value="${user.excess}" /></td>
            <td><c:out value="${user.dateTime}" /></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>