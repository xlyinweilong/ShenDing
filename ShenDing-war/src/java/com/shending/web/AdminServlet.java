package com.shending.web;

import com.shending.entity.GoodsOrder;
import com.shending.entity.NewAd;
import com.shending.entity.OrderRecord;
import com.shending.entity.ProductLog;
import com.shending.entity.SysUser;
import com.shending.entity.WageLog;
import com.shending.service.AdminService;
import com.shending.support.ResultList;
import com.shending.support.Tools;
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
import java.io.IOException;
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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

/**
 * 后台管理WEB层
 *
 * @author yin
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/order/*", "/admin/message/*", "/admin/account/*", "/admin/auth/*", "/admin/plate/*", "/admin/datadict/*", "/admin/common/*", "/admin/organization/*", "/admin/*"})
public class AdminServlet extends BaseServlet {

    @EJB
    private AdminService adminService;

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
        AdminServlet.PageEnum page = null;
        try {
            page = AdminServlet.PageEnum.valueOf(pathArray[0].toUpperCase());
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
        AdminServlet.PageEnum page = (AdminServlet.PageEnum) request.getAttribute(REQUEST_ATTRIBUTE_PAGE_ENUM);
        switch (page) {
            case INDEX:
            case LOGIN:
            case VERIFICATION_CODE:
            case LOGOUT:
            case FEEDBACK_CSV:
            case SIGNUP:
            case AD_LIST:
            case WAGE_LIST:
            case ORDER_WAGE_LIST:
            case WAGE_LOG_LIST:
            case USER_WAGE_LOG_TOTAL_LIST:
            case USER_WAGE_LOG_LIST:
            case WAGE_TOTAL_LIST:
            case ORDER_RECORD_LIST:
            case CONTRACT_LIST:
            case PRODUCT_LOG_WAGE_LIST:
            case PRODUCT_LOG_LIST:
            case ALL_WAGE_LIST:
                setLoginLogoutBothAllowed(request);
                break;
            default:
                setLoginOnly(request);
        }
        return KEEP_GOING_WITH_ORIG_URL;
    }

    @Override
    boolean processActionEnum(String actionString, HttpServletRequest request, HttpServletResponse response) throws BadPostActionException, ServletException, IOException {
        AdminServlet.ActionEnum action = null;
        try {
            action = AdminServlet.ActionEnum.valueOf(actionString);
        } catch (Exception ex) {
            throw new BadPostActionException();
        }
        // 设置这个参数很重要，后续要用到。Even it's null.
        request.setAttribute(REQUEST_ATTRIBUTE_ACTION_ENUM, action);
        return KEEP_GOING_WITH_ORIG_URL;
    }
    // </editor-fold>

    public static enum ActionEnum {

        LOGIN, LOGOUT,
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
        AdminServlet.ActionEnum action = (AdminServlet.ActionEnum) request.getAttribute(REQUEST_ATTRIBUTE_ACTION_ENUM);
        switch (action) {
            case LOGIN:
                return doLogin(request, response);
            case LOGOUT:
                return doLogout(request, response);
            default:
                throw new BadPostActionException();
        }
    }

    public static enum PageEnum {

        INDEX, VERIFICATION_CODE, FEEDBACK_CSV, LOGIN, LOGOUT, SIGNUP, AD_LIST, WAGE_LIST, ORDER_WAGE_LIST, WAGE_LOG_LIST, USER_WAGE_LOG_TOTAL_LIST, USER_WAGE_LOG_LIST, WAGE_TOTAL_LIST, ORDER_RECORD_LIST, CONTRACT_LIST, PRODUCT_LOG_LIST, PRODUCT_LOG_WAGE_LIST, ALL_WAGE_LIST;

    }

    @Override
    boolean processPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException, BadPageException {
        AdminServlet.PageEnum page = (AdminServlet.PageEnum) request.getAttribute(REQUEST_ATTRIBUTE_PAGE_ENUM);
        switch (page) {
            case INDEX:
                return KEEP_GOING_WITH_ORIG_URL;
            case VERIFICATION_CODE:
                return getVerificationCode(request, response);
            case LOGIN:
                return FORWARD_TO_ANOTHER_URL;
            case SIGNUP:
                return doSignup(request, response);
            case LOGOUT:
                return loadLogout(request, response);
            case AD_LIST:
                return loadAdList(request, response);
            case WAGE_LIST:
                return loadWageList(request, response);
            case ALL_WAGE_LIST:
                return loadAllWageList(request, response);
            case ORDER_WAGE_LIST:
                return loadOrderWageList(request, response);
            case WAGE_LOG_LIST:
                return loadWageLogList(request, response);
            case USER_WAGE_LOG_TOTAL_LIST:
                return loadUserWageLogTotalList(request, response);
            case USER_WAGE_LOG_LIST:
                return loadUserWageLogList(request, response);
            case WAGE_TOTAL_LIST:
                return loadWageTotalList(request, response);
            case ORDER_RECORD_LIST:
                return loadOrderRecordList(request, response);
            case CONTRACT_LIST:
                return loadContractList(request, response);
            case PRODUCT_LOG_LIST:
                return loadProductLogList(request, response);
            case PRODUCT_LOG_WAGE_LIST:
                return loadProductLogWageList(request, response);
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
    private boolean doLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return REDIRECT_TO_ANOTHER_URL;
    }

    /**
     * 登录
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    private boolean doLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String account = getRequestString(request, "account");
        String passwd = getRequestString(request, "passwd");
        String code = getRequestString(request, "code");
        Map map = Tools.getDMap();
        String sRand = (String) super.getSessionValue(request, "rand");
        if (sRand == null || !sRand.equalsIgnoreCase(code)) {
            super.setSessionValue(request, "rand", Tools.generateRandom8Chars());//清除原验证码
            map.put("msg", "验证码错误！");
            PrintWriter out = response.getWriter();
            try {
                out.println(Tools.caseObjectToJson(map));
            } finally {
                out.close();
            }
            return FORWARD_TO_ANOTHER_URL;
        }
        SysUser user = null;
        try {
            user = adminService.login(account, passwd);
        } catch (AccountNotExistException | EjbMessageException ex) {
            map.put("msg", ex.getMessage());
            super.setSessionValue(request, "rand", Tools.generateRandom8Chars());//清除原验证码
            PrintWriter out = response.getWriter();
            try {
                out.println(Tools.caseObjectToJson(map));
            } finally {
                out.close();
            }
            return FORWARD_TO_ANOTHER_URL;
        }
        map.put("success", "1");
        map.put("msg", "登录成功！");
        Map subMap = new HashMap();
        subMap.put("name", user.getName());
        subMap.put("headImageUrl", user.getHeadImageUrl());
        user = adminService.setAccountLoginCode(user);
        map.put("user", subMap);
        PrintWriter out = response.getWriter();
        super.setHttpOnlyCookieValue(request, response, "auth", user.getLoginCode());
        try {
            out.println(Tools.caseObjectToJson(map));
        } finally {
            out.close();
        }
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 注册
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    private boolean doSignup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String account = getRequestString(request, "account");
        String passwd = getRequestString(request, "passwd");
        String name = getRequestString(request, "name");
        String code = getRequestString(request, "code");
        String birthday = getRequestString(request, "birthday");
        String idCard = getRequestString(request, "idCard");
        String mobile = getRequestString(request, "mobile");
        String email = getRequestString(request, "email");
        String weChatCode = getRequestString(request, "weChatCode");
        String qq = getRequestString(request, "qq");
        String province = getRequestString(request, "province");
        Integer sex = getRequestInteger(request, "sex");
        String city = getRequestString(request, "city");
        String area = getRequestString(request, "area");
        String address = getRequestString(request, "address");
        String bankType = getRequestString(request, "bankType");
        String bankCardCode = getRequestString(request, "bankCardCode");
        Date birthdayDate = null;
        try {
            birthdayDate = Tools.parseDate(birthday, "yyyy-MM-dd");
        } catch (Exception e) {
            birthdayDate = null;
        }
        Map map = Tools.getDMap();
        if (Tools.isBlank(account) || Tools.isBlank(passwd) || Tools.isBlank(name)) {
            map.put("msg", "参数异常");
            return FORWARD_TO_ANOTHER_URL;
        }
//        if (Tools.isBlank(email)) {
//            map.put("msg", "请输入正确邮箱");
//            return FORWARD_TO_ANOTHER_URL;
//        }
        if (account.length() < 4) {
            map.put("msg", "帐号至少4位");
            return FORWARD_TO_ANOTHER_URL;
        }
        if (passwd.length() < 6) {
            map.put("msg", "密码至少6位");
            return FORWARD_TO_ANOTHER_URL;
        }
        String sRand = (String) super.getSessionValue(request, "rand");
        if (sRand == null || !sRand.equalsIgnoreCase(code)) {
            super.setSessionValue(request, "rand", Tools.generateRandom8Chars());//清除原验证码
            map.put("msg", "验证码错误！");
            PrintWriter out = response.getWriter();
            try {
                out.println(Tools.caseObjectToJson(map));
            } finally {
                out.close();
            }
            return FORWARD_TO_ANOTHER_URL;
        }
        SysUser user = null;
        try {
            user = adminService.createOrUpdateSysUser(null, account, name, passwd, email, sex, birthdayDate, idCard, mobile, weChatCode, qq, province, city, area, address, SysUserStatus.PEDING, null, bankType, bankCardCode);
        } catch (EjbMessageException ex) {
            map.put("msg", ex.getMessage());
            super.setSessionValue(request, "rand", Tools.generateRandom8Chars());//清除原验证码
            PrintWriter out = response.getWriter();
            try {
                out.println(Tools.caseObjectToJson(map));
            } finally {
                out.close();
            }
            return FORWARD_TO_ANOTHER_URL;
        }
        map.put("success", "1");
        map.put("msg", "登录成功！");
        Map subMap = new HashMap();
        subMap.put("name", user.getName());
        subMap.put("headImageUrl", user.getHeadImageUrl());
        if (Tools.isBlank(user.getLoginCode())) {
            user = adminService.setAccountLoginCode(user);
        }
        map.put("user", subMap);
        PrintWriter out = response.getWriter();
        super.setHttpOnlyCookieValue(request, response, "auth", user.getLoginCode());
        try {
            out.println(Tools.caseObjectToJson(map));
        } finally {
            out.close();
        }
        return FORWARD_TO_ANOTHER_URL;
    }

    // ************************************************************************
    // *************** PAGE RANDER处理的相关函数，放在这下面
    // ************************************************************************
    //*********************************************************************
    /**
     * 获取验证码
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean getVerificationCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        int width = 60, height = 20;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Random random = new Random();
        g.setColor(Tools.getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        g.setColor(Tools.getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        String sRand = "";
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 16);
        }
        // 将认证码存入SESSION
        request.getSession().setAttribute("rand", sRand);
        g.dispose();
        ImageIO.write(image, "JPEG", response.getOutputStream());
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 登出
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        super.removeCookie(request, response, "auth");
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 生成广告对账报表
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadAdList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "ad.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        Map searchMap = new HashMap();
        CategoryEnum category = CategoryEnum.valueOf(super.getRequestString(request, "category"));
        searchMap.put("category", category);
        if (startDate != null) {
            searchMap.put("startDate", startDate);
        }
        if (endDate != null) {
            searchMap.put("endDate", endDate);
        }
        ResultList<NewAd> resultList = adminService.findAdList(searchMap, 1, Integer.MAX_VALUE, null, Boolean.FALSE);
        String[] headLine = new String[14];
        headLine[0] = "日期";
        headLine[1] = "类型";
        headLine[2] = "平台";
        headLine[3] = "姓名";
        headLine[4] = "广告类型";
        headLine[5] = "联系方式";
        headLine[6] = "期限";
        headLine[7] = "级别";
        headLine[8] = "金额";
        headLine[9] = "付款方式";
        headLine[10] = "返还";
        headLine[11] = "提成";
        headLine[12] = "备注";
        headLine[13] = "是否回收";
        vecCsvData.add(headLine);
        //sets the data to be exported
        for (NewAd log : resultList) {
            String[] strLine = new String[14];
            strLine[0] = Tools.formatDate(log.getPayDate(), "yyyy-MM-dd");
            if ("AD_DEPARTMENT".equals(log.getCategoryPlus())) {
                strLine[1] = log.getGoods().getCategoryMean() + "(广告部)";
            } else {
                strLine[1] = log.getGoods().getCategoryMean();
            }
            strLine[2] = log.getGoods().getName();
            strLine[3] = log.getUser().getName();
            strLine[4] = log.getName();
            strLine[5] = log.getOwnerWeChat() == null ? "" : log.getOwnerWeChat();
            strLine[6] = log.getLimitType() == null ? "" : log.getLimitType().getMean();
            strLine[7] = log.getAdLevel() == null ? "" : log.getAdLevel().getMean();
            strLine[8] = log.getAmount().toString();
            strLine[9] = log.getGatewayTypeStr();
            strLine[10] = log.getUserBalanceAmount().toString();
            strLine[11] = log.getUserAmount().toString();
            strLine[12] = log.getRemark() == null ? "" : log.getRemark();
            strLine[13] = log.getBacked() ? "被回收" : "";
            vecCsvData.add(strLine);
        }
        //Exporting vector to csv file
        StringBuilder sb = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                sb.append(strLine[j]);
                if (j < colNum - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(sb.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    private boolean loadWageLogList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "wage_log.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        Map searchMap = new HashMap();
        if (startDate != null) {
            searchMap.put("startDate", startDate);
        }
        if (endDate != null) {
            searchMap.put("endDate", endDate);
        }
        CategoryEnum category = CategoryEnum.valueOf(super.getRequestString(request, "category"));
        searchMap.put("category", category);
        searchMap.put("type", WageLogTypeEnum.RECOMMEND);
        ResultList<WageLog> resultList = adminService.findWageLogList(searchMap, 1, Integer.MAX_VALUE, null, Boolean.FALSE);
        adminService.setGoodsNamesWageLog(resultList);
        String[] headLine = new String[10];
        headLine[0] = "日期";
        headLine[1] = "类型";
        headLine[2] = "平台";
        headLine[3] = "业务员";
        headLine[4] = "金额";
        headLine[5] = "手续费";
        headLine[6] = "付款方式";
        headLine[7] = "业务员平台";
        headLine[8] = "平台已付款";
        headLine[9] = "是否回收";
        vecCsvData.add(headLine);
        //sets the data to be exported
        for (WageLog log : resultList) {
            String[] strLine = new String[10];
            strLine[0] = Tools.formatDate(log.getPayDate(), "yyyy-MM-dd");
            strLine[1] = log.getGoodsOrder().getGoods().getCategoryMean();
            strLine[2] = log.getGoodsOrder().getGoods().getName();
            strLine[3] = log.getUser().getName();
            strLine[4] = log.getAmount().toString();
            strLine[5] = log.getFee().toString();
            strLine[6] = log.getGoodsOrder().getGatewayType().getMean();
            strLine[7] = log.getGoodsNames();
            strLine[8] = log.getGoodsOrder().getPaidPrice().toString();
            strLine[9] = log.getBacked() ? "被回收" : "";
            vecCsvData.add(strLine);
        }
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }
    
    
            

    /**
     * 订单详情
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadOrderRecordList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "order_record_list.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        Map searchMap = new HashMap();
        if (startDate != null) {
            searchMap.put("startDate", startDate);
        }
        if (endDate != null) {
            searchMap.put("endDate", endDate);
        }
        CategoryEnum category = CategoryEnum.valueOf(super.getRequestString(request, "category"));
        searchMap.put("category", category);
        ResultList<OrderRecord> resultList = adminService.findOrderRecordList(searchMap, 1, Integer.MAX_VALUE, null, Boolean.FALSE);
        String[] headLine = new String[12];
        headLine[0] = "日期";
        headLine[1] = "平台类型";
        headLine[2] = "推荐人";//推荐人
        headLine[3] = "姓名";//姓名
//        headLine[3] = "代理";
        headLine[4] = "平台";
        headLine[5] = "金额";
        headLine[6] = "付款方式";
        headLine[7] = "类型";
//        headLine[7] = "订单目前状态";
        headLine[8] = "应提成";//应提成
        headLine[9] = "手续费";//手续费
        headLine[10] = "实际提成";
        headLine[11] = "备注";

        vecCsvData.add(headLine);
        //sets the data to be exported
        for (OrderRecord record : resultList) {
            if (record.getOrder() == null || record.getDeleted() || record.getOrder().getDeleted()) {
                continue;
            }
            String[] strLine = new String[12];
            strLine[0] = Tools.formatDate(record.getPayDate(), "yyyy-MM-dd");
            strLine[1] = record.getGoods().getCategoryMean();
            strLine[2] = adminService.getGoodsOrderRecommendOrderNames(record.getOrder());
            strLine[3] = record.getOrder().getRecommendNames() == null ? "" : record.getOrder().getRecommendNames();
            strLine[4] = record.getGoods().getName();
//            strLine[3] = record.getOrder().getAgentUser() == null ? "还未填写" : record.getOrder().getAgentUser().getName();
            strLine[5] = record.getPrice().toString();
            strLine[6] = record.getGatewayTypeStr();
            strLine[7] = record.getTypeMean();
//            strLine[7] = record.getGoods().getStatusMean();

            if (record.getType().equals(OrderRecordTypeEnum.FINAL_PAYMENT)) {
                List<WageLog> logList = adminService.getWageLogByGoodsOrder(record.getOrder());
                if (logList == null) {
                    strLine[8] = "0";
                    strLine[9] = "0";
                    strLine[10] = Double.parseDouble(strLine[8]) - Double.parseDouble(strLine[9]) + "";
                } else {
                    strLine[8] = "";
                    strLine[9] = "";
                    int i = 1;
                    Double fee = 0.00d;
                    for (WageLog log : logList) {
                        if (i > 1) {
                            strLine[8] += ";";
                            strLine[9] += ";";
                            strLine[10] = ";";
                        }
                        strLine[8] += log.getAmount().toString();
                        strLine[9] += log.getFee().toString();
                        fee += log.getAmount().subtract(log.getFee()).doubleValue();
                        i++;
                    }
                    strLine[10] = fee + "";
                }
            } else {
                strLine[8] = "0";
                strLine[9] = "0";
                strLine[10] = Double.parseDouble(strLine[8]) - Double.parseDouble(strLine[9]) + "";
            }
            strLine[11] = record.getOrder().getRemark() == null ? "" : record.getOrder().getRemark();
            vecCsvData.add(strLine);
        }
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 合同
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadContractList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "contract_list.csv";//default file name
        List vecCsvData = new ArrayList();
//        String start = super.getRequestString(request, "startDate");
//        String end = super.getRequestString(request, "endDate");
//        Date startDate = null;
//        Date endDate = null;
//        if (Tools.isNotBlank(start)) {
//            try {
//                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
//            } catch (Exception e) {
//                startDate = null;
//            }
//        }
//        if (Tools.isNotBlank(end)) {
//            try {
//                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
//            } catch (Exception e) {
//                endDate = null;
//            }
//        }
        Map searchMap = new HashMap();
//        if (startDate != null) {
//            searchMap.put("startDate", startDate);
//        }
//        if (endDate != null) {
//            searchMap.put("endDate", endDate);
//        }
        CategoryEnum category = CategoryEnum.valueOf(super.getRequestString(request, "category"));
        searchMap.put("category", category);
        searchMap.put("status", OrderStatusEnum.SUCCESS);
        ResultList<GoodsOrder> resultList = adminService.findOrderList(searchMap, 0, 0, true, Boolean.FALSE);
        String[] headLine = new String[15];
        headLine[0] = "日期";
        headLine[1] = "序列号";
        headLine[2] = "合同编号";
        headLine[3] = "平台";
        headLine[4] = "代理";
        headLine[5] = "平台类型";
        headLine[6] = "加盟费";
        headLine[7] = "签约时间";
        headLine[8] = "结束时间";
        headLine[9] = "联系方式";
        headLine[10] = "代理人身份证";
        headLine[11] = "银行卡类型";
        headLine[12] = "银行卡号码";
        headLine[13] = "人数";
        headLine[14] = "备注";
        vecCsvData.add(headLine);
        //sets the data to be exported
        for (GoodsOrder order : resultList) {
            String[] strLine = new String[15];
            strLine[0] = Tools.formatDate(order.getLastPayDate(), "yyyy-MM-dd");
            strLine[1] = order.getSerialId();
            strLine[2] = order.getContractSerialId() == null ? "" : order.getContractSerialId();
            strLine[3] = order.getGoods().getName();
            strLine[4] = order.getAgentUser() == null ? "还未填写" : order.getAgentUser().getName();
            strLine[5] = order.getGoods().getCategoryMean();
            strLine[6] = order.getPaidPrice().toString();
            strLine[7] = Tools.formatDate(order.getLimitStart(), "yyyy-MM-dd");
            strLine[8] = Tools.formatDate(order.getLimitEnd(), "yyyy-MM-dd");
            strLine[9] = order.getAgentUser().getMobileOutput();
            strLine[10] = order.getAgentUser().getIdCardOutput();
            strLine[11] = order.getAgentUser().getBankType() != null ? order.getAgentUser().getBankType().replaceAll(",", " ") : "";
            strLine[12] = order.getAgentUser().getBankCardCodeOutput();
            strLine[13] = order.getGoods().getPeopleCount() == null ? "0" : order.getGoods().getPeopleCount() + "";
            strLine[14] = order.getRemark() == null ? "" : order.getRemark();
            vecCsvData.add(strLine);
        }
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 广告工资
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadWageList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "wage.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        CategoryEnum category = CategoryEnum.valueOf(super.getRequestString(request, "category"));
        List<String[]> resultList = null;
        if (category.equals(CategoryEnum.SERVICE_PEOPLE)) {
            resultList = adminService.findWageList(startDate, endDate);
        } else if (category.equals(CategoryEnum.MAKE_FRIENDS)) {
            resultList = adminService.findWageListMF(startDate, endDate);
        }
        String[] headLine = new String[10];
        headLine[0] = "用户";
        headLine[1] = "余额";
        headLine[2] = "总押金";
        headLine[3] = "银行类型";
        headLine[4] = "银行卡号";
        headLine[5] = "提成";
        headLine[6] = "返还";
        headLine[7] = "广告总工资";
        headLine[8] = "代理的平台";
        headLine[9] = "含有回收";
        vecCsvData.add(headLine);
        //sets the data to be exported
        for (String[] s : resultList) {
            vecCsvData.add(s);
        }
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 代理的总工资
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadAllWageList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "wage_all.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        List<String[]> resultList = adminService.findWageTotalListAll(startDate, endDate);
        String[] headLine = new String[11];
        headLine[0] = "用户";
        headLine[1] = "便民余额";
        headLine[2] = "便民押金";
//        headLine[3] = "交友余额";
//        headLine[4] = "交友押金";
        headLine[3] = "银行类型";
        headLine[4] = "银行卡号";
        headLine[5] = "广告工资";
        headLine[6] = "加盟提成工资";
        headLine[7] = "产品工资";
        headLine[8] = "总工资";
        headLine[9] = "代理的平台";
        headLine[10] = "含有回收";
        vecCsvData.add(headLine);
        //sets the data to be exported
        vecCsvData.addAll(resultList);
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 大区工资表
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadUserWageLogTotalList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "user_wage_log_total_list.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        CategoryEnum category = CategoryEnum.valueOf(super.getRequestString(request, "category"));
        List<String[]> resultList = adminService.findUserWageLogTotalList(startDate, endDate, category);
        String[] headLine = new String[4];
        headLine[0] = "姓名";
        headLine[1] = "提成";
        headLine[2] = "平台数量";
        headLine[3] = "含有回收";
        vecCsvData.add(headLine);
        //sets the data to be exported
        for (String[] s : resultList) {
            vecCsvData.add(s);
        }
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 大区工资表明细
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadUserWageLogList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "user_wage_log_list.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        CategoryEnum category = CategoryEnum.valueOf(super.getRequestString(request, "category"));
        List<String[]> resultList = adminService.findUserWageLogList(startDate, endDate, category);
        String[] headLine = new String[11];
        headLine[0] = "日期";
        headLine[1] = "姓名";
        headLine[2] = "类型";
        headLine[3] = "平台";
        headLine[4] = "订单号";
        headLine[5] = "提成";
        headLine[6] = "是否回收";
        headLine[7] = "推荐人";
        headLine[8] = "推荐人代理地区";
        headLine[9] = "支付方式";
        headLine[10] = "价钱";
        vecCsvData.add(headLine);
        //sets the data to be exported
        for (String[] s : resultList) {
            vecCsvData.add(s);
        }
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 代理工资
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadOrderWageList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "order_wage.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        CategoryEnum category = CategoryEnum.valueOf(super.getRequestString(request, "category"));
        List<String[]> resultList = null;
        if (category.equals(CategoryEnum.SERVICE_PEOPLE)) {
            resultList = adminService.findOrderWageList(startDate, endDate);
        } else if (category.equals(CategoryEnum.MAKE_FRIENDS)) {
            resultList = adminService.findOrderWageListMf(startDate, endDate);
        }
        String[] headLine = new String[8];
        headLine[0] = "用户";
        headLine[1] = "余额";
        headLine[2] = "总押金";
        headLine[3] = "银行类型";
        headLine[4] = "银行卡号";
        headLine[5] = "工资";
        headLine[6] = "含有回收";
        headLine[7] = "代理的平台";
        vecCsvData.add(headLine);
        //sets the data to be exported
        for (String[] s : resultList) {
            vecCsvData.add(s);
        }
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 代理工资总计
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadWageTotalList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "user_wage.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        CategoryEnum category = CategoryEnum.valueOf(super.getRequestString(request, "category"));
        List<String[]> resultList = null;
        if (category.equals(CategoryEnum.SERVICE_PEOPLE)) {
            resultList = adminService.findWageTotalList(startDate, endDate);
        } else if (category.equals(CategoryEnum.MAKE_FRIENDS)) {
            resultList = adminService.findWageTotalListMf(startDate, endDate);
        }
        String[] headLine = new String[10];
        headLine[0] = "用户";
        headLine[1] = "余额";
        headLine[2] = "总押金";
        headLine[3] = "银行类型";
        headLine[4] = "银行卡号";
        headLine[5] = "广告工资";
        headLine[6] = "加盟提成工资";
        headLine[7] = "总工资";
        headLine[8] = "代理的平台";
        headLine[9] = "含有回收";
        vecCsvData.add(headLine);
        //sets the data to be exported
        for (String[] s : resultList) {
            vecCsvData.add(s);
        }
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 下载产品列表对账表
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadProductLogList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "product_log.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        Map searchMap = new HashMap();
        searchMap.put("startDate", startDate);
        searchMap.put("endDate", endDate);
        searchMap.put("type", WageLogTypeEnum.PRODUCT);
        List<WageLog> resultList = adminService.findWageLogList(searchMap, 0, 0, true, false);
        String[] headLine = new String[7];
        headLine[0] = "时间";
        headLine[1] = "产品";
        headLine[2] = "价格";
        headLine[3] = "数量";
        headLine[4] = "购买平台";
        headLine[5] = "提成";
        headLine[6] = "备注";
        vecCsvData.add(headLine);
        //sets the data to be exported
        for (WageLog log : resultList) {
            ProductLog productLog = log.getProductLog();
            String[] s = new String[7];
            s[0] = Tools.formatDate(log.getPayDate(), "yyyy-MM-dd");
            s[1] = productLog.getProductStr();
            s[2] = productLog.getIncomeAmount().toString();
            s[3] = productLog.getSoldCount() + "";
            s[4] = productLog.getGoods().getName();
            s[5] = log.getAmount().toString();//对账用这个
            s[6] = productLog.getRemark() == null ? "" : productLog.getRemark();
            vecCsvData.add(s);
        }
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }

    /**
     * 下载产品列表工资表
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws NoSessionException
     */
    private boolean loadProductLogWageList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSessionException {
        try {
            SysUser user = adminService.getUserByLoginCode(super.getCookieValue(request, response, "auth"));
        } catch (AccountNotExistException | EjbMessageException ex) {
            return FORWARD_TO_ANOTHER_URL;
        }
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String fileName = "product_log_wage.csv";//default file name
        List vecCsvData = new ArrayList();
        String start = super.getRequestString(request, "startDate");
        String end = super.getRequestString(request, "endDate");
        Date startDate = null;
        Date endDate = null;
        if (Tools.isNotBlank(start)) {
            try {
                startDate = Tools.getBeginOfDay(Tools.parseDate(start, "yyyy-MM-dd"));
            } catch (Exception e) {
                startDate = null;
            }
        }
        if (Tools.isNotBlank(end)) {
            try {
                endDate = Tools.getEndOfDay(Tools.parseDate(end, "yyyy-MM-dd"));
            } catch (Exception e) {
                endDate = null;
            }
        }
        List<String[]> resultList = adminService.findWageLogProductList(startDate, endDate);
        String[] headLine = new String[8];
        headLine[0] = "用户";
        headLine[1] = "平台";
        headLine[2] = "银行";
        headLine[3] = "卡号";
        headLine[4] = "提成";
        headLine[5] = "交易次数";
        headLine[6] = "产品数量";
        headLine[7] = "代理全部平台";
        vecCsvData.add(headLine);
        //sets the data to be exported
        vecCsvData.addAll(resultList);
        //Exporting vector to csv file
        StringBuilder strOut = new StringBuilder();
        for (Object vecCsvData1 : vecCsvData) {
            String[] strLine = (String[]) vecCsvData1;
            int colNum = strLine.length;
            for (int j = 0; j < colNum; j++) {
                strOut.append(strLine[j]);
                if (j < colNum - 1) {
                    strOut.append(",");
                }
            }
            strOut.append("\n");
        }
        //***** Output strOut to Response ******
        response.reset();// Reset the response
        response.setContentType("application/octet-stream;charset=GB2312");// the encoding of this example is GB2312 
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.write(strOut.toString());
        } catch (IOException e) {
//            e.printStackTrace();
        }
        //***************************************
        return FORWARD_TO_ANOTHER_URL;
    }
    // ************************************************************************
    // *************** PAGE RANDER处理的相关函数，放在这下面
    // ************************************************************************
    //*********************************************************************
}
