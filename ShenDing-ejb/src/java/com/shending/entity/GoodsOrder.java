package com.shending.entity;

import com.shending.support.Tools;
import com.shending.support.enums.CategoryEnum;
import com.shending.support.enums.OrderStatusEnum;
import com.shending.support.enums.PaymentGatewayTypeEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
 * 订单
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "goods_order")
@XmlRootElement
public class GoodsOrder implements Serializable {

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
    //推荐人
    @Column(name = "recommend_ids")
    private String recommendIds;
    //推荐人的订单
    @Column(name = "recommend_order_ids")
    private String recommendOrderIds;
    //推荐人
    @Column(name = "recommend_names")
    private String recommendNames;
    @Column(name = "recommend_rates")
    //分成
    private String recommendRates;
    @JoinColumn(name = "goods_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //订单销售的商品
    private Goods goods;
    @Column(name = "goods_msg")
    //当时的商品信息
    private String goodsMsg;
    @Column(name = "goods_pinyin")
    //当时的商品的拼音
    private String goodsPinyin;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //商品负责人
    private SysUser user;
    @JoinColumn(name = "divide_user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //分成大区
    private SysUser divideUser;
    @Column(name = "user_amount")
    //大区分成的金额
    private BigDecimal userAmount;
    @JoinColumn(name = "agent_user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //商品代理人
    private SysUser agentUser;
    @Column(name = "deleted")
    private Boolean deleted = false;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 255)
    private OrderStatusEnum status = OrderStatusEnum.INIT;
    @Column(name = "invalid_msg")
    //作废理由
    private String invalidMsg;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    //订单完成时间
    private Date endDate;
    @Column(name = "last_pay_date")
    @Temporal(TemporalType.TIMESTAMP)
    //订单最后支付时间
    private Date lastPayDate;
    @Column(name = "serial_id")
    //订单序列号
    private String serialId;
    @Column(name = "price")
    //金额
    private BigDecimal price;
    @Column(name = "back_amount")
    //已经返回金额
    private BigDecimal backAmount = BigDecimal.ZERO;
    @Column(name = "people_count_fee")
    //人数费用
    private BigDecimal peopleCountFee = BigDecimal.ZERO;
    @Column(name = "divide_amount")
    //分成总金额
    private BigDecimal divideAmount = BigDecimal.ZERO;
    @Column(name = "fee")
    //手续费
    private BigDecimal fee = BigDecimal.ZERO;
    @Column(name = "paid_price")
    //已付金额
    private BigDecimal paidPrice = BigDecimal.ZERO;
    @Column(name = "limit_start")
    @Temporal(TemporalType.TIMESTAMP)
    //订单期限
    private Date limitStart;
    @Column(name = "limit_end")
    @Temporal(TemporalType.TIMESTAMP)
    //订单期限
    private Date limitEnd;
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway_type")
    //支付方式
    private PaymentGatewayTypeEnum gatewayType;
    @JoinColumn(name = "create_user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //创建者
    private SysUser createUser;
    @Column(name = "franchise_department_commission")
    //加盟部分成金额
    private BigDecimal franchiseDepartmentCommission = BigDecimal.ZERO;
    @Column(name = "remark")
    //备注
    private String remark;
    @Column(name = "contract_serial_id")
    //合同编号
    private String contractSerialId;
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 255)
    //平台分类
    private CategoryEnum category = CategoryEnum.SERVICE_PEOPLE;
    @Column(name = "old_name")
    //原名字
    private String oldName;
    @Column(name = "last_renew_date")
    @Temporal(TemporalType.TIMESTAMP)
    //最后续签时间
    private Date lastRenewDate;

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public String getCategoryMean() {
        return category.getMean();
    }

    public BigDecimal getPeopleCountFee() {
        return peopleCountFee;
    }

    public void setPeopleCountFee(BigDecimal peopleCountFee) {
        this.peopleCountFee = peopleCountFee;
    }

    public SysUser getDivideUser() {
        return divideUser;
    }

    public void setDivideUser(SysUser divideUser) {
        this.divideUser = divideUser;
    }

    public String getRecommendOrderIds() {
        return recommendOrderIds;
    }

    public void setRecommendOrderIds(String recommendOrderIds) {
        this.recommendOrderIds = recommendOrderIds;
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

    public BigDecimal getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(BigDecimal userAmount) {
        this.userAmount = userAmount;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public String getGoodsMsg() {
        return goodsMsg;
    }

    public void setGoodsMsg(String goodsMsg) {
        this.goodsMsg = goodsMsg;
    }

    public String getGoodsPinyin() {
        return goodsPinyin;
    }

    public void setGoodsPinyin(String goodsPinyin) {
        this.goodsPinyin = goodsPinyin;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public SysUser getAgentUser() {
        return agentUser;
    }

    public void setAgentUser(SysUser agentUser) {
        this.agentUser = agentUser;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public String getStatusMean() {
        return status.getMean();
    }

    public void setStatus(OrderStatusEnum status) {
        this.status = status;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getLastPayDate() {
        return lastPayDate;
    }

    public void setLastPayDate(Date lastPayDate) {
        this.lastPayDate = lastPayDate;
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

    public BigDecimal getFranchiseDepartmentCommission() {
        return franchiseDepartmentCommission;
    }

    public void setFranchiseDepartmentCommission(BigDecimal franchiseDepartmentCommission) {
        this.franchiseDepartmentCommission = franchiseDepartmentCommission;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(BigDecimal paidPrice) {
        this.paidPrice = paidPrice;
    }

    public PaymentGatewayTypeEnum getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(PaymentGatewayTypeEnum gatewayType) {
        this.gatewayType = gatewayType;
    }

    public Date getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(Date limitStart) {
        this.limitStart = limitStart;
    }

    public String getLimitStartStr() {
        if (limitStart == null) {
            return null;
        }
        return Tools.formatDate(limitStart, "yyyy-MM-dd");
    }

    public String getLimitEndStr() {
        if (limitEnd == null) {
            return null;
        }
        return Tools.formatDate(limitEnd, "yyyy-MM-dd");
    }

    public Date getLimitEnd() {
        return limitEnd;
    }

    public void setLimitEnd(Date limitEnd) {
        this.limitEnd = limitEnd;
    }

    public String getInvalidMsg() {
        return invalidMsg;
    }

    public void setInvalidMsg(String invalidMsg) {
        this.invalidMsg = invalidMsg;
    }

    public String getRecommendIds() {
        return recommendIds;
    }

    public void setRecommendIds(String recommendIds) {
        this.recommendIds = recommendIds;
    }

    public List<String> getRecommendIdList() {
        if (recommendIds == null) {
            return null;
        }
        return Arrays.asList(recommendIds.split(";"));
    }

    public String getRecommendNames() {
        return recommendNames;
    }

    public List<String> getRecommendNameList() {
        if (recommendNames == null) {
            return null;
        }
        return Arrays.asList(recommendNames.split(";"));
    }

    public List<String> getRecommendOrderIdsList() {
        if (recommendOrderIds == null) {
            return null;
        }
        return Arrays.asList(recommendOrderIds.split(";"));
    }

    public void setRecommendNames(String recommendNames) {
        this.recommendNames = recommendNames;
    }

    public String getRecommendRates() {
        return recommendRates;
    }

    public List<String> getRecommendRateList() {
        if (recommendRates == null) {
            return null;
        }
        return Arrays.asList(recommendRates.split(";"));
    }

    public void setRecommendRates(String recommendRates) {
        this.recommendRates = recommendRates;
    }

    public BigDecimal getBackAmount() {
        return backAmount;
    }

    public void setBackAmount(BigDecimal backAmount) {
        this.backAmount = backAmount;
    }

    public BigDecimal getDivideAmount() {
        return divideAmount;
    }

    public void setDivideAmount(BigDecimal divideAmount) {
        this.divideAmount = divideAmount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContractSerialId() {
        return contractSerialId;
    }

    public void setContractSerialId(String contractSerialId) {
        this.contractSerialId = contractSerialId;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public Date getLastRenewDate() {
        return lastRenewDate;
    }

    public String getLastRenewDateStr() {
        return lastRenewDate == null ? null : Tools.formatDate(lastRenewDate, "yyyy-MM-dd");
    }

    public void setLastRenewDate(Date lastRenewDate) {
        this.lastRenewDate = lastRenewDate;
    }

}
