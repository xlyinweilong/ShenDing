package com.shending.service;

import com.shending.entity.AladingwebApply;
import com.shending.entity.AladingwebSearch;
import com.shending.entity.AladingwebSpread;
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
import com.shending.entity.ProductMinShengBank;
import com.shending.entity.SysMenu;
import com.shending.entity.SysRole;
import com.shending.entity.SysRoleMenu;
import com.shending.entity.SysUser;
import com.shending.entity.UserWageLog;
import com.shending.entity.Vip;
import com.shending.entity.Vote;
import com.shending.support.ImageEdit;
import com.shending.support.ResultList;
import com.shending.support.Tools;
import com.shending.support.Trans2PinYin;
import com.shending.support.bo.PlaceWages;
import com.shending.support.bo.UserWages;
import com.shending.support.enums.AdGoodsTypeEnum;
import com.shending.support.enums.AdLevelEnum;
import com.shending.support.enums.AdLimitTypeEnum;
import com.shending.support.enums.CategoryEnum;
import com.shending.support.enums.GoodsStatusEnum;
import com.shending.support.enums.OrderRecordTypeEnum;
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
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
public class AladingWebService {
    
    @EJB
    private AdminService adminService;
    
    @PersistenceContext(unitName = "ShenDing-PU")
    private EntityManager em;
    private static final Logger logger = Logger.getLogger(AladingWebService.class.getName());
    
    @Asynchronous
    public void createPic() {
        TypedQuery<AladingwebSearch> query = em.createQuery("SELECT a FROM AladingwebSearch a where a.picUrl is null", AladingwebSearch.class);
        for (AladingwebSearch aladingwebSearch : query.getResultList()) {
            ImageEdit.createStringMark("/data/pic/main.jpg", "/data/pic/" + aladingwebSearch.getId() + ".jpg", aladingwebSearch.getName(), aladingwebSearch.getWecatCode(), aladingwebSearch.getContractCode(), Tools.formatDate(aladingwebSearch.getStartDate(), "yyyy-MM-dd"), Tools.formatDate(aladingwebSearch.getEndDate(), "yyyy-MM-dd"));
            aladingwebSearch.setPicUrl("/pic/" + aladingwebSearch.getId());
            em.merge(aladingwebSearch);
        }
    }

    /**
     * 查询
     *
     * @param search
     * @return
     */
    public AladingwebSearch findAladingwebSearch(String search) {
        AladingwebSearch aladingwebSearch = null;
        TypedQuery<AladingwebSearch> query = em.createQuery("SELECT a FROM AladingwebSearch a WHERE a.contractCode = :contractCode or a.name = :name or a.wecatCode = :wecatCode", AladingwebSearch.class);
        query.setParameter("contractCode", search).setParameter("name", search).setParameter("wecatCode", search);
        try {
            aladingwebSearch = query.getSingleResult();
        } catch (Exception e) {
            aladingwebSearch = null;
        }
        return aladingwebSearch;
    }

    /**
     * 创建查询代理
     *
     * @param id
     * @param startDate
     * @param endDate
     * @param name
     * @param wecatCode
     * @param contractCode
     * @return
     */
    public AladingwebSearch createOrUpdateAladingwebSearch(Long id, Date startDate, Date endDate, String name, String wecatCode,
            String contractCode) {
        AladingwebSearch aladingwebSearch = null;
        if (id == null) {
            aladingwebSearch = new AladingwebSearch();
        } else {
            aladingwebSearch = em.find(AladingwebSearch.class, id);
        }
        
        aladingwebSearch.setStartDate(startDate);
        aladingwebSearch.setEndDate(endDate);
        aladingwebSearch.setContractCode(contractCode);
        aladingwebSearch.setWecatCode(wecatCode);
        aladingwebSearch.setName(name);
        if (id == null) {
            //生产图片
            String picUrl = null;
            aladingwebSearch.setPicUrl(picUrl);
            em.persist(aladingwebSearch);
        } else {
            em.merge(aladingwebSearch);
        }
        return aladingwebSearch;
    }
    
    public AladingwebApply createOrUpdateAladingwebApply(Long id, String mobile, String platform, String product, String name, String wecatCode) {
        AladingwebApply aladingwebApply = null;
        if (id == null) {
            aladingwebApply = new AladingwebApply();
        } else {
            aladingwebApply = em.find(AladingwebApply.class, id);
        }
        aladingwebApply.setMobile(mobile);
        aladingwebApply.setPlatform(platform);
        aladingwebApply.setWecatCode(wecatCode);
        aladingwebApply.setName(name);
        aladingwebApply.setProduct(product);
        if (id == null) {
            em.persist(aladingwebApply);
        } else {
            em.merge(aladingwebApply);
        }
        return aladingwebApply;
    }
    
