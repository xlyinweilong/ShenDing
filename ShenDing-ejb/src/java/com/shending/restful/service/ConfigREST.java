package com.shending.restful.service;

import com.shending.entity.*;
import com.shending.restful.interception.AccountInterceptor;
import com.shending.service.AdminService;
import com.shending.service.ConfigService;
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
@Path("config")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
@Interceptors(AccountInterceptor.class)
public class ConfigREST {

    @EJB
    private AdminService adminService;
    @EJB
    private ConfigService configService;
    @PersistenceContext(unitName = "ShenDing-PU")
    private EntityManager em;

    /**
     * 获取推荐人提成的默认配置
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("commission")
    public String commission(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map dataMap = new HashMap();
        dataMap.put("PROVINCIAL_CAPITAL", configService.findConfigByKey("COMMISSION_PROVINCIAL_CAPITAL").getValue());
        dataMap.put("PREFECTURE", configService.findConfigByKey("COMMISSION_PREFECTURE").getValue());
        dataMap.put("OTHERS", configService.findConfigByKey("COMMISSION_OTHERS").getValue());
        map.put("data", dataMap);
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 保存推荐人提成的默认配置
     *
     * @param auth
     * @param capital
     * @param prefecture
     * @param others
     * @return
     * @throws Exception
     */
    @POST
    @Path("commission")
    public String saveCommission(@CookieParam("auth") String auth, @FormParam("capital") String capital, @FormParam("prefecture") String prefecture, @FormParam("others") String others) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map dataMap = new HashMap();
        configService.saveConfig("COMMISSION_PROVINCIAL_CAPITAL", capital);
        configService.saveConfig("COMMISSION_PREFECTURE", prefecture);
        configService.saveConfig("COMMISSION_OTHERS", others);
        map.put("success", "1");
        map.put("msg", "修改成功");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 激励语
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("excitation")
    public String excitation(@CookieParam("auth") String auth) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        map.put("data", configService.findConfigByKey("EXCITATION").getValue());
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 配置激励语
     *
     * @param auth
     * @param excitation
     * @return
     * @throws Exception
     */
    @POST
    @Path("excitation")
    public String saveExcitation(@CookieParam("auth") String auth, @FormParam("excitation") String excitation) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map dataMap = new HashMap();
        configService.saveConfig("EXCITATION", excitation);
        map.put("success", "1");
        map.put("msg", "修改成功");
        return Tools.caseObjectToJson(map);
    }
    
    
    /**
     * 配置
     *
     * @param auth
     * @return
     * @throws Exception
     */
    @GET
    @Path("config")
    public String getConfig(@CookieParam("auth") String auth,@QueryParam("key") String key) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        map.put("data", configService.findConfigByKey(key).getValue());
        map.put("success", "1");
        return Tools.caseObjectToJson(map);
    }

    /**
     * 修改配置
     *
     * @param auth
     * @param value
     * @return
     * @throws Exception
     */
    @POST
    @Path("save_config")
    public String saveConfig(@CookieParam("auth") String auth, @FormParam("key") String key, @FormParam("value") String value) throws Exception {
        SysUser user = adminService.getUserByLoginCode(auth);
        Map map = Tools.getDMap();
        Map dataMap = new HashMap();
        configService.saveConfig(key, value);
        map.put("success", "1");
        map.put("msg", "修改成功");
        return Tools.caseObjectToJson(map);
    }
}
