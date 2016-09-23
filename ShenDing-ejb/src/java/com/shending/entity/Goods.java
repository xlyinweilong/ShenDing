package com.shending.entity;

import com.shending.support.enums.CategoryEnum;
import com.shending.support.enums.GoodsStatusEnum;
import com.shending.support.enums.GoodsTypeEnum;
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
 * 商品
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "goods")
@XmlRootElement
public class Goods implements Serializable {

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
    //商品负责人
    private SysUser user;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 255)
    //平台类型
    private GoodsTypeEnum type = GoodsTypeEnum.GOVERNMENT_DIRECTLY;
    @Column(name = "serial_id")
    //商品序列号
    private String serialId;
    @Column(name = "wechat_code")
    //微信帐号
    private String weChatCode;
    @Column(name = "qq_code")
    //qq号
    private String qqCode;
    @Column(name = "name")
    private String name;
    @Column(name = "name_pinyin")
    private String namePinyin;
    @Column(name = "price")
    //价格
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 255)
    //商品状态
    private GoodsStatusEnum status = GoodsStatusEnum.SALE;
    @Column(name = "status_end_date")
    @Temporal(TemporalType.TIMESTAMP)
    //状态结束时间
    private Date statusEndDate;
    @Column(name = "status_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    //状态开始时间
    private Date statusStartDate;
    @Column(name = "province_str")
    private String provinceStr;
    @Column(name = "province")
    private String province;
    @Column(name = "people_count")
    private Integer peopleCount = 0;
    @Column(name = "deleted")
    private boolean deleted = false;
    @JoinColumn(name = "create_user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //创建者
    private SysUser createUser;
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 255)
    //平台分类
    private CategoryEnum category = CategoryEnum.SERVICE_PEOPLE;

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public String getCategoryMean() {
        return category.getMean();
    }

    public SysUser getCreateUser() {
        return createUser;
    }

    public void setCreateUser(SysUser createUser) {
        this.createUser = createUser;
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

    public GoodsTypeEnum getType() {
        return type;
    }

    public String getTypeString() {
        return type.getMean();
    }

    public void setType(GoodsTypeEnum type) {
        this.type = type;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public GoodsStatusEnum getStatus() {
        return status;
    }

    public String getStatusMean() {
        return status.getMean();
    }

    public void setStatus(GoodsStatusEnum status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getProvinceStr() {
        return provinceStr;
    }

    public void setProvinceStr(String provinceStr) {
        this.provinceStr = provinceStr;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getName() {
        return name;
    }

    public String getCategoryName() {
        return category.getMean().substring(0, 2) + "-" + name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }

    public Date getStatusEndDate() {
        return statusEndDate;
    }

    public void setStatusEndDate(Date statusEndDate) {
        this.statusEndDate = statusEndDate;
    }

    public Date getStatusStartDate() {
        return statusStartDate;
    }

    public String getQqCode() {
        return qqCode;
    }

    public void setQqCode(String qqCode) {
        this.qqCode = qqCode;
    }

    public void setStatusStartDate(Date statusStartDate) {
        this.statusStartDate = statusStartDate;
    }

    public String getWeChatCode() {
        return weChatCode;
    }

    public void setWeChatCode(String weChatCode) {
        this.weChatCode = weChatCode;
    }

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }

}
