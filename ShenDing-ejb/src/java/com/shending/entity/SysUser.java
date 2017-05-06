/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.entity;

import com.shending.support.Tools;
import com.shending.support.enums.SysUserStatus;
import com.shending.support.enums.SysUserTypeEnum;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 个人账户
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "user_account")
@XmlRootElement
public class SysUser implements Serializable {

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
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "account")
    //帐号
    private String account;
    @Column(name = "passwd")
    //密码
    private String passwd;
    @Size(max = 255)
    @Column(name = "name", length = 255)
    //姓名
    private String name;
    @Column(name = "deleted")
    private Boolean deleted = false;
    @Column(name = "head_image_url")
    //头像
    private String headImageUrl;
    @Column(name = "login_code")
    //登录号码
    private String loginCode;
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_type", nullable = false, length = 255)
    private SysUserTypeEnum adminType = SysUserTypeEnum.ADMIN;
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SysRole sysRole;
    @Column(name = "sex")
    private Integer sex = 1;
    @Column(name = "birthday")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;
    @Column(name = "id_card")
    private String idCard;
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "wechat_code")
    private String weChatCode;
    @Column(name = "qq")
    private String qq;
    @Column(name = "province")
    private String province;
    @Column(name = "city")
    private String city;
    @Column(name = "area")
    private String area;
    @Column(name = "province_str")
    private String provinceStr;
    @Column(name = "city_str")
    private String cityStr;
    @Column(name = "area_str")
    private String areaStr;
    @Column(name = "address")
    private String address;
    @Column(name = "email")
    private String email;
    @Column(name = "bank_type")
    private String bankType;
    @Column(name = "bank_card_code")
    private String bankCardCode;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 255)
    private SysUserStatus status = SysUserStatus.PEDING;
    @Column(name = "deposit")
    //用户的押金
    private BigDecimal deposit = BigDecimal.ZERO;
    @Column(name = "balance")
    //用户定金的余额
    private BigDecimal balance = BigDecimal.ZERO;
    @Column(name = "deposit_mf")
    //用户的交友押金
    private BigDecimal depositMf = BigDecimal.ZERO;
    @Column(name = "balance_mf")
    //用户交友定金的余额
    private BigDecimal balanceMf = BigDecimal.ZERO;
    @Transient
    private String goodsMsg;
    @Transient
    private String goodsCategory;
    @Transient
    private Long orderId;
    @Column(name = "last_wage_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastWageDate;
    @Transient
    private boolean inLastWageDate = false;

    public Integer getSex() {
        return sex;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getDeposit() {
        if (deposit == null || deposit.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getDepositMf() {
        return depositMf;
    }

    public void setDepositMf(BigDecimal depositMf) {
        this.depositMf = depositMf;
    }

    public BigDecimal getBalanceMf() {
        return balanceMf;
    }

    public void setBalanceMf(BigDecimal balanceMf) {
        this.balanceMf = balanceMf;
    }

    public BigDecimal getBalance() {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getRoleId() {
        if (adminType != SysUserTypeEnum.MANAGE) {
            return 0l;
        }
        if (sysRole == null) {
            return -1l;
        }
        return sysRole.getId();
    }

    public String getRoleString() {
        if (sysRole == null) {
            return "代理";
        }
        return sysRole.getName();
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public SysUserTypeEnum getAdminType() {
        return adminType;
    }

    public void setAdminType(SysUserTypeEnum adminType) {
        this.adminType = adminType;
    }

    public SysRole getSysRole() {
        return sysRole;
    }

    public void setSysRole(SysRole sysRole) {
        this.sysRole = sysRole;
    }

    public String getSexStr() {
        return sex == 1 ? "男" : "女";
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getBirthdayStr() {
        if (this.birthday == null) {
            return null;
        }
        return Tools.formatDate(this.birthday, "yyyy-MM-dd");
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getIdCard() {
        return idCard;
    }

    public String getIdCardOutput() {
        if (idCard != null) {
            idCard = idCard.replaceAll(",", " ").replaceAll("，", " ");
            if (idCard.length() > 10) {
                return idCard + ";";
            }
        } else {
            return "";
        }
        return idCard;
    }

    public String getBankCardCodeOutput() {
        if (bankCardCode != null) {
            bankCardCode = bankCardCode.replaceAll(",", " ").replaceAll("，", " ");
            if (bankCardCode.length() > 10) {
                return bankCardCode + ";";
            }
        } else {
            return "";
        }
        return bankCardCode;
    }

    public String getMobileOutput() {
        if (mobile != null) {
            mobile = mobile.replaceAll(",", " ").replaceAll("，", " ");
        } else {
            return "";
        }
        return mobile;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWeChatCode() {
        return weChatCode;
    }

    public void setWeChatCode(String weChatCode) {
        this.weChatCode = weChatCode;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public SysUserStatus getStatus() {
        return status;
    }

    public void setStatus(SysUserStatus status) {
        this.status = status;
    }

    public String getStatusMean() {
        return status.getMean();
    }

    public String getProvinceStr() {
        return provinceStr;
    }

    public void setProvinceStr(String provinceStr) {
        this.provinceStr = provinceStr;
    }

    public String getCityStr() {
        return cityStr;
    }

    public void setCityStr(String cityStr) {
        this.cityStr = cityStr;
    }

    public String getAreaStr() {
        return areaStr;
    }

    public void setAreaStr(String areaStr) {
        this.areaStr = areaStr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankCardCode() {
        return bankCardCode;
    }

    public void setBankCardCode(String bankCardCode) {
        this.bankCardCode = bankCardCode;
    }

    public String getGoodsMsg() {
        return goodsMsg;
    }

    public void setGoodsMsg(String goodsMsg) {
        this.goodsMsg = goodsMsg;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public Date getLastWageDate() {
        return lastWageDate;
    }

    public void setLastWageDate(Date lastWageDate) {
        this.lastWageDate = lastWageDate;
    }

    public boolean isInLastWageDate() {
        return inLastWageDate;
    }

    public boolean getInLastWageDate() {
        return inLastWageDate;
    }

    public void setInLastWageDate(boolean inLastWageDate) {
        this.inLastWageDate = inLastWageDate;
    }

}