    public AladingwebSpread createOrUpdateAladingwebSpread(Long id, String mobile, String platform, String product, String name, String wecatCode) {
        AladingwebSpread aladingwebSpread = null;
        if (id == null) {
            aladingwebSpread = new AladingwebSpread();
        } else {
            aladingwebSpread = em.find(AladingwebSpread.class, id);
        }
        aladingwebSpread.setWecatCode(wecatCode);
        aladingwebSpread.setProduct(product);
        aladingwebSpread.setPlatform(platform);
        aladingwebSpread.setMobile(mobile);
        aladingwebSpread.setName(name);
        if (id == null) {
            em.persist(aladingwebSpread);
        } else {
            em.merge(aladingwebSpread);
        }
        return aladingwebSpread;
    }

    /**
     * 删除查询代理
     *
     * @param ids
     */
    public void deleteAladingwebSearchById(List<Long> ids) {
        for (Long id : ids) {
            if (null == id) {
                continue;
            }
            AladingwebSearch search = em.find(AladingwebSearch.class, id);
            em.remove(search);
        }
    }
    
    public void deleteAladingwebSpreadById(List<Long> ids) {
        for (Long id : ids) {
            if (null == id) {
                continue;
            }
            AladingwebSpread search = em.find(AladingwebSpread.class, id);
            em.remove(search);
        }
    }
    
    public void deleteAladingwebApplyById(List<Long> ids) {
        for (Long id : ids) {
            if (null == id) {
                continue;
            }
            AladingwebApply search = em.find(AladingwebApply.class, id);
            em.remove(search);
        }
    }

    /**
     * 查询代理
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<AladingwebSearch> findAladingwebSearchList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<AladingwebSearch> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<AladingwebSearch> query = builder.createQuery(AladingwebSearch.class);
        Root root = query.from(AladingwebSearch.class);
        List<Predicate> criteria = new ArrayList<>();
        if (map.containsKey("search")) {
            criteria.add(
                    builder.or(builder.equal(root.get("contractCode"), map.get("search").toString()),
                            builder.equal(root.get("wecatCode"), "%" + map.get("search").toString() + "%"),
                            builder.like(root.get("name"), "%" + (String) map.get("search") + "%"))
            );
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
//                    throw new RuntimeException("no criteria");
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
//                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("id")));
                TypedQuery<AladingwebSearch> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<AladingwebSearch> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 查询代理申请
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<AladingwebApply> findAladingwebApplyList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<AladingwebApply> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<AladingwebApply> query = builder.createQuery(AladingwebApply.class);
        Root root = query.from(AladingwebApply.class);
        List<Predicate> criteria = new ArrayList<>();
        if (map.containsKey("search")) {
            criteria.add(
                    builder.or(
                            builder.like(root.get("name"), "%" + (String) map.get("search") + "%"))
            );
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
//                    throw new RuntimeException("no criteria");
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
//                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("id")));
                TypedQuery<AladingwebApply> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<AladingwebApply> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }

    /**
     * 查询全国推广
     *
     * @param map
     * @param pageIndex
     * @param maxPerPage
     * @param list
     * @param page
     * @return
     */
    public ResultList<AladingwebSpread> findAladingwebSpreadList(Map<String, Object> map, int pageIndex, int maxPerPage, Boolean list, Boolean page) {
        ResultList<AladingwebSpread> resultList = new ResultList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<AladingwebSpread> query = builder.createQuery(AladingwebSpread.class);
        Root root = query.from(AladingwebSpread.class);
        List<Predicate> criteria = new ArrayList<>();
        if (map.containsKey("search")) {
            criteria.add(
                    builder.or(
                            builder.like(root.get("name"), "%" + (String) map.get("search") + "%"))
            );
        }
        try {
            if (list == null || !list) {
                CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
                countQuery.select(builder.count(root));
                if (criteria.isEmpty()) {
//                    throw new RuntimeException("no criteria");
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
//                    throw new RuntimeException("no criteria");
                } else if (criteria.size() == 1) {
                    query.where(criteria.get(0));
                } else {
                    query.where(builder.and(criteria.toArray(new Predicate[0])));
                }
                query.orderBy(builder.desc(root.get("id")));
                TypedQuery<AladingwebSpread> typeQuery = em.createQuery(query);
                if (page != null && page) {
                    int startIndex = (pageIndex - 1) * maxPerPage;
                    typeQuery.setFirstResult(startIndex);
                    typeQuery.setMaxResults(maxPerPage);
                    resultList.setPageIndex(pageIndex);
                    resultList.setStartIndex(startIndex);
                    resultList.setMaxPerPage(maxPerPage);
                }
                List<AladingwebSpread> dataList = typeQuery.getResultList();
                resultList.addAll(dataList);
            }
        } catch (NoResultException ex) {
        }
        return resultList;
    }
    
}
