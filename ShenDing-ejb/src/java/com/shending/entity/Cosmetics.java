package com.shending.entity;

import com.shending.support.enums.PaymentGatewayTypeEnum;
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
 * 化妆品
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "cosmetics")
@XmlRootElement
public class Cosmetics implements Serializable {

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
    @Column(name = "product")
    //产品
    /**
     * 1.清颜原液 2.清滢柔肤洁面乳 3.舒缓清润精华液 4.馥活提亮精华液 5.多效蚕丝面膜 6.冻干粉修护套 7.冻干粉嫩肤套 8.冻干粉保湿套
     */
    private Integer product = 1;
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
    @JoinColumn(name = "regional_manager", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //提成的大区经理
    private SysUser regionalManager;
    @Column(name = "pay_type")
    @Enumerated(EnumType.STRING)
    //支付方式
    private PaymentGatewayTypeEnum payType = PaymentGatewayTypeEnum.BANK_TRANSFER;
    @Column(name = "regional_manager_amount")
    //大区经理提成金额
    private BigDecimal regionalManagerAmount = BigDecimal.ZERO;

    public Cosmetics() {
    }

    public Cosmetics(Long id) {
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

    public PaymentGatewayTypeEnum getPayType() {
        return payType;
    }

    public void setPayType(PaymentGatewayTypeEnum payType) {
        this.payType = payType;
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

    public Integer getProduct() {
        return product;
    }

    public String getProductStr() {
        switch (product) {
            case 1:
                return "清颜原液";
            case 2:
                return "清滢柔肤洁面乳";
            case 3:
                return "舒缓清润精华液";
            case 4:
                return "馥活提亮精华液";
            case 5:
                return "多效蚕丝面膜";
            case 6:
                return "冻干粉修护套";
            case 7:
                return "冻干粉嫩肤套";
            case 8:
                return "冻干粉保湿套";
        }
        return "";
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    public SysUser getRegionalManager() {
        return regionalManager;
    }

    public void setRegionalManager(SysUser regionalManager) {
        this.regionalManager = regionalManager;
    }

    public BigDecimal getRegionalManagerAmount() {
        return regionalManagerAmount;
    }

    public void setRegionalManagerAmount(BigDecimal regionalManagerAmount) {
        this.regionalManagerAmount = regionalManagerAmount;
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
        if (!(object instanceof Cosmetics)) {
            return false;
        }
        Cosmetics other = (Cosmetics) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.shending.entity.Cosmetics[ id=" + id + " ]";
    }

}
