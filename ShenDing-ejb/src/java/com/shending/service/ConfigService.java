package com.shending.service;

import com.shending.entity.Config;
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
import com.shending.entity.ProductLog;
import com.shending.entity.SysMenu;
import com.shending.entity.SysRole;
import com.shending.entity.SysRoleMenu;
import com.shending.entity.SysUser;
import com.shending.entity.UserWageLog;
import com.shending.entity.Vote;
import com.shending.support.ResultList;
import com.shending.support.Tools;
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
 * 配置服务层
 *
 * @author yin.weilong
 */
@Stateless
@LocalBean
public class ConfigService {

    @PersistenceContext(unitName = "ShenDing-PU")
    private EntityManager em;
    private static final Logger logger = Logger.getLogger(ConfigService.class.getName());

    /**
     * 根据key，獲取值
     *
     * @param Config
     * @return
     */
    public Config findConfigByKey(String key) {
        Config config = null;
        TypedQuery<Config> query = em.createQuery("SELECT w FROM Config w WHERE w.key = :key", Config.class);
        query.setParameter("key", key);
        try {
            config = query.getSingleResult();
        } catch (NoResultException e) {
            config = null;
        }
        return config;
    }

    /**
     * 保存配置
     *
     * @param key
     * @param value
     */
    public void saveConfig(String key, String value) {
        Config config = this.findConfigByKey(key);
        config.setValue(value);
        em.merge(config);
    }

}
