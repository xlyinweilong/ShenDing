package com.shending.restful.service;

import com.shending.config.Config;
import com.shending.entity.DataArea;
import com.shending.entity.DataCity;
import com.shending.entity.DataProvince;
import com.shending.entity.DicDefUserAmount;
import com.shending.entity.Goods;
import com.shending.entity.GoodsOrder;
import com.shending.entity.GoodsWeChat;
import com.shending.entity.Log;
import com.shending.entity.NewAd;
import com.shending.entity.OrderRecord;
import com.shending.entity.OrderRenew;
import com.shending.entity.SysMenu;
import com.shending.entity.SysRole;
import com.shending.entity.SysUser;
import com.shending.entity.UserWageLog;
import com.shending.entity.Vote;
import com.shending.entity.WageLog;
import com.shending.restful.interception.AccountInterceptor;
import com.shending.service.AdminService;
import com.shending.support.FileUploadItem;
import com.shending.support.FileUploadObj;
import com.shending.support.ResultList;
import com.shending.support.Tools;
import com.shending.support.Trans2PinYin;
import static com.shending.support.Trans2PinYin.trans2PinYinFirst;
import com.shending.support.enums.AdGoodsTypeEnum;
import com.shending.support.enums.AdLevelEnum;
import com.shending.support.enums.AdLimitTypeEnum;
import com.shending.support.enums.AdPushTypeEnum;
import com.shending.support.enums.CategoryEnum;
import com.shending.support.enums.WageLogTypeEnum;
import com.shending.support.enums.GoodsStatusEnum;
import com.shending.support.enums.GoodsTypeEnum;
import com.shending.support.enums.OrderRecordTypeEnum;
import com.shending.support.enums.OrderStatusEnum;
import com.shending.support.enums.PaymentGatewayTypeEnum;
import com.shending.support.enums.ProductEnum;
import com.shending.support.enums.SysMenuPopedomEnum;
import com.shending.support.enums.SysUserStatus;
import com.shending.support.enums.SysUserTypeEnum;
import com.shending.support.exception.EjbMessageException;
import com.shending.support.vo.PlaceStatistics;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
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
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 后台管理
 *
 * @author yin.weilong
 */
@Stateless
@Path("admin")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
@Interceptors(AccountInterceptor.class)
public class AdminREST {

    @EJB
    private AdminService adminService;
    @PersistenceContext(unitName = "ShenDing-PU")
    private EntityManager em;

