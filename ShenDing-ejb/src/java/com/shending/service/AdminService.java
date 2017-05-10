package com.shending.service;

import com.shending.entity.Cosmetics;
import com.shending.entity.WageLog;
import com.shending.entity.DataArea;
import com.shending.entity.DataCity;
import com.shending.entity.DataProvince;
import com.shending.entity.Goods;
import com.shending.entity.GoodsOrder;
import com.shending.entity.Log;
import com.shending.entity.NewAd;
import com.shending.entity.OrderRecord;
import com.shending.entity.ProductGrandSlam;
import com.shending.entity.ProductLog;
import com.shending.entity.SysMenu;
import com.shending.entity.SysRole;
import com.shending.entity.SysRoleMenu;
import com.shending.entity.SysUser;
import com.shending.entity.UserWageLog;
import com.shending.entity.Vote;
import com.shending.support.ResultList;
import com.shending.support.Tools;
import com.shending.support.bo.UserWages;
import com.shending.support.enums.AdGoodsTypeEnum;
import com.shending.support.enums.AdLevelEnum;
import com.shending.support.enums.AdLimitTypeEnum;
import com.shending.support.enums.CategoryEnum;
import com.shending.support.enums.WageLogTypeEnum;
import com.shending.support.enums.OrderStatusEnum;
import com.shending.support.enums.PaymentGatewayTypeEnum;
import com.shending.support.enums.ProductEnum;
import com.shending.support.enums.SysMenuPopedomEnum;
import com.shending.support.enums.SysUserStatus;
import com.shending.support.enums.SysUserTypeEnum;
import com.shending.support.exception.AccountNotExistException;
import com.shending.support.exception.EjbMessageException;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 后台用户服务层
 *
 * @author yin.weilong
 */
@Stateless
@LocalBean
public class AdminService {

    @PersistenceContext(unitName = "ShenDing-PU")
    private EntityManager em;
    private static final Logger logger = Logger.getLogger(AdminService.class.getName());

    @Asynchronous
    public void saveLog(SysUser user, String url, String msg) {
        Log log = new Log();
        log.setUser(user);
        log.setMsg(msg);
        log.setUrl(url);
        em.persist(log);
    }

    @Asynchronous
    public void createVote(String q1, String q2, String q3, String q4, String q5, String q6, String q7, String q8, String q9, String q10, String q11, String q12, String q13, String q14, String q15, String q16) {
        Vote vote = new Vote();
        vote.setQ1(q1);
        vote.setQ10(q10);
        vote.setQ11(q11);
        vote.setQ13(q13);
        vote.setQ12(q12);
        vote.setQ14(q14);
        vote.setQ15(q15);
        vote.setQ16(q16);
        vote.setQ2(q2);
        vote.setQ3(q3);
        vote.setQ4(q4);
        vote.setQ5(q5);
        vote.setQ6(q6);
        vote.setQ7(q7);
        vote.setQ8(q8);
        vote.setQ9(q9);
        em.persist(vote);
    }

    public SysUser setAccountLoginCode(SysUser sysUser) {
        Long id = sysUser.getId();
        int maxCount = 10;
        String loginCode = this.generateLogingCode(id);
        int i = 1;
        for (; i < maxCount && findAccountByLoginCode(loginCode) != null; i++) {
            loginCode = this.generateLogingCode(id);
        }
        if (i >= 10) {
            throw new RuntimeException("System Error");
        }
        sysUser.setLoginCode(loginCode);
        em.merge(sysUser);
        return sysUser;
    }

    public void createOrUpdateSysRoleMenu(Long rid, List<Long> mids) {
        String removeHql = "DELETE FROM SysRoleMenu srm WHERE srm.sysRole.id = :rid";
        Query query = em.createQuery(removeHql).setParameter("rid", rid);
        query.executeUpdate();
        SysRole sr = this.findSysRoleById(rid);
        for (Long mid : mids) {
            if (null == mid) {
                continue;
            }
            SysRoleMenu srm = new SysRoleMenu();
            SysMenu sm = em.find(SysMenu.class, mid);
            if (sm.getParentMenu() != null && this.findSysRoleMenu(sm.getParentMenu().getId(), rid) == null) {
                SysRoleMenu psrm = new SysRoleMenu();
                psrm.setSysMenu(sm.getParentMenu());
                psrm.setSysRole(sr);
                em.persist(psrm);
            }
            srm.setSysMenu(sm);
            srm.setSysRole(sr);
            em.persist(srm);
            em.flush();
        }
    }

    /**
     * 获取角色菜单关联
     *
     * @param mid
     * @param rid
     * @return
     */
    public SysRoleMenu findSysRoleMenu(Long mid, Long rid) {
        SysRoleMenu sysRoleMenu = null;
        try {
            TypedQuery<SysRoleMenu> query = em.createQuery("SELECT s FROM SysRoleMenu s WHERE s.sysMenu.id = :mid AND s.sysRole.id = :rid", SysRoleMenu.class);
            query.setParameter("rid", rid).setParameter("mid", mid);
            sysRoleMenu = query.getSingleResult();
        } catch (NoResultException ex) {
            sysRoleMenu = null;
        }
        return sysRoleMenu;
    }

    /**
     * 获取用户，抛出异常
     *
     * @param loginCode
     * @return
     * @throws AccountNotExistException
     * @throws EjbMessageException
     */
    public SysUser getUserByLoginCode(String loginCode) throws AccountNotExistException, EjbMessageException {
        SysUser user = this.findAccountByLoginCode(loginCode);
        if (user == null) {
            throw new AccountNotExistException("请先登录");
        }
        return user;
    }

    /**
     * 获取用户
     *
     * @param loginCode
     * @return
     */
    public SysUser findAccountByLoginCode(String loginCode) {
        if (Tools.isBlank(loginCode)) {
            return null;
        }
        SysUser user = null;
        try {
            TypedQuery<SysUser> query = em.createQuery("SELECT a FROM SysUser a WHERE a.loginCode = :loginCode and a.deleted = false", SysUser.class);
            query.setParameter("loginCode", loginCode);
            user = query.getSingleResult();
        } catch (NoResultException ex) {
            user = null;
        }
        return user;
    }

    /**
     * 根据ID获取
     *
     * @param uid
     * @return
     */
    public SysUser findById(Long uid) {
        return em.find(SysUser.class, uid);
    }

    /**
     * 通过ID获取菜单
     *
     * @param id
     * @return
     */
    public SysMenu findSysMenuById(Long id) {
        return em.find(SysMenu.class, id);
    }

    /**
     * 通过ID获取角色
     *
     * @param id
     * @return
     */
    public SysRole findSysRoleById(Long id) {
        return em.find(SysRole.class, id);
    }

