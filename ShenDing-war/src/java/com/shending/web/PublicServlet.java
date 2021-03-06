/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.web;

import com.shending.web.support.BadPageException;
import com.shending.web.support.BadPostActionException;
import com.shending.web.support.NoSessionException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

/**
 * 公共WEB层
 *
 * @author yin.weilong
 */
@WebServlet(name = "PublicServlet", urlPatterns = {"/public/*"})
public class PublicServlet extends BaseServlet {

    // <editor-fold defaultstate="collapsed" desc="重要但不常修改的函数. Click on the + sign on the left to edit the code.">
    @Override
    public boolean processUrlReWrite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        // PROCESS ROOT PAGE.
        if (pathInfo == null || pathInfo.equals("/")) {
            forward("/public/index", request, response);
            return FORWARD_TO_ANOTHER_URL;
        }
        String[] pathArray = StringUtils.split(pathInfo, "/");
        PageEnum page = PageEnum.INDEX;
        try {
            page = PageEnum.valueOf(pathArray[0].toUpperCase());
        } catch (Exception ex) {
            page = PageEnum.INDEX;
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

        SET_LANG, FOLLOW_US, SHARE_TO_WEIBO;
    }

    @Override
    boolean processAction(HttpServletRequest request, HttpServletResponse response) throws BadPostActionException, ServletException, IOException, NoSessionException {
        ActionEnum action = (ActionEnum) request.getAttribute(REQUEST_ATTRIBUTE_ACTION_ENUM);
        switch (action) {
            default:
                throw new BadPostActionException();
        }
    }

    private enum PageEnum {

        INDEX, SET_LANG, KEEP_SESSION, SEARCH, NO_AUTHORIZATION, SEARCH_INFO;
    }

    @Override
    boolean processPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException, BadPageException {
        PageEnum page = (PageEnum) request.getAttribute(REQUEST_ATTRIBUTE_PAGE_ENUM);
        switch (page) {
            case INDEX:
                return loadIndex(request, response);
            default:
                return KEEP_GOING_WITH_ORIG_URL;
        }
    }

    // ************************************************************************
    // *************** ACTION处理的相关函数，放在这下面
    // ************************************************************************
    // ************************************************************************
    // *************** PAGE RANDER处理的相关函数，放在这下面
    // ************************************************************************
    /**
     * 加载首页
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    private boolean loadIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return KEEP_GOING_WITH_ORIG_URL;
    }

    // ************************************************************************
    // *************** 支持性函数、共用函数等非直接功能函数，放在这下面
    // ************************************************************************
}
