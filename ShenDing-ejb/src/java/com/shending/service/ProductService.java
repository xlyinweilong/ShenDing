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
import com.shending.entity.ProductMinShengBank;
import com.shending.entity.SysMenu;
import com.shending.entity.SysRole;
import com.shending.entity.SysRoleMenu;
import com.shending.entity.SysUser;
import com.shending.entity.UserWageLog;
import com.shending.entity.Vip;
import com.shending.entity.Vote;
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
public class ProductService {

    @PersistenceContext(unitName = "ShenDing-PU")
    private EntityManager em;
    private static final Logger logger = Logger.getLogger(ProductService.class.getName());

    public Vip createOrUpdateVip(Long id, Date payDate, Date endDate, SysUser mangerUser, String provinceCode, String provinceStr, Long orderId, Long goodsId,
            SysUser divideUser, BigDecimal amountBd, BigDecimal divideUserAmountBd, BigDecimal welfareAmountBd,
            String vipName, Date vipBirthday, String vipWechat, String vipPhone, String remark) {
        Vip vip = null;
        if (id == null) {
            vip = new Vip();
        } else {
            vip = em.find(Vip.class, id);
        }

        vip.setPayDate(payDate);
        vip.setEndDate(endDate);

        vip.setDivideUser(divideUser);
        vip.setOrderId(orderId);
        vip.setGoodsId(goodsId);
        vip.setManager(mangerUser);

        vip.setAmount(amountBd);
        vip.setDivideUserAmount(divideUserAmountBd);
        vip.setWelfareAmount(welfareAmountBd);

        vip.setProvinceStr(provinceStr);
        vip.setProvince(provinceCode);

        vip.setVipBirthday(vipBirthday);
        vip.setVipName(vipName);
        vip.setVipPhone(vipPhone);
        vip.setVipWechat(vipWechat);
        vip.setRemark(remark);
        Long wageLogId = null;
        if (id == null) {
            em.persist(vip);
        } else {
            em.merge(vip);
            wageLogId = this.findWageLogByVip(vip).getId();
        }
        this.createOrUpdateWageLog(wageLogId, vip);
        return vip;
    }

    /**
     * 根会员创建/更新用户收益
     *
     * @param id
     * @param productMinShengBank
     * @return
     */
    /**
     * 根民生银行创建/更新用户收益
     *
     * @param id
     * @param productMinShengBank
     * @return
     */
    public WageLog createOrUpdateWageLog(Long id, Vip vip) {
        WageLog wageLog = new WageLog();
        if (id != null) {
            wageLog = em.find(WageLog.class, id);
        }
        GoodsOrder order = em.find(GoodsOrder.class, vip.getOrderId());
        wageLog.setCategory(order.getCategory());
        wageLog.setUser(vip.getDivideUser());
        wageLog.setAmount(vip.getDivideUserAmount());
        wageLog.setType(WageLogTypeEnum.VIP);
        wageLog.setFee(BigDecimal.ZERO);
        wageLog.setPayDate(vip.getPayDate());
        wageLog.setVip(vip);
        if (id == null) {
            em.persist(wageLog);
        } else {
            em.merge(wageLog);
        }
        return wageLog;
    }
    
    /**
     * 根据会员获取工资
     *
     * @param vip
     * @return
     */
    public WageLog findWageLogByVip(Vip vip) {
        WageLog log = null;
        TypedQuery<WageLog> query = em.createQuery("SELECT w FROM WageLog w WHERE w.vip = :vip", WageLog.class);
        query.setParameter("vip", vip);
        try {
            log = query.getSingleResult();
        } catch (NoResultException e) {
            log = null;
        }
        return log;
    }

}
