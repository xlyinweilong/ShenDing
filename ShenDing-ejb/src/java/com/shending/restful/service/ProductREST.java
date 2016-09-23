package com.shending.restful.service;

import com.shending.entity.*;
import com.shending.restful.interception.AccountInterceptor;
import com.shending.service.AdminService;
import com.shending.support.*;
import com.shending.support.enums.*;
import com.shending.support.exception.EjbMessageException;
import java.io.File;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.FileUtils;

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
    @PersistenceContext(unitName = "ShenDing-PU")
    private EntityManager em;

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
            @FormParam("product") String product, @FormParam("goodsOrderId") Long goodsOrderId, @FormParam("remark") String remark, @FormParam("payDate") String payDate) throws Exception {
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
        adminService.createOrUpdateProductLog(id, goodsOrderId, new BigDecimal(incomeAmount), new BigDecimal(commissionAmount), payDateTime, ProductEnum.valueOf(product), remark, soldCount);
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

}
