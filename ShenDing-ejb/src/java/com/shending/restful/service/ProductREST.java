package com.shending.restful.service;

import com.shending.entity.*;
import com.shending.restful.interception.AccountInterceptor;
import com.shending.service.AdminService;
import com.shending.service.ConfigService;
import com.shending.service.ProductService;
import com.shending.support.*;
import com.shending.support.enums.*;
import com.shending.support.exception.EjbMessageException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 后台管理
 *
 * @author yin.weilong
 */
@Stateless
@Path("product")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
@Interceptors(AccountInterceptor.class)
public class ProductREST {

    @EJB
    private AdminService adminService;
    @EJB
    private ConfigService configService;
    @EJB
    private ProductService productService;

    @PersistenceContext(unitName = "ShenDing-PU")
    private EntityManager em;

    /**
     * 导入产品
     *
     * @param servletRequest
     * @return
     * @throws Exception
     */
    @POST
    @Path("upload_file_product")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String importProduct(@CookieParam("auth") String auth, @Context HttpServletRequest servletRequest) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        FileUploadObj fileUploadObj = null;
        Map map = Tools.getDMap();
        try {
            fileUploadObj = Tools.uploadFile(servletRequest, 100, null, null, null);
        } catch (FileUploadException e) {
            map.put("msg", e.getMessage());
            return Tools.caseObjectToJson(map);
        }
        for (FileUploadItem item : fileUploadObj.getFileList()) {
            if ("file1".equals(item.getFieldName())) {
                File file = new File(item.getUploadFullPath());
                jxl.Workbook readwb = null;
                try {
                    //构建Workbook对象, 只读Workbook对象   
                    //直接从本地文件创建Workbook   
                    InputStream instream = new FileInputStream(item.getUploadFullPath());
                    readwb = Workbook.getWorkbook(instream);
                    //Sheet的下标是从0开始   
                    //获取第一张Sheet表   
                    Sheet readsheet = readwb.getSheet(0);
                    //获取Sheet表中所包含的总行数   
                    int rsRows = readsheet.getRows();
                    //到款时间
                    for (int i = 1; i < rsRows; i++) {
                        Cell[] cells = readsheet.getRow(i);
                        String payDateStr = StringUtils.trim(cells[0].getContents());
                        Date payDate = null;
                        try {
                            payDate = Tools.parseDate(payDateStr, "yyyy-MM-dd");
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行到款时间格式错误");
                        }
                        if (payDate == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行到款时间格式错误");
                        }
                        //分成代理
                        String placeName = StringUtils.trim(cells[1].getContents());
                        Map searchMap = new HashMap();
                        searchMap.put("category", CategoryEnum.SERVICE_PEOPLE);
                        searchMap.put("placeName", placeName);
                        searchMap.put("status", OrderStatusEnum.SUCCESS);
                        ResultList<GoodsOrder> list = adminService.findOrderList(searchMap, 1, 10, null, Boolean.TRUE);
                        if (list.size() != 1) {
                            throw new EjbMessageException("第" + (i + 1) + "行分成代理无法唯一定位，请手动录入该条");
                        }
                        GoodsOrder order = list.get(0);
                        //产品
//                        ProductEnum productEnum = ProductEnum.getEnum();
                        String productEnum = StringUtils.trim(cells[2].getContents());
                        String product = null;
                        try {
                            TypedQuery<ProductTypeConfig> queryTotal = em.createQuery("SELECT p FROM ProductTypeConfig p WHERE p.type = :type and p.name = :name", ProductTypeConfig.class);
                            queryTotal.setParameter("type", 1).setParameter("name", productEnum);
                            ProductTypeConfig configList = queryTotal.getSingleResult();
                            product = configList.getKey();
                        } catch (Exception e) {
                            product = null;
                        }

                        if (product == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行产品错误");
                        }
                        //数量
                        int count = 0;
                        try {
                            count = Integer.parseInt(StringUtils.trimToNull(cells[3].getContents()));
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行数量错误");
                        }
                        //价格
                        String amount = StringUtils.trimToNull(cells[4].getContents());
                        BigDecimal amountBd = null;
                        if (amount == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行价格错误");
                        }
                        try {
                            amountBd = new BigDecimal(amount);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行价格错误");
                        }
                        //提成
                        String commission = StringUtils.trimToNull(cells[5].getContents());
                        BigDecimal commissionBd = null;
                        if (commission == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行提成错误");
                        }
                        try {
                            commissionBd = new BigDecimal(commission);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行提成错误");
                        }
                        //分成大区经理
                        Long regionalManager = null;
                        String regionalManagerName = StringUtils.trimToNull(cells[6].getContents());
                        BigDecimal regionalManagerAmount = null;
                        if (regionalManagerName != null) {
                            searchMap.clear();
                            searchMap.put("name", regionalManagerName);
                            searchMap.put("notStatus", SysUserStatus.PEDING);
                            searchMap.put("adminType", SysUserTypeEnum.ADMIN);
                            searchMap.put("roleIdIsNotNull", true);
                            ResultList<SysUser> userList = adminService.findUserList(searchMap, 1, 10, null, Boolean.TRUE);
                            if (userList.size() != 1) {
                                throw new EjbMessageException("第" + (i + 1) + "行分成大区经理无法唯一定位，请手动录入该条");
                            }
                            regionalManager = userList.get(0).getId();

                            //大区经理提成
                            regionalManagerAmount = BigDecimal.ZERO;
                            String regionalManagerAmountStr = StringUtils.trimToNull(cells[7].getContents());
                            if (regionalManagerAmountStr != null) {
                                try {
                                    regionalManagerAmount = new BigDecimal(regionalManagerAmountStr);
                                } catch (Exception e) {
                                    throw new EjbMessageException("第" + (i + 1) + "行提成错误");
                                }
                            }
                        }
                        //备注
                        String remark = StringUtils.trimToNull(cells[8].getContents());
                        //支付方式
                        PaymentGatewayTypeEnum gatewayType = PaymentGatewayTypeEnum.getEnum(StringUtils.trim(cells[9].getContents()));
                        if (gatewayType == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行支付方式错误");
                        }
                        adminService.createOrUpdateProductLog(null, order.getId(), amountBd, commissionBd, payDate, product, remark, count, regionalManager, regionalManagerAmount, gatewayType);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    map.put("success", "0");
                    map.put("msg", e.getMessage());
                    return Tools.caseObjectToJson(map);
                } finally {
                    readwb.close();
                }
                FileUtils.deleteQuietly(file);
                map.put("msg", "上传成功");
                map.put("success", "1");
                map.put("data", "");
                return Tools.caseObjectToJson(map);
            }
        }
        map.put("msg", "未找到合法数据");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 导入化妆品
     *
     * @param servletRequest
     * @return
     * @throws Exception
     */
    @POST
    @Path("upload_file_cosmetics")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String importCosmetics(@CookieParam("auth") String auth, @Context HttpServletRequest servletRequest) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        FileUploadObj fileUploadObj = null;
        Map map = Tools.getDMap();
        try {
            fileUploadObj = Tools.uploadFile(servletRequest, 100, null, null, null);
        } catch (FileUploadException e) {
            map.put("msg", e.getMessage());
            return Tools.caseObjectToJson(map);
        }
        for (FileUploadItem item : fileUploadObj.getFileList()) {
            if ("file1".equals(item.getFieldName())) {
                File file = new File(item.getUploadFullPath());
                jxl.Workbook readwb = null;
                try {
                    //构建Workbook对象, 只读Workbook对象   
                    //直接从本地文件创建Workbook   
                    InputStream instream = new FileInputStream(item.getUploadFullPath());
                    readwb = Workbook.getWorkbook(instream);
                    //Sheet的下标是从0开始   
                    //获取第一张Sheet表   
                    Sheet readsheet = readwb.getSheet(0);
                    //获取Sheet表中所包含的总行数   
                    int rsRows = readsheet.getRows();
                    //到款时间
                    for (int i = 1; i < rsRows; i++) {
                        Cell[] cells = readsheet.getRow(i);
                        String payDateStr = StringUtils.trim(cells[0].getContents());
                        Date payDate = null;
                        try {
                            payDate = Tools.parseDate(payDateStr, "yyyy-MM-dd");
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行到款时间格式错误");
                        }
                        if (payDate == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行到款时间格式错误");
                        }
                        //分成代理
                        String placeName = StringUtils.trim(cells[1].getContents());
                        Map searchMap = new HashMap();
                        searchMap.put("category", CategoryEnum.SERVICE_PEOPLE);
                        searchMap.put("placeName", placeName);
                        searchMap.put("status", OrderStatusEnum.SUCCESS);
                        ResultList<GoodsOrder> list = adminService.findOrderList(searchMap, 1, 10, null, Boolean.TRUE);
                        if (list.size() != 1) {
                            throw new EjbMessageException("第" + (i + 1) + "行分成代理无法唯一定位，请手动录入该条");
                        }
                        GoodsOrder order = list.get(0);
                        //产品
                        String productName = StringUtils.trim(cells[2].getContents());
                        Integer product = null;
                        try {
                            TypedQuery<ProductTypeConfig> queryTotal = em.createQuery("SELECT p FROM ProductTypeConfig p WHERE p.type = :type and p.name = :name", ProductTypeConfig.class);
                            queryTotal.setParameter("type", 2).setParameter("name", productName);
                            ProductTypeConfig configList = queryTotal.getSingleResult();
                            product = Integer.parseInt(configList.getKey());
                        } catch (Exception e) {
                            product = null;
                        }

//                        if ("清颜原液".equals(productName)) {
//                            product = 1;
//                        } else if ("清滢柔肤洁面乳".equals(productName)) {
//                            product = 2;
//
//                        } else if ("舒缓清润精华液".equals(productName)) {
//                            product = 3;
//                        } else if ("馥活提亮精华液".equals(productName)) {
//                            product = 4;
//                        } else if ("多效蚕丝面膜".equals(productName)) {
//                            product = 5;
//                        } else if ("冻干粉修护套".equals(productName)) {
//                            product = 6;
//                        } else if ("冻干粉嫩肤套".equals(productName)) {
//                            product = 7;
//                        } else if ("冻干粉保湿套".equals(productName)) {
//                            product = 8;
//                        } else if ("推荐奖励".equals(productName)) {
//                            product = 9;
//                        } else if ("无".equals(productName)) {
//                            product = 10;
//                        }
                        if (product == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行产品错误");
                        }
                        //数量
                        int count = 0;
                        try {
                            count = Integer.parseInt(StringUtils.trimToNull(cells[3].getContents()));
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行数量错误");
                        }
                        //价格
                        String amount = StringUtils.trimToNull(cells[4].getContents());
                        BigDecimal amountBd = null;
                        if (amount == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行价格错误");
                        }
                        try {
                            amountBd = new BigDecimal(amount);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行价格错误");
                        }
                        //提成
                        String commission = StringUtils.trimToNull(cells[5].getContents());
                        BigDecimal commissionBd = null;
                        if (commission == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行提成错误");
                        }
                        try {
                            commissionBd = new BigDecimal(commission);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行提成错误");
                        }
                        //分成大区经理
                        Long regionalManager = null;
                        String regionalManagerName = StringUtils.trimToNull(cells[6].getContents());
                        BigDecimal regionalManagerAmount = null;
                        if (regionalManagerName != null) {
                            searchMap.clear();
                            searchMap.put("name", regionalManagerName);
                            searchMap.put("notStatus", SysUserStatus.PEDING);
                            searchMap.put("adminType", SysUserTypeEnum.ADMIN);
                            searchMap.put("roleIdIsNotNull", true);
                            ResultList<SysUser> userList = adminService.findUserList(searchMap, 1, 10, null, Boolean.TRUE);
                            if (userList.size() != 1) {
                                throw new EjbMessageException("第" + (i + 1) + "行分成大区经理无法唯一定位，请手动录入该条");
                            }
                            regionalManager = userList.get(0).getId();
                            //大区经理提成
                            regionalManagerAmount = BigDecimal.ZERO;
                            String regionalManagerAmountStr = StringUtils.trimToNull(cells[7].getContents());
                            if (regionalManagerAmountStr != null) {
                                try {
                                    regionalManagerAmount = new BigDecimal(regionalManagerAmountStr);
                                } catch (Exception e) {
                                    throw new EjbMessageException("第" + (i + 1) + "行提成错误");
                                }
                            }
                        }

                        //备注
                        String remark = StringUtils.trimToNull(cells[8].getContents());
                        //支付方式
                        PaymentGatewayTypeEnum gatewayType = PaymentGatewayTypeEnum.getEnum(StringUtils.trim(cells[9].getContents()));
                        if (gatewayType == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行支付方式错误");
                        }
                        adminService.createOrUpdateCosmetics(null, order.getId(), amountBd, commissionBd, payDate, product, remark, count, regionalManager, regionalManagerAmount, gatewayType);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    map.put("success", "0");
                    map.put("msg", e.getMessage());
                    return Tools.caseObjectToJson(map);
                } finally {
                    readwb.close();
                }
                FileUtils.deleteQuietly(file);
                map.put("msg", "上传成功");
                map.put("success", "1");
                map.put("data", "");
                return Tools.caseObjectToJson(map);
            }
        }
        map.put("msg", "未找到合法数据");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除产品
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_product")
    public String delProduct(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        for (Long id : ids) {
            ProductLog productLog = em.find(ProductLog.class, id);
            productLog.setDeleted(true);
            em.merge(productLog);
            WageLog wageLog = adminService.findWageLogByProduct(productLog);
            wageLog.setDeleted(Boolean.TRUE);
            em.merge(wageLog);
        }
        map.put("msg", "删除成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建/更新产品
     *
     * @param auth
     * @param id
     * @param soldCount
     * @param incomeAmount
     * @param commissionAmount
     * @param product
     * @param goodsOrderId
     * @param remark
     * @param payDate
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_or_update_product")
    public String createOrUpdateProduct(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("soldCount") int soldCount, @FormParam("incomeAmount") String incomeAmount, @FormParam("commissionAmount") String commissionAmount,
            @FormParam("product") String product, @FormParam("goodsOrderId") Long goodsOrderId, @FormParam("remark") String remark, @FormParam("payDate") String payDate, @FormParam("regionalManager") Long regionalManager, @FormParam("regionalManagerAmount") String regionalManagerAmount, @FormParam("payType") String payType) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        Date payDateTime = null;
        try {
            payDateTime = Tools.parseDate(payDate, "yyyy-MM-dd");
        } catch (Exception e) {
            payDateTime = Tools.getBeginOfDay(new Date());
        }
        if (new BigDecimal(incomeAmount).compareTo(BigDecimal.ZERO) < 0 || new BigDecimal(commissionAmount).compareTo(BigDecimal.ZERO) < 0) {
            throw new EjbMessageException("参数异常");
        }
        if (regionalManager == null) {
            regionalManagerAmount = "0";
        }
        adminService.createOrUpdateProductLog(id, goodsOrderId, new BigDecimal(incomeAmount), new BigDecimal(commissionAmount), payDateTime, product, remark, soldCount, regionalManager, new BigDecimal(regionalManagerAmount), PaymentGatewayTypeEnum.valueOf(StringUtils.trim(payType)));
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取产品列表
     *
     * @param auth
     * @param search
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("product_list")
    public String getProductList(@CookieParam("auth") String auth, @QueryParam("search") String search, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        String category = null;
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(search)) {
            String[] searchs = search.split(" ");
            for (String str : searchs) {
                if ("便民".equals(str)) {
                    category = "SERVICE_PEOPLE";
                } else if ("交友".equals(str)) {
                    category = "MAKE_FRIENDS";
                }
            }
            search = searchs[0];
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(category)) {
            searchMap.put("category", CategoryEnum.valueOf(category));
        }
        if (Tools.isNotBlank(start)) {
            searchMap.put("startDate", Tools.parseDate(start, "yyy-MM-dd"));
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount() && ((Date) searchMap.get("startDate")).before(Tools.getBeginOfYear(new Date()))) {
                throw new EjbMessageException("只能查询今年的数据");
            }
        } else {
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount()) {
                searchMap.put("startDate", Tools.getBeginOfYear(new Date()));
            }
        }
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.getEndOfDay(Tools.parseDate(end, "yyy-MM-dd")));
        }
        ResultList<ProductLog> list = adminService.findProductLogList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取产品信息
     *
     * @param auth
     * @param id
     * @return
     * @throws Exception
     */
    @GET
    @Path("product_info")
    public String getProductInfo(@CookieParam("auth") String auth, @QueryParam("id") Long id) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        ProductLog productLog = em.find(ProductLog.class, id);
        map.put("data", productLog);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建/更新大满贯
     *
     * @param auth
     * @param id
     * @param amount
     * @param goodsOrderId
     * @param remark
     * @param payDate
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_or_update_grand_slam")
    public String createOrUpdateGrandSlam(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("amount") String amount, @FormParam("goodsOrderId") Long goodsOrderId, @FormParam("remark") String remark, @FormParam("payDate") String payDate) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        Date payDateTime = null;
        try {
            payDateTime = Tools.parseDate(payDate, "yyyy-MM-dd");
        } catch (Exception e) {
            payDateTime = Tools.getBeginOfDay(new Date());
        }
        if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new EjbMessageException("参数异常");
        }
        adminService.createOrUpdateGrandSlam(id, goodsOrderId, new BigDecimal(amount), payDateTime, remark);
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取大满贯列表
     *
     * @param auth
     * @param search
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("grand_slam_list")
    public String getGrandSlamList(@CookieParam("auth") String auth, @QueryParam("search") String search, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        String category = null;
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(search)) {
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(start)) {
            searchMap.put("startDate", Tools.parseDate(start, "yyy-MM-dd"));
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount() && ((Date) searchMap.get("startDate")).before(Tools.getBeginOfYear(new Date()))) {
                throw new EjbMessageException("只能查询今年的数据");
            }
        } else {
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount()) {
                searchMap.put("startDate", Tools.getBeginOfYear(new Date()));
            }
        }
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.getEndOfDay(Tools.parseDate(end, "yyy-MM-dd")));
        }
        ResultList<ProductGrandSlam> list = adminService.findProductGrandSlamList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取大满贯信息
     *
     * @param auth
     * @param id
     * @return
     * @throws Exception
     */
    @GET
    @Path("grand_slam_info")
    public String getProductGrandSlamInfo(@CookieParam("auth") String auth, @QueryParam("id") Long id) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        ProductGrandSlam productGrandSlam = em.find(ProductGrandSlam.class, id);
        map.put("data", productGrandSlam);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除产品
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_grand_slam")
    public String delGrandSlam(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        for (Long id : ids) {
            ProductGrandSlam productGrandSlam = em.find(ProductGrandSlam.class, id);
            productGrandSlam.setDeleted(true);
            em.merge(productGrandSlam);
            WageLog wageLog = adminService.findWageLogByProductGrandSlam(productGrandSlam);
            wageLog.setDeleted(Boolean.TRUE);
            em.merge(wageLog);
        }
        map.put("msg", "删除成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建/更新大满贯
     *
     * @param auth
     * @param id
     * @param amount
     * @param goodsOrderId
     * @param remark
     * @param payDate
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_or_update_min_sheng_bank")
    public String createOrUpdateMinShengBank(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("amount") String amount, @FormParam("goodsOrderId") Long goodsOrderId, @FormParam("remark") String remark, @FormParam("payDate") String payDate) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        Date payDateTime = null;
        try {
            payDateTime = Tools.parseDate(payDate, "yyyy-MM-dd");
        } catch (Exception e) {
            payDateTime = Tools.getBeginOfDay(new Date());
        }
        if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new EjbMessageException("参数异常");
        }
        adminService.createOrUpdateMinShengBank(id, goodsOrderId, new BigDecimal(amount), payDateTime, remark);
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 导入民生銀行
     *
     * @param servletRequest
     * @return
     * @throws Exception
     */
    @POST
    @Path("upload_file_min_sheng")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String uploadFileMinShengBank(@CookieParam("auth") String auth, @Context HttpServletRequest servletRequest) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        FileUploadObj fileUploadObj = null;
        Map map = Tools.getDMap();
        try {
            fileUploadObj = Tools.uploadFile(servletRequest, 100, null, null, null);
        } catch (FileUploadException e) {
            map.put("msg", e.getMessage());
            return Tools.caseObjectToJson(map);
        }
        for (FileUploadItem item : fileUploadObj.getFileList()) {
            if ("file1".equals(item.getFieldName())) {
                File file = new File(item.getUploadFullPath());
                jxl.Workbook readwb = null;
                try {
                    //构建Workbook对象, 只读Workbook对象   
                    //直接从本地文件创建Workbook   
                    InputStream instream = new FileInputStream(item.getUploadFullPath());
                    readwb = Workbook.getWorkbook(instream);
                    //Sheet的下标是从0开始   
                    //获取第一张Sheet表   
                    Sheet readsheet = readwb.getSheet(0);
                    //获取Sheet表中所包含的总行数   
                    int rsRows = readsheet.getRows();
                    //入账时间
                    for (int i = 1; i < rsRows; i++) {
                        Cell[] cells = readsheet.getRow(i);
                        String payDateStr = StringUtils.trim(cells[0].getContents());
                        Date payDate = null;
                        try {
                            payDate = Tools.parseDate(payDateStr, "yyyy-MM-dd");
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行入账时间格式错误");
                        }
                        if (payDate == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行入账时间格式错误");
                        }
                        //获得代理
                        String placeName = StringUtils.trim(cells[1].getContents());
                        Map searchMap = new HashMap();
                        searchMap.put("category", CategoryEnum.SERVICE_PEOPLE);
                        searchMap.put("placeName", placeName);
                        searchMap.put("status", OrderStatusEnum.SUCCESS);
                        ResultList<GoodsOrder> orderList = adminService.findOrderList(searchMap, 1, 10, Boolean.TRUE, Boolean.FALSE);
                        if (orderList.size() != 1) {
                            throw new EjbMessageException("第" + (i + 1) + "获得代理无法唯一定位，请手动录入该条");
                        }
                        Long goodsOrderId = orderList.get(0).getId();
                        //金额
                        String amount = StringUtils.trimToNull(cells[2].getContents());
                        BigDecimal amountBd = null;
                        if (amount == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行金额错误");
                        }
                        try {
                            amountBd = new BigDecimal(amount);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行金额错误");
                        }
                        //备注
                        String remark = null;
                        if (cells.length > 3) {
                            remark = StringUtils.trimToNull(cells[3].getContents());
                        }

                        try {
                            adminService.createOrUpdateMinShengBank(null, goodsOrderId, amountBd, payDate, remark);
                        } catch (Exception e) {
                            map.put("success", "0");
                            map.put("msg", "第" + (i + 1) + "行，" + e.getMessage());
                            return Tools.caseObjectToJson(map);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    map.put("success", "0");
                    map.put("msg", e.getMessage());
                    return Tools.caseObjectToJson(map);
                } finally {
                    readwb.close();
                }
                FileUtils.deleteQuietly(file);
                map.put("msg", "上传成功");
                map.put("success", "1");
                map.put("data", "");
                return Tools.caseObjectToJson(map);
            }
        }
        map.put("msg", "未找到合法数据");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取大满贯列表
     *
     * @param auth
     * @param search
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("min_sheng_bank_list")
    public String getMinShengBankList(@CookieParam("auth") String auth, @QueryParam("search") String search, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        String category = null;
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(search)) {
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(start)) {
            searchMap.put("startDate", Tools.parseDate(start, "yyy-MM-dd"));
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount() && ((Date) searchMap.get("startDate")).before(Tools.getBeginOfYear(new Date()))) {
                throw new EjbMessageException("只能查询今年的数据");
            }
        } else {
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount()) {
                searchMap.put("startDate", Tools.getBeginOfYear(new Date()));
            }
        }
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.getEndOfDay(Tools.parseDate(end, "yyy-MM-dd")));
        }
        ResultList<ProductMinShengBank> list = adminService.findProductMinShengBankList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取民生银行信息
     *
     * @param auth
     * @param id
     * @return
     * @throws Exception
     */
    @GET
    @Path("min_sheng_bank_info")
    public String getProducMinShengBankInfo(@CookieParam("auth") String auth, @QueryParam("id") Long id) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        ProductMinShengBank productMinShengBank = em.find(ProductMinShengBank.class, id);
        map.put("data", productMinShengBank);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除民生銀行
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_min_sheng_bank")
    public String delMinShengBank(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        for (Long id : ids) {
            ProductMinShengBank productMinShengBank = em.find(ProductMinShengBank.class, id);
            productMinShengBank.setDeleted(true);
            em.merge(productMinShengBank);
            WageLog wageLog = adminService.findWageLogByProductMinShengBank(productMinShengBank);
            wageLog.setDeleted(Boolean.TRUE);
            em.merge(wageLog);
        }
        map.put("msg", "删除成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取会员列表
     *
     * @param auth
     * @param search
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("vip_list")
    public String getVipList(@CookieParam("auth") String auth, @QueryParam("search") String search, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        String category = null;
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(search)) {
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(start)) {
            searchMap.put("startDate", Tools.parseDate(start, "yyy-MM-dd"));
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount() && ((Date) searchMap.get("startDate")).before(Tools.getBeginOfYear(new Date()))) {
                throw new EjbMessageException("只能查询今年的数据");
            }
        } else {
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount()) {
                searchMap.put("startDate", Tools.getBeginOfYear(new Date()));
            }
        }
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.getEndOfDay(Tools.parseDate(end, "yyy-MM-dd")));
        }
        ResultList<Vip> list = adminService.findProductVipList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 导入会员
     *
     * @param servletRequest
     * @return
     * @throws Exception
     */
    @POST
    @Path("upload_file_vip")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String uploadFileVip(@CookieParam("auth") String auth, @Context HttpServletRequest servletRequest) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        FileUploadObj fileUploadObj = null;
        Map map = Tools.getDMap();
        try {
            fileUploadObj = Tools.uploadFile(servletRequest, 100, null, null, null);
        } catch (FileUploadException e) {
            map.put("msg", e.getMessage());
            return Tools.caseObjectToJson(map);
        }
        for (FileUploadItem item : fileUploadObj.getFileList()) {
            if ("file1".equals(item.getFieldName())) {
                File file = new File(item.getUploadFullPath());
                jxl.Workbook readwb = null;
                try {
                    //构建Workbook对象, 只读Workbook对象   
                    //直接从本地文件创建Workbook   
                    InputStream instream = new FileInputStream(item.getUploadFullPath());
                    readwb = Workbook.getWorkbook(instream);
                    //Sheet的下标是从0开始   
                    //获取第一张Sheet表   
                    Sheet readsheet = readwb.getSheet(0);
                    //获取Sheet表中所包含的总行数   
                    int rsRows = readsheet.getRows();

                    for (int i = 1; i < rsRows; i++) {
                        Cell[] cells = readsheet.getRow(i);
                        //到款日期
                        String payDateStr = StringUtils.trim(cells[0].getContents());
                        Date payDate = null;
                        try {
                            payDate = Tools.parseDate(payDateStr, "yyyy-MM-dd");
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行到款日期格式错误");
                        }
                        if (payDate == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行到款日期格式错误");
                        }
                        //会员到期时间
                        String endDateStr = StringUtils.trim(cells[1].getContents());
                        Date endDate = null;
                        try {
                            endDate = Tools.parseDate(endDateStr, "yyyy-MM-dd");
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行会员到期时间格式错误");
                        }
                        if (endDate == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行会员到期时间格式错误");
                        }
                        Map searchMap = new HashMap();
                        //地区经理
                        String mangerName = StringUtils.trim(cells[2].getContents());
                        SysUser mangerUser = null;
                        if (mangerName != null) {
                            searchMap.clear();
                            searchMap.put("name", mangerName);
                            searchMap.put("notStatus", SysUserStatus.PEDING);
                            searchMap.put("adminType", SysUserTypeEnum.ADMIN);
                            searchMap.put("roleIdIsNotNull", true);
                            ResultList<SysUser> userList = adminService.findUserList(searchMap, 1, 10, null, Boolean.TRUE);
                            if (userList.size() != 1) {
                                throw new EjbMessageException("第" + (i + 1) + "行地区经理无法唯一定位，请手动录入该条");
                            }
                            mangerUser = userList.get(0);

                        }

                        //省份
                        String provinceStr = StringUtils.trim(cells[3].getContents());
                        String provinceCode = null;
                        if (Tools.isNotBlank(provinceStr)) {
                            TypedQuery<DataProvince> dataProvinceQuery = em.createQuery("SELECT a FROM DataProvince a WHERE a.name = :name", DataProvince.class);
                            dataProvinceQuery.setParameter("name", provinceStr);
                            List<DataProvince> dataProvinceList = dataProvinceQuery.getResultList();
                            if (dataProvinceList.size() != 1) {
                                throw new EjbMessageException("第" + (i + 1) + "行省份无法唯一定位，请手动录入该条");
                            }
                            provinceCode = dataProvinceList.get(0).getCode();
                        }

                        //分成代理
                        String placeName = StringUtils.trim(cells[4].getContents());
                        searchMap.clear();
                        searchMap.put("category", CategoryEnum.SERVICE_PEOPLE);
                        searchMap.put("placeName", placeName);
                        searchMap.put("status", OrderStatusEnum.SUCCESS);
                        ResultList<GoodsOrder> orderList = adminService.findOrderList(searchMap, 1, 10, Boolean.TRUE, Boolean.FALSE);
                        if (orderList.size() != 1) {
                            throw new EjbMessageException("第" + (i + 1) + "获得代理无法唯一定位，请手动录入该条");
                        }
                        SysUser divideUser = orderList.get(0).getAgentUser();
                        Long orderId = orderList.get(0).getId();
                        Long goodsId = orderList.get(0).getGoods().getId();

                        //到款金额
                        String amount = StringUtils.trimToNull(cells[5].getContents());
                        BigDecimal amountBd = null;
                        if (amount == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行到款金额错误");
                        }
                        try {
                            amountBd = new BigDecimal(amount);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行到款金额错误");
                        }

                        //代理提成
                        String divideUserAmount = StringUtils.trimToNull(cells[6].getContents());
                        BigDecimal divideUserAmountBd = null;
                        if (divideUserAmount == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行代理提成错误");
                        }
                        try {
                            divideUserAmountBd = new BigDecimal(divideUserAmount);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行代理提成错误");
                        }

                        //公益
                        String welfareAmount = StringUtils.trimToNull(cells[7].getContents());
                        BigDecimal welfareAmountBd = null;
                        if (welfareAmount == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行代理提成错误");
                        }
                        try {
                            welfareAmountBd = new BigDecimal(welfareAmount);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行代理提成错误");
                        }

                        //会员姓名
                        String vipName = StringUtils.trimToNull(cells[8].getContents());

                        //会员生日
                        String vipBirthday = null;
                        //会员生日
                        String vipBirthdayNoYear = null;
                        
                        Date vipBirthdayDate = null;
                        if (cells.length > 9) {
                            vipBirthday = StringUtils.trimToNull(cells[9].getContents());
                            vipBirthdayNoYear = vipBirthday;
                            try {
                                vipBirthdayDate = Tools.parseDate(vipBirthday, "yyyy-MM-dd");
                            } catch (Exception e) {
                                vipBirthdayDate = null;
//                                throw new EjbMessageException("第" + (i + 1) + "行会员生日格式错误");
                            }
                            if (vipBirthdayDate == null && Tools.isBlank(vipBirthdayNoYear)) {
                                throw new EjbMessageException("第" + (i + 1) + "行会员生日必填");
                            }
                        }

                        //会员微信号
                        String vipWechat = null;
                        if (cells.length > 10) {
                            vipWechat = StringUtils.trimToNull(cells[10].getContents());
                        }

                        //会员电话
                        String vipPhone = null;
                        if (cells.length > 11) {
                            vipPhone = StringUtils.trimToNull(cells[11].getContents());
                        }
                         if (Tools.isBlank(vipPhone)) {
                                throw new EjbMessageException("第" + (i + 1) + "行会员电话必填");
                            }
                         
                        //备注
                        String remark = null;
                        if (cells.length > 12) {
                            remark = StringUtils.trimToNull(cells[12].getContents());
                        }

                        try {
                            productService.createOrUpdateVip(null, payDate, endDate, mangerUser, provinceCode, provinceStr, orderId, goodsId, divideUser, amountBd, divideUserAmountBd, welfareAmountBd, vipName, vipBirthdayDate,vipBirthdayNoYear, vipWechat, vipPhone, remark);
                        } catch (Exception e) {
                            map.put("success", "0");
                            map.put("msg", "第" + (i + 1) + "行，" + e.getMessage());
                            return Tools.caseObjectToJson(map);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    map.put("success", "0");
                    map.put("msg", e.getMessage());
                    return Tools.caseObjectToJson(map);
                } finally {
                    readwb.close();
                }
                FileUtils.deleteQuietly(file);
                map.put("msg", "上传成功");
                map.put("success", "1");
                map.put("data", "");
                return Tools.caseObjectToJson(map);
            }
        }
        map.put("msg", "未找到合法数据");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 会员信息列表
     *
     * @param auth
     * @param id
     * @return
     * @throws Exception
     */
    @GET
    @Path("vip_info")
    public String getVipInfo(@CookieParam("auth") String auth, @QueryParam("id") Long id) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Vip vip = em.find(Vip.class, id);
        GoodsOrder goodsOrder = em.find(GoodsOrder.class, vip.getOrderId());
        vip.setGoodsOrder(goodsOrder);
        map.put("data", vip);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    @POST
    @Path("create_or_update_vip")
    public String createOrUpdateVip(@CookieParam("auth") String auth, @FormParam("id") Long id,
            @FormParam("payDate") String payDate, @FormParam("endDate") String endDate, @FormParam("orderId") Long orderId, @FormParam("managerId") Long managerId,
            @FormParam("province") String province, @FormParam("amount") String amount, @FormParam("divideUserAmount") String divideUserAmount, @FormParam("welfareAmount") String welfareAmount,
            @FormParam("vipName") String vipName, @FormParam("vipBirthday") String vipBirthday,@FormParam("vipBirthdayNoYear") String vipBirthdayNoYear,  @FormParam("vipWechat") String vipWechat, @FormParam("vipPhone") String vipPhone, @FormParam("remark") String remark) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        Date payDateDate = Tools.parseDate(payDate, "yyyy-MM-dd");
        Date endDateDate = Tools.parseDate(endDate, "yyyy-MM-dd");

        Date vipBirthdayDate = null;
        if (Tools.isNotBlank(vipBirthday)) {
            vipBirthdayDate = Tools.parseDate(vipBirthday, "yyyy-MM-dd");
        }

        GoodsOrder goodsOrder = em.find(GoodsOrder.class, orderId);

        SysUser manager = em.find(SysUser.class, managerId);

        String provinceStr = null;
        if (Tools.isNotBlank(province)) {
            TypedQuery<DataProvince> dataProvinceQuery = em.createQuery("SELECT a FROM DataProvince a WHERE a.code = :code", DataProvince.class);
            dataProvinceQuery.setParameter("code", province);
            DataProvince dataProvince = dataProvinceQuery.getSingleResult();
            provinceStr = dataProvince.getName();
        }

        productService.createOrUpdateVip(id, payDateDate, endDateDate, manager, province, provinceStr, orderId, goodsOrder.getGoods().getId(), goodsOrder.getAgentUser(), new BigDecimal(amount), new BigDecimal(divideUserAmount), new BigDecimal(welfareAmount), vipName, vipBirthdayDate,vipBirthdayNoYear, vipWechat, vipPhone, remark);
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除会员
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_vip")
    public String delVip(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        for (Long id : ids) {
            Vip vip = em.find(Vip.class, id);
            vip.setDeleted(true);
            em.merge(vip);
            WageLog wageLog = productService.findWageLogByVip(vip);
            wageLog.setDeleted(Boolean.TRUE);
            em.merge(wageLog);
        }
        map.put("msg", "删除成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 产品查找订单
     *
     * @param auth
     * @param search
     * @param status
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("order_list")
    public String getOrderList(@CookieParam("auth") String auth, @QueryParam("search") String search, @QueryParam("status") String status, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        String category = null;
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(search)) {
            String[] searchs = search.split(" ");
            for (String str : searchs) {
                if ("便民".equals(str)) {
                    category = "SERVICE_PEOPLE";
                } else if ("交友".equals(str)) {
                    category = "MAKE_FRIENDS";
                }
            }
            search = searchs[0];
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(category)) {
            searchMap.put("category", CategoryEnum.valueOf(category));
        }
        if (Tools.isNotBlank(status)) {
            searchMap.put("status", OrderStatusEnum.valueOf(status));
        }
        ResultList<GoodsOrder> list = adminService.findOrderList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 我的产品列表
     *
     * @param auth
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("my_product_list")
    public String myNewOrderList(@CookieParam("auth") String auth, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        searchMap.put("type", WageLogTypeEnum.PRODUCT);
        searchMap.put("user", user);
        String sql = "SELECT SUM(w.amount) FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.type = :type";
        Date startDate = Tools.getBeginOfYear(new Date());
        if (Tools.isNotBlank(start)) {
            startDate = Tools.parseDate(start, "yyyy-MM-dd");
            if (!configService.findConfigByKey("FIND_ONLY_YEAR").getValue().equals("-1") && startDate.before(Tools.getBeginOfYear(new Date()))) {
                throw new EjbMessageException("只能查询今年的数据");
            }
        }
        searchMap.put("startDate", startDate);
        sql += " AND w.payDate > :start";
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
            sql += " AND w.payDate < :end";
        }
        sql += " GROUP BY w.user";
        Query queryTotal = em.createQuery(sql);
        queryTotal.setParameter("user", user).setParameter("type", WageLogTypeEnum.PRODUCT);
        queryTotal.setParameter("start", Tools.addDay(startDate, -1));
        if (Tools.isNotBlank(end)) {
            queryTotal.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
        }
        ResultList<WageLog> list = adminService.findWageLogList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("totalAmount", queryTotal.getResultList().isEmpty() ? null : queryTotal.getSingleResult());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 我的产品列表
     *
     * @param auth
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("my_grand_slam_list")
    public String myGrandSlamList(@CookieParam("auth") String auth, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        searchMap.put("type", WageLogTypeEnum.GRAND_SLAM);
        searchMap.put("user", user);
        String sql = "SELECT SUM(w.amount) FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.type = :type";
        Date startDate = Tools.getBeginOfYear(new Date());
        if (Tools.isNotBlank(start)) {
            startDate = Tools.parseDate(start, "yyyy-MM-dd");
            if (!configService.findConfigByKey("FIND_ONLY_YEAR").getValue().equals("-1") && startDate.before(Tools.getBeginOfYear(new Date()))) {
                throw new EjbMessageException("只能查询今年的数据");
            }
        }
        searchMap.put("startDate", startDate);
        sql += " AND w.payDate > :start";
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
            sql += " AND w.payDate < :end";
        }
        sql += " GROUP BY w.user";
        Query queryTotal = em.createQuery(sql);
        queryTotal.setParameter("user", user).setParameter("type", WageLogTypeEnum.GRAND_SLAM);
        queryTotal.setParameter("start", Tools.addDay(startDate, -1));
        if (Tools.isNotBlank(end)) {
            queryTotal.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
        }
        ResultList<WageLog> list = adminService.findWageLogList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("totalAmount", queryTotal.getResultList().isEmpty() ? null : queryTotal.getSingleResult());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 我的民生銀行列表
     *
     * @param auth
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("my_min_sheng_bank_list")
    public String myMinShengBankList(@CookieParam("auth") String auth, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        searchMap.put("type", WageLogTypeEnum.MIN_SHENG_BANK);
        searchMap.put("user", user);
        String sql = "SELECT SUM(w.amount) FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.type = :type";
        Date startDate = Tools.getBeginOfYear(new Date());
        if (Tools.isNotBlank(start)) {
            startDate = Tools.parseDate(start, "yyyy-MM-dd");
            if (!configService.findConfigByKey("FIND_ONLY_YEAR").getValue().equals("-1") && startDate.before(Tools.getBeginOfYear(new Date()))) {
                throw new EjbMessageException("只能查询今年的数据");
            }
        }
        searchMap.put("startDate", startDate);
        sql += " AND w.payDate > :start";
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
            sql += " AND w.payDate < :end";
        }
        sql += " GROUP BY w.user";
        Query queryTotal = em.createQuery(sql);
        queryTotal.setParameter("user", user).setParameter("type", WageLogTypeEnum.MIN_SHENG_BANK);
        queryTotal.setParameter("start", Tools.addDay(startDate, -1));
        if (Tools.isNotBlank(end)) {
            queryTotal.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
        }
        ResultList<WageLog> list = adminService.findWageLogList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("totalAmount", queryTotal.getResultList().isEmpty() ? null : queryTotal.getSingleResult());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }
    
     /**
     * 我的民生銀行列表
     *
     * @param auth
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("my_vip_list")
    public String myVipList(@CookieParam("auth") String auth, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        searchMap.put("type", WageLogTypeEnum.VIP);
        searchMap.put("user", user);
        String sql = "SELECT SUM(w.amount) FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.type = :type";
        Date startDate = Tools.getBeginOfYear(new Date());
        if (Tools.isNotBlank(start)) {
            startDate = Tools.parseDate(start, "yyyy-MM-dd");
            if (!configService.findConfigByKey("FIND_ONLY_YEAR").getValue().equals("-1") && startDate.before(Tools.getBeginOfYear(new Date()))) {
                throw new EjbMessageException("只能查询今年的数据");
            }
        }
        searchMap.put("startDate", startDate);
        sql += " AND w.payDate > :start";
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
            sql += " AND w.payDate < :end";
        }
        sql += " GROUP BY w.user";
        Query queryTotal = em.createQuery(sql);
        queryTotal.setParameter("user", user).setParameter("type", WageLogTypeEnum.VIP);
        queryTotal.setParameter("start", Tools.addDay(startDate, -1));
        if (Tools.isNotBlank(end)) {
            queryTotal.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
        }
        ResultList<WageLog> list = adminService.findWageLogList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("totalAmount", queryTotal.getResultList().isEmpty() ? null : queryTotal.getSingleResult());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    @GET
    @Path("doxxxx")
    public String myNewOrderList() throws Exception {
        Map map = Tools.getDMap();
        Date date = Tools.parseDate("2015-12-31", "yyyy-MM-dd");
        Query queryTotal1 = em.createQuery("SELECT SUM(w.paidPrice),COUNT(w) FROM GoodsOrder g WHERE g.lastPayDate > :date AND g.deleted = FALSE AND g.status = :status AND g.category = :category");
        queryTotal1.setParameter("status", OrderStatusEnum.SUCCESS).setParameter("date", date).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
        Query queryTotal2 = em.createQuery("SELECT SUM(w.paidPrice),COUNT(w) FROM GoodsOrder g WHERE g.lastPayDate > :date AND g.deleted = FALSE AND g.status = :status AND g.category = :category");
        queryTotal2.setParameter("status", OrderStatusEnum.SUCCESS).setParameter("date", date).setParameter("category", CategoryEnum.MAKE_FRIENDS);
        Object[] os1 = (Object[]) queryTotal1.getSingleResult();
        Object[] os2 = (Object[]) queryTotal2.getSingleResult();
        map.put("便民平台出售", os1[0] + " " + os1[1]);
        map.put("交友平台出售", os2[0] + " " + os2[1]);
        Query queryTotal3 = em.createQuery("SELECT SUM(a.amount),SUM(a.userBalanceAmount + userAmount) FROM NewAd a WHERE a.payDate > :date AND a.deleted = FALSE AND g.category = :category");
        queryTotal3.setParameter("date", date).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
        Query queryTotal4 = em.createQuery("SELECT SUM(a.amount),SUM(a.userBalanceAmount + userAmount) FROM NewAd a WHERE a.payDate > :date AND a.deleted = FALSE AND g.category = :category");
        queryTotal4.setParameter("date", date).setParameter("category", CategoryEnum.MAKE_FRIENDS);
        Object[] os3 = (Object[]) queryTotal3.getSingleResult();
        Object[] os4 = (Object[]) queryTotal4.getSingleResult();
        map.put("便民平台广告", os3[0] + " " + os3[1]);
        map.put("交友平台广告", os4[0] + " " + os4[1]);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 修正本地数据
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("bendi_data_recorver")
    public String bendiDataRecorver(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        TypedQuery<DataProvince> dataProvinceQuery = em.createQuery("SELECT a FROM DataProvince a", DataProvince.class);
        List<DataProvince> dataProvinceList = dataProvinceQuery.getResultList();
        TypedQuery<Goods> goodsQuery = em.createQuery("SELECT a FROM Goods a WHERE a.id > 36706 AND a.id < 37457", Goods.class);
        List<Goods> list = goodsQuery.getResultList();
        for (Goods goods : list) {
            DataProvince dp = null;
            for (DataProvince dataProvince : dataProvinceList) {
                if (dataProvince.getName().equals(goods.getProvince())) {
                    dp = dataProvince;
                    break;
                }
            }
            goods.setProvince(dp.getCode());
            goods.setProvinceStr(dp.getName());
            em.merge(goods);
        }
        return "ok";
    }

    /**
     * 导入本地管家的订单数据
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("new_goods_order_from_bendi")
    public String createNewGoodsOrderFromBendi(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        List<String> list1 = FileUtils.readLines(new File("d:/a/1.txt"));
        List<String> list2 = FileUtils.readLines(new File("d:/a/2.txt"));
        List<String> list3 = FileUtils.readLines(new File("d:/a/3.txt"));
        List<String> list4 = FileUtils.readLines(new File("d:/a/4.txt"));
        List<String> list5 = FileUtils.readLines(new File("d:/a/5.txt"));
        List<String> list6 = FileUtils.readLines(new File("d:/a/6.txt"));
        List<String> list7 = FileUtils.readLines(new File("d:/a/7.txt"));
        List<String> list8 = FileUtils.readLines(new File("d:/a/8.txt"));
        List<String> list9 = FileUtils.readLines(new File("d:/a/9.txt"));
        //读取文件，验证源文件
//        Set set = new HashSet();
//        List list = new ArrayList();
        for (int i = 0; i < list2.size(); i++) {
            String contractSerialId = list1.get(i);
            String name = list2.get(i).trim();
            String idCard = list3.get(i).trim();
            String goodsType = list4.get(i).trim();
            String oldName = list5.get(i);
            String goodsName = list6.get(i).trim();
            String goodsOrderStart = list7.get(i);
            String orderPrice = list8.get(i);
            String balance = list9.get(i);
            GoodsTypeEnum goodsTypeEnum = null;
            if ("省会".equals(goodsType)) {
                goodsTypeEnum = GoodsTypeEnum.PROVINCIAL_CAPITAL;
            } else if ("直辖市".equals(goodsType)) {
                goodsTypeEnum = GoodsTypeEnum.GOVERNMENT_DIRECTLY;
            } else if ("热点".equals(goodsType)) {
                goodsTypeEnum = GoodsTypeEnum.HOT;
            } else if ("地级市".equals(goodsType)) {
                goodsTypeEnum = GoodsTypeEnum.PREFECTURE;
            } else if ("县级市".equals(goodsType)) {
                goodsTypeEnum = GoodsTypeEnum.COUNTY_CITY;
            } else if ("县".equals(goodsType)) {
                goodsTypeEnum = GoodsTypeEnum.COUNTY;
            } else if ("自治县".equals(goodsType)) {
                goodsTypeEnum = GoodsTypeEnum.SELF_COUNTY;
            } else if ("直辖区".equals(goodsType)) {
                goodsTypeEnum = GoodsTypeEnum.MUNICIPAL_DISTRICTS;
            } else {
                System.out.println(goodsType);
            }
            TypedQuery<DataProvince> dataProvinceQuery = em.createQuery("SELECT a FROM DataProvince a", DataProvince.class);
            List<DataProvince> dataProvinceList = dataProvinceQuery.getResultList();
            String province = null;
            if (goodsName.contains("省")) {
                province = goodsName.split("省")[0] + "省";
                goodsName = goodsName.split("省")[1];
            } else if (goodsName.startsWith("重庆市")) {
                province = "重庆市";
                goodsName = goodsName.replaceFirst("重庆市", "");
            } else if (goodsName.startsWith("天津市")) {
                province = "天津市";
                goodsName = goodsName.replaceFirst("天津市", "");
            } else if (goodsName.startsWith("北京市")) {
                province = "北京市";
                goodsName = goodsName.replaceFirst("北京市", "");
            } else if (goodsName.startsWith("上海市")) {
                province = "上海市";
                goodsName = goodsName.replaceFirst("上海市", "");
            } else if (goodsName.startsWith("重庆")) {
                province = "重庆市";
                goodsName = goodsName.replaceFirst("重庆", "");
            } else if (goodsName.startsWith("天津")) {
                province = "天津市";
                goodsName = goodsName.replaceFirst("天津", "");
            } else if (goodsName.startsWith("北京")) {
                province = "北京市";
                goodsName = goodsName.replaceFirst("北京", "");
            } else if (goodsName.startsWith("上海")) {
                province = "上海市";
                goodsName = goodsName.replaceFirst("上海", "");
            } else if (goodsName.startsWith("内蒙古")) {
                province = "内蒙古";
                goodsName = goodsName.replaceFirst("内蒙古", "");
            } else {
                System.out.println("NO:" + goodsName);
            }
//            if (set.contains(goodsName)) {
//                System.out.println("重复地区："+goodsName);
//            }
//            set.add(goodsName);
//            list.add(goodsName);
            DataProvince dataProvince = null;
            for (DataProvince dataProvinceSub : dataProvinceList) {
                if (dataProvinceSub.getName().equals(province)) {
                    dataProvince = dataProvinceSub;
                } else if (dataProvinceSub.getName().equals(province.replaceFirst("省", ""))) {
                    dataProvince = dataProvinceSub;
                }
            }
            if (dataProvince == null) {
                System.out.println("省份未找到:" + goodsName);
            }
            //检查订单是否存在
            List<OrderStatusEnum> orderStatusEnumList = new ArrayList();
            orderStatusEnumList.add(OrderStatusEnum.WAIT_SIGN_CONTRACT);
            orderStatusEnumList.add(OrderStatusEnum.SUCCESS);
            orderStatusEnumList.add(OrderStatusEnum.EARNEST);
            TypedQuery<GoodsOrder> goodsorderQuery = em.createQuery("SELECT a FROM GoodsOrder a WHERE a.deleted = FALSE AND a.status IN :status AND a.category = :category AND a.goods.name = :name", GoodsOrder.class);
            goodsorderQuery.setParameter("category", CategoryEnum.MAKE_FRIENDS).setParameter("name", goodsName).setParameter("status", orderStatusEnumList);
            if (goodsorderQuery.getResultList().size() > 0) {
                System.out.println("size:" + goodsName);
            }
            //检查用户idCard
            TypedQuery<SysUser> sysUserQuery = em.createQuery("SELECT a FROM SysUser a WHERE a.deleted = FALSE AND a.idCard = :idCard", SysUser.class);
            sysUserQuery.setParameter("idCard", idCard);
            SysUser sysUser = null;
            if (sysUserQuery.getResultList().size() == 1) {
                sysUser = sysUserQuery.getResultList().get(0);
//                System.out.println("用户1:" + goodsName);
                if (!name.equals(sysUser.getName())) {
                    System.out.println("代理地区:" + goodsName + " 系统中名字：" + sysUser.getName() + " 文档名字：" + name);
                    continue;
                }
            } else if (sysUserQuery.getResultList().size() > 1) {
                System.out.println("用户2:" + goodsName);
            } else {
                //创建用户
                sysUser = new SysUser();
                sysUser.setAccount(idCard);
                sysUser.setBalance(BigDecimal.ZERO);
                sysUser.setDeposit(BigDecimal.ZERO);
                sysUser.setIdCard(idCard);
                sysUser.setName(name);
                sysUser.setPasswd(Tools.md5(idCard.toLowerCase().substring(idCard.length() - 6, idCard.length())));
                em.persist(sysUser);
            }
            Date goodsOrderStartDate = null;
            try {
                goodsOrderStartDate = Tools.parseDate(goodsOrderStart, "yyyy/mm/dd");
            } catch (Exception ex) {
                System.out.println("时间错误:" + goodsName);
            }
            Double orderPriceDouble = null;
            try {
                orderPriceDouble = Double.parseDouble(orderPrice);
            } catch (Exception ex) {
                System.out.println("金额错误:" + goodsName);
            }
            BigDecimal orderPriceBigDecimal = new BigDecimal(orderPriceDouble);
            if (Tools.isBlank(balance)) {
                balance = orderPrice;
            }
            Double balanceDouble = null;
            try {
                balanceDouble = Double.parseDouble(balance);
            } catch (Exception ex) {
                System.out.println("余额错误:" + goodsName);
            }
            BigDecimal balanceBigDecimal = new BigDecimal(balanceDouble);
            sysUser.setBalance(sysUser.getBalance().add(balanceBigDecimal));
            sysUser.setDeposit(sysUser.getDeposit().add(orderPriceBigDecimal));
            if (sysUser.getId() == null) {
                em.persist(sysUser);
            } else {
                em.merge(sysUser);
            }
            //生成商品
            Goods goods = new Goods();
            goods.setCategory(CategoryEnum.SERVICE_PEOPLE);
            goods.setName(goodsName);
            goods.setNamePinyin(Trans2PinYin.trans2PinYinFirst(goodsName));
            goods.setPeopleCount(0);
            goods.setPrice(new BigDecimal(goodsTypeEnum.getPrice()));
            goods.setProvince(dataProvince.getName());
            goods.setProvinceStr(dataProvince.getPinyin());
            goods.setSerialId(adminService.getUniqueGoodsSerialId());
            goods.setStatus(GoodsStatusEnum.SOLD_OUT);
            goods.setStatusStartDate(goodsOrderStartDate);
            goods.setStatusEndDate(Tools.addYear(goodsOrderStartDate, 1));
            goods.setType(goodsTypeEnum);
            //判断大区经理
            TypedQuery<SysUser> suQuery = em.createQuery("SELECT a.user FROM Goods a WHERE a.deleted = FALSE AND a.status = :status AND a.category = :category AND a.province = :province", SysUser.class);
            suQuery.setParameter("category", CategoryEnum.SERVICE_PEOPLE).setParameter("province", dataProvince.getCode()).setParameter("status", GoodsStatusEnum.SOLD_OUT);
            suQuery.setMaxResults(1);
            SysUser daqu = suQuery.getSingleResult();
            if (daqu == null) {
                System.out.println("daqujingli:" + goodsName);
            }
            goods.setUser(daqu);
            em.persist(goods);
            //生成订单
            GoodsOrder order = new GoodsOrder();
            order.setAgentUser(sysUser);
            order.setCategory(CategoryEnum.SERVICE_PEOPLE);
            order.setStatus(OrderStatusEnum.SUCCESS);
            order.setGatewayType(PaymentGatewayTypeEnum.BANK_TRANSFER);
            order.setGoods(goods);
            order.setGoodsMsg(goods.getProvinceStr() + "_" + goods.getName() + "_" + goods.getUser().getId() + "_" + goods.getUser().getName());
            order.setGoodsPinyin(Trans2PinYin.trans2PinYinFirst(goods.getName()));
            order.setEndDate(goodsOrderStartDate);
            order.setFee(BigDecimal.ZERO);
            order.setLimitEnd(Tools.addYear(goodsOrderStartDate, 1));
            order.setLastPayDate(goodsOrderStartDate);
            order.setLimitStart(goodsOrderStartDate);
            order.setOldName(oldName);
            order.setPaidPrice(orderPriceBigDecimal);
            order.setPeopleCountFee(BigDecimal.ZERO);
            order.setPrice(orderPriceBigDecimal);
            order.setSerialId(adminService.getUniqueOrderSerialId());
            order.setUser(daqu);
            order.setBackAmount(BigDecimal.ZERO);
            order.setUserAmount(BigDecimal.ZERO);
            if (Tools.isNotBlank(contractSerialId)) {
                order.setContractSerialId(contractSerialId);
            }
            em.persist(order);
            OrderRecord orderRecord = new OrderRecord();
            orderRecord.setGatewayType(PaymentGatewayTypeEnum.BANK_TRANSFER);
            orderRecord.setPayDate(goodsOrderStartDate);
            orderRecord.setType(OrderRecordTypeEnum.FINAL_PAYMENT);
            orderRecord.setGoods(order.getGoods());
            orderRecord.setOrder(order);
            orderRecord.setPrice(orderPriceBigDecimal);
            em.persist(orderRecord);
        }
        return "ok";
    }

    /**
     * 删除化妆品
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_cosmetics")
    public String delCosmetics(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        for (Long id : ids) {
            Cosmetics cosmetics = em.find(Cosmetics.class, id);
            cosmetics.setDeleted(true);
            em.merge(cosmetics);
            WageLog wageLog = adminService.findWageLogByCosmetics(cosmetics);
            wageLog.setDeleted(Boolean.TRUE);
            em.merge(wageLog);
        }
        map.put("msg", "删除成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建/更新化妆品
     *
     * @param auth
     * @param id
     * @param soldCount
     * @param incomeAmount
     * @param commissionAmount
     * @param product
     * @param goodsOrderId
     * @param remark
     * @param payDate
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_or_update_cosmetics")
    public String createOrUpdateCosmetics(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("soldCount") int soldCount, @FormParam("incomeAmount") String incomeAmount, @FormParam("commissionAmount") String commissionAmount,
            @FormParam("product") int product, @FormParam("goodsOrderId") Long goodsOrderId, @FormParam("remark") String remark, @FormParam("payDate") String payDate, @FormParam("regionalManager") Long regionalManager, @FormParam("regionalManagerAmount") String regionalManagerAmount,
            @FormParam("payType") String payType) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        Date payDateTime = null;
        try {
            payDateTime = Tools.parseDate(payDate, "yyyy-MM-dd");
        } catch (Exception e) {
            payDateTime = Tools.getBeginOfDay(new Date());
        }
        if (new BigDecimal(incomeAmount).compareTo(BigDecimal.ZERO) < 0 || new BigDecimal(commissionAmount).compareTo(BigDecimal.ZERO) < 0) {
            throw new EjbMessageException("参数异常");
        }
        if (regionalManager == null) {
            regionalManagerAmount = "0";
        }
        adminService.createOrUpdateCosmetics(id, goodsOrderId, new BigDecimal(incomeAmount), new BigDecimal(commissionAmount), payDateTime, product, remark, soldCount, regionalManager, new BigDecimal(regionalManagerAmount), PaymentGatewayTypeEnum.valueOf(StringUtils.trim(payType)));
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取化妆品列表
     *
     * @param auth
     * @param search
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("cosmetics_list")
    public String getCosmeticsList(@CookieParam("auth") String auth, @QueryParam("search") String search, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        String category = null;
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(search)) {
            String[] searchs = search.split(" ");
            for (String str : searchs) {
                if ("便民".equals(str)) {
                    category = "SERVICE_PEOPLE";
                } else if ("交友".equals(str)) {
                    category = "MAKE_FRIENDS";
                }
            }
            search = searchs[0];
            searchMap.put("search", search);
        }
        if (Tools.isNotBlank(category)) {
            searchMap.put("category", CategoryEnum.valueOf(category));
        }
        if (Tools.isNotBlank(start)) {
            searchMap.put("startDate", Tools.parseDate(start, "yyy-MM-dd"));
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount() && ((Date) searchMap.get("startDate")).before(Tools.getBeginOfYear(new Date()))) {
                throw new EjbMessageException("只能查询今年的数据");
            }
        } else {
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && !user.isIsFindSelfYearAmount()) {
                searchMap.put("startDate", Tools.getBeginOfYear(new Date()));
            }
        }
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.getEndOfDay(Tools.parseDate(end, "yyy-MM-dd")));
        }
        ResultList<Cosmetics> list = adminService.findCosmeticsList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取化妆品信息
     *
     * @param auth
     * @param id
     * @return
     * @throws Exception
     */
    @GET
    @Path("cosmetics_info")
    public String getCosmeticsInfo(@CookieParam("auth") String auth, @QueryParam("id") Long id) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Cosmetics cosmetics = em.find(Cosmetics.class, id);
        map.put("data", cosmetics);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 我的化妆品列表
     *
     * @param auth
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("my_cosmetics_list")
    public String myCosmeticsList(@CookieParam("auth") String auth, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        searchMap.put("type", WageLogTypeEnum.PRODUCT);
        searchMap.put("user", user);
        String sql = "SELECT SUM(w.amount) FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.type = :type";
        if (Tools.isNotBlank(start)) {
            searchMap.put("startDate", Tools.parseDate(start, "yyyy-MM-dd"));
            sql += " AND w.payDate > :start";
        }
        if (Tools.isNotBlank(end)) {
            searchMap.put("endDate", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
            sql += " AND w.payDate < :end";
        }
        sql += " GROUP BY w.user";
        Query queryTotal = em.createQuery(sql);
        queryTotal.setParameter("user", user).setParameter("type", WageLogTypeEnum.PRODUCT);
        if (Tools.isNotBlank(start)) {
            queryTotal.setParameter("start", Tools.addDay(Tools.parseDate(start, "yyyy-MM-dd"), -1));
        }
        if (Tools.isNotBlank(end)) {
            queryTotal.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
        }
        ResultList<WageLog> list = adminService.findWageLogList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("totalAmount", queryTotal.getResultList().isEmpty() ? null : queryTotal.getSingleResult());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    @GET
    @Path("product_type_config_list")
    public String productTypeConfigList(@CookieParam("auth") String auth, @DefaultValue("1") @QueryParam("type") Integer type, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("100") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        String sql = "SELECT p FROM ProductTypeConfig p WHERE p.type = :type";
        TypedQuery<ProductTypeConfig> queryTotal = em.createQuery(sql, ProductTypeConfig.class);
        queryTotal.setParameter("type", type);
        List<ProductTypeConfig> list = queryTotal.getResultList();
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }
    
    @GET
    @Path("ad_limit_config_list")
    public String adLimitConfigList(@CookieParam("auth") String auth, @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("100") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        String sql = "SELECT p FROM AdLimitConfig p";
        TypedQuery<ProductTypeConfig> queryTotal = em.createQuery(sql, ProductTypeConfig.class);
        List<ProductTypeConfig> list = queryTotal.getResultList();
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    @POST
    @Path("save_product_type_config")
    public String saveProductTypeConfig(@CookieParam("auth") String auth, @FormParam("type") Integer type, @FormParam("name") String name, @FormParam("key") String key) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        String sql = "SELECT p FROM ProductTypeConfig p WHERE p.key = :key";
        TypedQuery<ProductTypeConfig> queryTotal = em.createQuery(sql, ProductTypeConfig.class);
        queryTotal.setParameter("key", key);
        List<ProductTypeConfig> list = queryTotal.getResultList();
        ProductTypeConfig productTypeConfig = null;
        if (list == null || list.isEmpty()) {
            productTypeConfig = new ProductTypeConfig();
        } else {
            productTypeConfig = list.get(0);
            if (!type.equals(productTypeConfig.getType())) {
                map.put("msg", "化妆品和产品不能KEY相同");
                map.put("success", "0");
                return Tools.caseObjectToJson(map);
            }
        }
        productTypeConfig.setKey(key);
        productTypeConfig.setType(type);
        productTypeConfig.setName(name);
        if (list == null || list.isEmpty()) {
            em.persist(productTypeConfig);
        } else {
            em.merge(productTypeConfig);
        }
        map.put("msg", "操作成功");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }
    
    
    @POST
    @Path("save_ad_limit_config")
    public String saveAdLimitConfig(@CookieParam("auth") String auth, @FormParam("name") String name, @FormParam("key") String key) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        String sql = "SELECT p FROM AdLimitConfig p WHERE p.key = :key";
        TypedQuery<AdLimitConfig> queryTotal = em.createQuery(sql, AdLimitConfig.class);
        queryTotal.setParameter("key", key);
        List<AdLimitConfig> list = queryTotal.getResultList();
        AdLimitConfig adLimitConfig = null;
        if (list == null || list.isEmpty()) {
            adLimitConfig = new AdLimitConfig();
        } else {
            adLimitConfig = list.get(0);
            if (!key.equals(adLimitConfig.getKey())) {
                map.put("msg", "不能KEY相同");
                map.put("success", "0");
                return Tools.caseObjectToJson(map);
            }
        }
        adLimitConfig.setKey(key);
        adLimitConfig.setName(name);
        if (list == null || list.isEmpty()) {
            em.persist(adLimitConfig);
        } else {
            em.merge(adLimitConfig);
        }
        map.put("msg", "操作成功");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

}
