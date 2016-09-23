package com.shending.entity;

import com.shending.support.enums.AdLevelEnum;
import com.shending.support.enums.AdLimitTypeEnum;
import com.shending.support.enums.CategoryEnum;
import com.shending.support.enums.PaymentGatewayTypeEnum;
import com.shending.support.enums.WageLogTypeEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 广告
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "new_ad")
@XmlRootElement
public class NewAd implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date();
    @Version
    private Integer version;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //广告分成人
    private SysUser user;
    @JoinColumn(name = "goods_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //平台
    private Goods goods;
    @Column(name = "deleted")
    private Boolean deleted = false;
    @Column(name = "backed")
    private Boolean backed = false;
    @Column(name = "name")
    //名称
    private String name;
    @Column(name = "pay_date")
    @Temporal(TemporalType.TIMESTAMP)
    //收款时间
    private Date payDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    //开始时间
    private Date endDate;
    @Column(name = "owner_wechat")
    //广告主微信
    private String ownerWeChat;
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway_type")
    //支付方式
    private PaymentGatewayTypeEnum gatewayType;
    @Column(name = "amount")
    //入账
    private BigDecimal amount = BigDecimal.ZERO;
    @JoinColumn(name = "create_user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //创建者
    private SysUser createUser;
    @Column(name = "invoicing")
    //是否进行过结账
    private Boolean invoicing = false;
    @Column(name = "user_balance_amount")
    //扣掉的加盟定金是多少
    private BigDecimal userBalanceAmount = BigDecimal.ZERO;
    @Column(name = "user_amount")
    //提成
    private BigDecimal userAmount = BigDecimal.ZERO;
    @Column(name = "ad_level")
    @Enumerated(EnumType.STRING)
    //级别
    private AdLevelEnum adLevel;
    @Column(name = "limit_type")
    @Enumerated(EnumType.STRING)
    //期限
    private AdLimitTypeEnum limitType;
    @Column(name = "remark")
    //备注
    private String remark;
    @Column(name = "achievement")
    //业绩
    private BigDecimal achievement = BigDecimal.ZERO;
    @Column(name = "send_type")
    @Enumerated(EnumType.STRING)
    //期限
    private WageLogTypeEnum sendType;
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 255)
    //平台分类
    private CategoryEnum category = CategoryEnum.SERVICE_PEOPLE;
    @Column(name = "category_plus", length = 255)
    //平台分类附加
    private String categoryPlus = "NORMAL";

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public String getCategoryMean() {
//        if ("AD_DEPARTMENT".equals(categoryPlus)) {
//            return "广告部";
//        }
        return category.getMean();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public Boolean getBacked() {
        return backed;
    }

    public void setBacked(Boolean backed) {
        this.backed = backed;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOwnerWeChat() {
        return ownerWeChat;
    }

    public void setOwnerWeChat(String ownerWeChat) {
        this.ownerWeChat = ownerWeChat;
    }

    public PaymentGatewayTypeEnum getGatewayType() {
        return gatewayType;
    }

    public String getGatewayTypeStr() {
        if (gatewayType == null) {
            return "";
        }
        return gatewayType.getMean();
    }

    public void setGatewayType(PaymentGatewayTypeEnum gatewayType) {
        this.gatewayType = gatewayType;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public SysUser getCreateUser() {
        return createUser;
    }

    public void setCreateUser(SysUser createUser) {
        this.createUser = createUser;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getInvoicing() {
        return invoicing;
    }

    public void setInvoicing(Boolean invoicing) {
        this.invoicing = invoicing;
    }

    public BigDecimal getUserBalanceAmount() {
        return userBalanceAmount;
    }

    public void setUserBalanceAmount(BigDecimal userBalanceAmount) {
        this.userBalanceAmount = userBalanceAmount;
    }

    public BigDecimal getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(BigDecimal userAmount) {
        this.userAmount = userAmount;
    }

    public AdLevelEnum getAdLevel() {
        return adLevel;
    }

    public void setAdLevel(AdLevelEnum adLevel) {
        this.adLevel = adLevel;
    }

    public AdLimitTypeEnum getLimitType() {
        return limitType;
    }

    public void setLimitType(AdLimitTypeEnum limitType) {
        this.limitType = limitType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getAchievement() {
        return achievement;
    }

    public void setAchievement(BigDecimal achievement) {
        this.achievement = achievement;
    }

    public WageLogTypeEnum getSendType() {
        return sendType;
    }

    public String getSendTypeMean() {
        return sendType.getMean();
    }

    public void setSendType(WageLogTypeEnum sendType) {
        this.sendType = sendType;
    }

    public String getCategoryPlus() {
        return categoryPlus;
    }

    public void setCategoryPlus(String categoryPlus) {
        this.categoryPlus = categoryPlus;
    }

}
