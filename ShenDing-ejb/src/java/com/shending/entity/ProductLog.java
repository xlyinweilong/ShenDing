package com.shending.entity;

import com.shending.support.enums.ProductEnum;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 产品部产品售出日志
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "product_log")
@XmlRootElement
public class ProductLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Version
    private int version;
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date();
    @Enumerated(EnumType.STRING)
    @Column(name = "product")
    //产品
    private ProductEnum product = ProductEnum.MA_KA;
    @Column(name = "pay_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date payDate;
    @Column(name = "income_amount")
    //成交的价格
    private BigDecimal incomeAmount = BigDecimal.ZERO;
    @Column(name = "commission_amount")
    //提成
    private BigDecimal commissionAmount = BigDecimal.ZERO;
    @Column(name = "sold_count")
    //交易数量
    private int soldCount = 1;
    @JoinColumn(name = "uid", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //代理
    private SysUser user;
    @JoinColumn(name = "goods_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //商品
    private Goods goods;
    @JoinColumn(name = "goods_order_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //订单
    private GoodsOrder goodsOrder;
    @Size(max = 255)
    @Column(name = "remark")
    //备注
    private String remark;
    @Column(name = "deleted")
    private boolean deleted = false;

    public ProductLog() {
    }

    public ProductLog(Long id) {
        this.id = id;
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

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsOrder getGoodsOrder() {
        return goodsOrder;
    }

    public void setGoodsOrder(GoodsOrder goodsOrder) {
        this.goodsOrder = goodsOrder;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public ProductEnum getProduct() {
        return product;
    }

    public String getProductStr() {
        return product.getMean();
    }

    public void setProduct(ProductEnum product) {
        this.product = product;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductLog)) {
            return false;
        }
        ProductLog other = (ProductLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.shending.entity.ProductLog[ id=" + id + " ]";
    }

}
