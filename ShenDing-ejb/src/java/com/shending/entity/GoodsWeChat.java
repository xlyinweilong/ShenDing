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
 * 商品微信档案
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "goods_wechat")
@XmlRootElement
public class GoodsWeChat implements Serializable {

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
    @JoinColumn(name = "goods_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    //商品
    private Goods goods;
    @Column(name = "wechat_code")
    //微信帐号
    private String weChatCode;
    @Column(name = "deleted")
    private Boolean deleted = false;

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

    public String getWeChatCode() {
        return weChatCode;
    }

    public void setWeChatCode(String weChatCode) {
        this.weChatCode = weChatCode;
    }

}
