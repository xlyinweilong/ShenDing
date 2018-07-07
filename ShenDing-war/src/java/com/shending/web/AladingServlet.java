package com.shending.web;

import com.shending.entity.AladingwebSearch;
import com.shending.entity.Cosmetics;
import com.shending.entity.GoodsOrder;
import com.shending.entity.NewAd;
import com.shending.entity.OrderRecord;
import com.shending.entity.ProductLog;
import com.shending.entity.SysUser;
import com.shending.entity.Vip;
import com.shending.entity.WageLog;
import com.shending.service.AdminService;
import com.shending.service.AladingWebService;
import com.shending.service.ConfigService;
import com.shending.support.ResultList;
import com.shending.support.Tools;
import com.shending.support.bo.PlaceWages;
import com.shending.support.enums.CategoryEnum;
import com.shending.support.enums.OrderRecordTypeEnum;
import com.shending.support.enums.OrderStatusEnum;
import com.shending.support.enums.SysUserStatus;
import com.shending.support.enums.WageLogTypeEnum;
import com.shending.support.exception.AccountNotExistException;
import com.shending.support.exception.EjbMessageException;
import com.shending.support.exception.NotVerifiedException;
import com.shending.web.support.BadPageException;
import com.shending.web.support.BadPostActionException;
import com.shending.web.support.NoSessionException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

/**
 * 后台管理WEB层
 *
 * @author yin
 */
@WebServlet(name = "AladingServlet", urlPatterns = {"/alading/*"})
@MultipartConfig(
        fileSizeThreshold = 5_242_880, // 5M  
        maxFileSize = 20_971_520L, //20M  
        maxRequestSize = 41_943_040L //40M  
)
public class AladingServlet extends BaseServlet {

    @EJB
    private AdminService adminService;
    @EJB
    private AladingWebService aladingWebService;
    @EJB
    private ConfigService configService;

    /// <editor-fold defaultstate="collapsed" desc="重要但不常修改的函数. Click on the + sign on the left to edit the code.">
    @Override
    public boolean processUrlReWrite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        // PROCESS ROOT PAGE.
        if (pathInfo == null || pathInfo.equals("/")) {
            forward("/admin/index", request, response);
            return FORWARD_TO_ANOTHER_URL;
        }

        String[] pathArray = StringUtils.split(pathInfo, "/");
        AladingServlet.PageEnum page = null;
        try {
            page = AladingServlet.PageEnum.valueOf(pathArray[0].toUpperCase());
        } catch (Exception ex) {
            // PROCESS ACCESS CAN NOT PARSE TO A PAGE NAME.
            String url = String.format("/admin/index");
            forward(url, request, response);
            return FORWARD_TO_ANOTHER_URL;
        }

        // 设置这个参数很重要，后续要用。
        request.setAttribute(REQUEST_ATTRIBUTE_PAGE_ENUM, page);
        request.setAttribute(REQUEST_ATTRIBUTE_PATHINFO_ARRAY, pathArray);
        return KEEP_GOING_WITH_ORIG_URL;
    }

    @Override
    public boolean processLoginControl(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        AladingServlet.PageEnum page = (AladingServlet.PageEnum) request.getAttribute(REQUEST_ATTRIBUTE_PAGE_ENUM);
        switch (page) {
            case INDEX:
            case QUANGUOTUIGUANG:
            case ZUODAILI:
                setLoginLogoutBothAllowed(request);
                break;
            default:
                setLoginOnly(request);
        }
        return KEEP_GOING_WITH_ORIG_URL;
    }

    @Override
    boolean processActionEnum(String actionString, HttpServletRequest request, HttpServletResponse response) throws BadPostActionException, ServletException, IOException {
        AladingServlet.ActionEnum action = null;
        try {
            action = AladingServlet.ActionEnum.valueOf(actionString);
        } catch (Exception ex) {
            throw new BadPostActionException();
        }
        // 设置这个参数很重要，后续要用到。Even it's null.
        request.setAttribute(REQUEST_ATTRIBUTE_ACTION_ENUM, action);
        return KEEP_GOING_WITH_ORIG_URL;
    }
    // </editor-fold>

    public static enum ActionEnum {

        LOGIN, LOGOUT, SPREAD, APPLY
    }

    @Override
    boolean loginControlForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Boolean pageViewLoginAllowed = (Boolean) request.getAttribute("pageViewLoginAllowed");
        Boolean pageViewLogoutAllowed = (Boolean) request.getAttribute("pageViewLogoutAllowed");
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession();
        Object admin = session.getAttribute("admin");
        boolean adminLogin = false;
        if (admin != null) {
            adminLogin = true;
        }
        if (!adminLogin && pageViewLoginAllowed && !pageViewLogoutAllowed) {
            response.sendRedirect("/admin/login");
            response.flushBuffer();
            return FORWARD_TO_ANOTHER_URL;

        } else if (adminLogin && !pageViewLoginAllowed && pageViewLogoutAllowed) {
            response.sendRedirect("/admin/index");
            response.flushBuffer();
            if (debug) {
                log("Access logout only page, redirect to home from: " + requestURI);
            }
            return FORWARD_TO_ANOTHER_URL;
        } else {
            return KEEP_GOING_WITH_ORIG_URL;
        }
    }

    @Override
    boolean processAction(HttpServletRequest request, HttpServletResponse response) throws BadPostActionException, ServletException, IOException, NoSessionException, NotVerifiedException {
        AladingServlet.ActionEnum action = (AladingServlet.ActionEnum) request.getAttribute(REQUEST_ATTRIBUTE_ACTION_ENUM);
        switch (action) {
            default:
                throw new BadPostActionException();
        }
    }

    public static enum PageEnum {

        INDEX, QUANGUOTUIGUANG, ZUODAILI;

    }

    @Override
    boolean processPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException, BadPageException {
        AladingServlet.PageEnum page = (AladingServlet.PageEnum) request.getAttribute(REQUEST_ATTRIBUTE_PAGE_ENUM);
        switch (page) {
            case INDEX:
            case QUANGUOTUIGUANG:
            case ZUODAILI:
                return loadPage(request, response);
            default:
                throw new BadPageException();
        }
    }
    // ************************************************************************
    // *************** ACTION处理的相关函数，放在这下面
    // ************************************************************************

    /**
     * 登出账户
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    private boolean loadPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("ALADING_FILE_BG_URL", configService.findConfigByKey("ALADING_FILE_BG_URL").getValue());
        request.setAttribute("ALADING_FILE_LOGO", configService.findConfigByKey("ALADING_FILE_LOGO").getValue());
        return KEEP_GOING_WITH_ORIG_URL;
    }

    // ************************************************************************
    // *************** PAGE RANDER处理的相关函数，放在这下面
    // ************************************************************************
    //*********************************************************************
}