    /**
     * 根据商品，获取代理人
     *
     * @param goods
     * @return
     */
    public SysUser findUserByOrder(Goods goods) {
        TypedQuery<GoodsOrder> query = em.createQuery("SELECT go FROM GoodsOrder go WHERE go.goods = :goods AND go.status = :stauts AND go.deleted = FALSE ORDER BY go.createDate DESC", GoodsOrder.class);
        query.setParameter("goods", goods).setParameter("stauts", OrderStatusEnum.SUCCESS);
        query.setMaxResults(1);
        List<GoodsOrder> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0).getAgentUser();
        }
        return null;
    }

    /**
     * 根据商品，获取订单
     *
     * @param goods
     * @return
     */
    public GoodsOrder findOrderByOrder(Goods goods) {
        TypedQuery<GoodsOrder> query = em.createQuery("SELECT go FROM GoodsOrder go WHERE go.goods = :goods AND go.status = :stauts AND go.deleted = FALSE ORDER BY go.createDate DESC", GoodsOrder.class);
        query.setParameter("goods", goods).setParameter("stauts", OrderStatusEnum.SUCCESS);
        query.setMaxResults(1);
        List<GoodsOrder> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取菜单
     *
     * @param uid
     * @param lvIndex
     * @return
     */
    public List<SysMenu> findSysMenuByUserId(Long uid, Integer lvIndex) {
        SysUser su = this.findById(uid);
        SysMenuPopedomEnum popedom = SysMenuPopedomEnum.COMMON;
        if (SysUserTypeEnum.ROOT.equals(su.getAdminType())) {
            popedom = SysMenuPopedomEnum.ROOT;
            TypedQuery<SysMenu> query = em.createQuery("SELECT sm FROM SysMenu sm WHERE sm.lvIndex = :lvIndex AND sm.popedom = :popedom ORDER BY sm.sortIndex asc", SysMenu.class);
            query.setParameter("lvIndex", lvIndex).setParameter("popedom", popedom);
            return query.getResultList();
        } else if (SysUserTypeEnum.SUPER.equals(su.getAdminType()) || SysUserTypeEnum.MANAGE.equals(su.getAdminType())) {
            popedom = SysMenuPopedomEnum.ROOT;
            TypedQuery<SysMenu> query = em.createQuery("SELECT sm FROM SysMenu sm WHERE sm.lvIndex = :lvIndex AND sm.popedom != :popedom ORDER BY sm.sortIndex asc", SysMenu.class);
            query.setParameter("lvIndex", lvIndex).setParameter("popedom", popedom);
            return query.getResultList();
        } else {
            SysRole sr = su.getSysRole();
            if (sr == null) {
                TypedQuery<SysMenu> query = em.createQuery("SELECT sm FROM SysMenu sm WHERE sm.popedom = :popedom AND sm.lvIndex = :lvIndex ORDER BY sm.sortIndex asc", SysMenu.class);
                query.setParameter("lvIndex", lvIndex).setParameter("popedom", SysMenuPopedomEnum.ALL);
                return query.getResultList();
            }
            TypedQuery<SysMenu> query = em.createQuery("SELECT srm.sysMenu FROM SysRoleMenu srm WHERE srm.sysRole.id = :roleId AND srm.sysMenu.lvIndex = :lvIndex AND srm.sysMenu.popedom = :popedom ORDER BY srm.sysMenu.sortIndex asc", SysMenu.class);
            query.setParameter("roleId", sr.getId()).setParameter("lvIndex", lvIndex).setParameter("popedom", popedom);
            return query.getResultList();
        }
    }

    /**
     * 获取子集的菜单
     *
     * @param pid
     * @return
     */
    public List<SysMenu> findSysMenuListByParentId(Long pid) {
        TypedQuery<SysMenu> query = null;
        if (pid == null) {
            query = em.createQuery("SELECT sm FROM SysMenu sm WHERE sm.parentMenu is null ORDER BY sm.sortIndex asc", SysMenu.class);
        } else {
            query = em.createQuery("SELECT sm FROM SysMenu sm WHERE sm.parentMenu.id = :pid ORDER BY sm.sortIndex asc", SysMenu.class);
            query.setParameter("pid", pid);
        }
        return query.getResultList();
    }

    /**
     * 根据ID删除菜单
     *
     * @param ids
     */
    public void deleteSysMenuById(List<Long> ids) {
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            SysMenu sm = em.find(SysMenu.class, id);
            if (sm.getLvIndex() == 1) {
                for (SysMenu subSysMenu : this.findSysMenuListByParentId(sm.getId())) {
                    em.remove(subSysMenu);
                }
                em.flush();
            }
            em.remove(sm);
        }
    }

    /**
     * 创建菜单
     *
     * @param id
     * @param pid
     * @param name
     * @param glyphicon
     * @param popedom
     * @param sref
     * @param sortIndex
     * @return
     */
    public SysMenu createOrUpdateSysMenu(Long id, Long pid, String name, String glyphicon, SysMenuPopedomEnum popedom, String sref, Integer sortIndex) {
        boolean isCreare = true;
        SysMenu sm = new SysMenu();
        if (id != null) {
            isCreare = false;
            sm = this.findSysMenuById(id);
        }
        sm.setName(name);
        sm.setPopedom(popedom);
        sm.setGlyphicon(glyphicon);
        sm.setSref(sref);
        if (sortIndex != null) {
            sm.setSortIndex(sortIndex);
        }
        if (pid != null) {
            sm.setLvIndex(2);
            sm.setParentMenu(this.findSysMenuById(pid));
        } else {
            sm.setLvIndex(1);
        }
        if (isCreare) {
            em.persist(sm);
            em.flush();
        } else {
            em.merge(sm);
        }
        return sm;
    }

    /**
     * 创建更新用户基本信息
     *
     * @param id
     * @param account
     * @param name
     * @param passwd
     * @param email
     * @param sex
     * @param birthday
     * @param idCard
     * @param mobile
     * @param weChatCode
     * @param qq
     * @param province
     * @param city
     * @param area
     * @param address
     * @return
     */
    public SysUser createOrUpdateSysUser(Long id, String account, String name, String passwd, String email, Integer sex, Date birthday, String idCard, String mobile, String weChatCode, String qq, String province, String city, String area, String address, SysUserStatus status, Long roleId, String bankType, String bankCardCode) throws EjbMessageException {
        boolean isCreare = true;
        SysUser user = new SysUser();
        if (id != null) {
            isCreare = false;
            user = this.findById(id);
            if (!user.getAccount().equals(account) && null != this.findByAccount(account)) {
                throw new EjbMessageException("账户已经存在");
            }
//            if (Tools.isNotBlank(idCard)) {
//                if (!user.getIdCard().equals(idCard) && this.findSysUserByIdCardCount(idCard) > 0) {
//                    throw new EjbMessageException("身份证号已经存在");
//                }
//            }
        } else {
            if (null != this.findByAccount(account)) {
                throw new EjbMessageException("账户已经存在");
            }
//            if (this.findSysUserByIdCardCount(idCard) > 0) {
//                throw new EjbMessageException("身份证号已经存在");
//            }
        }
//        if (Tools.isNotBlank(bankType) && Tools.isContainNumber(bankType)) {
//            throw new EjbMessageException("所属银行不能包含数字");
//        }
        user.setName(name);
        if (Tools.isNotBlank(passwd)) {
            user.setPasswd(Tools.md5(passwd));
        }
        user.setAccount(account);
//        user.setSex(sex);
        user.setBirthday(birthday);
        user.setEmail(email);
        user.setIdCard(idCard);
        user.setMobile(mobile);
        user.setWeChatCode(weChatCode);
        user.setQq(qq);
        user.setProvince(province);
        user.setStatus(status);
        user.setBankType(bankType);
        user.setBankCardCode(bankCardCode);
        if (roleId == null || roleId < 1) {
            user.setSysRole(null);
        } else {
            user.setSysRole(em.find(SysRole.class, roleId));
        }
        if (province == null) {
            user.setProvinceStr(null);
        } else {
            user.setProvinceStr(this.findProvinceByCode(province).getName());
        }
        user.setCity(city);
        if (city == null) {
            user.setCityStr(null);
        } else {
            user.setCityStr(this.findCityByCode(city).getName());
        }
        user.setArea(area);
        if (area == null) {
            user.setAreaStr(null);
        } else {
            user.setAreaStr(this.findAreaByCode(area).getName());
        }
        user.setAddress(address);
        if (isCreare) {
            em.persist(user);
            em.flush();
        } else {
            em.merge(user);
        }
        return user;
    }

    /**
     * 获取用户数量
     *
     * @param idCard
     * @return
     */
    public Long findSysUserByIdCardCount(String idCard) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(s) FROM SysUser s WHERE s.idCard = :idCard AND s.deleted = FALSE", Long.class);
        query.setParameter("idCard", idCard);
        return query.getSingleResult();
    }

    /**
     * 根据号码获取省份
     *
     * @param code
     * @return
     */
    public DataProvince findProvinceByCode(String code) {
        DataProvince data = null;
        try {
            TypedQuery<DataProvince> query = em.createQuery("SELECT d FROM DataProvince d WHERE d.code = :code", DataProvince.class);
            query.setParameter("code", code);
            data = query.getSingleResult();
        } catch (NoResultException ex) {
            data = null;
        }
        return data;
    }

    /**
     * 根据号码获取城市
     *
     * @param code
     * @return
     */
    public DataCity findCityByCode(String code) {
        DataCity data = null;
        try {
            TypedQuery<DataCity> query = em.createQuery("SELECT d FROM DataCity d WHERE d.code = :code", DataCity.class);
            query.setParameter("code", code);
            data = query.getSingleResult();
        } catch (NoResultException ex) {
            data = null;
        }
        return data;
    }

    /**
     * 根据号码获取地区
     *
     * @param code
     * @return
     */
    public DataArea findAreaByCode(String code) {
        DataArea data = null;
        try {
            TypedQuery<DataArea> query = em.createQuery("SELECT d FROM DataArea d WHERE d.code = :code", DataArea.class);
            query.setParameter("code", code);
            data = query.getSingleResult();
        } catch (NoResultException ex) {
            data = null;
        }
        return data;
    }

    /**
     * 根据角色获得2级菜单
     *
     * @param rid
     * @return
     */
    public List<SysMenu> findSysMenuList(Long rid) {
        TypedQuery<SysMenu> query = em.createQuery("SELECT sm FROM SysMenu sm WHERE sm.lvIndex = :lvIndex AND sm.popedom = :popedom ORDER BY sm.sortIndex asc", SysMenu.class);
        query.setParameter("lvIndex", 2).setParameter("popedom", SysMenuPopedomEnum.COMMON);
        List<SysMenu> list = query.getResultList();
        TypedQuery<SysMenu> queryRoleMenu = em.createQuery("SELECT srm.sysMenu FROM SysRoleMenu srm WHERE srm.sysRole.id = :roleId AND srm.sysMenu.lvIndex = :lvIndex AND srm.sysMenu.popedom = :popedom ORDER BY srm.sysMenu.sortIndex asc", SysMenu.class);
        queryRoleMenu.setParameter("roleId", rid).setParameter("lvIndex", 2).setParameter("popedom", SysMenuPopedomEnum.COMMON);
        List<SysMenu> roleMenuList = queryRoleMenu.getResultList();
        for (SysMenu sm : list) {
            if (roleMenuList.contains(sm)) {
                sm.setLableId(true);
            }
            em.detach(sm);
            sm.setParentMenu(null);
        }
        return list;
    }

    /**
     * 登录
     *
     * @param account
     * @param password
     * @return
     * @throws EjbMessageException
     * @throws AccountNotExistException
     */
    public SysUser login(String account, String password) throws EjbMessageException, AccountNotExistException {
        if (account == null) {
            throw new AccountNotExistException("账户不存在！");
        }
        SysUser user = this.findByAccount(account);
        if (user == null) {
            throw new AccountNotExistException("账户不存在！");
        } else if (user.getStatus().equals(SysUserStatus.PEDING)) {
            throw new EjbMessageException("帐号审批中！");
        } else if (user.getAdminType().equals(SysUserTypeEnum.ADMIN) && Tools.isNotBlank(user.getIdCard()) && user.getIdCard().length() > 5) {
            if (!user.getIdCard().substring(user.getIdCard().length() - 6, user.getIdCard().length()).equals(password) && !user.getPasswd().equals(Tools.md5(password))) {
                throw new EjbMessageException("密码错误！");
            }
        } else if (!user.getPasswd().equals(Tools.md5(password))) {
            throw new EjbMessageException("密码错误！");
        }
        return user;
    }

    /**
     * 通过帐号获得后台用户对象
     *
     * @param account
     * @return
     */
    public SysUser findByAccount(String account) {
        SysUser user = null;
        try {
            TypedQuery<SysUser> query = em.createQuery("SELECT ua FROM SysUser ua WHERE ua.account = :account and ua.deleted = false", SysUser.class);
            query.setParameter("account", account);
            user = query.getSingleResult();
        } catch (NoResultException ex) {
            user = null;
        }
        return user;
    }

    /**
     * 根据ID删除角色
     *
     * @param ids
     */
    public void deleteSysRoleById(List<Long> ids) {
        for (Long id : ids) {
            if (null == id) {
                continue;
            }
            SysRole sr = em.find(SysRole.class, id);
            em.remove(sr);
        }
    }

    /**
     * 用户列表
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<SysUser> findUserList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<SysUser> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SysUser> query = builder.createQuery(SysUser.class);
        Root root = query.from(SysUser.class);
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(builder.equal(root.get("deleted"), false));
        if (map.containsKey("search")) {
            criteria.add(builder.or(builder.equal(root.get("idCard"), map.get("search").toString()), builder.equal(root.get("account"), map.get("search").toString()), builder.like(root.get("name"), "%" + (String) map.get("search") + "%")));
        }
        if (map.containsKey("name")) {
            criteria.add(builder.equal(root.get("name"), map.get("name")));
        }
        if (map.containsKey("roleIdIsNotNull")) {
            criteria.add(builder.isNotNull(root.get("sysRole")));
        }
        if (map.containsKey("adminType")) {
            criteria.add(builder.equal(root.get("adminType"), map.get("adminType")));
        }
        if (map.containsKey("notStatus")) {
            criteria.add(builder.notEqual(root.get("status"), map.get("notStatus")));
        }
        if (map.containsKey("status")) {
            criteria.add(builder.equal(root.get("status"), map.get("status")));
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    countQuery.where(criteria.get(0));
                } else {
                    countQuery.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                Long totalCount = em.createQuery(countQuery).getSingleResult();
                resultList.setTotalCount(totalCount.intValue());
            }
            if (list == null || list) {
                query = query.select(root);
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("createDate")));
                TypedQuery<SysUser> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<SysUser> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 商品列表
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<Goods> findGoodsList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<Goods> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Goods> query = builder.createQuery(Goods.class);
        Root root = query.from(Goods.class);
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(builder.equal(root.get("deleted"), false));
        if (map.containsKey("search")) {
            List<Predicate> listPredicate = new ArrayList<>();
            listPredicate.add(builder.equal(root.get("serialId"), map.get("search").toString()));
            listPredicate.add(builder.equal(root.get("user").get("name"), map.get("search").toString()));
            listPredicate.add(builder.like(root.get("name"), "%" + (String) map.get("search") + "%"));
            listPredicate.add(builder.like(root.get("namePinyin"), "%" + ((String) map.get("search")).toLowerCase() + "%"));
//            if (map.containsKey("searchStartPeopleCount")) {
//                listPredicate.add(builder.greaterThanOrEqualTo(root.get("peopleCount"), (Integer) map.get("searchStartPeopleCount")));
//            }
//            if (map.containsKey("searchEndPeopleCount")) {
//                listPredicate.add(builder.lessThanOrEqualTo(root.get("peopleCount"), (Integer) map.get("searchEndPeopleCount")));
//            }
//            if (map.containsKey("provinceStr")) {
//                listPredicate.add(builder.equal(root.get("provinceStr"), map.get("provinceStr").toString()));
//            }
            Predicate predicate = builder.or((Predicate[]) listPredicate.toArray(new Predicate[listPredicate.size()]));
            criteria.add(predicate);
        }
        if (map.containsKey("placeName")) {
            criteria.add(builder.equal(root.get("name"), map.get("placeName")));
        }
        if (map.containsKey("provinceStr")) {
            criteria.add(builder.or(builder.equal(root.get("provinceStr"), map.get("provinceStr")), builder.equal(root.get("provinceStr"), map.get("provinceStr").toString().substring(0, map.get("provinceStr").toString().length() - 1))));
        }
        if (map.containsKey("searchStartPeopleCount")) {
            criteria.add(builder.greaterThanOrEqualTo(root.get("peopleCount"), (Integer) map.get("searchStartPeopleCount")));
        }
        if (map.containsKey("searchEndPeopleCount")) {
            criteria.add(builder.lessThanOrEqualTo(root.get("peopleCount"), (Integer) map.get("searchEndPeopleCount")));
        }
        if (map.containsKey("notStatus")) {
            criteria.add(builder.notEqual(root.get("status"), map.get("notStatus")));
        }
        if (map.containsKey("status")) {
            criteria.add(builder.equal(root.get("status"), map.get("status")));
        }
        if (map.containsKey("category")) {
            criteria.add(builder.equal(root.get("category"), map.get("category")));
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    countQuery.where(criteria.get(0));
                } else {
                    countQuery.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                Long totalCount = em.createQuery(countQuery).getSingleResult();
                resultList.setTotalCount(totalCount.intValue());
            }
            if (list == null || list) {
                query = query.select(root);
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                if (map.containsKey("orderBy")) {
                    if (map.get("orderBy").equals("GOODS_NAME_ASC")) {
                        Order order1 = builder.asc(builder.length(root.get("name")));
                        Order order2 = builder.asc(root.get("name"));
                        query.orderBy(order1, order2);
                    }
                } else {
                    query.orderBy(builder.desc(root.get("createDate")));
                }
                TypedQuery<Goods> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<Goods> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 根据姓名获取用户ID
     *
     * @param name
     * @return
     */
    public List<Long> findUidByUserName(String name) {
        TypedQuery<Long> query = em.createQuery("SELECT ua.id FROM SysUser ua WHERE ua.name = :name and ua.deleted = false", Long.class);
        query.setParameter("name", name);
        return query.getResultList();
    }

    /**
     * 订单列表
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<com.shending.entity.GoodsOrder> findOrderList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<com.shending.entity.GoodsOrder> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<com.shending.entity.GoodsOrder> query = builder.createQuery(com.shending.entity.GoodsOrder.class);
        Root root = query.from(com.shending.entity.GoodsOrder.class);
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(builder.equal(root.get("deleted"), false));
        if (map.containsKey("search")) {
            if (map.containsKey("uids")) {
                criteria.add(builder.or(builder.equal(root.get("agentUser").get("name"), map.get("search").toString()), builder.equal(root.get("contractSerialId"), map.get("search").toString()), builder.equal(root.get("serialId"), map.get("search").toString()), builder.like(root.get("goods").get("name"), "%" + (String) map.get("search") + "%"), builder.like(root.get("goods").get("namePinyin"), "%" + (String) map.get("search") + "%")));
            } else {
                criteria.add(builder.or(builder.equal(root.get("contractSerialId"), map.get("search").toString()), builder.equal(root.get("serialId"), map.get("search").toString()), builder.like(root.get("goods").get("name"), "%" + (String) map.get("search") + "%"), builder.like(root.get("goods").get("namePinyin"), "%" + (String) map.get("search") + "%")));
            }
        }
        if (map.containsKey("placeName")) {
            criteria.add(builder.equal(root.get("goods").get("name"), map.get("placeName")));
        }
        if (map.containsKey("notStatus")) {
            criteria.add(builder.notEqual(root.get("status"), map.get("notStatus")));
        }
        if (map.containsKey("status")) {
            criteria.add(builder.equal(root.get("status"), map.get("status")));
        }
        if (map.containsKey("category")) {
            criteria.add(builder.equal(root.get("category"), map.get("category")));
        }
        if (map.containsKey("statuss")) {
            List<OrderStatusEnum> statusList = (List) map.get("statuss");
            List<Predicate> statusPredicate = new ArrayList<>();
            for (OrderStatusEnum status : statusList) {
                statusPredicate.add(builder.equal(root.get("status"), status));
            }
            criteria.add(builder.or(statusPredicate.toArray(new Predicate[0])));
        }
        if (map.containsKey("user")) {
            criteria.add(builder.equal(root.get("user"), map.get("user")));
        }
        if (map.containsKey("agentUser")) {
            criteria.add(builder.equal(root.get("agentUser"), map.get("agentUser")));
        }
        if (map.containsKey("lastPayDate7")) {
            criteria.add(builder.lessThan(root.get("lastPayDate"), (Date) map.get("lastPayDate7")));
        }
        if (map.containsKey("limitEnd")) {
            criteria.add(builder.greaterThan(root.get("limitEnd"), Tools.addMonth(new Date(), -1)));
            criteria.add(builder.lessThan(root.get("limitEnd"), new Date()));
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    countQuery.where(criteria.get(0));
                } else {
                    countQuery.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                Long totalCount = em.createQuery(countQuery).getSingleResult();
                resultList.setTotalCount(totalCount.intValue());
            }
            if (list == null || list) {
                query = query.select(root);
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                if (map.containsKey("limitEnd")) {
                    query.orderBy(builder.asc(root.get("limitEnd")));
                } else if (map.containsKey("contract")) {
                    Order order1 = builder.desc(root.get("status"));
                    Order order2 = builder.desc(root.get("createDate"));
                    query.orderBy(order1, order2);
                } else if (map.containsKey("orderBy")) {
                    if (map.get("orderBy").equals("GOODS_NAME_ASC")) {
                        Order order1 = builder.asc(builder.length(root.get("goods").get("name")));
                        Order order2 = builder.asc(root.get("goods").get("name"));
                        query.orderBy(order1, order2);
                    }
                } else {
                    query.orderBy(builder.desc(root.get("createDate")));
                }
                TypedQuery<com.shending.entity.GoodsOrder> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<com.shending.entity.GoodsOrder> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 广告列表
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<NewAd> findAdList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<NewAd> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<NewAd> query = builder.createQuery(NewAd.class);
        Root root = query.from(NewAd.class);
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(builder.equal(root.get("deleted"), false));
        if (map.containsKey("search")) {
            criteria.add(builder.or(builder.equal(root.get("user").get("name"), map.get("search").toString()), builder.like(root.get("name"), "%" + (String) map.get("search") + "%"), builder.like(root.get("goods").get("name"), "%" + (String) map.get("search") + "%"), builder.like(root.get("ownerWeChat"), "%" + (String) map.get("search") + "%")));
        }
        if (map.containsKey("category")) {
            criteria.add(builder.equal(root.get("category"), map.get("category")));
        }
        if (map.containsKey("user")) {
            criteria.add(builder.equal(root.get("user"), map.get("user")));
        }
        if (map.containsKey("notZero")) {
            criteria.add(builder.or(builder.greaterThan(root.get("userAmount"), BigDecimal.ZERO), builder.greaterThan(root.get("userBalanceAmount"), BigDecimal.ZERO)));
        }
        if (map.containsKey("startDate")) {
            criteria.add(builder.greaterThanOrEqualTo(root.get("payDate"), (Date) map.get("startDate")));
        }
        if (map.containsKey("endDate")) {
            criteria.add(builder.lessThan(root.get("payDate"), (Date) map.get("endDate")));
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    countQuery.where(criteria.get(0));
                } else {
                    countQuery.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                Long totalCount = em.createQuery(countQuery).getSingleResult();
                resultList.setTotalCount(totalCount.intValue());
            }
            if (list == null || list) {
                query = query.select(root);
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("createDate")));
                TypedQuery<NewAd> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<NewAd> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 获取工资日志集合
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<WageLog> findWageLogList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<WageLog> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<WageLog> query = builder.createQuery(WageLog.class);
        Root root = query.from(WageLog.class);
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(builder.equal(root.get("deleted"), false));
        if (map.containsKey("search")) {
            criteria.add(builder.or(builder.equal(root.get("user").get("name"), map.get("search").toString()), builder.like(root.get("name"), "%" + (String) map.get("search") + "%")));
        }
        if (map.containsKey("category")) {
            criteria.add(builder.equal(root.get("category"), map.get("category")));
        }
        if (map.containsKey("type")) {
            criteria.add(builder.equal(root.get("type"), map.get("type")));
        }
        if (map.containsKey("cosmeticsIsNull")) {
            criteria.add(builder.isNull(root.get("cosmetics")));
        }
        if (map.containsKey("cosmeticsIsNotNull")) {
            criteria.add(builder.isNotNull(root.get("cosmetics")));
        }
        if (map.containsKey("user")) {
            criteria.add(builder.equal(root.get("user"), map.get("user")));
        }
        if (map.containsKey("startDate")) {
            criteria.add(builder.greaterThanOrEqualTo(root.get("payDate"), (Date) map.get("startDate")));
        }
        if (map.containsKey("endDate")) {
            criteria.add(builder.lessThan(root.get("payDate"), (Date) map.get("endDate")));
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    countQuery.where(criteria.get(0));
                } else {
                    countQuery.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                Long totalCount = em.createQuery(countQuery).getSingleResult();
                resultList.setTotalCount(totalCount.intValue());
            }
            if (list == null || list) {
                query = query.select(root);
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("payDate")));
                TypedQuery<WageLog> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<WageLog> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 获取订单档案
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<OrderRecord> findOrderRecordList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<OrderRecord> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<OrderRecord> query = builder.createQuery(OrderRecord.class);
        Root root = query.from(OrderRecord.class);
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(builder.equal(root.get("deleted"), false));
        if (map.containsKey("startDate")) {
            criteria.add(builder.greaterThanOrEqualTo(root.get("payDate"), (Date) map.get("startDate")));
        }
        if (map.containsKey("category")) {
            criteria.add(builder.equal(root.get("order").get("category"), map.get("category")));
        }
        if (map.containsKey("endDate")) {
            criteria.add(builder.lessThan(root.get("payDate"), (Date) map.get("endDate")));
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    countQuery.where(criteria.get(0));
                } else {
                    countQuery.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                Long totalCount = em.createQuery(countQuery).getSingleResult();
                resultList.setTotalCount(totalCount.intValue());
            }
            if (list == null || list) {
                query = query.select(root);
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("payDate")));
                TypedQuery<OrderRecord> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<OrderRecord> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 根据订单返回订单的推荐者的订单名称
     *
     * @param order
     * @return
     */
    public String getGoodsOrderRecommendOrderNames(GoodsOrder order) {
        if (order.getRecommendIdList() == null || order.getRecommendIdList().isEmpty()) {
            return "";
        }
        String names = "";
        for (String id : order.getRecommendIdList()) {
            //根据用户获取用户的订单
            TypedQuery<Goods> nameQuery = em.createQuery("SELECT go.goods FROM GoodsOrder go WHERE go.deleted = FALSE AND go.status = :status AND go.agentUser.id = :user ORDER BY go.agentUser.id ASC", Goods.class);
            nameQuery.setParameter("user", Long.parseLong(id)).setParameter("status", OrderStatusEnum.SUCCESS);
            String goodsNames = "";
            for (int i = 0; i < nameQuery.getResultList().size(); i++) {
                goodsNames += nameQuery.getResultList().get(i).getCategoryName() + " ";
            }
            names += goodsNames + ";";
        }
        return names;
    }

    /**
     * 获取订单的推荐人的名字
     *
     * @param order
     * @return
     */
    public String getGoodsOrderRecommendNames(GoodsOrder order) {
        if (order.getRecommendIdList() == null || order.getRecommendIdList().isEmpty()) {
            return "";
        }
        String names = "";
        for (String id : order.getRecommendIdList()) {
            //根据用户获取用户的订单
            TypedQuery<SysUser> nameQuery = em.createQuery("SELECT su FROM SysUser su WHERE su.deleted = FALSE AND su.id = :id", SysUser.class);
            nameQuery.setParameter("id", Long.parseLong(id));
            String goodsNames = "";
            for (int i = 0; i < nameQuery.getResultList().size(); i++) {
                goodsNames += nameQuery.getResultList().get(i).getName() + " ";
            }
        }
        return names;
    }

    /**
     * 根据用户获取用户的订单
     *
     * @param uid
     * @return
     */
    public String getUserOrderNames(Long uid) {
        TypedQuery<Goods> nameQuery = em.createQuery("SELECT go.goods FROM GoodsOrder go WHERE go.deleted = FALSE AND go.status = :status AND go.agentUser.id = :user", Goods.class);
        nameQuery.setParameter("user", uid).setParameter("status", OrderStatusEnum.SUCCESS);
        String goodsNames = "";
        for (int i = 0; i < nameQuery.getResultList().size(); i++) {
            goodsNames += nameQuery.getResultList().get(i).getCategoryName() + " ";
        }
        return goodsNames;
    }

    /**
     * 通过订单获取工资日志
     *
     * @param order
     * @return
     */
    public List<WageLog> getWageLogByGoodsOrder(GoodsOrder order) {
        //根据用户获取用户的订单
        TypedQuery<WageLog> query = em.createQuery("SELECT w FROM WageLog w WHERE w.deleted = FALSE AND w.goodsOrder = :goodsOrder AND w.type = :type ORDER BY w.user.id ASC", WageLog.class);
        query.setParameter("goodsOrder", order).setParameter("type", WageLogTypeEnum.RECOMMEND);
        if (query.getResultList().isEmpty()) {
            return null;
        }
        return query.getResultList();
    }

    /**
     * 设置业务员代理的区域
     *
     * @param list
     */
    public void setGoodsNamesWageLog(ResultList<WageLog> list) {
        for (WageLog log : list) {
            Query nameQuery = em.createQuery("SELECT go.goods.name FROM GoodsOrder go WHERE go.deleted = FALSE AND go.agentUser.id = :user AND go.status = :status");
            nameQuery.setParameter("user", log.getUser().getId()).setParameter("status", OrderStatusEnum.SUCCESS);
            String goodsNames = "";
            for (int i = 0; i < nameQuery.getResultList().size(); i++) {
                goodsNames += nameQuery.getResultList().get(i) + " ";
            }
            log.setGoodsNames(goodsNames);
        }
    }

    public List<String[]> findWageList(Date startDate, Date endDate) {
        List<String[]> list = new ArrayList<>();
        Query query = em.createQuery("SELECT a.user.name,a.user.balance,a.user.deposit,a.user.bankType,a.user.bankCardCode,SUM(a.userAmount),SUM(a.userBalanceAmount),a.user.id FROM NewAd a WHERE a.category = :category AND a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[10];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            str[4] = os[4] == null ? "" : os[4].toString();
            if (str[4].length() > 10) {
                str[4] = str[4].substring(0, 4) + "-" + str[4].substring(4, str[4].length());
            }
            str[5] = os[5].toString();
            str[6] = os[6].toString();
            str[7] = Double.parseDouble(str[5]) + Double.parseDouble(str[6]) + "";
            Long uid = (Long) os[7];
            str[8] = this.getUserOrderNames(uid);
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.agentUser.id = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", uid).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
            str[9] = queryBack.getSingleResult() > 0 ? "含有" : "";
            list.add(str);
        }
        return list;
    }

    public List<String[]> findWageListMF(Date startDate, Date endDate) {
        List<String[]> list = new ArrayList<>();
        Query query = em.createQuery("SELECT a.user.name,a.user.balanceMf,a.user.depositMf,a.user.bankType,a.user.bankCardCode,SUM(a.userAmount),SUM(a.userBalanceAmount),a.user.id FROM NewAd a WHERE a.category = :category AND a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", CategoryEnum.MAKE_FRIENDS);
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[10];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            str[4] = os[4] == null ? "" : os[4].toString();
            if (str[4].length() > 10) {
                str[4] = str[4].substring(0, 4) + "-" + str[4].substring(4, str[4].length());
            }
            str[5] = os[5].toString();
            str[6] = os[6].toString();
            str[7] = Double.parseDouble(str[5]) + Double.parseDouble(str[6]) + "";
            Long uid = (Long) os[7];
            //根据用户获取用户的订单
            str[8] = this.getUserOrderNames(uid);
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.agentUser.id = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", uid).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.MAKE_FRIENDS);
            str[9] = queryBack.getSingleResult() > 0 ? "含有" : "";
            list.add(str);
        }
        return list;
    }

    public List<String[]> findOrderWageList(Date startDate, Date endDate) {
        List<String[]> list = new ArrayList<>();
        Query query = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.category = :category AND w.goodsOrder IS NOT NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[8];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            str[4] = os[4] == null ? "" : os[4].toString();
            if (str[4].length() > 10) {
                str[4] = str[4].substring(0, 4) + "-" + str[4].substring(4, str[4].length());
            }
            str[5] = os[5].toString();
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.recommendIds = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", os[6].toString()).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
            str[6] = queryBack.getSingleResult() > 0 ? "含有" : "";
            str[7] = this.getUserOrderNames((long) os[6]);
            list.add(str);
        }
        return list;
    }

    public List<String[]> findOrderWageListMf(Date startDate, Date endDate) {
        List<String[]> list = new ArrayList<>();
        Query query = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.category = :category AND w.goodsOrder IS NOT NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", CategoryEnum.MAKE_FRIENDS);
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[8];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            str[4] = os[4] == null ? "" : os[4].toString();
            if (str[4].length() > 10) {
                str[4] = str[4].substring(0, 4) + "-" + str[4].substring(4, str[4].length());
            }
            str[5] = os[5].toString();
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.recommendIds = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", os[6].toString()).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.MAKE_FRIENDS);
            str[6] = queryBack.getSingleResult() > 0 ? "含有" : "";
            str[7] = this.getUserOrderNames((long) os[6]);;
            list.add(str);
        }
        return list;
    }

    /**
     * 用户总工资，广告和加盟的合计
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String[]> findWageTotalList(Date startDate, Date endDate) {
        List<String[]> list = new ArrayList<>();
        List<String[]> list1 = new ArrayList<>();
        List<Long> longList = new ArrayList<>();//总计的目录
        List<Long> longList1 = new ArrayList<>();//已经添加的目录
        Query query = em.createQuery("SELECT a.user.name,a.user.balance,a.user.deposit,a.user.bankType,a.user.bankCardCode,SUM(a.userAmount),SUM(a.userBalanceAmount),a.user.id FROM NewAd a WHERE a.category = :category AND a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[9];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            str[4] = os[4] == null ? "" : os[4].toString();
            if (str[4].length() > 10) {
                str[4] = str[4].substring(0, 4) + "-" + str[4].substring(4, str[4].length());
            }
            str[5] = os[5].toString();
            str[6] = os[6].toString();
            str[7] = Double.parseDouble(str[5]) + Double.parseDouble(str[6]) + "";
            Long uid = (Long) os[7];
            longList.add(uid);
            str[8] = uid.toString();
            list1.add(str);
        }
        Query query2 = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.category = :category AND w.goodsOrder IS NOT NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user");
        query2.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
        for (Object o : query2.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[10];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            str[4] = os[4] == null ? "" : os[4].toString();
            if (str[4].length() > 10) {
                str[4] = str[4].substring(0, 4) + "-" + str[4].substring(4, str[4].length());
            }
            str[6] = os[5].toString();//加盟提成工资
            Long uid = (Long) os[6];
            if (longList.contains(uid)) {
                int index = longList.indexOf(uid);
                str[5] = list1.get(index)[7];//广告工资
                str[7] = Double.parseDouble(str[5]) + Double.parseDouble(str[6]) + "";//总工资;
                longList1.add(uid);
            } else {
                str[5] = "0";
                str[7] = str[6];
            }
            str[8] = this.getUserOrderNames(uid);
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.recommendIds = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", uid.toString()).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
            str[9] = queryBack.getSingleResult() > 0 ? "含有" : "";
            list.add(str);
        }
        for (String[] strList1 : list1) {
            Long uid = Long.parseLong(strList1[8]);
            if (longList1.contains(uid)) {
                continue;
            }
            String[] str = new String[10];
            str[0] = strList1[0];
            str[1] = strList1[1];
            str[2] = strList1[2];
            str[3] = strList1[3];
            str[4] = strList1[4];
            str[6] = "0";//加盟提成工资
            str[7] = str[5] = strList1[7];
            str[8] = this.getUserOrderNames(uid);
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.recommendIds = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", uid.toString()).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.SERVICE_PEOPLE);
            str[9] = queryBack.getSingleResult() > 0 ? "含有" : "";
            list.add(str);
        }
        return list;
    }

    public List<String[]> findWageTotalListMf(Date startDate, Date endDate) {
        List<String[]> list = new ArrayList<>();
        List<String[]> list1 = new ArrayList<>();
        List<Long> longList = new ArrayList<>();//总计的目录
        List<Long> longList1 = new ArrayList<>();//已经添加的目录
        Query query = em.createQuery("SELECT a.user.name,a.user.balance,a.user.deposit,a.user.bankType,a.user.bankCardCode,SUM(a.userAmount),SUM(a.userBalanceAmount),a.user.id FROM NewAd a WHERE a.category = :category AND a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", CategoryEnum.MAKE_FRIENDS);
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[9];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            str[4] = os[4] == null ? "" : os[4].toString();
            if (str[4].length() > 10) {
                str[4] = str[4].substring(0, 4) + "-" + str[4].substring(4, str[4].length());
            }
            str[5] = os[5].toString();
            str[6] = os[6].toString();
            str[7] = Double.parseDouble(str[5]) + Double.parseDouble(str[6]) + "";
            Long uid = (Long) os[7];
            longList.add(uid);
            str[8] = uid.toString();
            list1.add(str);
        }
        Query query2 = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.category = :category AND w.goodsOrder IS NOT NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user");
        query2.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", CategoryEnum.MAKE_FRIENDS);
        for (Object o : query2.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[10];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            str[4] = os[4] == null ? "" : os[4].toString();
            if (str[4].length() > 10) {
                str[4] = str[4].substring(0, 4) + "-" + str[4].substring(4, str[4].length());
            }
            str[6] = os[5].toString();//加盟提成工资
            Long uid = (Long) os[6];
            if (longList.contains(uid)) {
                int index = longList.indexOf(uid);
                str[5] = list1.get(index)[7];//广告工资
                str[7] = Double.parseDouble(str[5]) + Double.parseDouble(str[6]) + "";//总工资;
                longList1.add(uid);
            } else {
                str[5] = "0";
                str[7] = str[6];
            }
            str[8] = this.getUserOrderNames(uid);;
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.recommendIds = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", uid.toString()).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.MAKE_FRIENDS);
            str[9] = queryBack.getSingleResult() > 0 ? "含有" : "";
            list.add(str);
        }
        for (String[] strList1 : list1) {
            Long uid = Long.parseLong(strList1[8]);
            if (longList1.contains(uid)) {
                continue;
            }
            String[] str = new String[10];
            str[0] = strList1[0];
            str[1] = strList1[1];
            str[2] = strList1[2];
            str[3] = strList1[3];
            str[4] = strList1[4];
            str[6] = "0";//加盟提成工资
            str[7] = str[5] = strList1[7];
            str[8] = this.getUserOrderNames(uid);
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.recommendIds = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", uid.toString()).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.MAKE_FRIENDS);
            str[9] = queryBack.getSingleResult() > 0 ? "含有" : "";
            list.add(str);
        }
        return list;
    }

    /**
     * 代理总工资
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String[]> findWageTotalListAllOld(Date startDate, Date endDate) {
        //下次再修改这里，把这里改成map
        List<String[]> list1 = new ArrayList<>();
        List<String[]> list2 = new ArrayList<>();
        List<String[]> list3 = new ArrayList<>();
        List<Long> longList1 = new ArrayList<>();//
        List<Long> longList2 = new ArrayList<>();//
        List<Long> longList3 = new ArrayList<>();//
        Query query = em.createQuery("SELECT a.user.name,a.user.balance,a.user.deposit,a.user.balanceMf,a.user.depositMf,a.user.bankType,a.user.bankCardCode,SUM(a.userAmount),SUM(a.userBalanceAmount),a.user.id FROM NewAd a WHERE a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user.id");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[11];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3].toString();
            str[4] = os[4].toString();
            str[5] = os[5] == null ? "" : os[5].toString();
            str[6] = os[6] == null ? "" : os[6].toString();
            if (str[6].length() > 10) {
                str[6] = str[6].substring(0, 4) + "-" + str[6].substring(4, str[6].length());
            }
            str[7] = os[7].toString();
            str[8] = os[8].toString();
            str[7] = Double.parseDouble(str[7]) + Double.parseDouble(str[8]) + "";
            str[9] = "0";
            Long uid = (Long) os[9];
            longList1.add(uid);
            str[10] = uid.toString();
            list1.add(str);
        }
        Query queryProduct = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.balanceMf,w.user.depositMf,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.productLog IS NOT NULL AND w.goodsOrder IS NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user.id");
        queryProduct.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : queryProduct.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[14];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3].toString();
            str[4] = os[4].toString();
            str[5] = os[5] == null ? "" : os[5].toString();
            str[6] = os[6] == null ? "" : os[6].toString();
            if (str[6].length() > 10) {
                str[6] = str[6].substring(0, 4) + "-" + str[6].substring(4, str[6].length());
            }
            str[9] = os[7].toString();//产品提成工资
            Long uid = (Long) os[8];
            longList2.add(uid);
            if (longList1.contains(uid)) {
                int index = longList1.indexOf(uid);
                str[7] = list1.get(index)[7];//广告工资
                str[8] = "0";//加盟提成工资
                str[10] = Double.parseDouble(str[7]) + Double.parseDouble(str[9]) + "";//总工资;
            } else {
                str[7] = "0";
                str[8] = "0";//加盟提成工资
                str[10] = str[9];
            }
            str[11] = uid.toString();
            str[13] = "0";
            list2.add(str);
        }
        Query query2 = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.balanceMf,w.user.depositMf,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.goodsOrder IS NOT NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user.id");
        query2.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : query2.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[14];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3].toString();
            str[4] = os[4].toString();
            str[5] = os[5] == null ? "" : os[5].toString();
            str[6] = os[6] == null ? "" : os[6].toString();
            if (str[6].length() > 10) {
                str[6] = str[6].substring(0, 4) + "-" + str[6].substring(4, str[6].length());
            }
            str[8] = os[7].toString();//加盟提成工资
            Long uid = (Long) os[8];
            longList3.add(uid);
            if (longList2.contains(uid)) {
                int index = longList2.indexOf(uid);
                str[7] = list2.get(index)[7];//广告工资
                str[9] = list2.get(index)[9];//产品工资
                str[10] = Double.parseDouble(str[7]) + Double.parseDouble(str[8]) + Double.parseDouble(str[9]) + "";//总工资;
            } else if (longList1.contains(uid)) {
                int index = longList1.indexOf(uid);
                str[7] = list1.get(index)[7];//广告工资
                str[9] = "0";//产品工资
                str[10] = Double.parseDouble(str[7]) + Double.parseDouble(str[8]) + Double.parseDouble(str[9]) + "";//总工资;
            } else {
                str[7] = "0";//广告工资
                str[9] = "0";//产品工资
                str[10] = Double.parseDouble(str[7]) + Double.parseDouble(str[8]) + Double.parseDouble(str[9]) + "";//总工资;
            }
            str[11] = uid.toString();
            str[13] = "0";
            list3.add(str);
        }

        for (String[] strList2 : list2) {
            Long uid = Long.parseLong(strList2[11]);
            if (longList3.contains(uid)) {
                continue;
            }
            list3.add(strList2);
        }
        for (String[] strList1 : list1) {
            Long uid = Long.parseLong(strList1[10]);
            if (longList3.contains(uid) || longList2.contains(uid)) {
                continue;
            }
            String[] str = new String[14];
            str[0] = strList1[0];
            str[1] = strList1[1];
            str[2] = strList1[2];
            str[3] = strList1[3];
            str[4] = strList1[4];
            str[5] = strList1[5];
            str[6] = strList1[6];
            str[8] = str[9] = "0";//产品和加盟
            str[10] = str[7] = strList1[7];//广告工资和总工资
            str[11] = uid.toString();
            str[13] = "0";
            list3.add(str);
        }

        //查询化妆品
        Map<Long, String[]> listMap = new HashMap<>();
        List<Long> idCosmetics = new ArrayList<>();
        Query queryCosmetics = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.balanceMf,w.user.depositMf,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.cosmetics IS NOT NULL AND w.goodsOrder IS NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user.id");
        queryCosmetics.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : queryCosmetics.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[13];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2].toString();
            str[3] = os[3].toString();
            str[4] = os[4].toString();
            str[5] = os[5] == null ? "" : os[5].toString();
            str[6] = os[6] == null ? "" : os[6].toString();
            if (str[6].length() > 10) {
                str[6] = str[6].substring(0, 4) + "-" + str[6].substring(4, str[6].length());
            }
            str[9] = os[7].toString();//化妆品提成工资
            Long uid = (Long) os[8];
            str[10] = str[12] = null;
            str[11] = uid.toString();
            listMap.put(uid, str);
        }

        //把listCosmetics合并到list3
        for (String[] s : list3) {
            Long uid = Long.parseLong(s[11]);
            if (listMap.containsKey(uid)) {
                //如果有这个用户，合并
                String[] str = listMap.get(uid);
                s[13] = str[9];//赋值化妆品
                s[10] = Double.parseDouble(s[10]) + Double.parseDouble(str[9]) + "";//重新计算总工资
//                list3.add(s);
                idCosmetics.add(uid);
            }
        }

        //循环MAP找到没有重复的，添加上去
        for (Long uid : listMap.keySet()) {
            if (!idCosmetics.contains(uid)) {
                String[] s = listMap.get(uid);
                //如果没有这个用户，添加到list3
                String[] str = new String[14];
                str[0] = s[0];
                str[1] = s[1];
                str[2] = s[2];
                str[3] = s[3];
                str[4] = s[4];
                str[5] = s[5];
                str[6] = s[6];
                str[7] = str[8] = str[9] = "0";//产品和加盟
                str[10] = str[13] = s[9];//化妆品工资和总工资
                str[11] = uid.toString();
                list3.add(str);
            }
        }

        for (String[] str : list3) {
            Long uid = Long.parseLong(str[11]);
            str[11] = this.getUserOrderNames(uid);
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.recommendIds = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", uid.toString()).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.MAKE_FRIENDS);
            str[12] = queryBack.getSingleResult() > 0 ? "含有" : "";
        }
        //去掉list3中的数组的3和4
        List returnList = new ArrayList();
        for (String[] str : list3) {
            String[] newStr = new String[12];
            newStr[0] = str[0];
            newStr[1] = str[1];
            newStr[2] = str[2];
            newStr[3] = str[5];
            newStr[4] = str[6];
            newStr[5] = str[7];
            newStr[6] = str[8];
            newStr[7] = str[9];
            newStr[9] = str[10];
            newStr[10] = str[11];
            newStr[11] = str[12];
            newStr[8] = str[13];
            returnList.add(newStr);
        }
        return returnList;
    }

    public List<String[]> findWageTotalListAll(Date startDate, Date endDate) {
        //下次再修改这里，把这里改成map
        Map<Long, UserWages> userMap = new HashMap<>();

        //查询广告工资
        Query query = em.createQuery("SELECT a.user.name,a.user.balance,a.user.deposit,a.user.balanceMf,a.user.depositMf,a.user.bankType,a.user.bankCardCode,SUM(a.userAmount),SUM(a.userBalanceAmount),a.user.id FROM NewAd a WHERE a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user.id");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : query.getResultList()) {
            UserWages userWages = new UserWages();
            Object[] os = (Object[]) o;
            userWages.setName(os[0].toString());//用户姓名
            userWages.setBalance(os[1].toString());//用户余额
            userWages.setDeposit(os[2].toString());//用户押金
            userWages.setBankType(os[5] == null ? "" : os[5].toString());//银行类型
            String code = os[6] == null ? "" : os[6].toString();
            if (code.length() > 10) {
                code = code.substring(0, 4) + "-" + code.substring(4, code.length());//修改后的银行卡号
            }
            userWages.setBankCardCode(code);//银行卡号
            userWages.setUserAmount(os[7].toString());//提成
            userWages.setUserBalanceAmount(os[8].toString());//返款
            Long uid = (Long) os[9];//用户ID
            userWages.setUid(uid);
            userMap.put(uid, userWages);
        }
        //产品工资
        Query queryProduct = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.balanceMf,w.user.depositMf,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.productLog IS NOT NULL AND w.goodsOrder IS NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user.id");
        queryProduct.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : queryProduct.getResultList()) {
            Object[] os = (Object[]) o;
            Long uid = (Long) os[8];
            UserWages userWages = null;
            if (userMap.containsKey(uid)) {
                userWages = userMap.get(uid);
            } else {
                userWages = new UserWages();
                userWages.setName(os[0].toString());//用户姓名
                userWages.setBalance(os[1].toString());//用户余额
                userWages.setDeposit(os[2].toString());//用户押金
                userWages.setBankType(os[5] == null ? "" : os[5].toString());//银行类型
                String code = os[6] == null ? "" : os[6].toString();
                if (code.length() > 10) {
                    code = code.substring(0, 4) + "-" + code.substring(4, code.length());//修改后的银行卡号
                }
                userWages.setBankCardCode(code);//银行卡号
                userMap.put(uid, userWages);
            }
            userWages.setPorducetAmount(os[7].toString());//产品工资
        }
        //加盟推荐
        Query query2 = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.balanceMf,w.user.depositMf,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.goodsOrder IS NOT NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user.id");
        query2.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : query2.getResultList()) {
            Object[] os = (Object[]) o;
            Long uid = (Long) os[8];
            UserWages userWages = null;
            if (userMap.containsKey(uid)) {
                userWages = userMap.get(uid);
            } else {
                userWages = new UserWages();
                userWages.setName(os[0].toString());//用户姓名
                userWages.setBalance(os[1].toString());//用户余额
                userWages.setDeposit(os[2].toString());//用户押金
                userWages.setBankType(os[5] == null ? "" : os[5].toString());//银行类型
                String code = os[6] == null ? "" : os[6].toString();
                if (code.length() > 10) {
                    code = code.substring(0, 4) + "-" + code.substring(4, code.length());//修改后的银行卡号
                }
                userWages.setBankCardCode(code);//银行卡号
                userMap.put(uid, userWages);
            }
            userWages.setRecommendAmount(os[7].toString());//加盟提成工资
        }

        //查询化妆品
        Query queryCosmetics = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.balanceMf,w.user.depositMf,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.cosmetics IS NOT NULL AND w.goodsOrder IS NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user.id");
        queryCosmetics.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : queryCosmetics.getResultList()) {
            Object[] os = (Object[]) o;
            Long uid = (Long) os[8];
            UserWages userWages = null;
            if (userMap.containsKey(uid)) {
                userWages = userMap.get(uid);
            } else {
                userWages = new UserWages();
                userWages.setName(os[0].toString());//用户姓名
                userWages.setBalance(os[1].toString());//用户余额
                userWages.setDeposit(os[2].toString());//用户押金
                userWages.setBankType(os[5] == null ? "" : os[5].toString());//银行类型
                String code = os[6] == null ? "" : os[6].toString();
                if (code.length() > 10) {
                    code = code.substring(0, 4) + "-" + code.substring(4, code.length());//修改后的银行卡号
                }
                userWages.setBankCardCode(code);//银行卡号
                userMap.put(uid, userWages);
            }
            userWages.setCosmeticsAmount(os[7].toString());//加盟提成工资
        }

        //查询大满贯
        Query queryGrandSlam = em.createQuery("SELECT w.user.name,w.user.balance,w.user.deposit,w.user.balanceMf,w.user.depositMf,w.user.bankType,w.user.bankCardCode,SUM(w.amount)-SUM(w.fee),w.user.id FROM WageLog w WHERE w.productGrandSlam IS NOT NULL AND w.goodsOrder IS NULL AND w.deleted = FALSE AND w.payDate > :startDate AND w.payDate < :endDate GROUP BY w.user.id");
        queryGrandSlam.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : queryGrandSlam.getResultList()) {
            Object[] os = (Object[]) o;
            Long uid = (Long) os[8];
            UserWages userWages = null;
            if (userMap.containsKey(uid)) {
                userWages = userMap.get(uid);
            } else {
                userWages = new UserWages();
                userWages.setName(os[0].toString());//用户姓名
                userWages.setBalance(os[1].toString());//用户余额
                userWages.setDeposit(os[2].toString());//用户押金
                userWages.setBankType(os[5] == null ? "" : os[5].toString());//银行类型
                String code = os[6] == null ? "" : os[6].toString();
                if (code.length() > 10) {
                    code = code.substring(0, 4) + "-" + code.substring(4, code.length());//修改后的银行卡号
                }
                userWages.setBankCardCode(code);//银行卡号
                userMap.put(uid, userWages);
            }
            userWages.setGrandSlamAmount(os[7].toString());//加盟提成工资
        }
        for (Long uid : userMap.keySet()) {
            UserWages userWages = userMap.get(uid);
            userWages.setUserOrderNames(this.getUserOrderNames(uid));
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.recommendIds = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", uid.toString()).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", CategoryEnum.MAKE_FRIENDS);
            userWages.setBack(queryBack.getSingleResult() > 0 ? "含有" : "");
        }
        //去掉list3中的数组的3和4
        List returnList = new ArrayList();
        for (Long uid : userMap.keySet()) {
            UserWages userWages = userMap.get(uid);
            String[] newStr = new String[13];
            newStr[0] = userWages.getName();//用户
            newStr[1] = userWages.getBalance();//便民余额
            newStr[2] = userWages.getDeposit();//便民押金
            newStr[3] = userWages.getBankType();//银行类型
            newStr[4] = userWages.getBankCardCode();//银行卡号
            newStr[5] = userWages.getAdAmount();//广告工资
            newStr[6] = userWages.getRecommendAmount();//加盟提成工资
            newStr[7] = userWages.getPorducetAmount();//产品工资
            newStr[8] = userWages.getCosmeticsAmount();//化妆品工资
            newStr[9] = userWages.getGrandSlamAmount();//大满贯工资
            newStr[10] = userWages.getTotalAmount();//总工资
            newStr[11] = userWages.getUserOrderNames();//代理的平台
            newStr[12] = userWages.getBack();//含有回收
            returnList.add(newStr);
        }
        return returnList;
    }

    /**
     * 大区工资总计
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String[]> findUserWageLogTotalList(Date startDate, Date endDate, CategoryEnum category) {
        List<String[]> list = new ArrayList<>();
        Query query = em.createQuery("SELECT a.user.name,SUM(a.amount),a.user.id,COUNT(a.goodsOrder) FROM UserWageLog a WHERE a.category = :category AND a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", category);
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[4];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[3].toString();
            TypedQuery<Long> queryBack = em.createQuery("SELECT COUNT(go) FROM GoodsOrder go WHERE go.category = :category AND go.deleted = FALSE AND go.status = :status AND go.agentUser.id = :user AND go.lastPayDate > :startDate AND go.lastPayDate < :endDate", Long.class);
            queryBack.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("user", os[2]).setParameter("status", OrderStatusEnum.TERMINATION).setParameter("category", category);
            str[3] = queryBack.getSingleResult() > 0 ? "含有" : "";
            list.add(str);
        }
        return list;
    }

    /**
     * 大区工资明细
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String[]> findUserWageLogList(Date startDate, Date endDate, CategoryEnum category) {
        List<String[]> list = new ArrayList<>();
        TypedQuery<UserWageLog> query = em.createQuery("SELECT a FROM UserWageLog a WHERE a.deleted = FALSE AND a.payDate > :startDate AND a.payDate < :endDate AND a.category = :category", UserWageLog.class);
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("category", category);
        for (UserWageLog userWageLog : query.getResultList()) {
            if (userWageLog.getGoodsOrder().getDeleted()) {
                continue;
            }
            String[] str = new String[11];
            str[0] = Tools.formatDate(userWageLog.getPayDate(), "yyyy-MM-dd");
            str[1] = userWageLog.getUser().getName();
            str[2] = userWageLog.getGoodsOrder().getGoods().getCategoryMean();
            str[3] = userWageLog.getGoodsOrder().getGoods().getName();
            str[4] = userWageLog.getGoodsOrder().getSerialId();
            str[5] = userWageLog.getAmount().toString();
            str[6] = userWageLog.getGoodsOrder().getStatus().equals(OrderStatusEnum.TERMINATION) ? "被回收" : "";
            str[7] = this.getGoodsOrderRecommendNames(userWageLog.getGoodsOrder());
            str[8] = this.getGoodsOrderRecommendOrderNames(userWageLog.getGoodsOrder());
            str[9] = userWageLog.getGoodsOrder().getGatewayType().getMean();
            str[10] = userWageLog.getGoodsOrder().getPaidPrice().toString();
            list.add(str);
        }
        return list;
    }

    /**
     * 代理产品提成
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String[]> findWageLogProductList(Date startDate, Date endDate) {
        List<String[]> list = new ArrayList<>();
        Query query = em.createQuery("SELECT a.user.name,a.productLog.goods.name,a.user.bankType,a.user.bankCardCode,SUM(a.amount),COUNT(a.productLog),SUM(a.productLog.soldCount),a.user.id FROM WageLog a WHERE a.type = :type AND a.cosmetics IS NULL AND a.deleted =  FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("type", WageLogTypeEnum.PRODUCT);
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[8];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2] == null ? "" : os[2].toString();
            String bankCardCode = os[3] == null ? "" : os[3].toString();
            if (bankCardCode.length() > 10) {
                str[3] = bankCardCode + ";";
            } else {
                str[3] = os[3] == null ? "" : os[3].toString();
            }
            str[4] = os[4].toString();
            str[5] = os[5].toString();
            str[6] = os[6].toString();
            long uid = (long) os[7];
            str[7] = this.getUserOrderNames(uid);
            list.add(str);
        }
        return list;
    }

    /**
     * 代理化妆品提成
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String[]> findWageLogCosmeticsList(Date startDate, Date endDate) {
        List<String[]> list = new ArrayList<>();
        Query query = em.createQuery("SELECT a.user.name,a.cosmetics.goods.name,a.user.bankType,a.user.bankCardCode,SUM(a.amount),COUNT(a.cosmetics),SUM(a.cosmetics.soldCount),a.user.id FROM WageLog a WHERE a.type = :type AND a.cosmetics IS NOT NULL AND a.deleted =  FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.user");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0)).setParameter("type", WageLogTypeEnum.PRODUCT);
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[8];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2] == null ? "" : os[2].toString();
            String bankCardCode = os[3] == null ? "" : os[3].toString();
            if (bankCardCode.length() > 10) {
                str[3] = bankCardCode + ";";
            } else {
                str[3] = os[3] == null ? "" : os[3].toString();
            }
            str[4] = os[4].toString();
            str[5] = os[5].toString();
            str[6] = os[6].toString();
            long uid = (long) os[7];
            str[7] = this.getUserOrderNames(uid);
            list.add(str);
        }
        return list;
    }

    /**
     * 获取化妆品的代理工资
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String[]> findUserWageCosmeticsList(Date startDate, Date endDate) {
        List<String[]> list = new ArrayList<>();
        Query query = em.createQuery("SELECT a.regionalManager.name,SUM(a.regionalManagerAmount),COUNT(a.id),SUM(a.soldCount) FROM Cosmetics a WHERE a.regionalManager IS NOT NULL AND a.deleted =  FALSE AND a.payDate > :startDate AND a.payDate < :endDate GROUP BY a.regionalManager");
        query.setParameter("startDate", Tools.addDay(startDate, -1)).setParameter("endDate", Tools.addDay(endDate, 0));
        for (Object o : query.getResultList()) {
            Object[] os = (Object[]) o;
            String[] str = new String[4];
            str[0] = os[0].toString();
            str[1] = os[1].toString();
            str[2] = os[2] == null ? "" : os[2].toString();
            str[3] = os[3] == null ? "" : os[3].toString();
            list.add(str);
        }
        return list;
    }

    /**
     * 获取商品
     *
     * @param serialId
     * @return
     */
    public Goods findGoodsBySerialId(String serialId) {
        Goods goods = null;
        try {
            TypedQuery<Goods> query = em.createQuery("SELECT g FROM Goods g WHERE g.serialId = :serialId", Goods.class);
            query.setParameter("serialId", serialId);
            goods = query.getSingleResult();
        } catch (NoResultException ex) {
            goods = null;
        }
        return goods;
    }

    /**
     * 获取订单
     *
     * @param serialId
     * @return
     */
    public GoodsOrder findOrderBySerialId(String serialId) {
        GoodsOrder goodsOrder = null;
        try {
            TypedQuery<GoodsOrder> query = em.createQuery("SELECT g FROM GoodsOrder g WHERE g.serialId = :serialId", GoodsOrder.class);
            query.setParameter("serialId", serialId);
            goodsOrder = query.getSingleResult();
        } catch (NoResultException ex) {
            goodsOrder = null;
        }
        return goodsOrder;
    }

    /**
     * 生成唯一的号码
     *
     * @return
     */
    public String getUniqueGoodsSerialId() {
        int maxCount = 10;
        String serialId = Tools.generateRandom8Chars();
        int i = 1;
        for (; i < maxCount && isExistGoods(serialId); i++) {
            serialId = Tools.generateRandom8Chars();
        }
        if (i >= 10) {
            throw new RuntimeException("System Error");
        }
        return serialId;
    }

    /**
     * 生成唯一的订单号
     *
     * @return
     */
    public String getUniqueOrderSerialId() {
        int maxCount = 10;
        String serialId = Tools.generateRandom8Chars();
        int i = 1;
        for (; i < maxCount && isExistOrder(serialId); i++) {
            serialId = Tools.generateRandom8Chars();
        }
        if (i >= 10) {
            throw new RuntimeException("System Error");
        }
        return serialId;
    }

    /**
     * 生成广告的交易记录
     *
     * @param goodsOrder
     * @param user
     * @param amount
     * @param fee
     * @param type
     * @param payDate
     * @return
     */
    public WageLog createWageLog(GoodsOrder goodsOrder, SysUser user, BigDecimal amount, BigDecimal fee, WageLogTypeEnum type, Date payDate, CategoryEnum category) {
        WageLog wageLog = new WageLog();
        wageLog.setCategory(category);
        wageLog.setUser(user);
        wageLog.setAmount(amount);
        wageLog.setType(type);
        wageLog.setFee(fee);
        wageLog.setPayDate(payDate);
        wageLog.setGoodsOrder(goodsOrder);
        em.persist(wageLog);
        return wageLog;
    }

    /**
     * 根据产品创建/更新用户收益
     *
     * @param id
     * @param productLog
     * @return
     */
    public WageLog createOrUpdateWageLog(Long id, ProductLog productLog) {
        WageLog wageLog = new WageLog();
        if (id != null) {
            wageLog = em.find(WageLog.class, id);
        }
        wageLog.setCategory(productLog.getGoodsOrder().getCategory());
        wageLog.setUser(productLog.getUser());
        wageLog.setAmount(productLog.getCommissionAmount());
        wageLog.setType(WageLogTypeEnum.PRODUCT);
        wageLog.setFee(BigDecimal.ZERO);
        wageLog.setPayDate(productLog.getPayDate());
        wageLog.setProductLog(productLog);
        if (id == null) {
            em.persist(wageLog);
        } else {
            em.merge(wageLog);
        }
        return wageLog;
    }

    /**
     * 根据大满贯创建/更新用户收益
     *
     * @param id
     * @param productGrandSlam
     * @return
     */
    public WageLog createOrUpdateWageLog(Long id, ProductGrandSlam productGrandSlam) {
        WageLog wageLog = new WageLog();
        if (id != null) {
            wageLog = em.find(WageLog.class, id);
        }
        wageLog.setCategory(productGrandSlam.getGoodsOrder().getCategory());
        wageLog.setUser(productGrandSlam.getUser());
        wageLog.setAmount(productGrandSlam.getAmount());
        wageLog.setType(WageLogTypeEnum.GRAND_SLAM);
        wageLog.setFee(BigDecimal.ZERO);
        wageLog.setPayDate(productGrandSlam.getPayDate());
        wageLog.setProductGrandSlam(productGrandSlam);
        if (id == null) {
            em.persist(wageLog);
        } else {
            em.merge(wageLog);
        }
        return wageLog;
    }

    /**
     * 根据化妆品生成用户收益
     *
     * @param id
     * @param cosmetics
     * @return
     */
    public WageLog createOrUpdateWageLog(Long id, Cosmetics cosmetics) {
        WageLog wageLog = new WageLog();
        if (id != null) {
            wageLog = em.find(WageLog.class, id);
        }
        wageLog.setCategory(cosmetics.getGoodsOrder().getCategory());
        wageLog.setUser(cosmetics.getUser());
        wageLog.setAmount(cosmetics.getCommissionAmount());
        wageLog.setType(WageLogTypeEnum.PRODUCT);
        wageLog.setFee(BigDecimal.ZERO);
        wageLog.setPayDate(cosmetics.getPayDate());
        wageLog.setCosmetics(cosmetics);
        if (id == null) {
            em.persist(wageLog);
        } else {
            em.merge(wageLog);
        }
        return wageLog;
    }

    /**
     * 重新计算用户余额
     *
     * @param agentUser
     * @param goodsOrder
     * @throws EjbMessageException
     */
    public void resetUserBalance(SysUser agentUser, GoodsOrder goodsOrder) throws EjbMessageException {
        if (goodsOrder.getCategory().equals(CategoryEnum.SERVICE_PEOPLE)) {
            agentUser.setDeposit(agentUser.getDeposit().subtract((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))));
            agentUser.setBalance(agentUser.getBalance().subtract((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))).add(goodsOrder.getBackAmount()).compareTo(BigDecimal.ZERO) > 0 ? agentUser.getBalance().subtract(goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee())).add(goodsOrder.getBackAmount()) : BigDecimal.ZERO);
        } else if (goodsOrder.getCategory().equals(CategoryEnum.MAKE_FRIENDS)) {
            agentUser.setDeposit(agentUser.getDeposit().subtract((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))));
            agentUser.setBalance(agentUser.getBalance().subtract((goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee()))).add(goodsOrder.getBackAmount()).compareTo(BigDecimal.ZERO) > 0 ? agentUser.getBalance().subtract(goodsOrder.getPaidPrice().subtract(goodsOrder.getPeopleCountFee())).add(goodsOrder.getBackAmount()) : BigDecimal.ZERO);
        } else {
            throw new EjbMessageException("数据异常");
        }
    }

    /**
     * 根据订单删除交易日志
     *
     * @param goodsOrder
     */
    public void deleteWageLogByOrder(GoodsOrder goodsOrder) {
        TypedQuery<WageLog> query = em.createQuery("UPDATE WageLog a SET a.deleted = TRUE WHERE a.goodsOrder = :goodsOrder", WageLog.class);
        query.setParameter("goodsOrder", goodsOrder);
        query.executeUpdate();
    }

    /**
     * 根据产品删除交易日志
     *
     * @param productLog
     */
    public void deleteWageLogByProduct(ProductLog productLog) {
        TypedQuery<WageLog> query = em.createQuery("UPDATE WageLog a SET a.deleted = TRUE WHERE a.productLog = :productLog", WageLog.class);
        query.setParameter("productLog", productLog);
        query.executeUpdate();
    }

    /**
     * 创建更新
     *
     * @param id
     * @param goods
     * @param amount
     * @param payDate
     * @param limitType
     * @param name
     * @param ownerWeChat
     * @param gatewayType
     * @param createUser
     * @param userBalanceAmount
     * @param userAmount
     * @param adLevel
     * @param remark
     * @param sendType
     * @return
     * @throws EjbMessageException
     */
    public NewAd createOrUpdateNewAd(Long id, Goods goods, BigDecimal amount, Date payDate, AdLimitTypeEnum limitType,
            String name, String ownerWeChat, PaymentGatewayTypeEnum gatewayType, SysUser createUser, BigDecimal userBalanceAmount, BigDecimal userAmount, AdLevelEnum adLevel, String remark, WageLogTypeEnum sendType, CategoryEnum category, String categoryPlus) throws EjbMessageException {
        NewAd ad = new NewAd();
        if (id != null) {
            ad = em.find(NewAd.class, id);
            //重新计算用户余额
            SysUser user = ad.getUser();
            if (category.equals(CategoryEnum.SERVICE_PEOPLE)) {
                user.setBalance(user.getBalance().add(ad.getUserBalanceAmount()));
            } else if (category.equals(CategoryEnum.MAKE_FRIENDS)) {
                user.setBalance(user.getBalance().add(ad.getUserBalanceAmount()));
            } else {
                throw new EjbMessageException("数据异常");
            }
            SysUser newUser = this.findUserByOrder(goods);
            if (newUser == null) {
                throw new EjbMessageException("数据异常，联系管理员");
            }
            if (category.equals(CategoryEnum.SERVICE_PEOPLE) && newUser.getBalance().subtract(userBalanceAmount).compareTo(BigDecimal.ZERO) < 0) {
                throw new EjbMessageException("账户余额不足，用户剩下余额为:" + user.getBalance());
            }
            if (category.equals(CategoryEnum.MAKE_FRIENDS) && newUser.getBalance().subtract(userBalanceAmount).compareTo(BigDecimal.ZERO) < 0) {
                throw new EjbMessageException("账户余额不足，用户剩下余额为:" + user.getBalance());
            }
            if (category.equals(CategoryEnum.SERVICE_PEOPLE)) {
                newUser.setBalance(newUser.getBalance().subtract(userBalanceAmount));
            } else if (category.equals(CategoryEnum.MAKE_FRIENDS)) {
                newUser.setBalance(newUser.getBalance().subtract(userBalanceAmount));
            } else {
                throw new EjbMessageException("数据异常");
            }
            em.merge(user);
            em.merge(newUser);
            ad.setUser(newUser);
        } else {
            SysUser user = this.findUserByOrder(goods);
            ad.setUser(user);
            if (user == null) {
                throw new EjbMessageException("数据异常，联系管理员");
            }
            if (userBalanceAmount.compareTo(BigDecimal.ZERO) > 0) {
                //减去账户余额
                if (category.equals(CategoryEnum.SERVICE_PEOPLE) && user.getBalance().subtract(userBalanceAmount).compareTo(BigDecimal.ZERO) < 0) {
                    throw new EjbMessageException("账户余额不足，用户剩下余额为:" + user.getBalance());
                }
                if (category.equals(CategoryEnum.MAKE_FRIENDS) && user.getBalance().subtract(userBalanceAmount).compareTo(BigDecimal.ZERO) < 0) {
                    throw new EjbMessageException("账户余额不足，用户剩下余额为:" + user.getBalance());
                }
                if (category.equals(CategoryEnum.SERVICE_PEOPLE)) {
                    user.setBalance(user.getBalance().subtract(userBalanceAmount).compareTo(BigDecimal.ZERO) > 0 ? user.getBalance().subtract(userBalanceAmount) : BigDecimal.ZERO);
                } else if (category.equals(CategoryEnum.MAKE_FRIENDS)) {
                    user.setBalance(user.getBalance().subtract(userBalanceAmount).compareTo(BigDecimal.ZERO) > 0 ? user.getBalance().subtract(userBalanceAmount) : BigDecimal.ZERO);
                } else {
                    throw new EjbMessageException("数据异常");
                }
                em.merge(user);
            }
        }
        ad.setCategory(category);
        ad.setRemark(remark);
        ad.setAdLevel(adLevel);
        ad.setAmount(amount);
        ad.setGatewayType(gatewayType);
        ad.setGoods(goods);
        ad.setLimitType(limitType);
        ad.setPayDate(payDate);
        ad.setCategoryPlus(categoryPlus);
        if (AdLimitTypeEnum.MONTH_1.equals(ad.getLimitType())) {
            ad.setEndDate(Tools.addMonth(ad.getPayDate(), 1));
        } else if (AdLimitTypeEnum.MONTH_2.equals(ad.getLimitType())) {
            ad.setEndDate(Tools.addMonth(ad.getPayDate(), 2));
        } else if (AdLimitTypeEnum.MONTH_3.equals(ad.getLimitType())) {
            ad.setEndDate(Tools.addMonth(ad.getPayDate(), 3));
        } else if (AdLimitTypeEnum.DAY_15.equals(ad.getLimitType())) {
            ad.setEndDate(Tools.addDay(ad.getPayDate(), 15));
        } else if (AdLimitTypeEnum.DAY_10.equals(ad.getLimitType())) {
            ad.setEndDate(Tools.addDay(ad.getPayDate(), 10));
        }
        ad.setOwnerWeChat(ownerWeChat);
        ad.setName(name);
        ad.setSendType(sendType);
        ad.setUserAmount(userAmount);
        ad.setUserBalanceAmount(userBalanceAmount);
        if (id == null) {
            ad.setCreateUser(createUser);
            em.persist(ad);
        } else {
            em.merge(ad);
        }
        return ad;
    }

    /**
     * 删除广告
     *
     * @param ids
     */
    public void deleteNewAd(List<Long> ids) {
        TypedQuery<NewAd> query = em.createQuery("UPDATE NewAd a SET a.deleted = TRUE WHERE a.id IN :ids", NewAd.class);
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    /**
     * 创建或更新产品日志
     *
     * @param id
     * @param orderId
     * @param incomeAmount
     * @param commissionAmount
     * @param payDate
     * @param product
     * @param remark
     * @param soldCount
     */
    public void createOrUpdateProductLog(Long id, Long orderId, BigDecimal incomeAmount, BigDecimal commissionAmount, Date payDate, ProductEnum product, String remark, int soldCount) {
        ProductLog productLog = new ProductLog();
        if (id != null) {
            productLog = em.find(ProductLog.class, id);
        }
        GoodsOrder order = em.find(GoodsOrder.class, orderId);
        productLog.setGoodsOrder(order);
        productLog.setGoods(order.getGoods());
        productLog.setUser(order.getAgentUser());
        productLog.setIncomeAmount(incomeAmount);
        productLog.setCommissionAmount(commissionAmount);
        productLog.setPayDate(payDate);
        productLog.setProduct(product);
        productLog.setRemark(remark);
        productLog.setSoldCount(soldCount);
        Long wageLogId = null;
        if (id == null) {
            em.persist(productLog);
//            em.flush();
        } else {
            em.merge(productLog);
            wageLogId = this.findWageLogByProduct(productLog).getId();
        }
        this.createOrUpdateWageLog(wageLogId, productLog);
    }

    /**
     * 创建或更新大满贯
     *
     * @param id
     * @param orderId
     * @param amount
     * @param payDate
     * @param remark
     */
    public void createOrUpdateGrandSlam(Long id, Long orderId, BigDecimal amount, Date payDate, String remark) {
        ProductGrandSlam productGrandSlam = new ProductGrandSlam();
        if (id != null) {
            productGrandSlam = em.find(ProductGrandSlam.class, id);
        }
        GoodsOrder order = em.find(GoodsOrder.class, orderId);
        productGrandSlam.setGoodsOrderId(order.getId());
        productGrandSlam.setGoodsOrder(order);
        productGrandSlam.setUser(order.getAgentUser());
        productGrandSlam.setGoods(order.getGoods());
        productGrandSlam.setGoodsId(order.getGoods().getId());
        productGrandSlam.setUserId(order.getAgentUser().getId());
        productGrandSlam.setAmount(amount);
        productGrandSlam.setPayDate(payDate);
        productGrandSlam.setRemark(remark);
        Long wageLogId = null;
        if (id == null) {
            em.persist(productGrandSlam);
//            em.flush();
        } else {
            em.merge(productGrandSlam);
            wageLogId = this.findWageLogByProductGrandSlam(productGrandSlam).getId();
        }
        this.createOrUpdateWageLog(wageLogId, productGrandSlam);
    }

    /**
     * 创建或更新化妆品
     *
     * @param id
     * @param orderId
     * @param incomeAmount
     * @param commissionAmount
     * @param payDate
     * @param product
     * @param remark
     * @param soldCount
     * @param regionalManager
     * @param regionalManagerAmount
     */
    public void createOrUpdateCosmetics(Long id, Long orderId, BigDecimal incomeAmount, BigDecimal commissionAmount, Date payDate,
            int product, String remark, int soldCount, Long regionalManager, BigDecimal regionalManagerAmount) {
        Cosmetics cosmetics = new Cosmetics();
        if (id != null) {
            cosmetics = em.find(Cosmetics.class, id);
        }
        GoodsOrder order = em.find(GoodsOrder.class, orderId);
        cosmetics.setGoodsOrder(order);
        cosmetics.setGoods(order.getGoods());
        cosmetics.setUser(order.getAgentUser());
        cosmetics.setIncomeAmount(incomeAmount);
        cosmetics.setCommissionAmount(commissionAmount);
        cosmetics.setPayDate(payDate);
        cosmetics.setProduct(product);
        cosmetics.setRemark(remark);
        cosmetics.setSoldCount(soldCount);
        cosmetics.setRegionalManager(em.find(SysUser.class, regionalManager));
        cosmetics.setRegionalManagerAmount(regionalManagerAmount);
        Long wageLogId = null;
        if (id == null) {
            em.persist(cosmetics);
//            em.flush();
        } else {
            em.merge(cosmetics);
            wageLogId = this.findWageLogByCosmetics(cosmetics).getId();
        }
        this.createOrUpdateWageLog(wageLogId, cosmetics);
    }

    /**
     * 获取产品信息日志
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<ProductLog> findProductLogList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<ProductLog> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ProductLog> query = builder.createQuery(ProductLog.class);
        Root root = query.from(ProductLog.class);
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(builder.equal(root.get("deleted"), false));
        if (map.containsKey("startDate")) {
            criteria.add(builder.greaterThanOrEqualTo(root.get("payDate"), (Date) map.get("startDate")));
        }
        if (map.containsKey("endDate")) {
            criteria.add(builder.lessThan(root.get("payDate"), (Date) map.get("endDate")));
        }
        if (map.containsKey("search")) {
            criteria.add(builder.or(builder.like(root.get("goods").get("name"), "%" + (String) map.get("search") + "%"), builder.like(root.get("goods").get("namePinyin"), "%" + (String) map.get("search") + "%")));
        }
        if (map.containsKey("category")) {
            criteria.add(builder.equal(root.get("goodsOrder").get("category"), map.get("category")));
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    countQuery.where(criteria.get(0));
                } else {
                    countQuery.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                Long totalCount = em.createQuery(countQuery).getSingleResult();
                resultList.setTotalCount(totalCount.intValue());
            }
            if (list == null || list) {
                query = query.select(root);
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("payDate")));
                TypedQuery<ProductLog> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<ProductLog> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 获取大满贯
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<ProductGrandSlam> findProductGrandSlamList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<ProductGrandSlam> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ProductGrandSlam> query = builder.createQuery(ProductGrandSlam.class);
        Root root = query.from(ProductGrandSlam.class);
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(builder.equal(root.get("deleted"), false));
        if (map.containsKey("startDate")) {
            criteria.add(builder.greaterThanOrEqualTo(root.get("payDate"), (Date) map.get("startDate")));
        }
        if (map.containsKey("endDate")) {
            criteria.add(builder.lessThan(root.get("payDate"), (Date) map.get("endDate")));
        }
        if (map.containsKey("search")) {
            criteria.add(builder.or(builder.like(root.get("goods").get("name"), "%" + (String) map.get("search") + "%"), builder.like(root.get("goods").get("namePinyin"), "%" + (String) map.get("search") + "%")));
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    countQuery.where(criteria.get(0));
                } else {
                    countQuery.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                Long totalCount = em.createQuery(countQuery).getSingleResult();
                resultList.setTotalCount(totalCount.intValue());
            }
            if (list == null || list) {
                query = query.select(root);
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("payDate")));
                TypedQuery<ProductGrandSlam> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<ProductGrandSlam> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 获取化妆品列表
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<Cosmetics> findCosmeticsList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<Cosmetics> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Cosmetics> query = builder.createQuery(Cosmetics.class);
        Root root = query.from(Cosmetics.class);
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(builder.equal(root.get("deleted"), false));
        if (map.containsKey("startDate")) {
            criteria.add(builder.greaterThanOrEqualTo(root.get("payDate"), (Date) map.get("startDate")));
        }
        if (map.containsKey("endDate")) {
            criteria.add(builder.lessThan(root.get("payDate"), (Date) map.get("endDate")));
        }
        if (map.containsKey("search")) {
            criteria.add(builder.or(builder.like(root.get("goods").get("name"), "%" + (String) map.get("search") + "%"), builder.like(root.get("goods").get("namePinyin"), "%" + (String) map.get("search") + "%")));
        }
        if (map.containsKey("category")) {
            criteria.add(builder.equal(root.get("goodsOrder").get("category"), map.get("category")));
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    countQuery.where(criteria.get(0));
                } else {
                    countQuery.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                Long totalCount = em.createQuery(countQuery).getSingleResult();
                resultList.setTotalCount(totalCount.intValue());
            }
            if (list == null || list) {
                query = query.select(root);
                if (criteria.isEmpty()) {
                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("payDate")));
                TypedQuery<Cosmetics> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<Cosmetics> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 根据产品获取工资
     *
     * @param productLog
     * @return
     */
    public WageLog findWageLogByProduct(ProductLog productLog) {
        WageLog log = null;
        TypedQuery<WageLog> query = em.createQuery("SELECT w FROM WageLog w WHERE w.productLog = :productLog", WageLog.class);
        query.setParameter("productLog", productLog);
        try {
            log = query.getSingleResult();
        } catch (NoResultException e) {
            log = null;
        }
        return log;
    }

    /**
     * 根据大滿貫获取工资
     *
     * @param productLog
     * @return
     */
    public WageLog findWageLogByProductGrandSlam(ProductGrandSlam productGrandSlam) {
        WageLog log = null;
        TypedQuery<WageLog> query = em.createQuery("SELECT w FROM WageLog w WHERE w.productGrandSlam = :productGrandSlam", WageLog.class);
        query.setParameter("productGrandSlam", productGrandSlam);
        try {
            log = query.getSingleResult();
        } catch (NoResultException e) {
            log = null;
        }
        return log;
    }

    /**
     * 根据化妆品获取工资
     *
     * @param cosmetics
     * @return
     */
    public WageLog findWageLogByCosmetics(Cosmetics cosmetics) {
        WageLog log = null;
        TypedQuery<WageLog> query = em.createQuery("SELECT w FROM WageLog w WHERE w.cosmetics = :cosmetics", WageLog.class);
        query.setParameter("cosmetics", cosmetics);
        try {
            log = query.getSingleResult();
        } catch (NoResultException e) {
            log = null;
        }
        return log;
    }

    // **********************************************************************
    // ************* PUBLIC METHODS *****************************************
    // **********************************************************************
    private String generateLogingCode(Long id) {
        return Tools.md5(id + "_" + System.currentTimeMillis() + "_" + Tools.generateRandom8Chars());
    }

    /**
     * 订单号是否存在
     *
     * @param serialId
     * @return
     */
    private boolean isExistGoods(String serialId) {
        Goods goods = findGoodsBySerialId(serialId);
        if (goods == null) {
            return false;
        }
        return true;
    }

    private boolean isExistOrder(String serialId) {
        GoodsOrder goodsOrder = findOrderBySerialId(serialId);
        if (goodsOrder == null) {
            return false;
        }
        return true;
    }

}
