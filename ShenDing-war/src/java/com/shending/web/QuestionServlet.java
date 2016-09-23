package com.shending.web;

import com.shending.service.AdminService;
import com.shending.web.support.BadPageException;
import com.shending.web.support.BadPostActionException;
import com.shending.web.support.NoSessionException;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

/**
 * 问卷WEB层
 *
 * @author yin.weilong
 */
@WebServlet(name = "QuestionServlet", urlPatterns = {"/question/*"})
public class QuestionServlet extends BaseServlet {

    @EJB
    private AdminService adminService;

    // <editor-fold defaultstate="collapsed" desc="重要但不常修改的函数. Click on the + sign on the left to edit the code.">
    @Override
    public boolean processUrlReWrite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        // PROCESS ROOT PAGE.
        if (pathInfo == null || pathInfo.equals("/")) {
            forward("/question/ask", request, response);
            return FORWARD_TO_ANOTHER_URL;
        }
        String[] pathArray = StringUtils.split(pathInfo, "/");
        PageEnum page = PageEnum.ASK;
        try {
            page = PageEnum.valueOf(pathArray[0].toUpperCase());
        } catch (Exception ex) {
            page = PageEnum.ASK;
            // PROCESS ACCESS CAN NOT PARSE TO A PAGE NAME.
            //redirectToFileNotFound(request, response);
            //return REDIRECT_TO_ANOTHER_URL;
        }

        // 设置这个参数很重要，后续要用。
        request.setAttribute(REQUEST_ATTRIBUTE_PAGE_ENUM, page);
        request.setAttribute(REQUEST_ATTRIBUTE_PATHINFO_ARRAY, pathArray);
        return KEEP_GOING_WITH_ORIG_URL;
    }

    @Override
    public boolean processLoginControl(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        setLoginLogoutBothAllowed(request);
        return KEEP_GOING_WITH_ORIG_URL;
    }

    @Override
    boolean processActionEnum(String actionString, HttpServletRequest request, HttpServletResponse response) throws BadPostActionException, ServletException, IOException {
        ActionEnum action = null;
        try {
            action = ActionEnum.valueOf(actionString);
        } catch (Exception ex) {
            throw new BadPostActionException();
        }
        // 设置这个参数很重要，后续要用到。Even it's null.
        request.setAttribute(REQUEST_ATTRIBUTE_ACTION_ENUM, action);
        return KEEP_GOING_WITH_ORIG_URL;
    }
// </editor-fold>

    enum ActionEnum {

        SET_RESULT;
    }

    @Override
    boolean processAction(HttpServletRequest request, HttpServletResponse response) throws BadPostActionException, ServletException, IOException, NoSessionException {
        ActionEnum action = (ActionEnum) request.getAttribute(REQUEST_ATTRIBUTE_ACTION_ENUM);
        switch (action) {
            case SET_RESULT:
                return doSetResult(request, response);
            default:
                throw new BadPostActionException();
        }
    }

    private enum PageEnum {

        ASK;
    }

    @Override
    boolean processPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException, BadPageException {
        PageEnum page = (PageEnum) request.getAttribute(REQUEST_ATTRIBUTE_PAGE_ENUM);
        switch (page) {
            case ASK:
                return loadAsk(request, response);
            default:
                return KEEP_GOING_WITH_ORIG_URL;
        }
    }

    // ************************************************************************
    // *************** ACTION处理的相关函数，放在这下面
    // ************************************************************************
    private boolean doSetResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String q1 = request.getParameter("q1");
        String q2 = request.getParameter("q2");
        String q3 = request.getParameter("q3");
        String q4 = request.getParameter("q4");
        String q5 = request.getParameter("q5");
        String q6 = getStrings(request, "q6");
        String q7 = getStrings(request, "q7");
        String q8 = getStrings(request, "q8");
        String q9 = request.getParameter("q9");
        String q10 = request.getParameter("q10");
        String q11 = request.getParameter("q11");
        String q12 = request.getParameter("q12");
        String q13 = getStrings(request, "q13");
        String q14 = getStrings(request, "q14");
        String q15 = getStrings(request, "q15");
        String q16 = getStrings(request, "q16");
        adminService.createVote(q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15, q16);
        return KEEP_GOING_WITH_ORIG_URL;
    }

    // ************************************************************************
    // *************** PAGE RANDER处理的相关函数，放在这下面
    // ************************************************************************
    /**
     * 加载问卷页
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    private boolean loadAsk(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return KEEP_GOING_WITH_ORIG_URL;
    }

    // ************************************************************************
    // *************** 支持性函数、共用函数等非直接功能函数，放在这下面
    // ************************************************************************
    private String getStrings(HttpServletRequest request, String s) {
        String[] ss = request.getParameterValues(s);
        if (ss == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : ss) {
            sb.append(str).append(";");
        }
        return sb.toString();
    }
}