    /**
     * 导入广告
     *
     * @param servletRequest
     * @return
     * @throws Exception
     */
    @POST
    @Path("upload_file_ad")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String createOrUpdateEvent(@CookieParam("auth") String auth, @Context HttpServletRequest servletRequest) throws Exception {
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
                        //业务员
                        String placeName = StringUtils.trim(cells[1].getContents());
                        Map searchMap = new HashMap();
                        searchMap.put("category", CategoryEnum.SERVICE_PEOPLE);
                        searchMap.put("placeName", placeName);
                        searchMap.put("status", GoodsStatusEnum.SOLD_OUT);
                        ResultList<Goods> list = adminService.findGoodsList(searchMap, 1, 10, null, Boolean.TRUE);
                        if (list.size() != 1) {
                            throw new EjbMessageException("第" + (i + 1) + "行业务员无法唯一定位，请手动录入该条");
                        }
                        Goods goods = list.get(0);
                        //广告名称
                        String name = StringUtils.trim(cells[2].getContents());
                        if (StringUtils.isBlank(name)) {
                            throw new EjbMessageException("第" + (i + 1) + "行广告名称不能为空");
                        }
                        //广告微信
                        String ownerWeChat = StringUtils.trimToNull(cells[3].getContents());
                        //持续时间
                        String limitTypeStr = StringUtils.trimToNull(cells[4].getContents());
                        AdLimitTypeEnum limitType = AdLimitTypeEnum.getEnum(limitTypeStr);
                        if (limitType == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行持续时间错误");
                        }
                        //级别
                        String adLevelStr = StringUtils.trimToNull(cells[5].getContents());
                        AdLevelEnum adLevel = AdLevelEnum.getEnum(adLevelStr);
                        if (adLevel == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行级别错误");
                        }
                        //金额
                        String amount = StringUtils.trimToNull(cells[6].getContents());
                        BigDecimal amountBd = null;
                        if (amount == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行金额错误");
                        }
                        try {
                            amountBd = new BigDecimal(amount);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行金额错误");
                        }
                        //支付方式
                        String gatewayTypeStr = StringUtils.trimToNull(cells[7].getContents());
                        PaymentGatewayTypeEnum gatewayType = PaymentGatewayTypeEnum.getEnum(gatewayTypeStr);
                        if (gatewayType == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行支付方式错误");
                        }
                        //返还
                        String userBalanceAmount = StringUtils.trimToNull(cells[8].getContents());
                        BigDecimal userBalanceAmountBd = null;
                        if (userBalanceAmount == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行返还错误");
                        }
                        try {
                            userBalanceAmountBd = new BigDecimal(userBalanceAmount);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行返还错误");
                        }
                        //提成
                        String userAmount = StringUtils.trimToNull(cells[9].getContents());
                        BigDecimal userAmountBd = null;
                        if (userAmount == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行提成错误");
                        }
                        try {
                            userAmountBd = new BigDecimal(userAmount);
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行提成错误");
                        }
                        //备注
                        String remark = StringUtils.trimToNull(cells[10].getContents());

                        //接/发*
                        String sendTypeStr = StringUtils.trimToNull(cells[11].getContents());
                        WageLogTypeEnum sendType = WageLogTypeEnum.getEnum(sendTypeStr);
                        if (sendType == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行接/发类型错误");
                        }
                        //类型*
                        String categoryPlusStr = StringUtils.trimToNull(cells[12].getContents());
                        String categoryPlus = null;
                        if ("便民平台".equals(categoryPlusStr)) {
                            categoryPlus = "NORMAL";
                        } else if ("广告部".equals(categoryPlusStr)) {
                            categoryPlus = "AD_DEPARTMENT";
                        } else {
                            throw new EjbMessageException("第" + (i + 1) + "行类型错误");
                        }
                        try {
                            adminService.createOrUpdateNewAd(null, goods, amountBd, payDate, limitType, name, ownerWeChat, gatewayType, user, userBalanceAmountBd, userAmountBd, adLevel, remark, sendType, CategoryEnum.SERVICE_PEOPLE, categoryPlus);
                        } catch (EjbMessageException e) {
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
     * 导入订单
     *
     * @param servletRequest
     * @return
     * @throws Exception
     */
    @POST
    @Path("upload_file_order")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String importOrder(@CookieParam("auth") String auth, @Context HttpServletRequest servletRequest) throws Exception {
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

                        //代理区域
                        String placeName = StringUtils.trim(cells[0].getContents());
                        Map searchMap = new HashMap();
                        searchMap.put("category", CategoryEnum.SERVICE_PEOPLE);
                        searchMap.put("placeName", placeName);
                        searchMap.put("status", GoodsStatusEnum.SALE);
                        ResultList<Goods> list = adminService.findGoodsList(searchMap, 1, 10, Boolean.TRUE, Boolean.FALSE);
                        if (list.size() != 1) {
                            throw new EjbMessageException("第" + (i + 1) + "行代理区域无法唯一定位，请手动录入该条");
                        }
                        Goods goods = list.get(0);

                        //业务员
                        List<Long> recommendIds = new ArrayList<>();
                        List<String> recommendOrderIds = new ArrayList<>();
                        List<String> recommendRates = new ArrayList<>();
                        String placeNameYewu = StringUtils.trim(cells[1].getContents());
                        GoodsOrder orderYewu = null;
                        if (StringUtils.isNotBlank(placeNameYewu)) {
                            searchMap.clear();
                            searchMap.put("category", CategoryEnum.SERVICE_PEOPLE);
                            searchMap.put("placeName", placeNameYewu);
                            searchMap.put("status", OrderStatusEnum.SUCCESS);
                            ResultList<GoodsOrder> orderList = adminService.findOrderList(searchMap, 1, 10, Boolean.TRUE, Boolean.FALSE);
                            if (orderList.size() != 1) {
                                throw new EjbMessageException("第" + (i + 1) + "行业务员无法唯一定位，请手动录入该条");
                            }
                            orderYewu = orderList.get(0);
                            recommendIds.add(orderYewu.getAgentUser().getId());
                            recommendOrderIds.add(orderYewu.getId().toString());
                        }
                        String divideAmount = null;
                        if (!recommendIds.isEmpty()) {
                            //推荐分成总金额
                            divideAmount = StringUtils.trim(cells[2].getContents());
                        }

                        //分成大区经理
                        Long divideUserId = null;
                        String divideUserIdStr = StringUtils.trim(cells[3].getContents());
                        if (StringUtils.isNotBlank(divideUserIdStr)) {
                            searchMap.clear();
                            searchMap.put("name", divideUserIdStr);
                            searchMap.put("roleIdIsNotNull", true);
                            ResultList<SysUser> userList = adminService.findUserList(searchMap, 1, 10, Boolean.TRUE, Boolean.FALSE);
                            if (userList.size() != 1) {
                                throw new EjbMessageException("第" + (i + 1) + "行分成大区经理无法唯一定位，请手动录入该条");
                            }
                            divideUserId = userList.get(0).getId();
                        }
                        //分成大区经理分成金额
                        String userAmount = null;
                        if (divideUserId != null) {
                            userAmount = StringUtils.trim(cells[4].getContents());
                        }

                        //加盟金额
                        String price = StringUtils.trim(cells[5].getContents());
                        if (StringUtils.isBlank(price)) {
                            throw new EjbMessageException("第" + (i + 1) + "行加盟金额为空");
                        }

                        //人数费
                        String peopleCountFee = StringUtils.trimToNull(cells[6].getContents());

                        //已返还金额
                        String backAmount = StringUtils.trimToNull(cells[7].getContents());

                        //汇款金额
                        String amount = StringUtils.trimToNull(cells[8].getContents());
                        if (StringUtils.isBlank(amount)) {
                            throw new EjbMessageException("第" + (i + 1) + "行汇款金额为空");
                        }

                        //回款时间
                        String payDateStr = StringUtils.trim(cells[9].getContents());
                        Date payDate = null;
                        try {
                            payDate = Tools.parseDate(payDateStr, "yyyy-MM-dd");
                        } catch (Exception e) {
                            throw new EjbMessageException("第" + (i + 1) + "行回款时间格式错误");
                        }
                        if (payDate == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行回款时间格式错误");
                        }

                        //金额类型
                        OrderRecordTypeEnum orderRecordType = OrderRecordTypeEnum.getEnum(StringUtils.trim(cells[10].getContents()));
                        if (orderRecordType == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行金额类型错误");
                        }

                        //加盟部分成
                        String franchiseDepartmentCommissionStr = StringUtils.trimToNull(cells[11].getContents());
                        BigDecimal franchiseDepartmentCommission = BigDecimal.ZERO;
                        if (StringUtils.isNotBlank(franchiseDepartmentCommissionStr)) {
                            franchiseDepartmentCommission = new BigDecimal(franchiseDepartmentCommissionStr);
                        }

                        //备注
                        String remark = StringUtils.trimToNull(cells[12].getContents());

                        //支付方式
                        PaymentGatewayTypeEnum gatewayType = PaymentGatewayTypeEnum.getEnum(StringUtils.trim(cells[13].getContents()));
                        if (gatewayType == null) {
                            throw new EjbMessageException("第" + (i + 1) + "行支付方式错误");
                        }

                        adminService.createGoodsOrder(user, null, goods.getId(), userAmount, peopleCountFee, CategoryEnum.SERVICE_PEOPLE.toString(), remark, price, backAmount, recommendIds, divideAmount, recommendOrderIds, recommendRates, divideUserId, amount, payDateStr, gatewayType.toString(), orderRecordType.toString(), franchiseDepartmentCommission);
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

//    @GET
//    @Path("vote")
//    public String vote(@CookieParam("auth") String auth) throws Exception {
//        TypedQuery<Vote> query = em.createQuery("SELECT v FROM Vote v", Vote.class);
//        int total = 0;//总人数
//        int manTotal = 0;//男人总数
//        int womanToal = 0;//女人总数
//        int age = 0;//年龄
//        int ageCount = 0;//统计年龄人数
//        int tall = 0;//身高
//        int tallCount = 0;//身高人数
//        int wigtht = 0;//体重
//        int wigthtCount = 0;//体重
//        Map<String, Integer> subMap6 = new HashMap<>();
//        Map<String, Integer> subMap7 = new HashMap<>();
//        Map<String, Integer> subMap8 = new HashMap<>();
//        Map<String, Integer> subMap13 = new HashMap<>();
//        Map<String, Integer> subMap14 = new HashMap<>();
//        Map<String, Integer> subMap15 = new HashMap<>();
//        Map<String, Integer> subMap16 = new HashMap<>();
//        Map<String, Integer> subMap9 = new HashMap<>();
//        Map<String, Integer> subMap10 = new HashMap<>();
//        Map<String, Integer> subMap11 = new HashMap<>();
//        Map<String, Integer> subMap12 = new HashMap<>();
//        Map map = new HashMap();
//        for (Vote vote : query.getResultList()) {
//            if (vote.getQ2().equals("0")) {
//                womanToal++;
//            } else {
//                manTotal++;
//            }
//            total++;
//            if (Tools.isNotBlank(vote.getQ3())) {
//                int i = Integer.parseInt(vote.getQ3());
//                if (i < 100) {
//                    ageCount++;
//                    age += i;
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ4())) {
//                int i = Integer.parseInt(vote.getQ4());
//                tallCount++;
//                tall += i;
//            }
//            if (Tools.isNotBlank(vote.getQ5())) {
//                int i = Integer.parseInt(vote.getQ5());
//                if (i < 75) {
//                    i = i * 2;
//                }
//                wigthtCount++;
//                wigtht += i;
//            }
//            if (Tools.isNotBlank(vote.getQ6())) {
//                String s[] = vote.getQ6().split(";");
//                for (String str : s) {
//                    if (Tools.isNotBlank(str)) {
//                        if (subMap6.containsKey(str)) {
//                            subMap6.put(str, subMap6.get(str) + 1);
//                        } else {
//                            subMap6.put(str, 1);
//                        }
//                    }
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ7())) {
//                String s[] = vote.getQ7().split(";");
//                for (String str : s) {
//                    if (Tools.isNotBlank(str)) {
//                        if (subMap7.containsKey(str)) {
//                            subMap7.put(str, subMap7.get(str) + 1);
//                        } else {
//                            subMap7.put(str, 1);
//                        }
//                    }
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ8())) {
//                String s[] = vote.getQ8().split(";");
//                for (String str : s) {
//                    if (Tools.isNotBlank(str)) {
//                        if (subMap8.containsKey(str)) {
//                            subMap8.put(str, subMap8.get(str) + 1);
//                        } else {
//                            subMap8.put(str, 1);
//                        }
//                    }
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ13())) {
//                String s[] = vote.getQ13().split(";");
//                for (String str : s) {
//                    if (Tools.isNotBlank(str)) {
//                        if (subMap13.containsKey(str)) {
//                            subMap13.put(str, subMap13.get(str) + 1);
//                        } else {
//                            subMap13.put(str, 1);
//                        }
//                    }
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ14())) {
//                String s[] = vote.getQ14().split(";");
//                for (String str : s) {
//                    if (Tools.isNotBlank(str)) {
//                        if (subMap14.containsKey(str)) {
//                            subMap14.put(str, subMap14.get(str) + 1);
//                        } else {
//                            subMap14.put(str, 1);
//                        }
//                    }
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ15())) {
//                String s[] = vote.getQ15().split(";");
//                for (String str : s) {
//                    if (Tools.isNotBlank(str)) {
//                        if (subMap15.containsKey(str)) {
//                            subMap15.put(str, subMap15.get(str) + 1);
//                        } else {
//                            subMap15.put(str, 1);
//                        }
//                    }
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ16())) {
//                String s[] = vote.getQ16().split(";");
//                for (String str : s) {
//                    if (Tools.isNotBlank(str)) {
//                        if (subMap16.containsKey(str)) {
//                            subMap16.put(str, subMap16.get(str) + 1);
//                        } else {
//                            subMap16.put(str, 1);
//                        }
//                    }
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ9())) {
//                if (subMap9.containsKey(vote.getQ9())) {
//                    subMap9.put(vote.getQ9(), subMap9.get(vote.getQ9()) + 1);
//                } else {
//                    subMap9.put(vote.getQ9(), 1);
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ10())) {
//                if (subMap10.containsKey(vote.getQ10())) {
//                    subMap10.put(vote.getQ10(), subMap10.get(vote.getQ10()) + 1);
//                } else {
//                    subMap10.put(vote.getQ10(), 1);
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ11())) {
//                if (subMap11.containsKey(vote.getQ11())) {
//                    subMap11.put(vote.getQ11(), subMap11.get(vote.getQ11()) + 1);
//                } else {
//                    subMap11.put(vote.getQ11(), 1);
//                }
//            }
//            if (Tools.isNotBlank(vote.getQ12())) {
//                if (subMap12.containsKey(vote.getQ12())) {
//                    subMap12.put(vote.getQ12(), subMap12.get(vote.getQ12()) + 1);
//                } else {
//                    subMap12.put(vote.getQ12(), 1);
//                }
//            }
//        }
//        map.put("总人数", total);
//        map.put("男性", manTotal);
//        map.put("女性", womanToal);
//        map.put("平均年龄", age / ageCount);
//        map.put("平均身高", tall / tallCount);
//        map.put("平均体重", wigtht / wigthtCount);
//        map.put("问题6", subMap6);
//        map.put("问题7", subMap7);
//        map.put("问题8", subMap8);
//        map.put("问题9", subMap9);
//        map.put("问题10", subMap10);
//        map.put("问题11", subMap11);
//        map.put("问题12", subMap12);
//        map.put("问题13", subMap13);
//        map.put("问题14", subMap14);
//        map.put("问题15", subMap15);
//        map.put("问题16", subMap16);
//        return Tools.caseObjectToJson(map);
//    }
    /**
     * 获取用户左侧模块
     *
     * @param auth
     * @return
     * @throws java.lang.Exception
     */
    @GET
    @Path("nav")
    public String nav(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        List<SysMenu> list = adminService.findSysMenuByUserId(user.getId(), 1);
        List<SysMenu> subList = adminService.findSysMenuByUserId(user.getId(), 2);
        for (SysMenu sm : list) {
            em.detach(sm);
            List<SysMenu> dataList = new ArrayList<>();
            for (SysMenu ssm : subList) {
                if (sm.equals(ssm.getParentMenu())) {
                    em.detach(ssm);
                    ssm.setParentMenu(null);
                    dataList.add(ssm);
                }
            }
            sm.setSubSysMenuList(dataList);
        }
        map.put("data", list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获得省份
     *
     * @return
     * @throws Exception
     */
    @GET
    @Path("data_province")
    public String getDataProvince() throws Exception {
        Map map = Tools.getDMap();
        TypedQuery<DataProvince> query = em.createQuery("SELECT d FROM DataProvince d", DataProvince.class);
        map.put("data", query.getResultList());
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取城市
     *
     * @param provinceCode
     * @return
     * @throws Exception
     */
    @GET
    @Path("data_city")
    public String getDataCity(@QueryParam("code") String provinceCode) throws Exception {
        Map map = Tools.getDMap();
        if (Tools.isBlank(provinceCode)) {
            map.put("msg", "数据有误");
            return Tools.caseObjectToJson(map);

        }
        TypedQuery<DataCity> query = em.createQuery("SELECT d FROM DataCity d WHERE d.provinceCode = :provinceCode", DataCity.class
        );
        query.setParameter("provinceCode", provinceCode);
        map.put("data", query.getResultList());
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取区县
     *
     * @param cityCode
     * @return
     * @throws Exception
     */
    @GET
    @Path("data_area")
    public String getDataArea(@QueryParam("code") String cityCode) throws Exception {
        Map map = Tools.getDMap();
        if (Tools.isBlank(cityCode)) {
            map.put("msg", "数据有误");
            return Tools.caseObjectToJson(map);

        }
        TypedQuery<DataCity> query = em.createQuery("SELECT d FROM DataArea d WHERE d.cityCode = :cityCode", DataCity.class
        );
        query.setParameter(
                "cityCode", cityCode);
        map.put(
                "data", query.getResultList());
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取用户信息
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("user_info")
    public String getUserInfo(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map subMap = new HashMap();
        subMap.put("name", user.getName());
        subMap.put("bankCardCode", user.getBankCardCode());
        subMap.put("idCard", user.getIdCard());
        subMap.put("roleId", user.getRoleId());
        map.put("user", subMap);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 日志列表
     *
     * @param auth
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("log_list")
    public String logList(@CookieParam("auth") String auth, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        ResultList<Log> resultList = new ResultList<>();
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(l) FROM Log l ORDER BY l.createDate DESC", Long.class
        );
        Long totalCount = countQuery.getSingleResult();

        resultList.setTotalCount(totalCount.intValue());
        TypedQuery<Log> query = em.createQuery("SELECT l FROM Log l ORDER BY l.createDate DESC", Log.class);
        int startIndex = (pageIndex - 1) * maxPerPage;

        query.setFirstResult(startIndex);

        query.setMaxResults(maxPerPage);

        resultList.setPageIndex(pageIndex);

        resultList.setStartIndex(startIndex);

        resultList.setMaxPerPage(maxPerPage);

        resultList.addAll(query.getResultList());
        map.put(
                "totalCount", resultList.getTotalCount());
        map.put(
                "data", (List) resultList);
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 加载菜单
     *
     * @param auth
     * @param pid
     * @return
     * @throws Exception
     */
    @GET
    @Path("super_menu")
    public String getSuperMenu(@CookieParam("auth") String auth, @QueryParam("pid") Long pid) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        List<SysMenu> list = adminService.findSysMenuListByParentId(pid);
        for (SysMenu sm : list) {
            em.detach(sm);
            sm.setParentMenu(null);
        }
        map.put("data", list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除菜单
     *
     * @param auth
     * @param menuIds
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_menu")
    public String delMenu(@CookieParam("auth") String auth, @FormParam("menu_ids") List<Long> menuIds) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (!SysUserTypeEnum.ROOT.equals(user.getAdminType())) {
            throw new EjbMessageException("无效用户");
        }
        Map map = Tools.getDMap();
        adminService.deleteSysMenuById(menuIds);
        map.put("msg", "删除成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建菜单
     *
     * @param auth
     * @param menuId
     * @param pId
     * @param menuPopedom
     * @param name
     * @param glyphicon
     * @param sref
     * @param sortIndex
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_update_menu")
    public String createMenu(@CookieParam("auth") String auth, @FormParam("id") Long menuId, @FormParam("pid") Long pId, @FormParam("type") String menuPopedom, @FormParam("name") String name, @FormParam("glyphicon") String glyphicon, @FormParam("sref") String sref, @FormParam("index") Integer sortIndex) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        SysMenuPopedomEnum popedom = null;
        try {
            popedom = SysMenuPopedomEnum.valueOf(menuPopedom);
        } catch (Exception e) {
            popedom = SysMenuPopedomEnum.COMMON;
        }
        SysMenu sysMenu = adminService.createOrUpdateSysMenu(menuId, pId, name, glyphicon, popedom, sref, sortIndex);
        map.put("msg", "保存成功！");
        map.put("success", "1");
        map.put("data", sysMenu);
        return Tools.caseObjectToJson(map);
    }

    /**
     * 加载菜单
     *
     * @param auth
     * @param rid
     * @return
     * @throws Exception
     */
    @GET
    @Path("role_power")
    public String getMenuList(@CookieParam("auth") String auth, @QueryParam("rid") Long rid) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        map.put("data", adminService.findSysMenuList(rid));
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 修改密码
     *
     * @param auth
     * @param oldPassword
     * @param newPassword
     * @return
     * @throws Exception
     */
    @POST
    @Path("reset_password")
    public String resetPassword(@CookieParam("auth") String auth, @FormParam("oldPassword") String oldPassword, @FormParam("newPassword") String newPassword) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        adminService.saveLog(user, "修改密码", "");
        Map map = Tools.getDMap();
        if (Tools.isBlank(newPassword) || Tools.isBlank(oldPassword)) {
            map.put("msg", "参数异常！");
            return Tools.caseObjectToJson(map);
        }
        if (!user.getPasswd().equals(Tools.md5(oldPassword))) {
            map.put("msg", "原密码错误！");
            return Tools.caseObjectToJson(map);
        }
        user.setPasswd(Tools.md5(newPassword));
        em.merge(user);
        map.put("data", null);
        map.put("msg", "密码修改成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取角色列表
     *
     * @param auth
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("role_list")
    public String getRoleList(@CookieParam("auth") String auth, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        ResultList<SysRole> resultList = new ResultList<>();
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(sr) FROM SysRole sr ORDER BY sr.sortIndex DESC", Long.class
        );
        Long totalCount = countQuery.getSingleResult();

        resultList.setTotalCount(totalCount.intValue());
        TypedQuery<SysRole> query = em.createQuery("SELECT sr FROM SysRole sr ORDER BY sr.sortIndex DESC", SysRole.class);
        int startIndex = (pageIndex - 1) * maxPerPage;

        query.setFirstResult(startIndex);

        query.setMaxResults(maxPerPage);

        resultList.setPageIndex(pageIndex);

        resultList.setStartIndex(startIndex);

        resultList.setMaxPerPage(maxPerPage);

        resultList.addAll(query.getResultList());
        map.put(
                "totalCount", resultList.getTotalCount());
        map.put(
                "data", (List) resultList);
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建更新角色
     *
     * @param auth
     * @param name
     * @param id
     * @param sortIndex
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_update_role")
    public String createOrUpdateRole(@CookieParam("auth") String auth, @FormParam("name") String name, @FormParam("id") Long id, @FormParam("sortIndex") Integer sortIndex) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("无效用户");
        }
        adminService.saveLog(user, "创建更新角色", "名：" + name);
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(sr) FROM SysRole sr", Long.class
        );
        Long totalCount = countQuery.getSingleResult();
        Map map = Tools.getDMap();
        if (totalCount
                > 9) {
            map.put("msg", "角色已经上限！");
            return Tools.caseObjectToJson(map);
        }
        SysRole role = null;
        if (id
                == null) {
            role = new SysRole();
        } else {
            role = em.find(SysRole.class, id);
        }

        role.setName(name);
        if (sortIndex
                != null) {
            role.setSortIndex(sortIndex);
        }
        if (id
                == null) {
            em.persist(role);
        } else {
            em.merge(role);
        }

        em.flush();

        map.put(
                "msg", "操作成功！");
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建用户
     *
     * @param auth
     * @param name
     * @param email
     * @param id
     * @param account
     * @param passwd
     * @param birthday
     * @param idCard
     * @param mobile
     * @param weChatCode
     * @param qq
     * @param province
     * @param city
     * @param area
     * @param address
     * @param sex
     * @param roleId
     * @param bankType
     * @param bankCardCode
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_update_user")
    public String createOrUpdateUser(@CookieParam("auth") String auth, @FormParam("name") String name, @FormParam("email") String email, @FormParam("id") Long id, @FormParam("account") String account, @FormParam("passwd") String passwd,
            @FormParam("birthday") String birthday, @FormParam("idCard") String idCard, @FormParam("mobile") String mobile, @FormParam("weChatCode") String weChatCode, @FormParam("qq") String qq,
            @FormParam("province") String province, @FormParam("city") String city, @FormParam("area") String area, @FormParam("address") String address, @FormParam("sex") int sex, @FormParam("roleId") Long roleId,
            @FormParam("bankType") String bankType, @FormParam("bankCardCode") String bankCardCode, @FormParam("isFindSelfYearAmount") boolean isFindSelfYearAmount) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        adminService.saveLog(user, "创建更新用户", "账号：" + account + " 身份证：" + idCard);
        Map map = Tools.getDMap();
        Date birthdayDate = null;
        try {
            birthdayDate = Tools.parseDate(birthday, "yyyy-MM-dd");
        } catch (Exception e) {
            birthdayDate = null;
        }
        if (Tools.isBlank(account) || Tools.isBlank(name)) {
            map.put("msg", "参数异常！");
            return Tools.caseObjectToJson(map);
        }
        SysUser targetUser = adminService.createOrUpdateSysUser(id, isFindSelfYearAmount, account, name, passwd, email, sex, birthdayDate, idCard, mobile, weChatCode, qq, province, city, area, address, SysUserStatus.NORMAL, roleId, bankType, bankCardCode);
        map.put("data", targetUser);
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 修改用户余额
     *
     * @param auth
     * @param id
     * @param amount
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_update_user_amount")
    public String createOrUpdateUserAmount(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("amount") String amount) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("无效用户");

        }
        SysUser sysUser = em.find(SysUser.class, id);
        adminService.saveLog(user,
                "修改用户余额", "账号：" + sysUser.getAccount() + " 原来：" + sysUser.getBalance().toString() + " 改成：" + amount);
        sysUser.setBalance(
                new BigDecimal(amount));
        em.merge(sysUser);

        map.put(
                "data", sysUser);
        map.put(
                "msg", "操作成功！");
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除角色
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_role")
    public String delRole(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("无效用户");
        }
        Map map = Tools.getDMap();
        adminService.deleteSysRoleById(ids);
        map.put("msg", "删除成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 审批用户
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("approve_user")
    public String approveUser(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        TypedQuery<SysUser> query = em.createQuery("SELECT s FROM SysUser s WHERE s.id IN :ids", SysUser.class);
        query.setParameter("ids", ids);
        for (SysUser sysUser : query.getResultList()) {
            sysUser.setStatus(SysUserStatus.NORMAL);
            em.merge(sysUser);
            adminService.saveLog(user, "审批用户", "账号：" + sysUser.getAccount());
        }

        map.put("msg", "审批成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取用户列表
     *
     * @param auth
     * @param approve
     * @param search
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("user_list")
    public String getUserList(@CookieParam("auth") String auth, @DefaultValue("0")
            @QueryParam("approve") int approve, @QueryParam("search") String search, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        if (approve == 1) {
            searchMap.put("status", SysUserStatus.PEDING);
        } else {
            searchMap.put("notStatus", SysUserStatus.PEDING);
        }
        searchMap.put("adminType", SysUserTypeEnum.ADMIN);
        ResultList<SysUser> list = adminService.findUserList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 用户订单列表
     *
     * @param auth
     * @param category
     * @param search
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("user_order_list")
    public String getUserOrderList(@CookieParam("auth") String auth, @QueryParam("category") String category, @QueryParam("search") String search, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
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
        searchMap.put("status", OrderStatusEnum.SUCCESS);
        ResultList<GoodsOrder> orderList = adminService.findOrderList(searchMap, pageIndex, maxPerPage, Boolean.TRUE, Boolean.FALSE);
        searchMap.remove("status");
        searchMap.put("notStatus", SysUserStatus.PEDING);
        searchMap.put("adminType", SysUserTypeEnum.ADMIN);
        ResultList<SysUser> list = new ResultList<>();
        if (orderList != null && !orderList.isEmpty()) {
            for (GoodsOrder goodsOrder : orderList) {
                SysUser sysUser = goodsOrder.getAgentUser();
                em.detach(sysUser);
                sysUser.setGoodsMsg(goodsOrder.getGoods().getName());
                sysUser.setGoodsCategory(goodsOrder.getCategoryMean());
                sysUser.setOrderId(goodsOrder.getId());
                list.add(sysUser);
            }
        } else {
            list = adminService.findUserList(searchMap, pageIndex, maxPerPage, Boolean.TRUE, Boolean.FALSE);
        }
        map.put("totalCount", list.size());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 目标用户
     *
     * @param auth
     * @param id
     * @return
     * @throws Exception
     */
    @GET
    @Path("target_user_info")
    public String getUserInfo(@CookieParam("auth") String auth, @QueryParam("id") long id) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        map
                .put("data", em.find(SysUser.class, id));
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 商品列表
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
    @Path("goods_list")
    public String getGoodsList(@CookieParam("auth") String auth, @QueryParam("category") String category, @QueryParam("status") String status, @QueryParam("search") String search, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @QueryParam("orderBy") String orderBy,
            @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        searchMap.put("category", CategoryEnum.valueOf(category));
        if (Tools.isNotBlank(search)) {
            search = search.trim();
            if (search.contains(" ")) {
                String[] searchStr = search.split(" ");
                for (String searchEle : searchStr) {
                    if (searchEle.contains("-")) {
                        String startPeopleCount = searchEle.split("-")[0].trim();
                        String endPeopleCount = searchEle.split("-")[1].trim();
                        try {
                            searchMap.put("searchStartPeopleCount", Integer.parseInt(startPeopleCount));
                            searchMap.put("searchEndPeopleCount", Integer.parseInt(endPeopleCount));
                        } catch (NumberFormatException e) {
                        }
                    } else if (searchEle.trim().endsWith("省")) {
                        searchMap.put("provinceStr", searchEle.trim());
                    } else if (Tools.isNotBlank(searchEle) && !searchMap.containsKey("search")) {
                        searchMap.put("search", searchEle.trim());
                    }
                }
            } else if (search.endsWith("省")) {
                searchMap.put("provinceStr", search);
            } else if (search.contains("-")) {
                String startPeopleCount = search.split("-")[0].trim();
                String endPeopleCount = search.split("-")[1].trim();
                try {
                    searchMap.put("searchStartPeopleCount", Integer.parseInt(startPeopleCount));
                    searchMap.put("searchEndPeopleCount", Integer.parseInt(endPeopleCount));
                } catch (NumberFormatException e) {
                }
            } else {
                searchMap.put("search", search);
            }
        }
        if (Tools.isNotBlank(status)) {
            GoodsStatusEnum statusEnum = null;
            try {
                statusEnum = GoodsStatusEnum.valueOf(status);
            } catch (Exception e) {
                statusEnum = null;
            }
            if (statusEnum != null) {
                searchMap.put("status", statusEnum);
            }
        }
        if (StringUtils.isNotBlank(orderBy) && !"null".equals(orderBy)) {
            searchMap.put("orderBy", orderBy);
        }
        ResultList<Goods> list = adminService.findGoodsList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 分析某一
     *
     * @param auth
     * @param status
     * @param provinceStr
     * @return
     * @throws Exception
     */
    @GET
    @Path("goods_list_province")
    public String getGoodsByProvince(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        TypedQuery<Object[]> queryGoods = em.createQuery("SELECT g.goods.provinceStr,count(g.id),count(DISTINCT(g.agentUser.id)) FROM GoodsOrder g WHERE g.status = :status and g.deleted = false group by g.goods.provinceStr order by count(g.id) desc", Object[].class);
        queryGoods.setParameter("status", OrderStatusEnum.SUCCESS);
        List<Object[]> goodsList = queryGoods.getResultList();
        List<PlaceStatistics> list = new LinkedList<>();
        for (Object[] os : goodsList) {
            PlaceStatistics ps = new PlaceStatistics();
            ps.setGoodsCount((long) os[1]);
            ps.setPlaceName(os[0].toString());
            ps.setOrderCount((long) os[1]);
            ps.setUidCount((long) os[2]);
            list.add(ps);
        }
        map.put("data", list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 商品状态和类别
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("goods_status_and_type_list")
    public String getGoodsTypeList(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        map.put("statusList", GoodsStatusEnum.getList());
        map.put("typeList", GoodsTypeEnum.getList());
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建更新商品
     *
     * @param auth
     * @param name
     * @param id
     * @param type
     * @param price
     * @param uid
     * @param province
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_update_goods")
    public String createOrUpdateGoods(@CookieParam("auth") String auth, @FormParam("category") String category, @FormParam("name") String name, @FormParam("id") Long id, @FormParam("type") String type, @FormParam("price") String price, @FormParam("uid") long uid, @FormParam("province") String province, @FormParam("weChatCode") String weChatCode, @FormParam("qqCode") String qqCode, @FormParam("peopleCount") Integer peopleCount) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        boolean isCreate = true;
        Goods goods = new Goods();

        if (id != null) {
            goods = em.find(Goods.class, id);
            adminService.saveLog(user,
                    "更新商品", "商品号：" + goods.getSerialId() + " 名字：" + goods.getCategoryName() + "-" + name + " 价钱：" + goods.getPrice().toString() + "-" + price + " 人数：" + goods.getPeopleCount() + "-" + peopleCount);
            isCreate = false;

            if (Tools.isNotBlank(weChatCode)) {
                weChatCode = weChatCode.trim();
                if (!weChatCode.equals(goods.getWeChatCode())) {
                    goods.setWeChatCode(weChatCode);
                    GoodsWeChat goodsWeChat = new GoodsWeChat();
                    goodsWeChat.setGoods(goods);
                    goodsWeChat.setWeChatCode(weChatCode);
                    em.persist(goodsWeChat);
                }
            }
        } else {
            goods.setCreateUser(user);
            goods.setWeChatCode(weChatCode);
        }
        goods.setPrice(new BigDecimal(price));
        goods.setProvince(province);
        if (isCreate) {
            goods.setStatus(GoodsStatusEnum.SALE);
        }
        goods.setQqCode(qqCode);
        goods.setCategory(CategoryEnum.valueOf(category));
        goods.setType(GoodsTypeEnum.valueOf(type));
        goods.setProvinceStr(adminService.findProvinceByCode(province).getName());
        goods.setName(name);
        goods.setNamePinyin(Trans2PinYin.trans2PinYinFirst(name));
        goods.setSerialId(adminService.getUniqueGoodsSerialId());
        goods
                .setUser(em.find(SysUser.class, uid));
        goods.setPeopleCount(peopleCount
                == null ? 0 : peopleCount);
        if (isCreate) {
            em.persist(goods);
            em.flush();
            adminService.saveLog(user, "创建商品", "商品号：" + goods.getSerialId() + " 名字：" + goods.getCategoryName() + " 价钱：" + goods.getPrice().toString() + " 人数：" + goods.getPeopleCount());
        } else {
            em.merge(goods);
        }
        if (isCreate
                && Tools.isNotBlank(weChatCode)) {
            GoodsWeChat goodsWeChat = new GoodsWeChat();
            goodsWeChat.setGoods(goods);
            if (Tools.isNotBlank(weChatCode)) {
                weChatCode = weChatCode.trim();
                goodsWeChat.setWeChatCode(weChatCode);
            }
            em.persist(goodsWeChat);
        }

        map.put(
                "data", goods);
        map.put(
                "msg", "操作成功！");
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 订单列表
     *
     * @param auth
     * @param uid
     * @param search
     * @param status
     * @param task
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("order_list")
    public String getOrderList(@CookieParam("auth") String auth, @DefaultValue("false")
            @QueryParam("limitEnd") boolean limitEnd, @QueryParam("orderBy") String orderBy, @QueryParam("category") String category, @QueryParam("user") Boolean userSelf, @QueryParam("uid") Long uid, @DefaultValue("false")
            @QueryParam("task") boolean task, @DefaultValue("false")
            @QueryParam("contract") boolean contract, @QueryParam("status") String status, @QueryParam("search") String search, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        if (Tools.isNotBlank(search)) {
            List list = adminService.findUidByUserName(search);
            if (list != null && !list.isEmpty()) {
                searchMap.put("uids", true);
            }
            searchMap.put("search", search);
        }
        if (userSelf != null && userSelf) {
            searchMap.put("agentUser", user);

        }
        if (uid != null) {
            searchMap.put("agentUser", em.find(SysUser.class, uid));
        }
        if (Tools.isNotBlank(status)) {
            searchMap.put("status", OrderStatusEnum.valueOf(status));
        }
        if (task) {
            searchMap.put("lastPayDate7", Tools.addDay(new Date(), -7));
            if (user.getAdminType().equals(SysUserTypeEnum.ADMIN)) {
                searchMap.put("user", user);
            }
        }
        if (contract) {
            List<OrderStatusEnum> statusList = new ArrayList<>();
            statusList.add(OrderStatusEnum.WAIT_SIGN_CONTRACT);
            statusList.add(OrderStatusEnum.SUCCESS);
            searchMap.put("statuss", statusList);
            searchMap.put("contract", contract);
        }
        if (Tools.isNotBlank(category)) {
            searchMap.put("category", CategoryEnum.valueOf(category));
        }
        if (limitEnd) {
            List<OrderStatusEnum> statusList = new ArrayList<>();
            statusList.add(OrderStatusEnum.WAIT_SIGN_CONTRACT);
            statusList.add(OrderStatusEnum.SUCCESS);
            searchMap.put("statuss", statusList);
            searchMap.put("limitEnd", true);
        }
        if (StringUtils.isNotBlank(orderBy) && !"null".equals(orderBy)) {
            searchMap.put("orderBy", orderBy);
        }
        ResultList<com.shending.entity.GoodsOrder> list = adminService.findOrderList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除合同
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_contract_order")
    public String delContractOrder(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();

        for (Long id : ids) {
            GoodsOrder goodsOrder = em.find(GoodsOrder.class, id);
            if (goodsOrder.getStatus()
                    .equals(OrderStatusEnum.SUCCESS)) {
                //重新计算用户余额
                SysUser agentUser = goodsOrder.getAgentUser();
                adminService.resetUserBalance(agentUser, goodsOrder);
                em.merge(agentUser);
                //删除用户分成
                adminService.deleteWageLogByOrder(goodsOrder);
            }
            Goods goods = goodsOrder.getGoods();

            goodsOrder.setAgentUser(
                    null);
            goodsOrder.setStatus(OrderStatusEnum.WAIT_SIGN_CONTRACT);

            goods.setStatus(GoodsStatusEnum.WAIT_SIGN_CONTRACT);

            em.merge(goods);

            em.merge(goodsOrder);

            adminService.saveLog(user,
                    "删除合同", "订单号：" + goodsOrder.getSerialId());
        }
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建订单
     *
     * @param auth
     * @param id
     * @param goodsId
     * @param recommendId
     * @param userId
     * @param start
     * @param end
     * @param payDate
     * @param orderRecordType
     * @param gatewayType
     * @param amount
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_update_order")
    public String createOrUpdateOrder(@CookieParam("auth") String auth, @FormParam("category") String category, @FormParam("id") Long id, @FormParam("goodsId") Long goodsId, @FormParam("recommendIds") List<Long> recommendIds, @FormParam("recommendOrderIds") List<String> recommendOrderIds, @FormParam("rates") List<String> recommendRates,
            @FormParam("payDate") String payDate, @FormParam("orderRecordType") String orderRecordType, @FormParam("gatewayType") String gatewayType, @FormParam("amount") String amount,
            @FormParam("price") String price, @FormParam("divideAmount") String divideAmount, @FormParam("backAmount") String backAmount, @FormParam("uid") Long uid, @FormParam("remark") String remark, @FormParam("userAmount") String userAmount, @FormParam("divideUserId") Long divideUserId, @FormParam("peopleCountFee") String peopleCountFee,
            @FormParam("franchiseDepartmentCommission") String franchiseDepartmentCommission) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (user.getRoleId() != null && user.getRoleId() == 2l) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        map.put("data", adminService.createGoodsOrder(user, id, goodsId, userAmount, peopleCountFee, category, remark, price, backAmount, recommendIds, divideAmount, recommendOrderIds, recommendRates, divideUserId, amount, payDate, gatewayType, orderRecordType, new BigDecimal(franchiseDepartmentCommission)));
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 修改订单推荐人的提成
     *
     * @param auth
     * @param id
     * @param divideAmount
     * @return
     * @throws Exception
     */
    @POST
    @Path("modify_order_divide_amount")
    public String modifyOrderDivideAmount(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("divideAmount") String divideAmount) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (user.getRoleId() != null && user.getRoleId() == 2l) {
            throw new EjbMessageException("您没有权限");
        }
        adminService.modifyOrderDivideAmount(id, divideAmount);
        Map map = Tools.getDMap();
        map.put("data", null);
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 合同签约
     *
     * @param auth
     * @param id
     * @param userId
     * @param contractSerialId
     * @param lastRenew
     * @return
     * @throws Exception
     */
    @POST
    @Path("sign_contract")
    public String signContract(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("uid") Long userId, @FormParam("contractSerialId") String contractSerialId, @FormParam("lastRenew") String lastRenew) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        GoodsOrder goodsOrder = em.find(GoodsOrder.class, id);
        Goods goods = goodsOrder.getGoods();
        if (id
                == null && !goods.getStatus()
                .equals(GoodsStatusEnum.WAIT_SIGN_CONTRACT)) {
            throw new EjbMessageException("商品的状态错误");
        }

        if (!(goodsOrder.getStatus()
                .equals(OrderStatusEnum.WAIT_SIGN_CONTRACT) || goodsOrder.getStatus().equals(OrderStatusEnum.SUCCESS))) {
            throw new EjbMessageException("商品的状态错误");
        }
        SysUser goodsUser = em.find(SysUser.class, userId);
        SysUser orderGoodsUser = goodsOrder.getAgentUser();

        if (Tools.isNotBlank(contractSerialId)) {
            goodsOrder.setContractSerialId(contractSerialId);
        } else {
            goodsOrder.setContractSerialId(null);
        }
        Date lastRenewDate = null;

        try {
            if (Tools.isNotBlank(lastRenew)) {
                lastRenewDate = Tools.parseDate(lastRenew, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            lastRenewDate = null;
        }

        goodsOrder.setLastRenewDate(lastRenewDate);
        if (lastRenewDate
                != null) {
            //生成记录
            OrderRenew or = new OrderRenew();
            or.setOrderId(goodsOrder.getId());
            or.setRenewDate(lastRenewDate);
            goodsOrder.setLimitEnd(Tools.addYear(lastRenewDate, 1));
            em.persist(or);
        }

        goodsOrder.setAgentUser(goodsUser);

        goodsOrder.setStatus(OrderStatusEnum.SUCCESS);

        goods.setStatus(GoodsStatusEnum.SOLD_OUT);

        em.merge(goodsOrder);

        //增加代理的金额
        if (CategoryEnum.SERVICE_PEOPLE.equals(goodsOrder.getCategory())) {
            goodsUser.setDeposit(goodsUser.getDeposit().add((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))));
            goodsUser.setBalance(goodsUser.getBalance().add((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))).subtract(goodsOrder.getBackAmount()));
            if (orderGoodsUser != null) {
                orderGoodsUser.setDeposit(goodsUser.getDeposit().subtract((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))));
                orderGoodsUser.setBalance(goodsUser.getBalance().subtract((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))).add(goodsOrder.getBackAmount()));
                em.merge(orderGoodsUser);
            }
        } else if (CategoryEnum.MAKE_FRIENDS.equals(goodsOrder.getCategory())) {
            goodsUser.setDeposit(goodsUser.getDeposit().add((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))));
            goodsUser.setBalance(goodsUser.getBalance().add((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))).subtract(goodsOrder.getBackAmount()));
            if (orderGoodsUser != null) {
                orderGoodsUser.setDeposit(goodsUser.getDeposit().subtract((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))));
                orderGoodsUser.setBalance(goodsUser.getBalance().subtract((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))).add(goodsOrder.getBackAmount()));
                em.merge(orderGoodsUser);
            }
        } else {
            throw new EjbMessageException("数据异常");
        }

        em.merge(goodsUser);

        em.merge(goods);

        adminService.saveLog(user,
                "合同签约", "订单号：" + goodsOrder.getSerialId() + " 代理人:" + goodsOrder.getAgentUser().getAccount() + (goodsOrder.getContractSerialId() == null ? "" : " 合同编号:" + goodsOrder.getContractSerialId()));
        map.put(
                "data", goodsOrder);
        map.put(
                "msg", "操作成功！");
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 订单作废
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_order")
    public String delOrder(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (user.getRoleId() != null && user.getRoleId() == 2l) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();

        for (Long id : ids) {
            GoodsOrder goodsOrder = em.find(GoodsOrder.class, id);
            Goods goods = goodsOrder.getGoods();

            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && goodsOrder.getCreateUser() != null && !goodsOrder.getCreateUser().equals(user)) {
                throw new EjbMessageException("您没有权限删除");
            }

            goodsOrder.setDeleted(
                    true);
            goods.setStatus(GoodsStatusEnum.SALE);

            em.merge(goods);

            em.merge(goodsOrder);
            Query deleteQuery = em.createQuery("UPDATE WageLog w SET w.deleted = TRUE WHERE w.goodsOrder = :goodsOrder");

            deleteQuery.setParameter(
                    "goodsOrder", goodsOrder);
            deleteQuery.executeUpdate();

            if (goodsOrder.getStatus()
                    .equals(OrderStatusEnum.SUCCESS) || goodsOrder.getStatus().equals(OrderStatusEnum.WAIT_SIGN_CONTRACT)) {
                Query deleteUserWageQuery = em.createQuery("UPDATE UserWageLog w SET w.deleted = TRUE WHERE w.goodsOrder = :goodsOrder");
                deleteUserWageQuery.setParameter("goodsOrder", goodsOrder);
                deleteUserWageQuery.executeUpdate();
                if (goodsOrder.getStatus().equals(OrderStatusEnum.SUCCESS)) {
                    //重新计算用户余额
                    SysUser agentUser = goodsOrder.getAgentUser();
                    adminService.resetUserBalance(agentUser, goodsOrder);
                    em.merge(agentUser);
                }
                //删除用户分成
                adminService.deleteWageLogByOrder(goodsOrder);
            }

            adminService.saveLog(user,
                    "订单作废", "订单号：" + goodsOrder.getSerialId());
        }
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 回收订单
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("back_order")
    public String backOrder(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();

        for (Long id : ids) {
            GoodsOrder goodsOrder = em.find(GoodsOrder.class, id);
            Goods goods = goodsOrder.getGoods();

            goods.setStatus(GoodsStatusEnum.SALE);

            goods.setQqCode(
                    null);
            goods.setWeChatCode(
                    null);
            goods.setPeopleCount(
                    0);
            em.merge(goods);

            goodsOrder.setStatus(OrderStatusEnum.TERMINATION);

            em.merge(goodsOrder);
            Query deleteQuery = em.createQuery("UPDATE WageLog w SET w.backed = TRUE WHERE w.goodsOrder = :goodsOrder");

            deleteQuery.setParameter(
                    "goodsOrder", goodsOrder);
            deleteQuery.executeUpdate();
            //重新计算用户余额
            SysUser agentUser = goodsOrder.getAgentUser();
            if (agentUser
                    != null) {
                adminService.resetUserBalance(agentUser, goodsOrder);
                em.merge(agentUser);
            }
            //回收AD
            TypedQuery<NewAd> query = em.createQuery("SELECT a FROM NewAd a WHERE a.backed = FALSE AND a.deleted = FALSE AND a.goods = :goods", NewAd.class);

            query.setParameter(
                    "goods", goods);
            List<NewAd> adPushGoodsList = query.getResultList();
            for (NewAd ad : adPushGoodsList) {
                ad.setBacked(Boolean.TRUE);
                em.merge(ad);
            }

            adminService.saveLog(user,
                    "回收订单", "订单号：" + goodsOrder.getSerialId());
        }
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 恢复回收订单
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("back_order_return")
    public String backOrderReturn(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        if (ids != null && ids.size() > 1) {
            throw new EjbMessageException("一次最多恢复一个订单");

        }
        for (Long id : ids) {
            GoodsOrder goodsOrder = em.find(GoodsOrder.class, id);
            if (!goodsOrder.getStatus()
                    .equals(OrderStatusEnum.TERMINATION)) {
                throw new EjbMessageException("恢复回收状态不是回收状态");
            }
            SysUser agentUser = goodsOrder.getAgentUser();
            if (agentUser
                    == null) {
                throw new EjbMessageException("恢复回收的订单没有代理");
            }
            Goods goods = goodsOrder.getGoods();

            if (!goods.getStatus()
                    .equals(GoodsStatusEnum.SALE)) {
                throw new EjbMessageException("要恢复订单的商品不是在售状态");
            }

            goods.setStatus(GoodsStatusEnum.SOLD_OUT);

            em.merge(goods);

            goodsOrder.setStatus(OrderStatusEnum.SUCCESS);

            em.merge(goodsOrder);
            Query deleteQuery = em.createQuery("UPDATE WageLog w SET w.backed = FALSE WHERE w.goodsOrder = :goodsOrder");

            deleteQuery.setParameter(
                    "goodsOrder", goodsOrder);
            deleteQuery.executeUpdate();
            //重新计算用户余额
            if (agentUser
                    != null) {
                if (goodsOrder.getCategory().equals(CategoryEnum.SERVICE_PEOPLE)) {
                    agentUser.setBalance(agentUser.getBalance().add((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))).subtract(goodsOrder.getBackAmount()).compareTo(BigDecimal.ZERO) > 0 ? agentUser.getBalance().add(goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee())).subtract(goodsOrder.getBackAmount()) : BigDecimal.ZERO);
                    agentUser.setDeposit(agentUser.getDeposit().add((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))));
                } else if (goodsOrder.getCategory().equals(CategoryEnum.MAKE_FRIENDS)) {
                    agentUser.setBalance(agentUser.getBalance().add((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))).subtract(goodsOrder.getBackAmount()).compareTo(BigDecimal.ZERO) > 0 ? agentUser.getBalance().add(goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee())).subtract(goodsOrder.getBackAmount()) : BigDecimal.ZERO);
                    agentUser.setDeposit(agentUser.getDeposit().add((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))));
                } else {
                    throw new EjbMessageException("数据异常");
                }
                em.merge(agentUser);
            }
            //恢复AD
            TypedQuery<NewAd> query = em.createQuery("SELECT a FROM NewAd a WHERE a.backed = TRUE AND a.deleted = FALSE AND a.goods = :goods", NewAd.class);

            query.setParameter(
                    "goods", goods);
            List<NewAd> adPushGoodsList = query.getResultList();
            for (NewAd ad : adPushGoodsList) {
                ad.setBacked(Boolean.FALSE);
                em.merge(ad);
            }

            adminService.saveLog(user,
                    "恢复回收订单", "订单号：" + goodsOrder.getSerialId());
        }
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除商品
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_goods")
    public String delGoods(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();

        for (Long id : ids) {
            Goods goods = em.find(Goods.class, id);
            if (goods.getStatus()
                    .equals(GoodsStatusEnum.DRAFT) || goods.getStatus().equals(GoodsStatusEnum.SALE)) {
                if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && goods.getCreateUser() != null && !goods.getCreateUser().equals(user)) {
                    throw new EjbMessageException("您没有权限删除");
                }
                goods.setDeleted(true);
                em.merge(goods);
            }

            adminService.saveLog(user,
                    "删除商品", "订单号：" + goods.getSerialId());
        }
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 订单信息
     *
     * @param auth
     * @param id
     * @return
     * @throws Exception
     */
    @GET
    @Path("order_info")
    public String getOrderInfo(@CookieParam("auth") String auth, @QueryParam("id") Long id) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        GoodsOrder order = em.find(GoodsOrder.class, id);
        TypedQuery<OrderRecord> query = em.createQuery("SELECT o FROM OrderRecord o WHERE o.order = :order", OrderRecord.class);

        query.setParameter(
                "order", order);
        List<OrderRecord> list = query.getResultList();

        map.put(
                "data", order);
        map.put(
                "list", list);
        map.put(
                "total", list.size());
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 修改价钱
     *
     * @param auth
     * @param id
     * @param paidPrice
     * @return
     * @throws Exception
     */
    @POST
    @Path("update_success_order")
    public String updateSuccessOrder(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("paidPrice") String paidPrice) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        GoodsOrder order = em.find(GoodsOrder.class, id);
        adminService.saveLog(user,
                "修改成功订单", "订单号：" + order.getSerialId() + " 支付金额：" + order.getPaidPrice() + " 修改为：" + paidPrice);
        order.setPaidPrice(
                new BigDecimal(paidPrice));
        order.setPrice(
                new BigDecimal(paidPrice));
        em.merge(order);

        map.put(
                "msg", "操作成功！");
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 作废订单并且提成
     *
     * @param auth
     * @param id
     * @param userAmount
     * @param date
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_earnest_order")
    public String deleteEarnestOrder(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("userAmount") String userAmount, @FormParam("date") String date) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");

        }
        GoodsOrder order = em.find(GoodsOrder.class, id);
        if (!order.getStatus()
                .equals(OrderStatusEnum.EARNEST)) {
            throw new EjbMessageException("订单必须是定金状态");
        }

        if (order.getDivideUser()
                == null) {
            throw new EjbMessageException("订单不存在分成大区经理");
        }
        Date payDate = null;

        try {
            if (Tools.isNotBlank(date)) {
                payDate = Tools.parseDate(date, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            payDate = null;
        }
        if (payDate
                == null) {
            throw new EjbMessageException("请输入分成时间");
        }

        //支付超时订单
        order.setStatus(OrderStatusEnum.PAYMENT_TIMEOUT);

        em.merge(order);

        //生成返回大区提成
        UserWageLog userWageLog = new UserWageLog();

        userWageLog.setAmount(
                new BigDecimal(userAmount));
        userWageLog.setGoodsOrder(order);

        userWageLog.setPayDate(payDate);

        userWageLog.setUser(order.getDivideUser());
        userWageLog.setCategory(order.getCategory());
        em.persist(userWageLog);
        Map map = Tools.getDMap();

        adminService.saveLog(user,
                "作废订单并且提成", "订单号：" + order.getSerialId() + " 提成金额：" + order.getPaidPrice() + " 提成时间：" + date);
        map.put(
                "msg", "操作成功！");
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建补充订单
     *
     * @param auth
     * @param orderId
     * @param payDate
     * @param orderRecordType
     * @param gatewayType
     * @param amount
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_order_record")
    public String createOrderRecord(@CookieParam("auth") String auth, @FormParam("orderId") Long orderId, @FormParam("payDate") String payDate, @FormParam("orderRecordType") String orderRecordType, @FormParam("gatewayType") String gatewayType, @FormParam("amount") String amount,
            @FormParam("peopleCountFee") String peopleCountFee) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (user.getRoleId() != null && user.getRoleId() == 2l) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        GoodsOrder order = em.find(GoodsOrder.class, orderId);
        if (Tools.isNotBlank(peopleCountFee)) {
            order.setPeopleCountFee(new BigDecimal(peopleCountFee));
        }

        adminService.saveLog(user,
                "创建补充订单", "订单号：" + order.getSerialId() + " 支付时间：" + payDate + " 支付金额：" + amount + " 人数费：" + Tools.getString(peopleCountFee) + " 金额类型：" + Tools.getString(orderRecordType));
        OrderRecord orderRecord = new OrderRecord();

        orderRecord.setGatewayType(PaymentGatewayTypeEnum.valueOf(gatewayType));
        orderRecord.setPayDate(Tools.parseDate(payDate, "yyyy-MM-dd"));
        orderRecord.setType(OrderRecordTypeEnum.valueOf(orderRecordType));
        orderRecord.setGoods(order.getGoods());
        orderRecord.setOrder(order);

        orderRecord.setPrice(
                new BigDecimal(amount));
        em.persist(orderRecord);

        order.setLastPayDate(Tools.parseDate(payDate, "yyyy-MM-dd"));
        order.setGatewayType(PaymentGatewayTypeEnum.valueOf(gatewayType));
        order.setPaidPrice(order.getPaidPrice().add(new BigDecimal(amount)));
        if (OrderRecordTypeEnum.valueOf(orderRecordType)
                .equals(OrderRecordTypeEnum.FINAL_PAYMENT)) {
            order.setStatus(OrderStatusEnum.WAIT_SIGN_CONTRACT);
            order.setEndDate(new Date());
            order.setLimitEnd(Tools.addYear(Tools.parseDate(payDate, "yyyy-MM-dd"), 1));
            order.setLimitStart(Tools.parseDate(payDate, "yyyy-MM-dd"));
            Goods goods = order.getGoods();
            goods.setStatus(GoodsStatusEnum.WAIT_SIGN_CONTRACT);
            goods.setStatusStartDate(order.getLimitStart());
            goods.setStatusEndDate(order.getLimitEnd());
            em.merge(goods);
            //尾款了
            if (order.getUserAmount().compareTo(BigDecimal.ZERO) > 0 && order.getDivideUser() != null) {
                UserWageLog userWageLog = new UserWageLog();
                userWageLog.setCategory(order.getCategory());
                userWageLog.setAmount(order.getUserAmount());
                userWageLog.setUser(order.getDivideUser());
                userWageLog.setPayDate(order.getLastPayDate());
                userWageLog.setGoodsOrder(order);
                em.persist(userWageLog);
            }
            if (order.getRecommendIdList() != null && !order.getRecommendIdList().isEmpty()) {
                //计算手续费
                order.setFee(order.getPrice().subtract(order.getPaidPrice()));
                for (int i = 0; i < order.getRecommendIdList().size(); i++) {
                    String idStr = order.getRecommendIdList().get(i);
                    String rate = order.getRecommendRateList().get(i);
                    SysUser sysUser = em.find(SysUser.class, Long.parseLong(idStr));
                    CategoryEnum ce = order.getCategory();
                    if (order.getRecommendOrderIds() != null) {
                        String rOrderId = order.getRecommendOrderIdsList().get(i);
                        if (rOrderId != null && !rOrderId.equals("null")) {
                            try {
                                GoodsOrder go = em.find(GoodsOrder.class, Long.parseLong(rOrderId));
                                ce = go.getCategory();
                            } catch (Exception e) {
                                ce = order.getCategory();
                            }
                        }
                    }
                    adminService.createWageLog(order, sysUser, order.getDivideAmount().multiply(new BigDecimal(rate)).divide(new BigDecimal(100), RoundingMode.DOWN).setScale(2, RoundingMode.DOWN), order.getFee().multiply(new BigDecimal(rate)).divide(new BigDecimal(100), RoundingMode.UP).setScale(2, RoundingMode.UP), WageLogTypeEnum.RECOMMEND, order.getLastPayDate(), ce);
                }
            }
            //增加代理的金额
        }

        em.merge(order);

        map.put(
                "msg", "操作成功！");
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 广告列表
     *
     * @param auth
     * @param search
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("ad_list")
    public String getAdList(@CookieParam("auth") String auth, @QueryParam("category") String category, @QueryParam("search") String search, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        if (Tools.isNotBlank(search)) {
            searchMap.put("search", search);
        }
        searchMap.put("category", CategoryEnum.valueOf(category));
        ResultList<NewAd> list = adminService.findAdList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 默认分成大区经理分成金额
     *
     * @param auth
     * @param id
     * @param category
     * @param type
     * @param amount
     * @return
     * @throws Exception
     */
    @POST
    @Path("dic_def_user_amount")
    public String createOrUpdateDicDefUserAmount(@CookieParam("auth") String auth, @FormParam("category") String category, @FormParam("type") String type, @FormParam("amount") String amount) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        DicDefUserAmount ddua = new DicDefUserAmount();
        TypedQuery<DicDefUserAmount> query = em.createQuery("SELECT d FROM DicDefUserAmount d WHERE d.category = :category AND d.type = :type", DicDefUserAmount.class
        );
        query.setParameter(
                "type", type).setParameter("category", category);
        List<DicDefUserAmount> list = query.getResultList();
        if (list
                != null && !list.isEmpty()) {
            ddua = list.get(0);
        }

        ddua.setAmount(
                new BigDecimal(amount));
        ddua.setType(type);

        ddua.setCategory(category);

        if (ddua.getId()
                != null) {
            em.merge(ddua);
            adminService.saveLog(user, "修改默认分成大区经理分成金额", " category：" + Tools.getString(category) + " amount：" + Tools.getString(amount) + " type：" + Tools.getString(type));
        } else {
            em.persist(ddua);
            adminService.saveLog(user, "创建默认分成大区经理分成金额", " category：" + Tools.getString(category) + " amount：" + Tools.getString(amount) + " type：" + Tools.getString(type));
        }

        map.put(
                "data", ddua);
        map.put(
                "success", "1");
        map.put(
                "msg", "操作成功！");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取DicDefUserAmount
     *
     * @param auth
     * @param category
     * @param type
     * @return
     * @throws Exception
     */
    @GET
    @Path("dic_def_user_amount")
    public String getDicDefUserAmount(@CookieParam("auth") String auth, @QueryParam("category") String category, @QueryParam("type") String type) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        DicDefUserAmount ddua = null;
        TypedQuery<DicDefUserAmount> query = em.createQuery("SELECT d FROM DicDefUserAmount d WHERE d.category = :category AND d.type = :type", DicDefUserAmount.class
        );
        query.setParameter(
                "type", type).setParameter("category", category);
        List<DicDefUserAmount> list = query.getResultList();
        if (list
                != null && !list.isEmpty()) {
            ddua = list.get(0);
        }

        map.put(
                "data", ddua);
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 获取DicDefUserAmount列表
     *
     * @param auth
     * @param category
     * @return
     * @throws Exception
     */
    @GET
    @Path("dic_def_user_amount_list")
    public String getDicDefUserAmountList(@CookieParam("auth") String auth, @QueryParam("category") String category) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        TypedQuery<DicDefUserAmount> query = em.createQuery("SELECT d FROM DicDefUserAmount d WHERE d.category = :category", DicDefUserAmount.class
        );
        query.setParameter(
                "category", category);
        map.put(
                "data", query.getResultList());
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 广告作废
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_ad")
    public String delAd(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (user.getRoleId() != null && user.getRoleId() == 2l) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();

        for (Long id : ids) {
            NewAd ad = em.find(NewAd.class, id);
            if (!SysUserTypeEnum.SUPER.equals(user.getAdminType()) && ad.getCreateUser() != null && !ad.getCreateUser().equals(user)) {
                throw new EjbMessageException("您没有权限删除");
            }

            ad.setDeleted(true);
            SysUser sysUser = ad.getUser();//分成人

            if (ad.getCategory().equals(CategoryEnum.SERVICE_PEOPLE)) {
                sysUser.setBalance(sysUser.getBalance().add(ad.getUserBalanceAmount()));
            } else if (ad.getCategory().equals(CategoryEnum.MAKE_FRIENDS)) {
                sysUser.setBalance(sysUser.getBalance().add(ad.getUserBalanceAmount()));
            } else {
                throw new EjbMessageException("数据异常");
            }

            em.merge(ad);

            em.merge(sysUser);

            adminService.saveLog(user, "广告作废", " id：" + ad.getId() + " 名字：" + Tools.getString(ad.getName()));
        }
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除用户
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_user")
    public String delUser(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("您必须是超级管理员");
        }
        Map map = Tools.getDMap();

        for (Long id : ids) {
            SysUser sysUser = em.find(SysUser.class, id);
            sysUser.setDeleted(
                    true);
            em.merge(sysUser);

            adminService.saveLog(user,
                    "删除用户", " 账号：" + sysUser.getAccount());
        }
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 广告详情
     *
     * @param auth
     * @param id
     * @return
     * @throws Exception
     */
    @GET
    @Path("ad_info")
    public String getAdInfo(@CookieParam("auth") String auth, @QueryParam("id") Long id) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        NewAd ad = em.find(NewAd.class, id);
        map.put(
                "data", ad);
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 商品详情
     *
     * @param auth
     * @param id
     * @return
     * @throws Exception
     */
    @GET
    @Path("goods_info")
    public String getGoodsInfo(@CookieParam("auth") String auth, @QueryParam("id") Long id) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Goods goods = em.find(Goods.class, id);
        GoodsOrder order = adminService.findOrderByOrder(goods);
        TypedQuery<GoodsWeChat> query = em.createQuery("SELECT gwc FROM GoodsWeChat gwc WHERE gwc.goods = :goods AND gwc.deleted = FALSE ORDER BY gwc.createDate DESC", GoodsWeChat.class);

        query.setParameter("goods", goods);
        List<GoodsWeChat> list = query.getResultList();

        map.put("data", goods);
        map.put("order", order);
        map.put("list", list);
        map.put("total", list.size());
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 工资详情
     *
     * @param auth
     * @param start
     * @param end
     * @param search
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("wage_list")
    public String getWageList(@CookieParam("auth") String auth, @QueryParam("category") String category, @QueryParam("start") String start, @QueryParam("end") String end, @QueryParam("search") String search, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Date startDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                startDate = Tools.parseDate(start, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            startDate = null;
        }
        Date endDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                endDate = Tools.parseDate(end, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            endDate = null;
        }
        Map searchMap = new HashMap();
        if (Tools.isNotBlank(search)) {
            searchMap.put("search", search);
        }
        Query countQuery = em.createQuery("SELECT COUNT(DISTINCT(a.user)) FROM NewAd a WHERE a.category = :category AND a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate");
        countQuery.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 1)).setParameter("category", CategoryEnum.valueOf(category));
        Long totalCount = (Long) countQuery.getSingleResult();
        String sql = "SELECT a.user.name,a.user.balance,a.user.deposit,a.user.bankType,a.user.bankCardCode,SUM(a.userAmount),SUM(a.userBalanceAmount),a.user.id FROM NewAd a WHERE a.category = :category AND a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user ";
        if (CategoryEnum.valueOf(category).equals(CategoryEnum.MAKE_FRIENDS)) {
            sql = "SELECT a.user.name,a.user.balance,a.user.deposit,a.user.bankType,a.user.bankCardCode,SUM(a.userAmount),SUM(a.userBalanceAmount),a.user.id FROM NewAd a WHERE a.category = :category AND a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user ";
        }
        Query query = em.createQuery(sql);
        int startIndex = (pageIndex - 1) * maxPerPage;
        query.setFirstResult(startIndex);
        query.setMaxResults(maxPerPage);
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 1)).setParameter("category", CategoryEnum.valueOf(category));
        List<String[]> list = new ArrayList<>();
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[9];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            str[4] = os[4] == null ? "" : os[4].toString();
            str[5] = os[5].toString();
            str[6] = os[6].toString();
            str[7] = Double.parseDouble(str[5]) + Double.parseDouble(str[6]) + "";
            str[8] = os[7].toString();
            list.add(str);
        }
        map.put("totalCount", totalCount);
        map.put("data", list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 加盟工资
     *
     * @param auth
     * @param start
     * @param end
     * @param category
     * @param search
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("order_wage_list")
    public String getOrderWageList(@CookieParam("auth") String auth, @QueryParam("category") String category, @QueryParam("start") String start, @QueryParam("end") String end, @QueryParam("search") String search, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Date startDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                startDate = Tools.parseDate(start, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            startDate = null;
        }
        Date endDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                endDate = Tools.parseDate(end, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            endDate = null;
        }
        Map searchMap = new HashMap();
        String countSql = "SELECT COUNT(DISTINCT(w.user)) FROM WageLog w WHERE w.category = :category AND w.deleted = FALSE AND w.goodsOrder IS NOT NULL AND w.payDate > :startDate AND w.payDate < :endDate";
        String sql = "SELECT w.user.id,w.user.name,SUM(w.amount)-SUM(w.fee) FROM WageLog w WHERE w.category = :category AND w.deleted = FALSE AND w.goodsOrder IS NOT NULL AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user";
        if (Tools.isBlank(category)) {
            countSql = "SELECT COUNT(DISTINCT(w.user)) FROM WageLog w WHERE w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate";
            sql = "SELECT w.user.id,w.user.name,SUM(w.amount)-SUM(w.fee) FROM WageLog w WHERE w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user";
        }
        Query countQuery = em.createQuery(countSql);
        countQuery.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 1));
        if (Tools.isNotBlank(category)) {
            countQuery.setParameter("category", CategoryEnum.valueOf(category));
        }
        Long totalCount = (Long) countQuery.getSingleResult();
        Query query = em.createQuery(sql);
        int startIndex = (pageIndex - 1) * maxPerPage;
        query.setFirstResult(startIndex);
        query.setMaxResults(maxPerPage);
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 1));
        if (Tools.isNotBlank(category)) {
            query.setParameter("category", CategoryEnum.valueOf(category));
        }
        List<String[]> list = new ArrayList<>();
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[3];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            list.add(str);
        }
        map.put("totalCount", totalCount);
        map.put("data", list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 用户工资详情
     *
     * @param auth
     * @param uid
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("user_wage_info")
    public String getUserWageInfo(@CookieParam("auth") String auth, @QueryParam("uid") Long uid, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Date startDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                startDate = Tools.parseDate(start, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            startDate = null;
        }
        Date endDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                endDate = Tools.parseDate(end, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            endDate = null;

        }
        SysUser sysUser = em.find(SysUser.class, uid);

        //订单部分
        TypedQuery<WageLog> wageQuery = em.createQuery("SELECT w FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.payDate <= :endDate AND w.payDate >= :startDate AND w.type = :type ORDER BY w.payDate DESC", WageLog.class);

        wageQuery.setParameter("startDate", startDate).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", sysUser).setParameter("type", WageLogTypeEnum.RECOMMEND);
        List<WageLog> wageList = wageQuery.getResultList();

        //广告部分
        Query countQuery = em.createQuery("SELECT COUNT(w) FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.payDate <= :endDate AND w.payDate >= :startDate AND w.type = :type");

        countQuery.setParameter("startDate", startDate).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", sysUser).setParameter("type", WageLogTypeEnum.ACCEPT);
        Long totalCount = (Long) countQuery.getSingleResult();
        TypedQuery<WageLog> query = em.createQuery("SELECT w FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.payDate <= :endDate AND w.payDate >= :startDate AND w.type = :type ORDER BY w.payDate DESC", WageLog.class);
        int startIndex = (pageIndex - 1) * maxPerPage;

        query.setFirstResult(startIndex);

        query.setMaxResults(maxPerPage);

        query.setParameter("startDate", startDate).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", sysUser).setParameter("type", WageLogTypeEnum.ACCEPT);
        List<WageLog> list = query.getResultList();

        map.put("totalCount", totalCount);
        map.put("data", list);
        map.put("wageList", wageList.size() < 1 ? null : wageList);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 用户工资详情
     *
     * @param auth
     * @param uid
     * @param start
     * @param end
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("user_wage_ad_info")
    public String getUserWageAdInfo(@CookieParam("auth") String auth, @QueryParam("uid") Long uid, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Date startDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                startDate = Tools.parseDate(start, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            startDate = null;
        }
        Date endDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                endDate = Tools.parseDate(end, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            endDate = null;

        }
        SysUser sysUser = em.find(SysUser.class, uid);

        //广告部分
        Query countQuery = em.createQuery("SELECT COUNT(w) FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.payDate <= :endDate AND w.payDate >= :startDate AND w.type = :type");

        countQuery.setParameter(
                "startDate", startDate).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", sysUser).setParameter("type", WageLogTypeEnum.SEND);
        Long totalCount = (Long) countQuery.getSingleResult();
        TypedQuery<WageLog> query = em.createQuery("SELECT w FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.payDate <= :endDate AND w.payDate >= :startDate AND w.type = :type ORDER BY w.payDate DESC", WageLog.class);
        int startIndex = (pageIndex - 1) * maxPerPage;

        query.setFirstResult(startIndex);

        query.setMaxResults(maxPerPage);

        query.setParameter("startDate", startDate).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", sysUser).setParameter("type", WageLogTypeEnum.SEND);
        List list = query.getResultList();

        map.put("totalCount", totalCount);
        map.put("data", list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 保存权限
     *
     * @param auth
     * @param rid
     * @param mids
     * @return
     * @throws Exception
     */
    @POST
    @Path("save_power")
    public String savePower(@CookieParam("auth") String auth, @FormParam("rid") Long rid, @FormParam("mids") List<Long> mids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("无效用户");
        }
        adminService.createOrUpdateSysRoleMenu(rid, mids);
        Map map = Tools.getDMap();
        map.put("data", adminService.findSysMenuList(rid));
        map.put("msg", "保存成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 修改订单日期
     *
     * @param auth
     * @param id
     * @param payDate
     * @return
     * @throws Exception
     */
    @POST
    @Path("modify_order_date")
    public String modifyOrderDate(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("payDate") String payDate) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("无效用户");
        }
        Map map = Tools.getDMap();
        GoodsOrder order = em.find(GoodsOrder.class, id);
        if (!(OrderStatusEnum.SUCCESS.equals(order.getStatus()) || OrderStatusEnum.WAIT_SIGN_CONTRACT.equals(order.getStatus()))) {
            throw new EjbMessageException("订单不是完成状态！");
        }

        adminService.saveLog(user,
                "修改订单日期", " 订单号：" + order.getSerialId() + " 日期：" + Tools.getString(Tools.formatDate(order.getLastPayDate(), "yyyy-MM-dd")) + "-" + Tools.getString(payDate));
        Goods goods = order.getGoods();
        Date endDate = Tools.parseDate(payDate, "yyyy-MM-dd");

        order.setLimitEnd(Tools.addYear(endDate, 1));
        order.setLimitStart(endDate);

        order.setEndDate(endDate);

        order.setLastPayDate(endDate);
        TypedQuery<OrderRecord> typedQuery = em.createQuery("SELECT o FROM OrderRecord o WHERE o.order = :order ORDER BY o.createDate DESC", OrderRecord.class);

        typedQuery.setParameter(
                "order", order);
        List<OrderRecord> orderRecordList = typedQuery.getResultList();
        if (orderRecordList
                != null && !orderRecordList.isEmpty()) {
            OrderRecord orderRecord = orderRecordList.get(0);
            orderRecord.setPayDate(endDate);
            em.merge(orderRecord);
        }

        goods.setStatusStartDate(order.getLimitStart());
        goods.setStatusEndDate(order.getLimitEnd());
        em.merge(goods);

        em.merge(order);

        map.put(
                "data", order);
        map.put(
                "msg", "保存成功！");
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 个人信息
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("my_info")
    public String getMyInfo(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        map.put("data", user);
        map.put("msg", "保存成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 合同到期提示列表
     *
     * @param auth
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("order_task_list")
    public String orderTaskList(@CookieParam("auth") String auth, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);

        Map map = Tools.getDMap();
        map.put("data", user);
        map.put("msg", "保存成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 修改用户信息
     *
     * @param auth
     * @param id
     * @param account
     * @param email
     * @param sex
     * @param name
     * @param bankType
     * @param idCard
     * @param weChatCode
     * @param mobile
     * @param bankCardCode
     * @return
     * @throws Exception
     */
    @POST
    @Path("my_info")
    public String setMyInfo(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("account") String account, @FormParam("email") String email, @FormParam("sex") Integer sex, @FormParam("name") String name, @FormParam("bankType") String bankType, @FormParam("idCard") String idCard, @FormParam("weChatCode") String weChatCode, @FormParam("mobile") String mobile, @FormParam("bankCardCode") String bankCardCode) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (!user.getAccount().equals(account)) {
            if (adminService.findByAccount(account) != null) {
                throw new EjbMessageException("帐号已经存在");
            }
            user.setAccount(account);
        }
        if (user.getIdCard() == null || !user.getIdCard().equals(idCard)) {
            if (idCard != null && adminService.findSysUserByIdCardCount(idCard) > 0) {
                throw new EjbMessageException("身份证号已经存在");
            }
            user.setIdCard(idCard);
        }
        if (Tools.isNotBlank(bankType) && Tools.isContainNumber(bankType)) {
            throw new EjbMessageException("所属银行不能包含数字");
        }
        user.setEmail(email);
        user.setName(name);
        user.setSex(sex);
        user.setBankCardCode(bankCardCode);
        user.setBankType(bankType);
        user.setWeChatCode(weChatCode);
        user.setMobile(mobile);
        em.merge(user);
        adminService.saveLog(user, "修改自己信息", "");
        Map map = Tools.getDMap();
        map.put("data", null);
        map.put("msg", "保存成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 创建更新广告
     *
     * @param auth
     * @param id
     * @param goodsId
     * @param amount
     * @param payDate
     * @param limitType
     * @param name
     * @param ownerWeChat
     * @param gatewayType
     * @param userBalanceAmount
     * @param userAmount
     * @param adLevel
     * @param remark
     * @param sendType
     * @return
     * @throws Exception
     */
    @POST
    @Path("create_update_new_ad")
    public String createUpdateNewAd(@CookieParam("auth") String auth, @FormParam("category") String category, @FormParam("id") Long id, @FormParam("goodsId") Long goodsId, @FormParam("amount") String amount, @FormParam("payDate") String payDate, @FormParam("limitType") String limitType,
            @FormParam("name") String name, @FormParam("ownerWeChat") String ownerWeChat, @FormParam("gatewayType") String gatewayType, @FormParam("userBalanceAmount") String userBalanceAmount,
            @FormParam("userAmount") String userAmount, @FormParam("adLevel") String adLevel, @FormParam("remark") String remark, @FormParam("sendType") String sendType, @FormParam("categoryPlus") String categoryPlus) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (user.getRoleId() != null && user.getRoleId() == 2l) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        adminService.saveLog(user, "创建更新广告", " category：" + Tools.getString(category) + " amount：" + Tools.getString(amount) + " payDate：" + Tools.getString(payDate) + " limitType：" + Tools.getString(limitType) + " name：" + Tools.getString(name)
                + " ownerWeChat：" + Tools.getString(ownerWeChat) + " gatewayType：" + Tools.getString(gatewayType) + " userBalanceAmount：" + Tools.getString(userBalanceAmount) + " userAmount：" + Tools.getString(userAmount)
                + " adLevel：" + Tools.getString(adLevel) + " remark：" + Tools.getString(remark) + " sendType：" + Tools.getString(sendType) + " categoryPlus：" + Tools.getString(categoryPlus) + "goodsId:" + (goodsId == null ? "" : goodsId));
        map.put("data", adminService.createOrUpdateNewAd(id, em.find(Goods.class, goodsId), new BigDecimal(amount), Tools.parseDate(payDate, "yyyy-MM-dd"), AdLimitTypeEnum.valueOf(limitType), name, ownerWeChat, PaymentGatewayTypeEnum.valueOf(gatewayType), user, new BigDecimal(userBalanceAmount), new BigDecimal(userAmount), AdLevelEnum.valueOf(adLevel), remark, WageLogTypeEnum.valueOf(sendType), CategoryEnum.valueOf(category), categoryPlus));
        map.put("msg", "保存成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 我的推荐订单
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
    @Path("my_new_order_list")
    public String myNewOrderList(@CookieParam("auth") String auth, @QueryParam("search") String search, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        searchMap.put("type", WageLogTypeEnum.RECOMMEND);
        searchMap.put("user", user);
        if (Tools.isNotBlank(search)) {
            searchMap.put("search", search);
        }
        String sql = "SELECT SUM(w.amount) FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.type = :type";
        Date startDate = Tools.getBeginOfYear(new Date());
        if (Tools.isNotBlank(start)) {
            startDate = Tools.parseDate(start, "yyyy-MM-dd");
            if (startDate.before(Tools.getBeginOfYear(new Date()))) {
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
        queryTotal.setParameter("user", user).setParameter("type", WageLogTypeEnum.RECOMMEND);
        queryTotal.setParameter("start", Tools.addDay(startDate, -1));
        if (Tools.isNotBlank(end)) {
            queryTotal.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
        }
        ResultList<WageLog> list = adminService.findWageLogList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("isFindSelfYearAmount", user.isIsFindSelfYearAmount());
        map.put("totalCount", list.getTotalCount());
        map.put("totalAmount", queryTotal.getResultList().isEmpty() ? null : queryTotal.getSingleResult());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    @GET
    @Path("my_amount_list")
    public String myAmountList(@CookieParam("auth") String auth, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        //年份 2015 2016 2017 2018 2019
        Integer[] years = {2015, 2016, 2017, 2018, 2019};
        List<WageLogTypeEnum> types = new ArrayList<>();
        types.add(WageLogTypeEnum.RECOMMEND);
        types.add(WageLogTypeEnum.PRODUCT);
        types.add(WageLogTypeEnum.GRAND_SLAM);
        searchMap.put("user", user);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer year : years) {
            String sql = "SELECT SUM(w.amount) FROM WageLog w WHERE w.user = :user AND w.deleted = FALSE AND w.type in :types AND w.payDate > :start AND w.payDate < :end GROUP BY w.user";
            Date start = Tools.parseDate(year + "-01-01", "yyyy-MM-dd");
            Date end = Tools.parseDate((year + 1) + "-01-01", "yyyy-MM-dd");
            Query queryTotal = em.createQuery(sql);
            queryTotal.setParameter("user", user).setParameter("types", types).setParameter("start", start).setParameter("end", end);
            BigDecimal total = BigDecimal.ZERO;
            if (!queryTotal.getResultList().isEmpty()) {
                total = (BigDecimal) queryTotal.getSingleResult();
            }
            String sql2 = "SELECT SUM(w.userAmount + w.userBalanceAmount) FROM NewAd w WHERE w.user = :user AND w.deleted = FALSE AND w.payDate > :start AND w.payDate < :end GROUP BY w.user";
            Query queryTota2 = em.createQuery(sql2);
            queryTota2.setParameter("user", user).setParameter("start", start).setParameter("end", end);
            BigDecimal total2 = BigDecimal.ZERO;
            if (!queryTota2.getResultList().isEmpty()) {
                total2 = (BigDecimal) queryTota2.getSingleResult();
            }
            if ((total.add(total2)).compareTo(BigDecimal.ZERO) > 0) {
                Map returnMap = new HashMap();
                returnMap.put("year", year);
                returnMap.put("total", total.add(total2));
                list.add(returnMap);
            }
        }
        map.put("data", list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 我的推荐广告
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
    @Path("my_new_ad_list")
    public String myNewAdList(@CookieParam("auth") String auth, @QueryParam("search") String search, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        searchMap.put("notZero", true);
        searchMap.put("user", user);
        if (Tools.isNotBlank(search)) {
            searchMap.put("search", search);
        }
        String sql = "SELECT SUM(w.userAmount + w.userBalanceAmount) FROM NewAd w WHERE w.user = :user AND w.deleted = FALSE";
        Date startDate = Tools.getBeginOfYear(new Date());
        if (Tools.isNotBlank(start)) {
            startDate = Tools.parseDate(start, "yyyy-MM-dd");
            if (startDate.before(Tools.getBeginOfYear(new Date()))) {
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
        queryTotal.setParameter("user", user);
        queryTotal.setParameter("start", Tools.addDay(startDate, -1));
        if (Tools.isNotBlank(end)) {
            queryTotal.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
        }
        ResultList<NewAd> list = adminService.findAdList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("totalAmount", queryTotal.getResultList().isEmpty() ? null : queryTotal.getSingleResult());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 大区统计
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
    @Path("region_list")
    public String regionNewAdList(@CookieParam("auth") String auth, @QueryParam("search") String search, @QueryParam("start") String start, @QueryParam("end") String end, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        List list = new LinkedList<>();
        String sql = "SELECT go.user.id,go.user.name,COUNT(go.goods),1,SUM(go.paidPrice) FROM GoodsOrder go WHERE go.deleted = FALSE AND go.status IN :statusList";
        if (Tools.isNotBlank(start)) {
            sql += " AND go.lastPayDate > :start";
        }
        if (Tools.isNotBlank(end)) {
            sql += " AND go.lastPayDate < :end";
        }
        sql += " GROUP BY go.user ORDER by SUM(go.paidPrice) DESC";
        Query queryTotal = em.createQuery(sql);
        List statusList = new ArrayList<>();
        statusList.add(OrderStatusEnum.SUCCESS);
        statusList.add(OrderStatusEnum.WAIT_SIGN_CONTRACT);
        queryTotal.setParameter("statusList", statusList);
        if (Tools.isNotBlank(start)) {
            queryTotal.setParameter("start", Tools.addDay(Tools.parseDate(start, "yyyy-MM-dd"), -1));
        }
        if (Tools.isNotBlank(end)) {
            queryTotal.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
        }
        for (Object o : queryTotal.getResultList()) {
            Object[] os = (Object[]) o;
            Long uid = (Long) os[0];
            String sqlq = "SELECT COUNT(na.id),SUM(na.amount) FROM NewAd na WHERE na.deleted = FALSE AND na.goods.user.id = :uid ";
            String sqlAgentUserCount = "SELECT COUNT(DISTINCT(go.agentUser)) FROM GoodsOrder go WHERE go.deleted = FALSE AND go.user.id = :uid ";
            if (Tools.isNotBlank(start)) {
                sqlq += " AND na.payDate > :start";
                sqlAgentUserCount += " AND go.lastPayDate > :start";
            }
            if (Tools.isNotBlank(end)) {
                sqlq += " AND na.payDate < :end";
                sqlAgentUserCount += " AND go.lastPayDate < :end";
            }
            sqlq += " GROUP BY na.goods.user";
            sqlAgentUserCount += " GROUP BY go.user";
            Query query = em.createQuery(sqlq);
            Query queryAgentUserCount = em.createQuery(sqlAgentUserCount);
            if (Tools.isNotBlank(start)) {
                query.setParameter("start", Tools.addDay(Tools.parseDate(start, "yyyy-MM-dd"), -1));
                queryAgentUserCount.setParameter("start", Tools.addDay(Tools.parseDate(start, "yyyy-MM-dd"), -1));
            }
            if (Tools.isNotBlank(end)) {
                query.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
                queryAgentUserCount.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
            }
            query.setParameter("uid", uid);
            queryAgentUserCount.setParameter("uid", uid);
            Object adCount = query.getResultList().isEmpty() ? null : ((Object[]) query.getSingleResult())[0];
            Object adAmount = query.getResultList().isEmpty() ? null : ((Object[]) query.getSingleResult())[1];
            Object agentUserCount = queryAgentUserCount.getResultList().isEmpty() ? 0 : queryAgentUserCount.getSingleResult();
            Query goodsTotalQuery = em.createQuery("SELECT COUNT(g.id) FROM Goods g WHERE g.deleted = FALSE AND g.user.id = :uid");
            goodsTotalQuery.setParameter("uid", uid);
            Map subMap = new HashMap();
            subMap.put("uid", os[0]);
            subMap.put("username", os[1]);
            subMap.put("goodsCount", os[2]);
            subMap.put("agentUserCount", agentUserCount);
            subMap.put("orderAmount", os[4]);
            subMap.put("adCount", adCount);
            subMap.put("adAmount", adAmount);
            subMap.put("goodsTotalCount", goodsTotalQuery.getSingleResult());
            list.add(subMap);
        }
        if (Tools.isNotBlank(start)) {
            queryTotal.setParameter("start", Tools.addDay(Tools.parseDate(start, "yyyy-MM-dd"), -1));
        }
        if (Tools.isNotBlank(end)) {
            queryTotal.setParameter("end", Tools.addDay(Tools.parseDate(end, "yyyy-MM-dd"), 1));
        }
        map.put("data", list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 更新订单
     *
     * @return
     * @throws Exception
     */
//    @POST
//    @Path("update_order")
//    public String updateOrder(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("goodsId") Long goodsId, @FormParam("recommendIds") List<Long> recommendIds, @FormParam("rates") List<String> recommendRates,
//            @FormParam("payDate") String payDate, @FormParam("orderRecordType") String orderRecordType, @FormParam("gatewayType") String gatewayType, @FormParam("amount") String amount,
//            @FormParam("price") String price, @FormParam("divideAmount") String divideAmount, @FormParam("backAmount") String backAmount, @FormParam("uid") Long uid, @FormParam("remark") String remark) throws Exception {
//        SysUser user = adminService.getUserByLoginCode(auth);
//        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
//            throw new EjbMessageException("您没有权限");
//        }
//        Map map = Tools.getDMap();
//        boolean isCreate = true;
//        GoodsOrder goodsOrder = new GoodsOrder();
//        goodsOrder.setCreateUser(user);
//        if (id != null) {
//            goodsOrder = em.find(GoodsOrder.class, id);
//            isCreate = false;
//        }
//        Goods goods = em.find(Goods.class, goodsId);
//        if (id == null && !goods.getStatus().equals(GoodsStatusEnum.SALE)) {
//            throw new EjbMessageException("商品的状态错误");
//        }
//        goodsOrder.setRemark(remark);
//        goodsOrder.setGoods(goods);
//        goodsOrder.setUser(goods.getUser());
//        goodsOrder.setGoodsMsg(goods.getProvinceStr() + "_" + goods.getName() + "_" + goods.getUser().getId() + "_" + goods.getUser().getName());
//        goodsOrder.setGoodsPinyin(Trans2PinYin.trans2PinYinFirst(goods.getName()));
//        goodsOrder.setPrice(new BigDecimal(price));
//        goodsOrder.setBackAmount(Tools.isBlank(backAmount) ? BigDecimal.ZERO : new BigDecimal(backAmount));
//        goodsOrder.setSerialId(adminService.getUniqueOrderSerialId());
//        goodsOrder.setDivideAmount(BigDecimal.ZERO);
//        if (recommendIds != null && recommendIds.size() > 0) {
//            goodsOrder.setDivideAmount(new BigDecimal(divideAmount));
//            if (recommendIds.size() == 1) {
//                SysUser recommendUser = em.find(SysUser.class, recommendIds.get(0));
//                goodsOrder.setRecommendIds(recommendUser.getId().toString());
//                goodsOrder.setRecommendNames(recommendUser.getName());
//                goodsOrder.setRecommendRates("100");
//            } else {
//                String ids = "";
//                String names = "";
//                String rates = "";
//                for (int i = 0; i < recommendIds.size(); i++) {
//                    if (i == 0) {
//                        SysUser recommendUser = em.find(SysUser.class, recommendIds.get(i));
//                        ids = recommendUser.getId().toString();
//                        names = recommendUser.getName();
//                        rates = recommendRates.get(i);
//                    } else {
//                        SysUser recommendUser = em.find(SysUser.class, recommendIds.get(i));
//                        ids = ids + ";" + recommendUser.getId().toString();
//                        names = names + ";" + recommendUser.getName();
//                        rates = rates + ";" + recommendRates.get(i);
//                    }
//                }
//                goodsOrder.setRecommendIds(ids);
//                goodsOrder.setRecommendNames(names);
//                goodsOrder.setRecommendRates(rates);
//            }
//        }
//        goodsOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT);
//        goods.setStatus(GoodsStatusEnum.LOCKED);
//        goods.setStatusStartDate(new Date());
//        goods.setStatusEndDate(Tools.addMinute(new Date(), 30));
//        if (Tools.isNotBlank(amount) && isCreate && Tools.isNotBlank(payDate) && Tools.isNotBlank(gatewayType) && Tools.isNotBlank(orderRecordType)) {
//            goodsOrder.setPaidPrice(new BigDecimal(amount));
//            goodsOrder.setLastPayDate(Tools.parseDate(payDate, "yyyy-MM-dd"));
//            goodsOrder.setGatewayType(PaymentGatewayTypeEnum.valueOf(gatewayType));
//            if (OrderRecordTypeEnum.valueOf(orderRecordType).equals(OrderRecordTypeEnum.FINAL_PAYMENT)) {
//                goodsOrder.setStatus(OrderStatusEnum.WAIT_SIGN_CONTRACT);
//                goodsOrder.setLimitEnd(Tools.addYear(Tools.parseDate(payDate, "yyyy-MM-dd"), 1));
//                goodsOrder.setLimitStart(Tools.parseDate(payDate, "yyyy-MM-dd"));
//                goodsOrder.setEndDate(new Date());
//                goods.setStatus(GoodsStatusEnum.WAIT_SIGN_CONTRACT);
//                goods.setStatusStartDate(goodsOrder.getLimitStart());
//                goods.setStatusEndDate(goodsOrder.getLimitEnd());
//            } else {
//                goodsOrder.setStatus(OrderStatusEnum.EARNEST);
//                goods.setStatus(GoodsStatusEnum.RESERVE);
//                goods.setStatusStartDate(new Date());
//                goods.setStatusEndDate(Tools.addDay(new Date(), 7));
//            }
//        }
//        if (isCreate) {
//            em.persist(goodsOrder);
//            em.flush();
//        } else {
//            em.merge(goodsOrder);
//        }
//        if (goodsOrder.getStatus().equals(OrderStatusEnum.WAIT_SIGN_CONTRACT)) {
//            //尾款了
//            if (goodsOrder.getUserAmount().compareTo(BigDecimal.ZERO) > 0 && goodsOrder.getDivideUser() != null) {
//                UserWageLog userWageLog = new UserWageLog();
//                userWageLog.setAmount(goodsOrder.getUserAmount());
//                userWageLog.setUser(goodsOrder.getDivideUser());
//                userWageLog.setPayDate(goodsOrder.getLastPayDate());
//                userWageLog.setGoodsOrder(goodsOrder);
//                em.persist(userWageLog);
//            }
//            if (goodsOrder.getRecommendIdList() != null && !goodsOrder.getRecommendIdList().isEmpty()) {
//                for (int i = 0; i < goodsOrder.getRecommendIdList().size(); i++) {
//                    String idStr = goodsOrder.getRecommendIdList().get(i);
//                    String rate = goodsOrder.getRecommendRateList().get(i);
//                    SysUser sysUser = em.find(SysUser.class, Long.parseLong(idStr));
//                    adminService.createWageLog(goodsOrder, sysUser, goodsOrder.getDivideAmount().multiply(new BigDecimal(rate)).divide(new BigDecimal(100), RoundingMode.DOWN).setScale(2, RoundingMode.DOWN), goodsOrder.getFee().multiply(new BigDecimal(rate)).divide(new BigDecimal(100), RoundingMode.UP).setScale(2, RoundingMode.UP), WageLogTypeEnum.RECOMMEND, goodsOrder.getLastPayDate());
//                }
//            }
//            //增加代理的金额
//        }
//        OrderRecord orderRecord = new OrderRecord();
//        orderRecord.setGatewayType(PaymentGatewayTypeEnum.valueOf(gatewayType));
//        orderRecord.setPayDate(Tools.parseDate(payDate, "yyyy-MM-dd"));
//        orderRecord.setType(OrderRecordTypeEnum.valueOf(orderRecordType));
//        orderRecord.setGoods(goodsOrder.getGoods());
//        orderRecord.setOrder(goodsOrder);
//        orderRecord.setPrice(new BigDecimal(amount));
//        em.persist(orderRecord);
//        em.merge(goods);
//        map.put("data", goodsOrder);
//        map.put("msg", "操作成功！");
//        map.put("success", "1");
//        return Tools.caseObjectToJson(map);
//    }
    /**
     * 大区经理提成列表
     *
     * @param auth
     * @param start
     * @param end
     * @param search
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("user_wage_log_list")
    public String getUserWageLogList(@CookieParam("auth") String auth, @QueryParam("start") String start, @QueryParam("end") String end, @QueryParam("search") String search, @DefaultValue("1")
            @QueryParam("pageIndex") Integer pageIndex, @DefaultValue("10")
            @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Date startDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                startDate = Tools.parseDate(start, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            startDate = null;
        }
        Date endDate = null;
        try {
            if (Tools.isNotBlank(start)) {
                endDate = Tools.parseDate(end, "yyyy-MM-dd");
            }
        } catch (Exception e) {
            endDate = null;
        }
        Map searchMap = new HashMap();
        if (Tools.isNotBlank(search)) {
            searchMap.put("search", search);
        }
        Query countQuery = em.createQuery("SELECT COUNT(DISTINCT(a.user)) FROM UserWageLog a WHERE a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate");
        countQuery.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 1));
        Long totalCount = (Long) countQuery.getSingleResult();
        Query query = em.createQuery("SELECT a.user.name,SUM(a.amount),a.user.id,COUNT(a.goodsOrder) FROM UserWageLog a WHERE a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user ");
        int startIndex = (pageIndex - 1) * maxPerPage;
        query.setFirstResult(startIndex);
        query.setMaxResults(maxPerPage);
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 1));
        List<String[]> list = new ArrayList<>();
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[4];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3].toString();
            list.add(str);
        }
        map.put("totalCount", totalCount);
        map.put("data", list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 批量修改地区的大区经理
     *
     * @param auth
     * @param province
     * @param newUid
     * @return
     * @throws Exception
     */
    @POST
    @Path("modify_place_user")
    public String modifyPlaceUser(@CookieParam("auth") String auth, @FormParam("category") String category, @FormParam("province") String province, @FormParam("uid") Long newUid) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        Map map = Tools.getDMap();
        SysUser newUser = em.find(SysUser.class, newUid);
        adminService.saveLog(user,
                "批量修改地区的大区经理", " category：" + Tools.getString(category) + " province：" + Tools.getString(province));
        TypedQuery<Goods> goodsQuery = em.createQuery("SELECT g FROM Goods g WHERE g.deleted = FALSE AND g.province = :province AND g.category  = :category", Goods.class);

        goodsQuery.setParameter(
                "province", province).setParameter("category", CategoryEnum.valueOf(category));
        for (Goods goods
                : goodsQuery.getResultList()) {
            goods.setUser(newUser);
            em.merge(goods);
            TypedQuery<GoodsOrder> goodsOrderQuery = em.createQuery("SELECT g FROM GoodsOrder g WHERE g.deleted = FALSE AND g.goods = :goods AND g.category = :category", GoodsOrder.class);
            goodsOrderQuery.setParameter("goods", goods).setParameter("category", CategoryEnum.valueOf(category));
            for (GoodsOrder goodsOrder : goodsOrderQuery.getResultList()) {
                goodsOrder.setUser(newUser);
                em.merge(goodsOrder);
            }
        }

        map.put(
                "success", "1");
        map.put(
                "msg", "修改成功");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 批量修改商品价格通过类型
     *
     * @param auth
     * @param category
     * @param type
     * @param price
     * @return
     * @throws Exception
     */
    @POST
    @Path("modify_goods_price_all_by_type")
    public String modifyGoodsPriceAllByType(@CookieParam("auth") String auth, @FormParam("category") String category, @FormParam("type") String type, @FormParam("price") String price) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (!SysUserTypeEnum.SUPER.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        adminService.saveLog(user, "批量修改商品价格通过类型", " category：" + Tools.getString(category) + " type：" + Tools.getString(type) + " price：" + Tools.getString(price));
        Map map = Tools.getDMap();
        Query goodsQuery = em.createQuery("UPDATE Goods g SET g.price = :price WHERE g.deleted = FALSE AND g.type = :type AND g.category  = :category");
        goodsQuery.setParameter("type", GoodsTypeEnum.valueOf(type)).setParameter("category", CategoryEnum.valueOf(category)).setParameter("price", new BigDecimal(price));
        int count = goodsQuery.executeUpdate();
        map.put("success", "1");
        map.put("msg", "成功修改:" + count + "条数据");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 根据订单号获取订单
     *
     * @param auth
     * @param serialId
     * @return
     * @throws Exception
     */
    @GET
    @Path("find_order")
    public String findOrder(@CookieParam("auth") String auth, @QueryParam("serialId") String serialId) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        if (Tools.isBlank(serialId)) {
            throw new EjbMessageException("请输入订单号");

        }
        TypedQuery<GoodsOrder> query = em.createQuery("SELECT a FROM GoodsOrder a WHERE a.deleted = FALSE AND a.serialId = :serialId", GoodsOrder.class
        );
        query.setParameter(
                "serialId", serialId);
        GoodsOrder order = null;

        try {
            order = query.getSingleResult();
        } catch (Exception e) {
            order = null;
        }
        if (order
                == null) {
            throw new EjbMessageException("没有找到订单，若确认订单号正确，请联系管理员");
        }

        map.put(
                "data", order);
        map.put(
                "success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 修改大区经理提成
     *
     * @param auth
     * @param id
     * @param userAmount
     * @param newUid
     * @return
     * @throws Exception
     */
    @POST
    @Path("modify_order_user")
    public String modifyOrderUser(@CookieParam("auth") String auth, @FormParam("id") Long id, @FormParam("userAmount") String userAmount, @FormParam("uid") Long newUid) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        if (SysUserTypeEnum.MANAGE.equals(user.getAdminType())) {
            throw new EjbMessageException("您没有权限");
        }
        if (id == null || Tools.isBlank(userAmount) || newUid == null) {
            throw new EjbMessageException("缺少必要信息");
        }
        adminService.saveLog(user, "批量修改商品价格通过类型", " userAmount：" + Tools.getString(userAmount) + " newUid：" + newUid + " id：" + id);
        GoodsOrder order = em.find(GoodsOrder.class, id);
        SysUser sysUser = em.find(SysUser.class, newUid);

        order.setUserAmount(
                new BigDecimal(userAmount));
        order.setDivideUser(sysUser);

        em.merge(order);
        TypedQuery<UserWageLog> countSql = em.createQuery("SELECT a FROM UserWageLog a WHERE a.goodsOrder = :goodsOrder", UserWageLog.class);

        countSql.setParameter(
                "goodsOrder", order);
        if (countSql.getResultList()
                == null || countSql.getResultList().size() < 1) {
            UserWageLog userWageLog = new UserWageLog();
            userWageLog.setAmount(new BigDecimal(userAmount));
            userWageLog.setCategory(order.getCategory());
            userWageLog.setGoodsOrder(order);
            userWageLog.setPayDate(order.getLastPayDate());
            userWageLog.setUser(sysUser);
            em.persist(userWageLog);
        } else {
            for (UserWageLog log : countSql.getResultList()) {
                log.setUser(sysUser);
                log.setAmount(new BigDecimal(userAmount));
                em.merge(log);
            }
        }
        Map map = Tools.getDMap();

        map.put(
                "success", "1");
        map.put(
                "msg", "修改成功");
        return Tools.caseObjectToJson(map);
    }

    @GET
    @Path("dosome")
    public String dosome(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        SysUser u1 = em.find(SysUser.class, 1788l);//5500  11000
        SysUser u2 = em.find(SysUser.class, 1789l);//5125 5410 10535
        TypedQuery<NewAd> queryad = em.createQuery("SELECT a FROM NewAd a WHERE a.user = :u2 ", NewAd.class);

        queryad.setParameter(
                "u2", u2);
        for (NewAd ad
                : queryad.getResultList()) {
            ad.setUser(u1);
            em.merge(ad);
        }
        TypedQuery<GoodsOrder> queryOrderRecord = em.createQuery("SELECT a FROM GoodsOrder a WHERE a.agentUser = :u2 ", GoodsOrder.class);

        queryOrderRecord.setParameter(
                "u2", u2);
        for (GoodsOrder order
                : queryOrderRecord.getResultList()) {
            order.setAgentUser(u1);
            em.merge(order);
        }
        TypedQuery<WageLog> queryWageLog = em.createQuery("SELECT a FROM WageLog a WHERE a.user = :u2 ", WageLog.class);

        queryWageLog.setParameter(
                "u2", u2);
        for (WageLog log
                : queryWageLog.getResultList()) {
            log.setUser(u1);
            em.merge(log);
        }

        return "ok";
    }

    @GET
    @Path("dosome1")
    public String dosome1(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        TypedQuery<Goods> goodsQueryad = em.createQuery("SELECT a FROM Goods a WHERE a.deleted = FALSE ", Goods.class
        );
        for (Goods goods
                : goodsQueryad.getResultList()) {
            if (goods.getType().equals(GoodsTypeEnum.COUNTY) || goods.getType().equals(GoodsTypeEnum.SELF_COUNTY)) {
                //获取最后一个字和倒数第二个字
                String name = goods.getName();
                String lastWord = name.substring(name.length() - 1, name.length());
                String last2Word = name.substring(name.length() - 2, name.length() - 1);
                String lastWord2 = name.substring(name.length() - 2, name.length()); //最后两个数字
                if (lastWord2.equals("10") || lastWord2.equals("11") || lastWord2.equals("12") || lastWord2.equals("13")) {
                    System.out.println("************************10");
                } else if (!(lastWord.equals("县") || last2Word.equals("县"))) {
                    if (lastWord.equals("1") || lastWord.equals("2") || lastWord.equals("3") || lastWord.equals("4") || lastWord.equals("5") || lastWord.equals("6") || lastWord.equals("7") || lastWord.equals("8")
                            || lastWord.equals("9")) {
                        goods.setName(name.substring(0, name.length() - 1) + "县" + lastWord);
                        goods.setNamePinyin(Trans2PinYin.trans2PinYinFirst(goods.getName()));
                        em.merge(goods);
                    } else if (lastWord.equals("总")) {
                        goods.setName(name.substring(0, name.length() - 1) + "县" + lastWord);
                        goods.setNamePinyin(Trans2PinYin.trans2PinYinFirst(goods.getName()));
                        em.merge(goods);
                    } else {
                        goods.setName(name + "县");
                        goods.setNamePinyin(Trans2PinYin.trans2PinYinFirst(goods.getName()));
                        em.merge(goods);
                    }
                }
            }
            TypedQuery<GoodsOrder> goodsOrderQueryad = em.createQuery("SELECT a FROM GoodsOrder a WHERE a.deleted = FALSE AND a.goods = :goods", GoodsOrder.class);
            goodsOrderQueryad.setParameter("goods", goods);
            for (GoodsOrder goodsOrder : goodsOrderQueryad.getResultList()) {
                if (!(goods.getProvinceStr() + "_" + goods.getName() + "_" + goods.getUser().getId() + "_" + goods.getUser().getName()).equals(goodsOrder.getGoodsMsg())) {
                    goodsOrder.setGoodsMsg(goods.getProvinceStr() + "_" + goods.getName() + "_" + goods.getUser().getId() + "_" + goods.getUser().getName());
                    goodsOrder.setGoodsPinyin(Trans2PinYin.trans2PinYinFirst(goods.getName()));
                    em.merge(goodsOrder);
                }
            }

        }

        return "ok";
    }

    /**
     * 修改用户订单，针对用户所有的工资，公告
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("change_order_user")
    public String changeOrderUser(@CookieParam("auth") String auth) throws Exception {
        adminService.getUserByLoginCode(auth);
        SysUser user = em.find(SysUser.class, 2693l);//修改后的用户
        GoodsOrder order = em.find(GoodsOrder.class, 961l);//要修改的订单
        SysUser oldAgentUser = order.getAgentUser();//原来的代理

        order.setAgentUser(user);

        em.merge(order);//修改成新的代理
        TypedQuery<NewAd> newAdQueryad = em.createQuery("SELECT a FROM NewAd a WHERE a.user = :user AND a.goods = :goods AND a.deleted = FALSE ", NewAd.class);

        newAdQueryad.setParameter(
                "user", oldAgentUser).setParameter("goods", order.getGoods());
        BigDecimal tot = BigDecimal.ZERO;
        for (NewAd ad
                : newAdQueryad.getResultList()) {
            tot = tot.add(ad.getUserBalanceAmount());
            ad.setUser(user);
            em.merge(ad);
        }

        user.setBalance(
                (order.getPaidPrice().subtract(order.getPeopleCountFee())).subtract(tot));
        user.setDeposit(
                (order.getPaidPrice().subtract(order.getPeopleCountFee())));
        em.merge(user);

        oldAgentUser.setBalance(oldAgentUser.getBalance().add(tot).subtract((order.getPaidPrice().subtract(order.getPeopleCountFee()))));
        oldAgentUser.setDeposit(oldAgentUser.getDeposit().subtract((order.getPaidPrice().subtract(order.getPeopleCountFee()))));
        em.merge(oldAgentUser);

        return "ok";
    }

    /**
     * 修改用户推荐
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("change_order_user_recommend")
    public String changeOrderUseRrecommend(@CookieParam("auth") String auth) throws Exception {
        adminService.getUserByLoginCode(auth);
        SysUser user = em.find(SysUser.class, 2661l);//修改后的用户
        GoodsOrder order = em.find(GoodsOrder.class, 2179l);//要修改的订单
        SysUser oldAgentUser = em.find(SysUser.class, Long.parseLong(order.getRecommendIdList().get(0)));//原来的分成人

        order.setRecommendIds(user.getId().toString());
        order.setRecommendNames(user.getName());
        em.merge(order);//修改成新的分成人
        TypedQuery<WageLog> wageLogQueryad = em.createQuery("SELECT a FROM WageLog a WHERE a.user = :user AND a.goodsOrder = :goodsOrder AND a.deleted = FALSE ", WageLog.class);

        wageLogQueryad.setParameter(
                "user", oldAgentUser).setParameter("goodsOrder", order);
        for (WageLog wageLog
                : wageLogQueryad.getResultList()) {
            wageLog.setUser(user);
            em.merge(wageLog);
        }

        return "ok";
    }

    /**
     * 把一个用户下的广告和平台，余额都转给另一个用户
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("change_user_to_user")
    public String changeUseToUser(@CookieParam("auth") String auth) throws Exception {
        adminService.getUserByLoginCode(auth);
        SysUser user = em.find(SysUser.class, 1966L);//用户
        SysUser toUser = em.find(SysUser.class, 2612L);//目标用户
        TypedQuery<GoodsOrder> qrderQuery = em.createQuery("SELECT a FROM GoodsOrder a WHERE a.agentUser = :user AND a.deleted = FALSE ", GoodsOrder.class);

        qrderQuery.setParameter("user", user);
        for (GoodsOrder order : qrderQuery.getResultList()) {
            order.setAgentUser(toUser);//修改为新的代理
            em.merge(order);
        }
        TypedQuery<NewAd> newAdQueryad = em.createQuery("SELECT a FROM NewAd a WHERE a.user = :user AND a.deleted = FALSE ", NewAd.class);

        newAdQueryad.setParameter("user", user);
        for (NewAd ad : newAdQueryad.getResultList()) {
            ad.setUser(toUser);//修改广告的分成人
            em.merge(ad);
        }
        TypedQuery<WageLog> wageLogQueryad = em.createQuery("SELECT a FROM WageLog a WHERE a.user = :user AND a.deleted = FALSE ", WageLog.class);

        wageLogQueryad.setParameter("user", user);
        for (WageLog wageLog : wageLogQueryad.getResultList()) {
            wageLog.setUser(toUser);//修改工资
            em.merge(wageLog);
        }
        //**********************************************
        //注意查看用户有没有推荐的平台
        //***********************************************
        //转移用户余额

        toUser.setBalance(toUser.getBalance().add(user.getBalance()));
        toUser.setDeposit(toUser.getDeposit().add(user.getDeposit()));
        em.merge(toUser);

        user.setDeleted(Boolean.TRUE);

        em.merge(user);

        return "ok";
    }

    /**
     * 初始化地区
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("init_goods")
    public String initGoods(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        TypedQuery<Goods> goodsq = em.createQuery("SELECT a FROM Goods a WHERE a.deleted = FALSE AND a.category = :category", Goods.class
        );
        goodsq.setParameter("category", CategoryEnum.MAKE_FRIENDS);
        for (Goods goods : goodsq.getResultList()) {
            TypedQuery<Long> countSql = em.createQuery("SELECT COUNT(a) FROM Goods a WHERE a.deleted = FALSE AND a.category = :category AND a.name = :name", Long.class);
            countSql.setParameter("category", CategoryEnum.MAKE_FRIENDS).setParameter("name", goods.getName());
            if (countSql.getSingleResult() > 1) {
                if (goods.getStatus().equals(GoodsStatusEnum.SALE)) {
                    em.remove(goods);
                    em.flush();
                }
            }
        }

        return "ok";
    }

    /**
     * 批量修改大区的大区分成
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("change_daqu")
    public String changeDaqu(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        TypedQuery<GoodsOrder> goodsq = em.createQuery("SELECT a FROM GoodsOrder a WHERE a.deleted = FALSE AND a.category = :category AND a.goods.type = :type AND a.divideUser IS NOT NULL", GoodsOrder.class);
        goodsq.setParameter("category", CategoryEnum.MAKE_FRIENDS).setParameter("type", GoodsTypeEnum.HOT);
        for (GoodsOrder goods : goodsq.getResultList()) {
            goods.setUserAmount(new BigDecimal(200));
            em.merge(goods);
            TypedQuery<UserWageLog> countSql = em.createQuery("SELECT a FROM UserWageLog a WHERE a.goodsOrder = :goodsOrder", UserWageLog.class);
            countSql.setParameter("goodsOrder", goods);
            for (UserWageLog log : countSql.getResultList()) {
                log.setAmount(new BigDecimal(200));
                em.merge(log);
            }
        }

        return "ok";
    }

    /**
     * 把用户交友的余额全部转到余额上
     *
     * @param auth
     * @return
     * @throws Exception
     */
//    @GET
//    @Path("set_balance_from_mf")
//    public String setBalanceFromMf(@CookieParam("auth") String auth) throws Exception {
//        SysUser user = adminService.getUserByLoginCode(auth);
//        TypedQuery<SysUser> query = em.createQuery("SELECT a FROM SysUser a WHERE a.deleted = FALSE", SysUser.class);
//        for (SysUser sysUser : query.getResultList()) {
//            sysUser.setDeposit(sysUser.getDeposit().add(sysUser.getDepositMf()));
//            sysUser.setBalance(sysUser.getBalance().add(sysUser.getBalanceMf()));
//            sysUser.setDepositMf(BigDecimal.ZERO);
//            sysUser.setBalanceMf(BigDecimal.ZERO);
//            em.merge(sysUser);
//        }
//        System.out.println("改变了:" + query.getResultList().size());
//        return "ok";
//    }
    @GET
    @Path("jiaoyou2bianpin")
    public String jiaoyou2bianpin(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
//        TypedQuery<GoodsOrder> goodsq = em.createQuery("SELECT a FROM GoodsOrder a WHERE a.deleted = FALSE AND a.status IN :status AND a.category = :category", GoodsOrder.class);
//        List<OrderStatusEnum> orderStatusEnumList = new ArrayList();
//        orderStatusEnumList.add(OrderStatusEnum.WAIT_SIGN_CONTRACT);
//        orderStatusEnumList.add(OrderStatusEnum.SUCCESS);
//        orderStatusEnumList.add(OrderStatusEnum.EARNEST);
//        goodsq.setParameter("category", CategoryEnum.SERVICE_PEOPLE).setParameter("status", orderStatusEnumList);
//        List<String> list = new ArrayList();
//        for (GoodsOrder goodsOrder : goodsq.getResultList()) {
//            list.add(goodsOrder.getGoods().getName());
//        }
//        List<String> oldList = FileUtils.readLines(new File("d:/a/old.txt"));
//        for (String old : oldList) {
//            //
//            if (!list.contains(old)) {
//                System.out.println("******" + old);
//            }
//        }
//        List<String> newList = FileUtils.readLines(new File("d:/a/new.txt"));
        List<String> newList = new ArrayList<>();
//        newList.add("卢龙县4");
        Set set = new HashSet();
        int i = 0;
        int j = 0;
        for (String newString : newList) {
            i++;
            j += 2;
//            if (set.contains(newString)) {
//                System.out.println(newString);
//            }
//            set.add(newString);
            //
//            if (list.contains(newString)) {
//                System.out.println("******" + newString);
//            }
            TypedQuery<GoodsOrder> goodsq = em.createQuery("SELECT a FROM GoodsOrder a WHERE a.deleted = FALSE AND a.status != :status AND a.category = :category AND a.goods.name = :name", GoodsOrder.class
            );
            goodsq.setParameter(
                    "category", CategoryEnum.MAKE_FRIENDS).setParameter("name", newString).setParameter("status", OrderStatusEnum.TERMINATION);
//            if (goodsq.getResultList().size() != 1) {
//                System.out.println(newString);
//            }
            GoodsOrder order = goodsq.getSingleResult();
            Goods goods = order.getGoods();
            Query newAdq = em.createQuery("UPDATE NewAd a SET a.category = :newcategory WHERE a.deleted = FALSE AND a.category = :category AND a.goods = :goods");

            newAdq.setParameter(
                    "category", CategoryEnum.MAKE_FRIENDS).setParameter("goods", goods).setParameter("newcategory", CategoryEnum.SERVICE_PEOPLE);
            j = j + newAdq.executeUpdate();
            Query userWageLogq = em.createQuery("UPDATE UserWageLog a SET a.category = :newcategory WHERE a.deleted = FALSE AND a.category = :category AND a.goodsOrder = :goodsOrder");

            userWageLogq.setParameter(
                    "category", CategoryEnum.MAKE_FRIENDS).setParameter("goodsOrder", order).setParameter("newcategory", CategoryEnum.SERVICE_PEOPLE);
            j = j + userWageLogq.executeUpdate();
            Query wageLogq = em.createQuery("UPDATE WageLog a SET a.category = :newcategory WHERE a.deleted = FALSE AND a.category = :category AND a.goodsOrder = :goodsOrder");

            wageLogq.setParameter(
                    "category", CategoryEnum.MAKE_FRIENDS).setParameter("goodsOrder", order).setParameter("newcategory", CategoryEnum.SERVICE_PEOPLE);
            j = j + wageLogq.executeUpdate();

            order.setCategory(CategoryEnum.SERVICE_PEOPLE);

            goods.setCategory(CategoryEnum.SERVICE_PEOPLE);

            em.merge(order);

            em.merge(goods);
        }
        System.out.println(i);
        System.out.println(j);
//        FileUtils.writeLines(new File("d:/a/123.csv"), list);
        return "ok";
    }

}
