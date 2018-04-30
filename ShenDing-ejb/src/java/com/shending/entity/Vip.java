/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yin
 */
@Entity
@Table(name = "vip")
@XmlRootElement
public class Vip implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date();
    @Basic(optional = false)
    @NotNull
    @Column(name = "pay_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date payDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Version
    private int version;
    @JoinColumn(name = "divide_user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SysUser divideUser;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "goods_id")
    private Long goodsId;

    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SysUser manager;
    @Column(name = "province_str")
    private String provinceStr;
    @Column(name = "province")
    private String province;
    @NotNull
    @Column(name = "amount")
    private BigDecimal amount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "divide_user_amount")
    private BigDecimal divideUserAmount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "welfare_amount")
    private BigDecimal welfareAmount;
    @Size(max = 255)
    @Column(name = "vip_name")
    private String vipName;
    @Column(name = "vip_birthday")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vipBirthday;
    @Size(max = 255)
    @Column(name = "vip_wechat")
    private String vipWechat;
    @Size(max = 255)
    @Column(name = "vip_phone")
    private String vipPhone;
    @Size(max = 255)
    @Column(name = "remark")
    private String remark;
    @Column(name = "deleted")
    private boolean deleted = false;

    @Transient
    private Long managerId;
    @Transient
    private GoodsOrder goodsOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
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

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public SysUser getDivideUser() {
        return divideUser;
    }

    public void setDivideUser(SysUser divideUser) {
        this.divideUser = divideUser;
    }

    public SysUser getManager() {
        return manager;
    }

    public void setManager(SysUser manager) {
        this.manager = manager;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceStr() {
        return provinceStr;
    }

    public void setProvinceStr(String provinceStr) {
        this.provinceStr = provinceStr;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDivideUserAmount() {
        return divideUserAmount;
    }

    public void setDivideUserAmount(BigDecimal divideUserAmount) {
        this.divideUserAmount = divideUserAmount;
    }

    public BigDecimal getWelfareAmount() {
        return welfareAmount;
    }

    public void setWelfareAmount(BigDecimal welfareAmount) {
        this.welfareAmount = welfareAmount;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public Date getVipBirthday() {
        return vipBirthday;
    }

    public void setVipBirthday(Date vipBirthday) {
        this.vipBirthday = vipBirthday;
    }

    public String getVipWechat() {
        return vipWechat;
    }

    public void setVipWechat(String vipWechat) {
        this.vipWechat = vipWechat;
    }

    public String getVipPhone() {
        return vipPhone;
    }

    public void setVipPhone(String vipPhone) {
        this.vipPhone = vipPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
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
        if (!(object instanceof Vip)) {
            return false;
        }
        Vip other = (Vip) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.shending.entity.Vip[ id=" + id + " ]";
    }

}
