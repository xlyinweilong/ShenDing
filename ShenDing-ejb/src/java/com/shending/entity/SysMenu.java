package com.shending.entity;

import com.shending.support.enums.SysMenuPopedomEnum;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 后台菜单
 *
 * @author yin.weilong
 */
@Entity
@Table(name = "sys_menu")
@XmlRootElement
public class SysMenu implements Serializable {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "popedom", nullable = false, length = 255)
    private SysMenuPopedomEnum popedom = SysMenuPopedomEnum.COMMON;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Column(name = "glyphicon")
    private String glyphicon;
    @Basic(optional = false)
    //排序
    @Column(name = "sort_index")
    private Integer sortIndex = 0;
    @Column(name = "sref")
    private String sref;
    //级别
    @Column(name = "lv_index")
    private int lvIndex = 1;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SysMenu parentMenu;
    @Transient
    private List<SysMenu> subSysMenuList = new ArrayList<>();
    @Transient
    private boolean lableId = false;

    public SysMenu() {
    }

    public SysMenu(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getLableId() {
        return lableId;
    }

    public void setLableId(boolean lableId) {
        this.lableId = lableId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public SysMenuPopedomEnum getPopedom() {
        return popedom;
    }

    public void setPopedom(SysMenuPopedomEnum popedom) {
        this.popedom = popedom;
    }

    public String getGlyphicon() {
        return glyphicon;
    }

    public void setGlyphicon(String glyphicon) {
        this.glyphicon = glyphicon;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public String getSref() {
        return sref;
    }

    public void setSref(String sref) {
        this.sref = sref;
    }

    public int getLvIndex() {
        return lvIndex;
    }

    public void setLvIndex(int lvIndex) {
        this.lvIndex = lvIndex;
    }

    public SysMenu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(SysMenu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public List<SysMenu> getSubSysMenuList() {
        return subSysMenuList;
    }

    public void setSubSysMenuList(List<SysMenu> subSysMenuList) {
        this.subSysMenuList = subSysMenuList;
    }

    /**
     * 获取文字性的菜单权限
     *
     * @return
     */
    public String getPopedomStr() {
        switch (popedom) {
            case ROOT:
                return "ROOT菜单";
            case SUPER:
                return "超级管理员菜单";
            default:
                return "普通菜单";
        }
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
        if (!(object instanceof SysMenu)) {
            return false;
        }
        SysMenu other = (SysMenu) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cbra.entity.SysMenu[ id=" + id + " ]";
    }

}
