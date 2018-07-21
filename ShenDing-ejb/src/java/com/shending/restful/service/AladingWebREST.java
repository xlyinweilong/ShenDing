package com.shending.restful.service;

import com.shending.entity.*;
import com.shending.restful.interception.AccountInterceptor;
import com.shending.service.AdminService;
import com.shending.service.AladingWebService;
import com.shending.service.ConfigService;
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
@Path("aldingweb")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
@Interceptors(AccountInterceptor.class)
public class AladingWebREST {

    @EJB
    private AdminService adminService;
    @EJB
    private ConfigService configService;
    @EJB
    private AladingWebService aladingWebService;
    @PersistenceContext(unitName = "ShenDing-PU")
    private EntityManager em;

    /**
     * 导入代理信息
     *
     * @param auth
     * @param servletRequest
     * @return
     * @throws Exception
     */
    @POST
    @Path("upload_file_alading_web_search")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String importSearch(@CookieParam("auth") String auth, @Context HttpServletRequest servletRequest) throws Exception {
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
                        //姓名
                        String name = StringUtils.trimToNull(cells[0].getContents());
                        if (Tools.isBlank(name)) {
                            throw new EjbMessageException("第" + (i + 1) + "行姓名必填");
                        }

                        //微信号
                        String wecatCode = StringUtils.trimToNull(cells[1].getContents());
                        if (Tools.isBlank(wecatCode)) {
                            throw new EjbMessageException("第" + (i + 1) + "行微信号必填");
                        }

                        //省份
                        String province = StringUtils.trimToNull(cells[2].getContents());
                        if (Tools.isBlank(province)) {
                            throw new EjbMessageException("第" + (i + 1) + "行省份必填");
                        }
                        //代理地区
                        String agentArea = StringUtils.trimToNull(cells[3].getContents());
                        if (Tools.isBlank(agentArea)) {
                            throw new EjbMessageException("第" + (i + 1) + "行代理地区必填");
                        }
                        
                        aladingWebService.createOrUpdateAladingwebSearch(null, province, agentArea, name, wecatCode);
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
//                aladingWebService.createPic();
                map.put("msg", "上传成功");
                map.put("success", "1");
                map.put("data", "");
                return Tools.caseObjectToJson(map);
            }
        }
        map.put("msg", "未找到合法数据");
        return Tools.caseObjectToJson(map);
    }

    @POST
    @Path("upload_file_alading_web_config")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String config(@CookieParam("auth") String auth, @Context HttpServletRequest servletRequest) throws Exception {
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
                String exe = item.getOrigFileExtName();
                File file = new File(item.getUploadFullPath());
                String name = UUID.randomUUID().toString();
                File newFile = new File("/data/pic/" + name + exe);
                FileUtils.copyFile(file, newFile);
                FileUtils.deleteQuietly(file);
                configService.saveConfig("ALADING_FILE_LOGO", "/pic/" + name + exe);
            }
            if ("file2".equals(item.getFieldName())) {
                String exe = item.getOrigFileExtName();
                File file = new File(item.getUploadFullPath());
                String name = UUID.randomUUID().toString();
                File newFile = new File("/data/pic/" + name + exe);
                FileUtils.copyFile(file, newFile);
                FileUtils.deleteQuietly(file);
                configService.saveConfig("ALADING_FILE_BG_URL", "/pic/" + name + exe);

            }
        }
        map.put("msg", "上传成功");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 查询代理查询列表
     *
     * @param auth
     * @param search
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("alading_web_search_list")
    public String aladingWebSearchList(@CookieParam("auth") String auth, @QueryParam("search") String search,
            @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex,
            @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        ResultList<AladingwebSearch> list = aladingWebService.findAladingwebSearchList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 删除代理信息
     *
     * @param auth
     * @param ids
     * @return
     * @throws Exception
     */
    @POST
    @Path("delete_alading_web_search")
    public String deleteAladingWebSearch(@CookieParam("auth") String auth, @FormParam("ids") List<Long> ids) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        aladingWebService.deleteAladingwebSearchById(ids);
        map.put("msg", "操作成功！");
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 查询代理申请
     *
     * @param auth
     * @param search
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("alading_web_apply_list")
    public String aladingWebApplyList(@CookieParam("auth") String auth, @QueryParam("search") String search,
            @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex,
            @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        ResultList<AladingwebApply> list = aladingWebService.findAladingwebApplyList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 查询全国推广
     *
     * @param auth
     * @param search
     * @param pageIndex
     * @param maxPerPage
     * @return
     * @throws Exception
     */
    @GET
    @Path("alading_web_spread_list")
    public String aladingWebSpreadList(@CookieParam("auth") String auth, @QueryParam("search") String search,
            @DefaultValue("1") @QueryParam("pageIndex") Integer pageIndex,
            @DefaultValue("10") @QueryParam("maxPerPage") Integer maxPerPage) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map searchMap = new HashMap();
        if (Tools.isNotBlank(search)) {
            search = search.split(" ")[0];
            searchMap.put("search", search);
        }
        ResultList<AladingwebSpread> list = aladingWebService.findAladingwebSpreadList(searchMap, pageIndex, maxPerPage, null, Boolean.TRUE);
        map.put("totalCount", list.getTotalCount());
        map.put("data", (List) list);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    @GET
    @Path("createPic")
    public String createPic(@CookieParam("auth") String auth) throws Exception {
        Map map = Tools.getDMap();
        SysUser user = adminService.getUserByLoginCode(auth);
        aladingWebService.createPic();
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

}
