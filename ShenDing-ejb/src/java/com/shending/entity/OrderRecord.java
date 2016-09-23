package com.shending.entity;

import com.shending.support.enums.OrderRecordTypeEnum;
import com.shending.support.enums.OrderStatusEnum;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 订单档案
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "order_record")
@XmlRootElement
public class OrderRecord implements Serializable {

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
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //订单
    private GoodsOrder order;
    @JoinColumn(name = "goods_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //订单销售的商品
    private Goods goods;
    @Column(name = "deleted")
    private Boolean deleted = false;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 255)
    private OrderRecordTypeEnum type = OrderRecordTypeEnum.EARNEST;
    @Column(name = "pay_date")
    @Temporal(TemporalType.TIMESTAMP)
    //支付时间
    private Date payDate;
    @Column(name = "price")
    //金额
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway_type", nullable = false, length = 255)
    //支付方式
    private PaymentGatewayTypeEnum gatewayType;
    

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

    public GoodsOrder getOrder() {
        return order;
    }

    public void setOrder(GoodsOrder order) {
        this.order = order;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public OrderRecordTypeEnum getType() {
        return type;
    }

    public String getTypeMean() {
        return type.getMean();
    }

    public void setType(OrderRecordTypeEnum type) {
        this.type = type;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public PaymentGatewayTypeEnum getGatewayType() {
        return gatewayType;
    }

    public String getGatewayTypeStr() {
        return gatewayType.getMean();
    }

    public void setGatewayType(PaymentGatewayTypeEnum gatewayType) {
        this.gatewayType = gatewayType;
    }

}
