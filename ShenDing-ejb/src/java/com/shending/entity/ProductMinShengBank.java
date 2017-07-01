/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yin
 */
@Entity
@Table(name = "product_min_sheng_bank")
@XmlRootElement
public class ProductMinShengBank implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date();
    @Version
    private int version;
    @Basic(optional = false)
    @NotNull
    @Column(name = "goods_id")
    private long goodsId;
    @ManyToOne
    @JoinColumn(name = "goods_id", insertable = false, updatable = false)
    private Goods goods;
    @Basic(optional = false)
    @NotNull
    @Column(name = "goods_order_id")
    private long goodsOrderId;
    @ManyToOne
    @JoinColumn(name = "goods_order_id", insertable = false, updatable = false)
    private GoodsOrder goodsOrder;
    @Basic(optional = false)
    @NotNull
    @Column(name = "amount")
    private BigDecimal amount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private long userId;
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private SysUser user;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pay_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date payDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "deleted")
    private boolean deleted = false;

    public ProductMinShengBank() {
    }

    public ProductMinShengBank(Long id) {
        this.id = id;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public long getGoodsOrderId() {
        return goodsOrderId;
    }

    public void setGoodsOrderId(long goodsOrderId) {
        this.goodsOrderId = goodsOrderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
        if (!(object instanceof ProductMinShengBank)) {
            return false;
        }
        ProductMinShengBank other = (ProductMinShengBank) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.shending.entity.ProductMinShengBank[ id=" + id + " ]";
    }

}
