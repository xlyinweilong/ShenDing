package com.shending.entity;

import com.shending.support.enums.CategoryEnum;
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
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 工资日志
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "wage_log")
@XmlRootElement
public class WageLog implements Serializable {

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
    private int version;
    @Column(name = "deleted")
    private Boolean deleted = false;
    @Column(name = "backed")
    private Boolean backed = false;
    @Column(name = "pay_date")
    @Temporal(TemporalType.TIMESTAMP)
    //收款时间
    private Date payDate;
    @Column(name = "fee")
    //手续费
    private BigDecimal fee = BigDecimal.ZERO;
    @Column(name = "amount")
    //发放的金额
    private BigDecimal amount = BigDecimal.ZERO;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //用户
    private SysUser user;
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //签约订单
    private GoodsOrder goodsOrder;
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //销售的产品
    private ProductLog productLog;
    @JoinColumn(name = "cosmetics_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //销售的化妆品
    private Cosmetics cosmetics;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 255)
    private WageLogTypeEnum type = WageLogTypeEnum.SEND;
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 255)
    //平台分类
    private CategoryEnum category = CategoryEnum.SERVICE_PEOPLE;
    @Transient
    private String goodsNames;

    public ProductLog getProductLog() {
        return productLog;
    }

    public void setProductLog(ProductLog productLog) {
        this.productLog = productLog;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Boolean getBacked() {
        return backed;
    }

    public void setBacked(Boolean backed) {
        this.backed = backed;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public WageLogTypeEnum getType() {
        return type;
    }

    public void setType(WageLogTypeEnum type) {
        this.type = type;
    }

    public GoodsOrder getGoodsOrder() {
        return goodsOrder;
    }

    public void setGoodsOrder(GoodsOrder goodsOrder) {
        this.goodsOrder = goodsOrder;
    }

    public String getGoodsNames() {
        return goodsNames;
    }

    public void setGoodsNames(String goodsNames) {
        this.goodsNames = goodsNames;
    }

    public Cosmetics getCosmetics() {
        return cosmetics;
    }

    public void setCosmetics(Cosmetics cosmetics) {
        this.cosmetics = cosmetics;
    }

}
