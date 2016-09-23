<%@page import="com.shending.support.Tools"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if (Tools.parsingRequestIsFromMobile(request)) {

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/index_mobile.html");
        requestDispatcher.forward(request, response);
    } else if (Tools.parsingRequestIsFromLowIe9(request)) {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/index_ie_low.html");
        requestDispatcher.forward(request, response);
    } else {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/index_web.html");
        requestDispatcher.forward(request, response);
    }
%>